package sales.management.app.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import sales.management.app.entity.Employee;
import sales.management.app.enums.Role;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    Optional<Employee> findByEmail(String email);

    @Query(value = "SELECT * FROM employees  WHERE role = :role AND status = 'DANG_LAM_VIEC'", nativeQuery = true)
    List<Employee> findByRole(Role role);
}
