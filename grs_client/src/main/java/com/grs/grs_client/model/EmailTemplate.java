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
public class EmailTemplate {

    private Long id;
    private ActionToRole actionToRole;
    private String emailTemplateName;
    private String emailTemplateSubjectEng;
    private String emailTemplateSubjectBng;
    private String emailTemplateBodyEng;
    private String emailTemplateBodyBng;
    private Boolean status = true;
    private LanguageStatus language;
}