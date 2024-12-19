package com.grs.api.model.wrapperDTO;

import com.grs.core.domain.grs.GrievanceForwarding;
import com.grs.core.domain.grs.MovementAttachedFile;
import lombok.Data;

import java.util.List;

@Data
public class MovementAttachedFilesRefDTO {
    private GrievanceForwarding grievanceForwarding;
    private List<MovementAttachedFile> movementAttachedFiles;
}
