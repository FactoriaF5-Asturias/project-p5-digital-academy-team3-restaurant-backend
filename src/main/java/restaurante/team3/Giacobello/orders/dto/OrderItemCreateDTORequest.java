package restaurante.team3.Giacobello.orders.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record OrderItemCreateDTORequest(
                @NotNull @Positive Integer productId,
                @NotNull @Positive Integer quantity) {
}
