package restaurante.team3.Giacobello.payment_method.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity 
@Table(name = "payment_method")
public class PaymentMethodEntity {

    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "name", nullable = false, unique = true, length = 30)
    private String name;

    public PaymentMethodEntity() {
    }

    public PaymentMethodEntity(Integer id, String name) {
        this.id = id;
        this.name = name;
    }

    public Integer getId(Integer id) {
        return id;
    }

    public String getName() {
        return name;
    }
}
