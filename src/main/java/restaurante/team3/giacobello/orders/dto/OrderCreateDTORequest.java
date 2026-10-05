package restaurante.team3.giacobello.orders.dto;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record OrderCreateDTORequest(
                @NotNull @Positive Integer tabletId,
                @NotBlank String orderTypeName,
                @NotBlank String paymentMethodName,
                @NotEmpty List<@NotNull @Valid OrderItemCreateDTORequest> items,
                @Size(max = 255) @Pattern(regexp = "^pi_[A-Za-z0-9_]+$") String paymentIntentId) {
}
