package com.grs.grs_client.gateway;

import com.grs.grs_client.model.Education;
import com.grs.grs_client.model.SpProgram;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;

@Service
public class EducationGateway extends BaseRestTemplate{

    String GRS_CORE_CONTEXT_PATH = "/grs_server";

    public Education getEducation(Long id) {
        String url = getUrl() + GRS_CORE_CONTEXT_PATH + "/api/education/educationById/"+ id;
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<Education> response = restTemplate.exchange(url,
                HttpMethod.GET, entity, new ParameterizedTypeReference<Education>() {
                });
        return response.getBody();
    }
}
