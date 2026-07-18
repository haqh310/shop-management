package sales.management.app.controller;

import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import org.springframework.data.domain.Page;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import org.springframework.ui.Model;

import sales.management.app.dto.OrderFormDTO;
import sales.management.app.dto.OrderListDTO;
import sales.management.app.entity.Order;
import sales.management.app.enums.StatusOrder;
import sales.management.app.service.OrderService;
import sales.management.app.utils.BasicFunc;

@Controller
@RequestMapping("/order")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping("")
    public String orderPage(
            @RequestParam(name = "page", required = false, defaultValue = "0") String page,
            @RequestParam(name = "size", required = false, defaultValue = "10") String size,
            @RequestParam(name = "keyword", required = false, defaultValue = "") String keyword,
            @RequestParam(name = "statusOrder", required = false, defaultValue = "") String statusOrder,
            @RequestParam(name = "monthText", required = false) String monthText,
            Model model) {
        if (monthText == "" || monthText == null) {
            monthText = LocalDate.now().getMonthValue() + "-" + LocalDate.now().getYear();
        }
        Pageable pageable = PageRequest.of(Integer.parseInt(page), Integer.parseInt(size));
        Page<OrderListDTO> orderPage = orderService.findOrders(keyword, statusOrder, monthText, pageable);

        int currentPage = orderPage.getNumber();
        int totalPages = orderPage.getTotalPages();

        int start = Math.max(0, currentPage - 1);
        int end = Math.min(totalPages - 1, currentPage + 1);

        // List<Employee> sellerList = employeeService.getSellers();
        // List<Employee> khoList = employeeService.getKhos();

        model.addAttribute("activePage", "order");
        model.addAttribute("orderPage", orderPage);
        model.addAttribute("keyword", keyword);
        model.addAttribute("statusOrder", statusOrder);
        model.addAttribute("monthText", monthText);
        model.addAttribute("months", BasicFunc.getMonth());
        model.addAttribute("startPage", start);
        model.addAttribute("endPage", end);

        return "order-list";
    }

    @PutMapping("/{id}/status")
    @ResponseBody
    public ResponseEntity<Void> updateStatus(@PathVariable(name = "id") @NonNull String id,
            @RequestBody Map<String, String> body) {
        String status = body.get("status");

        StatusOrder orderStatus = StatusOrder.valueOf(status);
        orderService.updateStatus(id, orderStatus);

        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/upload")
    @ResponseBody
    public ResponseEntity<Void> uploadImage(@PathVariable(name = "id") @NonNull String id,
            @RequestParam MultipartFile file)
            throws Exception {
        orderService.updateProductImage(id, file);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/create")
    public ResponseEntity<?> postMethodName(@ModelAttribute OrderFormDTO createOrderDTO) {
        try {
            // Chuyển toàn bộ gánh nặng xử lý cho Service
            orderService.save(createOrderDTO);

            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "Đơn hàng đã được tạo thành công!");
            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            // Bắt lỗi được ném ra từ Service (bao gồm lỗi lưu file)
            return ResponseEntity.badRequest().body("Lỗi xử lý: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Lỗi hệ thống không xác định.");
        }

    }

}