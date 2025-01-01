package com.grs.grs_client.gateway;

import com.grs.grs_client.model.SafetyNetProgram;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SafetyNetProgramGateway extends BaseRestTemplate{

    String GRS_CORE_CONTEXT_PATH = "/grs_server";

    public List<SafetyNetProgram> getSafetyNetPrograms() {
        String url = getUrl() + GRS_CORE_CONTEXT_PATH + "/api/citizenCharter/getSafetyNetPrograms";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<List<SafetyNetProgram>> response = restTemplate.exchange(url,
                HttpMethod.GET, entity, new ParameterizedTypeReference<List<SafetyNetProgram>>() {
                });
        return response.getBody();
    }
}
