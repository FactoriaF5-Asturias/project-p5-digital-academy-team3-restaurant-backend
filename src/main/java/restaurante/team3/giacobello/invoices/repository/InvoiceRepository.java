package restaurante.team3.giacobello.invoices.repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import restaurante.team3.giacobello.invoices.entity.InvoiceEntity;

public interface InvoiceRepository extends JpaRepository<InvoiceEntity, Integer> {
    Optional<InvoiceEntity> findByOrderId(Integer orderId);

    List<InvoiceEntity> findByIssuedAtBetweenOrderByIssuedAtDesc(LocalDateTime from, LocalDateTime to);

    @Query("SELECT COALESCE(SUM(i.totalAmount), 0) FROM InvoiceEntity i WHERE i.issuedAt BETWEEN :from AND :to")
    BigDecimal sumTotalAmountByIssuedAtBetween(@Param("from") LocalDateTime from, @Param("to") LocalDateTime to);
}
