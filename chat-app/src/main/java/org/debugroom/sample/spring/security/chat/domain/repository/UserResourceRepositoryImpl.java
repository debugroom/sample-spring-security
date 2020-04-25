package org.debugroom.sample.spring.security.chat.domain.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.debugroom.sample.spring.security.common.apinfra.exception.BusinessException;
import org.debugroom.sample.spring.security.common.apinfra.exception.BusinessExceptionResponse;
import org.debugroom.sample.spring.security.common.apinfra.exception.ErrorResponse;
import org.debugroom.sample.spring.security.common.apinfra.exception.SystemException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import org.debugroom.sample.spring.security.common.model.UserResource;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestOperations;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

@Component
public class UserResourceRepositoryImpl implements UserResourceRepository{

    private static final String SERVICE_NAME = "/backend/user";
    private static final String API_VERSION = "/api/v1";

    @Autowired
    MessageSource messageSource;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    RestOperations restOperations;

    @Override
    public UserResource findOne(String userId) {
        String endpoint = SERVICE_NAME + API_VERSION + "/users/{userId}";
        return restOperations.getForObject(
                UriComponentsBuilder.fromPath(endpoint).build(userId).toString(),
                UserResource.class);
    }

    @Override
    public List<UserResource> findAll() {
        String endpoint = SERVICE_NAME + API_VERSION + "/users";
        return Arrays.asList(restOperations.getForObject(endpoint,
                UserResource[].class));
    }

    @Override
    public UserResource findOneByLoginId(String loginId) throws BusinessException{
        String endpoint = SERVICE_NAME + API_VERSION + "/users/user";
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.set("loginId", loginId);
        try{
            return restOperations.getForObject(
                    UriComponentsBuilder.fromPath(endpoint).queryParams(params)
                            .build().toString(), UserResource.class);
        }catch (HttpClientErrorException e){
            try {
                ErrorResponse errorResponse = objectMapper.readValue(
                        e.getResponseBodyAsString(), ErrorResponse.class);
                if(errorResponse instanceof BusinessExceptionResponse){
                    throw ((BusinessExceptionResponse)errorResponse).getBusinessException();
                }else {
                    String errorCode = "SE0002";
                    throw new SystemException(errorCode, messageSource.getMessage(
                            errorCode, new String[]{endpoint}, Locale.getDefault()), e);
                }
            }catch (IOException e1){
                String errorCode = "SE0002";
                throw new SystemException(errorCode, messageSource.getMessage(
                        errorCode, new String[]{endpoint}, Locale.getDefault()), e);
            }
        }
    }
}
