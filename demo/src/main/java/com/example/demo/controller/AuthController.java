package com.example.demo.controller;

import com.example.demo.entity.MyUser;
import com.example.demo.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class AuthController {
    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/")
    public String index() { return "index"; }

    @GetMapping("/login")
    public String login() { return "login"; }

    @GetMapping("/register")
    public String registerPage(Model model) {
        model.addAttribute("user", new MyUser());
        return "register";
    }

    @PostMapping("/register")
    public String register(@ModelAttribute("user") MyUser user, Model model) {
        var result = userService.register(user);
        if (Boolean.TRUE.equals(result.get("success"))) {
            return "redirect:/login";
        }
        // 将错误消息放入 Model，让前端展示行内提示
        result.forEach((key, value) -> model.addAttribute(key, value));
        return "register";
    }

    @GetMapping("/user/home")
    public String userHome(Model model) {
        model.addAttribute("username", userService.getCurrentUsername());
        return "userHome";
    }

    @GetMapping("/admin/home")
    public String adminHome(Model model) {
        model.addAttribute("username", userService.getCurrentUsername());
        return "adminHome";
    }

    @GetMapping("/denied")
    public String denied() { return "denied"; }
}

