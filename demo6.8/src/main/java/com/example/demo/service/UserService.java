package com.example.demo.service;
import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Collections;

@Service
public class UserService implements UserDetailsService {
    private final UserRepository userRepo;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepo, PasswordEncoder passwordEncoder) {
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepo.findByUsername(username);
        if (user == null) throw new UsernameNotFoundException("用户不存在");
        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                Collections.singletonList(new SimpleGrantedAuthority(user.getRole()))
        );
    }

    @Transactional
    public String register(User user) {
        // 清理用户名空格
        if (user.getUsername() != null) {
            user.setUsername(user.getUsername().trim());
        }
        
        // 检查用户名是否为空
        if (user.getUsername() == null || user.getUsername().isEmpty()) {
            return "用户名不能为空";
        }
        // 检查密码是否为空
        if (user.getPassword() == null || user.getPassword().isEmpty()) {
            return "密码不能为空";
        }
        // 检查确认密码是否为空
        if (user.getRepassword() == null || user.getRepassword().isEmpty()) {
            return "确认密码不能为空";
        }
        // 检查两次密码是否一致
        if (!user.getPassword().equals(user.getRepassword())) {
            return "两次输入的密码不一致";
        }
        
        // 检查用户名是否已存在
        User existingUser = userRepo.findByUsername(user.getUsername());
        if (existingUser != null) {
            return "用户名已存在";
        }
        
        try {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            user.setRole(user.getUsername().equals("admin") ? "ROLE_ADMIN" : "ROLE_USER");
            userRepo.save(user);
            return "success";
        } catch (Exception e) {
            // 检查是否是唯一约束冲突
            if (e.getMessage() != null && (e.getMessage().contains("unique") || e.getMessage().contains("duplicate") || e.getMessage().contains("UK_"))) {
                return "用户名已存在";
            }
            return "注册失败，请稍后重试";
        }
    }

    @Transactional
    public String resetPassword(String username, String newPassword) {
        // 检查用户名是否为空
        if (username == null || username.trim().isEmpty()) {
            return "用户名不能为空";
        }
        
        // 检查新密码是否为空
        if (newPassword == null || newPassword.isEmpty()) {
            return "密码不能为空";
        }
        
        // 查询用户是否存在
        User user = userRepo.findByUsername(username.trim());
        if (user == null) {
            return "用户不存在";
        }
        
        try {
            // 更新密码
            user.setPassword(passwordEncoder.encode(newPassword));
            userRepo.save(user);
            return "success";
        } catch (Exception e) {
            return "密码重置失败，请稍后重试";
        }
    }
}