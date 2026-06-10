package com.ch.ch8.service;

import com.ch.ch8.entity.MyUser;
import org.springframework.ui.Model;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface UserService {
    public String register(MyUser userDomain);
    public String loginSuccess(Model model);
    public String main(Model model);
    public String logout(HttpServletRequest request, HttpServletResponse response);
    public String deniedAccess(Model model);
}
