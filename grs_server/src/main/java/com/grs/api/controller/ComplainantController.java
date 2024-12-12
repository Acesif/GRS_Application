package com.grs.api.controller;

import com.grs.core.domain.grs.Complainant;
import com.grs.core.service.ComplainantService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/complainantService")
@RequiredArgsConstructor
public class ComplainantController {

    private final ComplainantService complainantService;

    @RequestMapping(value = "/findComplainantByPhoneNumber", method = RequestMethod.POST)
    public Complainant findComplainantByPhoneNumber(
            @RequestParam("phoneNumber") String phoneNumber
    ) {
        return complainantService.findComplainantByPhoneNumber(phoneNumber);
    }



}
