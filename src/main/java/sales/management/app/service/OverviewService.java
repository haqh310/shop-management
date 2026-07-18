package sales.management.app.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import sales.management.app.dto.AccountSellertReportDTO;
import sales.management.app.dto.ChartDatasetDTO;
import sales.management.app.dto.PlatformDetail;
import sales.management.app.enums.StatusAccount;
import sales.management.app.repository.AccountRepository;

@Service
public class OverviewService {
    private final AccountRepository accountRepository;

    OverviewService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public Map<String, Object> getAccountCountByPlatform() {
        List<Map<String, Object>> rawData = accountRepository.getAccountStatusCountByPlatform();
        Map<String, Integer> rawDataMap = rawData.stream()
                .collect(Collectors.toMap(
                        item -> item.get("platform").toString() + "-" + item.get("status").toString(),
                        item -> ((Number) item.get("count")).intValue(),
                        (existing, replacement) -> existing));

        List<String> platformList = rawData.stream().map(item -> item.get("platform").toString()).distinct().toList();
        List<ChartDatasetDTO> datasets = new ArrayList<>();
        List<Integer> totalList = new ArrayList<>();
        for (StatusAccount statusAccount : StatusAccount.values()) {
            List<Integer> dataList = new ArrayList<>();
            int total = 0;
            for (String platform : platformList) {
                String key = platform + "-" + statusAccount.name();
                int data = rawDataMap.getOrDefault(key, 0);
                dataList.add(data);
                total += data;
            }
            datasets.add(new ChartDatasetDTO(statusAccount.getLabel(), dataList, statusAccount.getBackgroudColor()));
            totalList.add(total);

        }
        Map<String, Object> result = new HashMap<>();
        result.put("platform", platformList);
        result.put("chartDataset", datasets);
        result.put("total", totalList);
        return result;

    }

    public Map<String, Object> getAccountBySellerPlatform() {
        List<Map<String, Object>> rawData = accountRepository.getAccountBySellerPlatform();

        Map<String, Integer> rawDataMap = rawData.stream()
                .collect(Collectors.toMap(
                        item -> item.get("employeeName").toString() + "-" + item.get("platform").toString() + "-"
                                + item.get("status").toString(),
                        item -> ((Number) item.get("count")).intValue(),
                        (existing, replacement) -> existing));

        List<String> employeeList = rawData.stream().map(item -> item.get("employeeName").toString()).distinct()
                .toList();
        List<String> platformList = rawData.stream().map(item -> item.get("platform").toString()).distinct().toList();

        List<AccountSellertReportDTO> reportList = new ArrayList<>();

        for (String employee : employeeList) {
            AccountSellertReportDTO sellerDto = new AccountSellertReportDTO();
            sellerDto.setSellerName(employee);

            for (String platform : platformList) {
                List<Integer> countList = new ArrayList<>();
                int total = 0;
                for (StatusAccount status : StatusAccount.values()) {
                    String key = employee + "-" + platform + "-" + status.name();
                    int count = rawDataMap.getOrDefault(key, 0);
                    countList.add(count);
                    total += count;
                }
                boolean allZero = countList.stream().allMatch(i -> i == 0);
                if (!allZero) {
                    sellerDto.getPlatforms().add(new PlatformDetail(platform, countList, total));
                }
            }

            reportList.add(sellerDto);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("data", reportList);
        return result;

    }
}
