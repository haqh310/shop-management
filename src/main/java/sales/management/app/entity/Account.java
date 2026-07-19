package sales.management.app.entity;

import jakarta.persistence.*;
import lombok.*;
import sales.management.app.enums.*;

import java.util.*;

@Entity
@Table(name = "accounts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Account {
    @Id
    private String accountName;
    private String login;
    private String platform;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = true)
    private Employee employee;
    @Enumerated(EnumType.STRING)
    private StatusAccount status;
    @Temporal(TemporalType.DATE)
    private Date issueDate;
    @Temporal(TemporalType.DATE)
    private Date actionDate;
    @Temporal(TemporalType.DATE)
    private Date dieDateWhite;
    @Temporal(TemporalType.DATE)
    private Date dieDate;
    @Temporal(TemporalType.DATE)
    private Date payoutDate;
    @Column(columnDefinition = "TEXT")
    private String dieReason;
    private String proxy;
    private String inf;
    private String ssn;
    private String phoneReg;
    private String email;
    private String emailPassword;
    private String recoveryEmail;
    @Column(name = "recovery_email_2fa")
    private String recoveryEmail2FA;
    private String platformPassword;
    private String docs;
    @Column(name = "platform_2fa")
    private String platform2FA;
    private String note1;
    private String note2;
    @Builder.Default
    @OneToMany(mappedBy = "account")
    private List<Order> orderList = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "account")
    private List<Proxy> proxyList = new ArrayList<>();
}