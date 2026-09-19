package restaurante.team3.Giacobello.orders.dto;

import jakarta.validation.constraints.NotBlank;

public record OrderStatusUpdateDTORequest(
        @NotBlank String statusName) {
}
