package org.debugroom.sample.spring.security.management.config;

import com.amazonaws.services.simplesystemsmanagement.AWSSimpleSystemsManagement;
import com.amazonaws.services.simplesystemsmanagement.AWSSimpleSystemsManagementClientBuilder;
import com.amazonaws.services.simplesystemsmanagement.model.GetParameterRequest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class CognitoOAuth2LoginConfig {

    @Value("${cognito.oauth2.redirect-uri}")
    private String redirectUriTemplate;

    @Value("${cognito.oauth2.jwk-set-uri}")
    private String jwkSetUri;

    @Value("${cognito.oauth2.ssm.app.client.id}")
    private String appClientIdParamName;

    @Value("${cognito.oauth2.ssm.app.client.secret}")
    private String appClientSecretParamName;

    @Value("${cognito.oauth2.ssm.domain}")
    private String domainParamName;

    @Value("${cognito.oauth2.ssm.user-pool-id}")
    private String userPoolIdParamName;

    @Value("${cloud.aws.region.static}")
    private String region;

    @Bean
    AWSSimpleSystemsManagement awsSimpleSystemsManagement(){
        return AWSSimpleSystemsManagementClientBuilder.defaultClient();
    }

    @Bean
    public ClientRegistrationRepository clientRegistrationRepository(){
        return new InMemoryClientRegistrationRepository(cognitoClientRegistration());
    }

    private ClientRegistration cognitoClientRegistration(){
        String clientId = getParameterFromParameterStore(appClientIdParamName, true);
        String clientSecret = getParameterFromParameterStore(appClientSecretParamName, true);
        String domain = getParameterFromParameterStore(domainParamName, false);
        String userPoolId = getParameterFromParameterStore(userPoolIdParamName, true);
        Map<String, Object> configurationMetadata = new HashMap<>();
        configurationMetadata.put("end_session_endpoint", domain + "/logout");
        return ClientRegistration.withRegistrationId("cognito")
                .clientId(clientId)
                .clientSecret(clientSecret)
                .clientAuthenticationMethod(ClientAuthenticationMethod.BASIC)
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .redirectUriTemplate(redirectUriTemplate)
                .scope("openid", "profile")
                .tokenUri(domain + "/oauth2/token")
                .authorizationUri(domain + "/oauth2/authorize")
                .userInfoUri(domain + "/oauth2/userInfo")
                .userNameAttributeName("cognito:username")
                .jwkSetUri(jwkSetUri + "/" + userPoolId + "/.well-known/jwks.json")
                .clientName("Cognito")
                .providerConfigurationMetadata(configurationMetadata)
                .build();
    }

    private String getParameterFromParameterStore(String paramName, boolean isEncripted){
        GetParameterRequest request = new GetParameterRequest();
        request.setName(paramName);
        request.setWithDecryption(isEncripted);
        return awsSimpleSystemsManagement().getParameter(request).getParameter().getValue();
    }
}
