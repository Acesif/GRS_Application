package com.grs.grs_client.gateway;

import com.grs.grs_client.enums.ServiceType;
import com.grs.grs_client.model.AttachedFile;
import com.grs.grs_client.model.FeedbackResponseDTO;
import com.grs.grs_client.model.Grievance;
import com.grs.grs_client.model.UserInformation;
import com.grs.grs_client.service.MessageService;
import com.grs.grs_client.utils.CalendarUtil;
import com.grs.grs_client.utils.Constant;
import com.grs.grs_client.utils.Utility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class GrievanceGateway extends BaseRestTemplate {

    @Autowired
    private MessageService messageService;

    public Grievance findGrievanceById(Long grievanceId) {
        String url = getUrl() + "/api/grievance/findGrievanceById/" + grievanceId;
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
        String url = getUrl() + "/api/grievance/saveGrievance";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Authorization", "Bearer " + getToken());
        HttpEntity<String> entity = new HttpEntity<>(headers);


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

    public Boolean isOISFComplainant(Authentication authentication, Grievance grievance) {

        if(grievance == null)
        {
            return false;
        }

        UserInformation userInformation = Utility.extractUserInformationFromAuthentication(authentication);

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
}
