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
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {
    private final MemberRepository memberRepository;

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        // 카카오에서 받은 데이터로 회원 정보 생성 또는 업데이트
        Member member = saveOrUpdate(oAuth2User);

        // DB에 저장
        memberRepository.save(member);

        // 사용자 속성을 포함하는 새로운 Map 생성
        Map<String, Object> attributes = new HashMap<>(oAuth2User.getAttributes());
        attributes.put("nickname", member.getNickname());  // nickname을 직접 추가

        return new DefaultOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority(member.getRole().name())),
                attributes,
                "nickname"  // 카카오의 기본 식별자 필드 사용
        );

    }

    private Member saveOrUpdate(OAuth2User oAuth2User) {
        Map<String, Object> attributes = oAuth2User.getAttributes();
        String providerID = String.valueOf(attributes.get("id"));

        Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
        Map<String, Object> profile = (Map<String, Object>) kakaoAccount.get("profile");

        String nickname = (String) profile.get("nickname");
        
        // 카카오 ID를 이용한 고유한 이메일 생성
        String generatedEmail = "kakao_" + providerID + "@baechoo.com";
        
        // UUID를 사용한 랜덤 비밀번호 생성
        String randomPassword = UUID.randomUUID().toString();
        
        // 임시 전화번호 설정
        String defaultPhone = "000-0000-0000";

        return memberRepository.findByProviderId(providerID)
                .map(entity -> entity.update(nickname)) // 기존 회원 정보 업데이트
                .orElse(Member.builder()
                        .providerId(providerID)
                        .nickname(nickname)
                        .provider("kakao")
                        .userId(providerID)
                        .name(nickname)
                        .email(generatedEmail)
                        .password(randomPassword)
                        .birth("0000-00-00")
                        .phone(defaultPhone)    // 임시 전화번호 설정
                        .role(Role.USER)
                        .build());
    }
}