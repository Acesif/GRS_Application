package com.grs.grs_client.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoleContainerDTO {
    List<SingleRoleDTO> roles;


    @Data
    private static class SingleRoleDTO {
        private Long officeUnitOrganogramId;
        private Long officeUnitId;
        private Long officeId;
        private Long officeOriginId;
        private Long officeMinistryId;
        private String designation;
        private String officeNameBangla;
        private String officeNameEnglish;
        private String officeUnitNameBangla;
        private String officeUnitNameEnglish;
        private Long layerLevel;
        private Long geoDivisionId;
        private Long geoDistrictId;
        private String phone;
        private String email;
        private boolean selected;
    }
}



