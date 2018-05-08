/* Electric(tm) VLSI Design System
 *
 * File: Constants.java
 *
 * Copyright (c) 2003, Oracle and/or its affiliates. All rights reserved.
 *
 * Electric(tm) is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 *
 * Electric(tm) is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.sun.electric.tool.dcs;

import com.sun.electric.database.text.Pref;
import com.sun.electric.tool.dcs.autotracing.Autotracing;
import java.io.File;
import java.io.IOException;

/**
 *
 * @author diivanov
 */
public class ConstantsAndPrefs {

    /**
     * Path to CB graph in .trc file.
     */
    private static final String CB_PATH = "./autotracing/CBGraph.trc";
    /**
     * Path to configuration String (list of keys), typically in /config/config.txt
     */
    private static String CONFIG_PATH = "../config/config.txt";
    /**
     * Path to global graph, typically in /electric/autotracing/global.trc
     */
    private static String GLOBAL_PATH = "./autotracing/global.trc";

    /**
     * Pref shows user choice about should app draw line between ports of key
     * when cursor is nearby or not.
     */
    private static final Pref keysIndicated = Pref.makeBooleanPref("keysIndicated",
            Autotracing.getAutotracingTool().prefs, false);
    /**
     * Pref shows user choice about should app show logs or not.
     */
    private static final Pref logging = Pref.makeBooleanPref("traceLogging",
            Autotracing.getAutotracingTool().prefs, false);

    /**
     * Path to electric folder (.../electric/).
     */
    private static final String PATH = new File("").getAbsoluteFile().getAbsolutePath();

    /**
     * Path to main folder of app.
     */
    private static final String PARENT_PATH = setStaticPath();

    /**
     * private constructor prohibits creating objects of this class.
     */
    private ConstantsAndPrefs() {
        throw new AssertionError();
    }

    public static String getPathTo(String pathTo) {
        switch (pathTo) {
            case "connection box":
                return CB_PATH;
            case "config":
                return CONFIG_PATH;
            case "global graph":
                return GLOBAL_PATH;
            default:
                return null;
        }
    }

    /**
     * Method is needed to get path to main folder e.g. C:/CYGELENG/.
     *
     * @return "C:/CYGELENG/electric/".
     */
    public static String getPath() {
        return PATH;
    }

    /**
     * Method is needed to get path to main folder e.g. C:/CYGELENG/.
     *
     * @return "C:/CYGELENG/".
     */
    public static String getParentPath() {
        return PARENT_PATH;
    }

    /**
     * Method is needed to get path to main folder e.g. C:/CYGELENG/
     */
    private static String setStaticPath() {
        try {
            return new File("..").getCanonicalFile().getAbsolutePath();
        } catch (IOException e) {
            e.printStackTrace();
            assert false;
        }
        return null;
    }

    /**
     * get keysIndicated variable
     *
     * @return
     */
    public static boolean isKeysIndicated() {
        return keysIndicated.getBoolean();
    }

    /**
     * set keysIndicated variable
     *
     * @param indicate
     */
    public static void setKeysIndicated(boolean indicate) {
        keysIndicated.setBoolean(indicate);
    }

    /**
     * get keysIndicated variable
     *
     * @return
     */
    public static boolean isLogging() {
        return logging.getBoolean();
    }

    /**
     * set keysIndicated variable
     *
     * @param writeLog
     */
    public static void setLogging(boolean writeLog) {
        logging.setBoolean(writeLog);
    }
}
