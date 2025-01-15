package com.grs.grs_client.model;

import com.grs.grs_client.enums.LanguageStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Created by HP on 2/7/2018.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EmailTemplateDTO {
    private Long id;
    private ActionToRole actionToRole;
    private String emailTemplateName;
    private String emailTemplateBodyEng;
    private String emailTemplateBodyBng;
    private String emailTemplateSubjectEng;
    private String emailTemplateSubjectBng;
    private Boolean status;
    private LanguageStatus language;
    private List<String> recipient;
}
