package restaurante.team3.giacobello.invoices.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "invoices")
public class InvoiceEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "order_id", nullable = false, unique = true)
    private Integer orderId;

    @Column(name = "invoice_number", nullable = false, length = 50)
    private String invoiceNumber;

    @Column(name = "total_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalAmount;

    @Column(name = "issued_at")
    private LocalDateTime issuedAt;

    protected InvoiceEntity() {
    }

    public InvoiceEntity(
            Integer orderId,
            String invoiceNumber,
            BigDecimal totalAmount,
            LocalDateTime issuedAt) {
        this.orderId = orderId;
        this.invoiceNumber = invoiceNumber;
        this.totalAmount = totalAmount;
        this.issuedAt = issuedAt;
    }

    public Integer getId() {
        return id;
    }

    public Integer getOrderId() {
        return orderId;
    }

    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public LocalDateTime getIssuedAt() {
        return issuedAt;
    }
}
