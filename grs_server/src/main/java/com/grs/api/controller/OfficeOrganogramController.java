package com.grs.api.controller;


import com.grs.api.model.response.organogram.OfficeOriginUnitOrganogramDTO;
import com.grs.api.model.response.organogram.TreeNodeDTO;
import com.grs.api.model.response.organogram.TreeNodeOfficerDTO;
import com.grs.core.domain.projapoti.OfficeUnitOrganogram;
import com.grs.core.service.OfficeOrganogramService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/officeOrganogram")
public class OfficeOrganogramController {

    private final OfficeOrganogramService officeOrganogramService;


    @RequestMapping(value = "/findOne/{id}",method = RequestMethod.GET)
    public OfficeUnitOrganogram findOne(@PathVariable("id") Long id){
        return officeOrganogramService.findOne(id);
    }

    @RequestMapping(value = "/findOfficeUnitOrganogramById/{id}",method = RequestMethod.GET)
    public OfficeUnitOrganogram findOfficeUnitOrganogramById(@PathVariable("id") Long id){
       return officeOrganogramService.findOfficeUnitOrganogramById(id);
    }

    // todo: need to do later
    @RequestMapping(value = "/getSubOfficesWithOrganograms",method = RequestMethod.POST)
    public List<TreeNodeDTO> getSubOfficesWithOrganograms(
            @RequestParam String id,
            Authentication authentication){
        return officeOrganogramService.getSubOfficesWithOrganograms(id,authentication);
    }
    // todo: need to do later
    @RequestMapping(value="/getOrganogram",method = RequestMethod.POST)
    public List<TreeNodeDTO> getOrganogram(
            @RequestParam String id,
            Authentication authentication){
        return officeOrganogramService.getOrganogram(id,authentication);
    }
    // todo: need to do later
    @RequestMapping(value="/getDescendentOffices",method = RequestMethod.POST)
    public List<TreeNodeDTO> getDescendentOffices(
            @RequestParam String id,
            Authentication authentication){
        return officeOrganogramService.getDescendentOffices(id, authentication);
    }
    // todo: need to do later
    @RequestMapping(value="/getDescendentOfficesWithUnits",method = RequestMethod.POST)
    public List<TreeNodeDTO> getDescendentOfficesWithUnits(
            @RequestParam  String id,
            Authentication authentication){
        return officeOrganogramService.getDescendentOfficesWithUnits(id,authentication);
    }
    // todo: need to do later
    @RequestMapping(value="/getSOOrganogram",method = RequestMethod.POST)
    public List<TreeNodeOfficerDTO> getSOOrganogram(
            @RequestParam String id,
            Authentication authentication){
       return officeOrganogramService.getSOOrganogram(id,authentication);
    }

    @RequestMapping(value="/findOfficeUnitOrganogramByOfficeOriginUnitOrgIdAndOfficeId",method = RequestMethod.POST)
    public List<OfficeUnitOrganogram> findOfficeUnitOrganogramByOfficeOriginUnitOrgIdAndOfficeId(
            @RequestParam Long officeOriginUnitOrgId,
            @RequestParam Long officeId){
        return officeOrganogramService.findOfficeUnitOrganogramByOfficeOriginUnitOrgIdAndOfficeId(officeOriginUnitOrgId,officeId);
    }

    @RequestMapping(value="/getOfficeOriginUnitOrganogramsByOfficeOriginId/{officeOriginId}",method = RequestMethod.GET)
    public List<OfficeOriginUnitOrganogramDTO> getOfficeOriginUnitOrganogramsByOfficeOriginId(
            @PathVariable("officeOriginId") Long officeOriginId){
        return officeOrganogramService.getOfficeOriginUnitOrganogramsByOfficeOriginId(officeOriginId);
    }

}
