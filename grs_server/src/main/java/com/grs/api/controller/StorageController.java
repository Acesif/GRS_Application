package com.grs.api.controller;

import com.grs.api.model.request.FileDTO;
import com.grs.api.model.response.file.DerivedFileContainerDTO;
import com.grs.api.model.response.file.FileContainerDTO;
import com.grs.core.model.EmptyJsonResponse;
import com.grs.core.service.StorageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.util.List;

@Slf4j
@RestController
public class StorageController {

    @Autowired
    private StorageService storageService;

    @RequestMapping(value = "/api/file/upload", method = RequestMethod.POST)
    public FileContainerDTO addFile(Principal principal, @RequestParam("files[]") MultipartFile[] files, HttpServletRequest request) throws Exception {
        request.setCharacterEncoding("UTF-8");
        return this.storageService.storeFile(principal, files);
    }

    @RequestMapping(value = "/api/file/upload_new", method = RequestMethod.POST)
    public FileContainerDTO addFileNew(Principal principal, @RequestParam("files[]") MultipartFile[] files, HttpServletRequest request) throws Exception {
        request.setCharacterEncoding("UTF-8");
        return this.storageService.storeFileNew(principal, files);
    }

    @RequestMapping(value = "/api/file/upload", method = RequestMethod.GET)
    public EmptyJsonResponse getSomething(Principal principal) {
        return new EmptyJsonResponse();
    }

    @RequestMapping(value = "/api/file/upload/{folderName}/{fileName}/", method = RequestMethod.DELETE)
    public Object deleteFile(Principal principal,
                             @PathVariable("folderName") String folderName,
                             @PathVariable("fileName") String fileName) {
        return this.storageService.deleteFile(principal, folderName, fileName);
    }

    @RequestMapping(value = "/api/file/upload/{folderName}/{fileName}/", method = RequestMethod.GET)
    public ResponseEntity<InputStreamResource> getFile(Principal principal,
                                                       @PathVariable("folderName") String folderName,
                                                       @PathVariable("fileName") String fileName) {
        return this.storageService.getFile(principal, folderName, fileName);
    }

    //todo:Need to do later
    @RequestMapping(value = "/api/file/storeDerivedFile", method = RequestMethod.POST)
    public DerivedFileContainerDTO storeDerivedFile(
            Principal principal,
            @RequestParam("files[]") MultipartFile[] files) {
        return this.storageService.storeDerivedFile(principal, files);
    }

    @RequestMapping(value = "/api/file/getMediaTypeFromFileType/{path}",method=RequestMethod.GET)
    public MediaType getMediaTypeFromFileType(@PathVariable("path") String path) {
        return this.storageService.getMediaTypeFromFileType(path);

    }

    @RequestMapping(value="/api/file/getThumbnailLinkImageForFile/{filePath}",method=RequestMethod.GET)
    public String getThumbnailLinkImageForFile(@PathVariable("filePath") String filePath){
        return this.storageService.getThumbnailLinkImageForFile(filePath);
    }

    @RequestMapping(value="/api/file/checkFileSize",method=RequestMethod.POST)
    public boolean checkFileSize(@RequestBody List<FileDTO> files){
        return this.storageService.checkFileSize(files);
    }

    @RequestMapping(value = "/uploadFile.do", method = RequestMethod.GET)
    public ModelAndView getFileUploadPage() {
        return new ModelAndView("fileUploadPage");
    }
}
