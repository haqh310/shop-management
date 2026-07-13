package sales.management.app.entity;

import jakarta.persistence.*;
import lombok.*;
import sales.management.app.enums.Role;
import sales.management.app.enums.StatusEmployee;

import java.util.*;

@Entity
@Table(name = "employees")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Employee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String name;
    @Enumerated(EnumType.STRING)
    private StatusEmployee status;
    @Column(unique = true)
    private String email;
    private String password;
    private String identityCard;
    private String identityCardImage;
    private String phoneNumber;
    @Temporal(TemporalType.DATE)
    private Date birthday;
    private String address;
    @Enumerated(EnumType.STRING)
    private Role role;
    private String bankNumber;
    @Temporal(TemporalType.DATE)
    private Date leaving_date;
    private String color;
    private String avatar;
    @OneToMany(mappedBy = "employee")
    private List<Order> orderList = new ArrayList<>();
    @OneToMany(mappedBy = "employee")
    private List<Account> accountList = new ArrayList<>();
}
