package com.grs.grs_client.gateway;

import com.grs.grs_client.model.*;
import com.grs.grs_client.utils.CacheUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.client.HttpClientErrorException;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class OfficesGateway extends BaseRestTemplate{

    @Autowired
    private OfficesGroGateway officesGroService;

    String GRS_CORE_CONTEXT_PATH = "/grs_server";

    public EmployeeRecord getEmployeeRecordById(Long employeeId) {
        String url = getUrl() + GRS_CORE_CONTEXT_PATH + "/api/employee/findEmployeeRecordById/"+employeeId;
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Authorization", "Bearer " + getToken());
        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<EmployeeRecord> response = restTemplate.exchange(url,
                HttpMethod.GET, entity, new ParameterizedTypeReference<EmployeeRecord>() {
                });
        return response.getBody();
    }

    public EmployeeRecordDTO getEmployeeRecord(Long id) {
        EmployeeRecord employeeRecord = this.getEmployeeRecordById(id);
        String designation = employeeRecord.getEmployeeOffices()
                .stream()
                .filter(employeeOffice -> employeeOffice.getStatus())
                .map(employeeOffice -> employeeOffice.getDesignation() + "," + employeeOffice.getOfficeUnit().getUnitNameBangla())
                .collect(Collectors.joining("\n"));

        return EmployeeRecordDTO.builder()
                .id(id.toString())
                .designation(designation)
                .email(employeeRecord.getPersonalEmail())
                .name(employeeRecord.getNameBangla())
                .phoneNumber(employeeRecord.getPersonalMobile())
                .build();
    }

    public List<OfficeSearchDTO> getGrsEnabledOfficeSearchingData() {
        return CacheUtil.getGrsEnabledOfficeSearchDTOList();
    }

    public Office getOfficeByOfficeId(Long officeId) {

        // todo: fix this url
        String url = getUrl() + GRS_CORE_CONTEXT_PATH + "/api/office/getOfficeByOfficeId/"+officeId;
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<Office> response = restTemplate.exchange(url,
                HttpMethod.GET, entity, new ParameterizedTypeReference<Office>() {
                });
        return response.getBody();
    }

    public List<Long> getAncestorOfficeIds(Long officeId) {
        List<Long> officeIds = new ArrayList();
        Office office = this.getOfficeByOfficeId(officeId);
        while (office.getParentOffice() != null) {
            office = office.getParentOffice();
            officeIds.add(office.getId());
        }
        List<Long> officeIdsInOfficesGro = this.officesGroService.findAllOffficeIds();
        return officeIds.stream()
                .filter(officeIdsInOfficesGro::contains)
                .collect(Collectors.toList());

    }

    public RoleContainerDTO getOfficeUnitOrganogramsForLoggedInUser(UserInformation userInformation) {
        String url = getUrl() + GRS_CORE_CONTEXT_PATH + "/api/user/organograms";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Authorization", "Bearer " + getToken());
        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<RoleContainerDTO> response = restTemplate.exchange(url,
                    HttpMethod.GET, entity, new ParameterizedTypeReference<RoleContainerDTO>() {
                    });
            return response.getBody();
        } catch (HttpClientErrorException e) {
            log.error("HTTP Client Error: {}", e.getResponseBodyAsString());
            throw e;
        } catch (Exception e) {
            log.error("Unexpected Error: {}", e.getMessage());
            throw e;
        }
    }

    public Office findOne(Long officeId) {

        String url = getUrl() + GRS_CORE_CONTEXT_PATH + "/findOneById/"+officeId;
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<Office> response = restTemplate.exchange(url,
                HttpMethod.GET, entity, new ParameterizedTypeReference<Office>() {
                });
        return response.getBody();
    }

    public Boolean hasAccessToAoAndSubOfficesDashboard(Long officeId) {

        String url = getUrl() + GRS_CORE_CONTEXT_PATH + "/hasAccessToAoAndSubOfficesDashboard/"+officeId;
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Authorization", "Bearer " + getToken());
        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<Boolean> response = restTemplate.exchange(url,
                HttpMethod.GET, entity, new ParameterizedTypeReference<Boolean>() {
                });
        return response.getBody();


    }

    public ServiceOrigin getServiceOriginDTObyId(Long id) {

        String url = getUrl() + GRS_CORE_CONTEXT_PATH + "/api/office/serviceOriginDTObyId/"+id;
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<ServiceOrigin> response = restTemplate.exchange(url,
                HttpMethod.GET, entity, new ParameterizedTypeReference<ServiceOrigin>() {
                });
        return response.getBody();

    }

    public List<OfficeOriginUnitDTO> getOfficeOriginUnitDTOListByOfficeOriginId(Long officeOriginId) {
        String url = getUrl() + GRS_CORE_CONTEXT_PATH + "/api/office-origins/" + officeOriginId + "/office-origin-units";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<List<OfficeOriginUnitDTO>> response = restTemplate.exchange(url,
                HttpMethod.GET, entity, new ParameterizedTypeReference<List<OfficeOriginUnitDTO>>() {
                });
        return response.getBody();
    }

    public List<OfficeOriginUnitOrganogramDTO> getOfficeOriginUnitOrganogramDTOListByOfficeOriginUnitId(Long officeOriginUnitId) {
        String url = getUrl() + GRS_CORE_CONTEXT_PATH + "/api/office-origin-units/" + officeOriginUnitId + "/office-origin-unit-organograms";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<List<OfficeOriginUnitOrganogramDTO>> response = restTemplate.exchange(url,
                HttpMethod.GET, entity, new ParameterizedTypeReference<List<OfficeOriginUnitOrganogramDTO>>() {
                });
        return response.getBody();
    }

    public List<OfficeSearchDTO> getOfficeSearchingData(){
        String url = getUrl() + GRS_CORE_CONTEXT_PATH + "/api/office/getOfficeSearchingData";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<List<OfficeSearchDTO>> response = restTemplate.exchange(url,
                HttpMethod.GET, entity, new ParameterizedTypeReference<List<OfficeSearchDTO>>() {
                });
        return response.getBody();
    }

    public ServiceOrigin getServiceOrigin(Long id) {
        String url = getUrl() + GRS_CORE_CONTEXT_PATH + "/getServiceOrigin/" + id;
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<ServiceOrigin> response = restTemplate.exchange(url,
                HttpMethod.GET, entity, new ParameterizedTypeReference<ServiceOrigin>() {
                });
        return response.getBody();
    }

    public Boolean isMinistryOrDivisionLevelOffice(Long officeId) {
        String url = getUrl() + GRS_CORE_CONTEXT_PATH + "/isMinistryOrDivisionLevelOffice/" + officeId;
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<Boolean> response = restTemplate.exchange(url,
                HttpMethod.GET, entity, new ParameterizedTypeReference<Boolean>() {
                });
        return response.getBody();
    }

    public List<OfficeSearchDTO> getDescendantOfficeSearchingData() {
        String url = getUrl() + GRS_CORE_CONTEXT_PATH + "/api/office/getDescendantOfficeSearchingData";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<List<OfficeSearchDTO>> response = restTemplate.exchange(url,
                HttpMethod.GET, entity, new ParameterizedTypeReference<List<OfficeSearchDTO>>() {
                });
        return response.getBody();
    }


    public List<OfficeSearchDTO> getTopLayerOffices() {

        String url = getUrl() + GRS_CORE_CONTEXT_PATH + "/api/office/getTopLayerOffices";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<List<OfficeSearchDTO>> response = restTemplate.exchange(url,
                HttpMethod.GET, entity, new ParameterizedTypeReference<List<OfficeSearchDTO>>() {
                });
        return response.getBody();

    }

    public Boolean canViewDashboardAsFieldCoordinator(Long officeId){

        String url = getUrl() + GRS_CORE_CONTEXT_PATH + "/api/office/canViewDashboardAsFieldCoordinator/" + officeId;
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Authorization", "Bearer " + getToken());
        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<Boolean> response = restTemplate.exchange(url,
                HttpMethod.GET, entity, new ParameterizedTypeReference<Boolean>() {
                });
        return response.getBody();

    }

    public Office getOffice( Long officeId){

        String url = getUrl() + GRS_CORE_CONTEXT_PATH + "/api/office/getOffice/" + officeId;
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<Office> response = restTemplate.exchange(url,
                HttpMethod.GET, entity, new ParameterizedTypeReference<Office>() {
                });
        return response.getBody();
    }
}
