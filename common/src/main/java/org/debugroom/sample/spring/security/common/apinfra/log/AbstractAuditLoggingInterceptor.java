package org.debugroom.sample.spring.security.common.apinfra.log;

import javax.servlet.http.HttpServletRequest;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import org.apache.commons.lang3.StringUtils;
import org.debugroom.sample.spring.security.common.apinfra.exception.SystemException;
import org.debugroom.sample.spring.security.common.apinfra.log.model.AuditLog;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Collections;
import java.util.Objects;

public abstract class AbstractAuditLoggingInterceptor extends HandlerInterceptorAdapter {

    protected String createJsonRepresentationHeaderValue(
            HttpServletRequest request){
        JsonFactory jsonFactory = new JsonFactory();
        StringWriter stringWriter = new StringWriter();
        try{
            JsonGenerator jsonGenerator = jsonFactory.createGenerator(stringWriter);
            jsonGenerator.writeStartObject();
            for(String headerName: Collections.list(request.getHeaderNames())){
                jsonGenerator.writeStringField(headerName, request.getHeader(headerName));
            }
            jsonGenerator.writeEndObject();
            jsonGenerator.close();
            return stringWriter.toString();
        }catch (IOException e){
            throw new SystemException("SE0004", e);
        }
    }

    protected String createJsonRepresentationRequestParameters(
            HttpServletRequest request){
        JsonFactory jsonFactory = new JsonFactory();
        StringWriter stringWriter = new StringWriter();
        try{
            JsonGenerator jsonGenerator = jsonFactory.createGenerator(stringWriter);
            jsonGenerator.writeStartObject();
            for(String requestParamName: Collections.list(request.getParameterNames())){
                jsonGenerator.writeStringField(requestParamName,
                        request.getParameter(requestParamName));
            }
            jsonGenerator.writeEndObject();
            jsonGenerator.close();
            return stringWriter.toString();
        }catch (IOException e){
            throw new SystemException("SE0004", e);
        }
    }

    protected AuditLog createAuditLog(HttpServletRequest request){
        String requestIpAddress = request.getRemoteAddr();
        if (Objects.nonNull(request.getParameter("X-Forwarded-For"))){
            requestIpAddress = StringUtils.substringBefore(
                    request.getParameter("X-Forwarded-For"), ",");
        }
        String sessionId = "";
        if(Objects.nonNull(request.getSession(false))){
            sessionId = request.getSession(false).getId();
        }
        return AuditLog.builder()
                .hostIpAddress(request.getLocalAddr())
                .sessionId(sessionId)
                .requestIpAddress(requestIpAddress)
                .userAgent(request.getHeader("user-agent"))
                .referer(request.getHeader("referer"))
                .headerDump(createJsonRepresentationHeaderValue(request))
                .cookie(request.getHeader("cookie"))
                .requestParameters(createJsonRepresentationRequestParameters(request))
                .build();
    }



}
