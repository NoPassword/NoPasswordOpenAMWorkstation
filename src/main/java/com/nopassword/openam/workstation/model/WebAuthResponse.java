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
public class WebAuthResponse {

    @JsonProperty("Succeeded")
    private boolean succeeded;

    @JsonProperty("Message")
    private String message;

    @JsonProperty("Value")
    private Value value;

    public WebAuthResponse() {
    }

    public WebAuthResponse(boolean succeeded, String message) {
        this.succeeded = succeeded;
        this.message = message;
    }

    public boolean succeeded() {
        return succeeded;
    }

    public void succeeded(boolean succeeded) {
        this.succeeded = succeeded;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Value getValue() {
        return value;
    }

    public void setValue(Value value) {
        this.value = value;
    }

    public class Value {

        @JsonProperty("RedirectUrl")
        private String redirectUrl;

        @JsonProperty("Token")
        private String token;

        public String getRedirectUrl() {
            return redirectUrl;
        }

        public void setRedirectUrl(String redirectUrl) {
            this.redirectUrl = redirectUrl;
        }

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }

        @Override
        public String toString() {
            return "Value{" + "redirectUrl=" + redirectUrl + ", token=" + token + '}';
        }

    }

    @Override
    public String toString() {
        return "WebAuthResponse{" + "succeeded=" + succeeded + ", message=" + message + ", value=" + value + '}';
    }

}
