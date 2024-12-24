package com.grs.grs_client.gateway;

import com.grs.grs_client.model.Notification;
import com.grs.grs_client.model.NotificationDTO;
import com.grs.grs_client.model.NotificationsDTO;
import com.grs.grs_client.model.SafetyNetProgram;
import com.grs.grs_client.utils.BanglaConverter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.*;

@Slf4j
@Service
public class NotificationGateway extends BaseRestTemplate{

    String GRS_CORE_CONTEXT_PATH = "/grs_server";

    public List<SafetyNetProgram> getSafetyNetPrograms() {
        String url = getUrl() + GRS_CORE_CONTEXT_PATH + "/api/citizenCharter/getSafetyNetPrograms";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Authorization", "Bearer " + getToken());
        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<List<SafetyNetProgram>> response = restTemplate.exchange(url,
                HttpMethod.GET, entity, new ParameterizedTypeReference<List<SafetyNetProgram>>() {
                });
        return response.getBody();
    }

    public NotificationsDTO findByOfficeIdAndEmployeeRecordIdAndOfficeUnitOrganogramIdOrderByIdDesc(
            Long officeId, Long employeeRecordId, Long officeUnitOrganogramId) {

        if (officeId == null || employeeRecordId == null || officeUnitOrganogramId == null) {
            throw new IllegalArgumentException("Parameters officeId, employeeRecordId, and officeUnitOrganogramId cannot be null");
        }

        String url = getUrl() + GRS_CORE_CONTEXT_PATH + "/api/findByOfficeIdAndEmployeeRecordIdAndOfficeUnitOrganogramIdOrderByIdDesc";

        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);

        HttpEntity<NotificationsDTO> entity = new HttpEntity<>(headers);

        String urlTemplate = UriComponentsBuilder.fromHttpUrl(url)
                .queryParam("officeId", "{officeId}")
                .queryParam("employeeRecordId", "{employeeRecordId}")
                .queryParam("officeUnitOrganogramId", "{officeUnitOrganogramId}")
                .buildAndExpand(officeId, employeeRecordId, officeUnitOrganogramId)
                .toUriString();

        try {
            ResponseEntity<NotificationsDTO> response = restTemplate.exchange(
                    urlTemplate,
                    HttpMethod.POST,
                    entity,
                    NotificationsDTO.class
            );

            return response.getBody();
        } catch (HttpClientErrorException e) {
            log.error("HTTP Client Error: {}", e.getResponseBodyAsString());
            throw e;
        } catch (Exception e) {
            log.error("Unexpected Error: {}", e.getMessage());
            throw e;
        }
    }


    public NotificationDTO convertToNotificationDTO(Notification notification) {
        return NotificationDTO.builder()
                .id(notification.getId())
                .complaintId(notification.getComplaintId())
                .text(notification.getText())
                .time(getTimeDifference(notification.getCreatedAt()))
                .seen(notification.getIsSeen())
                .build();
    }

    public String getTimeDifference(Date startDate) {
        StringBuffer sb = new StringBuffer();
        Date current = Calendar.getInstance().getTime();
        long diffInSeconds = (current.getTime() - startDate.getTime()) / 1000;

        long sec = (diffInSeconds >= 60 ? diffInSeconds % 60 : diffInSeconds);
        long min = (diffInSeconds = (diffInSeconds / 60)) >= 60 ? diffInSeconds % 60 : diffInSeconds;
        long hrs = (diffInSeconds = (diffInSeconds / 60)) >= 24 ? diffInSeconds % 24 : diffInSeconds;
        long days = (diffInSeconds = (diffInSeconds / 24)) >= 30 ? diffInSeconds % 30 : diffInSeconds;
        long months = (diffInSeconds = (diffInSeconds / 30)) >= 12 ? diffInSeconds % 12 : diffInSeconds;
        long years = (diffInSeconds = (diffInSeconds / 12));

        if (years > 0) {
            if (years == 1) {
                sb.append("১ বছর ");
            } else {
                sb.append(BanglaConverter.convertToBanglaDigit(years) + " বছর");
            }
            if (years <= 6 && months > 0) {
                if (months == 1) {
                    sb.append(" ১ মাস");
                } else {
                    sb.append(" " + BanglaConverter.convertToBanglaDigit(months) + " মাস");
                }
            }
        } else if (months > 0) {
            if (months == 1) {
                sb.append("১ মাস");
            } else {
                sb.append(BanglaConverter.convertToBanglaDigit(months) + " মাস");
            }
            if (months <= 6 && days > 0) {
                if (days == 1) {
                    sb.append(" ১ দিন");
                } else {
                    sb.append(" " + BanglaConverter.convertToBanglaDigit(days) + " দিন");
                }
            }
        } else if (days > 0) {
            if (days == 1) {
                sb.append("১ দিন");
            } else {
                sb.append(BanglaConverter.convertToBanglaDigit(days) + " দিন");
            }
            if (days <= 3 && hrs > 0) {
                if (hrs == 1) {
                    sb.append(" ১ ঘন্টা");
                } else {
                    sb.append(" " + BanglaConverter.convertToBanglaDigit(hrs) + " ঘন্টা");
                }
            }
        } else if (hrs > 0) {
            if (hrs == 1) {
                sb.append("১ ঘন্টা");
            } else {
                sb.append(BanglaConverter.convertToBanglaDigit(hrs) + " ঘন্টা");
            }
            if (min > 1) {
                sb.append(" " + BanglaConverter.convertToBanglaDigit(min) + " মিনিট");
            }
        } else if (min > 0) {
            if (min == 1) {
                sb.append("১ মিনিট");
            } else {
                sb.append(BanglaConverter.convertToBanglaDigit(min) + " মিনিট");
            }
        } else {
            if (sec <= 1) {
                sb.append("১ সেকেন্ড");
            } else {
                sb.append(" " + BanglaConverter.convertToBanglaDigit(sec) + " সেকেন্ড");
            }
        }

        return sb.toString();
    }

}
