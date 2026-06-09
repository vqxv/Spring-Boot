package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Table(name = "roles")
@Data
public class Authority {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name; // 存 "ROLE_USER", "ROLE_ADMIN"
    @ManyToMany(mappedBy = "authorities")
    private List<MyUser> myUsers;
}
