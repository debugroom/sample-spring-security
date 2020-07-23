package org.debugroom.sample.spring.security.management.domain.service;

import org.debugroom.sample.spring.security.common.model.user.UserResource;

public interface PortalOrchestrationService {

    public UserResource getUserResource(String loginId);

}
