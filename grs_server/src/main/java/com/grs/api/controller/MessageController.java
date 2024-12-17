package com.grs.api.controller;


import com.grs.core.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/message")
public class MessageController {

    private final MessageService messageService;

    @RequestMapping(value = "/getCurrentHttpRequest",method = RequestMethod.GET)
    public HttpServletRequest getCurrentHttpRequest(){
       return messageService.getCurrentHttpRequest();
    }

    @RequestMapping(value = "/getCurrentLanguageCode",method = RequestMethod.GET)
    public String getCurrentLanguageCode(){
        return messageService.getCurrentLanguageCode();
    }

    @RequestMapping(value = "/getMessage",method = RequestMethod.POST)
    public String getMessage(@RequestParam String code){
        return messageService.getMessage(code);

    }
    @RequestMapping(value = "/getMessageV2",method = RequestMethod.POST)
    public String getMessageV2(@RequestParam String code){
        return messageService.getMessageV2(code);

    }
    @RequestMapping(value = "/getMessageArgs",method = RequestMethod.POST)
    public String getMessage(
            @RequestParam String code,
            @RequestParam String[] args){
        return messageService.getMessage(code,args);

    }
    @RequestMapping(value = "/getMessageV2Args",method = RequestMethod.POST)
    public String getMessageV2(@RequestParam String code,
                               @RequestParam String[] args){
        return messageService.getMessageV2(code,args);
    }

    @RequestMapping(value = "/getMessageWithArgsV2",method = RequestMethod.POST)
    public String getMessageWithArgsV2(@RequestParam String code,
                                       @RequestParam String... args){
        return messageService.getMessageWithArgs(code, args);
    }

    @RequestMapping(value = "/isCurrentLanguageInEnglish",method = RequestMethod.GET)
    public Boolean isCurrentLanguageInEnglish(){
        return messageService.isCurrentLanguageInEnglish();

    }

    @RequestMapping(value = "/getCurrentMonthYearAsString",method = RequestMethod.GET)
    public String getCurrentMonthYearAsString(){
        return messageService.getCurrentMonthYearAsString();

    }
    @RequestMapping(value = "/getCurrentYearString",method = RequestMethod.GET)
    public String getCurrentYearString(){
        return messageService.getCurrentYearString();

    }
    @RequestMapping(value = "/getCurrentDateMonthYearAsString",method = RequestMethod.GET)
    public String getCurrentDateMonthYearAsString(){
        return messageService.getCurrentDateMonthYearAsString();

    }

}
