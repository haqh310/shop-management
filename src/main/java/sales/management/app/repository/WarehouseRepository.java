package sales.management.app.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import sales.management.app.dto.OrderWarehouseDTO;
import sales.management.app.entity.OrderWarehouse;

public interface WarehouseRepository extends JpaRepository<OrderWarehouse, Long> {
    @Query(value = """
            SELECT new sales.management.app.dto.OrderWarehouseDTO(
            ow.id, o.orderNumber, ow.tracking, ow.orderNumberWarehouse, ow.zip, ow.email, ow.password,
            ow.phoneNumber, ow.noteWarehouse1, ow.noteWarehouse2, ow.linkEvidence, ow.noteWarehouse
            )
            FROM OrderWarehouse ow
            LEFT JOIN ow.order o
            WHERE ow.id = :id
            """)
    Optional<OrderWarehouseDTO> findByIdForForm(@Param("id") Long id);

    @Query(value = """
            SELECT e.id
            FROM OrderWarehouse ow
            LEFT JOIN ow.employee e
            ORDER BY ow.createdAt DESC
            LIMIT :number
            """)
    List<Integer> getEmployeeIdInFirst(@Param("number") Integer number);
}
