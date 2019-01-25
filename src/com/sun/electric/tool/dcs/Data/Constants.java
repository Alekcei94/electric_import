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
    
    private static final String SPLITTER = "\\.";
    private static final String[] CONNECTION_ELEMENT_NAME = {"CB", "CB_small", "MUX4in1"};
    private static final String[] UNIQ_CONNECTION_ELEMENT_NAME = {"CB_small", "MUX4in1"};
    // TODO: form available nodeInsts from external file.
    private static final String[] AVAILABLE_NODEINSTS_IN_SCHEME = {};
    private static final String[] AVAILABLE_INVISIBLE_NODEINSTS_IN_SCHEME = {"Wire_Pin"};
    private static final String[] STARTING_NODEINST = {"Input", "Output", "Output_adr", "Output_ddr"};
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
    public static String[] getConnectionElementNames() {
        return CONNECTION_ELEMENT_NAME;
    }
    /**
     * @return the CONNECTION_ELEMENT_NAME
     */
    public static String[] getUniqConnectionElementNames() {
        return UNIQ_CONNECTION_ELEMENT_NAME;
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

    /**
     * @return the STARTING_NODEINST
     */
    public static String[] getPossibleStartingNodeInsts() {
        return STARTING_NODEINST;
    }

    /**
     * @return the AVAILABLE_INVISIBLE_NODEINSTS_IN_SCHEME
     */
    public static String[] getAvailableInvisibleNodeInstsInScheme() {
        return AVAILABLE_INVISIBLE_NODEINSTS_IN_SCHEME;
    }

}
