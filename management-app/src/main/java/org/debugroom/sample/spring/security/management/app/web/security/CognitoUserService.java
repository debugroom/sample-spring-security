package org.debugroom.sample.spring.security.management.app.web.security;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.converter.ClaimTypeConverter;
import org.springframework.security.oauth2.core.oidc.OidcScopes;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUserAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.function.Function;

public class CognitoUserService extends OidcUserService {

    private static final String INVALID_USER_INFO_RESPONSE_ERROR_CODE = "invalid_user_info_response";
    private static final Converter<Map<String, Object>, Map<String, Object>> DEFAULT_CLAIM_TYPE_CONVERTER =
            new ClaimTypeConverter(createDefaultClaimTypeConverters());
    private Set<String> accessibleScopes = new HashSet<>(Arrays.asList(
            OidcScopes.PROFILE, OidcScopes.EMAIL, OidcScopes.ADDRESS, OidcScopes.PHONE));
    private OAuth2UserService<OAuth2UserRequest, OAuth2User> oauth2UserService = new DefaultOAuth2UserService();
    private Function<ClientRegistration, Converter<Map<String, Object>, Map<String, Object>>> claimTypeConverterFactory =
            clientRegistration -> DEFAULT_CLAIM_TYPE_CONVERTER;

    @Override
    public OidcUser loadUser(OidcUserRequest userRequest) throws OAuth2AuthenticationException {
        Assert.notNull(userRequest, "userRequest cannot be null");
        OidcUserInfo userInfo = null;
        if(this.shouldRetrieveUserInfo(userRequest)){
           OAuth2User oAuth2User = this.oauth2UserService.loadUser(userRequest);

           Map<String, Object> claims;
           Converter<Map<String, Object>, Map<String, Object>> claimTypeConverter =
                   this.claimTypeConverterFactory.apply(userRequest.getClientRegistration());
           if(claimTypeConverter != null){
               claims = claimTypeConverter.convert(oAuth2User.getAttributes());
           }else {
               claims = DEFAULT_CLAIM_TYPE_CONVERTER.convert(oAuth2User.getAttributes());
           }
           userInfo = new OidcUserInfo(claims);
        }

        if (userInfo.getSubject() == null) {
            OAuth2Error oauth2Error = new OAuth2Error(INVALID_USER_INFO_RESPONSE_ERROR_CODE);
            throw new OAuth2AuthenticationException(oauth2Error, oauth2Error.toString());
        }

        if (!userInfo.getSubject().equals(userRequest.getIdToken().getSubject())) {
            OAuth2Error oauth2Error = new OAuth2Error(INVALID_USER_INFO_RESPONSE_ERROR_CODE);
            throw new OAuth2AuthenticationException(oauth2Error, oauth2Error.toString());
        }

        Set<GrantedAuthority> authorities = new LinkedHashSet<>();
        authorities.add(new OidcUserAuthority(userRequest.getIdToken(), userInfo));
        OAuth2AccessToken token = userRequest.getAccessToken();
        for (String authority : token.getScopes()) {
            authorities.add(new SimpleGrantedAuthority("SCOPE_" + authority));
        }

        OidcUser user;
        String userNameAttributeName = userRequest.getClientRegistration()
                .getProviderDetails().getUserInfoEndpoint().getUserNameAttributeName();

        return super.loadUser(userRequest);
    }

    private boolean shouldRetrieveUserInfo(OidcUserRequest userRequest){

        if(StringUtils.isEmpty(userRequest.getClientRegistration()
                .getProviderDetails().getUserInfoEndpoint().getUri())){
           return false;
        }

        if(AuthorizationGrantType.AUTHORIZATION_CODE.equals(
                userRequest.getClientRegistration().getAuthorizationGrantType())){
            return this.accessibleScopes.isEmpty() ||
                    CollectionUtils.containsAny(userRequest.getAccessToken().getScopes(), this.accessibleScopes);
        }



        return false;

    }
}
