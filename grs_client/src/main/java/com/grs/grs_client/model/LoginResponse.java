package com.grs.grs_client.model;

import lombok.Data;

import java.util.List;

@Data
public class LoginResponse {
    private Boolean opResult;
    private String opMessage;
    private String accessToken;
    private String refreshToken;
    private UserInformation userInformation;
    private List<String> authorities;
}
