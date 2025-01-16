package com.grs.grs_client.controller;

import com.grs.grs_client.gateway.AuthGateway;
import com.grs.grs_client.model.PasswordChange;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class AuthController {

    private final AuthGateway authService;

    @PostMapping("/auth/superAdmin/passwordChange")
    public String superAdminPasswordChange(@RequestBody PasswordChange passwordChange) {
        return authService.superAdminPasswordChange(passwordChange);
    }
}
