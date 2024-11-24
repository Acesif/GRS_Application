package com.grs.grs_client.gateway;

import com.grs.grs_client.model.UserDetails;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;

@Service
public class AuthGateway extends BaseRestTemplate{

    public UserDetails login(String userName) {
        String url = getUrl() + "/api/login";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Authorization", "Bearer " + getToken());
        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<UserDetails> response = restTemplate.exchange(url,
                HttpMethod.GET, entity, new ParameterizedTypeReference<UserDetails>() {
                });
        return response.getBody();
    }

}
