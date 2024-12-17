package com.grs.api.controller;


import com.grs.core.domain.projapoti.OfficeMinistry;
import com.grs.core.service.OfficeMinistryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/officeMinistry")
public class OfficeMinistryController {

    private final OfficeMinistryService officeMinistryService;

    @RequestMapping(value = "/getOfficeMinistry/{id}",method = RequestMethod.GET)
    public OfficeMinistry getOfficeMinistry(@PathVariable("id") Long id){
        return officeMinistryService.getOfficeMinistry(id);
    }
}
