package joonggo.baechoo.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@Controller
@Slf4j
@RequiredArgsConstructor
public class HomeController {

    @GetMapping("/")
    public String home(Model model, @AuthenticationPrincipal Object principal) {
        if (principal != null) {
            if (principal instanceof UserDetails) {
                UserDetails userDetails = (UserDetails) principal;
                model.addAttribute("username", userDetails.getUsername());
            } else if (principal instanceof OAuth2User) {
                OAuth2User oauth2User = (OAuth2User) principal;
                Map<String, Object> attributes = oauth2User.getAttributes();
                Map<String, Object> properties = (Map<String, Object>) attributes.get("properties");
                String nickname = (String) properties.get("nickname");
                model.addAttribute("username", nickname);
            }
        }
        return "home";
    }


    @GetMapping("/login")
    public String login(@RequestParam(value="error", required = false) String error,
                        Model model,
                        Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated() &&
                !(authentication instanceof AnonymousAuthenticationToken)) {
            log.info("이미 인증된 사용자가 로그인 페이지 접근 시도: {}", authentication.getName());
            return "redirect:/";
        }

        log.info("로그인 페이지 요청");
        if (error != null) {
            model.addAttribute("errorMessage", "아이디 또는 비밀번호가 올바르지 않습니다.");
        }
        return "login";
    }
}