package com.grs.grs_client.model;

import lombok.Data;

@Data
public class EmployeeOffice {
    private Long id;
    private Office office;
    private OfficeUnit officeUnit;
    private OfficeUnitOrganogram officeUnitOrganogram;
    private EmployeeRecord employeeRecord;
    private String identificationNumber;
    private String designation;
    private String inchargeLabel;
    private Boolean status;
    private Boolean isOfficeHead;
    private Boolean isDefaultRole;
}
