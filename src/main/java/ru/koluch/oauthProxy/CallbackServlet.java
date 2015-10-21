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


import com.google.appengine.api.datastore.*;

import javax.net.ssl.HttpsURLConnection;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.security.KeyFactory;
import java.text.ParseException;
import java.util.logging.Level;
import java.util.logging.Logger;

import static ru.koluch.oauthProxy.Utils.*;

public class CallbackServlet extends HttpServlet {

    private static final Logger log = Logger.getLogger(CallbackServlet.class.getName());

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {

            // Parse client id from url
            if(req.getPathInfo()==null) {
                throw new RuntimeException("Client id is null");
            }
            String clientId = req.getPathInfo().substring(1);

            // Parse incoming params
            String code = getString(req, "code");
            String state = getString(req, "state");

            // Fetch client secret from datastore
            DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
            String redirectUri;
            String clientSecret;
            try {
                Entity clientCridentials = datastore.get(new Entity("AppAttributes", clientId).getKey());
                if(!clientCridentials.getProperties().containsKey("client_secret")){
                    throw new RuntimeException("Property 'client_secret' is null");
                }
                if(!clientCridentials.getProperties().containsKey("redirect_uri")){
                    throw new RuntimeException("Property 'redirect_uri' is null");
                }
                clientSecret = (String) clientCridentials.getProperty("client_secret");
                redirectUri = (String) clientCridentials.getProperty("redirect_uri"); // "http://localhost:8888/callback"
            } catch (EntityNotFoundException e) {
                throw new RuntimeException("Can't find app attributes");
            }

            // Build params
            String params = "client_id=" + URLEncoder.encode(clientId, "UTF-8") + "&"
                    + "client_secret=" + URLEncoder.encode(clientSecret, "UTF-8") + "&"
                    + "code=" + URLEncoder.encode(code, "UTF-8") + "&"
                    + "redirect_uri=" + URLEncoder.encode(redirectUri, "UTF-8") + "&"
                    + "state=" + URLEncoder.encode(state, "UTF-8");

            // Make http request
            URL url = new URL("https://github.com/login/oauth/access_token?" + params);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            try {

                urlConnection.setRequestMethod("POST");
                urlConnection.setRequestProperty("Accept", "application/json");
                urlConnection.setRequestProperty("Content-type", "application/x-www-form-urlencoded");
                urlConnection.setConnectTimeout(5000);
                urlConnection.setReadTimeout(5000);
                urlConnection.setDoInput(true);
                urlConnection.connect();

                StringBuilder responseBuilder = new StringBuilder();
                try(BufferedInputStream bufferedOutputStream = new BufferedInputStream(urlConnection.getInputStream())) {
                    byte[] buf = new byte[1024 * 8];
                    int len;
                    while((len = bufferedOutputStream.read(buf)) != -1){
                        responseBuilder.append(new String(buf, 0, len, "UTF-8"));
                    }
                }

                try(BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(resp.getOutputStream()))){
                    writer.write(responseBuilder.toString());
                }
            } finally {
                urlConnection.disconnect();
            }

        } catch (ParseException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }
    
}
