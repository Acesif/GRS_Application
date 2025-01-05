package com.grs.grs_client.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OfficeOriginUnitDTO {
    private Long id;
    private Long officeOriginId;
    private String nameBangla;
    private String nameEnglish;
}