package sales.management.app.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import sales.management.app.entity.Employee;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    Optional<Employee> findByEmail(String email);

    @Query(value = "SELECT * FROM employees  WHERE role = :role AND status = 'DANG_LAM_VIEC'", nativeQuery = true)
    List<Employee> findByRole(@Param("role") String role);

    @Query(value = """
                   SELECT name
                   FROM employees
                   WHERE role='KHO' AND status = 'DANG_LAM_VIEC'
                   ORDER BY name DESC
            """, nativeQuery = true)
    List<String> getEmployeeNameWarehouse();

}
