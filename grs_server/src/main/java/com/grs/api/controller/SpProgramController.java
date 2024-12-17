package com.grs.api.controller;


import com.grs.api.model.response.SpProgramDto;
import com.grs.api.model.response.SpProgramGroDto;
import com.grs.core.domain.grs.SpProgramme;
import com.grs.core.service.SpProgrammeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/SpProgram")
public class SpProgramController {

    private final SpProgrammeService spProgrammeService;

    @RequestMapping(value = "/findAll", method = RequestMethod.GET)
    public List<SpProgramme> findAll() {
        return spProgrammeService.findAll();
    }

    @RequestMapping(value = "/findAllByStatusAndOfficeIdNotNull", method = RequestMethod.GET)
    public List<SpProgramme> findAllByStatusAndOfficeIdNotNull(){
        return spProgrammeService.findAllByStatusAndOfficeIdNotNull();
    }

    @RequestMapping(value = "/getSpProgramGroDetailsList", method = RequestMethod.GET)
    public List<SpProgramGroDto> getSpProgramGroDetailsList(){
        return spProgrammeService.getSpProgramGroDetailsList();
    }

    @RequestMapping(value = "/getSpProgramme/{id}", method = RequestMethod.GET)
    public SpProgramme getSpProgramme(@PathVariable("id") Integer id){
        return spProgrammeService.getSpProgramme(id);
    }

    @RequestMapping(value = "/convertToSpProgramDTO", method = RequestMethod.POST)
    public SpProgramDto convertToSpProgramDTO(@RequestBody SpProgramme entity){
        return spProgrammeService.convertToSpProgramDTO(entity);
    }

    @RequestMapping(value = "/createSpProgram", method = RequestMethod.POST)
    public Boolean createSpProgram(@RequestBody SpProgramDto dto){
        return spProgrammeService.createSpProgram(dto);
    }

    @RequestMapping(value = "/updateSpProgram", method = RequestMethod.POST)
    public Boolean updateSpProgram(@RequestBody SpProgramDto dto){
        return spProgrammeService.updateSpProgram(dto);
    }
}
