package com.finacial.security.service;

import com.finacial.config.Constants;
import com.finacial.dto.AccountDTO;
import com.finacial.dto.MessageResponse;
import com.finacial.model.Account;
import com.finacial.model.Status;
import com.finacial.model.VerificationToken;
import com.finacial.repository.VerificationTokenRepository;
import com.finacial.service.IAccountService;
import com.finacial.service.MailSenderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import javax.persistence.EntityManager;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

@Service("tokenVerifyService")
public class TokenVerifyService {

    @Value("${EXPIRATION_VERIFY_ACTIVE}")
    int expire;

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    private VerificationTokenRepository tokenRepository;

    @Autowired
    private MailSenderService mailSenderService;

    @Autowired
    IAccountService accountService;

    @Autowired
    Status sttTokenNotActived;

    @Autowired
    Status sttTokenActived;

    @Autowired
    private TransactionTemplate transactionTemplate;

    @Autowired
    private EntityManager entityManager;


    public void saveTokenSendVerificationToken(Account user) {
        VerificationToken verificationToken = generateVerifyToken(user);
        mailSenderService.sendEmailActive(user, tokenRepository.save(verificationToken).getToken());
    }

    public void resetSendVerificationToken(Account user, Long idTokenReset) {
        tokenRepository.deleteById(idTokenReset);
        saveTokenSendVerificationToken(user);
    }

    public MessageResponse VerifyToken(String token) {
        Optional<VerificationToken> vfTokenOpt = tokenRepository.findByToken(token);
        if (null != vfTokenOpt) {
            VerificationToken vfToken = vfTokenOpt.get();
            Date activeDate = new Date();
            if (vfToken.getStatus().equals(0L)) {
                if (activeDate.before(vfToken.getExpiryDate())) {
                    accountService.activeAccount(vfToken.getUser().getId());
                    updateStatus(vfToken.getId(), sttTokenActived);
                    return new MessageResponse(Constants.activeSuccess);
                } else {
                    //resend mail
                    resetSendVerificationToken(vfToken.getUser(), vfToken.getId());
                    return new MessageResponse(Constants.activeExpired);
                }
            } else {
                return new MessageResponse(Constants.activeSuccess);
            }
        } else {
            return new MessageResponse(Constants.activeUnSuccess);
        }
    }

    private VerificationToken generateVerifyToken(Account user) {
        VerificationToken verificationToken = new VerificationToken();
        String token = UUID.randomUUID().toString();
        StringBuilder tokenSb = new StringBuilder(token);
        tokenSb.append(user.getId());
        verificationToken.setToken(encoder.encode(tokenSb.toString()));
        verificationToken.setUser(user);
        verificationToken.setExpiryDate(new Date((new Date()).getTime() + (1000 * 60 * 60 * 24) * expire));
        verificationToken.setStatus(sttTokenNotActived.getStatusId());
        return verificationToken;
    }

    public void updateStatus(Long vftId, Status status) {
        try {
            transactionTemplate.execute(transactionStatus -> {
                entityManager.createNamedQuery("Verification_token.updateStatus")
                        .setParameter("statusId", status.getStatusId())
                        .setParameter("vftId", vftId)
                        .executeUpdate();
                transactionStatus.flush();
                return null;
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}
