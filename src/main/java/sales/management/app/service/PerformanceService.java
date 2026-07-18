package sales.management.app.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.stereotype.Service;

import sales.management.app.dto.ChartDatasetDTO;
import sales.management.app.enums.StatusAccount;
import sales.management.app.enums.StatusOrder;
import sales.management.app.repository.AccountRepository;
import sales.management.app.repository.EmployeeRepository;
import sales.management.app.repository.OrderRepository;

@Service
public class PerformanceService {

    private final OrderRepository orderRepository;
    private final AccountRepository accountRepository;
    private final EmployeeRepository employeeRepository;

    public PerformanceService(OrderRepository orderRepository, AccountRepository accountRepository,
            EmployeeRepository employeeRepository) {
        this.orderRepository = orderRepository;
        this.accountRepository = accountRepository;
        this.employeeRepository = employeeRepository;
    }

    /*
     * Function lấy doanh thu và đơn hàng của nhân viên bán hàng
     */
    public Map<String, Object> findTopSeller(LocalDate startDate, LocalDate endDate) {
        List<Map<String, Object>> rawData = orderRepository.findTopSeller(startDate, endDate);

        List<String> employeeList = new ArrayList<>();
        List<Double> balanceList = new ArrayList<>();
        List<Integer> orderCountList = new ArrayList<>();

        for (Map<String, Object> data : rawData) {
            String employee = data.get("employeeName").toString();
            Double balance = Double.valueOf(data.get("balance").toString());
            Integer count = Integer.valueOf(data.get("orderCount").toString());

            employeeList.add(employee);
            balanceList.add(balance);
            orderCountList.add(count);

        }
        Map<String, Object> result = new HashMap<>();
        result.put("employee", employeeList);
        result.put("balance", balanceList);
        result.put("orderCount", orderCountList);

        return result;
    }

    public Map<String, Object> findAccountStatusSumary() {
        List<Map<String, Object>> rawData = accountRepository.findAccountStatusSumary();
        Map<String, Integer> rawDataMap = rawData.stream()
                .collect(Collectors.toMap(
                        item -> item.get("employeeName") + "-" + item.get("status").toString(),
                        item -> ((Number) item.getOrDefault("total", 0)).intValue(),
                        (existing, replacement) -> existing));

        List<String> sellers = rawData.stream()
                .map(item -> item.get("employeeName").toString())
                .distinct()
                .toList();

        List<ChartDatasetDTO> datasets = new ArrayList<>();
        List<Integer> totalList = new ArrayList<>();
        for (StatusAccount statusAccount : StatusAccount.values()) {
            List<Integer> dataList = new ArrayList<>();
            int total = 0;
            for (String seller : sellers) {
                String key = seller + "-" + statusAccount.name();
                int data = rawDataMap.getOrDefault(key, 0);
                dataList.add(data);
                total += data;
            }
            datasets.add(new ChartDatasetDTO(statusAccount.getLabel(), dataList, statusAccount.getBackgroudColor()));
            totalList.add(total);

        }

        Map<String, Object> result = new HashMap<>();
        result.put("seller", sellers);
        result.put("chartDataset", datasets);
        result.put("total", totalList);

        return result;
    }

    public Map<String, Object> getBalanceOfWarehouse(LocalDate startDate, LocalDate endDate) {
        List<Map<String, Object>> rawData = orderRepository.getBalanceOfWarehouse(startDate, endDate, "KHO");

        List<String> employeeList = rawData.stream().map(item -> item.get("employeeName").toString()).toList();
        List<Double> balanceList = rawData.stream().map(item -> ((Number) item.get("balance")).doubleValue()).toList();

        Map<String, Object> result = new HashMap<>();
        result.put("employee", employeeList);
        result.put("balance", balanceList);
        return result;
    }

    public Map<String, Object> getOrderOfWarehouseSumary(LocalDate startDate, LocalDate endDate) {
        List<Integer> orderCountData = orderRepository.getOrderWarehouseByStatus(startDate, endDate,
                Stream.of(StatusOrder.values())
                        .map(item -> item.name())
                        .toList());
        List<Integer> orderNotProcessData = orderRepository.getOrderWarehouseByStatus(startDate, endDate,
                List.of("UU_TIEN", "THUE", "NONE"));
        List<Integer> orderProcessData = orderRepository.getOrderWarehouseByStatus(startDate, endDate,
                List.of("WAITING", "PED", "DA_LAM_LAI", "CHO_TRACK"));
        List<Integer> orderCancelData = orderRepository.getOrderWarehouseByStatus(startDate, endDate,
                List.of("CANCEL", "REFUND"));
        List<Integer> orderCompleteData = orderRepository.getOrderWarehouseByStatus(startDate, endDate,
                List.of("CO_TRACK", "DA_NHAP_TRACK"));

        List<String> employeeList = employeeRepository.getEmployeeNameWarehouse();

        List<ChartDatasetDTO> dataset = new ArrayList<>();
        dataset.add(new ChartDatasetDTO("Tổng đơn phụ trách", orderCountData, "#2563EB"));
        dataset.add(new ChartDatasetDTO("Đơn chưa xử lí", orderNotProcessData, "#06B6D4"));
        dataset.add(new ChartDatasetDTO("Đơn đang xử lí", orderProcessData, "#FBBF24"));
        dataset.add(new ChartDatasetDTO("Đơn hủy", orderCancelData, "#EA580C"));
        dataset.add(new ChartDatasetDTO("Đơn hoàn thành", orderCompleteData, "#D946EF"));

        Map<String, Object> result = new HashMap<>();
        result.put("employee", employeeList);
        result.put("dataset", dataset);

        return result;
    }

}