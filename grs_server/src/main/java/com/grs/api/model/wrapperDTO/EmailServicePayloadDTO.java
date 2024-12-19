package com.grs.api.model.wrapperDTO;

import com.grs.api.model.response.ActionToRoleDTO;
import com.grs.api.model.response.EmailTemplateDTO;
import lombok.Data;

@Data
public class EmailServicePayloadDTO {
    private EmailTemplateDTO emailTemplateDTO;
    private ActionToRoleDTO actionToRoleDTO;
}
