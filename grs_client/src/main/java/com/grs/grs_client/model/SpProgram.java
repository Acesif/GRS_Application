package com.grs.grs_client.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SpProgram {
    private Integer id;
    private String nameEn;
    private String nameBn;
    private Long officeId;
    private Boolean status;
}
