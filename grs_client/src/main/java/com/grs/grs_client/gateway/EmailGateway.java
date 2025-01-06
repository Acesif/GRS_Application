package com.grs.grs_client.gateway;

import com.grs.grs_client.model.EmailTemplate;
import com.grs.grs_client.model.OfficeSearchDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class EmailGateway extends BaseRestTemplate{

    String GRS_CORE_CONTEXT_PATH = "/grs_server";

    public EmailTemplate getEmailTemplate(Long id) {
        String url = getUrl() + GRS_CORE_CONTEXT_PATH + "/api/email/emailTempleteById/" + id;
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<EmailTemplate> response = restTemplate.exchange(url,
                HttpMethod.GET, entity, new ParameterizedTypeReference<EmailTemplate>() {
                });
        return response.getBody();
    }
}
