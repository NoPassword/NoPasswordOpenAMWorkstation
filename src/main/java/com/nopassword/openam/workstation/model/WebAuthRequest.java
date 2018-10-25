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
package com.nopassword.openam.workstation.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 *
 * @author NoPassword
 */
public class WebAuthRequest {

    @JsonProperty("APIKey")
    private String apiKey;

    @JsonProperty("Username")
    private String username;

    @JsonProperty("SuccessUrl")
    private String successUrl;

    @JsonProperty("NotRegisteredUrl")
    private String notRegisteredUrl;

    @JsonProperty("CancelUrl")
    private String cancelUrl;

    @JsonProperty("AuthType")
    private AuthType authType;

    public WebAuthRequest() {
    }

    public WebAuthRequest(String apiKey, String username, String successUrl, AuthType authType) {
        this.apiKey = apiKey;
        this.username = username;
        this.successUrl = successUrl;
        this.authType = authType;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getSuccessUrl() {
        return successUrl;
    }

    public void setSuccessUrl(String successUrl) {
        this.successUrl = successUrl;
    }

    public String getNotRegisteredUrl() {
        return notRegisteredUrl;
    }

    public void setNotRegisteredUrl(String notRegisteredUrl) {
        this.notRegisteredUrl = notRegisteredUrl;
    }

    public String getCancelUrl() {
        return cancelUrl;
    }

    public void setCancelUrl(String cancelUrl) {
        this.cancelUrl = cancelUrl;
    }

    public AuthType getAuthType() {
        return authType;
    }

    public void setAuthType(AuthType authType) {
        this.authType = authType;
    }

    @Override
    public String toString() {
        return "WebAuthRequest{" + "apiKey=" + apiKey + ", username=" + username + ", successUrl=" + successUrl + ", notRegisteredUrl=" + notRegisteredUrl + ", cancelUrl=" + cancelUrl + ", authType=" + authType + '}';
    }

}
