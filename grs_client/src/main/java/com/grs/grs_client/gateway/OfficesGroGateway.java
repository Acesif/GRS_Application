package com.grs.grs_client.gateway;

import com.grs.grs_client.model.OfficesGRO;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class OfficesGroGateway  extends BaseRestTemplate{

    String GRS_CORE_CONTEXT_PATH = "/grs_server";

    public List<OfficesGRO> getAncestors(Long officeId){
        String url = getUrl() + GRS_CORE_CONTEXT_PATH + "/api/officegro/getAncestors/"+officeId;
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

    public List<OfficesGRO> findActiveOffices(){
        String url = getUrl() + GRS_CORE_CONTEXT_PATH + "/api/officegro/findActiveOffices";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Authorization", "Bearer " + getToken());
        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<List<OfficesGRO>> response = restTemplate.exchange(url,
                HttpMethod.GET, entity, new ParameterizedTypeReference<List<OfficesGRO>>() {
                });
        return response.getBody();
    }

    public List<Long> findAllOffficeIds() {
        List<OfficesGRO> officeGros = this.findActiveOffices();
        return officeGros.stream()
                .map(OfficesGRO::getOfficeId)
                .sorted()
                .collect(Collectors.toList());
    }

    public List<OfficesGRO> findAll(){
        String url = getUrl() + GRS_CORE_CONTEXT_PATH + "/api/officegro/findAll";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<List<OfficesGRO>> response = restTemplate.exchange(url,
                HttpMethod.GET, entity, new ParameterizedTypeReference<List<OfficesGRO>>() {
                });
        return response.getBody();
    }

}
