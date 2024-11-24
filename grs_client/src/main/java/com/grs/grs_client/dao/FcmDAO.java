package com.grs.grs_client.dao;

import com.grs.grs_client.domain.FcmToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.grs.grs_client.repo.FcmMessageRepo;
import com.grs.grs_client.repo.FcmTokenRepo;
import com.grs.grs_client.domain.FcmMessage;

import java.util.Date;
import java.util.List;

@Service
public class FcmDAO {
    @Autowired
    private FcmTokenRepo fcmTokenRepo;
    @Autowired
    private FcmMessageRepo fcmMessageRepo;

    public FcmToken getFcmTokenObjectByDeviceToken(String deviceToken) {
        return fcmTokenRepo.findByDeviceToken(deviceToken);
    }

    public List<FcmToken> getListOfFcmTokensByUsername(String username) {
        return fcmTokenRepo.findByUsername(username);
    }

    public FcmToken findFcmTokenById(Long id) {
        return fcmTokenRepo.findOne(id);
    }

    public List<FcmToken> getListOfFcmTokensByIdInList(List<Long> ids) {
        return fcmTokenRepo.findByIdIn(ids);
    }

    public List<FcmMessage> getUnsentUnexpiredMessages() {
        return fcmMessageRepo.findByExpiredAtGreaterThanAndIsSentFalse(new Date());
    }

    public FcmToken saveFcmToken(FcmToken fcmToken) {
        return fcmTokenRepo.save(fcmToken);
    }

    public FcmMessage saveFcmMessage(FcmMessage fcmMessage) {
        return fcmMessageRepo.save(fcmMessage);
    }
}
