package com.example.demo.service;

import com.example.demo.entity.Authority;
import com.example.demo.entity.MyUser;
import com.example.demo.repository.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserService {
    private final UserRepository userRepo;
    private final PasswordEncoder encoder;

    public UserService(UserRepository userRepo, PasswordEncoder encoder) {
        this.userRepo = userRepo;
        this.encoder = encoder;
    }

    @Transactional
    public String register(MyUser user) {
        if (userRepo.findByUsername(user.getUsername()) != null)
            return "/register?error=exist";
        List<Authority> authorities = new ArrayList<>();
        Authority role = new Authority();
        role.setName(user.getUsername().equals("admin") ? "ROLE_ADMIN" : "ROLE_USER");
        authorities.add(role);
        user.setAuthorities(authorities);
        user.setPassword(encoder.encode(user.getPassword()));
        userRepo.save(user);
        return "/login";
    }

    public String getCurrentUsername() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null ? auth.getName() : "anonymous";
    }
}
