package sales.management.app.service;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import sales.management.app.entity.Order;
import sales.management.app.enums.StatusOrder;
import sales.management.app.repository.OrderRepository;
import sales.management.app.utils.BasicFunc;

@Service
public class OrderService {
    private final OrderRepository orderRepository;

    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    /*
     * Function lấy danh sách tất cả đơn hàng
     */
    public Page<Order> getOrders(@NonNull Pageable pageable) {
        return orderRepository.findAll(pageable);
    }

    /*
     * Function lấy danh sách đơn hàng theo điều kiện search
     */
    public Page<Order> searchOrders(String keyword, String status, String date, Pageable pageable) {
        if (keyword != null) {
            keyword = keyword.trim();
        } else {
            keyword = "";
        }
        StatusOrder statusOrder = null;

        if (status != null && !status.isBlank()) {
            statusOrder = StatusOrder.valueOf(status);
        }
        LocalDate[] dateInMonth = BasicFunc.getStartAndEndMonth(date);
        return orderRepository.search(keyword, statusOrder != null ? statusOrder.name() : null, dateInMonth[0],
                dateInMonth[1], pageable);
    }

    /*
     * Function update trạng thái đơn hàng
     */
    public void updateStatus(@NonNull String id, StatusOrder status) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Đơn hàng không tồn tại với ID: " + id));
        order.setStatus(status);
        orderRepository.save(order);
    }

    /*
     * Function update ảnh sản phẩm
     */
    public void updateProductImage(@NonNull String id, MultipartFile file) throws Exception {
        if (file.isEmpty()) {
            throw new Exception("File is empty");
        }
        // Save the file or perform any other necessary operations

        String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();

        Path path = Paths.get("uploads/product/" + fileName);

        Files.copy(file.getInputStream(), path);

        // You can also save the file information to the database if needed

        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Đơn hàng không tồn tại với ID: " + id));
        order.setProductImage(fileName);
        orderRepository.save(order);

    }

}
