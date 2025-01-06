package com.grs.grs_client.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GrsRole extends BaseEntity {

    private Long id;
    private String role;
    private List<Permission> permissions;
}
