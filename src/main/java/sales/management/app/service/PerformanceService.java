package sales.management.app.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import sales.management.app.dto.ChartDatasetDTO;
import sales.management.app.enums.StatusAccount;
import sales.management.app.repository.AccountRepository;
import sales.management.app.repository.OrderRepository;

@Service
public class PerformanceService {

    private final OrderRepository orderRepository;
    private final AccountRepository accountRepository;

    public PerformanceService(OrderRepository orderRepository, AccountRepository accountRepository) {
        this.orderRepository = orderRepository;
        this.accountRepository = accountRepository;
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

}