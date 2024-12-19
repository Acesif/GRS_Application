package com.grs.grs_client.model;

import lombok.Builder;
import lombok.Data;
import org.hibernate.validator.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@Builder
public class LoginRequest {
    private String username;
    private String password;
}