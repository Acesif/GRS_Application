package com.grs.grs_client.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OfficeOriginUnitOrganogramDTO {
    private Long id;
    private Long officeOriginUnitId;
    private String nameBangla;
    private String nameEnglish;
}