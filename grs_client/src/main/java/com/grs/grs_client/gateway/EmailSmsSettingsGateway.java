package com.grs.grs_client.gateway;

import com.grs.grs_client.model.EmailSmsSettings;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;

import java.util.WeakHashMap;

@Service
public class EmailSmsSettingsGateway extends BaseRestTemplate{

    String GRS_CORE_CONTEXT_PATH = "/grs_server";

    public EmailSmsSettings getEmailSmsSettings(Long id) {
        String url = getUrl() + GRS_CORE_CONTEXT_PATH + "/emailSmsSettingWithId/" + id;
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<EmailSmsSettings> response = restTemplate.exchange(url,
                HttpMethod.GET, entity, new ParameterizedTypeReference<EmailSmsSettings>() {
                });
        return response.getBody();
    }

    public WeakHashMap<String, String> convertToGrsRoleList() {
        String url = getUrl() + GRS_CORE_CONTEXT_PATH + "/api/emailSmsSetting/convertToGrsRoleList";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<WeakHashMap<String, String>> response = restTemplate.exchange(url,
                HttpMethod.GET, entity, new ParameterizedTypeReference<WeakHashMap<String, String>>() {
                });
        return response.getBody();
    }

    public WeakHashMap<String, String> convertToGrievanceStatusList() {
        String url = getUrl() + GRS_CORE_CONTEXT_PATH + "/api/emailSmsSetting/convertToGrievanceStatusList";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<WeakHashMap<String, String>> response = restTemplate.exchange(url,
                HttpMethod.GET, entity, new ParameterizedTypeReference<WeakHashMap<String, String>>() {
                });
        return response.getBody();
    }

    public WeakHashMap<String, String> convertToActionList() {
        String url = getUrl() + GRS_CORE_CONTEXT_PATH + "/api/emailSmsSetting/convertToActionList";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<WeakHashMap<String, String>> response = restTemplate.exchange(url,
                HttpMethod.GET, entity, new ParameterizedTypeReference<WeakHashMap<String, String>>() {
                });
        return response.getBody();
    }
}
