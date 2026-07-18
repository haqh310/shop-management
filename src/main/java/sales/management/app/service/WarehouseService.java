package sales.management.app.service;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.security.core.Authentication;

import sales.management.app.dto.OrderWarehouseDTO;
import sales.management.app.entity.CustomUserDetails;
import sales.management.app.entity.Order;
import sales.management.app.entity.OrderWarehouse;
import sales.management.app.repository.AccountRepository;
import sales.management.app.repository.OrderRepository;
import sales.management.app.repository.WarehouseRepository;

@Service
public class WarehouseService {

    private final WarehouseRepository warehouseRepository;
    private final OrderRepository orderRepository;
    private final AccountRepository accountRepository;

    WarehouseService(WarehouseRepository warehouseRepository, OrderRepository orderRepository,
            AccountRepository accountRepository) {
        this.warehouseRepository = warehouseRepository;
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

    public OrderWarehouseDTO findByOrderNumber(String orderNumber) {
        return warehouseRepository.findByOrderNumber(orderNumber).orElse(null);
    }

    public void saveOrderWarehouse(OrderWarehouseDTO createOrderWarehouseDTO) throws Exception {
        Order order = orderRepository.findByOrderNumber(createOrderWarehouseDTO.getOrderNumber())
                .orElseThrow(() -> new Exception("Not found order by order number!"));

        // 2. Lấy đối tượng OrderWarehouse đang liên kết từ Order gốc ra
        OrderWarehouse orderWarehouse = order.getOrderWarehouse();

        // 3. Nếu ĐƠN HÀNG MỚI hoàn toàn (chưa từng có thông tin kho dưới DB) -> Lúc này
        // mới khởi tạo
        if (orderWarehouse == null) {
            orderWarehouse = new OrderWarehouse();
            orderWarehouse.setOrder(order); // Thiết lập mối quan hệ
            order.setOrderWarehouse(orderWarehouse); // Đồng bộ 2 chiều
        }

        orderWarehouse.setTracking(createOrderWarehouseDTO.getTracking());
        orderWarehouse.setOrderNumberWarehouse(createOrderWarehouseDTO.getOrderNumberWarehouse());
        orderWarehouse.setZip(createOrderWarehouseDTO.getZip());
        orderWarehouse.setEmail(createOrderWarehouseDTO.getEmail());
        orderWarehouse.setPassword(createOrderWarehouseDTO.getPassword());
        orderWarehouse.setPhoneNumber(createOrderWarehouseDTO.getPhoneNumber());

        orderWarehouse.setNoteWarehouse1(createOrderWarehouseDTO.getNoteWarehouse1());
        orderWarehouse.setNoteWarehouse2(createOrderWarehouseDTO.getNoteWarehouse2());
        orderWarehouse.setLinkEvidence(createOrderWarehouseDTO.getLinkEvidence());
        orderWarehouse.setNoteWarehouse(createOrderWarehouseDTO.getNoteWarehouse());
        // get user create order warehouse
        CustomUserDetails user = getCurrentUserDetails();
        orderWarehouse.setEmployee(user.getEmployee());

        orderRepository.save(order);
    }
}
