package com.capo.bench_oauth_server.controller;

import java.util.Map;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.capo.bench_oauth_server.dto.RegistrationRequest;
import com.capo.bench_oauth_server.interfaces.LoginService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class LoginController {

    private final LoginService loginService;

    @GetMapping("/")
    public String index(Authentication authentication) {
        return loginService.resolveRootView(authentication);
    }

    @GetMapping(value = "/.well-known/appspecific/**", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<Map<String, String>> devtoolsAppSpecific() {
        return ResponseEntity.ok(Map.of());
    }

    @GetMapping("/login")
    public String login(Authentication authentication) {
        return loginService.resolveLoginView(authentication);
    }

    @GetMapping("/register")
    public String register(Authentication authentication) {
        return loginService.resolveLoginView(authentication);
    }

    @PostMapping("/register")
    public String handleRegister(@ModelAttribute RegistrationRequest registrationRequest) {
        return loginService.register(registrationRequest);
    }
}