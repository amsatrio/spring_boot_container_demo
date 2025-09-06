package io.github.amsatrio.spring_boot_container_demo.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginRequest {
    @Size(min = 4, max = 50, message = "username must be between 4-50 characters")
    @Pattern(regexp = "^[a-zA-Z0-9_-]+$", message = "username can only contain letters, numbers, underscores, and hyphens")
    @NotBlank(message = "username cannot be blank")
    private String username;

    @NotBlank(message = "password cannot be blank")
    @Size(min = 8, max = 64, message = "password must be between 8 and 64 characters")
    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{8,}$", message = "password must contain at least one uppercase letter, one lowercase letter, one number, and one special character")
    private String password;
}
