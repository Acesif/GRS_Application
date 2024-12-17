package com.grs.api.model.wrapperDTO;

import com.grs.api.model.UserInformation;
import com.grs.api.model.request.BlacklistRequestBodyDTO;
import lombok.Data;

@Data
public class BlacklistRequestPayload {
    private BlacklistRequestBodyDTO blacklistRequestBodyDTO;
    private UserInformation userInformation;
}
