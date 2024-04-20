package com.edw.controller;

import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

    @Value("${serverUrl}")
    private String serverUrl;

    @Value("${realm}")
    private String realm;

    @Value("${clientId}")
    private String clientId;

    @Value("${clientSecret}")
    private String clientSecret;

    @Value("${adminUser}")
    private String adminUser;

    @Value("${adminPassword}")
    private String adminPassword;

    @Autowired
    private KeyClock keyClock;

    public void talkWithKeyClockAdmin() {
        Keycloak keycloak = KeycloakBuilder.builder() //
          .serverUrl(serverUrl) //
          .realm(realm) //
          .grantType(OAuth2Constants.PASSWORD) //
          .clientId(clientId) //
          .clientSecret(clientSecret) //
          .username(adminUser) //
          .password(adminPassword) //
          .build();

        RealmResource realmResource = keycloak.realm(realm);
        UsersResource usersRessource = realmResource.users();
        System.out.println("users count: " + usersRessource.count());
    }

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

        logger.info("getAuthorizedClientRegistrationId: " + oauth2Auth.getAuthorizedClientRegistrationId());
        for (Workout workout : keyClock.getWorkout())
             logger.info("workout: " + workout);


        talkWithKeyClockAdmin();
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
