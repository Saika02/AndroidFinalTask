package com.example.lzz_finaltask.model;

import java.io.Serializable;

import lombok.Data;


@Data
public class User implements Serializable {

    private Long userId;

    private String username;

    private String password;

    private Integer role;

}