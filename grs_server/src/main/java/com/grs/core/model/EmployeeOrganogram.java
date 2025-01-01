package com.grs.core.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EmployeeOrganogram {
    private Long officeId;
    private Long ministryId;
    private Long officeUnitOrganogramId;
}
