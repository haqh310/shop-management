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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String proxy;
    private Integer status;
    @Temporal(TemporalType.DATE)
    private Date expiryDate;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_name")
    private Account account;
    private String supplier;

}