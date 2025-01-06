package com.grs.grs_client.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Education {
    private Long Id;
    private String educationBangla;
    private String educationEnglish;
    private Boolean status;
}
