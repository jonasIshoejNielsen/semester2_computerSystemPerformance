package org.simple.software;

import java.util.logging.Level;
import java.util.logging.Logger;

public class HelperFunctions {
    public static void print (Class c, String... msg) {
        Logger logger = Logger.getLogger(c.getSimpleName());
        var sb = new StringBuilder();
        for(String m : msg) {
            sb.append(m);
        }
        logger.log(Level.INFO, sb.toString());
    }
}
