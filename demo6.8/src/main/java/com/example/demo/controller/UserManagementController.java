package com.example.demo.controller;

import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/admin/users")
public class UserManagementController {
    
    private final UserRepository userRepo;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    public UserManagementController(UserRepository userRepo, UserService userService, PasswordEncoder passwordEncoder) {
        this.userRepo = userRepo;
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping
    public String listUsers(Model model) {
        List<User> users = userRepo.findAll();
        model.addAttribute("users", users);
        
        // 获取当前登录用户名，用于前端判断
        var auth = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = auth != null ? auth.getName() : "";
        model.addAttribute("currentUsername", currentUsername);
        
        return "userManagement";
    }

    @PostMapping("/add")
    @ResponseBody
    public String addUser(@ModelAttribute User user) {
        // 检查用户名是否已存在
        if (userRepo.findByUsername(user.getUsername()) != null) {
            return "用户名已存在";
        }
        
        // 加密密码
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        
        // 设置角色
        if (user.getRole() == null || user.getRole().isEmpty()) {
            user.setRole("ROLE_USER");
        }
        
        userRepo.save(user);
        return "success";
    }

    @PostMapping("/update/{id}")
    @ResponseBody
    public String updateUser(@PathVariable Long id, @RequestBody User updatedUser) {
        User existingUser = userRepo.findById(id).orElse(null);
        if (existingUser == null) {
            return "用户不存在";
        }
        
        // 获取当前登录用户，防止修改自己的权限为普通用户
        var auth = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = auth != null ? auth.getName() : "";
        
        // 如果是修改自己，不能降低自己的权限
        if (existingUser.getUsername().equals(currentUsername)) {
            if ("ROLE_USER".equals(updatedUser.getRole()) && "ROLE_ADMIN".equals(existingUser.getRole())) {
                return "不能降低自己的管理员权限";
            }
        }
        
        // 更新用户信息
        existingUser.setUsername(updatedUser.getUsername());
        existingUser.setRole(updatedUser.getRole());
        
        // 如果提供了新密码，则更新密码
        if (updatedUser.getPassword() != null && !updatedUser.getPassword().isEmpty()) {
            existingUser.setPassword(passwordEncoder.encode(updatedUser.getPassword()));
        }
        
        userRepo.save(existingUser);
        return "success";
    }

    @PostMapping("/delete/{id}")
    @ResponseBody
    public String deleteUser(@PathVariable Long id) {
        User user = userRepo.findById(id).orElse(null);
        if (user == null) {
            return "用户不存在";
        }
        
        // 获取当前登录用户，防止删除自己
        var auth = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = auth != null ? auth.getName() : "";
        
        if (user.getUsername().equals(currentUsername)) {
            return "不能删除当前登录的账户";
        }
        
        userRepo.delete(user);
        return "success";
    }

    @PostMapping("/reset-password/{id}")
    @ResponseBody
    public String resetUserPassword(@PathVariable Long id, @RequestParam String newPassword) {
        User user = userRepo.findById(id).orElse(null);
        if (user == null) {
            return "用户不存在";
        }
        
        if (newPassword == null || newPassword.isEmpty()) {
            return "密码不能为空";
        }
        
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepo.save(user);
        return "success";
    }
}
