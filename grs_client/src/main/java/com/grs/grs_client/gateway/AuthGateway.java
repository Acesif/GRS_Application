package com.grs.grs_client.gateway;

import com.grs.grs_client.enums.GRSUserType;
import com.grs.grs_client.enums.OISFUserType;
import com.grs.grs_client.enums.UserType;
import com.grs.grs_client.model.*;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class AuthGateway extends BaseRestTemplate{

    public UserDetails login(String userName, String password) {

//        String url = "http://localhost:8088/grs_security/auth/login";
        String url = getUrl() + "grs_security/auth/login";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
//        headers.add("Authorization", "Bearer " + getToken());

        LoginRequest loginRequest = LoginRequest.builder()
                .username(userName)
                .password(password)
                .build();

        HttpEntity<LoginRequest> entity = new HttpEntity<>(loginRequest, headers);

//        ResponseEntity<UserDetails> response = restTemplate.exchange(url,
//                HttpMethod.GET, entity, new ParameterizedTypeReference<UserDetails>() {
//                });
//        return response.getBody();

        ResponseEntity<LoginResponse> response = restTemplate.exchange(url,
                HttpMethod.POST, entity, new ParameterizedTypeReference<LoginResponse>() {
                });

        UserDetails userDetails = new UserDetails();
        UserInformation userInformation = response.getBody().getUserInformation();
        OfficeInformation officeInfo = response.getBody().getUserInformation().getOfficeInformation();

        userDetails.setId(userInformation.getUserId());
        userDetails.setUsername(userInformation.getUsername());
        userDetails.setName(userInformation.getUsername());
        userDetails.setUserType(userInformation.getUserType());
        userDetails.setGrsUserType(userInformation.getGrsUserType());
        userDetails.setOisfUserType(userInformation.getOisfUserType());
        OfficeInformation officeInformation = new OfficeInformation();
//        officeInformation.setOfficeId(officeInfo.getOfficeId());
//        officeInformation.setName(officeInfo.getName());
//        officeInformation.setDesignation(officeInfo.getDesignation());
        userDetails.setOfficeInformation(officeInformation);
        userDetails.setPermissions(response.getBody().getAuthorities());
        userDetails.setIsAppealOfficer(userInformation.getIsAppealOfficer());
        userDetails.setIsCellGRO(userInformation.getIsCellGRO());
        userDetails.setIsCentralDashboardUser(userInformation.getIsCentralDashboardUser());
        userDetails.setIsOfficeAdmin(userInformation.getIsOfficeAdmin());

        return userDetails;

    }

    // dummy //
//    public UserDetails login(String userName) {
//        UserDetails userDetails = new UserDetails();
//
//        userDetails.setId(1L);
//        userDetails.setUsername("Anwar");
//        userDetails.setName("Anwar");
//        userDetails.setUserType(UserType.COMPLAINANT);
//        userDetails.setGrsUserType(GRSUserType.OTHERS_COMPLAINANT);
//        userDetails.setOisfUserType(OISFUserType.GRO);
//        OfficeInformation officeInformation = new OfficeInformation();
//        officeInformation.setOfficeId(1L);
//        officeInformation.setName("Head Office");
//        officeInformation.setDesignation("Hulala");
//        userDetails.setOfficeInformation(officeInformation);

//        List<String> permissions = new ArrayList<>();
//        permissions.add("VIEW_PUBLIC_GRIEVANCES");
//        permissions.add("DO_APPEAL");
//        permissions.add("ADD_PUBLIC_GRIEVANCES");
//
//        userDetails.setPermissions(permissions);
//        userDetails.setIsAppealOfficer(false);
//        userDetails.setIsCellGRO(false);
//        userDetails.setIsCentralDashboardUser(true);
//        userDetails.setIsOfficeAdmin(false);
//
//        return userDetails;
//    }

}
