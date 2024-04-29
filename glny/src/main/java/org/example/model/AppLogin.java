package org.example.model;

import lombok.Data;

@Data
public class AppLogin {
    private String username;
    private String password;
    private PdaAppUser pdaAppUser;
    private String token;
}
