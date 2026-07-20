package sales.management.app.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
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
import sales.management.app.entity.Employee;
import sales.management.app.entity.Order;
import sales.management.app.entity.OrderWarehouse;
import sales.management.app.enums.StatusOrder;
import sales.management.app.repository.AccountRepository;
import sales.management.app.repository.EmployeeRepository;
import sales.management.app.repository.OrderRepository;
import sales.management.app.repository.WarehouseRepository;
import sales.management.app.utils.BasicFunc;

@Service
public class OrderService {
    private final OrderRepository orderRepository;
    private final AccountRepository accountRepository;
    private final WarehouseRepository warehouseRepository;
    private final EmployeeRepository employeeRepository;

    public OrderService(OrderRepository orderRepository, AccountRepository accountRepository,
            WarehouseRepository warehouseRepository, EmployeeRepository employeeRepository) {
        this.orderRepository = orderRepository;
        this.accountRepository = accountRepository;
        this.warehouseRepository = warehouseRepository;
        this.employeeRepository = employeeRepository;
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

        LocalDate[] dateInMonth = BasicFunc.getStartAndEndMonth(date);

        Page<OrderListDTO> orders;
        CustomUserDetails user = getCurrentUserDetails();
        if (user.isAdmin()) {
            orders = orderRepository.findOrderForAdmin(keyword, statusOrder,
                    dateInMonth[0],
                    dateInMonth[1], pageable);
        } else if (user.isSeller()) {
            orders = orderRepository.findOrderForSeller(keyword, statusOrder,
                    dateInMonth[0],
                    dateInMonth[1], user.getId(), pageable);
        } else {
            orders = orderRepository.findOrderForWarehouse(keyword, statusOrder,
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

    public Order save(OrderFormDTO createOrderDTO) throws Exception {
        Account account = accountRepository.findByAccountName(createOrderDTO.getAccount())
                .orElseThrow(() -> new Exception("Account not found"));

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
        order.setNoteSeller(createOrderDTO.getNoteSeller());
        order.setStatus(StatusOrder.NONE);

        // save path image
        order.setProductImage(savedImagePath);

        // Setting warehouse
        List<OrderWarehouse> warehouseList = new ArrayList<>();
        List<Integer> idList = employeeRepository.getEmployeeWarehouseId();
        List<Integer> idUsed = warehouseRepository.getEmployeeIdInFirst(idList.size() - 1);
        Employee employee = null;
        for (int id : idList) {
            if (!idUsed.contains(id)) {
                employee = employeeRepository.findById(id).orElse(null);
                if (employee != null) {
                    for (int i = 0; i < order.getQuantity(); i++) {
                        OrderWarehouse warehouse = new OrderWarehouse();
                        warehouse.setOrder(order);
                        warehouse.setEmployee(employee);
                        warehouse.setCreatedAt(new Date());
                        warehouseList.add(warehouse);
                    }

                    break;
                }
            }
        }
        // Set quantity lai thanh 1

        order.setOrderWarehouseList(warehouseList);
        order.setCreatedAt(new Date());

        orderRepository.save(order);

        return order;
    }

    public Order update(String orderNumber, OrderFormDTO createOrderDTO) throws Exception {
        Order order = orderRepository.findByOrderNumber(orderNumber).orElse(null);

        if (order == null) {
            throw new Exception("Order not found");
        }

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

        order.setOrderDate(createOrderDTO.getOrderDate());
        order.setExpiryDate(createOrderDTO.getExpiryDate());
        order.setProductName(createOrderDTO.getProductName());
        order.setQuantity(createOrderDTO.getQuantity());
        order.setBalance(createOrderDTO.getBalance());
        order.setColor(createOrderDTO.getColor());
        order.setOrderAddress(createOrderDTO.getAddress());
        order.setProductLink(createOrderDTO.getProductLink());
        order.setNoteSeller(createOrderDTO.getNoteSeller());

        // save path image
        order.setProductImage(savedImagePath);

        orderRepository.save(order);

        return order;
    }

    public OrderFormDTO findByOrderNumber(String orderMumber) {
        return orderRepository.findByOrderNumberForForm(orderMumber).orElse(null);
    }

    public void importCSV(MultipartFile file) throws Exception {
        BufferedReader fileReader = new BufferedReader(
                new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8));
        CSVParser parser = CSVFormat.DEFAULT
                .builder()
                .setHeader()
                .setSkipHeaderRecord(true)
                .setIgnoreHeaderCase(true)
                .setTrim(true)
                .build()
                .parse(fileReader);

        Iterable<CSVRecord> csvRecords = parser.getRecords();

        String fileName = file.getOriginalFilename();
        String accountName = fileName.split("_")[0];

        Account account = accountRepository.findByAccountName(accountName)
                .orElseThrow(() -> new Exception("account not found"));

        for (CSVRecord csvRecord : csvRecords) {
            // Đọc dữ liệu theo tên cột tương ứng trong file CSV của bạn
            String orderNumber = csvRecord.get("Order ID");
            String productName = csvRecord.get("Product Name");
            String quantity = csvRecord.get("Quantity");

            // Thực hiện convert dữ liệu và gọi Service để lưu vào database
            // Example:
            // Integer quantity = Integer.parseInt(quantityStr);
            // orderService.saveFromCSV(orderNumber, account, productName, quantity);

            System.out.println("Đã đọc dòng: " + orderNumber + " - " + account);
        }

    }
}
