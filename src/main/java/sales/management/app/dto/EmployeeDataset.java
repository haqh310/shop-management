package sales.management.app.dto;

import java.time.LocalDate;

import lombok.*;

@Getter
@Setter
public class EmployeeDataset {
    private String employeeName;
    private LocalDate orderDate;
    private Double balance;
    private Integer orderCount;

    public EmployeeDataset(String employeeName, Object orderDate, Object balance, Object orderCount) {
        this.employeeName = employeeName;
        this.orderDate = (orderDate instanceof java.sql.Date) ? ((java.sql.Date) orderDate).toLocalDate()
                : (LocalDate) orderDate;
        this.balance = balance != null ? ((Number) balance).doubleValue() : 0.0;
        this.orderCount = orderCount != null ? ((Number) orderCount).intValue() : 0;
    }

    public EmployeeDataset(String employeeName, Object balance, Object orderCount) {
        this.employeeName = employeeName;
        this.balance = balance != null ? ((Number) balance).doubleValue() : 0.0;
        this.orderCount = orderCount != null ? ((Number) orderCount).intValue() : 0;
    }

}
