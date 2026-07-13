package sales.management.app.utils;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class BasicFunc {
    public static String formatCurrency(double amount) {
        return String.format("$%,.2f", amount);
    }

    public static List<String> getMonth() {
        List<String> monthYears = new ArrayList<>();

        LocalDate now = LocalDate.now();

        for (int i = 0; i < 3; i++) {
            monthYears.add(now.minusMonths(i).getMonthValue() + "-" + now.minusMonths(i).getYear());
        }

        return monthYears;
    }

    public static LocalDate[] getStartAndEndMonth(String monthText) {
        int month, year;
        if (monthText == null || monthText == "") {
            month = LocalDate.now().getMonthValue();
            year = LocalDate.now().getYear();
        } else {
            String[] d = monthText.split("-");
            month = Integer.parseInt(d[0]);
            year = Integer.parseInt(d[1]);
        }
        YearMonth yearMonth = YearMonth.of(year, month);
        LocalDate startDate = yearMonth.atDay(1);
        LocalDate endDate = yearMonth.atEndOfMonth();
        return new LocalDate[] { startDate, endDate };
    }

    public static String getRandomColor() {
        Random obj = new Random();
        int rand_num = obj.nextInt(0xffffff + 1);
        return String.format("#%06x", rand_num);
    }

    public static Map<Integer, LocalDate[]> getWeeksInMonth(String monthText) {
        LocalDate[] date = getStartAndEndMonth(monthText);

        Map<Integer, LocalDate[]> rs = new HashMap<>();
        LocalDate startDate = date[0];
        for (int i = 1; i <= 4; i++) {
            if (i < 4) {
                rs.put(i, new LocalDate[] { startDate, startDate.plusDays(6) });
            } else {
                rs.put(i, new LocalDate[] { startDate, date[1] });
            }
            startDate = startDate.plusDays(7);
        }

        return rs;

    }
}
