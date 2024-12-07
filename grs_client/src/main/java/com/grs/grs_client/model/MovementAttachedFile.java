package com.grs.grs_client.model;

import lombok.Data;

@Data
public class MovementAttachedFile {
    private Long id;
    private String filePath;
    private String fileType;
    private String fileName;
}
