package sales.management.app.dto;

import java.math.BigDecimal;
import java.sql.Date;

import org.springframework.web.multipart.MultipartFile;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrderFormDTO {

    private String orderNumber;
    private Date orderDate;
    private Date expiryDate;
    private String account;
    private String productLink;
    private String productName;
    private Integer quantity;
    private BigDecimal balance;
    private String color;
    private MultipartFile productImage;
    private String address;
    private String noteSeller;

}
