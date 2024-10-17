package com.grs.api.model.request;

import com.grs.core.domain.GrievanceCurrentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Created by User on 11/5/2017.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GrievanceForwardingNoteDTO {
    private Long grievanceId;
    private String note;
    private GrievanceCurrentStatus currentStatus;
    private List<FileDTO> files;
    private List<Long> referredFiles;
}
