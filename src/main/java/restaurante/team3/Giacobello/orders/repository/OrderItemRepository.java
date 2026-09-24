package restaurante.team3.giacobello.orders.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import restaurante.team3.giacobello.orders.entity.OrderItemEntity;

public interface OrderItemRepository extends JpaRepository<OrderItemEntity, Integer> {
}
