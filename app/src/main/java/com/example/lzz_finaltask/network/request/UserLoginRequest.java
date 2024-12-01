package com.example.lzz_finaltask.network.request;

import java.io.Serializable;

public class UserLoginRequest implements Serializable {
    private String username;

    private String password;

    public UserLoginRequest(String username, String password) {
        this.username = username;
        this.password = password;
    }
}
