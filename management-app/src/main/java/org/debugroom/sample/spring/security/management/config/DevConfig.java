package org.debugroom.sample.spring.security.management.config;

import org.debugroom.sample.spring.security.management.domain.ServiceProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientProvider;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientProviderBuilder;
import org.springframework.security.oauth2.client.http.OAuth2ErrorResponseErrorHandler;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository;
import org.springframework.security.oauth2.client.web.reactive.function.client.ServletOAuth2AuthorizedClientExchangeFilterFunction;
import org.springframework.security.oauth2.core.http.converter.OAuth2AccessTokenResponseHttpMessageConverter;
import org.springframework.web.client.RestOperations;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Arrays;

@Configuration
public class DevConfig {

    @Autowired
    ServiceProperties serviceProperties;

//    @Bean
//    public RestOperations userRestOperations(RestTemplateBuilder restTemplateBuilder){
//        return restTemplateBuilder
//                .messageConverters(Arrays.asList(
//                        new MappingJackson2HttpMessageConverter(),
//                        new OAuth2AccessTokenResponseHttpMessageConverter()))
//                .rootUri(serviceProperties.getUser().getDns())
//                .errorHandler(new OAuth2ErrorResponseErrorHandler())
//                .build();
//    }

    @Bean
    public OAuth2AuthorizedClientManager auth2AuthorizedClientManager(
            ClientRegistrationRepository clientRegistrationRepository,
            OAuth2AuthorizedClientRepository oAuth2AuthorizedClientRepository){

        OAuth2AuthorizedClientProvider auth2AuthorizedClientProvider =
                OAuth2AuthorizedClientProviderBuilder.builder()
                .clientCredentials()
                .build();

        DefaultOAuth2AuthorizedClientManager auth2AuthorizedClientManager =
                new DefaultOAuth2AuthorizedClientManager(
                        clientRegistrationRepository, oAuth2AuthorizedClientRepository);
        auth2AuthorizedClientManager.setAuthorizedClientProvider(auth2AuthorizedClientProvider);

        return auth2AuthorizedClientManager;
    }

    @Bean
    public WebClient userWebClient(OAuth2AuthorizedClientManager authorizedClientManager){
        ServletOAuth2AuthorizedClientExchangeFilterFunction oauth2Client =
                new ServletOAuth2AuthorizedClientExchangeFilterFunction(authorizedClientManager);
        oauth2Client.setDefaultClientRegistrationId("cognito");
        return WebClient.builder()
                .baseUrl(serviceProperties.getUser().getDns())
                .apply(oauth2Client.oauth2Configuration())
                .build();
    }

}
