package com.forestplus.request;

import lombok.Data;

public class CompanyRequest {
    private String name;
    private String address;
    private Long adminId;

    public Long getAdminId() { return adminId; }
    public void setAdminId(Long adminId) { this.adminId = adminId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
}
