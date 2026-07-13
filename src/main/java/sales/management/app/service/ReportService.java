package sales.management.app.service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import sales.management.app.dto.ChartDTO;
import sales.management.app.dto.ChartDatasetDTO;
import sales.management.app.dto.EmployeeDataset;
import sales.management.app.enums.StatusOrder;
import sales.management.app.repository.OrderRepository;
import sales.management.app.utils.BasicFunc;

@Service
public class ReportService {

    private final OrderRepository orderRepository;

    public ReportService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    /*
     * Function lấy doanh thu và đơn hàng trong khoảng thời gian
     */
    public Map<String, Object> findOrderSummary(LocalDate startDate, LocalDate endDate) {
        return orderRepository.findOrderSummary(startDate, endDate);
    }

    /*
     * Function lấy doanh thu và đơn hàng của từng ngày
     */
    public Map<String, Object> findDailySumary(LocalDate startDate, LocalDate endDate) {
        List<ChartDTO> rawData = orderRepository.findDailySumary(startDate,
                endDate);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        List<String> dateList = rawData.stream()
                .map(item -> item.getOrderDate().format(formatter))
                .toList();
        List<Double> balanceList = rawData.stream()
                .map(item -> item.getBalance()).toList();
        List<Integer> countList = rawData.stream().map(item -> item.getCount()).toList();

        Map<String, Object> result = new HashMap<>();
        result.put("orderDate", dateList);
        result.put("balance", balanceList);
        result.put("orderCount", countList);

        return result;
    }

    /*
     * Function lấy doanh thu và đơn hàng của nhân viên bán hàng trong từng ngày
     */
    public Map<String, Object> findSellerDailySumary(LocalDate startDate, LocalDate endDate) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        List<EmployeeDataset> rawData = orderRepository.findSellerDailySumary(startDate, endDate);
        List<String> allDates = rawData.stream()
                .map(item -> item.getOrderDate().format(formatter))
                .distinct()
                .toList();
        List<String> allEmployees = rawData.stream()
                .map(item -> item.getEmployeeName())
                .distinct()
                .toList();

        Map<String, Object[]> rawDataMap = rawData.stream()
                .collect(Collectors.toMap(item -> item.getOrderDate().format(formatter) + "_" + item.getEmployeeName(),
                        item -> new Object[] { item.getBalance(), item.getOrderCount() },
                        (existing, replacement) -> existing));

        List<ChartDatasetDTO> employeeBalances = new ArrayList<>();
        List<ChartDatasetDTO> employeeCounts = new ArrayList<>();

        for (String employeeName : allEmployees) {
            List<Double> balanceLabels = new ArrayList<>();
            List<Integer> orderCountLabels = new ArrayList<>();

            for (String dateStr : allDates) {
                String key = dateStr + "_" + employeeName;
                Object[] match = rawDataMap.get(key);

                if (match != null) {
                    balanceLabels.add(((Number) match[0]).doubleValue());
                    orderCountLabels.add(((Number) match[1]).intValue());
                } else {
                    balanceLabels.add(0.0);
                    orderCountLabels.add(0);
                }
            }

            String bgColor = BasicFunc.getRandomColor();
            employeeBalances.add(new ChartDatasetDTO(employeeName, balanceLabels, bgColor));
            employeeCounts.add(new ChartDatasetDTO(employeeName, orderCountLabels, bgColor));

        }
        Map<String, Object> result = new HashMap<>();
        result.put("balanceDataset", employeeBalances);
        result.put("orderCountDataSet", employeeCounts);
        return result;
    }

    /*
     * Function lấy doanh thu và đơn hàng của nhân viên bán hàng
     */
    public Map<String, Object> findSellerSumary(LocalDate startDate, LocalDate endDate) {
        List<Map<String, Object>> rawData = orderRepository.findSellerSumary(startDate, endDate);

        List<String> employeeList = new ArrayList<>();
        List<Double> balanceList = new ArrayList<>();
        List<Integer> orderCountList = new ArrayList<>();

        for (Map<String, Object> data : rawData) {
            String name = data.get("employeeName").toString();
            Double balance = Double.valueOf(data.get("balance").toString());
            Integer count = Integer.valueOf(data.get("orderCount").toString());

            employeeList.add(name);
            balanceList.add(balance);
            orderCountList.add(count);

        }
        Map<String, Object> result = new HashMap<>();
        result.put("employee", employeeList);
        result.put("balance", balanceList);
        result.put("orderCount", orderCountList);

        return result;
    }

    /*
     * Function lấy tổng đơn hàng theo trạng thái đơn hàng
     */
    public Map<String, Object> findStatusDailySumary(LocalDate startDate, LocalDate endDate) {
        List<Map<String, Object>> rawData = orderRepository.findStatusDailySumary(startDate, endDate);
        List<String> statusList = new ArrayList<>();
        Map<String, Integer> statusMap = new HashMap<>();
        for (StatusOrder st : StatusOrder.values()) {
            statusMap.put(st.name(), 0);
            statusList.add(st.getLabel());
        }
        for (Map<String, Object> data : rawData) {
            String status = data.get("status").toString();
            Integer balance = Integer.valueOf(data.get("orderCount").toString());
            statusMap.put(status, balance);
        }
        List<Object> balanceList = new ArrayList<>(statusMap.values());
        Map<String, Object> result = new HashMap<>();
        result.put("status", statusList);
        result.put("orderCount", balanceList);
        return result;
    }

    /*
     * Function lấy doanh thu theo nền tảng
     */
    public Map<String, Object> findPlatformDailySumary(LocalDate startDate, LocalDate endDate) {
        List<Map<String, Object>> rawData = orderRepository.findPlatformDailySumary(startDate, endDate);

        List<String> platformList = new ArrayList<>();
        List<Double> balanceList = new ArrayList<>();

        for (Map<String, Object> data : rawData) {
            String platform = data.get("platform").toString();
            Double balance = Double.valueOf(data.get("balance").toString());
            platformList.add(platform);
            balanceList.add(balance);

        }

        Map<String, Object> result = new HashMap<>();
        result.put("platform", platformList);
        result.put("balance", balanceList);

        return result;
    }

    /*
     * Function lấy doanh thu theo account theo từng ngày
     */
    public Map<String, Object> findEmployeeAccountSumary(LocalDate startDate, LocalDate endDate) {

        List<Map<String, Object>> rawData = orderRepository.findEmployeeAccountSumary(startDate, endDate);

        List<String> accountList = new ArrayList<>();
        List<String> employeeList = new ArrayList<>();
        List<Double> balanceList = new ArrayList<>();

        for (Map<String, Object> data : rawData) {
            String account = data.get("accountName").toString();
            String employee = data.get("employeeName").toString();
            Double balance = Double.valueOf(data.get("balance").toString());

            accountList.add(account);
            employeeList.add(employee);
            balanceList.add(balance);

        }
        Map<String, Object> result = new HashMap<>();
        result.put("account", accountList);
        result.put("employee", employeeList);
        result.put("balance", balanceList);
        return result;
    }

}
