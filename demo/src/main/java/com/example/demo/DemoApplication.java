package com.example.demo;

import com.example.demo.entity.Authority;
import com.example.demo.entity.MyUser;
import com.example.demo.repository.AuthorityRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@SpringBootApplication
public class DemoApplication implements CommandLineRunner {
	@Autowired
	private UserRepository userRepo;
	@Autowired
	private AuthorityRepository authRepo;
	@Autowired
	private PasswordEncoder encoder;

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}
	@Transactional
	@Override
	public void run(String... args) throws Exception {
		// 插入角色
		if (authRepo.count() == 0) {
			Authority user = new Authority();
			user.setName("ROLE_USER");
			authRepo.save(user);
			Authority admin = new Authority();
			admin.setName("ROLE_ADMIN");
			authRepo.save(admin);
		}
		// 插入普通用户
		if (userRepo.findByUsername("user") == null) {
			MyUser user = new MyUser();
			user.setUsername("user");
			user.setPassword(encoder.encode("123"));
			user.setAuthorities(List.of(authRepo.findByName("ROLE_USER")));
			userRepo.save(user);
		}
		// 插入管理员
		if (userRepo.findByUsername("admin") == null) {
			MyUser admin = new MyUser();
			admin.setUsername("admin");
			admin.setPassword(encoder.encode("123"));
			admin.setAuthorities(List.of(authRepo.findByName("ROLE_ADMIN")));
			userRepo.save(admin);
		}
	}
}