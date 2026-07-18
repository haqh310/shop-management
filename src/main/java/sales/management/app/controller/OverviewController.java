package sales.management.app.controller;

import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import sales.management.app.service.OverviewService;
import sales.management.app.service.PerformanceService;

@Controller
@RequestMapping("/overview")
public class OverviewController {

    private final OverviewService overviewService;
    private final PerformanceService performanceService;

    OverviewController(OverviewService overviewService, PerformanceService performanceService) {
        this.overviewService = overviewService;
        this.performanceService = performanceService;
    }

    @GetMapping("")
    public String getOverview(Model model) {

        Map<String, Object> statusData = overviewService.getAccountCountByPlatform();

        Map<String, Object> sellerData = performanceService.findAccountStatusSumary();

        Map<String, Object> sellerPlatformData = overviewService.getAccountBySellerPlatform();

        model.addAttribute("statusData", statusData);
        model.addAttribute("sellerData", sellerData);
        model.addAttribute("sellerPlatformData", sellerPlatformData);
        model.addAttribute("activePage", "overview");
        return "overview";
    }

}
