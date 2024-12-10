package com.grs.api.controller;

import com.grs.core.dao.SafetyNetDAO;
import com.grs.core.domain.grs.SafetyNetProgram;
import com.grs.core.service.SafetyNetProgramService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.WeakHashMap;

@RestController
@RequiredArgsConstructor
public class SafetyNetProgramController {

    private final SafetyNetProgramService safetyNetProgramService;

    @RequestMapping(value = "/api/citizenCharter/getSafetyNetPrograms", method = RequestMethod.GET)
    public List<SafetyNetProgram> getSafetyNetPrograms(){
        return safetyNetProgramService.getSafetyNetPrograms();
    }

    @RequestMapping(value = "/api/citizenCharter/getSafetyNetPrograms/{id}", method = RequestMethod.GET)
    public SafetyNetProgram findById(@PathVariable("id") Long id){
        return safetyNetProgramService.findById(id);
    }

    @RequestMapping(value = "/api/citizenCharter/savesafetynetprogram", method = RequestMethod.POST)
    public WeakHashMap<String, String> saveProgram(@RequestBody SafetyNetDAO safetyNetDAO){
        return safetyNetProgramService.saveProgram(safetyNetDAO);
    }

    @RequestMapping(value = "/api/citizenCharter/safetynetsearch", method = RequestMethod.POST)
    public Page<SafetyNetDAO> safetyNetSearch(@RequestBody Pageable pageable){
        return safetyNetProgramService.safetyNetSearch(pageable);
    }
}
