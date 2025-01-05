package com.grs.grs_client.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class OfficeSetup {
    private OfficesGroDTO officesGroDTO;
    private ServiceOrigin serviceOriginDTO;
    private CitizenCharter citizenCharterDTO;
}
