package com.grs.grs_client.model;

import com.grs.grs_client.enums.GRSUserType;
import com.grs.grs_client.enums.OISFUserType;
import com.grs.grs_client.enums.UserType;
import lombok.Data;

import java.util.List;

@Data
public class UserDetails {
    private Long id;
    private String name;
    private String username;
    private String password;
    private Boolean isAppealOfficer;
    private Boolean isOfficeAdmin;
    private Boolean isCentralDashboardUser;
    private Boolean isCellGRO;
    private List<String> permissions;
    private OfficeInformation officeInformation;
    private UserType userType;
    private OISFUserType oisfUserType;
    private GRSUserType grsUserType;



}
