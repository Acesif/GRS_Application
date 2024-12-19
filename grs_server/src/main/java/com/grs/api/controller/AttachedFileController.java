package com.grs.api.controller;


import com.grs.api.model.request.FileDTO;
import com.grs.api.model.request.GrievanceRequestDTO;
import com.grs.api.model.wrapperDTO.AttachFIleDTO;
import com.grs.api.model.wrapperDTO.CellMeetingAttachedFilesDTO;
import com.grs.api.model.wrapperDTO.MovementAttachedFileDTO;
import com.grs.api.model.wrapperDTO.MovementAttachedFilesRefDTO;
import com.grs.core.domain.grs.*;
import com.grs.core.service.AttachedFileService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/attachedFile")
public class AttachedFileController {
    private final AttachedFileService attachedFileService;


    @RequestMapping(value = "/addAttachedFiles", method = RequestMethod.POST)
    public void addAttachedFiles(@RequestBody AttachFIleDTO attachFIleDTO) {
        this.attachedFileService.addAttachedFiles(attachFIleDTO.getGrievance(), attachFIleDTO.getGrievanceRequestDTO());
    }

    @RequestMapping(value = "/addMovementAttachedFiles", method = RequestMethod.POST)
    public void addMovementAttachedFiles(@RequestBody MovementAttachedFileDTO movementAttachedFileDTO) {
        this.attachedFileService.addMovementAttachedFiles(
                movementAttachedFileDTO.getGrievanceForwarding(),
                movementAttachedFileDTO.getFilePaths());
    }

    @RequestMapping(value = "/addMovementAttachedFilesRef", method = RequestMethod.POST)
    public void addMovementAttachedFilesRef(@RequestBody MovementAttachedFilesRefDTO movementAttachedFilesRefDTO){
        this.attachedFileService.addMovementAttachedFilesRef(
                movementAttachedFilesRefDTO.getGrievanceForwarding(),
                movementAttachedFilesRefDTO.getMovementAttachedFiles());
    }


    @RequestMapping(value = "/attachedFilesByIdList",method = RequestMethod.POST)
    public List<MovementAttachedFile> getAttachedFilesByIdList(@RequestParam List<Long> ids){
        return this.attachedFileService.getAttachedFilesByIdList(ids);
    }

    @RequestMapping(value = "/addCellMeetingAttachedFiles",method = RequestMethod.POST)
    public void addCellMeetingAttachedFiles(@RequestBody CellMeetingAttachedFilesDTO cellMeetingAttachedFilesDTO){
        this.attachedFileService.addCellMeetingAttachedFiles(
                cellMeetingAttachedFilesDTO.getCellMeeting(),
                cellMeetingAttachedFilesDTO.getFilePaths());
    }


    @RequestMapping(value = "/attachedFilesForCellMeeting",method = RequestMethod.POST)
    public List<CellMeetingAttachedFile> getAttachedFilesForCellMeeting(
            @RequestBody CellMeeting cellMeeting){
        return this.attachedFileService.getAttachedFilesForCellMeeting(cellMeeting);

    }



}
