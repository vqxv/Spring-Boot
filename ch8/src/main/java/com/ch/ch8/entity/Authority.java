package com.ch.ch8.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.List;
@Data
@Entity
@Table(name = "authority")
@JsonIgnoreProperties(value = { "hibernateLazyInitializer"})
public class Authority implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(nullable = false)
    private String name;
    @ManyToMany(mappedBy = "authorityList")
    @JsonIgnore
    private List<MyUser> userList;
}
