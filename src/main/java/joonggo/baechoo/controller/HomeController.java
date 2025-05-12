package joonggo.baechoo.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
@Log4j2
@RequiredArgsConstructor
public class HomeController {

    @GetMapping("/")
    public String home(){
        log.info("home");
        return "home";
    }

    @GetMapping("/login")
    public String login(){
        log.info("로그인 페이지 요청");
        return "login";
    }

}
