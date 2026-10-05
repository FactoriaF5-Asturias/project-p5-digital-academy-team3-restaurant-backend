package restaurante.team3.giacobello.product.dtos;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record ProductDTORequest(
        @NotBlank @Size(max = 100) String name,
        String description,
        @NotNull Integer categoryId,
        @NotNull @Positive BigDecimal price,
        String imageUrl,
        @NotNull Boolean status) {
}
