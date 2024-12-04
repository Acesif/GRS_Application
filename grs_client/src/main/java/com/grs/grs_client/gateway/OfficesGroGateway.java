package com.grs.grs_client.gateway;

import com.grs.grs_client.model.OfficesGRO;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OfficesGroGateway  extends BaseRestTemplate{
    public List<OfficesGRO> getAncestors(Long officeId){
        String url = getUrl() + "/api/officegro/getAncestors/"+officeId;
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Authorization", "Bearer " + getToken());
        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<List<OfficesGRO>> response = restTemplate.exchange(url,
                HttpMethod.GET, entity, new ParameterizedTypeReference<List<OfficesGRO>>() {
                });
        return response.getBody();
    }

    public OfficesGRO findOfficesGroByOfficeId(Long officeId) {
        String url = getUrl() + "/api/officegro/findOfficesGroByOfficeId/"+officeId;
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Authorization", "Bearer " + getToken());
        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<OfficesGRO> response = restTemplate.exchange(url,
                HttpMethod.GET, entity, new ParameterizedTypeReference<OfficesGRO>() {
                });
        return response.getBody();
    }
}
