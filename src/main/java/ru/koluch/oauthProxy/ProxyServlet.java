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


import com.google.appengine.api.log.RequestLogs;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ProxyServlet extends HttpServlet {

    private static final Logger log = Logger.getLogger(ProxyServlet.class.getName());

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            String code = getString(req, "code");
            String state = getString(req, "state");

        } catch (Exception e) {
            log.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    private static Integer rnd(Random random, Integer middle, Integer spread) {
        return middle - spread + random.nextInt(spread * 2 + 1);
    }


    private static Integer getInt(HttpServletRequest req, String key, Integer def) {
        String parameter = req.getParameter(key);
        if(parameter!=null) {
            try {
                return Integer.valueOf(parameter);
            } catch (Throwable ignored){}
        }
        return def;
    }


    private static Integer getInt(HttpServletRequest req, String key) {
        String parameter = req.getParameter(key);
        if(parameter!=null) {
            try {
                return Integer.valueOf(parameter);
            } catch (Throwable ignored){}
        }
        throw new RuntimeException("Can't parse parameter "+key+" from '" + parameter + "'");
    }

    private static String getString(HttpServletRequest req, String key) {
        String parameter = req.getParameter(key);
        if(parameter!=null) {
            return parameter;
        }
        throw new RuntimeException("Can't parse parameter "+key+" from '" + parameter + "'");
    }
}
