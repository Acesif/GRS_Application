package com.grs.grs_client.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CitizensCharterOrigin {
    private Long id;
    private Long officeOriginId;
    private Long layerLevel;
    private String visionBangla;
    private String visionEnglish;
    private String missionBangla;
    private String missionEnglish;
    private String expectationBangla;
    private String expectationEnglish;
}
