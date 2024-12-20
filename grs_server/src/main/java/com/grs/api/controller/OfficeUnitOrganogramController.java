package com.grs.api.controller;


import com.grs.core.domain.projapoti.OfficeUnitOrganogram;
import com.grs.core.service.OfficeUnitOrganogramService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/officeUnitOrgranogram")
public class OfficeUnitOrganogramController {

    private final OfficeUnitOrganogramService officeUnitOrganogramService;

    @RequestMapping(value = "/getOfficeUnitOrganogramById/{id}", method = RequestMethod.GET)
    public OfficeUnitOrganogram getOfficeUnitOrganogramById(@PathVariable("id") Long id) {
        return officeUnitOrganogramService.getOfficeUnitOrganogramById(id);

    }
    @RequestMapping(value = "/getAdminOrganogram/{officeId}",method = RequestMethod.GET)
    public OfficeUnitOrganogram getAdminOrganogram(@PathVariable("officeId") Long officeId){
        return officeUnitOrganogramService.getAdminOrganogram(officeId);
    }
}
