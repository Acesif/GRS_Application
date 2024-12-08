package com.grs.grs_client.controller;


import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

/**
 * Created by Acer on 10/2/2017.
 */

@Slf4j
@RestController
public class StorageController {

    @RequestMapping(value = "/uploadFile.do", method = RequestMethod.GET)
    public ModelAndView getFileUploadPage() {
        return new ModelAndView("fileUploadPage");
    }
}
