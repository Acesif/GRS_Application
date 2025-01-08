package com.grs.api.controller;


import com.grs.api.config.security.OISFUserDetailsServiceImpl;
import com.grs.api.model.OISFUserDTO;
import com.grs.api.model.UserInformation;
import com.grs.core.dao.EmployeeRecordDAO;
import com.grs.core.domain.doptor.UserInfo;
import com.grs.core.domain.projapoti.User;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/oisfuser")
@RequiredArgsConstructor
public class OISFUserDetailsController {

    private final OISFUserDetailsServiceImpl oisfUserDetailsService;
    private final EmployeeRecordDAO employeeRecordDAO;

    @PostMapping("/userinfo")
    public UserInformation getUserInfo(@RequestBody UserInfo userInfo){
        if (userInfo == null){
            return null;
        }
        return oisfUserDetailsService.getUserInfo(userInfo);
    }

    @PostMapping("/getUserInfoFromUser")
    public UserInformation getUserInfoFromUser(@RequestBody OISFUserDTO oisfUserDTO) {
        User user = User.builder()
                .id(oisfUserDTO.getId())
                .username(oisfUserDTO.getUsername())
                .employeeRecord(employeeRecordDAO.findEmployeeRecordById(oisfUserDTO.getEmployeeRecordId()))
                .build();
        return oisfUserDetailsService.getUserInfo(user);
    }

    @PostMapping("/getUserInformationByApi")
    public UserInformation getUserInformationByApi(
            @RequestParam("username") String username
    ) {
        return oisfUserDetailsService.getUserInformationByApi(username);
    }
}
