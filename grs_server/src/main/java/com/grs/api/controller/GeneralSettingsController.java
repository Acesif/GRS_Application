package com.grs.api.controller;

import com.grs.api.model.request.FileSettingsDTO;
import com.grs.api.model.request.SystemNotificationSettingsDTO;
import com.grs.api.model.response.GenericResponse;
import com.grs.core.service.GeneralSettingsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/generalSettings")
public class GeneralSettingsController {
    private final GeneralSettingsService generalSettingsService;

    @RequestMapping(value = "/getAllowedFileTypes",method= RequestMethod.GET)
    public String getAllowedFileTypes() {
        return generalSettingsService.getAllowedFileTypes();
    }

    @RequestMapping(value = "/getSettingsValueByFieldName",method = RequestMethod.POST)
    public String getSettingsValueByFieldName(@RequestParam String name) {
        return generalSettingsService.getSettingsValueByFieldName(name);
    }

    @RequestMapping(value = "/getMaximumFileSize",method = RequestMethod.GET)
    public Integer getMaximumFileSize() {
        return generalSettingsService.getMaximumFileSize();
    }

    @RequestMapping(value = "/getAllowedFileSizeLabel",method = RequestMethod.GET)
    public String getAllowedFileSizeLabel() {
       return generalSettingsService.getAllowedFileSizeLabel();
    }

    @RequestMapping(value = "/getAllowedFileTypesLabel",method = RequestMethod.GET)
    public String getAllowedFileTypesLabel() {
        return generalSettingsService.getAllowedFileTypesLabel();
    }

    @RequestMapping(value = "/saveFileSettings",method = RequestMethod.POST)
    public GenericResponse saveFileSettings(@RequestBody FileSettingsDTO fileSettingsDTO){
        return generalSettingsService.saveFileSettings(fileSettingsDTO);
    }

    @RequestMapping(value = "/saveSystemNotificationSettings",method = RequestMethod.POST)
    public GenericResponse saveSystemNotificationSettings(@RequestBody SystemNotificationSettingsDTO systemNotificationSettingsDTO){
        return generalSettingsService.saveSystemNotificationSettings(systemNotificationSettingsDTO);
    }


}
