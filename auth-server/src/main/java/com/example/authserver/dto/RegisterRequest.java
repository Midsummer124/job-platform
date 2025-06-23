package com.example.authserver.dto;

import lombok.Data;
import java.util.Set;

@Data
public class RegisterRequest {
    private String username;
    private String password;
    private String email;
    private String phone;
    private String role;
    private Long company_id;
    private String position;
    private String department;
    //    private Set<String> roles;
    // 可以添加其他注册时需要的字段
}
