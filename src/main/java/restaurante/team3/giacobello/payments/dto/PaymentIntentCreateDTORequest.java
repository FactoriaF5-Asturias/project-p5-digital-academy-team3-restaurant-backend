package restaurante.team3.giacobello.payments.dto;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import restaurante.team3.giacobello.orders.dto.OrderItemCreateDTORequest;

public record PaymentIntentCreateDTORequest(
                @NotEmpty List<@NotNull @Valid OrderItemCreateDTORequest> items) {
}
