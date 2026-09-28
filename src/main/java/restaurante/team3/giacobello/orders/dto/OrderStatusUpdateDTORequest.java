package restaurante.team3.giacobello.orders.dto;

import jakarta.validation.constraints.NotBlank;

public record OrderStatusUpdateDTORequest(
        @NotBlank String statusName) {
}
