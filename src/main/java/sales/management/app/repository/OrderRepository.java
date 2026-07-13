package sales.management.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import sales.management.app.dto.EmployeeDataset;
import sales.management.app.dto.ChartDTO;
import sales.management.app.entity.Order;

public interface OrderRepository extends JpaRepository<Order, String> {

        /*
         * Láy dữ liệu theo keyword, trạng thái đơn hàng trong khoảng thời gian chỉ định
         */
        @Query(value = """
                                SELECT *
                                FROM orders
                                WHERE (order_number LIKE CONCAT('%', :keyword, '%') OR product_name LIKE CONCAT('%', :keyword, '%'))
                                AND (:status IS NULL OR status = :status)
                                AND order_date >= :startDate
                                AND order_date <= :endDate
                        """, nativeQuery = true)
        Page<Order> search(@Param("keyword") String keyword, @Param("status") String status,
                        @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate,
                        Pageable pageable);

        /*
         * Thống kế tổng doanh thu và đơn hàng theo từng ngày
         */
        @Query(value = """
                        SELECT order_date AS orderDay, COALESCE(SUM(balance), 0) AS balance, COALESCE(COUNT(order_number),0) AS orderCount
                        FROM orders
                        WHERE order_date >= :startDate AND order_date <= :endDate
                        AND status NOT IN ('CANCEL', 'REFUND')
                        GROUP BY order_date
                        ORDER BY order_date ASC
                                """, nativeQuery = true)
        List<ChartDTO> findDailySumary(@Param("startDate") LocalDate startDate,
                        @Param("endDate") LocalDate endDate);

        /*
         * Thống kế tổng doanh thu và đơn hàng trong khoảng thời gian chỉ định
         */
        @Query(value = """
                        SELECT COALESCE(COUNT(order_number), 0) AS orderCount, COALESCE(SUM(balance),0) AS balance
                        FROM orders
                        WHERE order_date >= :startDate AND order_date <= :endDate
                        AND status NOT IN ('CANCEL', 'REFUND')
                        """, nativeQuery = true)
        Map<String, Object> findOrderSummary(@Param("startDate") LocalDate startDate,
                        @Param("endDate") LocalDate endDate);

        /*
         * Thống kế tổng doanh thu và đơn hàng của nhân viên bán hàng theo từng ngày
         */
        @Query(value = """
                        SELECT e.name AS employeeName, o.order_date AS orderDate, COALESCE(SUM(o.balance), 0) AS balance, COALESCE(COUNT(o.order_number), 0) As orderCount
                        FROM orders o
                        JOIN accounts a ON a.account_name = o.account_name
                        JOIN employees e ON e.id = a.employee_id
                        WHERE o.order_date >= :startDate
                        AND o.order_date <= :endDate
                        AND o.status NOT IN ('CANCEL', 'REFUND')
                        GROUP BY o.order_date, e.name
                        ORDER BY o.order_date ASC
                        """, nativeQuery = true)
        List<EmployeeDataset> findSellerDailySumary(@Param("startDate") LocalDate startDate,
                        @Param("endDate") LocalDate endDate);

        /*
         * Thống kế tổng doanh thu và đơn hàng của nhân viên trong khoảng thời gian chỉ
         * định
         */
        @Query(value = """
                        SELECT e.name AS employeeName, COALESCE(SUM(o.balance),0) AS balance, COALESCE(COUNT(o.order_number),0) As orderCount
                        FROM orders o
                        JOIN accounts a ON a.account_name = o.account_name
                        JOIN employees e ON e.id = a.employee_id
                        WHERE o.order_date >= :startDate
                        AND o.order_date <= :endDate
                        AND o.status NOT IN ('CANCEL', 'REFUND')
                        GROUP BY  e.name
                        """, nativeQuery = true)
        List<Map<String, Object>> findSellerSumary(@Param("startDate") LocalDate startDate,
                        @Param("endDate") LocalDate endDate);

        /*
         * Thống kế doanh thu và đơn hàng theo tình trạng đơn hàng trong khoảng thời
         * gian chỉ định
         */
        @Query(value = """
                        SELECT status,  COALESCE(COUNT(order_number), 0) AS orderCount
                        FROM orders
                        WHERE order_date >= :startDate
                        AND order_date <= :endDate
                        GROUP BY  status """, nativeQuery = true)
        List<Map<String, Object>> findStatusDailySumary(@Param("startDate") LocalDate startDate,
                        @Param("endDate") LocalDate endDate);

        /*
         * Thống kế doanh thu và đơn hàng của nền tảng trong khoảng thời gian chỉ định
         */
        @Query(value = """
                        SELECT a.platform As platform,  COALESCE(SUM(o.balance), 0) As balance
                        FROM orders o
                        JOIN accounts a ON a.account_name = o.account_name
                        WHERE o.order_date >= :startDate
                        AND o.order_date <= :endDate
                        GROUP BY a.platform
                        """, nativeQuery = true)
        List<Map<String, Object>> findPlatformDailySumary(@Param("startDate") LocalDate startDate,
                        @Param("endDate") LocalDate endDate);

        /*
         * Thống kế doanh thu của account của nhân viên bán hàng trong khoảng thời gian
         * chỉ định
         */
        @Query(value = """
                        SELECT a.account_name As accountName, e.name As employeeName,  COALESCE(SUM(o.balance), 0) As balance
                        FROM orders o
                        JOIN accounts a ON a.account_name = o.account_name
                        JOIN employees e ON e.id = a.employee_id
                        WHERE o.order_date >= :startDate
                        AND o.order_date <= :endDate
                        GROUP BY a.account_name """, nativeQuery = true)
        List<Map<String, Object>> findEmployeeAccountSumary(@Param("startDate") LocalDate startDate,
                        @Param("endDate") LocalDate endDate);

        /*
         * Thống kế tổng doanh thu và đơn hàng của top 10 nhân viên trong khoảng thời
         * gian chỉ
         * định
         */
        @Query(value = """
                        SELECT e.name As employeeName, COALESCE(SUM(o.balance),0) AS balance, COALESCE(COUNT(o.order_number),0) As orderCount
                        FROM orders o
                        JOIN accounts a ON a.account_name = o.account_name
                        JOIN employees e ON e.id = a.employee_id
                        WHERE o.order_date >= :startDate
                        AND o.order_date <= :endDate
                        AND o.status NOT IN ('CANCEL', 'REFUND')
                        GROUP BY  e.name
                        ORDER BY balance DESC
                        LIMIT 10
                        """, nativeQuery = true)
        List<Map<String, Object>> findTopSeller(@Param("startDate") LocalDate startDate,
                        @Param("endDate") LocalDate endDate);

}