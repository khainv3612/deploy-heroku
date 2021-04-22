package com.finacial.service;

import com.finacial.config.Constants;
import com.finacial.model.Account;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.internet.MimeMessage;

@Service("mailSenderService")
public class MailSenderService {
    @Autowired
    private JavaMailSender emailSender;


    public void sendEmailActive(Account account, String token) {
        try {
            MimeMessage message = emailSender.createMimeMessage();
            StringBuilder content = new StringBuilder("");
            boolean multipart = true;
            MimeMessageHelper helper = new MimeMessageHelper(message, multipart, "utf-8");
            content.append("<h3 style='color:red;'>Please active your account by this url</h3>");
            content.append("<a href='http://localhost:8081/active/" + token + "'>Active</a>");
            message.setContent(content.toString(), "text/html");
            helper.setTo(account.getEmail());

            helper.setSubject(Constants.subjectEmailVerify);
            this.emailSender.send(message);
        } catch (Exception uaeEx) {
            uaeEx.printStackTrace();
        }
    }
}
