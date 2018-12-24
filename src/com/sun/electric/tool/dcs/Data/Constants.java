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
package com.sun.electric.tool.dcs.Data;

/**
 *
 * @author diivanov
 */
public class Constants {
    
    private static final String SPLITTER = "_";
    private static final String CONNECTION_ELEMENT_NAME = "CB";
    private static final String KEY_NAME = "key";
    private static final String FPGA_SCHEME_NAME = "FPGA";
    private static final String MAIN_CELL_NAME = "5400TP094";
    private static final String MAIN_LIBRARY_NAME = "5400TP094";

    /**
     * @return the splitter
     */
    public static String getSplitter() {
        return SPLITTER;
    }

    /**
     * @return the CONNECTION_ELEMENT_NAME
     */
    public static String getConnectionElementName() {
        return CONNECTION_ELEMENT_NAME;
    }
    
    private Constants() {
        throw new AssertionError("Constructor is not required here.");
    }

    public static String getKey() {
        return KEY_NAME;
    }

    /**
     * @return the FPGA_SCHEME_NAME
     */
    public static String getFpgaSchemeName() {
        return FPGA_SCHEME_NAME;
    }

    /**
     * @return the MAIN_CELL_NAME
     */
    public static String getMainCellName() {
        return MAIN_CELL_NAME;
    }

    /**
     * @return the MAIN_LIBRARY_NAME
     */
    public static String getMainLibraryName() {
        return MAIN_LIBRARY_NAME;
    }

}
