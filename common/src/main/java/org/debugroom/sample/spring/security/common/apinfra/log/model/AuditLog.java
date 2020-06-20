package org.debugroom.sample.spring.security.common.apinfra.log.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class AuditLog implements Serializable {

    private String serviceName;
    private String hostIpAddress;
    private String trackId;
    private String sessionId;
    private String requestIpAddress;
    private String referer;
    private String userAgent;
    private String cookie;
    private String headerDump;
    private String requestParameters;

}
