package org.debugroom.sample.spring.security.backend.domain.service;

import java.util.List;
import java.util.Locale;
import java.util.Objects;

import org.debugroom.sample.spring.security.common.apinfra.exception.BusinessException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import org.debugroom.sample.spring.security.backend.domain.model.entity.User;
import org.debugroom.sample.spring.security.backend.domain.repository.UserRepository;

@Service
public class SampleServiceImpl implements SampleService{

    @Autowired
    MessageSource messageSource;

    @Autowired
    UserRepository userRepository;

    @Override
    public List<User> getUsers() {
        return userRepository.findAll();
    }

    @Override
    public User getUser(Long id) throws BusinessException{
        User user = userRepository.findByUserId(id);
        if(Objects.isNull(user)){
            String errorCode = "BE0001";
            throw new BusinessException(errorCode, messageSource.getMessage(
                    errorCode, new String[]{id.toString()}, Locale.getDefault()));
        }
        return user;
    }

    @Override
    public User getUserByLoginId(String loginId) throws BusinessException {
        User user = userRepository.findByLoginId(loginId);
        if(Objects.isNull(user)){
            String errorCode = "BE0002";
            throw new BusinessException(errorCode, messageSource.getMessage(
                    errorCode, new String[]{loginId}, Locale.getDefault()));
        }
        return user;
    }

}
