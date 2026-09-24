package restaurante.team3.giacobello.invoices.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import restaurante.team3.giacobello.invoices.entity.InvoiceEntity;

public interface InvoiceRepository extends JpaRepository<InvoiceEntity, Integer> {
    Optional<InvoiceEntity> findByOrderId(Integer orderId);
}
