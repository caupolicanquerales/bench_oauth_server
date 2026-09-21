package com.capo.bench_oauth_server.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.capo.bench_oauth_server.enums.RolesEnum;
import com.capo.bench_oauth_server.interfaces.LoginService;
import com.capo.bench_oauth_server.models.Roles;
import com.capo.bench_oauth_server.models.UserDetail;
import com.capo.bench_oauth_server.repository.RolesRepository;
import com.capo.bench_oauth_server.repository.UserRepository;

@Service
public class LoginServiceImpl implements LoginService {
	
	private final UserRepository userRepository;
    private final RolesRepository rolesRepository;
    private final PasswordEncoder passwordEncoder;
    
    public LoginServiceImpl(UserRepository userRepository,
            RolesRepository rolesRepository,
            PasswordEncoder passwordEncoder) {
    	this.userRepository = userRepository;
        this.rolesRepository = rolesRepository;
        this.passwordEncoder = passwordEncoder;
    }
    
    @Override
	public String validationProcessRegister(String email, String password, String confirmPassword, String fullName, String portalRole) {
		if (password == null || password.length() < 6) {
            return "redirect:/register?reg_error=Password+must+be+at+least+6+characters";
        }
        if (confirmPassword != null && !password.equals(confirmPassword)) {
            return "redirect:/register?reg_error=Passwords+do+not+match";
        }
        if (email == null || !email.contains("@")) {
            return "redirect:/register?reg_error=Please+provide+a+valid+email+address";
        }

        String username = email.trim();
        if (userRepository.findByUsername(username).isPresent() || userRepository.findByEmail(username).isPresent()) {
            return "redirect:/register?reg_error=An+account+with+this+email+already+exists";
        }

        RolesEnum roleEnum = RolesEnum.fromString(portalRole);

        UserDetail newUser = UserDetail.builder()
                .username(username)
                .email(username)
                .fullName(fullName != null && !fullName.isBlank() ? fullName.trim() : null)
                .password(passwordEncoder.encode(password))
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
}
