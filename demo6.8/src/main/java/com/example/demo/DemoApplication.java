package com.example.demo;

import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;


@SpringBootApplication
public class DemoApplication{

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}
	@Transactional
	@Bean
	public CommandLineRunner initUsers(UserRepository userRepo, PasswordEncoder encoder) {
		return args -> {
			if (userRepo.findByUsername("user") == null) {
				User u = new User();
				u.setUsername("user");
				u.setPassword(encoder.encode("123"));
				u.setRole("ROLE_USER");
				userRepo.save(u);
			}
			if (userRepo.findByUsername("admin") == null) {
				User a = new User();
				a.setUsername("admin");
				a.setPassword(encoder.encode("123"));
				a.setRole("ROLE_ADMIN");
				userRepo.save(a);
			}
		};
	}

}