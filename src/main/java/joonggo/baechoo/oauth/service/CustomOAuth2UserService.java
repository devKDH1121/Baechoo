package joonggo.baechoo.oauth.service;

import joonggo.baechoo.domain.Member;
import joonggo.baechoo.domain.Role;
import joonggo.baechoo.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {
    private final MemberRepository memberRepository;

    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        // 카카오에서 받은 데이터로 회원 정보 생성 또는 업데이트
        Member member = saveOrUpdate(oAuth2User);

        return new DefaultOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority(member.getRole().name())),
                oAuth2User.getAttributes(),
                "id"
        );
    }

    private Member saveOrUpdate(OAuth2User oAuth2User) {
        Map<String, Object> attributes = oAuth2User.getAttributes();
        String providerID = String.valueOf(attributes.get("id"));

        Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
        Map<String, Object> profile = (Map<String, Object>) kakaoAccount.get("profile");

        String nickname = (String) profile.get("nickname");

        return memberRepository.findByProviderId(providerID)
                .map(entity -> entity.update(nickname))
                .orElse(Member.builder()
                        .providerId(providerID)
                        .nickname(nickname)
                        .provider("kakao")
                        .userId(providerID)  // providerId를 userId로 사용
                        .name(nickname)      // nickname을 name으로 사용
                        .role(Role.USER)
                        .build());
    }


}
