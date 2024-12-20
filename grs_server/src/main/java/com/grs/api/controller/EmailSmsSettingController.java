package com.grs.api.controller;

import com.grs.api.model.response.EmailSmsSettingsDTO;
import com.grs.core.domain.grs.EmailSmsSettings;
import com.grs.core.service.EmailSmsSettingsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.WeakHashMap;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/emailSmsSetting")
public class EmailSmsSettingController {
    private final EmailSmsSettingsService emailSmsSettingsService;

    @RequestMapping(value = "/findEmailSmsSettingDTO",method = RequestMethod.POST)
    public EmailSmsSettingsDTO getEmailSmsSettingsDTO(
            @RequestBody EmailSmsSettings emailSmsSettings){
        return this.emailSmsSettingsService.getEmailSmsSettingsDTO(emailSmsSettings);
    }

    @RequestMapping(value = "/findEmailSmsSetting",method = RequestMethod.GET)
    public List<EmailSmsSettings> findEmailSmsSettings(){
        return this.emailSmsSettingsService.findEmailSmsSettings();
    }

    @RequestMapping(value = "/emailSmsSettingWithId/{id}",method = RequestMethod.GET)
    public EmailSmsSettings getEmailSmsSettings(@PathVariable("id") Long id){
        return this.emailSmsSettingsService.getEmailSmsSettings(id);
    }

    @RequestMapping(value = "/updateEmailSmsSettings/{id}",method = RequestMethod.POST)
    public Boolean updateEmailSmsSettings(
           @PathVariable("id") Long smsID,
           @RequestBody EmailSmsSettingsDTO emailSmsSettingsDTO){
        return this.emailSmsSettingsService.updateEmailSmsSettings(smsID,emailSmsSettingsDTO);
    }

    @RequestMapping(value = "/findOneEmailSmsId/{emailSmsID}",method = RequestMethod.GET)
    public EmailSmsSettings findOne(@PathVariable("emailSmsID") Long emailSmsID){
        return this.emailSmsSettingsService.findOne(emailSmsID);
    }

    @RequestMapping(value = "/convertToGrievanceStatusList",method = RequestMethod.GET)
    public WeakHashMap<String, String> convertToGrievanceStatusList(){
        return this.emailSmsSettingsService.convertToGrievanceStatusList();
    }

    @RequestMapping(value = "/convertToGrsRoleList",method = RequestMethod.GET)
    public WeakHashMap<String, String> convertToGrsRoleList(){
        return this.emailSmsSettingsService.convertToGrsRoleList();
    }

    @RequestMapping(value = "/convertToActionList",method = RequestMethod.GET)
    public WeakHashMap<String, String> convertToActionList(){
        return this.emailSmsSettingsService.convertToActionList();
    }


}
