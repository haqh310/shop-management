package sales.management.app.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import sales.management.app.dto.OrderWarehouseDTO;
import sales.management.app.entity.OrderWarehouse;

public interface WarehouseRepository extends JpaRepository<OrderWarehouse, String> {
    @Query(value = """
            SELECT new sales.management.app.dto.OrderWarehouseDTO(
                orderNumber, tracking, orderNumberWarehouse, zip, email, password, phoneNumber,noteWarehouse1, noteWarehouse2, linkEvidence, noteWarehouse
            )
            FROM OrderWarehouse
            WHERE orderNumber = :orderNumber
            """)
    Optional<OrderWarehouseDTO> findByOrderNumber(@Param("orderNumber") String orderNumber);

}
