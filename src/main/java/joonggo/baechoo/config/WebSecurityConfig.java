package joonggo.baechoo.config;

import joonggo.baechoo.oauth.service.CustomOAuth2UserService;
import joonggo.baechoo.service.CustomUserDetailService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.security.web.SecurityFilterChain;

import java.util.HashMap;
import java.util.Map;

// WebSecurityConfig.java
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@Order(1)  // API 보안 설정보다 뒤에 적용
public class WebSecurityConfig {
    private final CustomUserDetailService customUserDetailService;
    private final CustomOAuth2UserService customOAuth2UserService;

    @Bean
    public SecurityFilterChain webFilterChain(HttpSecurity http, InMemoryClientRegistrationRepository clientRegistrationRepository) throws Exception {
        http
                .securityMatcher("/**")
                .userDetailsService(customUserDetailService)
                .csrf((csrf) -> csrf.disable())
                .authorizeHttpRequests((auth)->auth
                        .requestMatchers(
                                "/",
                                "/login",
                                "/members/register",
                                "/static/**",
                                "/include/**",
                                "/error/**",
                                "/js/**",
                                "/images/**"
                        ).permitAll()
                        .anyRequest().authenticated()
                )
                .formLogin((form)-> form
                        .loginPage("/login")
                        .loginProcessingUrl("/login-process")
                        .usernameParameter("userId")
                        .passwordParameter("password")
                        .defaultSuccessUrl("/")
                        .failureUrl("/login?error=true")
                        .permitAll()
                )
                .logout((logout)->logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                        .permitAll()
                )
                .sessionManagement((session) -> session
                        .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                )
                .oauth2Login(oauth2 -> oauth2
                        .loginPage("/login")
                        .userInfoEndpoint(userInfo -> userInfo
                                .userService(customOAuth2UserService)
                        )
                        .authorizationEndpoint(authorization -> authorization
                                .authorizationRequestResolver(customAuthorizationRequestResolver(clientRegistrationRepository)) // 수정
                        )
                        .defaultSuccessUrl("/")
                        .failureUrl("/login?error=true")

                );

        return http.build();
    }
    public OAuth2AuthorizationRequestResolver customAuthorizationRequestResolver(InMemoryClientRegistrationRepository clientRegistrationRepository) {
        DefaultOAuth2AuthorizationRequestResolver defaultResolver =
                new DefaultOAuth2AuthorizationRequestResolver(
                        clientRegistrationRepository,
                        "/oauth2/authorization");

        return new OAuth2AuthorizationRequestResolver() {
            @Override
            public OAuth2AuthorizationRequest resolve(jakarta.servlet.http.HttpServletRequest request) {
                OAuth2AuthorizationRequest defaultRequest = defaultResolver.resolve(request);
                if (defaultRequest == null) {
                    return null;
                }

                Map<String, Object> additionalParameters =
                        new HashMap<>(defaultRequest.getAdditionalParameters());
                additionalParameters.put("prompt", "login");

                return OAuth2AuthorizationRequest.from(defaultRequest)
                        .additionalParameters(additionalParameters)
                        .build();
            }

            @Override
            public OAuth2AuthorizationRequest resolve(jakarta.servlet.http.HttpServletRequest request, String clientRegistrationId) {
                OAuth2AuthorizationRequest defaultRequest = defaultResolver.resolve(request, clientRegistrationId);
                if (defaultRequest == null) {
                    return null;
                }

                Map<String, Object> additionalParameters =
                        new HashMap<>(defaultRequest.getAdditionalParameters());
                additionalParameters.put("prompt", "login");

                return OAuth2AuthorizationRequest.from(defaultRequest)
                        .additionalParameters(additionalParameters)
                        .build();
            }
        };
    }


}