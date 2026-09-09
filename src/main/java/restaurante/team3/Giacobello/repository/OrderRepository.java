package restaurante.team3.Giacobello.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import restaurante.team3.Giacobello.entity.OrderEntity;

public interface OrderRepository
        extends JpaRepository<OrderEntity, Integer> {
}
