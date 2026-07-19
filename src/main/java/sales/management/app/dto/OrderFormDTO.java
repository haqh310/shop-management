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
    private String productImagePath;
    private MultipartFile productImage;
    private String address;
    private String noteSeller;

    OrderFormDTO(String orderNumber, Date orderDate, Date expiryDate, String account, String productLink,
            String productName, Integer quantity, BigDecimal balance, String color, String productImagePath,
            String address, String noteSeller) {
        this.orderNumber = orderNumber;
        this.orderDate = orderDate;
        this.expiryDate = expiryDate;
        this.account = account;
        this.productLink = productLink;
        this.productName = productName;
        this.quantity = quantity;
        this.balance = balance;
        this.color = color;
        this.productImagePath = productImagePath;
        this.address = address;
        this.noteSeller = noteSeller;

    }

}
