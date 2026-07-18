package sales.management.app.dto;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AccountSellertReportDTO {
    private String sellerName;
    private List<PlatformDetail> platforms = new ArrayList<>();
    private int totalOfSeller;
}
