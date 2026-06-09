package com.example.demo.security;

import com.example.demo.entity.MyUser;
import com.example.demo.repository.UserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final UserRepository userRepo;

    public SecurityConfig(UserRepository userRepo) {
        this.userRepo = userRepo;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    //认证规则
    @Bean
    public UserDetailsService userDetailsService() {
        return username -> {
            MyUser user = userRepo.findByUsername(username);
            if (user == null) throw new UsernameNotFoundException("用户不存在");
            return org.springframework.security.core.userdetails.User.builder()
                    .username(user.getUsername())
                    .password(user.getPassword())
                    .authorities(user.getAuthorities().stream()
                            .map(auth -> new SimpleGrantedAuthority(auth.getName()))
                            .toArray(SimpleGrantedAuthority[]::new))
                    .build();
        };
    }
    //跳转规则
    @Bean
    public AuthenticationSuccessHandler successHandler() {
        return (request, response, authentication) -> {
            String redirectUrl = "/";
            for (var auth : authentication.getAuthorities()) {
                if (auth.getAuthority().equals("ROLE_ADMIN"))
                    redirectUrl = "/admin/home";
                else if (auth.getAuthority().equals("ROLE_USER"))
                    redirectUrl = "/user/home";
            }
            response.sendRedirect(redirectUrl);
        };
    }
    // 授权规则
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/login", "/register", "/css/&zwnj;**", "/js/**&zwnj;", "/h2-console/**").permitAll()
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .successHandler(successHandler())
                        .failureUrl("/login?error")
                )
                .logout(logout -> logout.permitAll())
                .exceptionHandling(ex -> ex.accessDeniedPage("/denied"))
                .headers(headers -> headers.frameOptions(frame -> frame.sameOrigin()))
                .csrf(csrf -> csrf.ignoringRequestMatchers("/h2-console/**","/register","/login"));
        return http.build();
    }
}