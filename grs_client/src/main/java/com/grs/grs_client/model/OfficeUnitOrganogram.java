package com.grs.grs_client.model;

import lombok.Data;

@Data
public class OfficeUnitOrganogram {
    private Long id;
    private Long officeId;
    private OfficeUnit officeUnit;
    private Long officeUnitId;
    private String designationEnglish;
    private String designationBangla;
    private Office office;
    private Long refOriginUnitOrgId;
    public Boolean isAdmin;
    public Boolean status;
}
