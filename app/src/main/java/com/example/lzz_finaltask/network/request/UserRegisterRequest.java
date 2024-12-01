package com.example.lzz_finaltask.network.request;

import lombok.Data;

import java.io.Serializable;

@Data
public class UserRegisterRequest implements Serializable {
    private String username;
    private String password;

    public UserRegisterRequest(String username, String password) {
        this.username = username;
        this.password = password;
    }
}
