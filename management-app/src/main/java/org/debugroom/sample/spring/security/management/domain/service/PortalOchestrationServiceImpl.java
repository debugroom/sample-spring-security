package org.debugroom.sample.spring.security.management.domain.service;

import org.debugroom.sample.spring.security.common.apinfra.exception.BusinessException;
import org.debugroom.sample.spring.security.common.model.user.UserResource;
import org.debugroom.sample.spring.security.management.domain.repository.portal.PortalUserResourceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PortalOchestrationServiceImpl implements PortalOrchestrationService{

    @Autowired
    PortalUserResourceRepository portalUserResourceRepository;

    @Override
    public UserResource getUserResource(String loginId) {
        try{
            return portalUserResourceRepository.findOneByLoginId(loginId);
        }catch (BusinessException e){
            System.out.println(e);
        }
        return null;
    }
}
