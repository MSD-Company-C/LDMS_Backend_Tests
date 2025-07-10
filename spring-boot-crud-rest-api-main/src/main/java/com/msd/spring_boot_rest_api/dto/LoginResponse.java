package com.msd.spring_boot_rest_api.dto;

public class LoginResponse {
    private String token;
    private String tokenType = "Bearer";

    public LoginResponse(String token) {
        this.token = token;
    }

    public LoginResponse(String token, String tokenType) {
        this.token = token;
        this.tokenType = tokenType;
    }

    // getters and setters
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }
}
