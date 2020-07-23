package org.debugroom.sample.spring.security.backend.config;

import com.amazonaws.services.simplesystemsmanagement.AWSSimpleSystemsManagement;
import com.amazonaws.services.simplesystemsmanagement.AWSSimpleSystemsManagementClientBuilder;
import com.amazonaws.services.simplesystemsmanagement.model.GetParameterRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@EnableWebSecurity
public class CognitoAuthorizationConfig extends WebSecurityConfigurerAdapter {

    @Value("${cognito.oauth2.jwk-set-uri}")
    private String jwkSetUri;

    @Value("${cognito.oauth2.ssm.user-pool-id}")
    private String userPoolIdParamName;

    @Bean
    AWSSimpleSystemsManagement awsSimpleSystemsManagement(){
        return AWSSimpleSystemsManagementClientBuilder.defaultClient();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        String userPoolId = getParameterFromParameterStore(userPoolIdParamName, true);
        http.authorizeRequests(
                authorize -> authorize.anyRequest().authenticated())
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(
                        jwt -> jwt.jwkSetUri(jwkSetUri + "/" + userPoolId + "/.well-known/jwks.json")
                ));
    }

    private String getParameterFromParameterStore(String paramName, boolean isEncripted){
        GetParameterRequest request = new GetParameterRequest();
        request.setName(paramName);
        request.setWithDecryption(isEncripted);
        return awsSimpleSystemsManagement().getParameter(request).getParameter().getValue();
    }

}
