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
 * Class holds vertex logic for autotracing system.
 * @author diivanov
 */
public class Vertex {

    private final String CONTEXT;
    
    private final int MAXPATHCOUNT = 1000;
    private int pathCount = MAXPATHCOUNT;
      
    private final boolean isExternal;
    private boolean isVisited;
    

    /**
     * @param context
     */
    public Vertex(String context) {
        this.CONTEXT = context;
        isExternal = context.contains("#");
    }
    
    /**
     * Copy constructor.
     * @param vert 
     */
    public Vertex(Vertex vert) {
        this(vert.getContext());
        this.setVisited(vert.isVisited());
        this.setPathCount(vert.getPathCount());
    }

    /**
     * Uniq label.
     * @return label.
     */
    public String getContext() {
        return CONTEXT;
    }

    /**
     * Get variable for deikstra method.
     * @return true if vert was visited
     */
    public boolean isVisited() {
        return isVisited;
    }

    /**
     * Set variable for deikstra method.
     * @param isVisited
     */
    public final void setVisited(boolean isVisited) {
        this.isVisited = isVisited;
    }

    /**
     * Get variable for deikstra method.
     * @return current path value for vertice
     */
    public int getPathCount() {
        return pathCount;
    }

    /**
     * Set variable for deikstra method.
     * @param count
     */
    public final void setPathCount(int count) {
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
     * @return 
     */
    public int getMaxPathCount() {
        return MAXPATHCOUNT;
    }
    
    @Override
    public String toString() {
        String answer = "Vertex: " + this.getContext();
        return answer;
    }

    /**
     * @return the isExternal
     */
    public boolean isExternal() {
        return isExternal;
    }
}
