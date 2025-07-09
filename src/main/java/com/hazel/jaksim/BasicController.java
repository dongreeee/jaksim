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
}
