package com.example.demo.controller;

import com.example.demo.entity.User;
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
    public String index(Model model) {
        model.addAttribute("user", new User());
        return "index";
    }

    @PostMapping("/register")
    @ResponseBody
    public String register(@ModelAttribute User user) {
        String result = userService.register(user);
        return result;
    }

    @GetMapping("/home")
    public String home() {
        // 由Security的defaultSuccessUrl跳到这里，再根据角色重定向
        return "redirect:/" + getRolePrefix();
    }

    private String getRolePrefix() {
        var auth = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
        boolean isAdmin = auth.getAuthorities().stream().anyMatch(g -> g.getAuthority().equals("ROLE_ADMIN"));
        return isAdmin ? "admin/home" : "user/home";
    }

    @GetMapping("/user/home")
    public String userHome(Model model) {
        model.addAttribute("username", getCurrentUsername());
        return "userHome";
    }

    @GetMapping("/admin/home")
    public String adminHome(Model model) {
        model.addAttribute("username", getCurrentUsername());
        return "adminHome";
    }

    @RequestMapping(value = "/denied", method = {RequestMethod.GET, RequestMethod.POST})
    public String denied() {
        return "denied";
    }

    @PostMapping("/reset-password")
    @ResponseBody
    public String resetPassword(@RequestParam String username, @RequestParam String newPassword) {
        return userService.resetPassword(username, newPassword);
    }

    private String getCurrentUsername() {
        var auth = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
        return auth != null ? auth.getName() : "";
    }
}