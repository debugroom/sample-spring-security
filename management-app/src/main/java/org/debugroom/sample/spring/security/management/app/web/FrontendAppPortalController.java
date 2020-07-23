package org.debugroom.sample.spring.security.management.app.web;

import org.debugroom.sample.spring.security.management.domain.service.PortalOrchestrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;

import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class FrontendAppPortalController {

    @Autowired
    OAuth2AuthorizedClientService auth2AuthorizedClientService;

    @Autowired
    PortalOrchestrationService portalOrchestrationService;

    @GetMapping("/")
    public String index(@AuthenticationPrincipal OidcUser oidcUser,
                        OAuth2AuthenticationToken authenticationToken, Model model){
        return portal(oidcUser, authenticationToken, model);
    }

    @GetMapping(value = "/portal")
    public String portal(@AuthenticationPrincipal OidcUser oidcUser,
                         OAuth2AuthenticationToken oAuth2AuthenticationToken, Model model){
        OAuth2AuthorizedClient oAuth2AuthorizedClient =
                auth2AuthorizedClientService.loadAuthorizedClient(
                        oAuth2AuthenticationToken.getAuthorizedClientRegistrationId(),
                        oAuth2AuthenticationToken.getName());
        model.addAttribute("oidcUser", oidcUser);
        model.addAttribute( auth2AuthorizedClientService
                .loadAuthorizedClient(
                        oAuth2AuthenticationToken.getAuthorizedClientRegistrationId(),
                        oAuth2AuthenticationToken.getName()));
        model.addAttribute("accessToken", auth2AuthorizedClientService
                .loadAuthorizedClient(
                        oAuth2AuthenticationToken.getAuthorizedClientRegistrationId(),
                        oAuth2AuthenticationToken.getName()).getAccessToken());
        model.addAttribute("portalInformation",
                portalOrchestrationService.getUserResource(oidcUser.getName()));
        return "portal";

    }

}
