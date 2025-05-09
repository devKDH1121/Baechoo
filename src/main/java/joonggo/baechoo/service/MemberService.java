package joonggo.baechoo.service;

import jakarta.transaction.Transactional;
import joonggo.baechoo.domain.Member;
import joonggo.baechoo.domain.MemberUpdatedDTO;
import joonggo.baechoo.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class MemberService {

    private final MemberRepository memberRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    // 회원 등록
    public Member register(Member member){
        // 중복 확인
        if(memberRepository.findByUserId(member.getUserId()).isPresent()){
            throw new IllegalArgumentException("이미 존재하는 아이디입니다.");
        }
        member.setPassword(passwordEncoder.encode(member.getPassword()));

        return memberRepository.save(member);
    }

    // 회원 조회
    public List<Member> listAll(){
        return memberRepository.findAllByDel(0);
    }

    // 회원 선택 조회
    public Optional<Member> detailMember(Long id){
        return memberRepository.findByIdAndDel(id, 0);
    }

    // 회원 삭제 del로 변경
    public void deleteMember(Long id){
        Optional<Member> memberDel = memberRepository.findByIdAndDel(id, 0);
        if(memberDel.isPresent()){
            Member member = memberDel.get();
            member.setDel(1);
            memberRepository.save(member);
        } else {
            throw new IllegalArgumentException("회원이 존재하지 않거나 이미 삭제되었습니다.");
        }
    }
    
    // 추 후 생각
    public void updateMember(MemberUpdatedDTO dto){
        Member member = memberRepository.findByIdAndDel(dto.getId(), 0)
                .orElseThrow(() -> new IllegalArgumentException("회원이 존재하지 않습니다."));

        member.setUserId(dto.getUserId());
        member.setName(dto.getName());
        member.setAddress(dto.getAddress());
        member.setEmail(dto.getEmail());
        member.setPhone(dto.getPhone());

    }

}
