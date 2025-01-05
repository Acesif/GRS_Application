package com.grs.grs_client.gateway;

import com.grs.grs_client.model.EmailTemplate;
import com.grs.grs_client.model.SmsTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ShortMessageGateway extends BaseRestTemplate{

    String GRS_CORE_CONTEXT_PATH = "/grs_server";

    public SmsTemplate getSmsTemplate(Long id) {
        String url = getUrl() + GRS_CORE_CONTEXT_PATH + "/api/shortMessage/smsTemplateID/" + id;
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<SmsTemplate> response = restTemplate.exchange(url,
                HttpMethod.GET, entity, new ParameterizedTypeReference<SmsTemplate>() {
                });
        return response.getBody();
    }
}
