package com.grs.grs_client.gateway;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.ser.BeanSerializerModifier;
import com.grs.grs_client.enums.GrievanceCurrentStatus;
import com.grs.grs_client.enums.OISFUserType;
import com.grs.grs_client.enums.RoleType;
import com.grs.grs_client.enums.UserType;
import com.grs.grs_client.model.*;
import com.grs.grs_client.utils.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@Slf4j
public class GrievanceForwardingGateway extends BaseRestTemplate {

    @Autowired
    private OfficesGroGateway officesGroService;

    String GRS_CORE_CONTEXT_PATH = "/grs_server";

    public Boolean getComplaintRetakeFlag(Long grievanceId) {
        String url = getUrl() + GRS_CORE_CONTEXT_PATH +  "/api/getComplaintRetakeFlag/" + grievanceId;
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Authorization", "Bearer " + getToken());
        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<Boolean> response = restTemplate.exchange(url,
                HttpMethod.GET, entity, new ParameterizedTypeReference<Boolean>() {
                });
        return response.getBody();
    }

    public void updateForwardSeenStatus(UserInformation userInformation, Long id){
        String url = getUrl() + GRS_CORE_CONTEXT_PATH +  "/api/updateForwardSeenStatus/" + id;
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Authorization", "Bearer " + getToken());
        ObjectMapper objectMapper = new ObjectMapper();
        String body = "";
        try {
            body = objectMapper.writeValueAsString(userInformation);
        }catch (Exception ex){
            log.error("",ex);
        }
        HttpEntity<String> entity = new HttpEntity<>(body, headers);

        restTemplate.exchange(url,
                HttpMethod.POST, entity, Void.class);
    }

    public List<GrievanceForwarding> getAllUserRelatedForwardings(Grievance grievance, UserInformation userInformation){
        String url = getUrl() + GRS_CORE_CONTEXT_PATH +  "/api/getAllUserRelatedForwardings/" + grievance.getId();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Authorization", "Bearer " + getToken());
        ObjectMapper objectMapper = new ObjectMapper();
        String body = "";
        try {
            body = objectMapper.writeValueAsString(userInformation);
        }catch (Exception ex){
            log.error("",ex);
        }
        HttpEntity<String> entity = new HttpEntity<>(body, headers);

        ResponseEntity<List<GrievanceForwarding>> response = restTemplate.exchange(url,
                HttpMethod.POST, entity, new ParameterizedTypeReference<List<GrievanceForwarding>>() {});

        return response.getBody();
    }

    public GrievanceForwarding getLastForwardingForGivenGrievance(Long grievanceId){
        String url = getUrl() + GRS_CORE_CONTEXT_PATH +  "/api/grievanceforwarding/getLastForwadingForGivenGrievance/" + grievanceId;
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Authorization", "Bearer " + getToken());
        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<GrievanceForwarding> response = restTemplate.exchange(url,
                HttpMethod.GET, entity, new ParameterizedTypeReference<GrievanceForwarding>() {
                });
        return response.getBody();
    }

    public List<GrievanceForwarding> findByGrievanceAndIsCurrent(Long grievanceId){
        String url = getUrl() + GRS_CORE_CONTEXT_PATH + "/api/grievanceforwarding/findByGrievanceAndIsCurrent/" + grievanceId;
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Authorization", "Bearer " + getToken());
        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<List<GrievanceForwarding>> response = restTemplate.exchange(url,
                HttpMethod.GET, entity, new ParameterizedTypeReference<List<GrievanceForwarding>>() {
                });
        return response.getBody();
    }

    public List<GrievanceForwarding> getAllComplaintMovementByGrievance(Long grievanceId) {
        String url = getUrl() + GRS_CORE_CONTEXT_PATH + "/api/grievanceforwarding/getAllComplaintMovementByGrievance/" + grievanceId;
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Authorization", "Bearer " + getToken());
        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<List<GrievanceForwarding>> response = restTemplate.exchange(url,
                HttpMethod.GET, entity, new ParameterizedTypeReference<List<GrievanceForwarding>>() {
                });
        return response.getBody();
    }

    public GrievanceForwarding getLastClosedOrRejectedForwarding(Long grievanceId) {
        String url = getUrl() + GRS_CORE_CONTEXT_PATH + "/api/grievanceforwarding/getLastClosedOrRejectedForwarding/" + grievanceId;
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Authorization", "Bearer " + getToken());
        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<GrievanceForwarding> response = restTemplate.exchange(url,
                HttpMethod.GET, entity, new ParameterizedTypeReference<GrievanceForwarding>() {
                });
        return response.getBody();
    }

    public Boolean soAppealActivationFlag(Grievance grievance) {
        if(grievance == null){
            return false;
        }
        if (grievance.getOfficeId() == 0) {
            return false;
        }
        if (grievance.getGrievanceCurrentStatus().toString().contentEquals("CLOSED_ACCUSATION_PROVED")) {
            GrievanceForwarding grievanceForwarding = this.getLastClosedOrRejectedForwarding(grievance.getId());
            Date closingDate = grievanceForwarding.getCreatedAt();
            Date today = new Date();
            Long days = TimeUnit.DAYS.convert((today.getTime() - closingDate.getTime()), TimeUnit.MILLISECONDS);
            if (days <= 31) {
                return true;
            }
        }
        return false;
    }

    public List<GrievanceForwarding> findByGrievanceIdAndAssignedRole(Long grievanceId, String roleType){
        String url = getUrl() + GRS_CORE_CONTEXT_PATH + "/api/grievanceforwarding/findByGrievanceIdAndAssignedRole/" + grievanceId + "/" + roleType;
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
//        headers.add("Authorization", "Bearer " + getToken());
        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<List<GrievanceForwarding>> response = restTemplate.exchange(url,
                HttpMethod.GET, entity, new ParameterizedTypeReference<List<GrievanceForwarding>>() {
                });
        return response.getBody();
    }

    public GrievanceForwarding getByActionAndToOfficeIdAndToOfficeUnitOrganogramIdAndGrievance(
            Long grievanceId, Long officeId, Long officeOrganogramId, String action
    ){
        String url = getUrl() + GRS_CORE_CONTEXT_PATH + "/api/grievanceforwarding/getByActionAndToOfficeIdAndToOfficeUnitOrganogramIdAndGrievance/" + grievanceId + "/" + officeId + "/" + officeOrganogramId + "/" + action;
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Authorization", "Bearer " + getToken());
        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<GrievanceForwarding> response = restTemplate.exchange(url,
                HttpMethod.GET, entity, new ParameterizedTypeReference<GrievanceForwarding>() {
                });
        return response.getBody();
    }

    public int getCountOfComplaintMovementAttachedFiles(UserInformation userInformation, Grievance grievance){
        Long grievanceId = grievance.getId();
        String url = getUrl() + GRS_CORE_CONTEXT_PATH +  "/api/getCountOfComplaintMovementAttachedFiles/" + grievanceId;
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Authorization", "Bearer " + getToken());
        ObjectMapper objectMapper = new ObjectMapper();
        String body = "";
        try {
            body = objectMapper.writeValueAsString(userInformation);
        } catch (Exception ex){
            log.error("",ex);
        }
        HttpEntity<String> entity = new HttpEntity<>(body, headers);

        ResponseEntity<Integer> response = restTemplate.exchange(url,
                HttpMethod.POST, entity, new ParameterizedTypeReference<Integer>() {});

        return response.getBody();
    }

    public List<GrievanceForwarding> findByGrievanceAndActionLikeOrderByIdDesc(Long grievanceId, String action){
        String url = getUrl() + GRS_CORE_CONTEXT_PATH + "/api/grievanceforwarding/findByGrievanceAndActionLikeOrderByIdDesc/" + grievanceId + "/" + action;
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Authorization", "Bearer " + getToken());
        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<List<GrievanceForwarding>> response = restTemplate.exchange(url,
                HttpMethod.GET, entity, new ParameterizedTypeReference<List<GrievanceForwarding>>() {
                });
        return response.getBody();
    }

    public GrievanceForwarding findByGrievanceAndActionLikeAndCurrentStatusLike(Long grievanceId, String action, String status){
        String url = getUrl() + GRS_CORE_CONTEXT_PATH + "/api/grievanceforwarding/findByGrievanceAndActionLikeAndCurrentStatusLike/" + grievanceId + "/" + action + "/" + status;
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Authorization", "Bearer " + getToken());
        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<GrievanceForwarding> response = restTemplate.exchange(url,
                HttpMethod.GET, entity, new ParameterizedTypeReference<GrievanceForwarding>() {
                });
        return response.getBody();
    }

    public GrievanceForwarding findByGrievanceAndActionLikeAndCurrentStatusNotLike(long grievanceId, String action, String status){
        String url = getUrl() + GRS_CORE_CONTEXT_PATH + "/api/grievanceforwarding/findByGrievanceAndActionLikeAndCurrentStatusNotLike/" + grievanceId + "/" + action + "/" + status;
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Authorization", "Bearer " + getToken());
        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<GrievanceForwarding> response = restTemplate.exchange(url,
                HttpMethod.GET, entity, new ParameterizedTypeReference<GrievanceForwarding>() {
                });
        return response.getBody();
    }

    public List<GrievanceForwarding> getAllRelatedComplaintMovementsBetweenDates(
            Long grievanceId, Long officeId, List<Long> officeUnitOrganogramId, String action, Date start, Date finish
    ){
        String url = getUrl() + GRS_CORE_CONTEXT_PATH + "/api/grievanceforwarding/getAllRelatedComplaintMovementsBetweenDates";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Authorization", "Bearer " + getToken());
        url = UriComponentsBuilder.fromHttpUrl(url)
                .queryParam("grievanceId", grievanceId)
                .queryParam("officeId", officeId)
                .queryParam("action", action)
                .queryParam("start", start)
                .queryParam("finish", finish)
                .queryParam("officeUnitOrganogramId", officeUnitOrganogramId)
                .toUriString();

        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<List<GrievanceForwarding>> response = restTemplate.exchange(url,
                HttpMethod.GET, entity, new ParameterizedTypeReference<List<GrievanceForwarding>>() {
                });
        return response.getBody();
    }

    public List<GrievanceForwarding> getAllRelatedComplaintMovements(
            Long grievanceId,
            Long officeId,
            List<Long> officeUnitOrganogramId,
            String action
    ) {
        String url = getUrl() + GRS_CORE_CONTEXT_PATH + "/api/grievanceforwarding/getAllRelatedComplaintMovements";

        // Prepare headers
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + getToken());
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        // Prepare form data
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("grievanceId", grievanceId.toString());
        formData.add("officeId", officeId.toString());

        // For the list, add each value
        if (officeUnitOrganogramId != null) {
            for (Long orgId : officeUnitOrganogramId) {
                formData.add("officeUnitOrganogramId", orgId.toString());
            }
        }

        formData.add("action", action);

        // Wrap into an HttpEntity
        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(formData, headers);

        // Execute POST with form data
        ResponseEntity<List<GrievanceForwarding>> response = restTemplate.exchange(
                url,
                HttpMethod.POST,
                entity,
                new ParameterizedTypeReference<List<GrievanceForwarding>>() {}
        );

        return response.getBody();
    }


    public GrievanceForwarding getLastActiveGrievanceForwardingOfCurrentUser(Long grievanceId, Long userOfficeId, Long userOrganogramId){
        String url = getUrl() + GRS_CORE_CONTEXT_PATH + "/api/grievanceforwarding/getLastActiveGrievanceForwardingOfCurrentUser/" + grievanceId + "/" + userOfficeId + "/" + userOrganogramId;
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Authorization", "Bearer " + getToken());
        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<GrievanceForwarding> response = restTemplate.exchange(url,
                HttpMethod.GET, entity, new ParameterizedTypeReference<GrievanceForwarding>() {
                });
        return response.getBody();
    }

    public GrievanceForwarding updateGrievanceForwarding(GrievanceForwarding grievanceForwarding){
        String url = getUrl() + GRS_CORE_CONTEXT_PATH + "/api/grievanceforwarding/update";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        ObjectMapper objectMapper = new ObjectMapper();
        String body = "";
        try {
            body = objectMapper.writeValueAsString(grievanceForwarding);
        }catch (Exception ex){
            log.error("",ex);
        }
        HttpEntity<String> entity = new HttpEntity<>(body, headers);

        ResponseEntity<GrievanceForwarding> response = restTemplate.exchange(url,
                HttpMethod.POST, entity, new ParameterizedTypeReference<GrievanceForwarding>() {
                });
        return response.getBody();
    }

    public GrievanceForwarding saveGrievanceForwardingHistory(GrievanceForwarding grievanceForwarding){
        String url = getUrl() + GRS_CORE_CONTEXT_PATH + "/api/grievanceforwarding/saveGrievanceForwardingHistory";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Authorization", "Bearer " + getToken());
        ObjectMapper objectMapper = new ObjectMapper();
        String body = "";
        try {
            body = objectMapper.writeValueAsString(grievanceForwarding);
        }catch (Exception ex){
            log.error("",ex);
        }
        HttpEntity<String> entity = new HttpEntity<>(body, headers);

        ResponseEntity<GrievanceForwarding> response = restTemplate.exchange(url,
                HttpMethod.POST, entity, new ParameterizedTypeReference<GrievanceForwarding>() {
                });
        return response.getBody();
    }

    public List<FileDerivedDTO> getFiles(GrievanceForwarding grievanceForwarding) {
        List<MovementAttachedFile> attachedFiles = grievanceForwarding.getAttachedFiles();
        List<FileDerivedDTO> files = null;

        if (attachedFiles.size() > 0) {
            files = attachedFiles.stream().map(attachedFile -> {
                StringBuilder stringBuilderFirst = StringUtil.replaceAll(
                        new StringBuilder(attachedFile.getFilePath().substring(1)),
                        "uploadedFiles", "api/file/upload");
                String link = StringUtil.replaceAll(stringBuilderFirst, "\\", "/").append("/").toString();
                return FileDerivedDTO.builder()
                        .url(link)
                        .name(attachedFile.getFileName())
                        .build();
            }).collect(Collectors.toList());
        }
        return files;
    }

    private List<Long> getCurrentInvestigationCommitteeMembersOrganograms(List<GrievanceForwarding> grievanceForwardings, GrievanceCurrentStatus currentStatus) {
        List<Long> organogramIds = new ArrayList<>();
        List<GrievanceForwarding> refinedForwardings = grievanceForwardings.stream().filter(grievanceForwarding -> grievanceForwarding.getCurrentStatus().equals(currentStatus)).collect(Collectors.toList());
        Long currentId = refinedForwardings.get(0).getId();
        organogramIds.add(refinedForwardings.get(0).getToOfficeUnitOrganogramId());
        refinedForwardings.remove(0);
        for (GrievanceForwarding grievanceForwarding : refinedForwardings) {
            if (Math.abs(grievanceForwarding.getId() - currentId) > 1) {
                break;
            }
            currentId = grievanceForwarding.getId();
            organogramIds.add(grievanceForwarding.getToOfficeUnitOrganogramId());
        }
        return organogramIds;
    }
}
