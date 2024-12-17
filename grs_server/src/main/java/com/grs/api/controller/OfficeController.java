package com.grs.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.grs.api.config.security.TokenAuthenticationServiceUtil;
import com.grs.api.model.OfficeInformation;
import com.grs.api.model.OfficewithCountDTO;
import com.grs.api.model.UserInformation;
import com.grs.api.model.request.KeyValueStringPairDTO;
import com.grs.api.model.response.*;
import com.grs.api.model.response.dashboard.GrievanceCountByItemDTO;
import com.grs.api.model.response.grievance.GrievanceDTO;
import com.grs.api.model.response.officeSelection.OfficeSearchContentsDTO;
import com.grs.api.model.response.officeSelection.OfficeSearchDTO;
import com.grs.api.model.response.organogram.OfficeOriginUnitOrganogramDTO;
import com.grs.api.model.response.organogram.TreeNodeDTO;
import com.grs.api.model.response.organogram.TreeNodeOfficerDTO;
import com.grs.api.model.response.roles.RoleContainerDTO;
import com.grs.api.model.response.roles.SingleRoleDTO;
import com.grs.core.dao.SafetyNetDAO;
import com.grs.core.domain.ServiceType;
import com.grs.core.domain.grs.*;
import com.grs.core.domain.projapoti.*;
import com.grs.core.service.*;
import com.grs.utils.Constant;
import com.grs.utils.StringUtil;
import com.grs.utils.Utility;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.stream.Collectors;

/**
 * Created by Tanvir on 8/30/2017.
 */
@Slf4j
@RestController
@RequiredArgsConstructor
public class OfficeController {
    private final OfficeService officeService;
    private final OfficeOrganogramService officeOrganogramService;
    private final CitizenCharterService citizenCharterService;
    private final GrievanceForwardingService grievanceForwardingService;
    private final MessageService messageService;
    private final OfficesGroService officesGroService;
    private final ActionToRoleService actionToRoleService;
    private final GrievanceService grievanceService;
    private final CacheService cacheService;
    private final ObjectMapper objectMapper;
    private final SafetyNetProgramService safetyNetProgramService;

    @RequestMapping(value = "/api/office-origin-unit-organograms/{officeOriginId}", method = RequestMethod.GET)
    public List<OfficeOriginUnitOrganogramDTO> getOfficeOriginUnitOrganogramsByOfficeOriginId(@PathVariable("officeOriginId") Long officeOriginId) {
        return this.officeOrganogramService.getOfficeOriginUnitOrganogramsByOfficeOriginId(officeOriginId);
    }

    @RequestMapping(value = "/api/officelayers/{layer_level}", method = RequestMethod.GET)
    public List<Office> getOfficeLayers(@PathVariable("layer_level") Integer layerLevel,
                                        @RequestParam(value = "grsEnabled", defaultValue = "true") Boolean grsEnabled,
                                        @RequestParam(value = "showChildOfficesOnly", defaultValue = "false") Boolean showChildOfficesOnly) {
        return officeService.getOfficesByLayerLevel(layerLevel, grsEnabled, showChildOfficesOnly);
    }


    @RequestMapping(value = "/api/officelayers/{layer_level}/count", method = RequestMethod.GET)
    public List<OfficewithCountDTO> getOfficeLayersWithCount(@PathVariable("layer_level") Integer layerLevel,
                                                             @RequestParam(value = "grsEnabled", defaultValue = "true") Boolean grsEnabled,
                                                             @RequestParam(value = "showChildOfficesOnly", defaultValue = "false") Boolean showChildOfficesOnly) {
        List<Office> offices = new ArrayList<>();
        if (layerLevel == 0) {
            offices.add(Office.builder().id(0L).nameBangla("সেল").nameEnglish("Cell").build());
        } else {
            offices.addAll(officeService.getOfficesByLayerLevel(layerLevel, grsEnabled, showChildOfficesOnly));
        }
        List<OfficewithCountDTO> officewithCountDTOS = new ArrayList<>();
        for (Office office : offices) {
            OfficewithCountDTO countDTO = OfficewithCountDTO.builder()
                    .count(grievanceService.getSubmittedGrievancesCountByOffice(office.getId()))
                    .id(office.getId())
                    .layerLevel(layerLevel)
                    .nameBangla(office.getNameBangla())
                    .nameEnglish(office.getNameEnglish())
                    .build();
            officewithCountDTOS.add(countDTO);
        }
        return officewithCountDTOS;

    }

    @RequestMapping(value = "/api/office/service/{office_id}/{service_type}", method = RequestMethod.GET)
    public ServiceContainerDTO getServicesByServiceType(@PathVariable("office_id") Long officeID,
                                                        @PathVariable("service_type") ServiceType serviceType) {
        return ServiceContainerDTO.builder()
                .services(this.officeService.getServicesByServiceType(officeID, serviceType))
                .build();
    }

    @RequestMapping(value = "/api/office/{office_id}/offices-citizen-charters/details", method = RequestMethod.GET)
    public Object getOfficesCitizenChartersForOffice(@PathVariable("office_id") Long officeId,
                                                                         @RequestParam("layerLevel") Long layerLevel,
                                                                         @RequestParam("officeOriginId") Long officeOriginId) {
        try {
            Office office = officeService.findOne(officeId);
            if (officeOriginId == 0) {
                officeOriginId = office.getOfficeOriginId();
            }
            if (office == null) {
                throw new Exception("Office doesn't exist");
            }
            return CitizenChartersByOfficeDTO.builder()
                    .officeNameBangla(office.getNameBangla())
                    .officeNameEnglish(office.getNameEnglish())
                    .websiteUrl(office.getWebsiteUrl())
                    .visionMission(officeService.getOfficesVisionMission(layerLevel, officeOriginId))
                    .citizenServices(officeService.getServicesByServiceTypeFromOfficesCitizenCharter(officeId, ServiceType.NAGORIK))
                    .officialServices(officeService.getServicesByServiceTypeFromOfficesCitizenCharter(officeId, ServiceType.DAPTORIK))
                    .internalServices(officeService.getServicesByServiceTypeFromOfficesCitizenCharter(officeId, ServiceType.STAFF))
                    .officeGRO(officeService.getGRODetailsByOfficeId(officeId))
                    .officeAO(officeService.getAODetailsByOfficeId(officeId))
                    .build();

        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return GenericResponse.builder().success(false).message("Some Error Occured").build();
        }
    }

    @RequestMapping(value = "/api/office/service/{office_id}", method = RequestMethod.GET)
    public ServiceContainerDTO getServices(@PathVariable("office_id") Long officeID) {
        return ServiceContainerDTO.builder()
                .services(this.officeService.getServices(officeID))
                .build();
    }

    @RequestMapping(value = "/api/services/{service_id}", method = RequestMethod.GET)
    public ServiceOriginDTO getServiceById(@PathVariable("service_id") Long serviceId) {
        ServiceOrigin serviceOrigin = officeService.getServiceOrigin(serviceId);
        return officeService.getServiceDTOFromService(serviceOrigin);
    }

    @RequestMapping(value = "/api/office/employee/{office_id}", method = RequestMethod.GET)
    public BaseObjectContainerDTO getEmployees(@PathVariable("office_id") Long officeID) {
        return this.officeService.getEmployeesByOffices(officeID);
    }

    @RequestMapping(value = "/api/office/organogram", method = RequestMethod.GET)
    public List<TreeNodeDTO> getOrganogramTreeOfOffice(@RequestParam("id") String id, Authentication authentication) {
        return officeOrganogramService.getOrganogram(id, authentication);
    }

    @RequestMapping(value = "/api/office/organogram/so", method = RequestMethod.GET)
    public List<TreeNodeOfficerDTO> getSoOrganogramTreeOfOffice(@RequestParam("id") String id, Authentication authentication) {
        return officeOrganogramService.getSOOrganogram(id, authentication);
    }

    @RequestMapping(value = "/api/office/organogram/suborganogram", method = RequestMethod.GET)
    public List<TreeNodeOfficerDTO> getRootOfAOSubOffice(Authentication authentication, @RequestParam("grievanceId") Long grievanceId) {
        return grievanceForwardingService.getRootOfAOSubOffice(grievanceId, authentication);
    }

    @RequestMapping(value = "/api/office/organogram/subordinate", method = RequestMethod.GET)
    public List<TreeNodeDTO> getRootOfSubOffice(Authentication authentication, @RequestParam("grievanceId") Long grievanceId) {
        return grievanceForwardingService.getRootOfSubOffice(grievanceId, authentication);
    }

    @RequestMapping(value = "/api/office/descendents", method = RequestMethod.GET)
    public List<TreeNodeDTO> getDescendentOffices(@RequestParam("id") String id, Authentication authentication) {
        return officeOrganogramService.getDescendentOffices(id, authentication);
    }

    @RequestMapping(value = "/api/user", method = RequestMethod.GET)
    public Object getLoggedInUser(Authentication authentication) {
        return authentication;
    }

    @RequestMapping(value = "/api/office/services", method = RequestMethod.POST)
    public GenericResponse addService(@RequestBody ServiceOriginDTO serviceOriginDTO) {
        if (!(StringUtil.isValidString(serviceOriginDTO.getServiceNameBangla()) && StringUtil.isValidString(serviceOriginDTO.getServiceNameEnglish()))) {
            String message = messageService.getMessageWithArgsV2("x.cannot.be.blank", "service.name");
            return GenericResponse.builder().success(false).message(message).build();
        }
        if (serviceOriginDTO.getServiceTime() != null && serviceOriginDTO.getServiceTime() < 0) {
            String message = messageService.getMessageWithArgsV2("invalid.input.for.x", "service.time");
            return GenericResponse.builder().success(false).message(message).build();
        }
        Boolean flag = citizenCharterService.saveService(serviceOriginDTO);
        String code = flag ? "x.save.success" : "can.not.save.x";
        String message = messageService.getMessageWithArgsV2(code, "service");
        return GenericResponse.builder().success(flag).message(message).build();
    }

    @RequestMapping(value = "/api/office/services/{id}", method = RequestMethod.PUT)
    public GenericResponse updateService(@PathVariable("id") Long id,
                                         @RequestBody ServiceOriginDTO serviceOriginDTO) {
        String code;
        Boolean flag;
        ServiceOrigin serviceOrigin = officeService.getServiceOrigin(id);
        if (serviceOrigin == null) {
            flag = false;
            code = "x.does.not.exist";
        } else {
            if (!(StringUtil.isValidString(serviceOriginDTO.getServiceNameBangla()) && StringUtil.isValidString(serviceOriginDTO.getServiceNameEnglish()))) {
                String message = messageService.getMessageWithArgsV2("x.cannot.be.blank", "service.name");
                return GenericResponse.builder().success(false).message(message).build();
            }
            if (serviceOriginDTO.getServiceTime() == null || serviceOriginDTO.getServiceTime() < 0) {
                String message = messageService.getMessageWithArgsV2("invalid.input.for.x", "service.time");
                return GenericResponse.builder().success(false).message(message).build();
            }
            flag = citizenCharterService.saveService(serviceOriginDTO);
            code = flag ? "x.updated.successfully" : "cannot.update.x";
        }
        String message = messageService.getMessageWithArgsV2(code, "service");
        return GenericResponse.builder().success(flag).message(message).build();
    }

    @RequestMapping(value = "/api/office/services", method = RequestMethod.GET)
    public Page<ServiceOriginDTO> getAllServices(@PageableDefault(value = Integer.MAX_VALUE) Pageable pageable) {
        return officeService.getAllServices(pageable);
    }

    @RequestMapping(value = "/api/office/{office_id}/citizen-charters", method = RequestMethod.GET)
    public Page<CitizenCharterDTO> getCitizenChartersByOffice(
            @PageableDefault(value = Integer.MAX_VALUE) Pageable pageable,
            @PathVariable("office_id") Long officeID) {
        return officeService.getAllCitizenChartersByOffice(pageable, officeID);
    }

    @RequestMapping(value = "/api/office/{office_id}/employees", method = RequestMethod.GET)
    public EmployeeRecordsWithOfficeInfoDTO getEmployeeRecordsByOffice(@PageableDefault(value = Integer.MAX_VALUE) Pageable pageable,
                                                                       @PathVariable("office_id") Long officeID) {
        Office office = officeService.findOne(officeID);
        return EmployeeRecordsWithOfficeInfoDTO.builder()
                .officeId(office.getId())
                .officeNameBangla(office.getNameBangla())
                .officeNameEnglish(office.getNameEnglish())
                .employeeRecords(officeService.getAllEmployeeRecordByOffice(pageable, office))
                .build();
    }

    @RequestMapping(value = "/api/offices/{officeId}/gro/employeeRecordId", method = RequestMethod.GET)
    public IdDTO getEmployeeRecordIdOfGROByOfficeId(@PathVariable("officeId") Long officeId) {
        EmployeeRecordDTO groEmployeeRecord = officeService.getGRODetailsByOfficeId(officeId);
        Long id = groEmployeeRecord != null ? Long.parseLong(groEmployeeRecord.getId()) : null;
        return IdDTO.builder().id(id).build();
    }

    @RequestMapping(value = "/api/office/{office_id}/service/{service_id}/so/organogram-id", method = RequestMethod.GET)
    public IdDTO getOrganogramIdOfSOByOfficeId(@PathVariable("office_id") Long officeId,
                                               @PathVariable("service_id") Long serviceId) {
        CitizenCharter citizenCharter = this.citizenCharterService.findByOfficeIdAndServiceId(officeId, serviceId);
        return IdDTO.builder().id(citizenCharter != null ? citizenCharter.getSoOfficeUnitOrganogramId() : null).build();
    }

    @RequestMapping(value = "/api/office/{officeId}/{employeeType}/organogram-id", method = RequestMethod.GET)
    public IdDTO getOrganogramIdByEmployeeTypeAndOfficeId(@PathVariable("officeId") Long officeId,
                                                          @PathVariable("employeeType") String employeeType) {
        OfficesGRO officesGRO = officeService.getOfficesGRO(officeId);
        Long organogramId = null;
        if (officesGRO != null) {
            switch (employeeType) {
                case "gro":
                    organogramId = officesGRO.getGroOfficeUnitOrganogramId();
                    break;
                case "ao":
                    organogramId = officesGRO.getAppealOfficerOfficeUnitOrganogramId();
                    break;
                case "admin":
                    organogramId = officesGRO.getAdminOfficeUnitOrganogramId();
                    break;
            }
        }
        return IdDTO.builder().id(organogramId).build();
    }

    @RequestMapping(value = "/api/office/setup", method = RequestMethod.GET)
    public Page<OfficesGroDTO> getViewGRSOfficePage(@PageableDefault(value = Integer.MAX_VALUE) Pageable pageable) {
        return this.officesGroService.findActiveSetup(pageable);
    }

    @RequestMapping(value = "/api/office/setupMissing", method = RequestMethod.GET)
    public Page<OfficesGroDTO> getOfficeSetupMissingInfo(Authentication authentication,
                                                         @PageableDefault(value = Integer.MAX_VALUE) Pageable pageable,
                                                         @RequestParam(value = "office_layers") Long officeLayers,
                                                         @RequestParam(value = "first_selection") Long firstSelection,
                                                         @RequestParam(value = "second_selection") Long secondSelection,
                                                         @RequestParam(value = "officer_type") Long missingOfficerType) {
        return officeService.getOfficeSetUpMissing(missingOfficerType, officeLayers, firstSelection, secondSelection, pageable);
    }

    @RequestMapping(value = "/api/office/{office_id}/setup", method = RequestMethod.GET)
    public OfficesGroDTO getOfficeSetupInfo(@PathVariable("office_id") Long officeId) {
        return officeService.getOfficerCitizenCharter(officeId);
    }

    @RequestMapping(value = "/api/office/setup/{office_id}", method = RequestMethod.PUT)
    public OfficesGroDTO updateOfficeSetup(@PathVariable("office_id") Long officeId, @RequestBody OfficesGroDTO officesGroDTO) {
        OfficesGroDTO savedOfficesGroDTO = officesGroService.saveOfficeSetup(officeId, officesGroDTO);
        cacheService.updateOfficeSearchCacheContents();
        return savedOfficesGroDTO;
    }

    @RequestMapping(value = "/api/offices/disable/{office_id}", method = RequestMethod.GET)
    public GenericResponse disableOfficeSetup(@PathVariable("office_id") Long officeId) {
        boolean flag = officesGroService.disableOfficeSetup(officeId);
        return GenericResponse.builder()
                .message("")
                .success(flag)
                .build();
    }

    @RequestMapping(value = "/api/safety-net/setup", method = RequestMethod.POST)
    public WeakHashMap<String, String> safetyNetSetup(@RequestBody SafetyNetDAO safetyNetDAO) {
        return safetyNetProgramService.saveProgram(safetyNetDAO);
    }

    @RequestMapping(value = "/api/safety-net/search", method = RequestMethod.GET)
    public Page<SafetyNetDAO> safetyNetSearch(@PageableDefault(value = Integer.MAX_VALUE) Pageable pageable) {
        return safetyNetProgramService.safetyNetSearch(pageable);
    }

    @RequestMapping(value = "/api/user/organograms", method = RequestMethod.GET)
    public RoleContainerDTO getOrganogramsForLoggedInUser(Authentication authentication) {
        UserInformation userInformation = Utility.extractUserInformationFromAuthentication(authentication);
        return officeService.getOfficeUnitOrganogramsForLoggedInUser(userInformation);
    }

    @RequestMapping(value = "/api/user/organograms", method = RequestMethod.PUT)
    public void setCurrentOrganogram(Authentication authentication, HttpServletResponse response, @RequestBody SingleRoleDTO roleDTO) {
        UserInformation userInformation = Utility.extractUserInformationFromAuthentication(authentication);
        userInformation = this.officeService.switchOISFUserRole(userInformation, roleDTO);

        Set<String> authorities = this.actionToRoleService.findGrsRole(userInformation.getOisfUserType())
                .getPermissions()
                .stream()
                .map(permission -> permission.getName())
                .collect(Collectors.toSet());

        String token = TokenAuthenticationServiceUtil.constuctJwtToken(authentication.getName(), authorities, userInformation);

        Cookie cookie = new Cookie(Constant.HEADER_STRING, token);
        cookie.setPath("/");
        cookie.setMaxAge(Constant.COOKIE_EXPIRATION_TIME);
        response.addCookie(cookie);
        response.addHeader("Content-Type", "application/json; charset=utf-8");

        RoleContainerDTO roleContainerDTO = officeService.getOfficeUnitOrganogramsForLoggedInUser(userInformation);
        try {
            objectMapper.writeValue(response.getWriter(), roleContainerDTO);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @RequestMapping(value = "/api/offices/{officeId}/gro", method = RequestMethod.GET)
    public GroContactInfoResponseDTO getGRO(@PathVariable("officeId") Long officeId) {
        return this.officeService.getGROcontactInfoByOfficeId(officeId);
    }

    @RequestMapping(value = "/api/offices/{officeId}/ao", method = RequestMethod.GET)
    public GroContactInfoResponseDTO getAO(@PathVariable("officeId") Long officeId) {
        return this.officeService.getAoContactInfoByOfficeId(officeId);
    }

    @RequestMapping(value = "/api/offices/ancestors", method = RequestMethod.GET)
    public List<Office> getOfficeAlongWithAncestorOffices(Object request) {
        if (request instanceof Authentication){
            UserInformation userInformation = Utility.extractUserInformationFromAuthentication(
                    (Authentication) request
            );
            return this.officeService.getOfficeAlongWithAncestorOffices(userInformation);
        } else if (request instanceof UserInformation){
            return this.officeService.getOfficeAlongWithAncestorOffices((UserInformation) request);
        } else {
            return null;
        }
    }

    @RequestMapping(value = "/api/office-origin/{layer_level}", method = RequestMethod.GET)
    public List<OfficeOrigin> getOfficeOrigins( @PathVariable("layer_level") Integer layerLevel,
                                               @RequestParam(value = "grsEnabled", defaultValue = "true") Boolean grsEnabled,
                                               @RequestParam(value = "showChildOfficesOnly", defaultValue = "false") Boolean showChildOfficesOnly) {
        return this.officeService.getOfficeOriginsByLayerLevel(layerLevel, grsEnabled, showChildOfficesOnly);
    }

    @RequestMapping(value = "/api/vision-mission/{layer_level}/{office_origin_id}", method = RequestMethod.GET)
    public CitizensCharterOriginWithServiceOriginsDTO getMissionVisionByOfficeOrigin(@PathVariable("layer_level") Long layerLevel,
                                                                                     @PathVariable("office_origin_id") Long officeOriginId) {
        return officeService.getCitizensCharterOriginWithServiceOriginList(layerLevel, officeOriginId);
    }

    @RequestMapping(value = "/api/setup/layer-level/{layer_level}/office-origin/{office_origin_id}", method = RequestMethod.POST)
    public CitizensCharterOriginDTO saveVisionMission(@PathVariable("layer_level") Long layerLevel,
                                                      @PathVariable("office_origin_id") Long officeOriginId,
                                                      @RequestBody KeyValueStringPairDTO keyValueStringPairDTO) {
        return officeService.saveVisionMission(layerLevel, officeOriginId, keyValueStringPairDTO);
    }

    @RequestMapping(value = "/api/office-origins/{office_origin_id}/offices", method = RequestMethod.GET)
    public List<Office> getOffices(@PathVariable("office_origin_id") Long officeOriginId,
                                   @RequestParam(value = "grsEnabled", defaultValue = "true") Boolean grsEnabled,
                                   @RequestParam(value = "showChildOfficesOnly", defaultValue = "false") Boolean showChildOfficesOnly) {
        return this.officeService.findByOfficeOriginId(officeOriginId, grsEnabled, showChildOfficesOnly);
    }

    @RequestMapping(value = "/api/layer-level/{layer_level}/custom-layers/{custom_layer_id}/offices", method = RequestMethod.GET)
    public List<Office> getOfficesByCustomLayerId(@PathVariable("layer_level") Integer layerLevel,
                                                  @PathVariable("custom_layer_id") Integer customLayerId,
                                                  @RequestParam(value = "grsEnabled", defaultValue = "true") Boolean grsEnabled,
                                                  @RequestParam(value = "showChildOfficesOnly", defaultValue = "false") Boolean showChildOfficesOnly) {
        return this.officeService.getOfficesByLayerLevelAndCustomLayerId(layerLevel, customLayerId, grsEnabled, showChildOfficesOnly);
    }

    @RequestMapping(value = "/api/layer-level/{layer_level}/custom-layers", method = RequestMethod.GET)
    public List<CustomOfficeLayer> getCustomOfficeLayersByLayerLevel(@PathVariable("layer_level") Integer layerLevel) {
        return officeService.getCustomOfficeLayersByLayerLevel(layerLevel);
    }

    @RequestMapping(value = "/api/offices/{office_origin_id}/grs-enabled", method = RequestMethod.GET)
    public List<Office> getGrsEnabledOffices(@PathVariable("office_origin_id") Long officeOriginId) {
        return this.officeService.findGRSenabledOfficesByOfficeOriginId(officeOriginId);
    }

    @RequestMapping(value = "/api/office/{office_id}/citizen-charters", method = RequestMethod.PUT, params = "officeOriginId")
    public List<CitizenCharterDTO> copyCitizenCharter(@PathVariable("office_id") Long officeId, @RequestParam Long officeOriginId) {
        return this.officeService.copyCitizenChartersFromOrigins(officeId, officeOriginId);
    }

    @RequestMapping(value = "/api/office/{office_id}/gro-ao-admin-info", method = RequestMethod.GET)
    public OfficesGroDTO showGroAoAdminInfo(@PathVariable("office_id") Long officeId) {
        return this.officesGroService.findOfficersByOfficeId(officeId);
    }

    @RequestMapping(value = "/api/office/{office_id}/service/{service_id}", method = RequestMethod.GET)
    public CitizenCharterDTO getCitizenCharter(@PathVariable("office_id") Long officeId, @PathVariable("service_id") Long serviceId) {
        return officeService.convertToCitizenCharterDTO(citizenCharterService.findByOfficeIdAndServiceId(officeId, serviceId));
    }

    @RequestMapping(value = "/api/office/{office_id}/status-change/{service_id}", method = RequestMethod.PUT)
    public GenericResponse updateCitizensCharterStatus(@PathVariable("office_id") Long officeId, @PathVariable("service_id") Long serviceId) {
        CitizenCharter citizenCharter = this.citizenCharterService.findByOfficeIdAndServiceId(officeId, serviceId);
        citizenCharter.setStatus(!citizenCharter.getStatus());
        citizenCharter = this.citizenCharterService.saveCitizenCharter(citizenCharter);
        return GenericResponse.builder().success(citizenCharter.getStatus()).build();
    }

    @RequestMapping(value = "/api/office/{office_id}/service/{service_id}/assign/SO", method = RequestMethod.PUT)
    public CitizenCharterDTO updateSODetails(@RequestBody CitizenCharterDTO citizenCharterDTO, @PathVariable("office_id") Long officeId, @PathVariable("service_id") Long serviceId) {
        return this.officeService.assignSO(citizenCharterDTO, officeId, serviceId);
    }

    @RequestMapping(value = "/api/office-origins/{office_origin_id}/office-origin-units", method = RequestMethod.GET)
    public List<OfficeOriginUnitDTO> getOfficeOriginUnitDTOListByOfficeOriginId(@PathVariable("office_origin_id") Long officeOriginId) {
        return officeService.getOfficeOriginUnitDTOListByOfficeOriginId(officeOriginId);
    }

    @RequestMapping(value = "/api/office-origin-units/{office_origin_unit_id}/office-origin-unit-organograms", method = RequestMethod.GET)
    public List<com.grs.api.model.response.OfficeOriginUnitOrganogramDTO> getOfficeOriginUnitOrganogramDTOListByOfficeOriginUnitId(@PathVariable("office_origin_unit_id") Long officeOriginId) {
        return officeService.getOfficeOriginUnitOrganogramDTOListByOfficeOriginUnitId(officeOriginId);
    }

    @RequestMapping(value = "/api/office-origins/service-origins", method = RequestMethod.POST)
    public GenericResponse addServiceOrigin(@RequestBody ServiceOriginDTO serviceOriginDTO) {
        return citizenCharterService.saveServiceOrigin(serviceOriginDTO);
    }

    @RequestMapping(value = "/api/office-origins/service-origins/{service_origin_id}", method = RequestMethod.PUT)
    public GenericResponse editServiceOrigin(@PathVariable("service_origin_id") Long serviceOriginId,
                                             @RequestBody ServiceOriginDTO serviceOriginDTO) {
        return citizenCharterService.saveServiceOrigin(serviceOriginDTO);
    }

    @RequestMapping(value = "/api/office-origins/service-origins/{service_origin_id}/user-offices", method = RequestMethod.GET)
    public List<ServiceStatusInOfficeDTO> getCitizenCharterListByServiceOriginId(@PathVariable("service_origin_id") Long serviceOriginId) {
        return citizenCharterService.getCitizenCharterListByServiceOriginId(serviceOriginId);
    }

    @RequestMapping(value = "/api/offices/services/disable", method = RequestMethod.PUT)
    public GenericResponse updateServiceUserOfficesStatus(@RequestBody List<ItemIdStatusDTO> idStatusList) {
        Boolean success = citizenCharterService.updateServiceUserOfficesStatus(idStatusList);
        String message = messageService.getMessage(success ? "status.changed.success" : "status.changed.failure");
        return GenericResponse.builder()
                .success(success)
                .message(message)
                .build();
    }

    @RequestMapping(value = "/api/offices/{officeId}/gro-ao-records", method = RequestMethod.GET)
    public GroAoEmployeeRecordsDTO getGroAndAoEmployeeRecordsByOfficeId(@PathVariable("officeId") Long officeId) {
        return officeService.getGroAndAoEmployeeRecords(officeId);
    }

    @RequestMapping(value = "/api/offices/{officeId}/complaints/{type}/rated-in-current-month", method = RequestMethod.GET)
    public List<GrievanceDTO> getCurrentMonthGrievancesWithRatingsByOfficeId(@PathVariable("officeId") Long officeId, @PathVariable("type") String complaintType) {
        log.info("View Page Request : /api/offices/{}/complaints/{}/rated-in-current-month", officeId, complaintType);
        Boolean isAppeal = StringUtil.isValidString(complaintType) && complaintType.equals("appeal");
        return grievanceService.getCurrentMonthComplaintsWithRatingsByOfficeIdAndType(officeId, isAppeal);
    }

    @RequestMapping(value = "/api/offices/{officeId}/check-if-user-from-ancestors", method = RequestMethod.GET)
    public boolean checkIfUserFromParentOffices(Authentication authentication, @PathVariable("officeId") Long officeId) {
        if(authentication == null) {
            return false;
        }
        return officeService.checkIfUserOfficeExistsInAncestorsList(officeId);
    }

    @RequestMapping(value = "/api/search-office/layer-levels/{layer_level}/office-origins/{office_origin_id}/custom-layers/{custom_layer_id}/offices/{office_id}", method = RequestMethod.GET)
    public OfficeSearchContentsDTO getDropdownDataOnOfficeSearch(@PathVariable("layer_level") Integer layerLevel,
                                                                 @PathVariable("office_origin_id") Long officeOriginId,
                                                                 @PathVariable("custom_layer_id") Integer customLayerId,
                                                                 @PathVariable("office_id") Long officeId,
                                                                 @RequestParam(value = "grsEnabled", defaultValue = "true") boolean grsEnabled,
                                                                 @RequestParam(value = "showChildOfficesOnly", defaultValue = "false") Boolean showChildOfficesOnly) {
        return officeService.getDropdownDataOnOfficeSearch(layerLevel, officeOriginId, customLayerId, officeId, grsEnabled, showChildOfficesOnly);
    }

    public Integer getChildCountByParentOfficeId(Long parentOfficeId){
        return officeService.getChildCountByParentOfficeId(parentOfficeId);
    }

    public Office findOne(Long id){
        return officeService.findOne(id);
    }

    public EmployeeOffice findEmployeeOfficeByOfficeAndIsOfficeHead(Long officeId){
        return officeService.findEmployeeOfficeByOfficeAndIsOfficeHead(officeId);
    }

    public EmployeeOffice findEmployeeOfficeByOfficeAndOfficeUnitOrganogramAndStatus(
            Long officeId,
            Long officeUnitOrganogramId,
            boolean status
    ){
        return officeService.findEmployeeOfficeByOfficeAndOfficeUnitOrganogramAndStatus(
                officeId,
                officeUnitOrganogramId,
                status
        );
    }

    public Office getOffice(Long officeId){
        return officeService.getOffice(officeId);
    }

    public OfficeOrigin getOfficeOrigin(Long officeOriginId){
        return officeService.getOfficeOrigin(officeOriginId);
    }

    public ServiceOrigin getServiceOrigin(Long id){
        return officeService.getServiceOrigin(id);
    }

    public EmployeeRecord findEmployeeRecordById(Long id){
        return officeService.findEmployeeRecordById(id);
    }

    public SingleRoleDTO findSingleRole(Long officeId, Long officeUnitOrganogramId){
        return officeService.findSingleRole(officeId, officeUnitOrganogramId);
    }

    public List<OfficeLayer> getOfficeLayersByLayerLevel(Integer layerLevel){
        return officeService.getOfficeLayersByLayerLevel(layerLevel);
    }

    public List<OfficeLayer> getOfficeLayersByLayerLevelAndMinistryId(Integer layerLevel, Long ministryId){
        return officeService.getOfficeLayersByLayerLevelAndMinistryId(layerLevel, ministryId);
    }

    public List<OfficeLayer> getOfficeLayersByLayerLevelAndCustomLayerId(Integer layerLevel, Integer customLayerId){
        return officeService.getOfficeLayersByLayerLevelAndCustomLayerId(layerLevel, customLayerId);
    }

    public List<OfficeLayer> getOfficeLayersByLayerLevelAndCustomLayerIdInList(
            Integer layerLevel,
            List<Integer> customLayerIdList
    )
    {
        return officeService.getOfficeLayersByLayerLevelAndCustomLayerIdInList(layerLevel, customLayerIdList);
    }

    public Boolean isMinistryOrDivisionLevelOffice(Long officeId){
        return officeService.isMinistryOrDivisionLevelOffice(officeId);
    }

    public List<Office> getOfficesByOfficeLayer(List<OfficeLayer> officeLayers, Boolean grsEnabled){
        return officeService.getOfficesByOfficeLayer(officeLayers, grsEnabled);
    }

    public String getOfficeName(long id){
        return officeService.getOfficeName(id);
    }

    public List<Office> getOfficesByParentOfficeId(Long parentOfficeId){
        return officeService.getOfficesByParentOfficeId(parentOfficeId);
    }

    public List<Office> findByOfficeIdInList(List<Long> idList){
        return officeService.findByOfficeIdInList(idList);
    }

    public ServiceOriginDTO convertToService(CitizenCharter citizenCharter){
        return officeService.convertToService(citizenCharter);
    }

    public List<CitizenCharter> getServicesHavingServiceOfficerInfo(List<CitizenCharter> citizenCharters){
        return officeService.getServicesHavingServiceOfficerInfo(citizenCharters);
    }

    public List<ServiceOriginDTO> getServicesByServiceTypeFromOfficesCitizenCharter(
            Long officeId,
            ServiceType serviceType
    ){
        return officeService.getServicesByServiceTypeFromOfficesCitizenCharter(
                officeId,
                serviceType
        );
    }

    public BaseObjectContainerDTO getEmployeesByOffices(Long officeID){
        return officeService.getEmployeesByOffices(officeID);
    }

    public List<EmployeeRecordDTO> getListOfEmployeeRecordsFromGivenOfficeUnitOrganogram(Long officeUnitOrganogramId){
        return officeService.getListOfEmployeeRecordsFromGivenOfficeUnitOrganogram(officeUnitOrganogramId);
    }

    public OfficeUnitOrganogram getOfficeUnitOrganogramById(Long id){
        return officeService.getOfficeUnitOrganogramById(id);
    }

    public ServiceOriginDTO getServiceDTOFromService(ServiceOrigin serviceOrigin){
        return officeService.getServiceDTOFromService(serviceOrigin);
    }

    public CitizenCharterDTO getCitizenCharterDTOFromCitizenCharter(CitizenCharter citizenCharter){
        return officeService.getCitizenCharterDTOFromCitizenCharter(citizenCharter);
    }

    public boolean hasChildOffice(Long officeId){
        return officeService.hasChildOffice(officeId);
    }

    public Page<CitizenCharterDTO> getAllCitizenChartersByOffice(Pageable pageable, Long officeId){
        return officeService.getAllCitizenChartersByOffice(pageable, officeId);
    }

    public Page<EmployeeRecordDTO> getAllEmployeeRecordByOffice(Pageable pageable, Office office){
        return officeService.getAllEmployeeRecordByOffice(pageable, office);
    }

    public OfficesGRO getOfficesGRO(Long officeId){
        return officeService.getOfficesGRO(officeId);
    }

    public EmployeeRecordDTO getGRODetailsByOfficeId(Long officeId){
        return officeService.getGRODetailsByOfficeId(officeId);
    }

    public EmployeeRecordDTO getAODetailsByOfficeId(Long officeId){
        return officeService.getAODetailsByOfficeId(officeId);
    }

    public OfficesGroDTO getVisionMissionByOfficeId(Long officeId){
        return officeService.getVisionMissionByOfficeId(officeId);
    }

    public OfficesGroDTO getOfficesVisionMission(Long officeId){
        return officeService.getVisionMissionByOfficeId(officeId);
    }
    public CitizensCharterOriginDTO getOfficesVisionMission(Long layerLevel, Long officeOriginId){
        return officeService.getOfficesVisionMission(layerLevel, officeOriginId);
    }

    public OfficesGroDTO getOfficerCitizenCharter(Long officeId){
        return officeService.getOfficerCitizenCharter(officeId);
    }

    public Page<OfficesGroDTO> getOfficeSetUpMissing(
            Long missingOfficerType,
            Long officeLayers,
            Long firstSelection,
            Long secondSelection,
            Pageable pageable
    ){
        return officeService.getOfficeSetUpMissing(
                missingOfficerType,
                officeLayers,
                firstSelection,
                secondSelection,
                pageable
        );

    }

    public RoleContainerDTO getOfficeUnitOrganogramsForLoggedInUser(UserInformation userInformation){
        return officeService.getOfficeUnitOrganogramsForLoggedInUser(userInformation);
    }

    public Boolean isUpazilaLevelOffice(Long officeId){
        return officeService.isUpazilaLevelOffice(officeId);
    }

    public Boolean isZilaLevelOffice(Long officeId){
        return officeService.isZilaLevelOffice(officeId);
    }

    public GroContactInfoResponseDTO getGROcontactInfoByOfficeId(Long officeId){
        return officeService.getGROcontactInfoByOfficeId(officeId);
    }

    public GroContactInfoResponseDTO getAoContactInfoByOfficeId(Long officeId){
        return officeService.getAoContactInfoByOfficeId(officeId);
    }

    public List<GrievanceCountByItemDTO> getListOfOfficeUnitsByOfficeId(Long officeId){
        return officeService.getListOfOfficeUnitsByOfficeId(officeId);
    }

    public List<GrievanceCountByItemDTO> getGrievanceCountByCitizensCharter(Long officeId){
        return officeService.getGrievanceCountByCitizensCharter(officeId);
    }

    public List<Long> getAncestorOfficeIds(Long officeId){
        return officeService.getAncestorOfficeIds(officeId);
    }

    public OfficeUnit getOfficeUnitById(Long officeUnitId){
        return officeService.getOfficeUnitById(officeUnitId);
    }

    public OfficeUnit getOfficeUnitByIdIncludingFakeOfficeUnitForCell(Long officeUnitId){
        return officeService.getOfficeUnitByIdIncludingFakeOfficeUnitForCell(officeUnitId);
    }

    public CitizensCharterOriginDTO convertToOfficeOriginInfoDTO(CitizensCharterOrigin citizensCharterOrigin){
        return officeService.convertToOfficeOriginInfoDTO(citizensCharterOrigin);
    }

    public EmployeeOffice findEmployeeOfficeByOfficeIdAndOfficeUnitOrganogramId(
            Long officeId,
            Long officeUnitOrganogramId
    ){
        return officeService.findEmployeeOfficeByOfficeIdAndOfficeUnitOrganogramId(
                officeId,
                officeUnitOrganogramId
        );
    }

    public CitizenCharterDTO convertToCitizenCharterDTO(CitizenCharter citizenCharter){
        return officeService.convertToCitizenCharterDTO(citizenCharter);
    }

    public ServiceOriginDTO getServiceOriginDTObyId(Long id){
        return officeService.getServiceOriginDTObyId(id);
    }

    public List<Office> findByIdContainsInList(List<Long> idList){
        return officeService.findByIdContainsInList(idList);
    }

    public List<OfficeOrigin> getOfficeOriginsByLayerLevel(
            Integer layerLevel,
            Boolean grsEnabled,
            Boolean showChildOfficesOnly
    ){
        if (showChildOfficesOnly == null){
            return officeService.getOfficeOriginsByLayerLevel(layerLevel, grsEnabled);
        }
        return officeService.getOfficeOriginsByLayerLevel(layerLevel, grsEnabled, showChildOfficesOnly);
    }

    public List<Office> findByOfficeOriginId(Long officeoriginId, Boolean grsEnabled, Boolean showChildOfficesOnly){
        if (showChildOfficesOnly == null){
            return officeService.findByOfficeOriginId(
                    officeoriginId,
                    grsEnabled
            );
        }
        return officeService.findByOfficeOriginId(officeoriginId, grsEnabled, showChildOfficesOnly);
    }
    public List<Office> findByOfficeOriginIds(
            List<Long> officeoriginIds,
            Boolean grsEnabled,
            Boolean showChildOfficesOnly
    ){
        return officeService.findByOfficeOriginIds(officeoriginIds, grsEnabled, showChildOfficesOnly);
    }

    public Boolean hasAccessToAoAndSubOfficesDashboard(UserInformation userInformation, Long officeId){
        return officeService.hasAccessToAoAndSubOfficesDashboard(userInformation, officeId);
    }

    public Boolean canViewDashboardAsFieldCoordinator(Authentication authentication, Long officeId){
        return officeService.canViewDashboardAsFieldCoordinator(authentication, officeId);
    }

    public List<CellMemberInfoDTO> getCellMembersInfo(List<CellMember> cellMembers){
        return officeService.getCellMembersInfo(cellMembers);
    }

    public List<Office> getGrsEnabledDivisionOffices(Long divisionId){
        return officeService.getGrsEnabledDivisionOffices(divisionId);
    }

    public List<Office> getDivisionLevelOffices(Integer divisionId){
        return officeService.getDivisionLevelOffices(divisionId);
    }
    public List<Office> getDistrictLevelOffices(Integer divisionId, Integer districtId){
        return officeService.getDistrictLevelOffices(divisionId, districtId);
    }

    public List<Office> getUpazilaLevelOffices(Integer divisionId, Integer districtId, Integer upazilaId){
        return officeService.getUpazilaLevelOffices(divisionId, districtId, upazilaId);
    }

    public List<Office> getGrsEnabledDistrictOffices(Long districtId){
        return officeService.getGrsEnabledDistrictOffices(districtId);
    }

    public List<Office> getGRSenabledOfficesFromOffices(List<Long> officeIdList){
        return officeService.getGRSenabledOfficesFromOffices(officeIdList);
    }

    public List<Long> getOfficeIdListByGeoDivisionId(Long geoDivisionId, Long layerLevel){
        return officeService.getOfficeIdListByGeoDivisionId(geoDivisionId, layerLevel);
    }

    public List<Long> getOfficeIdListByGeoDistrictId(Long geoDistrictId, Long layerLevel){
        return officeService.getOfficeIdListByGeoDistrictId(geoDistrictId, layerLevel);
    }

    public List<Office> getOfficesByLayerLevel(
            Integer layerLevel,
            Boolean grsEnabled,
            Boolean showChildOfficesOnly
    )
    {
        if (showChildOfficesOnly == null){
            return officeService.getOfficesByLayerLevel(layerLevel, grsEnabled);
        }
        return officeService.getOfficesByLayerLevel(layerLevel, grsEnabled, showChildOfficesOnly);
    }
    
    public List<Office> getOfficesByNullableLayerLevelWithChildOffices(
            Integer layerLevel,
            Long firstSelection,
            Long secondSelection,
            Boolean grsEnabled
    ){
        return officeService.getOfficesByNullableLayerLevelWithChildOffices(
                layerLevel,
                firstSelection,
                secondSelection,
                grsEnabled
        );
    }

    public List<Office> getOfficesByLayerLevelWithChildOffices(
            Integer layerLevel,
            Long firstSelection,
            Long secondSelection,
            Boolean grsEnabled
    ){
        return officeService.getOfficesByLayerLevelWithChildOffices(
                layerLevel,
                firstSelection,
                secondSelection,
                grsEnabled
        );
    }

    public OfficeInformation getCurrentLoggedInUserInformation(){
        return officeService.getCurrentLoggedInUserInformation();
    }

    public List<OfficeSearchDTO> getDescendantOfficeSearchingData(){
        return officeService.getDescendantOfficeSearchingData();
    }

    public List<OfficeSearchDTO> getTopLayerOffices(){
        return officeService.getTopLayerOffices();
    }

    public List<OfficeSearchDTO> getGrsEnabledOfficeSearchingData(){
        return officeService.getGrsEnabledOfficeSearchingData();
    }

    public List<OfficeSearchDTO> getOfficeSearchingData(){
        return officeService.getOfficeSearchingData();
    }

    public List<OfficeSearchDTO> generateOfficeSearchingData(boolean grsEnabled){
        return officeService.generateOfficeSearchingData(grsEnabled);
    }

    public List<Office> getDescendantOfficesByMinistryId(OfficeMinistry officeMinistry){
        return officeService.getDescendantOfficesByMinistryId(officeMinistry);
    }

    public WeakHashMap<String, WeakHashMap> generateDescendantOfficesIdListOfMinistries(){
        return officeService.generateDescendantOfficesIdListOfMinistries();
    }

    public List<Office> findByDivisionIdAndOfficeMinistry(
            Integer divisionId,
            OfficeMinistry officeMinistry
    ){
        return officeService.findByDivisionIdAndOfficeMinistry(divisionId,officeMinistry);
    }
    public List<Office> findByDivisionIdAndDistrictIdAndOfficeMinistry(
            Integer divisionId,
            Integer districtId,
            OfficeMinistry officeMinistry
    ){
        return officeService.findByDivisionIdAndDistrictIdAndOfficeMinistry(
                divisionId,
                districtId,
                officeMinistry
        );
    }
    public List<Office> findByDivisionIdAndDistrictIdAndUpazilaIdAndOfficeMinistry(
            Integer divisionId,
            Integer districtId,
            Integer upazilaId,
            OfficeMinistry officeMinistry
    ){
        return officeService.findByDivisionIdAndDistrictIdAndUpazilaIdAndOfficeMinistry(
                divisionId,
                districtId,
                upazilaId,
                officeMinistry
        );
    }
}
