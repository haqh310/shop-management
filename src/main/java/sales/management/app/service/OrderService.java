package sales.management.app.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import sales.management.app.dto.OrderFormDTO;
import sales.management.app.dto.OrderListDTO;
import sales.management.app.entity.Account;
import sales.management.app.entity.CustomUserDetails;
import sales.management.app.entity.Order;
import sales.management.app.enums.StatusOrder;
import sales.management.app.repository.AccountRepository;
import sales.management.app.repository.OrderRepository;
import sales.management.app.utils.BasicFunc;

@Service
public class OrderService {
    private final OrderRepository orderRepository;
    private final AccountRepository accountRepository;

    public OrderService(OrderRepository orderRepository, AccountRepository accountRepository) {
        this.orderRepository = orderRepository;
        this.accountRepository = accountRepository;
    }

    public CustomUserDetails getCurrentUserDetails() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails) {
            return (CustomUserDetails) authentication.getPrincipal();
        }
        return null;
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
    public Page<OrderListDTO> findOrders(String keyword, String status, String date, Pageable pageable) {
        if (keyword != null) {
            keyword = keyword.trim();
        } else {
            keyword = "";
        }
        StatusOrder statusOrder = null;

        if (status != null && !status.isBlank()) {
            statusOrder = StatusOrder.valueOf(status);
        }
        status = statusOrder != null ? statusOrder.name() : null;

        LocalDate[] dateInMonth = BasicFunc.getStartAndEndMonth(date);

        Page<OrderListDTO> orders;
        CustomUserDetails user = getCurrentUserDetails();
        if (user.isAdmin()) {
            orders = orderRepository.findOrderForAdmin(keyword, status,
                    dateInMonth[0],
                    dateInMonth[1], pageable);
        } else if (user.isSeller()) {
            orders = orderRepository.findOrderForSeller(keyword, status,
                    dateInMonth[0],
                    dateInMonth[1], user.getId(), pageable);
        } else {
            orders = orderRepository.findOrderForWarehouse(keyword, status,
                    dateInMonth[0],
                    dateInMonth[1], user.getId(), pageable);
        }
        return orders;
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
        String savedImagePath = saveImage(file);

        // You can also save the file information to the database if needed

        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Đơn hàng không tồn tại với ID: " + id));
        order.setProductImage(savedImagePath);
        orderRepository.save(order);

    }

    public String saveImage(MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            throw new IOException("File is empty");
        }
        // Save the file or perform any other necessary operations

        String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();

        Path path = Paths.get("uploads/order/" + fileName);

        Files.copy(file.getInputStream(), path);

        return path.toString();
    }

    public Order save(OrderFormDTO createOrderDTO) {
        Account account = accountRepository.findByAccountName(createOrderDTO.getAccount());

        // Lưu đường dẫn ảnh vào thuộc tính imageUrl của Entity
        String savedImagePath = null;

        MultipartFile file = createOrderDTO.getProductImage();
        if (file != null && !file.isEmpty()) {

            try {
                savedImagePath = saveImage(file);
            } catch (IOException e) {
                // Ném ra RuntimeException để Spring tự động Rollback giao dịch (Transaction)
                // nếu có lỗi xảy ra
                throw new RuntimeException("Lỗi trong quá trình lưu file hình ảnh: " + e.getMessage(), e);
            }
        }

        Order order = new Order();
        order.setOrderNumber(createOrderDTO.getOrderNumber());
        order.setAccount(account);
        order.setOrderDate(createOrderDTO.getOrderDate());
        order.setProductName(createOrderDTO.getProductName());
        order.setExpiryDate(createOrderDTO.getExpiryDate());
        order.setQuantity(createOrderDTO.getQuantity());
        order.setBalance(createOrderDTO.getBalance());
        order.setColor(createOrderDTO.getColor());
        order.setOrderAddress(createOrderDTO.getAddress());
        order.setProductLink(createOrderDTO.getProductLink());
        order.setStatus(StatusOrder.NONE);

        // save path image
        order.setProductImage(savedImagePath);

        orderRepository.save(order);

        return order;
    }

}
