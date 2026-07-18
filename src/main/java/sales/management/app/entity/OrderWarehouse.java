package sales.management.app.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
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
    private String orderNumber;

    private String tracking, orderNumberWarehouse, zip, email, password, phoneNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id")
    private Employee employee;
    private String noteWarehouse1, noteWarehouse2, linkEvidence, noteWarehouse;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "order_number")
    private Order order;

}
