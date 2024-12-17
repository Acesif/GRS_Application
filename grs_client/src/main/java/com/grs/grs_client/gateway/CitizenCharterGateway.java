package com.grs.grs_client.gateway;

import com.grs.grs_client.enums.ServiceType;
import com.grs.grs_client.model.*;
import com.grs.grs_client.utils.BanglaConverter;
import com.grs.grs_client.utils.CookieUtil;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

@Service
public class CitizenCharterGateway extends BaseRestTemplate{

    String GRS_CORE_CONTEXT_PATH = "/grs_server";

    public CitizenCharter getCitizenCharterByOfficeCitizenCharterId(Long officeCitizenCharterId) {
        String url = getUrl() + GRS_CORE_CONTEXT_PATH + "/api/citizenCharter/getCitizenCharterByOfficeCitizenCharterId/"+officeCitizenCharterId;
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Authorization", "Bearer " + getToken());
        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<CitizenCharter> response = restTemplate.exchange(url,
                HttpMethod.GET, entity, new ParameterizedTypeReference<CitizenCharter>() {
                });
        return response.getBody();
    }

    public List<ServicePair> getDefaultAllowedServiceTypes(HttpServletRequest request) {
        String languageCode = CookieUtil.getValue(request, "lang");
        String publicService;
        String officeService;
        String staffService;
        if (languageCode == null || languageCode.equals("fr")) {
            publicService = BanglaConverter.convertServiceTypeToBangla(ServiceType.NAGORIK);
            officeService = BanglaConverter.convertServiceTypeToBangla(ServiceType.DAPTORIK);
            staffService = BanglaConverter.convertServiceTypeToBangla(ServiceType.STAFF);
        } else {
            publicService = "Public";
            officeService = "Official";
            staffService = "Stuff";
        }
        List<ServicePair> servicePairs = new ArrayList<>();
        servicePairs.add(new ServicePair(ServiceType.NAGORIK, publicService));
        servicePairs.add(new ServicePair(ServiceType.STAFF, staffService));
        servicePairs.add(new ServicePair(ServiceType.DAPTORIK, officeService));
        return servicePairs;
    }

}
