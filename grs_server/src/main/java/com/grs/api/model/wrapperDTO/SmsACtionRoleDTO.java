package com.grs.api.model.wrapperDTO;

import com.grs.api.model.response.ActionToRoleDTO;
import com.grs.api.model.response.SmsTemplateDTO;
import lombok.Data;
import org.springframework.web.bind.annotation.RequestBody;

@Data
public class SmsACtionRoleDTO {

   private SmsTemplateDTO smsTemplateDTO;

    private ActionToRoleDTO actionToRoleDTO;
}
