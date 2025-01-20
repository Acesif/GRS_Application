package com.grs.mobileApp.service;

import com.grs.api.gateway.ComplainantGateway;
import com.grs.core.domain.grs.Complainant;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MobileAuthService {

    private final ComplainantGateway complainantRepo;

    public Complainant findByMobileNumber(String mobileNumber){
        return complainantRepo.findByPhoneNumber(mobileNumber);
    }
}
