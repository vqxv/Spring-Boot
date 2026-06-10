package com.ch.ch8.repository;

import com.ch.ch8.entity.MyUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MyUserRepository extends JpaRepository<MyUser, Integer> {
    //根据用户名查询用户，方法名命名符合Spring Data JPA规范
    MyUser findByUsername(String username);
}
