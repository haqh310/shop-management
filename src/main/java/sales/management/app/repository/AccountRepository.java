package sales.management.app.repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import sales.management.app.dto.AccountListDTO;
import sales.management.app.entity.Account;
import sales.management.app.enums.StatusAccount;

public interface AccountRepository extends JpaRepository<Account, String> {
    List<Account> findByStatus(StatusAccount tinhTrang);

    Optional<Account> findByAccountName(String accountName);

    @Query(value = """
                SELECT
                    a.accountName AS accountName,
                    a.login AS login,
                    a.platform AS platform,
                    e.name AS employeeName,
                    a.status AS status,
                    a.issueDate AS issueDate,
                    a.actionDate AS actionDate,
                    a.dieDateWhite AS dieDateWhite,
                    a.dieDate AS dieDate,
                    a.payoutDate AS payoutDate,
                    a.dieReason AS dieReason,
                    p.proxy AS proxy,
                    a.inf AS inf,
                    a.ssn AS ssn,
                    a.phoneReg AS phoneReg,
                    a.email AS email,
                    a.emailPassword AS emailPassword,
                    a.recoveryEmail AS recoveryEmail,
                    a.recoveryEmail2FA AS recoveryEmail2FA,
                    a.platformPassword AS platformPassword,
                    a.docs AS docs,
                    a.platform2FA AS platform2FA,
                    a.note1 AS note1,
                    a.note2 AS note2
                FROM Account a
                JOIN a.employee e
                JOIN a.proxy p
                WHERE a.accountName LIKE CONCAT('%', :keyword, '%')
                AND (:status IS NULL OR a.status = :status)
            """, nativeQuery = false)
    Page<AccountListDTO> findAccountByCondition(@Param("keyword") String keyword, @Param("status") StatusAccount status,
            Pageable pageable);

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
