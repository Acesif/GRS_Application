package com.grs.api.controller;


import com.grs.core.domain.grs.FcmMessage;
import com.grs.core.domain.grs.FcmToken;
import com.grs.core.service.FcmService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/Fcm")
public class FcmController {

    private final FcmService fcmService;

    @RequestMapping(value = "/registerDeviceToken",method = RequestMethod.POST)
    public FcmToken registerDeviceToken(
           @RequestParam String deviceToken,
           @RequestParam String username){
        return this.fcmService.registerDeviceToken(deviceToken,username);

    }

    @RequestMapping(value = "/sendPushNotification",method = RequestMethod.POST)
    public Boolean sendPushNotification(
          @RequestParam  String username,
          @RequestParam  String message,
          @RequestParam  String clickAction){
        return this.fcmService.sendPushNotification(username,message,clickAction);
    }
}
