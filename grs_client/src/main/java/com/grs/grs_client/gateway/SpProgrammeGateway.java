package com.grs.grs_client.gateway;

import com.grs.grs_client.model.SpProgram;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;

@Service
public class SpProgrammeGateway extends BaseRestTemplate{

    String GRS_CORE_CONTEXT_PATH = "/grs_server";

    public SpProgram getSpProgramme(Integer id) {
        String url = getUrl() + GRS_CORE_CONTEXT_PATH + "/api/SpProgram/getSpProgramme/"+ id;
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
//        headers.add("Authorization", "Bearer " + getToken());
        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<SpProgram> response = restTemplate.exchange(url,
                HttpMethod.GET, entity, new ParameterizedTypeReference<SpProgram>() {
                });
        return response.getBody();
    }
}
