/* Electric(tm) VLSI Design System
 *
 * File: AutotracingTool.java
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
package com.sun.electric.tool.dcs.autotracing;

import com.sun.electric.tool.Tool;

/**
 * This class is used as a controller for autotracing system All methods here is
 * just links for other classes' methods (MV model)
 */
public class AutotracingTool extends Tool {

    /**
     * the AutotracingTool tool.
     */
    private static final AutotracingTool tool = new AutotracingTool();

    /**
     * The constructor sets up the Autotracing tool.
     */
    private AutotracingTool() {
        super("autotracing");
    }

    /**
     * Method to initialize the AutotracingTool tool.
     */
    @Override
    public void init() {
    }

    /**
     * Method to retrieve the singleton associated with the AutotracingTool tool,
     *
     * @return the AutotracingTool tool.
     */
    public static AutotracingTool getAutotracingTool() {
        return tool;
    }

}
