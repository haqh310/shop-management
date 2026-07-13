package sales.management.app.controller;

import java.time.LocalDate;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

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

    @GetMapping("")
    public String index(@RequestParam(required = false) String monthText,
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
        return "performance";
    }

}
