package com.grs.grs_client.gateway;

import com.grs.grs_client.model.GrsUser;
import com.grs.grs_client.model.OfficesGRO;
import com.grs.grs_client.model.SuperAdmin;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;

@Service
public class GrsUsergateway extends BaseRestTemplate{

    String GRS_CORE_CONTEXT_PATH = "/grs_server";

    public GrsUser convertToSuperAdminDTO(SuperAdmin superAdmin) {
        String url = getUrl() + GRS_CORE_CONTEXT_PATH + "/api/grsUsers/getGrsUserInfo";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<SuperAdmin> entity = new HttpEntity<>(superAdmin, headers);

        ResponseEntity<GrsUser> response = restTemplate.exchange(
                url,
                HttpMethod.POST,
                entity,
                new ParameterizedTypeReference<GrsUser>() {
                }
        );

        return response.getBody();
    }


}
