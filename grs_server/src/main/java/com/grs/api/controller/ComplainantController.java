package com.grs.api.controller;

import com.grs.api.model.UserInformation;
import com.grs.api.model.request.BlacklistRequestBodyDTO;
import com.grs.api.model.request.ComplainantDTO;
import com.grs.api.model.request.PasswordChangeDTO;
import com.grs.api.model.response.ComplainantResponseDTO;
import com.grs.api.model.response.UserDetailsDTO;
import com.grs.api.model.response.grievance.ComplainantInfoBlacklistReqDTO;
import com.grs.api.model.response.grievance.ComplainantInfoDTO;
import com.grs.api.model.response.grievance.GrievanceDTO;
import com.grs.api.model.response.grievance.GrievanceShortDTO;
import com.grs.api.model.wrapperDTO.BlacklistRequestPayload;
import com.grs.api.model.wrapperDTO.UpdateComplainantPasswordPayload;
import com.grs.core.domain.grs.Blacklist;
import com.grs.core.domain.grs.Complainant;
import com.grs.core.domain.grs.SuperAdmin;
import com.grs.core.domain.projapoti.Office;
import com.grs.core.service.ComplainantService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api/complainantService")
@RequiredArgsConstructor
public class ComplainantController {

    private final ComplainantService complainantService;

    @RequestMapping(value = "/findComplainantByPhoneNumber", method = RequestMethod.POST)
    public Complainant findComplainantByPhoneNumber(
            @RequestParam("phoneNumber") String phoneNumber
    ) {
        return complainantService.findComplainantByPhoneNumber(phoneNumber);
    }

    @RequestMapping(value = "/countAll", method = RequestMethod.GET)
    public Long countAll() {
        return complainantService.countAll();
    }

    @RequestMapping(value = "/findOne/{id}", method = RequestMethod.GET)
    public Complainant findOne(@PathVariable("id") Long id) {
        return complainantService.findOne(id);
    }

    @RequestMapping(value = "/getRandomPinNumber", method = RequestMethod.GET)
    public String getRandomPinNumber() {
        return complainantService.getRandomPinNumber();
    }

    @RequestMapping(value = "/getRandomPinNumberEightDigit", method = RequestMethod.GET)
    public String getRandomPinNumberEightDigit() {
        return complainantService.getRandomPinNumberEightDigit();
    }

    @RequestMapping(value = "/insertComplainant", method = RequestMethod.POST)
    public Complainant insertComplainant(@RequestBody ComplainantDTO complainantDTO) {
        return complainantService.insertComplainant(complainantDTO);
    }

    @RequestMapping(value = "/insertComplainantWithoutLogin", method = RequestMethod.POST)
    public Complainant insertComplainantWithoutLogin(@RequestBody ComplainantDTO complainantDTO) {
        try {
            return complainantService.insertComplainantWithoutLogin(complainantDTO);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return null;
    }

    @RequestMapping(value = "/getComplainantInfo/{id}", method = RequestMethod.GET)
    public ComplainantInfoDTO getComplainantInfo(@PathVariable("id") Long id) {
        return complainantService.getComplainantInfo(id);
    }

    @RequestMapping(value = "/convertToComplainantResponseDTO", method = RequestMethod.POST)
    public ComplainantResponseDTO convertToComplainantResponseDTO(@RequestBody Complainant complainant) {
        return complainantService.convertToComplainantResponseDTO(complainant);
    }

    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public Complainant save(@RequestBody Complainant complainant) {
        return complainantService.save(complainant);
    }

    @RequestMapping(value = "/doBlacklistByComplainantId", method = RequestMethod.POST)
    public boolean doBlacklistByComplainantId(
            @RequestParam("complainantId") Long complainantId,
            @RequestParam("officeId") Long officeId
    ) {
        return complainantService.doBlacklistByComplainantId(complainantId, officeId);
    }

    @RequestMapping(value = "/doBlacklistRequestByComplainantId", method = RequestMethod.POST)
    public boolean doBlacklistRequestByComplainantId(
            @RequestBody BlacklistRequestPayload blacklistRequestPayload
    ) {
        BlacklistRequestBodyDTO blacklistRequestBodyDTO = blacklistRequestPayload.getBlacklistRequestBodyDTO();
        UserInformation userInformation = blacklistRequestPayload.getUserInformation();

        return complainantService.doBlacklistRequestByComplainantId(blacklistRequestBodyDTO, userInformation);
    }

    @RequestMapping(value = "/doBlacklistRequestByComplainantId/{complainantId}", method = RequestMethod.GET)
    public boolean doBlacklistRequestByComplainantId(
            @PathVariable("complainantId") Long complainantId,
            @RequestBody UserInformation userInformation
    ) {
        return complainantService.doBlacklistRequestByComplainantId(complainantId, userInformation);
    }

    // todo: how to handle auth?
    @RequestMapping(value = "/isBlacklistedUser", method = RequestMethod.GET)
    public Boolean isBlacklistedUser(
            Authentication authentication
    ){
        return complainantService.isBlacklistedUser(authentication);
    }

    @RequestMapping(value = "/isBlacklistedUserByComplainantId/{complainantId}", method = RequestMethod.GET)
    public Boolean isBlacklistedUserByComplainantId(@PathVariable("complainantId") Long complainantId){
        return complainantService.isBlacklistedUserByComplainantId(complainantId);
    }

    @RequestMapping(value = "/getBlacklistByOfficeId/{officeId}", method = RequestMethod.GET)
    public List<ComplainantInfoDTO> getBlacklistByOfficeId(@PathVariable("officeId") Long officeId){
        return complainantService.getBlacklistByOfficeId(officeId);
    }

    @RequestMapping(value = "/getBlacklistRequestByChildOffices", method = RequestMethod.POST)
    public List<ComplainantInfoBlacklistReqDTO> getBlacklistRequestByChildOffices(
            @RequestParam("officeId") Long officeId,
            @RequestParam("officeUnitOrganogramId") Long officeUnitOrganogramId
    ) {
        return complainantService.getBlacklistRequestByChildOffices(officeId, officeUnitOrganogramId);
    }

    @RequestMapping(value = "/doUnBlacklistByComplainantId", method = RequestMethod.POST)
    public boolean doUnBlacklistByComplainantId(
            @RequestParam("complainantId") Long complainantId,
            @RequestParam("officeId") Long officeId
    ) {
        return complainantService.doUnBlacklistByComplainantId(complainantId, officeId);
    }

    @RequestMapping(value = "/doUnBlacklistRequestByComplainantId", method = RequestMethod.POST)
    public boolean doUnBlacklistRequestByComplainantId(
            @RequestParam("complainantId") Long complainantId,
            @RequestParam("officeId") Long officeId
    ) {
        return complainantService.doUnBlacklistRequestByComplainantId(complainantId, officeId);
    }

    @RequestMapping(value = "/getComplaintDTO/{id}", method = RequestMethod.GET)
    public ComplainantDTO getComplaintDTO(@PathVariable("id") Long id) {
        return complainantService.getComplaintDTO(id);
    }

    @RequestMapping(value = "/findBlacklistedOffices/{complainantId}", method = RequestMethod.GET)
    public List<Long> findBlacklistedOffices(@PathVariable("complainantId") Long complainantId) {
        return complainantService.findBlacklistedOffices(complainantId);
    }

    @RequestMapping(value = "/updateComplainantPassword", method = RequestMethod.POST)
    public Boolean updateComplainantPassword(
            @RequestBody UpdateComplainantPasswordPayload updateComplainantPassword
    ) {
        UserInformation userInformation = updateComplainantPassword.getUserInformation();
        PasswordChangeDTO passwordChangeDTO = updateComplainantPassword.getPasswordChangeDTO();

        return complainantService.updateComplainantPassword(userInformation, passwordChangeDTO);
    }

    @RequestMapping(value = "/getPaginatedUsersData", method = RequestMethod.POST)
    public Page<UserDetailsDTO> getPaginatedUsersData(
            @RequestBody Pageable pageable
    ) {
        return complainantService.getPaginatedUsersData(pageable);
    }
}