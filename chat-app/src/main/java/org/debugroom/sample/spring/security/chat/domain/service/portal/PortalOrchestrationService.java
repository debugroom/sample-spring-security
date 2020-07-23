package org.debugroom.sample.spring.security.chat.domain.service.portal;

import org.debugroom.sample.spring.security.common.model.user.UserResource;

public interface PortalOrchestrationService {

    public UserResource getUserResource(String userId);

}
