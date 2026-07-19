package sales.management.app.entity;

import jakarta.persistence.*;
import lombok.*;
import sales.management.app.enums.StatusOrder;

import java.math.BigDecimal;
import java.util.*;

@Entity
@Table(name = "orders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Order {
    @Id
    @Column(name = "order_number")
    private String orderNumber;
    @Temporal(TemporalType.DATE)
    private Date orderDate;
    @Temporal(TemporalType.DATE)
    private Date expiryDate;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_name", nullable = true)
    private Account account;
    @Column(columnDefinition = "TEXT")
    private String productLink;
    @Column(columnDefinition = "TEXT")
    private String productName;
    private Integer quantity;
    private BigDecimal balance;
    private String color;
    private String productImage;
    @Column(columnDefinition = "TEXT")
    private String orderAddress;
    @Enumerated(EnumType.STRING)
    private StatusOrder status;
    @Column(columnDefinition = "TEXT")
    private String noteSeller;
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<OrderWarehouse> orderWarehouseList = new ArrayList<>();

}