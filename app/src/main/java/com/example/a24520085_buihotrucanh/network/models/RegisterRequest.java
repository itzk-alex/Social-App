package com.example.a24520085_buihotrucanh.network.models;

public class RegisterRequest {
    public String name;
    public String email;
    public String phone;
    public String password;

    public RegisterRequest(String name, String email, String phone, String password) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.password = password;
    }
}

