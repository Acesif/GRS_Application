package com.grs.grs_client.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EmailSmsSettings {
    private Long id;
    private String type;
    private String username;
    private String password;
    private String host;
    private Long port;
    private String smtpHost;
    private String url;
    private String ms_prefix;
    private Boolean disabled;
}
