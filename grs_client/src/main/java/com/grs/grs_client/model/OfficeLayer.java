package com.grs.grs_client.model;

import lombok.Data;

@Data
public class OfficeLayer {
    private Long id;
    private String layerNameBangla;
    private String layerNameEnglish;
    private Integer layerLevel;
    private Integer customLayerId;
    private Integer layerSequence;
    private OfficeMinistry officeMinistry;
}
