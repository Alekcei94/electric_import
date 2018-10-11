/* Electric(tm) VLSI Design System
 *
 * File: Preferences.java
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

/**
 *
 * @author diivanov
 */
public class Preferences {

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
