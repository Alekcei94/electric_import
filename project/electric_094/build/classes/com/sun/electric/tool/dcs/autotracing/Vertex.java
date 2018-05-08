/* Electric(tm) VLSI Design System
 *
 * File: Vertex.java
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

/**
 *
 * @author diivanov
 */
public class Vertex {

    private final String LABEL;
    private boolean isVisited;
    private int pathCount;

    private final int MAXPATHCOUNT = 1000;
    private int cost = 1;

    /**
     *
     * @param label
     */
    public Vertex(String label) {
        this.LABEL = label;
        this.pathCount = MAXPATHCOUNT;
    }

    /**
     * Uniq label
     *
     * @return
     */
    public String getLabel() {
        return LABEL;
    }

    /**
     * Get variable for deikstra method.
     *
     * @return
     */
    public boolean getVisited() {
        return isVisited;
    }

    /**
     * Set variable for deikstra method.
     *
     * @param isVisited
     */
    public void setVisited(boolean isVisited) {
        this.isVisited = isVisited;
    }

    /**
     * Get variable for deikstra method.
     *
     * @return
     */
    public int getPathCount() {
        return pathCount;
    }

    /**
     * Set variable for deikstra method.
     *
     * @param count
     */
    public void setPathCount(int count) {
        this.pathCount = count;
    }

    /**
     * Reset all variables to restart deikstra method.
     */
    public void resetPathCount() {
        this.pathCount = MAXPATHCOUNT;
    }

    /**
     * Get pathcount of untouched vertex.
     */
    public int getMaxPathCount() {
        return MAXPATHCOUNT;
    }
}
