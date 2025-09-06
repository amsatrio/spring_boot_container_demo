package io.github.amsatrio.spring_boot_container_demo.dto.request;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterRequest {

    @NotBlank
    @Size(max = 50)
    @Email
    private String email;

    private String role;

    @NotBlank(message = "password cannot be blank")
    @Size(min = 8, max = 64, message = "password must be between 8 and 64 characters")
    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{8,}$", message = "password must contain at least one uppercase letter, one lowercase letter, one number, and one special character")
    private String password;
}
