package com.grs.grs_client.model;

import lombok.Data;

@Data
public class AttachedFile {
    private Long id;
    private String filePath;
    private String fileType;
    private String fileName;
}
