package com.grs.api.controller;

import com.grs.api.model.UserInformation;
import com.grs.api.model.request.*;
import com.grs.api.model.response.*;
import com.grs.api.model.response.file.ExistingFileDerivedDTO;
import com.grs.api.model.response.file.FileDerivedDTO;

import com.grs.api.model.response.grievance.*;
import com.grs.core.config.CaptchaSettings;

import com.grs.api.model.response.grievance.ComplainantInfoDTO;
import com.grs.api.model.response.grievance.GrievanceDTO;
import com.grs.api.model.response.grievance.GrievanceDetailsDTO;
import com.grs.api.model.response.grievance.OISFIntermediateDashboardDTO;
import com.grs.core.service.SpProgrammeService;

import com.grs.core.dao.GrievanceForwardingDAO;
import com.grs.core.domain.grs.Complainant;
import com.grs.core.domain.grs.Grievance;
import com.grs.core.domain.grs.GrievanceForwarding;
import com.grs.core.domain.grs.Notification;
import com.grs.core.domain.projapoti.EmployeeRecord;
import com.grs.core.model.EmployeeOrganogram;
import com.grs.core.model.ListViewType;
import com.grs.core.service.*;
import com.grs.utils.ListViewConditionOnCurrentStatusGenerator;
import com.grs.utils.Utility;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.*;

/*
 *  Has old code
 */

@Slf4j
@RestController
public class GrievanceController {

    @Autowired
    private GrievanceService grievanceService;
    @Autowired
    private OfficeService officeService;
    @Autowired
    private GrievanceForwardingService grievanceForwardingService;
    @Autowired
    private GrievanceForwardingDAO grievanceForwardingDAO;
    @Autowired
    private MessageService messageService;
    @Autowired
    private NotificationService notificationService;
    @Autowired
    private ComplainantService complainantService;
    @Autowired
    private SpProgrammeService spProgrammeService;

    @RequestMapping(value = "/api/findGrievanceById/{grievanceId}", method = RequestMethod.GET)
    public Grievance findGrievanceById(@PathVariable("grievanceId") Long grievanceId) {
        return this.grievanceService.findGrievanceById(grievanceId);
    }

    @RequestMapping(value = "/api/findCount", method = RequestMethod.POST)
    public Long findCount(
            @RequestParam String sql,
            @RequestBody Map<String, Object> params) {
        return this.grievanceService.findCount(sql, params);
    }

    @RequestMapping(value = "/api/saveGrievance", method = RequestMethod.POST)
    public Grievance saveGrievance(@RequestBody Grievance grievance,
                                   @RequestParam boolean callHistory) {
        return this.grievanceService.saveGrievance(grievance, callHistory);
    }

    @RequestMapping(value = "/api/saveGrievanceList", method = RequestMethod.POST)
    public void SaveGrievancesList(@RequestBody List<Grievance> grievances) {
        this.grievanceService.SaveGrievancesList(grievances);
    }

    @RequestMapping(value = "/api/getEmployeeRecordById/{id}", method = RequestMethod.GET)
    public EmployeeRecord getEmployeeRecordById(@PathVariable("id") Long id) {
        return this.grievanceService.getEmployeeRecordById(id);
    }


    @RequestMapping(value = "/api/convertToGrievanceDTO", method = RequestMethod.POST)
    public GrievanceDTO convertToGrievanceDTO(@RequestBody Grievance grievance) {
        return this.grievanceService.convertToGrievanceDTO(grievance);
    }

    @RequestMapping(value = "/api/getCurrentMonthComplaintsWithRatingsByOfficeIdAndType", method = RequestMethod.POST)
    public List<GrievanceDTO> getCurrentMonthComplaintsWithRatingsByOfficeIdAndType(
            @RequestParam Long officeId,
            @RequestParam Boolean isAppeal) {
        return this.grievanceService.getCurrentMonthComplaintsWithRatingsByOfficeIdAndType(officeId, isAppeal);

    }

    @RequestMapping(value = "/api/grievanceDetails/{id}", method = RequestMethod.GET)
    public GrievanceDetailsDTO getGrievanceDetails(@PathVariable("id") Long id) {
        return this.grievanceService.getGrievanceDetails(id);
    }

    @RequestMapping(value = "/api/complainantInfo", method = RequestMethod.POST)
    public ComplainantInfoDTO getComplainantInfo(@RequestBody Grievance grievance) {
        return this.grievanceService.getComplainantInfo(grievance);
    }

    @RequestMapping(value = "/api/spProgrammeList/get", method = RequestMethod.GET)
    public List<com.grs.core.domain.grs.SpProgramme> getSpProgrammeList() {
        return spProgrammeService.findAllByStatusAndOfficeIdNotNull();
    }

    @RequestMapping(value = "/api/spProgrammeList/get/all", method = RequestMethod.GET)
    public List<com.grs.core.domain.grs.SpProgramme> getAllSpProgrammeList() {
        return spProgrammeService.findAll();
    }

    @RequestMapping(value = "/api/spProgramme/get/{programId}", method = RequestMethod.GET)
    public SpProgramGroDto getSpProgrammeOfficeNameBn(@PathVariable("programId") Integer programId) {
//        try {
//            SpProgramGroDto dto = new SpProgramGroDto();
//            dto.setOfficeName(officeService.getOffice(spProgrammeService.getSpProgramme(programId).
//                    getOfficeId()).getNameBangla());
//            return dto;
//        } catch (Exception ex) {
//            throw ex;
//        }
        SpProgramGroDto dto = new SpProgramGroDto();
        dto.setOfficeName(
                officeService.getOffice(
                        spProgrammeService.getSpProgramme(programId).getOfficeId()
                ).getNameBangla()
        );
        return dto;
    }

    @RequestMapping(value = "/getSpProgrammeGroDetailList", method = RequestMethod.GET)
    public List<SpProgramGroDto> getSpProgramGroDetailsList(@PageableDefault(value = Integer.MAX_VALUE) Pageable pageable) {
        return spProgrammeService.getSpProgramGroDetailsList();
    }

    @RequestMapping(value = "/api/grievance", method = RequestMethod.GET)
    public Page<GrievanceDTO> getGrievances(Authentication authentication, @PageableDefault(value = Integer.MAX_VALUE) Pageable pageable) {
        UserInformation userInformation = Utility.extractUserInformationFromAuthentication(authentication);
        return grievanceService.findGrievancesByUsers(userInformation, pageable);
    }

    @RequestMapping(value = "/api/grievance", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public WeakHashMap<String, String> addGrievance(Authentication authentication, @RequestBody GrievanceRequestDTO grievanceRequestDTO) throws Exception {
        return this.grievanceService.addGrievance(authentication, grievanceRequestDTO);
    }

    @RequestMapping(value = "/api/grievanceWithoutLogin", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public WeakHashMap<String, Object> addGrievanceWithoutLogin(Authentication authentication, @RequestBody GrievanceWithoutLoginRequestDTO grievanceRequestDTO) throws Exception {
        return this.grievanceService.addGrievanceWithoutLogin(authentication, grievanceRequestDTO);
    }

    @RequestMapping(value = "/api/getAllComplaintMovementByGrievance", method = RequestMethod.POST)
    public List<GrievanceForwarding> getAllComplaintMovementByGrievance(
            @RequestBody Grievance grievance) {
        return this.grievanceService.getAllComplaintMovementByGrievance(grievance);
    }

    @RequestMapping(value = "/api/findGrievancesByOthersComplainant/{userId}", method = RequestMethod.GET)
    public List<GrievanceDTO> findGrievancesByOthersComplainant(@PathVariable("userId") long userId) {
        return this.grievanceService.findGrievancesByOthersComplainant(userId);

    }

    @RequestMapping(value = "/api/grievanceForOthers", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public WeakHashMap<String, Object> addGrievanceForOthers(Authentication authentication, @RequestBody GrievanceWithoutLoginRequestDTO grievanceRequestDTO) throws Exception {
        return this.grievanceService.addGrievanceForOthers(authentication, grievanceRequestDTO);
    }

    @RequestMapping(value = "/api/grievance/safety-net-category-change", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public WeakHashMap<String, Object> changeSafetyNetCategory(Authentication authentication, @RequestBody ChangeSafetyNetCategoryDTO request) throws Exception {
        return this.grievanceService.changeSafetyNetCategory(authentication, request);
    }

    @RequestMapping(value = "/api/userInformationForComplainant", method = RequestMethod.POST)
    public UserInformation generateUserInformationForComplainant(@RequestBody Complainant complainant) {
        return this.grievanceService.generateUserInformationForComplainant(complainant);
    }

    @RequestMapping(value = "/api/grievance/{id}", method = RequestMethod.GET)
    public GrievanceDetailsDTO getGrievanceDetails(Authentication authentication, @PathVariable("id") Long id) {
        return grievanceService.getGrievanceDetailsWithMenuOptions(authentication, id);
    }

    @RequestMapping(value = "/api/grievance/{id}/files", method = RequestMethod.GET)
    public List<FileDerivedDTO> getGrievancesFiles(@PathVariable("id") Long id) {
        return grievanceService.getGrievancesFiles(id);
    }

    @RequestMapping(value = "/api/grievance/register/{office_id}", method = RequestMethod.GET)
    public Page<RegisterDTO> getIncomingRegister(Authentication authentication,
                                                 @PathVariable("office_id") Long officeId,
                                                 @PageableDefault(value = Integer.MAX_VALUE) Pageable pageable) {
        return grievanceService.getGrievanceByOfficeID(pageable, authentication, officeId);
    }

    @RequestMapping(value = "/api/grievance/inbox/cell", method = RequestMethod.GET)
    public Page<GrievanceDTO> getCellInboxGrievances(Authentication authentication, @PageableDefault(value = Integer.MAX_VALUE) Pageable pageable) {
        UserInformation userInformation = Utility.extractUserInformationFromAuthentication(authentication);
        return grievanceService.getInboxGrievanceList(userInformation, pageable);
    }

    @RequestMapping(value = "/api/grievance/inbox", method = RequestMethod.GET)
    public Page<GrievanceDTO> getInboxGrievances(Authentication authentication, @PageableDefault(value = Integer.MAX_VALUE) Pageable pageable) {
        UserInformation userInformation = Utility.extractUserInformationFromAuthentication(authentication);
        return grievanceService.getInboxGrievanceList(userInformation, pageable);
    }

    @RequestMapping(value = "/api/grievance/outbox", method = RequestMethod.GET)
    public Page<GrievanceDTO> getOutboxGrievances(Authentication authentication, @PageableDefault(value = Integer.MAX_VALUE) Pageable pageable) {
        UserInformation userInformation = Utility.extractUserInformationFromAuthentication(authentication);
        return grievanceService.getOutboxGrievance(userInformation, pageable);
    }

    @RequestMapping(value = "/api/grievance/cc", method = RequestMethod.GET)
    public Page<GrievanceDTO> getCCGrievances(Authentication authentication, @PageableDefault(value = Integer.MAX_VALUE) Pageable pageable) {
        UserInformation userInformation = Utility.extractUserInformationFromAuthentication(authentication);
        return grievanceService.getCCGrievance(userInformation, pageable);
    }

    @RequestMapping(value = "/api/grievance/forwarded", method = RequestMethod.GET)
    public Page<GrievanceDTO> getRejectedGrievances(Authentication authentication, @PageableDefault(value = Integer.MAX_VALUE) Pageable pageable) {
        UserInformation userInformation = Utility.extractUserInformationFromAuthentication(authentication);
        return grievanceService.getForwardedGrievances(userInformation, pageable);
    }

    @RequestMapping(value = "/api/grievance/closed", method = RequestMethod.GET)
    public Page<GrievanceDTO> getClosedGrievances(Authentication authentication, @PageableDefault(value = Integer.MAX_VALUE) Pageable pageable) {
        UserInformation userInformation = Utility.extractUserInformationFromAuthentication(authentication);
        return grievanceService.getClosedGrievances(userInformation, pageable);
    }

    @RequestMapping(value = "/api/grievance/appeal/inbox", method = RequestMethod.GET)
    public Page<GrievanceDTO> getInboxAppealGrievances(Authentication authentication, @PageableDefault(value = Integer.MAX_VALUE) Pageable pageable) {
        UserInformation userInformation = Utility.extractUserInformationFromAuthentication(authentication);
        return grievanceService.getInboxAppealGrievanceList(userInformation, pageable);
    }

    @RequestMapping(value = "/api/grievance/appeal/outbox", method = RequestMethod.GET)
    public Page<GrievanceDTO> getOutboxAppealGrievances(Authentication authentication, @PageableDefault(value = Integer.MAX_VALUE) Pageable pageable) {
        UserInformation userInformation = Utility.extractUserInformationFromAuthentication(authentication);
        return grievanceService.getOutboxAppealGrievanceList(userInformation, pageable);
    }

    @RequestMapping(value = "/api/caseNumber/{officeId}", method = RequestMethod.GET)
    public String getCaseNumber(@PathVariable("officeId") Long officeId) {
        return grievanceService.getCaseNumber(officeId);
    }

    @RequestMapping(value = "/api/employee/{employee_record_id}", method = RequestMethod.GET)
    public EmployeeRecordDTO getEmployeeRecord(@PathVariable("employee_record_id") Long id) {
        return grievanceService.getEmployeeRecord(id);
    }


    @RequestMapping(value = "/api/serviceOfficer/{grievanceId}", method = RequestMethod.GET)
    public EmployeeOrganogram getServiceOfficer(@PathVariable("grievanceId") Long grievanceId) {
        return this.grievanceService.getServiceOfficer(grievanceId);
    }

    @RequestMapping(value = "/api/getGRO/{grievanceId}", method = RequestMethod.GET)
    public EmployeeOrganogram getGRO(@PathVariable("grievanceId") Long grievanceId) {
        return this.grievanceService.getGRO(grievanceId);
    }

    @RequestMapping(value = "/api/appealOfficer/{grievanceId}", method = RequestMethod.GET)
    public EmployeeOrganogram getAppealOfficer(@PathVariable("grievanceId") Long grievanceId) {
        return this.grievanceService.getAppealOfficer(grievanceId);
    }

    @RequestMapping(value = "/api/grievance/appeal/closed", method = RequestMethod.GET)
    public Page<GrievanceDTO> getClosedAppealGrievances(Authentication authentication, @PageableDefault(value = Integer.MAX_VALUE) Pageable pageable) {
        UserInformation userInformation = Utility.extractUserInformationFromAuthentication(authentication);
        return this.grievanceService.getClosedAppealGrievances(userInformation, pageable);
    }

    @RequestMapping(value = "/api/grievance/forward/latest/gro/{grievanceId}", method = RequestMethod.GET)
    public EmployeeOrganogram getGroOfRecentmostGrievanceForwarding(@PathVariable("grievanceId") Long grievanceId) {
        return this.grievanceForwardingService.getGroOfRecentmostGrievanceForwarding(grievanceId);
    }

    @RequestMapping(value = "/api/grievance/gro/{grievanceId}", method = RequestMethod.GET)
    public EmployeeOrganogramDTO getGroOfGrievance(@PathVariable("grievanceId") Long grievanceId) {
        return this.grievanceService.getGroOfGrievance(grievanceId);
    }

    @RequestMapping(value = "/api/grievance/submittedGrievancesCountByOffice/{officeId}", method = RequestMethod.GET)
    public Long getSubmittedGrievancesCountByOffice(@PathVariable("officeId") Long officeId) {
        return this.grievanceService.getSubmittedGrievancesCountByOffice(officeId);
    }

    @RequestMapping(value = "api/grievance/so/{grievanceId}")
    public EmployeeOrganogramDTO getSOOfGrievance(@PathVariable("grievanceId") Long grievanceId) {
        return this.grievanceService.getSODetail(grievanceId);
    }

    @RequestMapping(value = "api/grievance/{listType}/search", method = RequestMethod.GET)
    public Page<GrievanceDTO> searchNormalGrievances(Authentication authentication,
                                                     @PathVariable(value = "listType") String listType,
                                                     @RequestParam(value = "value") String value,
                                                     @PageableDefault(value = Integer.MAX_VALUE) Pageable pageable) {

        UserInformation userInformation = Utility.extractUserInformationFromAuthentication(authentication);
        ListViewConditionOnCurrentStatusGenerator viewConditionOnCurrentStatusGenerator = new ListViewConditionOnCurrentStatusGenerator();
        ListViewType listViewType = viewConditionOnCurrentStatusGenerator.getNormalListTypeByString(listType);
        return this.grievanceService.getListViewWithSearching(userInformation, value, listViewType, pageable);
    }


    @RequestMapping(value = "api/grievance/inboxCount", method = RequestMethod.POST)
    public Long getInboxCount(Authentication authentication,
                              @RequestBody ListViewType listViewType) {
        UserInformation userInformation = Utility.extractUserInformationFromAuthentication(authentication);
        return this.grievanceService.getInboxCount(userInformation, listViewType);
    }


    @RequestMapping(value = "api/grievance/listViewWithOutSearching", method = RequestMethod.POST)
    public List<Grievance> getListViewWithOutSearching(
            @RequestParam long officeId,
            @RequestParam long userId,
            @RequestParam long officeOrganogramId) {
        return this.grievanceService.getListViewWithOutSearching(officeId, userId, officeOrganogramId);
    }


    @RequestMapping(value = "/api/grievance/oisfIntermediateDashboard",method = RequestMethod.POST)
    public OISFIntermediateDashboardDTO getInboxDataDTO(
            @RequestParam Long officeId,
            @RequestParam Long officeUnitOrganogramId,
           @RequestParam Long userId){
        return this.grievanceService.getInboxDataDTO(officeId,officeUnitOrganogramId,userId);
    }

    @RequestMapping(value = "/api/grievance/appeal/{listType}/search", method = RequestMethod.GET)
    public Page<GrievanceDTO> searchAppealGrievances(Authentication authentication,
                                                     @PathVariable(value = "listType") String listType,
                                                     @RequestParam(value = "value") String value,
                                                     @PageableDefault(value = Integer.MAX_VALUE) Pageable pageable) {

        UserInformation userInformation = Utility.extractUserInformationFromAuthentication(authentication);
        ListViewConditionOnCurrentStatusGenerator viewConditionOnCurrentStatusGenerator = new ListViewConditionOnCurrentStatusGenerator();
        ListViewType listViewType = viewConditionOnCurrentStatusGenerator.getAppealListTypeByString(listType);
        return this.grievanceService.getListViewWithSearching(userInformation, value, listViewType, pageable);
    }


    @RequestMapping(value = "/api/grievance/expired", method = RequestMethod.GET)
    public Page<GrievanceDTO> getExpiredGrievances(Authentication authentication, @PageableDefault(value = Integer.MAX_VALUE) Pageable pageable) {
        UserInformation userInformation = Utility.extractUserInformationFromAuthentication(authentication);
        return grievanceService.getExpiredGrievances(userInformation, pageable);
    }

    @RequestMapping(value = "/api/grievance/appeal/expired", method = RequestMethod.GET)
    public Page<GrievanceDTO> getExpiredAppealGrievances(Authentication authentication, @PageableDefault(value = Integer.MAX_VALUE) Pageable pageable) {
        UserInformation userInformation = Utility.extractUserInformationFromAuthentication(authentication);
        return grievanceService.getAppealExpiredGrievances(userInformation, pageable);
    }

    @RequestMapping(value = "/api/grievance/status", method = RequestMethod.GET)
    public Object getStatusOfGrievance(@RequestParam("trackingNumber") String trackingNumber, @RequestParam("phoneNumber") String phoneNumber) {
        return this.grievanceService.getStatusOfGrievance(trackingNumber, phoneNumber);
    }

    @RequestMapping(value = "/api/grievance/existingfiles/{grievanceId}", method = RequestMethod.GET)
    public List<ExistingFileDerivedDTO> getStatusOfGrievance(Authentication authentication, @PathVariable("grievanceId") Long grievanceId) {
        return this.grievanceForwardingService.getAllFilesList(grievanceId, authentication);
    }

    @RequestMapping(value = "/api/grievances/{complainantId}", method = RequestMethod.GET)
    public List<GrievanceDTO> getGrievancesByComplainantId(Authentication authentication, @PathVariable("complainantId") Long complainantId) {
        return this.grievanceService.getGrievancesByComplainantId(complainantId);
    }

    @RequestMapping(value = "/api/grievances/grievancesByComplainantIdForApi/{complainantId}", method = RequestMethod.GET)
    public List<GrievanceDTO> getGrievancesByComplainantIdForApi(@PathVariable("complainantId") Long complainantId) {
        return this.grievanceService.getGrievancesByComplainantIdForApi(complainantId);
    }

    @RequestMapping(value = "/api/grievances/grievanceByTrackingNumber", method = RequestMethod.POST)
    public Object getGrievanceByTrackingNumber(@RequestParam String trackingNumber) {
        return this.grievanceService.getGrievanceByTrackingNumber(trackingNumber);
    }

    @RequestMapping(value = "/api/grievances/singleGrievanceByTrackingNumber", method = RequestMethod.POST)
    public Grievance getSingleGrievanceByTrackingNumber(@RequestParam String trackingNumber) {
        return this.grievanceService.getSingleGrievanceByTrackingNumber(trackingNumber);
    }

    @RequestMapping(value = "/api/grievance/provide-nudge", method = RequestMethod.POST)
    public GenericResponse provideNudgeOfAGrievance(Authentication authentication, @RequestBody GrievanceForwardingMessageDTO forwardingMessageDTO) {
        return this.grievanceForwardingService.provideNudgeAgainstGrievance(forwardingMessageDTO, authentication);
    }

    @RequestMapping(value = "api/grievance/users/{grievanceId}", method = RequestMethod.GET)
    public List<EmployeeDetailsDTO> getRelatedUserToGrievance(@PathVariable("grievanceId") Long grievanceId) {
        return this.grievanceService.getAllRelatedUsers(grievanceId);
    }

    @RequestMapping(value = "/api/grievance/feedback", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE)
    public GenericResponse getFeedbackByComplainant(Authentication authentication, @RequestBody FeedbackRequestDTO feedbackRequestDTO) {
//        boolean status = true;
//        String msg = "";
//        try {
//            Grievance grievance = this.grievanceService.getFeedbackByComplainant(feedbackRequestDTO);
//            msg = this.messageService.getMessage("feedback.saved");
//        } catch (Exception ex) {
//            status = false;
//            msg = this.messageService.getMessage("feedback.saved.failed");
//            log.info(ex.getMessage());
//        } finally {
//            return GenericResponse.builder()
//                    .success(status)
//                    .message(msg)
//                    .build();
//        }
        boolean success = true;
        String message;

        try {
            grievanceService.getFeedbackByComplainant(feedbackRequestDTO);
            message = messageService.getMessage("feedback.saved");
        } catch (Exception ex) {
            success = false;
            message = messageService.getMessage("feedback.saved.failed");
            log.error("Error saving feedback: {}", ex.getMessage(), ex);
        }
        return GenericResponse.builder()
                .success(success)
                .message(message)
                .build();
    }


    @RequestMapping(value = "/api/notification/update", method = RequestMethod.PUT)
    public NotificationUrlDTO updateNotification(@RequestParam("id") Long id) {
        Notification notification = this.notificationService.updateNotification(id);
        return NotificationUrlDTO.builder().url(notification.getUrl()).build();
    }

    @RequestMapping(value = "/api/grievances/{grievanceId}/citizens-charter/{citizensCharterId}", method = RequestMethod.GET)
    public ServiceOriginDTO getCitizensCharterServiceDetailsOfGrievance(Authentication authentication, @PathVariable("grievanceId") Long grievanceId, @PathVariable("citizensCharterId") Long citizensCharterId) {
        UserInformation userInformation = Utility.extractUserInformationFromAuthentication(authentication);
        return grievanceService.getCitizenCharterAsServiceOriginDTO(userInformation, citizensCharterId, grievanceId);
    }

    @RequestMapping(value = "/api/unseen/count/{inboxType}", method = RequestMethod.GET)
    public UnseenCountDTO getUnseenCount(Authentication authentication, @PathVariable("inboxType") String inboxType) {
        return authentication == null ? UnseenCountDTO.builder().build() : this.grievanceForwardingService.getUnseenCountForUser(authentication, inboxType);
    }

    @RequestMapping(value = "/api/total/count/{inboxType}", method = RequestMethod.GET)
    public UnseenCountDTO getTotalCount(Authentication authentication, @PathVariable("inboxType") String inboxType) {
        return authentication == null ? UnseenCountDTO.builder().build() : this.grievanceForwardingService.getTotalCountForUser(authentication, inboxType);
    }

    @RequestMapping(value = "/api/grievance/cell", method = RequestMethod.GET)
    public List<GrievanceDTO> getAllCellGrievance(Authentication authentication) {
        if (authentication != null) {
            UserInformation userInformation = Utility.extractUserInformationFromAuthentication(authentication);
            if (Objects.equals(userInformation.getOfficeInformation().getOfficeId(), 0L)) {
                return this.grievanceService.getAllCellComplaints();
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    @RequestMapping(value = "api/user/details", method = RequestMethod.GET)
    public Page<UserDetailsDTO> getPaginatedUsersData(@PageableDefault(value = Integer.MAX_VALUE) Pageable pageable) {
        return complainantService.getPaginatedUsersData(pageable);
    }

    @RequestMapping(value = "/api/grievance/admin", method = RequestMethod.GET)
    public Page<GrievanceAdminDTO> getAdminGrievances(Authentication authentication,
                                                      @RequestParam(value = "officeId", required = false) Long officeId,
                                                      @RequestParam(value = "referenceNumber", required = false) String referenceNumber,
                                                      @PageableDefault(value = Integer.MAX_VALUE) Pageable pageable) {
        UserInformation userInformation = Utility.extractUserInformationFromAuthentication(authentication);
        return grievanceService.getAdminGrievances(userInformation, officeId, referenceNumber, pageable);
    }

    @RequestMapping(value = "/api/grievance/getGrievancesByIds", method = RequestMethod.POST)
    public List<Grievance> getGrievancesByIds(@RequestParam List<Long> grievanceIds){
        return this.grievanceService.getGrievancesByIds(grievanceIds);
    }

    @RequestMapping(value = "/api/grievance/re-assign", method = RequestMethod.POST)
    public WeakHashMap<String, Object> reassignGrievance(Authentication authentication, @RequestBody ReassignGrievanceDTO reassignGrievance) {
        UserInformation userInformation = Utility.extractUserInformationFromAuthentication(authentication);
        return grievanceService.reassignGrievance(reassignGrievance, userInformation);
    }
    @GetMapping("/api/grievanceforwarding/findByGrievanceIdAndAssignedRole/{grievanceId}/{roleType}")
    public List<GrievanceForwarding> findByGrievanceIdAndAssignedRole(
            @PathVariable Long grievanceId,
            @PathVariable("roleType") String roleName
    ){
        return this.grievanceForwardingDAO.findByGrievanceIdAndAssignedRole(grievanceId, roleName);
    }

    @RequestMapping(value = "/api/grievance/getSafetyNetGrievanceSummary", method = RequestMethod.POST)
    public SafetyNetGrievanceSummaryListDto getSafetyNetGrievanceSummary
            (@RequestBody SafetyNetGrievanceSummaryRequest request) {


    }


}