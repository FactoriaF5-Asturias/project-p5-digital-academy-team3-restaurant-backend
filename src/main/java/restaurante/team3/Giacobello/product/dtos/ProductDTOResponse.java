package restaurante.team3.Giacobello.product.dtos;

import java.math.BigDecimal;

public record ProductDTOResponse(

    Integer id,
    String name,
    String description,
    String category,
    BigDecimal price,
    Boolean status,
    String imageUrl) {

}
