package joonggo.baechoo.service;

import joonggo.baechoo.domain.Member;
import joonggo.baechoo.repository.MemberRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Log4j2
public class CustomUserDetailService implements UserDetailsService {

    private final MemberRepository memberRepository;
    public CustomUserDetailService(MemberRepository memberRepository){
        this.memberRepository = memberRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String name) throws UsernameNotFoundException {
        // 사용자 정보를 데이터베이스에서 조회
        log.info("로그인 시도 : username{}", name);
        Member member = memberRepository.findByUserId(name)
                .orElseThrow(()->new UsernameNotFoundException("사용자를 찾을 수 없습니다."));
        log.info("사용자 찾음 {} : " , member.getUserId());
        return org.springframework.security.core.userdetails.User.builder()
                .username(member.getName())
                .password(member.getPassword())
                .roles(member.getRole().name())
                .build();

    }
}
