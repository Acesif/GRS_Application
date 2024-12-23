package com.grs.grs_client.gateway;

import com.grs.grs_client.model.Notification;
import com.grs.grs_client.model.NotificationDTO;
import com.grs.grs_client.model.NotificationsDTO;
import com.grs.grs_client.model.SafetyNetProgram;
import com.grs.grs_client.utils.BanglaConverter;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

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

//    public NotificationsDTO findByOfficeIdAndEmployeeRecordIdAndOfficeUnitOrganogramIdOrderByIdDesc(Long officeId, Long employeeRecordId, Long officeUnitOrganogramId) {
//        List<Notification> notifications;
//
//        String url = getUrl() + GRS_CORE_CONTEXT_PATH + "/api/notification/findByOfficeIdAndEmployeeRecordIdAndOfficeUnitOrganogramIdOrderByIdDesc/" + officeId + "/" + employeeRecordId + "/" + officeUnitOrganogramId;
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.APPLICATION_JSON);
//        headers.add("Authorization", "Bearer " + getToken());
//        HttpEntity<String> entity = new HttpEntity<>(headers);
//
//        ResponseEntity<List<Notification>> response = restTemplate.exchange(url,
//                HttpMethod.GET, entity, new ParameterizedTypeReference<List<Notification>>() {
//                });
//        notifications =  response.getBody();
//
//        return NotificationsDTO.builder()
//                .countBangla(BanglaConverter.convertToBanglaDigit(notifications.stream().filter(notification -> !notification.getIsSeen()).count()))
//                .count(notifications.stream().filter(notification -> !notification.getIsSeen()).count())
//                .notifications(
//                        notifications.stream().map(this::convertToNotificationDTO).collect(Collectors.toList())
//                )
//                .build();
//    }
    public NotificationsDTO findByOfficeIdAndEmployeeRecordIdAndOfficeUnitOrganogramIdOrderByIdDesc(Long officeId, Long employeeRecordId, Long officeUnitOrganogramId) {
        String url = getUrl() + GRS_CORE_CONTEXT_PATH + "/api/notification/findByOfficeIdAndEmployeeRecordIdAndOfficeUnitOrganogramIdOrderByIdDesc";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Authorization", "Bearer " + getToken());

        // Prepare request body
        Map<String, Long> requestBody = new HashMap<>();
        requestBody.put("officeId", officeId);
        requestBody.put("employeeRecordId", employeeRecordId);
        requestBody.put("officeUnitOrganogramId", officeUnitOrganogramId);

        HttpEntity<Map<String, Long>> entity = new HttpEntity<>(requestBody, headers);

        ResponseEntity<NotificationsDTO> response = restTemplate.exchange(
                url,
                HttpMethod.POST,
                entity,
                NotificationsDTO.class
        );

        return response.getBody(); // Return the NotificationsDTO
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
