package sales.management.app.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderWarehouseDTO {
    private String orderNumber;
    private String tracking, orderNumberWarehouse, zip, email, password, phoneNumber;
    private String noteWarehouse1, noteWarehouse2, linkEvidence, noteWarehouse;
}
