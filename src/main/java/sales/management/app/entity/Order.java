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
    @JoinColumn(name = "account_name")
    private Account account;
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
    private String noteSeller;

    @OneToOne(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private OrderWarehouse orderWarehouse;
}