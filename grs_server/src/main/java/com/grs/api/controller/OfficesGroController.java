package com.grs.api.controller;

import com.grs.api.model.response.OfficesGroDTO;
import com.grs.core.dao.OfficesGroDAO;
import com.grs.core.domain.grs.OfficesGRO;
import com.grs.core.domain.projapoti.*;
import com.grs.core.service.OfficesGroService;
import com.grs.utils.Constant;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/officegro")
@RequiredArgsConstructor
public class OfficesGroController {

    private final OfficesGroService officesGroService;
    private final OfficesGroDAO officesGroDAO;

    @RequestMapping(value = "/getAncestors/{officeId}", method = RequestMethod.GET)
    public List<OfficesGRO> getAncestors(@PathVariable("officeId") Long officeId) {
        if (officeId == null) {
            return new ArrayList<>();
        }
        return officesGroService.getAncestors(officeId);
    }

    @RequestMapping(value = "/findOfficesGroByOfficeId/{id}", method = RequestMethod.GET)
    public OfficesGRO findOfficesGroByOfficeId(@PathVariable("id") Long id) {
        if (id == null) {
            return null;
        }
        return officesGroService.findOfficesGroByOfficeId(id);
    }

    @RequestMapping(value = "/findActiveOffices", method = RequestMethod.GET)
    public List<OfficesGRO> findActiveOffices() {
        return officesGroDAO.findActiveOffices();
    }

    @RequestMapping(value = "/findOfficesGroByMissingOfficerType", method = RequestMethod.POST)
    public List<OfficesGRO> findOfficesGroByMissingOfficerType(
            @RequestParam("missingOfficerType") Long missingOfficerType,
            @RequestParam("officeLayers") Long officeLayers,
            @RequestParam("firstSelection") Long firstSelection,
            @RequestParam("secondSelection") Long secondSelection
    ) {
        return officesGroService.findOfficesGroByMissingOfficerType(
                missingOfficerType,
                officeLayers,
                firstSelection,
                secondSelection
        );
    }

    @RequestMapping(value = "/findByAppealOfficeUnitOrganogramId/{officeUnitOrganogramId}", method = RequestMethod.GET)
    public List<OfficesGRO> findByAppealOfficeUnitOrganogramId(
            @PathVariable("officeUnitOrganogramId") Long officeUnitOrganogramId
    ) {
        return officesGroService.findByAppealOfficeUnitOrganogramId(officeUnitOrganogramId);
    }

    @RequestMapping(value = "/findByAdminOfficeUnitOrganogramId/{officeUnitOrganogramId}", method = RequestMethod.GET)
    public List<OfficesGRO> findByAdminOfficeUnitOrganogramId(@PathVariable("officeUnitOrganogramId") Long officeUnitOrganogramId) {
        return this.officesGroService.findByAdminOfficeUnitOrganogramId(officeUnitOrganogramId);
    }

    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public OfficesGRO save(
            @RequestBody OfficesGRO officesGRO
    ) {
        return officesGroService.save(officesGRO);
    }

    @RequestMapping(value = "/findAll", method = RequestMethod.GET)
    public List<OfficesGRO> findAll() {
        return officesGroService.findAll();
    }

    @RequestMapping(value = "/findAllOffficeIds", method = RequestMethod.GET)
    public List<Long> findAllOffficeIds() {
        return officesGroService.findAllOffficeIds();
    }

    @RequestMapping(value = "/findAllOfficeIdsIncludingInactive", method = RequestMethod.GET)
    public Set<Long> findAllOfficeIdsIncludingInactive() {
        return officesGroService.findAllOffficeIdsIncludingInactive();
    }

    @RequestMapping(value = "/findAllOfficeOriginIds", method = RequestMethod.GET)
    public List<Long> findAllOfficeOriginIds() {
        return officesGroService.findAllOffficeOriginIds();
    }

    @RequestMapping(value = "/convertToOfficesGroDTO", method = RequestMethod.POST)
    public OfficesGroDTO convertToOfficesGroDTO(
            @RequestBody OfficesGRO officesGRO
    ) {
        return officesGroService.convertToOfficesGroDTO(officesGRO);
    }

    @RequestMapping(value = "/getGRSEnabledOfficeIdFromOfficeIdList", method = RequestMethod.POST)
    public List<Long> getGRSEnabledOfficeIdFromOfficeIdList(
            @RequestBody List<Long> officeIdList
    ) {
        return officesGroService.getGRSEnabledOfficeIdFromOfficeIdList(officeIdList);
    }

    @RequestMapping(value = "/findByAppealOfficer", method = RequestMethod.POST)
    public List<Office> findByAppealOfficer(
            @RequestParam("officeId") Long officeId,
            @RequestParam("officeUnitOrganogramId") Long officeUnitOrganogramId
    ) {
        return officesGroService.findByAppealOfficer(officeId, officeUnitOrganogramId);
    }

    @RequestMapping(value = "/findAll", method = RequestMethod.POST)
    public Page<OfficesGroDTO> findAll(
            @RequestBody Pageable pageable
    ) {
        return officesGroService.findAll(pageable);
    }

    @RequestMapping(value = "/findOne/{id}", method = RequestMethod.GET)
    public OfficesGroDTO findOne(@PathVariable("id") Long id) {
        return officesGroService.findOne(id);
    }

    @RequestMapping(value = "/saveOfficeSetup/{officeId}", method = RequestMethod.POST)
    public OfficesGroDTO saveOfficeSetup(
            @PathVariable("officeId") Long officeId,
            @RequestBody OfficesGroDTO officesGroDTO
    ) {
        return officesGroService.saveOfficeSetup(
                officeId,
                officesGroDTO
        );
    }

    @RequestMapping(value = "/findActiveSetup", method = RequestMethod.POST)
    public Page<OfficesGroDTO> findActiveSetup(
            @RequestBody Pageable pageable
    ) {
        return officesGroService.findActiveSetup(pageable);
    }

    @RequestMapping(value = "/findOfficersByOfficeId/{officeId}", method = RequestMethod.GET)
    public OfficesGroDTO findOfficersByOfficeId(@PathVariable("officeId") Long officeId) {
        return officesGroService.findOfficersByOfficeId(officeId);
    }

    @RequestMapping(value = "/getCurrentlyGrsEnabledOffices", method = RequestMethod.GET)
    public List<OfficesGRO> getCurrentlyGrsEnabledOffices() {
        return officesGroService.getCurrentlyGrsEnabledOffices();
    }
}
