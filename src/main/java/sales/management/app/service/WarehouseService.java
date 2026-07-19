package sales.management.app.service;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.security.core.Authentication;

import sales.management.app.dto.OrderWarehouseDTO;
import sales.management.app.entity.CustomUserDetails;
import sales.management.app.entity.OrderWarehouse;
import sales.management.app.repository.WarehouseRepository;

@Service
public class WarehouseService {

    private final WarehouseRepository warehouseRepository;

    WarehouseService(WarehouseRepository warehouseRepository) {
        this.warehouseRepository = warehouseRepository;
    }

    public CustomUserDetails getCurrentUserDetails() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails) {
            return (CustomUserDetails) authentication.getPrincipal();
        }
        return null;
    }

    public OrderWarehouseDTO findById(Long id) {
        return warehouseRepository.findByIdForForm(id).orElse(null);
    }

    public void saveOrderWarehouse(Long id, OrderWarehouseDTO createOrderWarehouseDTO) throws Exception {
        OrderWarehouse orderWarehouse = warehouseRepository.findById(id).orElse(null);

        if (orderWarehouse == null) {
            throw new Exception("Order Warehouse not found");
        }

        orderWarehouse.setTracking(createOrderWarehouseDTO.getTracking());
        orderWarehouse.setOrderNumberWarehouse(createOrderWarehouseDTO.getOrderNumberWarehouse());
        orderWarehouse.setZip(createOrderWarehouseDTO.getZip());
        orderWarehouse.setEmail(createOrderWarehouseDTO.getEmail());
        orderWarehouse.setPassword(createOrderWarehouseDTO.getPassword());
        orderWarehouse.setPhoneNumber(createOrderWarehouseDTO.getPhoneNumber());
        orderWarehouse.setNoteWarehouse1(createOrderWarehouseDTO.getNoteWarehouse1());
        orderWarehouse.setNoteWarehouse2(createOrderWarehouseDTO.getNoteWarehouse2());
        orderWarehouse.setLinkEvidence(createOrderWarehouseDTO.getLinkEvidence());
        orderWarehouse.setNoteWarehouse(createOrderWarehouseDTO.getNoteWarehouse());

        // get user create order warehouse
        CustomUserDetails user = getCurrentUserDetails();
        orderWarehouse.setEmployee(user.getEmployee());

        warehouseRepository.save(orderWarehouse);
    }
}
