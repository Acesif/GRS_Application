package com.grs.grs_client.model;

import com.grs.grs_client.enums.LanguageStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SmsTemplate {

    private Long id;
    private ActionToRole actionToRole;
    private String smsTemplateName;
    private String smsTemplateBodyEng;
    private String smsTemplateBodyBng;
    private Boolean status = true;
    private LanguageStatus language;
}