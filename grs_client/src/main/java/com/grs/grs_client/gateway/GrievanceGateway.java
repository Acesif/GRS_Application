package com.grs.grs_client.gateway;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.grs.grs_client.enums.GrievanceCurrentStatus;
import com.grs.grs_client.enums.OISFUserType;
import com.grs.grs_client.enums.ServiceType;
import com.grs.grs_client.enums.UserType;
import com.grs.grs_client.model.*;
import com.grs.grs_client.service.MessageService;
import com.grs.grs_client.utils.CalendarUtil;
import com.grs.grs_client.utils.Constant;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.codec.binary.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class GrievanceGateway extends BaseRestTemplate {

    @Autowired
    private MessageService messageService;

    @Autowired
    private OfficesGateway officeService;

    @Autowired
    private CitizenCharterGateway citizenCharterService;

    String GRS_CORE_CONTEXT_PATH = "/grs_server";

    public Grievance findGrievanceById(Long grievanceId) {
        String url = getUrl() + GRS_CORE_CONTEXT_PATH + "/api/grievance/findGrievanceById/" + grievanceId;
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Authorization", "Bearer " + getToken());
        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<Grievance> response = restTemplate.exchange(url,
                HttpMethod.GET, entity, new ParameterizedTypeReference<Grievance>() {
                });
        return response.getBody();
    }



    public Grievance saveGrievance(Grievance grievance, boolean callHistory) {
        String url = getUrl() + GRS_CORE_CONTEXT_PATH + "/api/grievance";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Authorization", "Bearer " + getToken());
        ObjectMapper objectMapper = new ObjectMapper();
        String body = "";
        try {
            body = objectMapper.writeValueAsString(grievance);
        }catch (Exception ex){
            log.error("",ex);
        }
        HttpEntity<String> entity = new HttpEntity<>(body, headers);


        ResponseEntity<Grievance> response = restTemplate.exchange(url,
                HttpMethod.POST, entity, new ParameterizedTypeReference<Grievance>() {
                });
        return response.getBody();
    }

    public Boolean appealActivationFlag(Grievance grievance) {
        if(grievance == null){
            return false;
        }
        Date today = new Date();
        if (grievance.getOfficeId() == 0) {
            return false;
        }
        if (grievance.getGrievanceCurrentStatus().toString().contains("CLOSED") || grievance.getGrievanceCurrentStatus().toString().contains("REJECTED")) {
            return true;
        } else if(!grievance.getGrievanceCurrentStatus().toString().contains("APPEAL") &&
                TimeUnit.DAYS.convert((today.getTime() - grievance.getCreatedAt().getTime()), TimeUnit.MILLISECONDS) > CalendarUtil.getWorkDaysCountAfter(grievance.getCreatedAt(), (int) Constant.GRIEVANCE_EXPIRATION_TIME)){
            return true;
        }
        return false;
    }

    public Boolean isOISFComplainant(UserInformation userInformation, Grievance grievance) {

        if(grievance == null)
        {
            return false;
        }

        if (!grievance.isGrsUser()
                && (grievance.getComplainantId().equals(userInformation.getUserId())
                || grievance.getComplainantId().equals(userInformation.getOfficeInformation().getEmployeeRecordId()))) {
            return true;
        }
        return false;
    }

    public Boolean serviceIsNull(Grievance grievance) {
        if(grievance == null){
            return false;
        }
        if (grievance.getServiceOrigin() == null) {
            return true;
        } else {
            return false;
        }
    }

    public Boolean isNagorikTypeGrievance(Grievance grievance) {
        if(grievance == null){
            return false;
        }
        return grievance.getGrievanceType().equals(ServiceType.NAGORIK);
    }

    public Boolean isFeedbackEnabled(Grievance grievance) {
        if(grievance == null){
            return false;
        }
        if (grievance.getGrievanceCurrentStatus().toString().matches("(.*)(CLOSED|REJECTED)(.*)")) {
            if (grievance.getGrievanceCurrentStatus().toString().matches("^((?!APPEAL).)*$")) {
                if (grievance.getIsRatingGiven() == null || grievance.getIsRatingGiven().equals(false)) {
                    return true;
                }
            } else if (grievance.getIsAppealRatingGiven() == null || grievance.getIsAppealRatingGiven().equals(false)) {
                return true;
            }
        }
        return false;
    }

    public Boolean isSubmittedByAnonymousUser(Grievance grievance) {
        if (grievance == null) {
            return false;
        }
        return grievance.getComplainantId().equals(0L);
    }

    public List<FeedbackResponseDTO> getFeedbacks(Grievance grievance) {
        FeedbackResponseDTO feedback, appealFeedback;
        List<FeedbackResponseDTO> feedbacks = new ArrayList();
        if(grievance.getIsRatingGiven()!=null && grievance.getIsRatingGiven().equals(true)) {
            feedback = FeedbackResponseDTO.builder()
                    .title(this.messageService.getMessage("feedback.grievance"))
                    .rating(grievance.getRating())
                    .comments(grievance.getFeedbackComments())
                    .build();
            feedbacks.add(feedback);
        }
        if(grievance.getIsAppealRatingGiven()!=null && grievance.getIsAppealRatingGiven().equals(true)) {
            appealFeedback = FeedbackResponseDTO.builder()
                    .title(this.messageService.getMessage("feedback.grievance.appeal"))
                    .rating(grievance.getAppealRating())
                    .comments(grievance.getAppealFeedbackComments())
                    .build();
            feedbacks.add(appealFeedback);
        }
        return feedbacks;
    }

    public int getCountOfAttachedFiles(Grievance grievance){
        if(grievance == null){
            return 0;
        }
        List<AttachedFile> attachedFiles = grievance.getAttachedFiles();
        return attachedFiles.size();
    }

    public Boolean isComplaintRevivable(Grievance grievance, UserInformation userInformation) {
        if(grievance.getGrievanceCurrentStatus().equals(GrievanceCurrentStatus.REJECTED)){
            if(userInformation.getUserType().equals(UserType.OISF_USER)){
                if(userInformation.getOisfUserType().equals(OISFUserType.GRO)){
                    return true;
                }
            }
        }
        return false;
    }

    public ServiceRelatedInfoRequestDTO convertFromBase64encodedString(String base64EncodedString) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        String base64DecodedParameters = StringUtils.newStringUtf8(org.apache.tomcat.util.codec.binary.Base64.decodeBase64(base64EncodedString));
        ServiceRelatedInfoRequestDTO serviceRelatedInfoRequestDTO = objectMapper.readValue(base64DecodedParameters, ServiceRelatedInfoRequestDTO.class);
        String officeName = this.getOfficeNameBanglaByOfficeId(serviceRelatedInfoRequestDTO.getOfficeId());
        String serviceName = this.getServiceNameBanglaByOfficeCitizenCharterId(serviceRelatedInfoRequestDTO.getOfficeCitizenCharterId());
        serviceRelatedInfoRequestDTO.setServiceName(serviceName);
        serviceRelatedInfoRequestDTO.setOfficeName(officeName);
        return serviceRelatedInfoRequestDTO;
    }

    public String getOfficeNameBanglaByOfficeId(Long officeId) {
        Office office = officeService.getOfficeByOfficeId(officeId);
        return office.getNameBangla();
    }

    public String getServiceNameBanglaByOfficeCitizenCharterId(Long officeCitizenCharterId) {
        CitizenCharter citizenCharter = citizenCharterService.getCitizenCharterByOfficeCitizenCharterId(officeCitizenCharterId);
        return citizenCharter.getServiceNameBangla();
    }

    public SafetyNetGrievanceSummaryListDto getSafetyNetGrievanceSummary
            (SafetyNetGrievanceSummaryRequest request) {
        String url = getUrl() + GRS_CORE_CONTEXT_PATH + "/api/grievance/getSafetyNetGrievanceSummary";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Authorization", "Bearer " + getToken());
        ObjectMapper objectMapper = new ObjectMapper();
        String body = "";
        try {
            body = objectMapper.writeValueAsString(request);
        }catch (Exception ex){
            log.error("",ex);
        }
        HttpEntity<String> entity = new HttpEntity<>(body, headers);


        ResponseEntity<SafetyNetGrievanceSummaryListDto> response = restTemplate.exchange(url,
                HttpMethod.POST, entity, new ParameterizedTypeReference<SafetyNetGrievanceSummaryListDto>() {
                });
        return response.getBody();
    }
}
