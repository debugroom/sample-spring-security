package org.debugroom.sample.spring.security.common.apinfra.exception;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class SystemExceptionResponse implements ErrorResponse{

    private SystemException systemException;

}
