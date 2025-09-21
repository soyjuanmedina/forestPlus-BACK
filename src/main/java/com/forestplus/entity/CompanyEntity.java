package com.forestplus.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "companies")
public class CompanyEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String address;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "admin_id", nullable = false)
    private UserEntity admin;

    @OneToMany(mappedBy = "company", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserEntity> users;

    @Column(name = "created_at", updatable = false, insertable = false)
    private LocalDateTime createdAt;

    // Getters
    public Long getId() { return id; }
    public String getName() { return name; }
    public String getAddress() { return address; }
    public UserEntity getAdmin() { return admin; }
    public List<UserEntity> getUsers() { return users; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    // Setters
    public void setId(Long id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setAddress(String address) { this.address = address; }
    public void setAdmin(UserEntity admin) { this.admin = admin; }
    public void setUsers(List<UserEntity> users) { this.users = users; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}

