package com.grs.grs_client.gateway;

import com.grs.grs_client.model.EmployeeRecord;
import com.grs.grs_client.model.EmployeeRecordDTO;
import com.grs.grs_client.model.OfficesGRO;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class OfficesGateway extends BaseRestTemplate{

    public EmployeeRecord getEmployeeRecordById(Long employeeId) {
        String url = getUrl() + "/api/employee/findEmployeeRecordById/"+employeeId;
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
}
