package com.grs.api.model.wrapperDTO;

import com.grs.api.model.request.FileDTO;
import com.grs.core.domain.grs.GrievanceForwarding;
import lombok.Data;
import java.util.List;

@Data
public class MovementAttachedFileDTO {
    private GrievanceForwarding grievanceForwarding;
    private List<FileDTO> filePaths;
}
