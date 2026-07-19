package sales.management.app.dto;

import java.math.BigDecimal;
import java.util.*;
import sales.management.app.enums.StatusOrder;

public interface OrderListDTO {
    // === Nhóm thông tin từ Order ===
    String getOrderNumber();

    Date getOrderDate();

    Date getExpiryDate();

    String getAccountName();

    String getSellerName();

    String getProductLink();

    String getProductName();

    Integer getQuantity();

    BigDecimal getBalance();

    String getColor();

    String getProductImage();

    String getOrderAddress();

    String getNoteSeller();

    StatusOrder getStatus();

    // === Nhóm thông tin từ OrderWarehouse ===
    Long getWarehouseId();

    String getTracking();

    String getOrderNumberWarehouse();

    String getZip();

    String getEmail();

    String getPassword();

    String getPhoneNumber();

    String getNoteWarehouse1();

    String getWarehouseName();

    String getNoteWarehouse2();

    String getLinkEvidence();

    String getNoteWarehouse();
}