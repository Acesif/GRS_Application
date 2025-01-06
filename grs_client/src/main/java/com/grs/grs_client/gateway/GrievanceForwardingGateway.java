package com.grs.grs_client.gateway;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@Slf4j
public class GrievanceForwardingGateway extends BaseRestTemplate {

    @Autowired
    private OfficesGroGateway officesGroService;

    String GRS_CORE_CONTEXT_PATH = "/grs_server";

    public Boolean getComplaintRetakeFlag(Grievance grievance, UserInformation userInformation) {
        OfficesGRO gro = this.officesGroService.findOfficesGroByOfficeId(grievance.getOfficeId());
        boolean flag = false;
        if(grievance.getGrievanceCurrentStatus().toString().contains("CLOSED") || grievance.getGrievanceCurrentStatus().toString().contains("REJECTED")
                || grievance.getGrievanceCurrentStatus().toString().contains("INV") || grievance.getGrievanceCurrentStatus().toString().contains("REVIEW")
                || grievance.getGrievanceCurrentStatus().toString().contains("CELL") ){
            return false;
        }
        if(gro.getGroOfficeId().equals(userInformation.getOfficeInformation().getOfficeId())
                && gro.getGroOfficeUnitOrganogramId().equals(userInformation.getOfficeInformation().getOfficeUnitOrganogramId())
                && grievance.getCurrentAppealOfficeId() == null){
            List<GrievanceForwarding> grievanceForwardings = this.findByGrievanceAndIsCurrent(grievance.getId());
            for (GrievanceForwarding grievanceForwarding : grievanceForwardings) {
                if (grievanceForwarding.getFromOfficeId().equals(gro.getGroOfficeId())
                        && grievanceForwarding.getFromOfficeUnitOrganogramId().equals(gro.getGroOfficeUnitOrganogramId())
                        && grievanceForwarding.getDeadlineDate() != null
                        && grievanceForwarding.getDeadlineDate().before(new Date())
                        && !grievanceForwarding.getIsCC()) {
                    flag = true;
                    break;
                } else if (!grievanceForwarding.getIsSeen() && grievanceForwarding.getIsCurrent()
                        && grievanceForwarding.getFromOfficeId().equals(gro.getGroOfficeId())
                        && grievanceForwarding.getFromOfficeUnitOrganogramId().equals(gro.getGroOfficeUnitOrganogramId())
                        && !grievanceForwarding.getIsCC()
                        && !grievance.getGrievanceCurrentStatus().equals(GrievanceCurrentStatus.NEW)) {
                    flag = true;
                    break;
                }
            }
        } else if(grievance.getCurrentAppealOfficeId() != null){
            if(grievance.getCurrentAppealOfficeId().equals(userInformation.getOfficeInformation().getOfficeId())
                    && grievance.getCurrentAppealOfficerOfficeUnitOrganogramId().equals(userInformation.getOfficeInformation().getOfficeUnitOrganogramId())
            ){
                List<GrievanceForwarding> grievanceForwardings = this.findByGrievanceAndIsCurrent(grievance.getId());
                for (GrievanceForwarding grievanceForwarding : grievanceForwardings) {
                    if (grievanceForwarding.getFromOfficeId().equals(userInformation.getOfficeInformation().getOfficeId())
                            && grievanceForwarding.getFromOfficeUnitOrganogramId().equals(userInformation.getOfficeInformation().getOfficeUnitOrganogramId())
                            && grievanceForwarding.getDeadlineDate() != null
                            && grievanceForwarding.getDeadlineDate().before(new Date())) {
                        flag = true;
                        break;
                    }
                }
            }
        } else if(grievance.getGrievanceCurrentStatus().name().contains("FORWARDED")){
            GrievanceForwarding grievanceForwarding = this.getLastForwadingForGivenGrievance(grievance.getId());
            if(!grievanceForwarding.getIsSeen() && grievanceForwarding.getIsCurrent() &&
                    grievanceForwarding.getAction().contains("FORWARD") &&
                    grievanceForwarding.getFromOfficeUnitOrganogramId().equals(userInformation.getOfficeInformation().getOfficeUnitOrganogramId()) &&
                    grievanceForwarding.getFromOfficeId().equals(userInformation.getOfficeInformation().getOfficeId())){
                flag = true;
            }
        }
        return flag;
    }

    public GrievanceForwarding getLastForwadingForGivenGrievance(Long grievanceId){
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
        List<GrievanceForwarding> grievanceForwardings;
        List<FileDerivedDTO> files = new ArrayList<>();
        if(userInformation.getUserType().equals(UserType.COMPLAINANT)){
            grievanceForwardings = this.findByGrievanceIdAndAssignedRole(grievance.getId(), RoleType.COMPLAINANT.name());
        } else {
            grievanceForwardings = this.getAllUserRelatedForwardings(grievance, userInformation);
        }
        grievanceForwardings.forEach(
                grievanceForwarding -> {
                    List<FileDerivedDTO> fileDerivedDTOS = getFiles(grievanceForwarding);
                    if(fileDerivedDTOS != null){
                        files.addAll(fileDerivedDTOS);
                    }
                }
        );
        return files.size();
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
            Long grievanceId, Long officeId, List<Long> officeUnitOrganogramId, String action
    ){
        String url = getUrl() + GRS_CORE_CONTEXT_PATH + "/api/grievanceforwarding/getAllRelatedComplaintMovements";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Authorization", "Bearer " + getToken());
        url = UriComponentsBuilder.fromHttpUrl(url)
                .queryParam("grievanceId", grievanceId)
                .queryParam("officeId", officeId)
                .queryParam("action", action)
                .queryParam("officeUnitOrganogramId", officeUnitOrganogramId)
                .toUriString();

        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<List<GrievanceForwarding>> response = restTemplate.exchange(url,
                HttpMethod.GET, entity, new ParameterizedTypeReference<List<GrievanceForwarding>>() {
                });
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

    public List<GrievanceForwarding> getAllUserRelatedForwardings(Grievance grievance, UserInformation userInformation){
        if (userInformation.getOfficeInformation() == null) {
            return new ArrayList<>();
        }
        List<GrievanceForwarding> grievanceForwardings;
        Long officeId = userInformation.getOfficeInformation().getOfficeId();
        Long officeUnitOrganogramId = userInformation.getOfficeInformation().getOfficeUnitOrganogramId();
        GrievanceForwarding committeeEntry = this.getByActionAndToOfficeIdAndToOfficeUnitOrganogramIdAndGrievance(grievance.getId(), officeId, officeUnitOrganogramId, "INVESTIGATION");
        if (committeeEntry != null && (committeeEntry.getIsCommitteeMember() || committeeEntry.getIsCommitteeHead())) {
            List<GrievanceForwarding> committeeForwardings = this.findByGrievanceAndActionLikeOrderByIdDesc(grievance.getId(), "%INVESTIGATION%");
            List<Long> committeeOrganograms = getCurrentInvestigationCommitteeMembersOrganograms(committeeForwardings, committeeEntry.getCurrentStatus());
            GrievanceForwarding reportEntry = committeeEntry.getCurrentStatus().toString().contains("APPEAL") ?
                    this.findByGrievanceAndActionLikeAndCurrentStatusLike(grievance.getId(), "%REPORT%", "%APPEAL%")
                    : this.findByGrievanceAndActionLikeAndCurrentStatusNotLike(grievance.getId(), "%REPORT%", "%APPEAL%");
            grievanceForwardings = this.getAllRelatedComplaintMovementsBetweenDates(
                    grievance.getId(), userInformation.getOfficeInformation().getOfficeId(),
                    new ArrayList<Long>() {{
                        addAll(committeeOrganograms);
                    }},
                    "",
                    committeeEntry.getCreatedAt(),
                    reportEntry == null ? new Date() : reportEntry.getCreatedAt()
            );
            grievanceForwardings.addAll(this.getAllRelatedComplaintMovements(
                    grievance.getId(), officeId, new ArrayList<Long>() {{
                        add(officeUnitOrganogramId);
                    }}, ""
            ));
        } else if (userInformation.getOisfUserType().equals(OISFUserType.HEAD_OF_OFFICE)) {
            OfficesGRO officesGRO = this.officesGroService.findOfficesGroByOfficeId(userInformation.getOfficeInformation().getOfficeId());
            List<Long> orgUnitList = new ArrayList<>();
            if (officesGRO.getGroOfficeUnitOrganogramId() != null) {
                orgUnitList.add(officesGRO.getGroOfficeUnitOrganogramId());
            }
            if (userInformation.getOfficeInformation() != null && userInformation.getOfficeInformation().getOfficeUnitOrganogramId() != null) {
                orgUnitList.add(userInformation.getOfficeInformation().getOfficeUnitOrganogramId());
            }
            grievanceForwardings = this.getAllRelatedComplaintMovements(grievance.getId(),
                    officesGRO.getOfficeId(),orgUnitList,
                    "");
        } else {
            grievanceForwardings = this.getAllRelatedComplaintMovements(
                    grievance.getId(), officeId, new ArrayList<Long>() {{
                        add(officeUnitOrganogramId);
                    }}, ""
            );
        }
        return grievanceForwardings.stream().distinct().collect(Collectors.toList());
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

    public void updateForwardSeenStatus(UserInformation userInformation, Grievance grievance) {
        if (userInformation.getUserType().equals(UserType.COMPLAINANT)) {
            return;
        }
        if (grievance.getGrievanceCurrentStatus().toString().contains("REJECTED") ||
                grievance.getGrievanceCurrentStatus().toString().contains("CLOSED")) {
            return;
        }

        Long userOfficeId = userInformation.getOfficeInformation().getOfficeId();
        Long userOrganogramId = userInformation.getOfficeInformation().getOfficeUnitOrganogramId();

        GrievanceForwarding grievanceForwarding = this.getLastActiveGrievanceForwardingOfCurrentUser(grievance.getId(), userOfficeId, userOrganogramId);
        if (grievanceForwarding == null) {
            return;
        }
        if (grievanceForwarding.getIsSeen()) {
            return;
        }
        Long toOfficeId = grievanceForwarding.getToOfficeId();
        Long toOrganogramId = grievanceForwarding.getToOfficeUnitOrganogramId();

        if (toOfficeId.equals(userOfficeId) && toOrganogramId.equals(userOrganogramId)) {
            if (!grievanceForwarding.getIsSeen()) {
                grievanceForwarding.setIsSeen(true);
                this.updateGrievanceForwarding(grievanceForwarding);
                this.saveGrievanceForwardingHistory(grievanceForwarding);
                return;
            }
        }
    }

}
