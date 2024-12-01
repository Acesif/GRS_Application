package com.grs.grs_client.controller;

import com.grs.api.model.OISFUserType;
import com.grs.api.model.UserInformation;
import com.grs.api.model.UserType;
import com.grs.api.model.request.*;
import com.grs.api.model.response.*;
import com.grs.api.model.response.file.ExistingFileDerivedDTO;
import com.grs.api.model.response.file.FileDerivedDTO;
import com.grs.api.model.response.grievance.GrievanceDTO;
import com.grs.api.model.response.grievance.GrievanceDetailsDTO;
import com.grs.core.config.CaptchaSettings;
import com.grs.core.dao.SpProgrammeDAO;
import com.grs.core.domain.ServicePair;
import com.grs.core.domain.ServiceType;
import com.grs.core.domain.grs.Grievance;
import com.grs.core.domain.grs.Notification;
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
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.*;

/**
 * Created by Tanvir on 9/14/2017.
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
    private CitizenCharterService citizenCharterService;
    @Autowired
    private ModelViewService modelViewService;
    @Autowired
    private ComplainantService complainantService;
    @Autowired
    private AccessControlService accessControlService;
    @Autowired
    private SafetyNetProgramService safetyNetProgramService;

    @Autowired
    private CaptchaSettings captchaSettings;


    @RequestMapping(value = "/viewGrievances.do", method = RequestMethod.GET)
    public ModelAndView getViewGrievancesPage(Authentication authentication, Model model, HttpServletRequest request) {
        if (authentication != null) {
            String fragmentFolder = Utility.isUserAnGRSUserOrOthersComplainant(authentication) ? "dashboard" : "grievances";
            String fragmentName = Utility.isUserAnGRSUserOrOthersComplainant(authentication) ? "dashboardCitizen" : "manageGrievances";
            Boolean isBlacklisted = complainantService.isBlacklistedUser(authentication);
            model.addAttribute("isBlacklisted", isBlacklisted);
            return modelViewService.addNecessaryAttributesAndReturnViewPage(model,
                    authentication,
                    request,
                    fragmentFolder,
                    fragmentName,
                    "admin");
        }
        return new ModelAndView("redirect:/error-page");
    }

    @RequestMapping(value = "/viewMyGrievances.do", method = RequestMethod.GET)
    public ModelAndView getMyViewGrievancesPage(Authentication authentication, Model model, HttpServletRequest request) {
        if (authentication != null) {
            String fragmentFolder = "grievances";
            String fragmentName = "viewMyGrievances";
            model.addAttribute("isHoo", Utility.isUserAHOOUser(authentication));
            return modelViewService.addNecessaryAttributesAndReturnViewPage(model,
                    authentication,
                    request,
                    fragmentFolder,
                    fragmentName,
                    "admin");
        }
        return new ModelAndView("redirect:/error-page");
    }

    @RequestMapping(value = "/viewAppealGrievances.do", method = RequestMethod.GET)
    public ModelAndView getViewAppealGrievancesPage(Authentication authentication, Model model, HttpServletRequest request) {
        if (authentication != null) {
            String fragmentFolder = "grievances";
            String fragmentName = "manageAppealGrievances";
            return modelViewService.addNecessaryAttributesAndReturnViewPage(model,
                    authentication,
                    request,
                    fragmentFolder,
                    fragmentName,
                    "admin");
        }
        return new ModelAndView("redirect:/error-page");
    }

    @RequestMapping(value = "/searchGrievances.do", method = RequestMethod.GET, params = "id")
    public ModelAndView searchGrievancesPage(
            Authentication authentication,
            Model model,
            HttpServletRequest request,
            @RequestParam Long id,
            @RequestParam(required = false) String tab, // Add tab as an optional parameter
            @RequestParam(required = false) Integer page, // Add page as an optional parameter
            @RequestParam(required = false) Integer size // Add size as an optional parameter
    ) {
        if (authentication != null) {
            if(!accessControlService.hasPermissionToViewGrievanceDetails(authentication, id)) {
                return new ModelAndView("redirect:/error-page");
            }
            Boolean isGrsUser = Utility.isUserAnGRSUser(authentication);
            Boolean isOthersComplainant = Utility.isUserAnOthersComplainant(authentication);
            Boolean isGROUser = Utility.isUserAnGROUser(authentication);
            Boolean appealButtonFlag = this.grievanceService.appealActivationFlag(id);
            Boolean isOISFComplainant = this.grievanceService.isOISFComplainant(authentication, id);
            Boolean serviceIsNull = this.grievanceService.serviceIsNull(id);
            Boolean isNagorik = this.grievanceService.isNagorikTypeGrievance(id);
            Boolean isBlacklisted = complainantService.isBlacklistedUser(authentication);
            Boolean isFeedbackEnabled = this.grievanceService.isFeedbackEnabled(id);
            Boolean isAnonymousUser = this.grievanceService.isSubmittedByAnonymousUser(id);
            Boolean soAppealOption = null;
            Boolean isComplainantBlacklisted = false;
            Boolean canRetakeComplaint = false;
            if (Utility.isServiceOfficer(authentication)) {
                soAppealOption = this.grievanceService.soAppealActivationFlag(id);
            }
            if(!isGrsUser) {
                isComplainantBlacklisted = grievanceService.isComplainantBlackListedByGrievanceId(id);
                canRetakeComplaint = grievanceForwardingService.getComplaintRetakeFlag(id, authentication);
            }
            List<FeedbackResponseDTO> feedbacks = this.grievanceService.getFeedbacks(id);
            model = grievanceService.addFileSettingsAttributesToModel(model);
            model.addAttribute("grsUser", isGrsUser);
            model.addAttribute("isOthersComplainant", isOthersComplainant);
            model.addAttribute("isGRO", isGROUser);
            model.addAttribute("appealButtonFlag", appealButtonFlag);
            model.addAttribute("isOISFComplainant", isOISFComplainant);
            model.addAttribute("serviceIsNull", serviceIsNull);
            model.addAttribute("isNagorik", isNagorik);
            model.addAttribute("isBlacklisted", isBlacklisted);
            model.addAttribute("isAnonymousUser", isAnonymousUser);
            model.addAttribute("isFeedbackEnabled", isFeedbackEnabled);
            model.addAttribute("feedbacks", feedbacks);
            model.addAttribute("soAppealOption", soAppealOption);
            model.addAttribute("isComplainantBlacklisted", isComplainantBlacklisted);
            model.addAttribute("canRetakeComplaint", canRetakeComplaint);
            model.addAttribute("grievanceFileCount", this.grievanceService.getCountOfAttachedFiles(id));
            model.addAttribute("grievanceForwardingFileCount", this.grievanceForwardingService.getCountOfComplaintMovementAttachedFiles(authentication, id));
            Boolean reviveFlag = this.grievanceService.isComplaintRevivable(id, authentication);
            model.addAttribute("reviveFlag", reviveFlag);
            this.grievanceForwardingService.updateForwardSeenStatus(Utility.extractUserInformationFromAuthentication(authentication), id);
            model.addAttribute("searchableOffices", officeService.getGrsEnabledOfficeSearchingData());

            // Add tab, page, and size parameters to the model so they can be passed back
            model.addAttribute("currentTab", tab);
            model.addAttribute("currentPage", page);
            model.addAttribute("pageSize", size);

            return modelViewService.addNecessaryAttributesAndReturnViewPage(model,
                    authentication,
                    request,
                    "grievances",
                    "searchGrievance",
                    "admin");
        }
        return new ModelAndView("redirect:/error-page");
    }

    @RequestMapping(value = "/viewGrievances.do", method = RequestMethod.GET, params = "id")
    public ModelAndView getViewGrievancesPage(
            Authentication authentication,
            Model model,
            HttpServletRequest request,
            @RequestParam Long id,
            @RequestParam(required = false) String tab, // Add tab as an optional parameter
            @RequestParam(required = false) Integer page, // Add page as an optional parameter
            @RequestParam(required = false) Integer size // Add size as an optional parameter
    ) {
        if (authentication != null) {
            if(!accessControlService.hasPermissionToViewGrievanceDetails(authentication, id)) {
                return new ModelAndView("redirect:/error-page");
            }
            Boolean isGrsUser = Utility.isUserAnGRSUser(authentication);
            Boolean isOthersComplainant = Utility.isUserAnOthersComplainant(authentication);
            Boolean isGROUser = Utility.isUserAnGROUser(authentication);
            Boolean appealButtonFlag = this.grievanceService.appealActivationFlag(id);
            Boolean isOISFComplainant = this.grievanceService.isOISFComplainant(authentication, id);
            Boolean serviceIsNull = this.grievanceService.serviceIsNull(id);
            Boolean isNagorik = this.grievanceService.isNagorikTypeGrievance(id);
            Boolean isBlacklisted = complainantService.isBlacklistedUser(authentication);
            Boolean isFeedbackEnabled = this.grievanceService.isFeedbackEnabled(id);
            Boolean isAnonymousUser = this.grievanceService.isSubmittedByAnonymousUser(id);
            Boolean soAppealOption = null;
            Boolean isComplainantBlacklisted = false;
            Boolean canRetakeComplaint = false;
            if (Utility.isServiceOfficer(authentication)) {
                soAppealOption = this.grievanceService.soAppealActivationFlag(id);
            }
            if(!isGrsUser) {
                isComplainantBlacklisted = grievanceService.isComplainantBlackListedByGrievanceId(id);
                canRetakeComplaint = grievanceForwardingService.getComplaintRetakeFlag(id, authentication);
            }
            List<FeedbackResponseDTO> feedbacks = this.grievanceService.getFeedbacks(id);
            model = grievanceService.addFileSettingsAttributesToModel(model);
            model.addAttribute("grsUser", isGrsUser);
            model.addAttribute("isOthersComplainant", isOthersComplainant);
            model.addAttribute("isGRO", isGROUser);
            model.addAttribute("appealButtonFlag", appealButtonFlag);
            model.addAttribute("isOISFComplainant", isOISFComplainant);
            model.addAttribute("serviceIsNull", serviceIsNull);
            model.addAttribute("isNagorik", isNagorik);
            model.addAttribute("isBlacklisted", isBlacklisted);
            model.addAttribute("isAnonymousUser", isAnonymousUser);
            model.addAttribute("isFeedbackEnabled", isFeedbackEnabled);
            model.addAttribute("feedbacks", feedbacks);
            model.addAttribute("soAppealOption", soAppealOption);
            model.addAttribute("isComplainantBlacklisted", isComplainantBlacklisted);
            model.addAttribute("canRetakeComplaint", canRetakeComplaint);
            model.addAttribute("grievanceFileCount", this.grievanceService.getCountOfAttachedFiles(id));
            model.addAttribute("grievanceForwardingFileCount", this.grievanceForwardingService.getCountOfComplaintMovementAttachedFiles(authentication, id));
            Boolean reviveFlag = this.grievanceService.isComplaintRevivable(id, authentication);
            model.addAttribute("reviveFlag", reviveFlag);
            this.grievanceForwardingService.updateForwardSeenStatus(Utility.extractUserInformationFromAuthentication(authentication), id);
            model.addAttribute("searchableOffices", officeService.getGrsEnabledOfficeSearchingData());

            // Add tab, page, and size parameters to the model so they can be passed back
            model.addAttribute("currentTab", tab);
            model.addAttribute("currentPage", page);
            model.addAttribute("pageSize", size);

            return modelViewService.addNecessaryAttributesAndReturnViewPage(model,
                    authentication,
                    request,
                    "grievances",
                    "viewGrievancesDetails",
                    "admin");
        }
        return new ModelAndView("redirect:/error-page");
    }

    @RequestMapping(value = "/viewAppealGrievances.do", method = RequestMethod.GET, params = "id")
    public ModelAndView getViewAppealGrievancesPage(Authentication authentication, Model model, HttpServletRequest request, @RequestParam Long id) {
        if (authentication != null) {
            Boolean isGrsUser = Utility.isUserAnGRSUser(authentication);
            Boolean isOthersComplainant = Utility.isUserAnOthersComplainant(authentication);
            Boolean isGROUser = Utility.isUserAnGROUser(authentication);
            Boolean serviceIsNull = this.grievanceService.serviceIsNull(id);
            Boolean isFeedbackEnabled = this.grievanceService.isFeedbackEnabled(id);
            List<FeedbackResponseDTO> feedbacks = this.grievanceService.getFeedbacks(id);
            model = grievanceService.addFileSettingsAttributesToModel(model);
            model.addAttribute("isGRO", isGROUser);
            model.addAttribute("serviceIsNull", serviceIsNull);
            model.addAttribute("isFeedbackEnabled", isFeedbackEnabled);
            model.addAttribute("feedbacks", feedbacks);

            Boolean appealButtonFlag = this.grievanceService.appealActivationFlag(id);
            Boolean isOISFComplainant = this.grievanceService.isOISFComplainant(authentication, id);
            Boolean isNagorik = this.grievanceService.isNagorikTypeGrievance(id);
            Boolean isBlacklisted = complainantService.isBlacklistedUser(authentication);
            Boolean isAnonymousUser = this.grievanceService.isSubmittedByAnonymousUser(id);
            Boolean soAppealOption = null;
            Boolean isComplainantBlacklisted = false;
            Boolean canRetakeComplaint = false;
            if (Utility.isServiceOfficer(authentication)) {
                soAppealOption = this.grievanceService.soAppealActivationFlag(id);
            }
            if(!isGrsUser) {
                isComplainantBlacklisted = grievanceService.isComplainantBlackListedByGrievanceId(id);
                canRetakeComplaint = grievanceForwardingService.getComplaintRetakeFlag(id, authentication);
            }
            model = grievanceService.addFileSettingsAttributesToModel(model);
            model.addAttribute("grsUser", isGrsUser);
            model.addAttribute("isOthersComplainant", isOthersComplainant);
            model.addAttribute("isGRO", isGROUser);
            model.addAttribute("appealButtonFlag", appealButtonFlag);
            model.addAttribute("isOISFComplainant", isOISFComplainant);
            model.addAttribute("serviceIsNull", serviceIsNull);
            model.addAttribute("isNagorik", isNagorik);
            model.addAttribute("isBlacklisted", isBlacklisted);
            model.addAttribute("isAnonymousUser", isAnonymousUser);
            model.addAttribute("isFeedbackEnabled", isFeedbackEnabled);
            model.addAttribute("feedbacks", feedbacks);
            model.addAttribute("soAppealOption", soAppealOption);
            model.addAttribute("isComplainantBlacklisted", isComplainantBlacklisted);
            model.addAttribute("canRetakeComplaint", canRetakeComplaint);
            model.addAttribute("grievanceFileCount", this.grievanceService.getCountOfAttachedFiles(id));
            model.addAttribute("grievanceForwardingFileCount", this.grievanceForwardingService.getCountOfComplaintMovementAttachedFiles(authentication, id));
            Boolean reviveFlag = this.grievanceService.isComplaintRevivable(id, authentication);
            model.addAttribute("reviveFlag", reviveFlag);
            this.grievanceForwardingService.updateForwardSeenStatus(Utility.extractUserInformationFromAuthentication(authentication), id);
            model.addAttribute("searchableOffices", officeService.getGrsEnabledOfficeSearchingData());

            this.grievanceForwardingService.updateForwardSeenStatus(Utility.extractUserInformationFromAuthentication(authentication), id);
            return modelViewService.addNecessaryAttributesAndReturnViewPage(model,
                    authentication,
                    request,
                    "grievances",
                    "viewAppealGrievancesDetails",
                    "admin");
        }
        return new ModelAndView("redirect:/error-page");
    }

    @RequestMapping(value = "/addPublicGrievances.do", method = RequestMethod.GET)
    public ModelAndView getAddGrievancesPage(Authentication authentication, Model model, HttpServletRequest request,
                                             @RequestParam(value = "serviceInfo", defaultValue = "-1") Optional<String> serviceInfo
    ) throws IOException {
        model.addAttribute("grievanceDTO", new GrievanceRequestDTO());
        if (!serviceInfo.get().equals("-1")) {
            ServiceRelatedInfoRequestDTO serviceRelatedInfoRequestDTO = this.grievanceService.convertFromBase64encodedString(serviceInfo.get());
            model.addAttribute("serviceId", serviceRelatedInfoRequestDTO.getServiceId());
            model.addAttribute("officeId", serviceRelatedInfoRequestDTO.getOfficeId());
            model.addAttribute("officeName", serviceRelatedInfoRequestDTO.getOfficeName());
            model.addAttribute("serviceName", serviceRelatedInfoRequestDTO.getServiceName());
        }
        if (authentication != null) {
            UserInformation userInformation = Utility.extractUserInformationFromAuthentication(authentication);
            boolean isComplainant = userInformation.getUserType().equals(UserType.COMPLAINANT);
            List<Long> blackLiterOffice = new ArrayList<>();
            if(!isComplainant) {
                return new ModelAndView("redirect:/error-page");
            }

            if(complainantService.isBlacklistedUser(authentication)){
                blackLiterOffice = complainantService.findBlacklistedOffices(userInformation.getUserId());
            }
            model.addAttribute("grievanceDTO", new GrievanceRequestDTO());
            List<ServicePair> servicePairs = citizenCharterService.getDefaultAllowedServiceTypes(request);
            model = grievanceService.addFileSettingsAttributesToModel(model);
            model.addAttribute("servicePairs", servicePairs);
            model.addAttribute("searchableOffices", officeService.getGrsEnabledOfficeSearchingData());
            model.addAttribute("safetyNetPrograms", safetyNetProgramService.getSafetyNetPrograms());
            model.addAttribute("complaintType", ServiceType.NAGORIK.name());

            model.addAttribute("blacklistInOfficeId", blackLiterOffice);

            return modelViewService.returnViewsForComplainWithoutLogin(
                    authentication,
                    model,
                    request,
                    "grievances",
                    "addGrievancesForOthers",
                    "adminForOthers");

        }
        return new ModelAndView("redirect:/login?a=0&redirectUrl=addPublicGrievances.do?serviceInfo=" + serviceInfo.get());
    }


    @RequestMapping(value = "/addSelfMotivatedGrievances.do", method = RequestMethod.GET)
    public ModelAndView getAddSelfMotivatedGrievancesPage(Authentication authentication, Model model, HttpServletRequest request) throws IOException {
        model.addAttribute("grievanceDTO", new GrievanceRequestDTO());

        if (authentication != null) {
            UserInformation userInformation = Utility.extractUserInformationFromAuthentication(authentication);
            if(userInformation.getUserType().equals(UserType.COMPLAINANT)) {
                return new ModelAndView("redirect:/error-page");
            }

            model.addAttribute("grievanceDTO", new GrievanceRequestDTO());
            List<ServicePair> servicePairs = citizenCharterService.getDefaultAllowedServiceTypes(request);
            model = grievanceService.addFileSettingsAttributesToModel(model);
            model.addAttribute("servicePairs", servicePairs);
            model.addAttribute("searchableOffices", officeService.getGrsEnabledOfficeSearchingData());
            model.addAttribute("safetyNetPrograms", safetyNetProgramService.getSafetyNetPrograms());
            model.addAttribute("complaintType", ServiceType.STAFF.name());

            List<Long> blackLiterOffice = new ArrayList<>();

            model.addAttribute("blacklistInOfficeId", blackLiterOffice);

            return modelViewService.returnViewsForComplainWithoutLogin(
                    authentication,
                    model,
                    request,
                    "grievances",
                    "addGrievancesForOthers",
                    "adminForOthers");

        }
        return new ModelAndView("redirect:/login?a=0&redirectUrl=addSelfMotivatedGrievances.do");
    }

    @RequestMapping(value = "/addStaffGrievances.do", method = RequestMethod.GET)
    public ModelAndView getAddStaffGrievancesPage(Authentication authentication, Model model, HttpServletRequest request) throws IOException {
        model.addAttribute("grievanceDTO", new GrievanceRequestDTO());
        if (authentication != null) {
            UserInformation userInformation = Utility.extractUserInformationFromAuthentication(authentication);
            if(!userInformation.getUserType().equals(UserType.OISF_USER)) {
                return new ModelAndView("redirect:/error-page");
            }

            model.addAttribute("grievanceDTO", new GrievanceRequestDTO());
            List<ServicePair> servicePairs = citizenCharterService.getDefaultAllowedServiceTypes(request);
            model = grievanceService.addFileSettingsAttributesToModel(model);
            model.addAttribute("servicePairs", servicePairs);
            model.addAttribute("searchableOffices", officeService.getGrsEnabledOfficeSearchingData());
            model.addAttribute("safetyNetPrograms", safetyNetProgramService.getSafetyNetPrograms());
            model.addAttribute("complaintType", ServiceType.STAFF.name());

            List<Long> blackLiterOffice = new ArrayList<>();

            model.addAttribute("blacklistInOfficeId", blackLiterOffice);

            return modelViewService.returnViewsForComplainWithoutLogin(
                    authentication,
                    model,
                    request,
                    "grievances",
                    "addGrievancesForOthers",
                    "adminForOthers");

        }
        return new ModelAndView("redirect:/error-page");
    }

    @RequestMapping(value = "/addOfficialGrievances.do", method = RequestMethod.GET)
    public ModelAndView getAddOfficialGrievancesPage(Authentication authentication, Model model, HttpServletRequest request) throws IOException {
        model.addAttribute("grievanceDTO", new GrievanceRequestDTO());
        if (authentication != null) {
            if(!Utility.isUserAHOOUser(authentication)) {
                return new ModelAndView("redirect:/error-page");
            }


            model.addAttribute("grievanceDTO", new GrievanceRequestDTO());
            List<ServicePair> servicePairs = citizenCharterService.getDefaultAllowedServiceTypes(request);
            model = grievanceService.addFileSettingsAttributesToModel(model);
            model.addAttribute("servicePairs", servicePairs);
            model.addAttribute("searchableOffices", officeService.getGrsEnabledOfficeSearchingData());
            model.addAttribute("safetyNetPrograms", safetyNetProgramService.getSafetyNetPrograms());
            model.addAttribute("complaintType", ServiceType.DAPTORIK.name());

            List<Long> blackLiterOffice = new ArrayList<>();

            model.addAttribute("blacklistInOfficeId", blackLiterOffice);

            return modelViewService.returnViewsForComplainWithoutLogin(
                    authentication,
                    model,
                    request,
                    "grievances",
                    "addGrievancesForOthers",
                    "adminForOthers");

        }
        return new ModelAndView("redirect:/error-page");
    }

    @RequestMapping(value = "/anonymousAddGrievance.do", method = RequestMethod.GET)
    public ModelAndView getAnonymousAddGrievancePage(HttpServletRequest request, Authentication authentication, Model model) {

        model.addAttribute("anonymousForOthers", true);

        String htmlFragment = authentication == null ? "addGrievancesWithoutLogin" : "addGrievancesForOthers";

        model.addAttribute("grievanceDTO", new GrievanceRequestDTO());
        List<ServicePair> servicePairs = citizenCharterService.getDefaultAllowedServiceTypes(request);
        model = grievanceService.addFileSettingsAttributesToModel(model);
        model.addAttribute("servicePairs", servicePairs);
        model.addAttribute("searchableOffices", officeService.getGrsEnabledOfficeSearchingData());
        model.addAttribute("safetyNetPrograms", safetyNetProgramService.getSafetyNetPrograms());
        model.addAttribute("complaintType", ServiceType.NAGORIK.name());

        List<Long> blackLiterOffice = new ArrayList<>();

        model.addAttribute("blacklistInOfficeId", blackLiterOffice);

        return modelViewService.returnViewsForComplainWithoutLogin(
                authentication,
                model,
                request,
                "grievances",
                htmlFragment,
                "adminForOthers");
    }


    @RequestMapping(value = "/complainWithoutLogin.do", method = RequestMethod.GET)
    public ModelAndView complainWithoutLoginPage(HttpServletRequest request, Authentication authentication, Model model) {
        String sn = request.getParameter("sn");
        model.addAttribute("grievanceDTO", new GrievanceRequestDTO());
        List<ServicePair> servicePairs = citizenCharterService.getDefaultAllowedServiceTypes(request);
        model = grievanceService.addFileSettingsAttributesToModel(model);
        model.addAttribute("servicePairs", servicePairs);
        model.addAttribute("searchableOffices", officeService.getGrsEnabledOfficeSearchingData());
        model.addAttribute("sn", sn);
        model.addAttribute("safetyNetPrograms", safetyNetProgramService.getSafetyNetPrograms());
        model.addAttribute("complaintType", ServiceType.NAGORIK.name());
        model.addAttribute("captchaSettings", captchaSettings);

        List<Long> blackLiterOffice = new ArrayList<>();

        model.addAttribute("blacklistInOfficeId", blackLiterOffice);

        return modelViewService.returnViewsForComplainWithoutLogin(
                authentication,
                model,
                request,
                "grievances",
                "addGrievancesWithoutLogin",
                "adminWithoutLogin");

    }
    @RequestMapping(value = "/complainForOthers.do", method = RequestMethod.GET)
    public ModelAndView complainForOthersPage(HttpServletRequest request, Authentication authentication, Model model) {
        model.addAttribute("grievanceDTO", new GrievanceRequestDTO());
        List<ServicePair> servicePairs = citizenCharterService.getDefaultAllowedServiceTypes(request);
        model = grievanceService.addFileSettingsAttributesToModel(model);
        model.addAttribute("servicePairs", servicePairs);
        model.addAttribute("searchableOffices", officeService.getGrsEnabledOfficeSearchingData());
        model.addAttribute("safetyNetPrograms", safetyNetProgramService.getSafetyNetPrograms());
        model.addAttribute("complaintType", ServiceType.NAGORIK.name());

        List<Long> blackLiterOffice = new ArrayList<>();

        model.addAttribute("blacklistInOfficeId", blackLiterOffice);

        return modelViewService.returnViewsForComplainWithoutLogin(
                authentication,
                model,
                request,
                "grievances",
                "addGrievancesForOthers",
                "adminForOthers");

    }

    @RequestMapping(value = "/safetynetgrievancedata.do", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public com.grs.api.model.response.grievance.SafetyNetGrievanceSummaryListDto getSafetyNetGrievanceList
            (@RequestBody SafetyNetGrievanceSummaryRequest request, Model model) {
        com.grs.api.model.response.grievance.SafetyNetGrievanceSummaryListDto safetyNetGrievanceSummaryListDto
                = this.grievanceService.getSafetyNetGrievanceSummary(request);
        model.addAttribute("resultList", safetyNetGrievanceSummaryListDto.safetyNetGrievanceSummaryList);
        return safetyNetGrievanceSummaryListDto;
    }

    @RequestMapping(value = "addOfflineGrievance.do", method = RequestMethod.GET)
    public ModelAndView getOfflineGrievanceSubmissionPage(Authentication authentication, Model model, HttpServletRequest request){
        if (authentication != null) {
            model.addAttribute("grievanceDTO", new GrievanceRequestDTO());
            List<ServicePair> servicePairs = citizenCharterService.getDefaultAllowedServiceTypes(request);
            model = grievanceService.addFileSettingsAttributesToModel(model);
            model.addAttribute("servicePairs", servicePairs);
            model.addAttribute("searchableOffices", officeService.getGrsEnabledOfficeSearchingData());
            model.addAttribute("safetyNetPrograms", safetyNetProgramService.getSafetyNetPrograms());
            model.addAttribute("complaintType", ServiceType.NAGORIK.name());

            List<Long> blackLiterOffice = new ArrayList<>();

            model.addAttribute("blacklistInOfficeId", blackLiterOffice);

            return modelViewService.returnViewsForComplainWithoutLogin(
                    authentication,
                    model,
                    request,
                    "grievances",
                    "addGrievancesForOthers",
                    "adminForOthers");
        }
        return new ModelAndView("redirect:/error-page");
    }

    @RequestMapping(value = "/registrationFromUpload.do", method = RequestMethod.GET)
    public ModelAndView addModelAndRedirect(Authentication authentication, Model model, HttpServletRequest request) {
        if(authentication!=null){//TODO: check if the user is gro.
            model.addAttribute("fromGrievanceUpload", true);
            return modelViewService.returnViewsForNormalPages(authentication, model, request, "grsRegistrationForm");
        }
        return new ModelAndView("redirect:/error-page");
    }

    @RequestMapping(value = "/addPublicGrievancesByGRO.do", method = RequestMethod.GET)
    public ModelAndView addPublicGrievanceByGRO(Authentication authentication, Model model, HttpServletRequest request, @RequestParam("phone") String phoneNumber){
        if (authentication != null) {
            UserInformation userInformation = Utility.extractUserInformationFromAuthentication(authentication);
            if(!userInformation.getUserType().equals(UserType.OISF_USER) ) {
                return new ModelAndView("redirect:/error-page");
            }
            if(!userInformation.getOisfUserType().equals(OISFUserType.GRO) || phoneNumber==null){
                return new ModelAndView("redirect:/error-page");
            }

            model.addAttribute("grievanceDTO", new GrievanceRequestDTO());
            List<ServicePair> servicePairs = citizenCharterService.getDefaultAllowedServiceTypes(request);
            model = grievanceService.addFileSettingsAttributesToModel(model);
            model.addAttribute("servicePairs", servicePairs);
            model.addAttribute("searchableOffices", officeService.getGrsEnabledOfficeSearchingData());
            model.addAttribute("safetyNetPrograms", safetyNetProgramService.getSafetyNetPrograms());
            model.addAttribute("complaintType", ServiceType.NAGORIK.name());

            List<Long> blackLiterOffice = new ArrayList<>();

            model.addAttribute("blacklistInOfficeId", blackLiterOffice);

            return modelViewService.returnViewsForComplainWithoutLogin(
                    authentication,
                    model,
                    request,
                    "grievances",
                    "addGrievancesForOthers",
                    "adminForOthers");
        }
        return new ModelAndView("redirect:/error-page");
    }

    @RequestMapping(value = "/addAnonymousGrievanceGRO.do", method = RequestMethod.GET)
    public ModelAndView getAddAnonymousGrievanceGROPage(HttpServletRequest request, Authentication authentication, Model model) {

        model.addAttribute("anonymousForOthers", true);

        String htmlFragment = authentication == null ? "addGrievancesWithoutLogin" : "addGrievancesForOthers";

        model.addAttribute("grievanceDTO", new GrievanceRequestDTO());
        List<ServicePair> servicePairs = citizenCharterService.getDefaultAllowedServiceTypes(request);
        model = grievanceService.addFileSettingsAttributesToModel(model);
        model.addAttribute("servicePairs", servicePairs);
        model.addAttribute("searchableOffices", officeService.getGrsEnabledOfficeSearchingData());
        model.addAttribute("safetyNetPrograms", safetyNetProgramService.getSafetyNetPrograms());
        model.addAttribute("complaintType", ServiceType.NAGORIK.name());

        List<Long> blackLiterOffice = new ArrayList<>();

        model.addAttribute("blacklistInOfficeId", blackLiterOffice);

        return modelViewService.returnViewsForComplainWithoutLogin(
                authentication,
                model,
                request,
                "grievances",
                htmlFragment,
                "adminForOthers");
    }
}