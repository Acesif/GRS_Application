package com.grs.grs_client.controller;

import com.grs.grs_client.config.CaptchaSettings;
import com.grs.grs_client.enums.OISFUserType;
import com.grs.grs_client.enums.ServiceType;
import com.grs.grs_client.enums.UserType;
import com.grs.grs_client.gateway.*;

import com.grs.grs_client.model.*;
import com.grs.grs_client.service.AccessControlService;
import com.grs.grs_client.service.ModelAndViewService;
import com.grs.grs_client.utils.Utility;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
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


@Slf4j
@RestController
public class GrievanceController {

    @Autowired
    private GrievanceGateway grievanceService;
    @Autowired
    private ComplainantGateway complainantService;
    @Autowired
    private OfficesGateway officeService;
    @Autowired
    private GrievanceForwardingGateway grievanceForwardingService;
    @Autowired
    private ModelAndViewService modelViewService;
    @Autowired
    private AccessControlService accessControlService;
    @Autowired
    private CitizenCharterGateway citizenCharterService;
    @Autowired
    private SafetyNetProgramGateway safetyNetProgramService;
    @Autowired
    private CaptchaSettings captchaSettings;


    @RequestMapping(value = "/viewGrievances.do", method = RequestMethod.GET)
    public ModelAndView getViewGrievancesPage(Authentication authentication, Model model, HttpServletRequest request) {
        if (authentication != null) {
            Boolean isBlacklisted = false;
            UserInformation userInformation = Utility.extractUserInformationFromAuthentication(authentication);
            if (userInformation.getUserType().equals(UserType.COMPLAINANT)) {
                isBlacklisted =  complainantService.isBlacklistedUser(userInformation.getUserId());
            }
            String fragmentFolder = Utility.isUserAnGRSUserOrOthersComplainant(userInformation) ? "dashboard" : "grievances";
            String fragmentName = Utility.isUserAnGRSUserOrOthersComplainant(userInformation) ? "dashboardCitizen" : "manageGrievances";

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
            UserInformation userInformation = Utility.extractUserInformationFromAuthentication(authentication);
            String fragmentFolder = "grievances";
            String fragmentName = "viewMyGrievances";
            model.addAttribute("isHoo", Utility.isUserAHOOUser(userInformation));
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
            UserInformation userInformation = Utility.extractUserInformationFromAuthentication(authentication);
            Grievance grievance = this.grievanceService.findGrievanceById(id);
            Boolean isGrsUser = Utility.isUserAnGRSUser(userInformation);
            Boolean isOthersComplainant = Utility.isUserAnOthersComplainant(userInformation);
            Boolean isGROUser = Utility.isUserAnGROUser(userInformation);
            Boolean appealButtonFlag = this.grievanceService.appealActivationFlag(grievance);
            Boolean isOISFComplainant = this.grievanceService.isOISFComplainant(userInformation, grievance);
            Boolean serviceIsNull = this.grievanceService.serviceIsNull(grievance);
            Boolean isNagorik = this.grievanceService.isNagorikTypeGrievance(grievance);
            Boolean isBlacklisted = complainantService.isBlacklistedUser(userInformation.getUserId());
            Boolean isFeedbackEnabled = this.grievanceService.isFeedbackEnabled(grievance);
            Boolean isAnonymousUser = this.grievanceService.isSubmittedByAnonymousUser(grievance);
            Boolean soAppealOption = null;
            Boolean isComplainantBlacklisted = false;
            Boolean canRetakeComplaint = false;
            if (Utility.isServiceOfficer(authentication)) {
                soAppealOption = this.grievanceForwardingService.soAppealActivationFlag(grievance);
            }
            if(!isGrsUser) {
                isComplainantBlacklisted = complainantService.isBlacklistedUserByComplainantId(grievance.getComplainantId());
                canRetakeComplaint = grievanceForwardingService.getComplaintRetakeFlag(grievance, userInformation);
            }
            List<FeedbackResponseDTO> feedbacks = this.grievanceService.getFeedbacks(grievance);
            model = modelViewService.addFileSettingsAttributesToModel(model);
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
            model.addAttribute("grievanceFileCount", this.grievanceService.getCountOfAttachedFiles(grievance));
            model.addAttribute("grievanceForwardingFileCount", this.grievanceForwardingService.getCountOfComplaintMovementAttachedFiles(userInformation, grievance));
            Boolean reviveFlag = this.grievanceService.isComplaintRevivable(grievance, userInformation);
            model.addAttribute("reviveFlag", reviveFlag);
            this.grievanceForwardingService.updateForwardSeenStatus(Utility.extractUserInformationFromAuthentication(authentication), grievance);
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
            UserInformation userInformation = Utility.extractUserInformationFromAuthentication(authentication);
            Grievance grievance = this.grievanceService.findGrievanceById(id);
            Boolean isGrsUser = Utility.isUserAnGRSUser(userInformation);
            Boolean isOthersComplainant = Utility.isUserAnOthersComplainant(userInformation);
            Boolean isGROUser = Utility.isUserAnGROUser(userInformation);
            Boolean appealButtonFlag = this.grievanceService.appealActivationFlag(grievance);
            Boolean isOISFComplainant = this.grievanceService.isOISFComplainant(userInformation, grievance);
            Boolean serviceIsNull = this.grievanceService.serviceIsNull(grievance);
            Boolean isNagorik = this.grievanceService.isNagorikTypeGrievance(grievance);
            Boolean isBlacklisted = complainantService.isBlacklistedUser(userInformation.getUserId());
            Boolean isFeedbackEnabled = this.grievanceService.isFeedbackEnabled(grievance);
            Boolean isAnonymousUser = this.grievanceService.isSubmittedByAnonymousUser(grievance);
            Boolean soAppealOption = null;
            Boolean isComplainantBlacklisted = false;
            Boolean canRetakeComplaint = false;
            if (Utility.isServiceOfficer(authentication)) {
                soAppealOption = this.grievanceForwardingService.soAppealActivationFlag(grievance);
            }
            if(!isGrsUser) {
                isComplainantBlacklisted = complainantService.isBlacklistedUserByComplainantId(grievance.getComplainantId());
                canRetakeComplaint = grievanceForwardingService.getComplaintRetakeFlag(grievance, userInformation);
            }
            List<FeedbackResponseDTO> feedbacks = this.grievanceService.getFeedbacks(grievance);
            model = modelViewService.addFileSettingsAttributesToModel(model);
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
            model.addAttribute("grievanceFileCount", this.grievanceService.getCountOfAttachedFiles(grievance));
            model.addAttribute("grievanceForwardingFileCount", this.grievanceForwardingService.getCountOfComplaintMovementAttachedFiles(userInformation, grievance));
            Boolean reviveFlag = this.grievanceService.isComplaintRevivable(grievance, userInformation);
            model.addAttribute("reviveFlag", reviveFlag);
            this.grievanceForwardingService.updateForwardSeenStatus(userInformation, grievance);
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
            UserInformation userInformation = Utility.extractUserInformationFromAuthentication(authentication);
            Grievance grievance = this.grievanceService.findGrievanceById(id);
            Boolean isGrsUser = Utility.isUserAnGRSUser(userInformation);
            Boolean isOthersComplainant = Utility.isUserAnOthersComplainant(userInformation);
            Boolean isGROUser = Utility.isUserAnGROUser(userInformation);
            Boolean serviceIsNull = this.grievanceService.serviceIsNull(grievance);
            Boolean isFeedbackEnabled = this.grievanceService.isFeedbackEnabled(grievance);
            List<FeedbackResponseDTO> feedbacks = this.grievanceService.getFeedbacks(grievance);
            model = modelViewService.addFileSettingsAttributesToModel(model);
            model.addAttribute("isGRO", isGROUser);
            model.addAttribute("serviceIsNull", serviceIsNull);
            model.addAttribute("isFeedbackEnabled", isFeedbackEnabled);
            model.addAttribute("feedbacks", feedbacks);

            Boolean appealButtonFlag = this.grievanceService.appealActivationFlag(grievance);
            Boolean isOISFComplainant = this.grievanceService.isOISFComplainant(userInformation, grievance);
            Boolean isNagorik = this.grievanceService.isNagorikTypeGrievance(grievance);
            Boolean isBlacklisted = complainantService.isBlacklistedUser(userInformation.getUserId());
            Boolean isAnonymousUser = this.grievanceService.isSubmittedByAnonymousUser(grievance);
            Boolean soAppealOption = null;
            Boolean isComplainantBlacklisted = false;
            Boolean canRetakeComplaint = false;
            if (Utility.isServiceOfficer(authentication)) {
                soAppealOption = this.grievanceForwardingService.soAppealActivationFlag(grievance);
            }
            if(!isGrsUser) {
                isComplainantBlacklisted = complainantService.isBlacklistedUserByComplainantId(grievance.getComplainantId());
                canRetakeComplaint = grievanceForwardingService.getComplaintRetakeFlag(grievance, userInformation);
            }
            model = modelViewService.addFileSettingsAttributesToModel(model);
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
            model.addAttribute("grievanceFileCount", this.grievanceService.getCountOfAttachedFiles(grievance));
            model.addAttribute("grievanceForwardingFileCount", this.grievanceForwardingService.getCountOfComplaintMovementAttachedFiles(userInformation, grievance));
            Boolean reviveFlag = this.grievanceService.isComplaintRevivable(grievance, userInformation);
            model.addAttribute("reviveFlag", reviveFlag);
            this.grievanceForwardingService.updateForwardSeenStatus(userInformation, grievance);
            model.addAttribute("searchableOffices", officeService.getGrsEnabledOfficeSearchingData());

            this.grievanceForwardingService.updateForwardSeenStatus(userInformation, grievance);
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

            if(complainantService.isBlacklistedUser(userInformation.getUserId())){
                blackLiterOffice = complainantService.findBlacklistedOffices(userInformation.getUserId());
            }
            model.addAttribute("grievanceDTO", new GrievanceRequestDTO());
            List<ServicePair> servicePairs = citizenCharterService.getDefaultAllowedServiceTypes(request);
            model = modelViewService.addFileSettingsAttributesToModel(model);
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
            model = modelViewService.addFileSettingsAttributesToModel(model);
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
            model = modelViewService.addFileSettingsAttributesToModel(model);
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
            UserInformation userInformation = Utility.extractUserInformationFromAuthentication(authentication);
            if(!Utility.isUserAHOOUser(userInformation)) {
                return new ModelAndView("redirect:/error-page");
            }


            model.addAttribute("grievanceDTO", new GrievanceRequestDTO());
            List<ServicePair> servicePairs = citizenCharterService.getDefaultAllowedServiceTypes(request);
            model = modelViewService.addFileSettingsAttributesToModel(model);
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
        model = modelViewService.addFileSettingsAttributesToModel(model);
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
        model = modelViewService.addFileSettingsAttributesToModel(model);
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
        model = modelViewService.addFileSettingsAttributesToModel(model);
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
    public SafetyNetGrievanceSummaryListDto getSafetyNetGrievanceList
            (@RequestBody SafetyNetGrievanceSummaryRequest request, Model model) {
        SafetyNetGrievanceSummaryListDto safetyNetGrievanceSummaryListDto
                = this.grievanceService.getSafetyNetGrievanceSummary(request);
        model.addAttribute("resultList", safetyNetGrievanceSummaryListDto.getSafetyNetGrievanceSummaryList());
        return safetyNetGrievanceSummaryListDto;
    }

    @RequestMapping(value = "addOfflineGrievance.do", method = RequestMethod.GET)
    public ModelAndView getOfflineGrievanceSubmissionPage(Authentication authentication, Model model, HttpServletRequest request){
        if (authentication != null) {
            model.addAttribute("grievanceDTO", new GrievanceRequestDTO());
            List<ServicePair> servicePairs = citizenCharterService.getDefaultAllowedServiceTypes(request);
            model = modelViewService.addFileSettingsAttributesToModel(model);
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
            model = modelViewService.addFileSettingsAttributesToModel(model);
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
        model = modelViewService.addFileSettingsAttributesToModel(model);
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

//    @RequestMapping(value = "/api/total/count/{inboxType}", method = RequestMethod.GET)
//    public UnseenCountDTO getTotalCount(Authentication authentication, @PathVariable("inboxType") String inboxType) {
//        return authentication == null ? UnseenCountDTO.builder().build() : this.grievanceService.getTotalCountForUser(inboxType);
//    }
//
//    @RequestMapping(value = "api/grievance/{listType}/search", method = RequestMethod.GET)
//    public Page<GrievanceDTO> searchNormalGrievances(Authentication authentication,
//                                                     @PathVariable("listType") String listType,
//                                                     @RequestParam(value = "value") String value,
//                                                     @PageableDefault(value = Integer.MAX_VALUE) Pageable pageable) {
//        return authentication == null
//                ? new PageImpl<>(Collections.emptyList(), pageable, 0)
//                : this.grievanceService.searchGrievancesForUser(listType, value, pageable);
//    }
}