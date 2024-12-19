package com.grs.grs_client.gateway;

import com.grs.grs_client.model.Blacklist;
import com.grs.grs_client.model.Complainant;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ComplainantGateway extends BaseRestTemplate{
    public boolean isBlacklistedUser(Long userId){
        String url = "http://localhost:8088/grs_server/complainantService/isBlacklistedUserByComplainantId/"+userId;
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
//        headers.add("Authorization", "Bearer " + getToken());
        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<Boolean> response = restTemplate.exchange(url,
                HttpMethod.GET, entity, new ParameterizedTypeReference<Boolean>() {
                });
        return response.getBody();
    }

    public Boolean isBlacklistedUserByComplainantId(Long complainantId) {
        List<Blacklist> blacklists = this.findBlacklistByComplainantId(complainantId);
        if (blacklists.size() == 0){
            return false;
        }
        Boolean flag = false;
        for (Blacklist blacklist : blacklists){
            if(blacklist.getBlacklisted()){
                flag = true;
                break;
            }
        }
        return flag;
    }

    public List<Blacklist> findBlacklistByComplainantId(Long complainantId){
        String url = getUrl() + "/api/blacklist/findByComplainantId/"+complainantId;
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Authorization", "Bearer " + getToken());
        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<List<Blacklist>> response = restTemplate.exchange(url,
                HttpMethod.GET, entity, new ParameterizedTypeReference<List<Blacklist>>() {
                });
        return response.getBody();
    }

    public List<Long> findBlacklistedOffices(Long complainantId) {
        String url = getUrl() + "/api/blacklist/findBlacklistedOffices/"+complainantId;
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Authorization", "Bearer " + getToken());
        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<List<Long>> response = restTemplate.exchange(url,
                HttpMethod.GET, entity, new ParameterizedTypeReference<List<Long>>() {
                });
        return response.getBody();
    }

    public Complainant getComplainantByUserId(Long userId) {
        String url = getUrl() + "/api/complainant/getComplainantByUserId/"+userId;
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Authorization", "Bearer " + getToken());
        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<Complainant> response = restTemplate.exchange(url,
                HttpMethod.GET, entity, new ParameterizedTypeReference<Complainant>() {
                });
        return response.getBody();
    }
}
