package com.grs.grs_client.gateway;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.grs.grs_client.enums.GRSUserType;
import com.grs.grs_client.enums.OISFUserType;
import com.grs.grs_client.enums.UserType;
import com.grs.grs_client.model.*;
import com.grs.grs_client.utils.Utility;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class AuthGateway extends BaseRestTemplate{

    String GRS_AUTH_CONTEXT_PATH = "/grs_security";

    public LoginResponse login(LoginRequest loginRequest) {
        String url = getUrl() + GRS_AUTH_CONTEXT_PATH + "/auth/login";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        ObjectMapper objectMapper = new ObjectMapper();
        String body = "";
        try {
            body = objectMapper.writeValueAsString(loginRequest);
        }catch (Exception ex){
            log.error("",ex);
        }
        HttpEntity<String> entity = new HttpEntity<>(body, headers);
        ResponseEntity<LoginResponse> response = restTemplate.exchange(url,
                HttpMethod.POST, entity, new ParameterizedTypeReference<LoginResponse>() {
                });
        return response.getBody();
    }

    public LoginResponse adminLogin(String data) {
        String url = getUrl() + GRS_AUTH_CONTEXT_PATH + "/auth/admin-login?data="+data;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<LoginResponse> response = restTemplate.exchange(url,
                HttpMethod.POST, entity, new ParameterizedTypeReference<LoginResponse>() {});

        return response.getBody();
    }

    public String superAdminPasswordChange(PasswordChange passwordChange) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserInformation userInformation = Utility.extractUserInformationFromAuthentication(authentication);

        if (!userInformation.getGrsUserType().equals(GRSUserType.SUPER_ADMIN)) {
            return "You are not authorized to change password";
        }

        String url = getUrl() + GRS_AUTH_CONTEXT_PATH + "/auth/superAdmin/passwordChange";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        passwordChange.setUsername(userInformation.getUsername());

        ObjectMapper objectMapper = new ObjectMapper();
        String body = "";
        try {
            body = objectMapper.writeValueAsString(passwordChange);
        }catch (Exception ex){
            log.error("",ex);
        }
        HttpEntity<String> entity = new HttpEntity<>(body, headers);
        ResponseEntity<String> response = restTemplate.exchange(url,
                HttpMethod.POST, entity, new ParameterizedTypeReference<String>() {
                });
        return response.getBody();
    }
}
