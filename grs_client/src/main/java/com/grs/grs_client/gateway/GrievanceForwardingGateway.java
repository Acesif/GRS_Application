package com.grs.grs_client.gateway;

import com.grs.grs_client.enums.GrievanceCurrentStatus;
import com.grs.grs_client.model.Grievance;
import com.grs.grs_client.model.GrievanceForwarding;
import com.grs.grs_client.model.OfficesGRO;
import com.grs.grs_client.model.UserInformation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class GrievanceForwardingGateway extends BaseRestTemplate {

    @Autowired
    private OfficesGroGateway officesGroService;

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
        String url = getUrl() + "/api/grievanceforwarding/getLastForwadingForGivenGrievance/" + grievanceId;
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
        String url = getUrl() + "/api/grievanceforwarding/findByGrievanceAndIsCurrent/" + grievanceId;
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
        String url = getUrl() + "/api/grievanceforwarding/getAllComplaintMovementByGrievance/" + grievanceId;
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
        String url = getUrl() + "/api/grievanceforwarding/getLastClosedOrRejectedForwarding/" + grievanceId;
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

}
