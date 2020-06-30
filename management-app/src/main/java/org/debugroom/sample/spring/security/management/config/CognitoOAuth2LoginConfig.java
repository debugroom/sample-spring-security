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

    private static final String REDIRECT_URI_TEMPLATE
            = "http://localhost:8080/login/oauth2/code/cognito";

    private static final String JWK_SET_URI_DOMAIN
            = "https://cognito-idp.ap-northeast-1.amazonaws.com";

    private static final String APP_CLIENT_ID
            = "sample-spring-security-cognito-custom-app-client-id";

    private static final String APP_CLIENT_SECRET
            = "sample-spring-security-cognito-custom-app-client-secret";

    private static final String DOMAIN
            = "sample-spring-security-cognito-custom-domain";

    private static final String USER_POOL_ID
            = "sample-spring-security-cognito-custom-user-pool-id";

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
        String clientId = getParameterFromParameterStore(APP_CLIENT_ID, true);
        String clientSecret = getParameterFromParameterStore(APP_CLIENT_SECRET, true);
        String domain = getParameterFromParameterStore(DOMAIN, false);
        String userPoolId = getParameterFromParameterStore(USER_POOL_ID, true);
        Map<String, Object> configurationMetadata = new HashMap<>();
        configurationMetadata.put("end_session_endpoint", domain + "/logout");
        return ClientRegistration.withRegistrationId("cognito")
                .clientId(clientId)
                .clientSecret(clientSecret)
                .clientAuthenticationMethod(ClientAuthenticationMethod.BASIC)
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .redirectUriTemplate(REDIRECT_URI_TEMPLATE)
                .scope("openid", "profile")
                .tokenUri(domain + "/oauth2/token")
                .authorizationUri(domain + "/oauth2/authorize")
                .userInfoUri(domain + "/oauth2/userInfo")
                .userNameAttributeName("cognito:username")
                .jwkSetUri(JWK_SET_URI_DOMAIN + "/" + userPoolId + "/.well-known/jwks.json")
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
