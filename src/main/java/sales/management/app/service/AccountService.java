package sales.management.app.service;

import java.util.List;

import org.springframework.stereotype.Service;

import sales.management.app.entity.Account;
import sales.management.app.enums.StatusAccount;
import sales.management.app.repository.AccountRepository;

@Service
public class AccountService {

    private final AccountRepository accountRepository;

    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public List<Account> getAllAccounts() {
        return accountRepository.findAll();
    }

    public List<Account> getAccountLive() {
        return accountRepository.findByStatus(StatusAccount.LIVE);
    }

}
