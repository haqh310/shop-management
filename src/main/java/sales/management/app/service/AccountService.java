package sales.management.app.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import sales.management.app.dto.AccountListDTO;
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

    public Page<AccountListDTO> findAccounts(String keyword, String status, Pageable pageable) {
        if (keyword != null) {
            keyword = keyword.trim();
        } else {
            keyword = "";
        }

        StatusAccount statusAccount = null;

        if (status != null && !status.isBlank()) {
            statusAccount = StatusAccount.valueOf(status);
        }

        Page<AccountListDTO> accountList = accountRepository.findAccountByCondition(keyword, statusAccount, pageable);

        return accountList;
    }

    public void updateSatus(String accountName, StatusAccount statusAccount) throws Exception {
        Account account = accountRepository.findByAccountName(accountName)
                .orElseThrow(() -> new Exception("Account not found"));

        account.setStatus(statusAccount);

        accountRepository.save(account);
    }

}
