package com.grs.grs_client.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OfficeUnitWithDesignationDTO {
    private String officeUnitNameBangla;
    private String officeUnitNameEnglish;
    private String designation;
    private Long officeUnitOrganogramId;
}
