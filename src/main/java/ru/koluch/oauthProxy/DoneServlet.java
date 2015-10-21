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


import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import static ru.koluch.oauthProxy.Utils.*;

public class DoneServlet extends HttpServlet {

    private static final Logger log = Logger.getLogger(DoneServlet.class.getName());

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        try(BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(resp.getOutputStream()))) {

            Map<String, String[]> params = req.getParameterMap();
            for (Map.Entry<String, String[]> entry : params.entrySet()) {
                writer.write("<h1>" + entry.getKey() + "</h1>");
                for (String[] param : params.values()) {
                    writer.write("<p>" + param + "</p>");
                }
            }
            
            log.info("DONE!");
        }

    }


}
