package com.grs.grs_client.gateway;

import com.grs.grs_client.model.Blacklist;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ComplainantGateway extends BaseRestTemplate{
    public boolean isBlacklistedUser(Long userId){
        String url = getUrl() + "/api/complainant/isBlacklistedUser/"+userId;
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Authorization", "Bearer " + getToken());
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
}
