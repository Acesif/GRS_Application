package com.grs.grs_client.model;

import lombok.Data;

@Data
public class CitizenCharter {
    private Long id;
    private Long officeId;
    private Long officeOriginId;
    private Long soOfficeId;
    private Long soOfficeUnitId;
    private Long soOfficeUnitOrganogramId;
    private ServiceOrigin serviceOrigin;
    private String serviceNameBangla;
    private String serviceNameEnglish;
    private String serviceProcedureBangla;
    private String serviceProcedureEnglish;
    private String documentAndLocationBangla;
    private String documentAndLocationEnglish;
    private String paymentMethodBangla;
    private String paymentMethodEnglish;
    private Integer serviceTime;
    private Boolean status;
    private Boolean originStatus;
}
