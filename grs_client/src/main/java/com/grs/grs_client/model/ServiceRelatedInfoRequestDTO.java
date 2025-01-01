package com.grs.grs_client.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ServiceRelatedInfoRequestDTO {
    private Long officeCitizenCharterId;
    private Long serviceId;
    private Long officeId;
    private String serviceName;
    private String officeName;
}
