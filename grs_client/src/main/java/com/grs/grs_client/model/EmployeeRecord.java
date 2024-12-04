package com.grs.grs_client.model;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class EmployeeRecord {
    private Long id;
    private String nameEnglish;
    private String nameBangla;
    private String motherNameEnglish;
    private String motherNameBangla;
    private String fatherNameEnglish;
    private String fatherNameBangla;
    private String personalEmail;
    private String personalMobile;
    private String nationalId;
    private Date dateOfBirth;
    private String gender;
    private List<Office> offices;
    private List<OfficeUnit> officeUnits;
    private List<OfficeUnitOrganogram> officeUnitOrganograms;
    private List<EmployeeOffice> employeeOffices;
}
