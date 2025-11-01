package com.carolinacomba.marketplace.dto;

public class PasswordResetEmailData {
    private String email;
    private String userName;
    private String resetLink;

    public PasswordResetEmailData() {
    }

    public PasswordResetEmailData(String email, String userName, String resetLink) {
        this.email = email;
        this.userName = userName;
        this.resetLink = resetLink;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getResetLink() {
        return resetLink;
    }

    public void setResetLink(String resetLink) {
        this.resetLink = resetLink;
    }
}

