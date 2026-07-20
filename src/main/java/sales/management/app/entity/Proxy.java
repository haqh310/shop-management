package sales.management.app.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.*;

@Entity
@Table(name = "proxies")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Proxy {
    @Id
    private String proxy;
    private boolean isActive;
    @Temporal(TemporalType.DATE)
    private Date expiryDate;
    private String supplier;
    @Builder.Default
    @OneToMany(mappedBy = "proxy")
    private List<Account> accountList = new ArrayList<>();

}