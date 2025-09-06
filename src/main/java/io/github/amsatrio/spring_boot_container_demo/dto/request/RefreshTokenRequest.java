package io.github.amsatrio.spring_boot_container_demo.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class RefreshTokenRequest {
    @NotBlank(message = "mainToken cannot be blank")
    private String mainToken;

    @NotBlank(message = "mainToken cannot be blank")
    private String refreshToken;
}
