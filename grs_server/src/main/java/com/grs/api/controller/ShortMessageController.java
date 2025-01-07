package com.grs.api.controller;

import com.grs.api.model.response.ActionToRoleDTO;
import com.grs.api.model.response.SmsTemplateDTO;
import com.grs.api.model.wrapperDTO.SmsACtionRoleDTO;
import com.grs.core.domain.grs.ActionToRole;
import com.grs.core.domain.grs.SmsTemplate;
import com.grs.core.service.ShortMessageService;
import groovy.util.logging.Slf4j;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
public class ShortMessageController {
    
    //Unresolved @Async annotation Method

    private final ShortMessageService shortMessageService;

    @GetMapping(value = "/api/shortMessage/findAllSmsTemplates")
    public Page<SmsTemplate> findAllSmsTemplates(
            @PageableDefault(value = Integer.MAX_VALUE) Pageable pageable) {
        return shortMessageService.findAllSmsTemplates(pageable);

    }

    @PostMapping(value = "/api/shortMessage/saveAllSmsTemplates")
    public Boolean saveAllSmsTemplates(
           @RequestBody SmsACtionRoleDTO smsACtionRoleDTO) {
        return shortMessageService.saveAllSmsTemplates(
                smsACtionRoleDTO.getSmsTemplateDTO(),
                smsACtionRoleDTO.getActionToRoleDTO());
    }

    @PostMapping(value = "/api/shortMessage/smsTemplateID/{smsTemplateID}")
    public Boolean updateSmsTemplate(
            @PathVariable("smsTemplateID") Long smsTemplateID,
            @RequestBody  SmsTemplateDTO smsTemplateDTO) {
        return shortMessageService.updateSmsTemplate(smsTemplateID,smsTemplateDTO);
    }

    @GetMapping(value = "/api/shortMessage/smsTemplateID/{smsTemplateID}")
    public SmsTemplate getSmsTemplate(@PathVariable("smsTemplateID") Long smsTemplateID){

        return shortMessageService.getSmsTemplate(smsTemplateID);
    }

    @PostMapping(value = "/api/shortMessage/findSmsTemplate")
    public SmsTemplate findSmsTemplate(@RequestBody ActionToRole actionToRole) {
        return shortMessageService.findSmsTemplate(actionToRole);
    }

    @PostMapping(value = "/api/shortMessage/convertToSmsTemplateDTO")
    public SmsTemplateDTO convertToSmsTemplateDTO(@RequestBody SmsTemplate smsTemplate) {
        return shortMessageService.convertToSmsTemplateDTOWithRecipient(smsTemplate);
    }

    @PostMapping(value = "/api/shortMessage/convertToSmsTemplateDTOWithRecipient")
    public SmsTemplateDTO convertToSmsTemplateDTOWithRecipient(
            @RequestBody SmsTemplate smsTemplate) {
        return shortMessageService.convertToSmsTemplateDTOWithRecipient(smsTemplate);
    }



}
