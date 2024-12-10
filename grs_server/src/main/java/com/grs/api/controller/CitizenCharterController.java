package com.grs.api.controller;

import com.grs.api.model.response.*;
import com.grs.core.domain.grs.CitizenCharter;
import com.grs.core.domain.grs.ServiceOrigin;
import com.grs.core.service.CitizenCharterService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/citizenCharter")
public class CitizenCharterController {

    private final CitizenCharterService citizenCharterService;

    @RequestMapping(value = "/getCitizenCharterByOfficeCitizenCharterId/{officeCitizenCharterId}", method = RequestMethod.GET)
    public CitizenCharter findOne(@PathVariable("officeCitizenCharterId") Long officeCitizenCharterId){
        return citizenCharterService.findOne(officeCitizenCharterId);
    }

    @RequestMapping(value = "/findByOfficeAndService/{officeId}", method = RequestMethod.POST)
    public CitizenCharter findByOfficeAndService(@PathVariable("officeId") Long officeId, @RequestBody ServiceOrigin serviceOrigin){
        return citizenCharterService.findByOfficeAndService(officeId, serviceOrigin);
    }

    @RequestMapping(value = "/validateServiceOriginInput", method = RequestMethod.POST)
    public GenericResponse validateServiceOriginInput(@RequestBody ServiceOriginDTO serviceOriginDTO){
        return citizenCharterService.validateServiceOriginInput(serviceOriginDTO);
    }
    @RequestMapping(value = "/saveService", method = RequestMethod.POST)
    public Boolean  saveService(@RequestBody ServiceOriginDTO serviceOriginDTO){
        return citizenCharterService.saveService(serviceOriginDTO);
    }
    @RequestMapping(value = "/saveServiceOrigin", method = RequestMethod.POST)
    public GenericResponse saveServiceOrigin(@RequestBody ServiceOriginDTO serviceOriginDTO){
        return citizenCharterService.saveServiceOrigin(serviceOriginDTO);
    }
    @RequestMapping(value = "/getCitizenCharterListByServiceOriginId/{serviceOriginId}", method = RequestMethod.POST)
    public List<ServiceStatusInOfficeDTO> getCitizenCharterListByServiceOriginId(@PathVariable("serviceOriginId") Long serviceOriginId){
        return citizenCharterService.getCitizenCharterListByServiceOriginId(serviceOriginId);
    }
    @RequestMapping(value = "/updateServiceUserOfficesStatus", method = RequestMethod.POST)
    public Boolean updateServiceUserOfficesStatus(@RequestBody List<ItemIdStatusDTO> idStatusList){
        return citizenCharterService.updateServiceUserOfficesStatus(idStatusList);
    }
    @RequestMapping(value = "/findByOfficeIdAndServiceId", method = RequestMethod.POST)
    public CitizenCharter findByOfficeIdAndServiceId(
            @RequestParam Long officeId,
            @RequestParam Long serviceId
    ){
        return citizenCharterService.findByOfficeIdAndServiceId(officeId, serviceId);
    }
    @RequestMapping(value = "/saveCitizenCharter", method = RequestMethod.POST)
    public CitizenCharter saveCitizenCharter(@RequestBody CitizenCharter citizenCharter){
        return citizenCharterService.saveCitizenCharter(citizenCharter);
    }

}
