package com.grs.grs_client.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
public class BaseEntity {

    private Date createdAt;
    private Date updatedAt;
    private Long createdBy;
    private Long modifiedBy;
    private Boolean status;
}
