package com.edw.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.annotation.RequestScope;

import java.util.HashMap;
import java.util.logging.Logger;

/**
 * <pre>
 *     com.edw.controller.IndexController
 * </pre>
 *
 * @author Muhammad Edwin < edwin at redhat dot com >
 * 21 Mar 2023 20:09
 */
@RestController
public class IndexController {
    private Logger logger = Logger.getLogger(IndexController.class.getName());

    @Autowired
    private KeyClock keyClock;

    @Bean
    @RequestScope
    public KeyClock accessToken(OAuth2AuthorizedClientService clientService) {
        Authentication authentication =
          SecurityContextHolder.getContext().getAuthentication();
        String accessToken = null;
        if (authentication.getClass()
          .isAssignableFrom(OAuth2AuthenticationToken.class)) {
            OAuth2AuthenticationToken oauthToken =
              (OAuth2AuthenticationToken) authentication;
            String clientRegistrationId =
              oauthToken.getAuthorizedClientRegistrationId();
            if (clientRegistrationId.equals("external")) {
                OAuth2AuthorizedClient client = clientService.loadAuthorizedClient(
                  clientRegistrationId, oauthToken.getName());
                accessToken = client.getAccessToken().getTokenValue();
            }
        }
        return new KeyClock(accessToken);
    }

    @GetMapping(path = "/")
    public HashMap index() {
        // get a successful user login
        OAuth2User user = ((OAuth2User)SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        logger.info(String.valueOf(user));

        OAuth2AuthenticationToken oauth2Auth = (OAuth2AuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        String idToken = ((DefaultOidcUser) oauth2Auth.getPrincipal()).getIdToken().getTokenValue();

//        OAuth2AuthorizedClient client =
//          clientService.loadAuthorizedClient(
//            oauth2Auth.getAuthorizedClientRegistrationId(),
//            oauth2Auth.getName());
//        String accessToken = client.getAccessToken().getTokenValue();
        logger.info("getAuthorizedClientRegistrationId: " + oauth2Auth.getAuthorizedClientRegistrationId());
        for (Workout workout : keyClock.getWorkout())
             logger.info("workout: " + workout);

        return new HashMap(){{
            put("hello", user.getAttribute("name"));
            put("your email is", user.getAttribute("email"));
        }};
    }


    @GetMapping(path = "/unauthenticated")
    public HashMap unauthenticatedRequests() {
        return new HashMap(){{
            put("this is", "unauthenticated endpoint");
        }};
    }

}
