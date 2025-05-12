package joonggo.baechoo.controller;

import joonggo.baechoo.domain.Member;
import joonggo.baechoo.domain.MemberUpdatedDTO;
import joonggo.baechoo.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/members")
@Log4j2
public class MemberController {

    private final MemberService memberService;

    // 회원 목록
    @GetMapping("/list")
    public String listMembers(Model model){
        model.addAttribute("members", memberService.listAll());
        return "members/list";
    }

    // 회원 선택 조회
    @GetMapping("/detail/{id}")
    public String detailMember(Model model, @PathVariable Long id){
        Member member = memberService.detailMember(id)
                .orElseThrow(()->new IllegalArgumentException("해당 회원이 존재하지 않습니다."));
    model.addAttribute("member", member);
        return "members/detail";
    }
    
    //회원 등록 폼
    @GetMapping("/register")
    public String registerForm(Model model){
        log.info("회원가입 페이지 이동");
        model.addAttribute("member", new Member());
        return "members/register";
    }

    //회원 등록 처리
    @PostMapping("/register")
    public String register(@ModelAttribute Member member){
        memberService.register(member);

        return "redirect:/members/list";
    }

    //회원 수정 폼
    @GetMapping("/{id}/update")
    public String updateForm(@PathVariable Long id, Model model){
        Member member = memberService.detailMember(id)
                .orElseThrow(()->new IllegalArgumentException("존재하지 않는 회원입니다."));
        model.addAttribute("member", member);
        return "members/update";
    }
    //회원 수정 처리
    @PostMapping("/{id}/update")
    public String update(@ModelAttribute MemberUpdatedDTO dto){
        memberService.updateMember(dto);

        return "redirect:/members/detail/" + dto.getId();
    }

    //회원 삭제 처리
    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id){
        memberService.deleteMember(id);

        return "redirect:/members/list";
    }

}
