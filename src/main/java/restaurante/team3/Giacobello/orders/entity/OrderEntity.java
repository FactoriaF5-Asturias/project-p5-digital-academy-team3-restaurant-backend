package restaurante.team3.Giacobello.orders.entity;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "orders")
public class OrderEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "tablet_id")
    private Integer tabletId;

    @Column(name = "order_type_name", length = 30)
    private String orderTypeName;

    @Column(name = "payment_method_name", length = 30)
    private String paymentMethodName;

    @Column(name = "status_name", length = 50)
    private String statusName;

    @Column(name = "total_amount", precision = 10, scale = 2)
    private BigDecimal totalAmount;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public OrderEntity() {
    }

    public OrderEntity(
            Integer id,
            Integer tabletId,
            String orderTypeName,
            String paymentMethodName,
            String statusName,
            BigDecimal totalAmount,
            LocalDateTime createdAt) {
        this.id = id;
        this.tabletId = tabletId;
        this.orderTypeName = orderTypeName;
        this.paymentMethodName = paymentMethodName;
        this.statusName = statusName;
        this.totalAmount = totalAmount;
        this.createdAt = createdAt;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getTabletId() {
        return tabletId;
    }

    public void setTabletId(Integer tabletId) {
        this.tabletId = tabletId;
    }

    public String getStatusName() {
        return statusName;
    }

    public void setStatusName(String statusName) {
        this.statusName = statusName;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getOrderTypeName() {
        return orderTypeName;
    }

    public void setOrderTypeName(String orderTypeName) {
        this.orderTypeName = orderTypeName;
    }

    public String getPaymentMethodName() {
        return paymentMethodName;
    }

    public void setPaymentMethodName(String paymentMethodName) {
        this.paymentMethodName = paymentMethodName;
    }

    @OneToMany(mappedBy = "order")
    @OrderBy("id ASC")
    private List<OrderItemEntity> items = new ArrayList<>();

    public List<OrderItemEntity> getItems() {
        return items;
    }
}
