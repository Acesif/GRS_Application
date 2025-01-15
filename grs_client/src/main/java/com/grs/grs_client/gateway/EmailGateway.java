package com.grs.grs_client.gateway;

import com.grs.grs_client.model.EmailTemplateDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class EmailGateway extends BaseRestTemplate{

    String GRS_CORE_CONTEXT_PATH = "/grs_server";

    public EmailTemplateDTO getEmailTemplate(Long id) {
        String url = getUrl() + GRS_CORE_CONTEXT_PATH + "/api/email/emailTempleteById/" + id;
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<EmailTemplateDTO> response = restTemplate.exchange(url,
                HttpMethod.GET, entity, new ParameterizedTypeReference<EmailTemplateDTO>() {
                });
        return response.getBody();
    }
}
