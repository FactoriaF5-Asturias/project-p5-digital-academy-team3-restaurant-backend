package restaurante.team3.giacobello.orders.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import restaurante.team3.giacobello.orders.entity.OrderEntity;

public interface OrderRepository
        extends JpaRepository<OrderEntity, Integer> {
}
