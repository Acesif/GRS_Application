package com.grs.grs_client.gateway;

import com.grs.grs_client.model.PermissionsToUsers;
import com.grs.grs_client.model.SpProgram;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class PermissionsToUsersGateway extends BaseRestTemplate{

    String GRS_CORE_CONTEXT_PATH = "/grs_server";

    public List<PermissionsToUsers> findByOisfUserId(Long userId) {
        String url = getUrl() + GRS_CORE_CONTEXT_PATH + "/api/permissions-to-users/findByOisfUserId/"+ userId;
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
//        headers.add("Authorization", "Bearer " + getToken());
        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<List<PermissionsToUsers>> response = restTemplate.exchange(url,
                HttpMethod.GET, entity, new ParameterizedTypeReference<List<PermissionsToUsers>>() {
                });
        return response.getBody();
    }
}
