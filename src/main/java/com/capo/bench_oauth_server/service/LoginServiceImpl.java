package com.capo.bench_oauth_server.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.capo.bench_oauth_server.dto.RegistrationRequest;
import com.capo.bench_oauth_server.enums.RolesEnum;
import com.capo.bench_oauth_server.interfaces.LoginService;
import com.capo.bench_oauth_server.models.Roles;
import com.capo.bench_oauth_server.models.UserDetail;
import com.capo.bench_oauth_server.repository.RolesRepository;
import com.capo.bench_oauth_server.repository.UserRepository;

@Service
public class LoginServiceImpl implements LoginService {

    private static final String LOGIN_VIEW = "login";
    private static final String REDIRECT_TO_LOGIN = "redirect:/login";
	
	private final UserRepository userRepository;
    private final RolesRepository rolesRepository;
    private final PasswordEncoder passwordEncoder;
    private final String frontendUrl;
    
    public LoginServiceImpl(UserRepository userRepository,
            RolesRepository rolesRepository,
            PasswordEncoder passwordEncoder,
            @Value("${app.frontend.url:http://localhost:4200}") String frontendUrl) {
    	this.userRepository = userRepository;
        this.rolesRepository = rolesRepository;
        this.passwordEncoder = passwordEncoder;
        this.frontendUrl = frontendUrl;
    }

    @Override
    public String resolveRootView(Authentication authentication) {
        if (isAuthenticated(authentication)) {
            return redirectToFrontend();
        }
        return REDIRECT_TO_LOGIN;
    }

    @Override
    public String resolveLoginView(Authentication authentication) {
        if (isAuthenticated(authentication)) {
            return redirectToFrontend();
        }
        return LOGIN_VIEW;
    }
    
    @Override
	public String register(RegistrationRequest request) {
		if (request.registerPassword() == null || request.registerPassword().length() < 6) {
            return "redirect:/register?reg_error=Password+must+be+at+least+6+characters";
        }
        if (request.confirmPassword() != null && !request.registerPassword().equals(request.confirmPassword())) {
            return "redirect:/register?reg_error=Passwords+do+not+match";
        }
        if (request.email() == null || !request.email().contains("@")) {
            return "redirect:/register?reg_error=Please+provide+a+valid+email+address";
        }

        String username = request.email().trim();
        if (userRepository.findByUsername(username).isPresent() || userRepository.findByEmail(username).isPresent()) {
            return "redirect:/register?reg_error=An+account+with+this+email+already+exists";
        }

        RolesEnum roleEnum = RolesEnum.fromString(request.portalRole());

        UserDetail newUser = UserDetail.builder()
                .username(username)
                .email(username)
                .fullName(request.fullName() != null && !request.fullName().isBlank() ? request.fullName().trim() : null)
                .password(passwordEncoder.encode(request.registerPassword()))
                .enable(true)
                .build();

        UserDetail savedUser = userRepository.save(newUser);

        Roles userRole = Roles.builder()
                .user(savedUser)
                .role(roleEnum.getRoleName())
                .build();
        rolesRepository.save(userRole);

        return "redirect:/login?registered=true";
	}

    private String redirectToFrontend() {
        return "redirect:" + frontendUrl;
    }

    private boolean isAuthenticated(Authentication authentication) {
        return authentication != null
            && authentication.isAuthenticated()
            && !(authentication instanceof AnonymousAuthenticationToken);
    }
}
