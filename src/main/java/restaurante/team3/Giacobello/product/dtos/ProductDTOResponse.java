package restaurante.team3.Giacobello.product.dtos;

import java.math.BigDecimal;

public class ProductDTOResponse {

    private Integer id;
    private String name;
    private String description;
    private String category;
    private BigDecimal price;
    private Boolean status;
    private String imageUrl;

    public ProductDTOResponse() {
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getCategory() {
        return category;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public Boolean getStatus() {
        return status;
    }

    public String getImageUrl() {
        return imageUrl;
    }
}
