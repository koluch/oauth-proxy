/**
 * --------------------------------------------------------------------
 * Copyright 2015 Nikolay Mavrenkov
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * --------------------------------------------------------------------
 * <p/>
 * Author:  Nikolay Mavrenkov <koluch@koluch.ru>
 * Created: 21.10.2015 01:41
 */
package ru.koluch.oauthProxy;


import javax.net.ssl.HttpsURLConnection;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.logging.Level;
import java.util.logging.Logger;

import static ru.koluch.oauthProxy.Utils.*;

public class CallbackServlet extends HttpServlet {

    private static final Logger log = Logger.getLogger(CallbackServlet.class.getName());

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            // Parse incoming params
            String code = null;
            String state = null;
            try {
                code = getString(req, "code");
                state = getString(req, "state");
            } catch (Exception e) {
                log.log(Level.SEVERE, e.getMessage());
            }

            // Params for POST request
            String clientId = "";
            String client_secret = "";
            String redirect_uri = "http://localhost:8888/done";

            // Build body
            String body = "client_id=" + clientId + "&"
                    + "client_secret=" + client_secret + "&"
                    + "code=" + code + "&"
                    + "redirect_uri=" + redirect_uri + "&"
                    + "state=" + state;
            body = URLEncoder.encode(body, "UTF-8");
            
            // Make http request
            URL url = new URL("https://github.com/login/oauth/access_token");
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            try {
                
                urlConnection.setRequestMethod("POST");
//                urlConnection.setRequestProperty("Accept", "application/json");
                urlConnection.setRequestProperty("Content-type", "application/x-www-form-urlencoded");
                urlConnection.setRequestProperty("Content-length", String.valueOf(body.length()));
                urlConnection.setConnectTimeout(5000);
                urlConnection.setReadTimeout(5000);
                urlConnection.setDoOutput(true);
                urlConnection.setDoInput(true);
                urlConnection.connect();

                try(BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(urlConnection.getOutputStream())) {
                    bufferedOutputStream.write(body.getBytes());
                }
                try(BufferedInputStream bufferedOutputStream = new BufferedInputStream(urlConnection.getInputStream())) {
                    byte[] buf = new byte[1024 * 8];
                    int len;
                    StringBuilder responseBuilder = new StringBuilder();
                    while((len = bufferedOutputStream.read(buf)) != -1){
                        responseBuilder.append(new String(buf, 0, len, "UTF-8"));
                    }
                    log.info("Response: " + responseBuilder.toString());
                }
            } finally {
                urlConnection.disconnect();
            }

        } catch (Exception e) {
            log.log(Level.SEVERE, e.getMessage(), e);
        }
    }
    
}
