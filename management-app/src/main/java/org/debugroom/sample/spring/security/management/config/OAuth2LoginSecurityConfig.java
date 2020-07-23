package org.debugroom.sample.spring.security.management.config;

import net.minidev.json.JSONArray;
import org.debugroom.sample.spring.security.management.app.web.security.CognitoLogoutSuccessHandler;
import org.debugroom.sample.spring.security.management.app.web.security.CognitoOAuth2User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.userinfo.CustomUserTypesOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.oidc.user.OidcUserAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

import java.util.*;

@EnableWebSecurity
public class OAuth2LoginSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    ClientRegistrationRepository clientRegistrationRepository;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests(
                authorize -> authorize
                        .antMatchers("/favicon.ico").permitAll()
                        .antMatchers("/webjars/*").permitAll()
                        .antMatchers("/static/*").permitAll()
                        .anyRequest().authenticated())
                .oauth2Login(oauth2 -> oauth2
                        .userInfoEndpoint(userInfo -> userInfo
                                .userAuthoritiesMapper(authoritiesMapper())
                                .oidcUserService(oidcUserService())))
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessHandler(oidcLogoutSuccessHandler()));
    }

    private LogoutSuccessHandler oidcLogoutSuccessHandler(){
        CognitoLogoutSuccessHandler cognitoLogoutSuccessHandler =
                new CognitoLogoutSuccessHandler(clientRegistrationRepository);

        cognitoLogoutSuccessHandler.setPostLogoutRedirectUri("{baseUrl}");

        return cognitoLogoutSuccessHandler;

    }

    private OidcUserService oidcUserService(){
        OidcUserService oidcUserService = new OidcUserService();
        oidcUserService.setOauth2UserService(oAuth2UserService());
        return oidcUserService;
    }

    private OAuth2UserService oAuth2UserService(){
        Map<String, Class<? extends OAuth2User>> customUserTypes = new HashMap<>();
        customUserTypes.put("cognito", CognitoOAuth2User.class);
        return new CustomUserTypesOAuth2UserService(customUserTypes);
    }

    private GrantedAuthoritiesMapper authoritiesMapper(){
        return authorities -> {
            List<GrantedAuthority> grantedAuthorities = new ArrayList<>();
            for (GrantedAuthority grantedAuthority : authorities){
                grantedAuthorities.add(grantedAuthority);
                if(OidcUserAuthority.class.isInstance(grantedAuthority)){
                    Map<String, Object> attributes = ((OidcUserAuthority)grantedAuthority).getAttributes();
                    JSONArray groups = (JSONArray) attributes.get("cognito:groups");
                    String isAdmin = (String) attributes.get("custom:isAdmin");
                    if(Objects.nonNull(groups) && groups.contains("admin")){
                        grantedAuthorities.add( new SimpleGrantedAuthority("ROLE_ADMIN"));
                    }else if (Objects.nonNull(isAdmin) && Objects.equals(isAdmin, "1")){
                        grantedAuthorities.add( new SimpleGrantedAuthority("ROLE_ADMIN"));
                    }
                }
            }
            return grantedAuthorities;
        };
    }
}
