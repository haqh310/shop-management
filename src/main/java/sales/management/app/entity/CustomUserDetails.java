
package sales.management.app.entity;

import lombok.*;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.*;

@Getter
@Setter
public class CustomUserDetails implements UserDetails {

    private final Employee employee;
    private final Collection<? extends GrantedAuthority> authorities;

    public CustomUserDetails(Employee employee, Collection<? extends GrantedAuthority> authorities) {
        this.employee = employee;
        this.authorities = authorities;
    }

    public Integer getId() {
        return employee.getId();
    }

    public String getName() {
        return employee.getName();
    }

    public String getEmail() {
        return employee.getEmail();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Chuyển đổi Role từ DB thành GrantedAuthority của Spring Security
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + employee.getRole()));
    }

    @Override
    public String getPassword() {
        return employee.getPassword();
    }

    @Override
    public String getUsername() {
        return employee.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public boolean isAdmin() {
        return hasRole("ROLE_QUAN_LY");
    }

    public boolean isSeller() {
        return hasRole("ROLE_BAN_HANG");
    }

    public boolean isWarehouse() {
        return hasRole("ROLE_KHO");
    }

    private boolean hasRole(String roleName) {
        if (this.authorities == null)
            return false;
        return this.authorities.stream()
                .anyMatch(auth -> auth.getAuthority().equals(roleName));
    }
}
