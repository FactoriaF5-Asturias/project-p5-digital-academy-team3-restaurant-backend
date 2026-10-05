package restaurante.team3.giacobello.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ChangePasswordRequest(
        @NotBlank String username,
        @NotBlank String currentPassword,
        @NotBlank @Size(min = 8) String newPassword) {
}
