package sales.management.app.controller;

import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import io.micrometer.common.lang.NonNull;

import org.springframework.ui.Model;

import sales.management.app.dto.AccountListDTO;
import sales.management.app.enums.StatusAccount;
import sales.management.app.service.AccountService;

@Controller
@RequestMapping("/account")
public class AccountController {

    private final AccountService accountService;

    AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @GetMapping({ "", "/" })
    public String listAccount(
            @RequestParam(name = "page", required = false, defaultValue = "0") String page,
            @RequestParam(name = "size", required = false, defaultValue = "20") String size,
            @RequestParam(name = "keyword", required = false, defaultValue = "") String keyword,
            @RequestParam(name = "statusAccount", required = false, defaultValue = "") String statusAccount,
            Model model) {

        Pageable pageable = PageRequest.of(Integer.parseInt(page), Integer.parseInt(size));
        Page<AccountListDTO> accountPage = accountService.findAccounts(keyword, statusAccount, pageable);

        int currentPage = accountPage.getNumber();
        int totalPages = accountPage.getTotalPages();

        int start = Math.max(0, currentPage - 1);
        int end = Math.min(totalPages - 1, currentPage + 1);

        model.addAttribute("activePage", "order");
        model.addAttribute("accountPage", accountPage);
        model.addAttribute("keyword", keyword);
        model.addAttribute("statusAccount", statusAccount);
        model.addAttribute("startPage", start);
        model.addAttribute("endPage", end);

        return "account-list";
    }

    @PutMapping("/{id}/status")
    @ResponseBody
    public ResponseEntity<Void> updateStatus(@PathVariable(name = "id") @NonNull String id,
            @RequestBody Map<String, String> body) {
        String status = body.get("status");
        try {
            StatusAccount statusAccount = StatusAccount.valueOf(status);
            accountService.updateSatus(id, statusAccount);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok().build();
    }

}
