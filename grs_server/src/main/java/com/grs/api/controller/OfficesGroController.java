package com.grs.api.controller;

import com.grs.core.dao.OfficesGroDAO;
import com.grs.core.domain.grs.OfficesGRO;
import com.grs.core.service.OfficesGroService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/officegro")
@RequiredArgsConstructor
public class OfficesGroController {

    private final OfficesGroService officesGroService;
    private final OfficesGroDAO officesGroDAO;

    @RequestMapping(value = "/getAncestors/{officeId}", method = RequestMethod.GET)
    public List<OfficesGRO> getAncestors(@PathVariable("officeId") Long officeId){
        if (officeId == null){
            return new ArrayList<>();
        }
        return officesGroService.getAncestors(officeId);
    }

    @RequestMapping(value = "/findOfficesGroByOfficeId/{id}", method = RequestMethod.GET)
    public OfficesGRO findOfficesGroByOfficeId(@PathVariable("id") Long id){
        if (id == null){
            return null;
        }
        return officesGroService.findOfficesGroByOfficeId(id);
    }
    @RequestMapping(value = "/findActiveOffices", method = RequestMethod.GET)
    public List<OfficesGRO> findActiveOffices(){
        return officesGroDAO.findActiveOffices();
    }
}
