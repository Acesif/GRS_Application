package com.grs.grs_client.service;

import com.grs.grs_client.enums.OISFUserType;
import com.grs.grs_client.enums.UserType;
import com.grs.grs_client.gateway.GrievanceGateway;
import com.grs.grs_client.gateway.OfficesGroGateway;
import com.grs.grs_client.model.Grievance;
import com.grs.grs_client.model.GrievanceForwarding;
import com.grs.grs_client.model.OfficesGRO;
import com.grs.grs_client.model.UserInformation;
import com.grs.grs_client.utils.Utility;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
public class AccessControlService {
    @Autowired
    GrievanceGateway grievanceService;
    @Autowired
    OfficesGroGateway officesGroService;

    public Boolean hasPermissionToViewGrievanceDetails(Authentication authentication, Long grievanceId) {
        Grievance grievance = grievanceService.findGrievanceById(grievanceId);
        UserInformation userInformation = Utility.extractUserInformationFromAuthentication(authentication);

        if(userInformation.getUserType().equals(UserType.COMPLAINANT)) {
            return userInformation.getUserId().equals(grievance.getComplainantId());
        } else if(userInformation.getUserType().equals(UserType.OISF_USER)) {

            Long officeId = userInformation.getOfficeInformation().getOfficeId();
            Long officeUnitOrganogramId = userInformation.getOfficeInformation().getOfficeUnitOrganogramId();
            if(grievance.getComplainantId() != null && (grievance.getComplainantId().equals(userInformation.getUserId()) || grievance.getComplainantId().equals(userInformation.getOfficeInformation().getEmployeeRecordId()))) {
                return true;
            }
            List<OfficesGRO> officesGROES = this.officesGroService.getAncestors(grievance.getOfficeId());
            for (OfficesGRO officesGRO : officesGROES){
                if((Objects.equals(officeId, officesGRO.getGroOfficeId()) && Objects.equals(officeUnitOrganogramId, officesGRO.getGroOfficeUnitOrganogramId())) ||
                        (Objects.equals(officeId, officesGRO.getGroOfficeId()) && Objects.equals(officeUnitOrganogramId, officesGRO.getAppealOfficerOfficeUnitOrganogramId()))){
                    return true;
                }
            }
            if (userInformation.getIsCentralDashboardUser()){
                return true;
            }
            if( userInformation.getOisfUserType().equals(OISFUserType.HEAD_OF_OFFICE) && Objects.equals(officeId, grievance.getOfficeId())){
                return true;
            }
            List<GrievanceForwarding> grievanceForwardingList = grievanceService.getAllComplaintMovementByGrievance(grievance.getId());
            List<Long> permittedOrganogramIdList = new ArrayList();
            grievanceForwardingList.forEach(grievanceForwarding -> {
                permittedOrganogramIdList.add(grievanceForwarding.getFromOfficeUnitOrganogramId());
                permittedOrganogramIdList.add(grievanceForwarding.getToOfficeUnitOrganogramId());
            });
            return permittedOrganogramIdList.contains(userInformation.getOfficeInformation().getOfficeUnitOrganogramId());
        } else {
            return false;
        }
    }
}
