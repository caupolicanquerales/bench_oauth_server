package com.capo.bench_oauth_server.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.capo.bench_oauth_server.interfaces.LoginService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Controller
public class LoginController {
	
	private final LoginService loginService;

    @Value("${app.frontend.url:http://localhost:4200}")
    private String frontendUrl;

    public LoginController(LoginService loginService) {
    	this.loginService= loginService;
    }

    @GetMapping("/")
    public String index(Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            return "redirect:" + frontendUrl;
        }
        return "redirect:/login";
    }

    @GetMapping(value = "/.well-known/appspecific/**", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String devtoolsAppSpecific() {
        return "{}";
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
        return "login";
    }

    @PostMapping("/register")
    public String handleRegister(
            @RequestParam(value = "email", required = false) String email,
            @RequestParam(value = "registerPassword", required = false) String registerPassword,
            @RequestParam(value = "confirmPassword", required = false) String confirmPassword,
            @RequestParam(value = "fullName", required = false) String fullName,
            @RequestParam(value = "portalRole", defaultValue = "athlete") String portalRole,
            HttpServletRequest request) {

        request.getSession(true);

        String registrationEmail = (email != null) ? email.trim() : "";
        String rawPassword = (registerPassword != null) ? registerPassword : "";

        return loginService.validationProcessRegister(
                registrationEmail, 
                rawPassword, 
                confirmPassword, 
                fullName, 
                portalRole
        );
    }

}
