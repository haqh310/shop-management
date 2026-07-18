package sales.management.app.controller;

import java.time.LocalDate;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import sales.management.app.service.PerformanceService;
import sales.management.app.utils.BasicFunc;

import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequestMapping("/performance")
public class PerformanceController {

    private final PerformanceService performanceService;

    public PerformanceController(PerformanceService performanceService) {
        this.performanceService = performanceService;
    }

    @GetMapping("/seller")
    public String getSellerScreen(@RequestParam(name = "monthText", required = false) String monthText,
            Model model) {
        // Lấy ngày đàu tháng và cuối tháng
        LocalDate[] dateInMonth = BasicFunc.getStartAndEndMonth(monthText);
        Map<String, Object> dataMonth = performanceService.findTopSeller(dateInMonth[0], dateInMonth[1]);

        // Doanh Thu nhân viên trong toàn tháng
        Map<Integer, LocalDate[]> weeks = BasicFunc.getWeeksInMonth(monthText);

        Map<String, Object> statusData = performanceService.findAccountStatusSumary();

        // Doanh thu nhân viên theo tuần
        Map<String, Object> dataWeekOne = performanceService.findTopSeller(weeks.get(1)[0], weeks.get(1)[1]);
        Map<String, Object> dataWeekTwo = performanceService.findTopSeller(weeks.get(2)[0], weeks.get(2)[1]);
        Map<String, Object> dataWeekThree = performanceService.findTopSeller(weeks.get(3)[0], weeks.get(3)[1]);
        Map<String, Object> dataWeekFour = performanceService.findTopSeller(weeks.get(4)[0], weeks.get(4)[1]);

        model.addAttribute("dataMonth", dataMonth);
        model.addAttribute("dataWeekOne", dataWeekOne);
        model.addAttribute("dataWeekTwo", dataWeekTwo);
        model.addAttribute("dataWeekThree", dataWeekThree);
        model.addAttribute("dataWeekFour", dataWeekFour);

        model.addAttribute("statusData", statusData);

        model.addAttribute("months", BasicFunc.getMonth());
        model.addAttribute("monthText", monthText);
        model.addAttribute("activePage", "performance");
        return "performance-seller";
    }

    @GetMapping("/warehouse")
    public String getWarehouseScreen(@RequestParam(name = "monthText", required = false) String monthText,
            Model model) {
        // Lấy ngày đàu tháng và cuối tháng
        LocalDate[] dateInMonth = BasicFunc.getStartAndEndMonth(monthText);

        // Thống kê đơn hàng của kho trong hôm nay
        Map<String, Object> todayOrderData = performanceService.getOrderOfWarehouseSumary(LocalDate.now(),
                LocalDate.now());

        // Thống kê doanh thu của kho trong hôm nay
        Map<String, Object> todayBalanceData = performanceService.getBalanceOfWarehouse(LocalDate.now(),
                LocalDate.now());

        // Thống kê đơn hàng của kho trong tháng
        Map<String, Object> monthOrderData = performanceService.getOrderOfWarehouseSumary(dateInMonth[0],
                dateInMonth[1]);

        // Thống kê đơn hàng của kho trong tháng
        Map<String, Object> monthBalanceData = performanceService.getBalanceOfWarehouse(dateInMonth[0],
                dateInMonth[1]);

        // Convert dữ liệu dataset sang Json
        ObjectMapper objectMapper = new ObjectMapper();
        String todayStatusDatasetsJson = "";
        String monthStatusDatasetsJson = "";
        try {
            todayStatusDatasetsJson = objectMapper
                    .writeValueAsString(todayOrderData.get("dataset"));
            monthStatusDatasetsJson = objectMapper
                    .writeValueAsString(monthOrderData.get("dataset"));
        } catch (JsonProcessingException e) {
            e.printStackTrace(); // Log lỗi nếu ép kiểu JSON thất bại
        }

        model.addAttribute("employeeList", todayOrderData.get("employee"));
        model.addAttribute("todayOrderDataJson", todayStatusDatasetsJson);
        model.addAttribute("todayBalanceData", todayBalanceData.get("balance"));
        model.addAttribute("monthOrderDataJson", monthStatusDatasetsJson);
        model.addAttribute("monthBalanceData", monthBalanceData.get("balance"));

        model.addAttribute("months", BasicFunc.getMonth());
        model.addAttribute("monthText", monthText);
        model.addAttribute("activePage", "performance");
        return "performance-warehouse";

    }

}
