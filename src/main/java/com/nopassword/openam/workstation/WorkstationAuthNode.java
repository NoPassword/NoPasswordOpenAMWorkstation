/*
 * The contents of this file are subject to the terms of the Common Development and
 * Distribution License (the License). You may not use this file except in compliance with the
 * License.
 *
 * You can obtain a copy of the License at legal/CDDLv1.0.txt. See the License for the
 * specific language governing permission and limitations under the License.
 *
 * When distributing Covered Software, include this CDDL Header Notice in each file and include
 * the License file at legal/CDDLv1.0.txt. If applicable, add the following below the CDDL
 * Header, with the fields enclosed by brackets [] replaced by your own identifying
 * information: "Portions copyright [year] [name of copyright owner]".
 *
 * Copyright 2011-2017 ForgeRock AS. All Rights Reserved
 */
/**
 * Portions Copyright 2018 NoPassword Inc.
 */
package com.nopassword.openam.workstation;

import com.google.inject.assistedinject.Assisted;
import com.iplanet.sso.SSOException;
import com.nopassword.openam.workstation.model.AuthType;
import com.nopassword.openam.workstation.model.CheckTokenRequest;
import com.nopassword.openam.workstation.model.CheckTokenResponse;
import com.nopassword.openam.workstation.model.WebAuthRequest;
import com.nopassword.openam.workstation.model.WebAuthResponse;
import com.sun.identity.authentication.spi.AuthLoginException;
import com.sun.identity.authentication.spi.RedirectCallback;
import com.sun.identity.idm.AMIdentity;
import com.sun.identity.idm.IdRepoException;
import com.sun.identity.shared.debug.Debug;
import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.inject.Inject;
import org.forgerock.openam.annotations.sm.Attribute;
import org.forgerock.openam.auth.node.api.Action;
import static org.forgerock.openam.auth.node.api.Action.send;
import org.forgerock.openam.auth.node.api.Node;
import org.forgerock.openam.auth.node.api.NodeProcessException;
import org.forgerock.openam.auth.node.api.TreeContext;
import org.forgerock.openam.core.CoreWrapper;
import org.forgerock.openam.auth.node.api.AbstractDecisionNode;
import static org.forgerock.openam.auth.node.api.SharedStateConstants.REALM;
import static org.forgerock.openam.auth.node.api.SharedStateConstants.USERNAME;

/**
 *
 * @author NoPassword
 */
@Node.Metadata(outcomeProvider = AbstractDecisionNode.OutcomeProvider.class,
        configClass = WorkstationAuthNode.Config.class)
public class WorkstationAuthNode extends AbstractDecisionNode {

    private static final String DEBUG_FILE_NAME = WorkstationAuthNode.class.getSimpleName();
    private final Debug DEBUG = Debug.getInstance(DEBUG_FILE_NAME);
    private final WorkstationAuthNode.Config config;
    private final CoreWrapper coreWrapper;
    private static final String TOKEN = "token";

    /**
     * Configuration for the node.
     */
    public interface Config {

        @Attribute(order = 100)
        String noPasswordLoginKey();

        @Attribute(order = 200)
        String webAuthUrl();

        @Attribute(order = 300)
        String redirectUrl();

        @Attribute(order = 400)
        String checkTokenUrl();

    }

    /**
     * Guice constructor.
     *
     * @param config The node configuration.
     * @param coreWrapper
     * @throws NodeProcessException If there is an error reading the
     * configuration.
     */
    @Inject
    public WorkstationAuthNode(@Assisted WorkstationAuthNode.Config config, CoreWrapper coreWrapper) throws NodeProcessException {
        this.config = config;
        this.coreWrapper = coreWrapper;
    }

    @Override
    public Action process(TreeContext context) {
        String token = context.sharedState.get(TOKEN).asString();

        if (token != null) {
            if (token.isEmpty()) {
                return goTo(false).build();
            }

            try {
                CheckTokenResponse response = WorkstationUtils.checkToken(
                        new CheckTokenRequest(config.noPasswordLoginKey(), token), config.checkTokenUrl());
                return goTo(response.getValue().isAutheticated()).build();
            } catch (IOException ex) {
                DEBUG.error("Error checking token: " + token, ex);
                return goTo(false).build();
            }
        } else {
            String username = context.sharedState.get(USERNAME).asString();
            String realm = context.sharedState.get(REALM).asString();
            String email = getEmail(username, realm);

            if (email == null) {
                return goTo(false).build();
            }

            WebAuthResponse response = getNoPasswordAuthUrl(
                    new WebAuthRequest(
                            config.noPasswordLoginKey(), email,
                            config.redirectUrl(), AuthType.Workstation));

            if (!response.succeeded()) {
                goTo(false).build();
            }

            RedirectCallback callback = new RedirectCallback(response.getValue().getRedirectUrl(), null, "GET");
            callback.setTrackingCookie(true);
            return send(callback)
                    .replaceSharedState(context.sharedState.add(TOKEN, response.getValue().getToken()))
                    .build();
        }
    }

    /**
     * Gets NoPassword web authentication url.
     *
     * @param request Web authentication request.
     * @return Web authentication response containing web authentcation url and token.
     */
    public WebAuthResponse getNoPasswordAuthUrl(WebAuthRequest request) {
        try {
            WebAuthResponse response = WorkstationUtils
                    .doPost(config.webAuthUrl(), request, WebAuthResponse.class);

            if (!response.succeeded()) {
                DEBUG.error(response.getMessage());
            }

            return response;
        } catch (IOException ex) {
            DEBUG.message("Error getting redirect URL");
            return new WebAuthResponse(false, null);
        }
    }

    /**
     * Gets user email.
     *
     * @param username Username.
     * @param realm AM realm.
     * @return Email.
     */
    private String getEmail(String username, String realm) {
        AMIdentity userIdentity = coreWrapper.getIdentity(username, realm);

        if (userIdentity == null) {
            DEBUG.error("User not found: " + username);
            return null;
        }

        try {
            String email = getEmail(userIdentity);
            return email;
        } catch (SSOException | AuthLoginException | IdRepoException ex) {
            DEBUG.error("Error retrieving user email", ex);
            return null;
        }
    }

    /**
     * Gets user email from datastore.
     *
     * @param userIdentity User identity.
     * @return Email.
     * @throws AuthLoginException
     * @throws IdRepoException
     * @throws SSOException
     */
    private String getEmail(AMIdentity userIdentity) throws AuthLoginException, IdRepoException, SSOException {
        String email = "";
        Set<String> a = new HashSet<>();
        a.add("mail");
        a.add("email");
        Map attrs = userIdentity.getAttributes(a);
        HashSet<String> emailSet = (HashSet) attrs.get("mail");

        //check mail and email attributes
        if (!emailSet.isEmpty()) {
            email = emailSet.iterator().next();
        } else {
            emailSet = (HashSet) attrs.get("email");
            if (!emailSet.isEmpty()) {
                email = emailSet.iterator().next();
            }
        }

        //if both mail and email are empty, then get email from dn
        if (email == null || email.isEmpty()) {
            Set<String> dnSet = userIdentity.getAttribute("dn");
            email = getEmailFromDN(dnSet.iterator().next());    //userIdentity.getDn() returns null!!!
        }
        return email;
    }

    /**
     * Gets email from user DN.
     *
     * @param dn User DN.
     * @return Email.
     */
    private String getEmailFromDN(String dn) {
        if (dn == null || !dn.contains("dc=")) {
            return "";
        }

        String[] dc = dn.split(",dc=");
        int eqIdx = dn.indexOf('=');
        StringBuilder sb = new StringBuilder();
        sb.append(dn.substring(eqIdx + 1, dn.indexOf(',', eqIdx)))
                .append('@');

        for (int i = 1; i < dc.length; i++) {
            sb.append(dc[i]);

            if (i < dc.length - 1) {
                sb.append('.');
            }
        }
        return sb.toString();
    }

}
