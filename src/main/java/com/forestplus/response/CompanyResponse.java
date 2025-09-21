package com.forestplus.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

public class CompanyResponse {
    private Long id;
    private String name;
    private String address;
    private UserResponse admin;
    private List<UserResponse> users;
    private LocalDateTime createdAt;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public UserResponse getAdmin() { return admin; }
    public void setAdmin(UserResponse admin) { this.admin = admin; }

    public List<UserResponse> getUsers() { return users; }
    public void setUsers(List<UserResponse> users) { this.users = users; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}

