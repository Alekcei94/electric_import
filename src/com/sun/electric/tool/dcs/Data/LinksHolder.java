/* Electric(tm) VLSI Design System
 *
 * File: LinksHolder.java
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

import com.sun.electric.tool.user.User;
import java.io.File;
import java.io.IOException;

/**
 * Class to hold links and relative/absolute addresses
 *
 * @author diivanov
 */
public class LinksHolder {

    private static LinksHolder linksHolder;

    private LinksHolder() {

    }

    public static LinksHolder getInstance() {
        if (linksHolder == null) {
            linksHolder = new LinksHolder();
        }
        return linksHolder;
    }

    private final String memoryMapRoot = "./MEMORY_MAP/";

    public String getMemoryMapRoot() {
        return memoryMapRoot;
    }

    ///////////////////////////////////////////////////////////////////////////
    /**
     * Path to CB graph in .trc file.
     */
    private static final String CB_PATH = "./autotracing/CBGraph.trc";
    /**
     * Path to configuration String (list of keys), typically in
     * /config/config.txt
     */
    private static final String CONFIG_PATH = "../config/config.txt";
    /**
     * Path to global graph, typically in /electric/autotracing/global.trc
     */
    private static final String GLOBAL_PATH = "./autotracing/global.trc";
    /*
     * Path to file config filters
     */
    private static final String FILER_CONFIG = "./filterDesign.txt";
    /*
     * Path to file all used block in scheme.
     */
    private static final String PATH_FILE_ALL_USED_BLOCKS_IN_SCHEME = "./accessory/allUsedBlocks.acc";
    /*
     * Path in file config FPGA.
     */
    private static final String PATH_FILE_CONFIG_FPGA_BLOCK = "../Projects/5400TP094/simulation/Verilog.bitnum";
    /*
     *
     */
    private static final String PATH_FILTERS = "../config/Filters/";
    /**
     * Path to electric folder (.../electric/).
     */
    private static final String PATH = new File("").getAbsoluteFile().getAbsolutePath();

    /**
     * Path to main folder of app.
     */
    private static final String PARENT_PATH = setStaticPath();

    public static String getPathTo(String pathTo) {
        switch (pathTo) {
            case "connection graph":
                return CB_PATH;
            case "config":
                return CONFIG_PATH;
            case "global graph":
                return GLOBAL_PATH;
            default:
                return null;
        }
    }

    public static String getPathInFileConfigFPGABlock() {
        return PATH_FILE_CONFIG_FPGA_BLOCK;
    }

    public static String getPathFileAllUsedBlocksInScheme() {
        return PATH_FILE_ALL_USED_BLOCKS_IN_SCHEME;
    }

    public static String getPathFiltetrs() {
        return PATH_FILTERS;
    }

    public static String getFilterConfig() {
        return FILER_CONFIG;
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

    //////////////////////////////////////////////////////////////////////////
    /**
     * Method to get absolutePath to project. ex:
     * C:/CYGELENG/Projects/5400TP094/
     *
     * @return
     */
    public static String getProjectPath() {
        return User.getWorkingDirectory();
    }

    /**
     * Method to get absolutePath to projectSimulationFolder. ex:
     * C:/CYGELENG/Projects/5400TP094/simulation
     *
     * @return
     */
    public static String getProjectSimulationPath() {
        return getProjectPath() + File.separator + "simulation";
    }

    /////////////////////////////////////////////////////////////////////////
    private static final String pathToXCAD = PARENT_PATH + "\\XCAD\\bin";

    /**
     * Method to get path to xcad. ex: C:/CYGELENG/xcad/bin/
     *
     * @return
     */
    public static String getXCADPath() {
        return pathToXCAD;
    }
}
