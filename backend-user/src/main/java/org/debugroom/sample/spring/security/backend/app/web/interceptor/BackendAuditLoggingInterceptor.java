package org.debugroom.sample.spring.security.backend.app.web.interceptor;

import lombok.extern.slf4j.Slf4j;
import org.debugroom.sample.spring.security.common.apinfra.log.AbstractAuditLoggingInterceptor;
import org.debugroom.sample.spring.security.common.apinfra.log.model.AuditLog;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
public class BackendAuditLoggingInterceptor extends AbstractAuditLoggingInterceptor {

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response,
                           Object handler, ModelAndView modelAndView) throws Exception {
        AuditLog auditLog = createAuditLog(request);
        log.info("Request : {}", auditLog);
    }

}
