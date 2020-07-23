package org.debugroom.sample.spring.security.management.domain.repository.portal;

import java.util.List;

import org.debugroom.sample.spring.security.common.apinfra.exception.BusinessException;
import org.debugroom.sample.spring.security.common.model.user.UserResource;

public interface PortalUserResourceRepository {

    public UserResource findOne(String userId);
    public UserResource findOneByLoginId(String loginId) throws BusinessException;
    public List<UserResource> findAll();

}
