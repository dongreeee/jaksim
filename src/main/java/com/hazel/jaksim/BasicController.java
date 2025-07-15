package com.hazel.jaksim;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class BasicController {

    @GetMapping("/")
    String hello(){
        return "index.html";
    }

    @GetMapping("/test")
    String test()
    {
        return "test.html";
    }


//    ✅ ResponseEntity<String> vs List<...> 차이
//     ResponseEntity<String>
//     직접 문자열을 응답으로 보낼 때 사용 -> 메세지 단순 응답 (ok, fail)
//     json이 아닌 순수 문자열이나 커스텀 상태코드/ 헤더를 설정할 때 유용
//     JSON으로 보내고 싶다면 문자열을 직접 JSON으로 만들어야 한다.

//    List<Map<String, String>> 또는 List<DTO>
//    Spring이 자동으로 json으로 직렬화해서 응답해줌
//    일반적으로 REST API에서는 이 방식을 훨씬 더 많이 사용



}
