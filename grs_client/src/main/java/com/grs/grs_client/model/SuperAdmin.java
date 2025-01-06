package com.grs.grs_client.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SuperAdmin {

    private Long id;

    private String username;

    private String password;

    private String phoneNumber;

    private String email;

    private long officeId;

    private GrsRole role;
}
