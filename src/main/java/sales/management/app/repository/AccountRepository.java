package sales.management.app.repository;

import java.util.List;
import java.util.Map;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import sales.management.app.entity.Account;
import sales.management.app.enums.StatusAccount;

public interface AccountRepository extends JpaRepository<Account, String> {
    List<Account> findByStatus(StatusAccount tinhTrang);

    Account findByAccountName(String accountName);

    @Query(value = """
                SELECT e.name AS employeeName, COUNT(a.accountName) AS count
                FROM accounts a
                JOIN employees e ON e.id = a.employee_id
                GROUP BY e.name
            """, nativeQuery = true)
    List<Map<String, Object>> accountByEmployee();

    @Query(value = """
                SELECT e.name AS employeeName, a.status AS status
                FROM accounts a
                JOIN employees e ON e.id = a.employee_id
                GROUP BY a.status
            """, nativeQuery = true)
    List<Map<String, Object>> getStatusOfAccount();

    /*
     * Thống kế tình trạng account của nhân viên trong khoảng thời
     * gian chỉ
     * định
     */
    @Query(value = """
            SELECT COALESCE(e.name, '') As employeeName, a.status As status, COALESCE(COUNT(a.account_name),0) AS total
            FROM accounts a
            LEFT JOIN employees e ON e.id = a.employee_id
            GROUP BY  e.name, a.status
             ORDER BY e.name DESC
            """, nativeQuery = true)
    List<Map<String, Object>> findAccountStatusSumary();

    @Query(value = """
                SELECT COALESCE(a.platform, '') AS platform, a.status AS status, COUNT(a.account_name) AS count
                FROM accounts a
                GROUP BY a.platform, a.status
                ORDER BY a.platform DESC
            """, nativeQuery = true)
    List<Map<String, Object>> getAccountStatusCountByPlatform();

    @Query(value = """
                SELECT COALESCE(e.name, '') AS employeeName, COALESCE(a.platform, '') AS platform, a.status AS status, COUNT(a.account_name) AS count
                FROM accounts a
                LEFT JOIN employees e ON e.id = a.employee_id
                GROUP BY e.name, a.platform, a.status
                ORDER BY e.name DESC
            """, nativeQuery = true)
    List<Map<String, Object>> getAccountBySellerPlatform();

    @Query(value = """
                SELECT a.accountName
                FROM Account a
                LEFT JOIN a.employee e
                WHERE e.id = :employeeId
            """, nativeQuery = false)
    List<String> getAccountNameByEmployee(@Param("employeeId") Integer employeeId);

}
