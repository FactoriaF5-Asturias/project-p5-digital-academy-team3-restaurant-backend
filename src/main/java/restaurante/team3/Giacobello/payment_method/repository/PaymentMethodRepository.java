package restaurante.team3.Giacobello.payment_method.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import restaurante.team3.Giacobello.payment_method.entity.PaymentMethodEntity;

public interface PaymentMethodRepository extends JpaRepository<PaymentMethodEntity, Integer> {
}
