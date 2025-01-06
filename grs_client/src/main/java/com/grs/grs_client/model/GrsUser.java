package com.grs.grs_client.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GrsUser {

    private Long Id;

    private String username;

    private String password;

    private String email;

    private String mobileNumber;

    private long officeId;
}