package restaurante.team3.giacobello.orders.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import jakarta.persistence.LockModeType;

import restaurante.team3.giacobello.orders.entity.OrderEntity;

public interface OrderRepository
        extends JpaRepository<OrderEntity, Integer> {

    boolean existsByStripePaymentIntentId(String stripePaymentIntentId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT o FROM OrderEntity o WHERE o.id = :id")
    Optional<OrderEntity> findByIdForUpdate(@Param("id") Integer id);
}
