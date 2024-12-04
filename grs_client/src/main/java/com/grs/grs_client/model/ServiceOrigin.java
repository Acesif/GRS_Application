package com.grs.grs_client.model;

import com.grs.grs_client.enums.ServiceType;
import lombok.Data;

@Data
public class ServiceOrigin {
    private Long id;
    private String serviceNameBangla;
    private String serviceNameEnglish;
    private String serviceProcedureBangla;
    private String serviceProcedureEnglish;
    private String documentAndLocationBangla;
    private String documentAndLocationEnglish;
    private String paymentMethodBangla;
    private String paymentMethodEnglish;
    private Integer serviceTime;
    private Long officeOriginId;
    private Long officeOriginUnitId;
    private Long officeOriginUnitOrganogramId;
    private Long officeOriginNameBangla;
    private Long officeOriginNameEnglish;
    private ServiceType serviceType;
}
