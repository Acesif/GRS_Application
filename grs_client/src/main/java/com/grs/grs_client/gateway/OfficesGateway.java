package com.grs.grs_client.gateway;

import com.grs.grs_client.model.*;
import com.grs.grs_client.utils.CacheUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
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
        headers.add("Authorization", "Bearer " + getToken());
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
                .filter(id -> officeIdsInOfficesGro.contains(id))
                .collect(Collectors.toList());

    }
}
