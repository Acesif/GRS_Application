package com.grs.grs_client.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.grs.grs_client.enums.GRSUserType;
import com.grs.grs_client.enums.OISFUserType;
import com.grs.grs_client.enums.UserType;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
@ToString
public class UserInformation {
    private Long userId;
    private String username;
    private UserType userType;
    private OISFUserType oisfUserType;
    private GRSUserType grsUserType;
    private OfficeInformation officeInformation;
    private Boolean isAppealOfficer;
    private Boolean isOfficeAdmin;
    private Boolean isCentralDashboardUser;
    private Boolean isCellGRO;
    private Boolean isMobileLogin;
    private Boolean isMyGovLogin;
    private String token = "";
//    private MyGovTokenResponse myGovTokenResponse;
}
