package com.grs.grs_client.model;

import lombok.Data;

@Data
public class Blacklist {
    private Long id;
    private Long complainantId;
    private Long officeId;
    private Boolean requested;
    private Boolean blacklisted;
    private String officeName;
    private String reason;
}
