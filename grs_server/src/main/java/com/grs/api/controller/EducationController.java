package com.grs.api.controller;

import com.grs.api.model.response.EducationDTO;
import com.grs.core.domain.grs.Education;
import com.grs.core.service.EducationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/education")
public class EducationController {

    private final EducationService educationService;

    @RequestMapping(value = "/convertToEducationDTO",method = RequestMethod.POST)
    public EducationDTO convertToEducationDTO(
            @RequestBody Education education){
        return this.educationService.convertToEducationDTO(education);
    }
    @RequestMapping(value = "/findAllEducation",method = RequestMethod.GET)
    public Page<EducationDTO> findAllEducation (
            @PageableDefault(value = Integer.MAX_VALUE) Pageable pageable){
        return this.educationService.findAllEducation(pageable);
    }
    @RequestMapping(value = "/saveAllEducation",method = RequestMethod.POST)
    public Boolean saveAllEducation(@RequestBody EducationDTO educationDTO){
        return this.educationService.saveAllEducation(educationDTO);
    }
    @RequestMapping(value = "/educationById/{id}",method = RequestMethod.GET)
    public Education getEducation(@PathVariable("id") Long id){
        return this.educationService.getEducation(id);
    }

    @RequestMapping(value = "/updateOccupation",method = RequestMethod.POST)
    public Boolean updateOccupation(
           @PathVariable("educationID") Long educationID,
           @RequestBody EducationDTO educationDTO){
        return this.educationService.updateOccupation(educationID,educationDTO);
    }



}
