package sales.management.app.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import sales.management.app.dto.OrderFormDTO;
import sales.management.app.dto.OrderWarehouseDTO;
import sales.management.app.service.AccountService;
import sales.management.app.service.OrderService;
import sales.management.app.service.WarehouseService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;

@RestController
@RequestMapping("/api")
public class ApiController {

    private final WarehouseService warehouseService;
    private final OrderService orderService;
    private final AccountService accountService;

    ApiController(WarehouseService warehouseService, OrderService orderService, AccountService accountService) {
        this.warehouseService = warehouseService;
        this.orderService = orderService;
        this.accountService = accountService;
    }

    @GetMapping("/warehouse/{id}")
    public ResponseEntity<?> get(@PathVariable(name = "id") Long id) {
        OrderWarehouseDTO orderWarehouse = warehouseService.findById(id);

        if (orderWarehouse == null) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }

        return ResponseEntity.ok(orderWarehouse);
    }

    @PostMapping("/warehouse/{id}/save")
    public ResponseEntity<?> save(@PathVariable(name = "id") Long id,
            @ModelAttribute OrderWarehouseDTO createOrderWarehouseDTO) {
        try {
            // Chuyển toàn bộ gánh nặng xử lý cho Service
            warehouseService.saveOrderWarehouse(id, createOrderWarehouseDTO);

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

    @GetMapping("/order/{orderNumber}")
    public ResponseEntity<?> getOrder(@PathVariable("orderNumber") String orderNumber) {
        OrderFormDTO orderFormDTO = orderService.findByOrderNumber(orderNumber);
        if (orderFormDTO == null) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }

        return ResponseEntity.ok(orderFormDTO);
    }

    @PutMapping("/order/{orderNumber}/update")
    public ResponseEntity<?> updateOrder(@PathVariable("orderNumber") String orderNumber,
            @ModelAttribute OrderFormDTO dto) {
        try {
            // Chuyển toàn bộ gánh nặng xử lý cho Service
            orderService.update(orderNumber, dto);

            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "Đơn hàng đã được update thành công!");
            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            // Bắt lỗi được ném ra từ Service (bao gồm lỗi lưu file)
            return ResponseEntity.badRequest().body("Lỗi xử lý: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Lỗi hệ thống không xác định.");
        }
    }

    @GetMapping("/account/list")
    public ResponseEntity<?> getAccountNames() {
        List<String> accountNames = accountService.getAccountNameByEmployee();
        return ResponseEntity.ok(accountNames);

    }

}
