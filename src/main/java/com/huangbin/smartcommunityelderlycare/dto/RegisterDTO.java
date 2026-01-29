package com.huangbin.smartcommunityelderlycare.dto;

import lombok.Data;

@Data
public class RegisterDTO {
    private String phone;
    private String password;
    private String name;
    private String role;
    private String emergencyContact;
}