package com.finacial.controller;

import com.finacial.config.Constants;
import com.finacial.dto.MessageResponse;
import com.finacial.security.service.TokenVerifyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletRequest;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class AccountController {
    @Autowired
    TokenVerifyService tokenVerifyService;

    @GetMapping("/active/**")
    public RedirectView activeAccount(HttpServletRequest request) {
        String token = request.getRequestURI().split(request.getContextPath() + "/active/")[1];
        MessageResponse messageResponse = tokenVerifyService.VerifyToken(token);
        RedirectView redirectView = new RedirectView();
        switch (messageResponse.getMessage()) {
            case Constants
                    .activeSuccess: {
                redirectView.setUrl("http://localhost:4200/login");
                break;
            }
            case Constants
                    .activeExpired: {
                redirectView.setUrl("http://localhost:4200/active_expired");
                break;
            }
            case Constants
                    .activeUnSuccess: {
                redirectView.setUrl("http://localhost:4200/error_page");
                break;
            }
            default: {
                break;
            }
        }
        return redirectView;
    }

}
