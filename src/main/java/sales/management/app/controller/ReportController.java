package sales.management.app.controller;

import java.time.LocalDate;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.ui.Model;

import sales.management.app.service.ReportService;
import sales.management.app.utils.BasicFunc;

@Controller
@RequestMapping("/report")
public class ReportController {

        private final ReportService reportService;

        public ReportController(ReportService reportService) {
                this.reportService = reportService;
        }

        @GetMapping("")
        public String showReport(
                        @RequestParam(name = "monthText", required = false) String monthText,
                        Model model) {
                // Lấy ngày đàu tháng và cuối tháng
                LocalDate[] dateInMonth = BasicFunc.getStartAndEndMonth(monthText);

                // Tổng doanh thu và đơn hàng ngày hôm nay
                Map<String, Object> dataToday = reportService.findOrderSummary(LocalDate.now(),
                                LocalDate.now());

                // Tổng doanh thu và đơn hàng trong tháng
                Map<String, Object> dataMonth = reportService.findOrderSummary(dateInMonth[0],
                                dateInMonth[1]);

                // Doanh thu và đơn hàng cho từng ngày trong tháng
                Map<String, Object> dataDaily = reportService.findDailySumary(dateInMonth[0], dateInMonth[1]);

                // Dữ liệu doanh thu và đơn hàng từng nhân viên cho từng ngày trong tháng
                Map<String, Object> sellerDaily = reportService.findSellerDailySumary(
                                dateInMonth[0],
                                dateInMonth[1]);

                // Convert dữ liệu dataset sang Json
                ObjectMapper objectMapper = new ObjectMapper();
                String balanceDtasetsJson = "";
                String orderCountDatasetsJson = "";
                try {
                        balanceDtasetsJson = objectMapper
                                        .writeValueAsString(sellerDaily.get("balanceDataset"));
                        orderCountDatasetsJson = objectMapper
                                        .writeValueAsString(sellerDaily.get("orderCountDataSet"));
                } catch (JsonProcessingException e) {
                        e.printStackTrace(); // Log lỗi nếu ép kiểu JSON thất bại
                }

                // Tổng doanh thu và đơn hàng của nhân viên bán hàng trong tháng
                Map<String, Object> sellerSumary = reportService.findSellerSumary(dateInMonth[0],
                                dateInMonth[1]);

                // Số đơn hàng theo tình trạng đơn hàng trong tháng
                Map<String, Object> statusSumary = reportService.findStatusDailySumary(
                                dateInMonth[0],
                                dateInMonth[1]);

                // Tổng doanh thu theo nền tảng trong tháng
                Map<String, Object> platformSumary = reportService.findPlatformDailySumary(dateInMonth[0],
                                dateInMonth[1]);

                // Tổng doanh thu theo account cua nhan vien ban hang trong tháng
                Map<String, Object> accountSumary = reportService.findEmployeeAccountSumary(dateInMonth[0],
                                dateInMonth[1]);

                model.addAttribute("dataToday", dataToday);
                model.addAttribute("dataMonth", dataMonth);
                model.addAttribute("dataDaily", dataDaily);
                model.addAttribute("balanceDtasetsJson", balanceDtasetsJson);
                model.addAttribute("orderCountDatasetsJson", orderCountDatasetsJson);
                model.addAttribute("sellerSumary", sellerSumary);
                model.addAttribute("statusSumary", statusSumary);
                model.addAttribute("platformSumary", platformSumary);
                model.addAttribute("accountSumary", accountSumary);

                model.addAttribute("months", BasicFunc.getMonth());
                model.addAttribute("monthText", monthText);
                model.addAttribute("activePage", "report");

                return "report";
        }
}
