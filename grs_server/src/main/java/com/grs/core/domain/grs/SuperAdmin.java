package com.grs.core.domain.grs;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;

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
