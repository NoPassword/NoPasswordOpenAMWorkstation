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
public class CheckTokenResponse {

    @JsonProperty("Succeeded")
    private boolean succeeded;

    @JsonProperty("Message")
    private String message;

    @JsonProperty("Value")
    private Value value;

    public boolean isSucceeded() {
        return succeeded;
    }

    public void setSucceeded(boolean succeeded) {
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

        @JsonProperty("Username")
        private String username;

        @JsonProperty("IsAuthenticated")
        private boolean isAutheticated;

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public boolean isAutheticated() {
            return isAutheticated;
        }

        public void isAutheticated(boolean isAutheticated) {
            this.isAutheticated = isAutheticated;
        }

        @Override
        public String toString() {
            return "Value{" + "username=" + username + ", isAutheticated=" + isAutheticated + '}';
        }

    }

    @Override
    public String toString() {
        return "CheckTokenResponse{" + "succeeded=" + succeeded + ", message=" + message + ", value=" + value + '}';
    }

}
