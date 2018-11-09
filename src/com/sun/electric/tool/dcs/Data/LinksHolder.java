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
    
    private String memoryMapRoot = "./MAP_PACIS/";
    
    public String getMemoryMapRoot() {
        return memoryMapRoot;
    }
    
    ///////////////////////////////////////////////////////////////////////////
    
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
     * Path to global graph, typically in /electric/autotracing/global.trc
     */
    private static String MAP = "./MAP";
    
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
            case "connection box":
                return CB_PATH;
            case "config":
                return CONFIG_PATH;
            case "global graph":
                return GLOBAL_PATH;
            case "MAP":
                return MAP;
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
    
    
    //////////////////////////////////////////////////////////////////////////
    
    /**
     * Method to get absolutePath to project.
     * ex: C:/CYGELENG/Projects/5400TP094/
     * @return 
     */
    public static String getProjectPath() {
        return User.getWorkingDirectory();
    }
    
    /**
     * Method to get absolutePath to projectSimulationFolder.
     * ex: C:/CYGELENG/Projects/5400TP094/simulation
     * @return 
     */
    public static String getProjectSimulationPath() {
        return getProjectPath() + File.separator + "simulation";
    }
    
    
    /////////////////////////////////////////////////////////////////////////
    
    private static final String pathToXCAD = "c:/electric2_2/XCAD/bin";
    
    /**
     * Method to get path to xcad.
     * ex: C:/CYGELENG/xcad/bin/
     * @return 
     */
    public static String getXCADPath() {
        return pathToXCAD;
    }
}
