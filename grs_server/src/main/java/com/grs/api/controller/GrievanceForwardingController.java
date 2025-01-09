package com.grs.api.controller;

import com.grs.api.model.GrievanceForwardingDTO;
import com.grs.api.model.UserInformation;
import com.grs.api.model.request.*;
import com.grs.api.model.response.GenericResponse;
import com.grs.api.model.response.GrievanceForwardingEmployeeRecordsDTO;
import com.grs.api.model.response.OpinionReceiverDTO;
import com.grs.api.model.response.UnseenCountDTO;
import com.grs.api.model.response.file.ExistingFileDerivedDTO;
import com.grs.api.model.response.file.FileDerivedDTO;
import com.grs.api.model.response.grievanceForwarding.GrievanceForwardingInvestigationDTO;
import com.grs.core.dao.GrievanceForwardingDAO;
import com.grs.core.domain.grs.Grievance;
import com.grs.core.domain.grs.GrievanceForwarding;
import com.grs.core.model.EmployeeOrganogram;
import com.grs.core.service.GrievanceForwardingService;
import com.grs.core.service.GrievanceService;
import com.grs.utils.Utility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.validation.Valid;
import java.util.Date;
import java.util.List;

/*
 *  Has old code
 */

@RestController
public class GrievanceForwardingController {
    @Autowired
    private GrievanceForwardingService grievanceForwardingService;
    @Autowired
    private GrievanceForwardingDAO grievanceForwardingDAO;
    @Autowired
    private GrievanceService grievanceService;

    @RequestMapping(value = "/api/grievance/forward", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public GenericResponse forwardGrievanceToAnotherOffice(Authentication authentication, @RequestBody ForwardToAnotherOfficeDTO forwardToAnotherOfficeDTO) {
        UserInformation userInformation = Utility.extractUserInformationFromAuthentication(authentication);
        return this.grievanceForwardingService.forwardGrievanceToAnotherOffice(forwardToAnotherOfficeDTO, userInformation);
    }
    @RequestMapping(value = "/api/grievance/forwardall/{grievanceId}", method = RequestMethod.GET)
    public List<GrievanceForwardingEmployeeRecordsDTO> searchAllComplaintMovements(@PathVariable("grievanceId") Long grievanceId) {
        return this.grievanceForwardingService.searchAllComplaintMovementHistoryByGrievance(grievanceId);
    }

    @RequestMapping(value = "/api/grievance/forward/{grievanceId}", method = RequestMethod.GET)
    public List<GrievanceForwardingEmployeeRecordsDTO> getAllComplaintMovements(@PathVariable("grievanceId") Long grievanceId, Authentication authentication) {
        return this.grievanceForwardingService.getAllComplaintMovementHistoryByGrievance(grievanceId, authentication);
    }

    @RequestMapping(value = "/api/grievance/forward/complainant/{grievanceId}", method = RequestMethod.GET)
    public List<GrievanceForwardingEmployeeRecordsDTO> getAllComplainantComplaintMovements(@PathVariable("grievanceId") Long grievanceId, Authentication authentication) {
        return this.grievanceForwardingService.getAllComplainantComplaintMovementHistoryByGrievance(grievanceId, authentication);
    }

    @PostMapping(value = "/api/grievance/forward/complainant/complaintMovementHistoryByTrackingNumber")
    public List<GrievanceForwardingEmployeeRecordsDTO> getAllComplainantComplaintMovementHistoryByTrackingNumber(
            @RequestParam String trackingNumber) {
        return grievanceForwardingService.getAllComplainantComplaintMovementHistoryByTrackingNumber(trackingNumber);
    }

    @RequestMapping(value = "/api/grievance/forward/{grievanceId}/files", method = RequestMethod.GET)
    public List<FileDerivedDTO> getAllComplainantComplaintMovementsFiles(@PathVariable("grievanceId") Long grievanceId, Authentication authentication) {
        return this.grievanceForwardingService.getAllComplaintMovementAttachedFiles(authentication, grievanceId);
    }

    @RequestMapping(value = "/api/grievance/forward/reject/{grievanceId}", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE)
    public GenericResponse rejectGrievance(Authentication authentication, @PathVariable("grievanceId") Long grievanceId, @RequestBody GrievanceForwardingNoteDTO comment) {
        UserInformation userInformation = Utility.extractUserInformationFromAuthentication(authentication);
        return this.grievanceForwardingService.rejectGrievance(userInformation, comment);
    }

    @RequestMapping(value = "/api/grievance/forward/close/{grievanceId}", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE)
    public GenericResponse closeGrievance(Authentication authentication, @PathVariable("grievanceId") Long grievanceId, @RequestBody GrievanceForwardingCloseDTO grievanceForwardingCloseDTO) {
        return this.grievanceForwardingService.closeGrievance(authentication, grievanceId, grievanceForwardingCloseDTO);
    }

    @RequestMapping(value = "/api/grievance/forward/opinion/receiver/{grievance_id}", method = RequestMethod.GET)
    public OpinionReceiverDTO getOpinionReceiverFields(Authentication authentication, @PathVariable("grievance_id") Long grievanceId, @RequestParam("postNodeId") String postNodeId) {
        return grievanceForwardingService.getOpinionFieldsByGrievance(grievanceId, authentication, postNodeId);
    }

    @RequestMapping(value = "/api/grievance/forward/opinion", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public GenericResponse sendForOpinion(Authentication authentication, @Valid @RequestBody OpinionRequestDTO grievanceOpinionRequestDTO) {
        /* Old Logic

        if (!(grievanceOpinionRequestDTO.getPostNode() != null
                && !grievanceOpinionRequestDTO.getPostNode().isEmpty()
                && grievanceOpinionRequestDTO.getPostNode().size() <= 1
                && grievanceOpinionRequestDTO.getPostNode().get(0) != null)) {
            return new GenericResponse(false, "অনুগ্রহ করে মতামতের জন্য অন্ততপক্ষে যে কোন একজনকে নির্বাচন করুন");
        }

        */

        if (!(grievanceOpinionRequestDTO.getPostNode() != null
                && grievanceOpinionRequestDTO.getPostNode().size() == 1
                && grievanceOpinionRequestDTO.getPostNode().get(0) != null)) {
            return new GenericResponse(false, "অনুগ্রহ করে মতামতের জন্য অন্ততপক্ষে যে কোন একজনকে নির্বাচন করুন");
        }

        if (grievanceOpinionRequestDTO.getCcNode() != null) {
            List<String> ccNodeList = grievanceOpinionRequestDTO.getCcNode();
            for (String ccNode : ccNodeList) {
                if (ccNode.equals(grievanceOpinionRequestDTO.getPostNode().get(0))) {
                    return new GenericResponse(false, "অনুগ্রহ করে প্রধান প্রাপক ব্যতীত অন্য একজনকে অনুলিপি প্রাপক হিসেবে নির্বাচন করুন");
                }
            }
        }
        return this.grievanceForwardingService.sendForOpinion(authentication, grievanceOpinionRequestDTO);

    }

    @RequestMapping(value = "/api/grievance/forward/appeal/officer/send", method = RequestMethod.POST)
    public GenericResponse sendToAppealOfficer(@RequestBody GrievanceForwardingNoteDTO grievanceForwardingNoteDTO, Authentication authentication) {
        return this.grievanceForwardingService.sendToAppealOfficer(authentication, grievanceForwardingNoteDTO);
    }

    @RequestMapping(value = "api/grievance/forward/appeal", method = RequestMethod.POST)
    public GenericResponse appealToOfficer(Authentication authentication, @RequestBody GrievanceForwardingNoteDTO grievanceForwardingNoteDTO) {
        return this.grievanceForwardingService.appealToOfficer(grievanceForwardingNoteDTO, authentication);
    }

    @RequestMapping(value = "api/grievance/forward/opinion/reply", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public GenericResponse giveOpinion(@Valid @RequestBody OpinionRequestDTO opinionRequestDTO, Authentication authentication){
        return this.grievanceForwardingService.giveOpinion(authentication, opinionRequestDTO);
    }

    @RequestMapping(value = "api/grievance/forward/permission/ask", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public GenericResponse askPermission(@Valid @RequestBody GrievanceForwardingNoteDTO grievanceForwardingNoteDTO, Authentication authentication){
        return this.grievanceForwardingService.askPermission(authentication, grievanceForwardingNoteDTO);
    }

    @RequestMapping(value = "api/grievance/forward/permission/give", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public GenericResponse givePermission(@Valid @RequestBody GrievanceForwardingNoteDTO grievanceForwardingNoteDTO, Authentication authentication){
        return this.grievanceForwardingService.givePermission(authentication, grievanceForwardingNoteDTO);
    }

    @RequestMapping(value = "api/grievance/forward/guidance/service", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public GenericResponse giveGuidanceToGiveService(@Valid @RequestBody GrievanceForwardingGuidanceForServiceDTO grievanceForwardingGuidanceForServiceDTO, Authentication authentication){
        return this.grievanceForwardingService.giveGuidanceToGiveService(authentication, grievanceForwardingGuidanceForServiceDTO);
    }

    @RequestMapping(value = "api/grievance/forward/department/action", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public GenericResponse giveRecommendDepartmentalAction(@Valid @RequestBody GrievanceForwardingNoteDTO grievanceForwardingNoteDTO, Authentication authentication){
        return this.grievanceForwardingService.giveRecommendDepartmentalAction(authentication, grievanceForwardingNoteDTO.getGrievanceId(), grievanceForwardingNoteDTO.getNote());
    }

    @RequestMapping(value = "/api/grievance/forward/report/agreement", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public GenericResponse confirmReport(@RequestBody GrievanceForwardingInvestigationComment grievanceForwardingMessageDTO, Authentication authentication){
        return this.grievanceForwardingService.confirmReport(authentication, grievanceForwardingMessageDTO);
    }

    @RequestMapping(value = "/api/office/organogram/sub", method = RequestMethod.GET)
    public void getRootOfAOSubOffice(Authentication authentication, @RequestParam("id") Long id) {
         grievanceForwardingService.getRootOfAOSubOffice(id, authentication);
    }

    @RequestMapping(value = "/api/gro/history/{grievanceId}", method =  RequestMethod.GET)
    public List<GrievanceForwardingEmployeeRecordsDTO> getGroHistory(@PathVariable("grievanceId") Long grievanceId){
        return this.grievanceForwardingService.getGroHistory(grievanceId);
    }

    @RequestMapping(value = "/api/gro/recentmostGrievanceForwarding/{grievanceId}", method =  RequestMethod.GET)
    public EmployeeOrganogram getGroOfRecentmostGrievanceForwarding(@PathVariable("grievanceId") Long grievanceId) {
        return this.grievanceForwardingService.getGroOfRecentmostGrievanceForwarding(grievanceId);
    }

    @GetMapping(value = "/api/file/list/{grievanceId}")
    public List<ExistingFileDerivedDTO> getAllFilesList(
            @PathVariable("grievanceId") Long grievanceId,
            Authentication authentication) {
        return this.grievanceForwardingService.getAllFilesList(grievanceId,authentication);

    }

    @RequestMapping(value = "/api/grievance/inspection/initiate", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public GenericResponse initiateInvestigation(Authentication authentication, @Valid @RequestBody GrievanceForwardingInvestigationDTO grievanceForwardingInvestigationDTO) {
        return this.grievanceForwardingService.initiateInvestigation(grievanceForwardingInvestigationDTO, authentication);
    }

    @RequestMapping(value = "/api/grievance/inspection/notice/inspector/file", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public GenericResponse requestAdditionalEvidenceFile(Authentication authentication, @Valid @RequestBody InvestigationMaterialHearingDTO investigationMaterialHearingDTO) {
        return this.grievanceForwardingService.requestEvidences(investigationMaterialHearingDTO, authentication);
    }

    @RequestMapping(value = "/api/grievance/inspection/notice/inspector/hearing", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public GenericResponse askForHearing(Authentication authentication, @Valid @RequestBody InvestigationMaterialHearingDTO investigationMaterialHearingDTO) {
        return this.grievanceForwardingService.askForHearing(investigationMaterialHearingDTO, authentication);
    }

    @RequestMapping(value = "/api/grievance/inspection/take/hearing", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public GenericResponse takeHearing(Authentication authentication, @Valid @RequestBody GrievanceForwardingNoteDTO grievanceForwardingNoteDTO) {
        return this.grievanceForwardingService.takeHearing(grievanceForwardingNoteDTO, authentication);
    }

    @RequestMapping(value = "/api/grievance/inspection/notice/complainant/hearing", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public GenericResponse joinHearingComplainant(Authentication authentication, @Valid @RequestBody GrievanceForwardingNoteDTO grievanceForwardingNoteDTO) {
        return this.grievanceForwardingService.joinHearing(grievanceForwardingNoteDTO, authentication);
    }

    @RequestMapping(value = "/api/grievance/inspection/notice/complainant/file", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public GenericResponse saveComplaintGivenFile(Authentication authentication, @Valid @RequestBody GrievanceForwardingNoteDTO grievanceForwardingNoteDTO) {
        return this.grievanceForwardingService.addFileTransitToHearing(grievanceForwardingNoteDTO, authentication);
    }

    @RequestMapping(value = "/api/grievance/inspection/report", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public GenericResponse submitInspectionReport(Authentication authentication, @Valid @RequestBody GrievanceForwardingNoteDTO grievanceForwardingReportDTO) {
        return this.grievanceForwardingService.investigationReportSubmission(grievanceForwardingReportDTO, authentication);
    }

    @RequestMapping(value = "/api/grievance/forward/last/{grievanceId}", method = RequestMethod.GET)
    public GrievanceForwardingDTO getLatestForwardingEntry(@PathVariable("grievanceId") Long id){
        return this.grievanceForwardingService.getLatestForwardingEntry(id);
    }

    @RequestMapping(value = "/api/grievance/forward/investigation/head/{grievanceId}", method = RequestMethod.GET)
    public Object getInvestigationHeadEntry(@PathVariable("grievanceId") Long id){
        return this.grievanceForwardingService.getInvestigationHeadEntry(id);
    }

    @RequestMapping(value = "/api/test", method = RequestMethod.GET)
    public Object test(Authentication authentication){
        UserInformation userInformation = Utility.extractUserInformationFromAuthentication(authentication);
        String url = "http://doptor.gov.bd:8090/identity/designation/" +
                userInformation.getOfficeInformation().getOfficeUnitOrganogramId() +
                "/apps";
        RestTemplate restTemplate = new RestTemplate();

        ResponseEntity<Object> responseEntity = restTemplate.getForEntity(url,Object.class);
        return responseEntity.getBody();
    }

    @RequestMapping(value = "/api/grievance/retrieve/{grievanceId}", method = RequestMethod.PUT)
    public GenericResponse retrieveRejectedComplaint(@PathVariable("grievanceId") Long grievanceId){
        Boolean complaintRevived = this.grievanceForwardingService.retrieveRejectedComplaint(grievanceId);
        return GenericResponse.builder().message("Retrieved").success(complaintRevived).build();
    }
    @RequestMapping(value = "/api/grievance/retake/{grievanceId}", method = RequestMethod.PUT)
    public GenericResponse retakeTimeExpiredComplaint(@PathVariable("grievanceId") Long grievanceId, Authentication authentication){
        Boolean complaintRevived = this.grievanceForwardingService.retakeTimeExpiredComplaint(grievanceId, authentication);
        return GenericResponse.builder().message("Retrieved").success(complaintRevived).build();
    }

    @PostMapping(value = "/api/grievance/unseenCountForUser")
    public UnseenCountDTO getUnseenCountForUser(
            Authentication authentication,
            @RequestParam String inboxType) {
        return grievanceForwardingService.getUnseenCountForUser(authentication,inboxType);

    }

    @PostMapping(value = "/api/grievance/totalCountForUser")
    public UnseenCountDTO getTotalCountForUser(
            Authentication authentication,
            @RequestParam String inboxType) {
        return grievanceForwardingService.getTotalCountForUser(authentication,inboxType);
    }



    @GetMapping("/api/grievanceforwarding/getLastForwadingForGivenGrievance/{grievanceId}")
    public GrievanceForwarding getLastForwadingForGivenGrievance(@PathVariable Long grievanceId){

        return grievanceForwardingDAO.getLastForwadingForGivenGrievance(grievanceService.findGrievanceById(grievanceId));

    }

    @GetMapping("/api/grievanceforwarding/findByGrievanceAndIsCurrent/{grievanceId}")
    public List<GrievanceForwarding> findByGrievanceAndIsCurrent(@PathVariable Long grievanceId){

        return grievanceForwardingDAO.findByGrievanceAndIsCurrent(grievanceService.findGrievanceById(grievanceId), true);

    }

    @GetMapping("/api/grievanceforwarding/getAllComplaintMovementByGrievance/{grievanceId}")
    public List<GrievanceForwarding> getAllComplaintMovementByGrievance(@PathVariable Long grievanceId) {

        return grievanceService.getAllComplaintMovementByGrievance(grievanceService.findGrievanceById(grievanceId));

    }

    @GetMapping("/api/grievanceforwarding/getLastClosedOrRejectedForwarding/{grievanceId}")
    public GrievanceForwarding getLastClosedOrRejectedForwarding(@PathVariable Long grievanceId) {

        return grievanceForwardingDAO.getLastClosedOrRejectedForwarding(grievanceService.findGrievanceById(grievanceId));

    }


    @GetMapping("/api/grievanceforwarding/getByActionAndToOfficeIdAndToOfficeUnitOrganogramIdAndGrievance/{grievanceId}/{officeId}/{officeUnitOrganogramId}/{action}")
    public GrievanceForwarding getByActionAndToOfficeIdAndToOfficeUnitOrganogramIdAndGrievance(
            @PathVariable Long grievanceId,
            @PathVariable Long officeId,
            @PathVariable Long officeUnitOrganogramId,
            @PathVariable String action
    ){

        return grievanceForwardingDAO.getByActionAndToOfficeIdAndToOfficeUnitOrganogramIdAndGrievance(grievanceService.findGrievanceById(grievanceId), officeId, officeUnitOrganogramId, action);

    }

    @GetMapping("/api/grievanceforwarding/findByGrievanceAndActionLikeOrderByIdDesc/{grievanceId}/{action}")
    public List<GrievanceForwarding> findByGrievanceAndActionLikeOrderByIdDesc(
            @PathVariable Long grievanceId,
            @PathVariable String action){

        return grievanceForwardingDAO.findByGrievanceAndActionLikeOrderByIdDesc(grievanceService.findGrievanceById(grievanceId), action);

    }

    @GetMapping("/api/grievanceforwarding/findByGrievanceAndActionLikeAndCurrentStatusLike/{grievanceId}/{action}/{status}")
    public GrievanceForwarding findByGrievanceAndActionLikeAndCurrentStatusLike(
            @PathVariable Long grievanceId,
            @PathVariable String action,
            @PathVariable  String status){


        return grievanceForwardingDAO.findByGrievanceAndActionLikeAndCurrentStatusLike(grievanceService.findGrievanceById(grievanceId), action, status);

    }

    @GetMapping("/api/grievanceforwarding/findByGrievanceAndActionLikeAndCurrentStatusNotLike/{grievanceId}/{action}/{status}")
    public GrievanceForwarding findByGrievanceAndActionLikeAndCurrentStatusNotLike(
            @PathVariable Long grievanceId,
            @PathVariable String action,
            @PathVariable  String status){


        return grievanceForwardingDAO.findByGrievanceAndActionLikeAndCurrentStatusNotLike(grievanceService.findGrievanceById(grievanceId), action, status);


    }


    @PostMapping("/api/grievanceforwarding/getAllRelatedComplaintMovementsBetweenDates")
    public List<GrievanceForwarding> getAllRelatedComplaintMovementsBetweenDates(
            @RequestParam Long grievanceId,
            @RequestParam Long officeId,
            @RequestParam List<Long> officeUnitOrganogramId,
            @RequestParam String action,
            @RequestParam Date start,
            @RequestParam Date finish
    ){


        return grievanceForwardingDAO.getAllRelatedComplaintMovementsBetweenDates(grievanceId, officeId, officeUnitOrganogramId, action, start, finish);

    }

    @PostMapping("/api/grievanceforwarding/getAllRelatedComplaintMovements")
    public List<GrievanceForwarding> getAllRelatedComplaintMovements(
            @RequestParam("grievanceId") Long grievanceId,
            @RequestParam("officeId") Long officeId,
            @RequestParam("officeUnitOrganogramId") List<Long> officeUnitOrganogramId,
            @RequestParam("action") String action
    ) {
        return grievanceForwardingDAO.getAllRelatedComplaintMovements(
                grievanceId, officeId, officeUnitOrganogramId, action
        );
    }

    @GetMapping("/api/grievanceforwarding/getLastActiveGrievanceForwardingOfCurrentUser/{grievanceId}/{officeId}/{officeUnitOrganogramId}")
    public GrievanceForwarding getLastActiveGrievanceForwardingOfCurrentUser(
            @PathVariable Long grievanceId,
            @PathVariable Long officeId,
            @PathVariable Long officeUnitOrganogramId){

        return grievanceForwardingDAO.getLastActiveGrievanceForwardingOfCurrentUser(grievanceService.findGrievanceById(grievanceId), officeId, officeUnitOrganogramId);

    }

    @PostMapping("/api/grievanceforwarding/update")
    public GrievanceForwarding saveGrievanceForwardingHistory(
            @RequestBody GrievanceForwarding grievanceForwarding){

        return grievanceForwardingDAO.save(grievanceForwarding);
    }

    @GetMapping("/api/getComplaintRetakeFlag/{id}")
    public boolean getComplaintRetakeFlag(@PathVariable Long id, Authentication authentication){
        return grievanceForwardingService.getComplaintRetakeFlag(id, authentication);
    }

    @PostMapping("/api/updateForwardSeenStatus/{id}")
    public void updateForwardSeenStatus(@PathVariable Long id, @RequestBody UserInformation userInformation){
        grievanceForwardingService.updateForwardSeenStatus(userInformation, id);
    }

    @PostMapping("/api/getAllUserRelatedForwardings/{id}")
    public List<GrievanceForwarding> getAllUserRelatedForwardings(@PathVariable Long id, @RequestBody UserInformation userInformation){
        return grievanceForwardingService.getAllUserRelatedForwardings(
                grievanceService.findGrievanceById(id), userInformation
        );
    }
    @PostMapping("/api/getCountOfComplaintMovementAttachedFiles/{id}")
    public Integer getCountOfComplaintMovementAttachedFiles(@PathVariable Long id, @RequestBody UserInformation userInformation){
        return grievanceForwardingService.getCountOfComplaintMovementAttachedFiles(userInformation, id);
    }
}
