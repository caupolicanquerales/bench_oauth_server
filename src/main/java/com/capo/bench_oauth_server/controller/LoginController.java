package com.capo.bench_oauth_server.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {

    @GetMapping("/login")
    public String login(HttpServletRequest request) {
        request.getSession(true);
        return "login";
    }
    
    @GetMapping("/register")
    public String register() {
        return "register";
    }
}
