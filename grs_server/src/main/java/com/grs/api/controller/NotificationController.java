package com.grs.api.controller;

import com.grs.api.model.NotificationDTO;
import com.grs.api.model.NotificationsDTO;
import com.grs.api.model.UserInformation;
import com.grs.core.domain.grs.GrievanceForwarding;
import com.grs.core.domain.grs.Notification;
import com.grs.core.service.ModelViewService;
import com.grs.core.service.NotificationService;
import com.grs.utils.Utility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.Authentication;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;

@RestController
public class NotificationController {
    @Autowired
    private NotificationService notificationService;

    @RequestMapping(value = "/api/notification", method = RequestMethod.GET)
    public Page<NotificationDTO> getAllNationalitiesAlongWithCountry(Authentication authentication, @PageableDefault(value = Integer.MAX_VALUE) Pageable pageable) {
        UserInformation userInformation = Utility.extractUserInformationFromAuthentication(authentication);
        return this.notificationService.getAllNotificationOfAnUser(userInformation, pageable);
    }

    @RequestMapping(value = "/api/findByOfficeIdAndEmployeeRecordIdAndOfficeUnitOrganogramIdOrderByIdDesc",method = RequestMethod.POST)
    public NotificationsDTO findByOfficeIdAndEmployeeRecordIdAndOfficeUnitOrganogramIdOrderByIdDesc(
            @RequestParam Long officeId,
            @RequestParam Long employeeRecordId,
            @RequestParam Long officeUnitOrganogramId){
        return notificationService.findByOfficeIdAndEmployeeRecordIdAndOfficeUnitOrganogramIdOrderByIdDesc(
                officeId,employeeRecordId,officeUnitOrganogramId);
    }
    @RequestMapping(value = "/api/updateNotification/{id}",method = RequestMethod.GET)
    public Notification updateNotification(@PathVariable("id") Long id){
        return notificationService.updateNotification(id);
    }

    @RequestMapping(value = "/api/saveNotification",method = RequestMethod.POST)
    public Notification saveNotification(
           @RequestBody GrievanceForwarding grievanceForwarding,
           @RequestParam String text,
           @RequestParam String url){
        return notificationService.saveNotification(grievanceForwarding,text,url);
    }



}
