package org.debugroom.sample.spring.security.management.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Component
@ConfigurationProperties(prefix = "service")
public class ServiceProperties {

    private String test;

    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    public static class User {
        public String dns;
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    public static class Message {
        private String dns;
    }

    private User user;
    private Message message;

}


