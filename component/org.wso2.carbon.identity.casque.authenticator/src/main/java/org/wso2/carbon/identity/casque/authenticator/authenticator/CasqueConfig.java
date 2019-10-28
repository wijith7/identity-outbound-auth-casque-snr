/*
 * Copyright (c) 2018, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.wso2.carbon.identity.casque.authenticator.authenticator;

import org.wso2.carbon.identity.casque.authenticator.constants.CasqueAuthenticatorConstants;
import org.wso2.carbon.identity.casque.authenticator.exception.CasqueException;
import org.wso2.carbon.utils.CarbonUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.net.InetAddress;

/**
 * CASQUE SNR Authenticator Configuration
 */
public class CasqueConfig {

    public static byte[] radiusSecret = null;      /* Shared secret between the authenticator and the CASQUE Server. */
    public static InetAddress casqueAddress = null;/* IP Address of the CASQUE SNR Server. */
    public static int casquePort = 0;              /* Port used by the CASQUE SNR Server. */
    public static int localPort = 0;               /* Port for the CasqueAuthenticator to use. */
    private static boolean configLoaded = false;   /* True if the configuration has been loaded. */

    /**
     * Parse a configuration line
     *
     * @param line the line to parse
     * @throws IOException
     */
    private static void parseLine(String line) throws IOException {

        if (line.startsWith(CasqueAuthenticatorConstants.HASH)) {
            return;
        } else if (line.startsWith(CasqueAuthenticatorConstants.CONF_CASQUE_SECRET)) {
            radiusSecret = line.substring(CasqueAuthenticatorConstants.CONF_CASQUE_SECRET.length()).trim().getBytes();
        } else if (line.startsWith(CasqueAuthenticatorConstants.CONF_CASQUE_ADDRESS)) {
            String server = line.substring(CasqueAuthenticatorConstants.CONF_CASQUE_ADDRESS.length()).trim();
            casqueAddress = InetAddress.getByName(server);
        } else if (line.startsWith(CasqueAuthenticatorConstants.CONF_CASQUE_PORT)) {
            String port = line.substring(CasqueAuthenticatorConstants.CONF_CASQUE_PORT.length()).trim();
            casquePort = Integer.parseInt(port);
        } else if (line.startsWith(CasqueAuthenticatorConstants.CONF_LOCAL_PORT)) {
            String port = line.substring(CasqueAuthenticatorConstants.CONF_LOCAL_PORT.length()).trim();
            localPort = Integer.parseInt(port);
        }
    }

    /**
     * Load the Configuration file, casque.conf
     *
     * @throws CasqueException
     */
    public static void loadConfig() throws CasqueException {

        if (!configLoaded) {
            File casqueConf = new File(CarbonUtils.getCarbonConfigDirPath() + File.separator
                    + CasqueAuthenticatorConstants.CONF_FILE);

            try (InputStream input = new FileInputStream(casqueConf);
                 BufferedReader bufferedInput = new BufferedReader(new InputStreamReader(input))) {
                 String line;
                while ((line = bufferedInput.readLine()) != null) {
                    parseLine(line);
                }
                configLoaded = true;
            } catch (IOException e) {
                throw new CasqueException(" Failed to load casque.conf file ", e);
            }
        }
    }
}
