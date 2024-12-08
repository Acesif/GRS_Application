package com.grs.grs_client.model;

import lombok.Data;

@Data
public class SafetyNetProgram {
    private Long id;
    private String code;
    private String nameEn;
    private String nameBn;
    private Long officeId;
    private Long officeLayer;
    private Boolean active = Boolean.TRUE;
}
