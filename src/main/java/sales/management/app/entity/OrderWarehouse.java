package sales.management.app.entity;

import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.*;

@Entity
@Table(name = "order_warehouses")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Setter
@Getter
public class OrderWarehouse {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String tracking, orderNumberWarehouse, zip, email, password, phoneNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id")
    private Employee employee;
    private String noteWarehouse1, noteWarehouse2, linkEvidence;
    @Column(columnDefinition = "TEXT")
    private String noteWarehouse;
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    @ManyToOne
    @JoinColumn(name = "order_number") // Khóa ngoại liên kết sang bảng Order
    private Order order;

}
