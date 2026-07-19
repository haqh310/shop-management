package sales.management.app.service;

import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import sales.management.app.entity.Account;
import sales.management.app.entity.CustomUserDetails;
import sales.management.app.enums.StatusAccount;
import sales.management.app.repository.AccountRepository;

@Service
public class AccountService {

    private final AccountRepository accountRepository;

    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public CustomUserDetails getCurrentUserDetails() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails) {
            return (CustomUserDetails) authentication.getPrincipal();
        }
        return null;
    }

    public List<Account> getAllAccounts() {
        return accountRepository.findAll();
    }

    public List<Account> getAccountLive() {
        return accountRepository.findByStatus(StatusAccount.LIVE);
    }

    public List<String> getAccountNameByEmployee() {
        CustomUserDetails user = getCurrentUserDetails();
        return accountRepository.getAccountNameByEmployee(user.getId());
    }

}
