package com.grs.api.model.wrapperDTO;

import com.grs.api.model.UserInformation;
import com.grs.api.model.request.PasswordChangeDTO;
import lombok.Data;

@Data
public class UpdateComplainantPasswordPayload {
    UserInformation userInformation;
    PasswordChangeDTO passwordChangeDTO;
}
