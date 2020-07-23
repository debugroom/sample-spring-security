package org.debugroom.sample.spring.security.chat.app.web;

import org.debugroom.sample.spring.security.chat.app.model.portal.PortalInformation;
import org.debugroom.sample.spring.security.chat.app.web.security.CustomUserDetails;
import org.debugroom.sample.spring.security.chat.domain.service.portal.PortalOrchestrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

@Controller
public class FrontendAppPortalController {

    @Autowired
    PortalOrchestrationService portalOrchestrationService;

    @GetMapping(value = "/login")
    public String login(){
        return "login";
    }

    @GetMapping(value= "/portal")
    public String portal(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                         Model model, HttpSession httpSession){
        model.addAttribute("portalInformation", PortalInformation.builder()
                .userResource(portalOrchestrationService
                        .getUserResource(customUserDetails
                                .getUserResource()
                                .getUserId())));
        String sessionId = httpSession.getId();
        model.addAttribute("sessionId", sessionId);
        return "portal";
    }

    @GetMapping(value = "/invalidateSession")
    @ResponseBody
    public ResponseEntity<String> invalidateSession(HttpSession httpSession){
        String sessionId = httpSession.getId();
        httpSession.invalidate();
        return ResponseEntity.ok("Session Invalidate: " + sessionId);
    }

    @GetMapping(value = "/timeout")
    public String timeout(){
        return "timeout";
    }

}
