package com.grs.api.model.wrapperDTO;

import com.grs.api.model.request.FileDTO;
import com.grs.core.domain.grs.CellMeeting;
import lombok.Data;

import java.util.List;

@Data
public class CellMeetingAttachedFilesDTO {
    private CellMeeting cellMeeting;
    private List<FileDTO> filePaths;
}
