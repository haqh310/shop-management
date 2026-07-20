package sales.management.app.dto;

import java.util.Date;

import sales.management.app.enums.StatusAccount;

public interface AccountListDTO {
    String getAccountName();

    String getLogin();

    String getPlatform();

    String getEmployeeName();

    StatusAccount getStatus();

    Date getIssueDate();

    Date getActionDate();

    Date getDieDateWhite();

    Date getDieDate();

    Date getPayoutDate();

    String getDieReason();

    String getProxy();

    String getInf();

    String getSsn();

    String getPhoneReg();

    String getEmail();

    String getEmailPassword();

    String getRecoveryEmail();

    String getRecoveryEmail2FA();

    String getPlatformPassword();

    String getDocs();

    String getPlatform2FA();

    String getNote1();

    String getNote2();
}
