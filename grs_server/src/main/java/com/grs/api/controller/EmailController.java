package com.grs.api.controller;

import com.grs.api.model.response.ActionToRoleDTO;
import com.grs.api.model.response.EmailTemplateDTO;
import com.grs.api.model.wrapperDTO.EmailServicePayloadDTO;
import com.grs.core.domain.grs.ActionToRole;
import com.grs.core.domain.grs.EmailTemplate;
import com.grs.core.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/email")
public class EmailController {
    private final EmailService emailService;

    @RequestMapping(value = "/emailTempletes", method = RequestMethod.GET)
    public Page<EmailTemplateDTO> findAllEmailTemplates(
            @PageableDefault(value = Integer.MAX_VALUE) Pageable pageable) {
        return this.emailService.findAllEmailTemplates(pageable);
    }

    @RequestMapping(value = "/convertToEmailTemplateDTO", method = RequestMethod.POST)
    public EmailTemplateDTO convertToEmailTemplateDTO(
            @RequestBody EmailTemplate emailTemplate) {
        return this.emailService.convertToEmailTemplateDTO(emailTemplate);
    }

    @RequestMapping(value = "/convertToEmailTemplateDTOWithRecipient", method = RequestMethod.POST)
    public EmailTemplateDTO convertToEmailTemplateDTOWithRecipient(
            @RequestBody EmailTemplate emailTemplate) {
        return this.emailService.convertToEmailTemplateDTOWithRecipient(emailTemplate);
    }

    @RequestMapping(value = "/saveAllEmailTemplates", method = RequestMethod.POST)
    public Boolean saveAllEmailTemplates(
            @RequestBody EmailServicePayloadDTO emailServicePayloadDTO
            ){
        return this.emailService.saveAllEmailTemplates(emailServicePayloadDTO.getEmailTemplateDTO(),emailServicePayloadDTO.getActionToRoleDTO());

    }

    @RequestMapping(value = "/emailTempleteById/{id}",method = RequestMethod.GET)
    public EmailTemplate getEmailTemplate(@PathVariable("id") Long id){
        return this.emailService.getEmailTemplate(id);
    }

    @RequestMapping(value = "/emailTemplteByActionRole",method = RequestMethod.POST)
    public EmailTemplate findEmailTemplate(@RequestBody ActionToRole actionToRole){
        return this.emailService.findEmailTemplate(actionToRole);
    }


    @RequestMapping(value = "/updateEmailTemplate/{emailTemplateID}",method = RequestMethod.POST)
    public Boolean updateEmailTemplate(
           @PathVariable("emailTemplateID") Long emailTemplateID,
           @RequestBody EmailTemplateDTO emailTemplateDTO){
        return this.emailService.updateEmailTemplate(emailTemplateID,emailTemplateDTO);
    }


}
