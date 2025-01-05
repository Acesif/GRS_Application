package com.grs.grs_client.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Occupation {

    private Long id;
    private String occupationBangla;
    private String occupationEnglish;
    private Boolean status = true;
}
