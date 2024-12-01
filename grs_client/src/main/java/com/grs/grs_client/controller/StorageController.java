package com.grs.grs_client.controller;

import com.grs.api.model.response.file.FileContainerDTO;
import com.grs.core.model.EmptyJsonResponse;
import com.grs.core.service.StorageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;

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
