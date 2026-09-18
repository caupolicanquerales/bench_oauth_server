package com.capo.bench_oauth_server.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.capo.bench_oauth_server.interfaces.LoginService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Controller
public class LoginController {
	
	private final LoginService loginService;

    public LoginController(LoginService loginService) {
    	this.loginService= loginService;
    }

    @GetMapping("/login")
    public String login(HttpServletRequest request) {
        request.getSession(true);
        return "login";
    }
    
    @GetMapping("/logout")
    public String logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        new SecurityContextLogoutHandler().logout(request, response, authentication);
        return "redirect:/login?logout=true";
    }
    
    @GetMapping("/register")
    public String register(HttpServletRequest request) {
        request.getSession(true);
        return "redirect:/login?mode=register";
    }

    @PostMapping("/register")
    public String handleRegister(
            @RequestParam("email") String email,
            @RequestParam("password") String password,
            @RequestParam(value = "confirmPassword", required = false) String confirmPassword,
            @RequestParam(value = "fullName", required = false) String fullName,
            @RequestParam(value = "portalRole", defaultValue = "athlete") String portalRole,
            HttpServletRequest request) {

        request.getSession(true);

        return loginService.validationProcessRegister(email, password, confirmPassword, portalRole);
    }

}
