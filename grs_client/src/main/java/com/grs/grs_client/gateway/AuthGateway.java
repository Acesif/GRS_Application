package com.grs.grs_client.gateway;

import com.grs.grs_client.enums.GRSUserType;
import com.grs.grs_client.enums.OISFUserType;
import com.grs.grs_client.enums.UserType;
import com.grs.grs_client.model.OfficeInformation;
import com.grs.grs_client.model.UserDetails;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class AuthGateway extends BaseRestTemplate{

//    public UserDetails login(String userName) {
//        String url = getUrl() + "/api/login";
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.APPLICATION_JSON);
//        headers.add("Authorization", "Bearer " + getToken());
//        HttpEntity<String> entity = new HttpEntity<>(headers);
//
//        ResponseEntity<UserDetails> response = restTemplate.exchange(url,
//                HttpMethod.GET, entity, new ParameterizedTypeReference<UserDetails>() {
//                });
//        return response.getBody();
//    }

    // dummy //
    public UserDetails login(String userName) {
        UserDetails userDetails = new UserDetails();

        userDetails.setId(1L);
        userDetails.setUsername("Anwar");
        userDetails.setName("Anwar");
        userDetails.setUserType(UserType.COMPLAINANT);
        userDetails.setGrsUserType(GRSUserType.OTHERS_COMPLAINANT);
        userDetails.setOisfUserType(OISFUserType.GRO);
        OfficeInformation officeInformation = new OfficeInformation();
        officeInformation.setOfficeId(1L);
        officeInformation.setName("Head Office");
        officeInformation.setDesignation("Hulala");
        userDetails.setOfficeInformation(officeInformation);

        List<String> permissions = new ArrayList<>();
        permissions.add("ADMIN");
        permissions.add("OPERATOR");

        userDetails.setPermissions(permissions);
        userDetails.setIsAppealOfficer(false);
        userDetails.setIsCellGRO(false);
        userDetails.setIsCentralDashboardUser(true);
        userDetails.setIsOfficeAdmin(false);

        return userDetails;
    }

}
