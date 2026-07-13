package sales.management.app.dto;

import java.time.LocalDate;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
public class ChartDTO {
    private LocalDate orderDate;
    private Double balance;
    private Integer count;

    public ChartDTO(Object orderDate, Object balance, Object count) {
        this.orderDate = (orderDate instanceof java.sql.Date) ? ((java.sql.Date) orderDate).toLocalDate()
                : (LocalDate) orderDate;
        this.balance = balance != null ? ((Number) balance).doubleValue() : 0.0;
        this.count = count != null ? ((Number) count).intValue() : 0;
    }
}
