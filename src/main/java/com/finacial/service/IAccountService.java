package com.finacial.service;

import com.finacial.dto.AccountDTO;
import com.finacial.model.Account;
import com.finacial.model.Status;
import com.finacial.model.VerificationToken;
import org.springframework.stereotype.Service;

@Service("accountService")
public interface IAccountService {
    String generatePassWordSalt();

    String getPasswordsaltByUsername(String username);

    AccountDTO toDto(Account account);

    void updateStatus(Long accId, Status status);

    void activeAccount(Long accId);
}
