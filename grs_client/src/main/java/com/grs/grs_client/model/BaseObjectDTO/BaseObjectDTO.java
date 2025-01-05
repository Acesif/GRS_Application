package com.grs.grs_client.model.BaseObjectDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BaseObjectDTO {
    private Long id;
    private String name;
}
