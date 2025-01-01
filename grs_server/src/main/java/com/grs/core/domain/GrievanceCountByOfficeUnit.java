package com.grs.core.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GrievanceCountByOfficeUnit {
    private Long count;
    private Long officeUnitId;
}
