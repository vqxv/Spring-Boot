package com.example.demo.service;

import com.example.demo.entity.Authority;
import com.example.demo.entity.MyUser;
import com.example.demo.repository.AuthorityRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class UserService {
    private final UserRepository userRepo;
    private final AuthorityRepository authorityRepo;
    private final PasswordEncoder encoder;

    public UserService(UserRepository userRepo, AuthorityRepository authorityRepo, PasswordEncoder encoder) {
        this.userRepo = userRepo;
        this.authorityRepo = authorityRepo;
        this.encoder = encoder;
    }

    /**
     * 注册新用户，返回校验结果 Map。
     * 成功时 Map 包含 key="success" 值为 true；
     * 失败时 Map 包含 field-level 错误消息，前端按字段名展示行内提示。
     */
    @Transactional
    public Map<String, Object> register(MyUser user) {
        Map<String, Object> result = new LinkedHashMap<>();
        boolean hasError = false;

        // 1. 基本空值校验
        if (user == null || user.getUsername() == null || user.getUsername().isBlank()) {
            result.put("error_username", "用户名不能为空");
            hasError = true;
        }
        if (user == null || user.getPassword() == null || user.getPassword().isBlank()) {
            result.put("error_password", "密码不能为空");
            hasError = true;
        }
        if (hasError) {
            return result;
        }

        // 2. 用户名已存在
        if (userRepo.findByUsername(user.getUsername()) != null) {
            result.put("error_username", "该用户名已被占用");
            return result;
        }

        // 3. 密码与确认密码不一致
        if (user.getRepassword() == null || !user.getRepassword().equals(user.getPassword())) {
            result.put("error_repassword", "两次输入的密码不一致");
            return result;
        }

        // 4. 注册成功：分配角色、加密密码、保存
        String roleName = "ROLE_USER";
        Authority role = authorityRepo.findByName(roleName);
        if (role == null) {
            role = new Authority();
            role.setName(roleName);
        }
        user.setAuthorities(new ArrayList<>(List.of(role)));
        user.setPassword(encoder.encode(user.getPassword()));
        userRepo.save(user);
        result.put("success", true);
        return result;
    }

    public String getCurrentUsername() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null ? auth.getName() : "anonymous";
    }
}
