package com.grs.api.model.wrapperDTO;

import com.grs.core.domain.grs.GrievanceStatus;
import com.grs.core.domain.grs.GrsRole;
import lombok.Data;

@Data
public class GrievanceStatusAndRoleTypeDTO {
    private GrievanceStatus grievanceStatus;
    private GrsRole role;
}
