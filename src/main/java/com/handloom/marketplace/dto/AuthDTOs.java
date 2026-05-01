package com.handloom.marketplace.dto;

import com.handloom.marketplace.entity.User;
import jakarta.validation.constraints.*;

public class AuthDTOs {

    public static class RegisterRequest {
        @NotBlank private String name;
        @Email @NotBlank private String email;
        @NotBlank @Size(min = 6) private String password;
        @NotNull private User.Role role;
        public String getName() { return name; }
        public void setName(String n) { this.name = n; }
        public String getEmail() { return email; }
        public void setEmail(String e) { this.email = e; }
        public String getPassword() { return password; }
        public void setPassword(String p) { this.password = p; }
        public User.Role getRole() { return role; }
        public void setRole(User.Role r) { this.role = r; }
    }

    public static class LoginRequest {
        @Email @NotBlank private String email;
        @NotBlank private String password;
        public String getEmail() { return email; }
        public void setEmail(String e) { this.email = e; }
        public String getPassword() { return password; }
        public void setPassword(String p) { this.password = p; }
    }

    public static class AuthResponse {
        private String token;
        private UserDTO user;
        public AuthResponse(String token, UserDTO user) { this.token = token; this.user = user; }
        public String getToken() { return token; }
        public UserDTO getUser() { return user; }
    }

    public static class UserDTO {
        private Long id;
        private String name;
        private String email;
        private User.Role role;
        private User.UserStatus status;
        public UserDTO(Long id, String name, String email, User.Role role, User.UserStatus status) {
            this.id = id; this.name = name; this.email = email; this.role = role; this.status = status;
        }
        public Long getId() { return id; }
        public String getName() { return name; }
        public String getEmail() { return email; }
        public User.Role getRole() { return role; }
        public User.UserStatus getStatus() { return status; }
    }
}
