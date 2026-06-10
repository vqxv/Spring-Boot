package com.ch.ch8.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * 认证和授权处理类
 */
@Configuration
@EnableWebSecurity
public class SpringSecurityConfig {
    //依赖注入通用的用户服务类
    @Autowired
    private MyUserSecurityService myUserSecurityService;
    @Autowired
    private MyAuthenticationSuccessHandler myAuthenticationSuccessHandler;
    /**
     * BCryptPasswordEncoder是PasswordEncoder的接口实现
     * 实现加密功能
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    /**
     * DaoAuthenticationProvider是AuthenticationProvider的实现
     */
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provide = new DaoAuthenticationProvider();
        //不隐藏用户未找到异常
        provide.setHideUserNotFoundExceptions(false);
        //设置自定义认证方式，用户登录认证
        provide.setUserDetailsService(myUserSecurityService);
        //设置密码加密程序认证
        provide.setPasswordEncoder(passwordEncoder());
        return provide;
    }
    /**
     * 获取AuthenticationManager(认证管理器)，登录时认证使用
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception{
        return authenticationConfiguration.getAuthenticationManager();
    }
    /**
     * 请求授权 用户授权操作
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                //设置权限
                .authorizeHttpRequests(authorize -> authorize
                    //首页、登录、注册页面、登录注册功能、以及静态资源过滤掉，即可任意访问
                    .requestMatchers("/toLogin", "/toRegister", "/", "/login", "/register", "/css/**", "/fonts/**", "/js/**").permitAll()
                    //这里默认追加ROLE_，/user/**是控制器的请求匹配路径
                    .requestMatchers("/user/**").hasRole("USER")
                    .requestMatchers("/admin/**").hasAnyRole("ADMIN", "DBA")
                    //其他所有请求登录后才能访问
                    .anyRequest().authenticated()
                )
                //将输入的用户名与密码和授权的进行比较
                .formLogin()
                    .loginPage("/login").successHandler(myAuthenticationSuccessHandler)
                    .usernameParameter("username")
                    .passwordParameter("password")
                    //登录失败
                    .failureUrl("/login?error")
                .and()
                //注销行为可任意访问
                .logout().permitAll()
                .and()
                //指定异常处理页面
                .exceptionHandling().accessDeniedPage("/deniedAccess")
                .and().build();
    }
}
