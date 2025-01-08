package com.grs.api.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OISFUserDTO {
    private Long id;
    private String username;
    private Long employeeRecordId;
}
