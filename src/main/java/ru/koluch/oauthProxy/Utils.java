package ru.koluch.oauthProxy;

import javax.servlet.http.HttpServletRequest;
import java.text.ParseException;
import java.util.Random;

/**
 * Created by Nikolai_Mavrenkov on 21/10/15.
 */
public class Utils {
    private static Integer rnd(Random random, Integer middle, Integer spread) {
        return middle - spread + random.nextInt(spread * 2 + 1);
    }

    private static Integer getInt(HttpServletRequest req, String key, Integer def) throws ParseException {
        String parameter = req.getParameter(key);
        if(parameter!=null) {
            try {
                return Integer.valueOf(parameter);
            } catch (Throwable ignored){}
        }
        return def;
    }

    private static Integer getInt(HttpServletRequest req, String key) throws ParseException  {
        String parameter = req.getParameter(key);
        if(parameter!=null) {
            try {
                return Integer.valueOf(parameter);
            } catch (Throwable ignored){}
        }
        throw new RuntimeException("Can't parse parameter "+key+" from '" + parameter + "'");
    }

    static String getString(HttpServletRequest req, String key) throws ParseException {
        String parameter = req.getParameter(key);
        if(parameter!=null) {
            return parameter;
        }
        throw new RuntimeException("Can't parse parameter "+key+" from '" + parameter + "'");
    }
}
