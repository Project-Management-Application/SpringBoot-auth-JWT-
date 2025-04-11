package com.midou.tutorial.Projects.DTO;

public class InviteRequest {
    private String email;
    private String role; // Expecting "VIEWER" or "EDITOR"

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
}