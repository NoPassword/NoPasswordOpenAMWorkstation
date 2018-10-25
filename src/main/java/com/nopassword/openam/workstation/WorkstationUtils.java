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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.iplanet.am.util.SystemProperties;
import com.nopassword.openam.workstation.model.CheckTokenRequest;
import com.nopassword.openam.workstation.model.CheckTokenResponse;
import com.sun.identity.shared.Constants;
import com.sun.identity.shared.debug.Debug;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 *
 * @author NoPassword
 */
public class WorkstationUtils {

    private static final Debug DEBUG = Debug.getInstance("WorkstationUtils");

    public static CheckTokenResponse checkToken(CheckTokenRequest request, String url) throws IOException {
        return doPost(url, request, CheckTokenResponse.class);

    }

    /**
     * Pure JSE REST client
     *
     * @param <T>
     * @param url URL
     * @param o Data
     * @param resultType Class
     * @return
     * @throws MalformedURLException
     * @throws IOException
     */
    public static <T> T doPost(String url, Object o, Class<T> resultType) throws MalformedURLException, IOException {
        ObjectMapper mapper = new ObjectMapper();
        String payload = mapper.writeValueAsString(o);
        DEBUG.message("Request payload=" + payload);
        URL urlx = new URL(url);
        HttpURLConnection conn = (HttpURLConnection) urlx.openConnection();
        conn.setDoOutput(true);
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        OutputStream out = conn.getOutputStream();
        out.write(payload.getBytes());
        out.flush();
        if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
            throw new RuntimeException("Failed: HTTP error code " + conn.getResponseCode());
        }
        StringBuilder input;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
            String line;
            input = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                input.append(line);
            }
        }
        conn.disconnect();
        return mapper.readValue(input.toString(), resultType);
    }

    /**
     * Constructs the server URL using the AM server protocol, host, port and
     * services deployment descriptor from {@link SystemProperties}. If any of
     * these properties are not available, an empty string is returned instead.
     *
     * @return The server URL.
     */
    protected static String getServerURL() {
        final String protocol = SystemProperties.get(Constants.AM_SERVER_PROTOCOL);
        final String host = SystemProperties.get(Constants.AM_SERVER_HOST);
        final String port = SystemProperties.get(Constants.AM_SERVER_PORT);
        final String descriptor = SystemProperties.get(Constants.AM_SERVICES_DEPLOYMENT_DESCRIPTOR);

        if (protocol != null && host != null && port != null && descriptor != null) {
            return protocol + "://" + host + ":" + port + descriptor;
        } else {
            return "";
        }
    }

}
