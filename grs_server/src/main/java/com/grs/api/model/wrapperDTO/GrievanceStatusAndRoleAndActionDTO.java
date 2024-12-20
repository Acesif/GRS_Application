package com.grs.api.model.wrapperDTO;

import com.grs.core.domain.grs.Action;
import com.grs.core.domain.grs.GrievanceStatus;
import com.grs.core.domain.grs.GrsRole;
import lombok.Data;

@Data
public class GrievanceStatusAndRoleAndActionDTO {
    private GrievanceStatus grievanceStatus;
    private GrsRole grsRole;
    private Action action;
}
