package sales.management.app.service;

import java.util.List;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import sales.management.app.entity.Employee;
import sales.management.app.enums.Role;
import sales.management.app.repository.EmployeeRepository;

@Service
public class EmployeeService {

    private final EmployeeRepository employeeRepository;

    public EmployeeService(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    public List<Employee> getEmployees() {
        return employeeRepository.findAll();
    }

    public List<Employee> getSellers() {
        return employeeRepository.findByRole(Role.BAN_HANG);
    }

    public List<Employee> getKhos() {
        return employeeRepository.findByRole(Role.KHO);
    }

    public Employee getNhanVienById(@NonNull Long id) {
        return employeeRepository.findById(id).orElse(null);
    }

}
