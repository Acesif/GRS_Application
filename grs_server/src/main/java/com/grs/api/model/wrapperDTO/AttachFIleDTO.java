package com.grs.api.model.wrapperDTO;

import com.grs.api.model.request.GrievanceRequestDTO;
import com.grs.core.domain.grs.Grievance;
import lombok.Data;

@Data
public class AttachFIleDTO {
    private Grievance grievance;
    private GrievanceRequestDTO grievanceRequestDTO;
}
