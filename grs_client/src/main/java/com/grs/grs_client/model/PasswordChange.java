package com.grs.grs_client.model;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PasswordChange {
    private String username;
    private String oldPassword;
    private String newPassword;
    private String confirmPassword;
}
