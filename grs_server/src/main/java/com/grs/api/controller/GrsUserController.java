package com.grs.api.controller;


import com.grs.api.model.request.GrsUserDTO;
import com.grs.core.domain.grs.SuperAdmin;
import com.grs.core.service.GrsUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/grsUsers")
public class GrsUserController {
    private final GrsUserService grsUserService;

    @RequestMapping(value = "/getGrsUserInfo", method = RequestMethod.POST)
    public GrsUserDTO convertToSuperAdminDTO(@RequestBody SuperAdmin superAdmin) {
        return grsUserService.convertToSuperAdminDTO(superAdmin);
    }

    @RequestMapping(value = "/getGrsUsers", method = RequestMethod.POST)
    public SuperAdmin save(@RequestBody SuperAdmin superAdmin) {
        return grsUserService.save(superAdmin);
    }

    @RequestMapping(value = "/addGrsUsers", method = RequestMethod.POST)
    public Boolean register(@RequestBody GrsUserDTO grsUserDTO){
        return grsUserService.register(grsUserDTO);
    }

    @RequestMapping(value = "/findGrsUserByPhoneNumber", method = RequestMethod.POST)
    public SuperAdmin findGrsUserByPhoneNumber(@RequestParam String phoneNumber){
        return grsUserService.findGrsUserByPhoneNumber(phoneNumber);
    }
    @RequestMapping(value = "/findGrsUserByRole/{role}", method = RequestMethod.GET)
    public List<SuperAdmin> findByRole(@PathVariable("role") long role){
        return grsUserService.findByRole(role);
    }
}
