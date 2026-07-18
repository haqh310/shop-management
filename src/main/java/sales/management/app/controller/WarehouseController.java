package sales.management.app.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import sales.management.app.dto.OrderWarehouseDTO;
import sales.management.app.entity.OrderWarehouse;
import sales.management.app.service.WarehouseService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@RestController
@RequestMapping("/api/warehouse")
public class WarehouseController {

    private final WarehouseService warehouseService;

    WarehouseController(WarehouseService warehouseService) {
        this.warehouseService = warehouseService;
    }

    @GetMapping("/{oderNumber}")
    public ResponseEntity<?> get(@PathVariable(name = "oderNumber") String orderNumber) {
        OrderWarehouseDTO orderWarehouse = warehouseService.findByOrderNumber(orderNumber);

        if (orderWarehouse == null) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }

        return ResponseEntity.ok(orderWarehouse);
    }

    @PostMapping("/save")
    public ResponseEntity<?> save(@ModelAttribute OrderWarehouseDTO createOrderWarehouseDTO) {
        try {
            // Chuyển toàn bộ gánh nặng xử lý cho Service
            warehouseService.saveOrderWarehouse(createOrderWarehouseDTO);

            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            // Bắt lỗi được ném ra từ Service (bao gồm lỗi lưu file)
            return ResponseEntity.badRequest().body("Lỗi xử lý: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Lỗi hệ thống không xác định.");
        }

    }

}
