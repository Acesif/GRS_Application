package com.grs.grs_client.model;

import lombok.*;

@Data
@EqualsAndHashCode(callSuper=true)
@AllArgsConstructor
@NoArgsConstructor
public class FileDerivedDTO extends FileBaseDTO {
    private String url;
    private String thumbnailUrl;
    private String deleteUrl;
    private String deleteType;
    private String previewerCode;

    @Builder
    public FileDerivedDTO(String name,
                          String size,
                          String url,
                          String thumbnailUrl,
                          String deleteUrl,
                          String deleteType, String previewerCode) {
        super(name,size);
        this.url = url;
        this.thumbnailUrl = thumbnailUrl;
        this.deleteType = deleteType;
        this.deleteUrl = deleteUrl;
        this.previewerCode = previewerCode;
    }

    public static class FileDerivedDTOBuilder extends FileBaseDTOBuilder {
        FileDerivedDTOBuilder() {
            super();
        }
    }
}
