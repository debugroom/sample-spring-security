package org.debugroom.sample.spring.security.chat.domain.service.portal;

import org.debugroom.sample.spring.security.chat.app.model.portal.PortalInformation;
import org.debugroom.sample.spring.security.chat.domain.repository.portal.PortalUserResourceRepository;
import org.debugroom.sample.spring.security.common.model.user.UserResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PortalOrchestrationServiceImpl implements PortalOrchestrationService {

    @Autowired
    PortalUserResourceRepository portalUserResourceRepository;

    @Override
    public UserResource getUserResource(String userId) {
        return portalUserResourceRepository.findOne(userId);
    }

}
