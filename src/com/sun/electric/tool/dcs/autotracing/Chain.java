/* Electric(tm) VLSI Design System
 *
 * File: Chain.java
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

import com.sun.electric.tool.dcs.Accessory;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author diivanov
 */
public class Chain extends Vertex {

    private final List<String> vertsList = new ArrayList<>();
    private final String vertsFromGlobalGraph;
    private int weight = 1;
    private boolean isDeleted;

    /**
     * Constructor: parse input String to port elements, adding parse return to
     * ArrayList.
     *
     * @param vertsFromGlobalGraph
     * @param label
     */
    public Chain(String vertsFromGlobalGraph, String label) {
        super(label);
        this.vertsFromGlobalGraph = vertsFromGlobalGraph;
        String[] connectedVertices = vertsFromGlobalGraph.split(" ");
        this.vertsList.addAll(Arrays.asList(connectedVertices));
    }

    /**
     * Constructor: copy Contructor.
     *
     * @param chain
     */
    public Chain(Chain chain) {
        super(chain.getName());
        this.vertsFromGlobalGraph = chain.getLine();
        String[] connectedVertices = vertsFromGlobalGraph.split(" ");
        this.vertsList.addAll(Arrays.asList(connectedVertices));
        this.weight = chain.getWeight();
    }

    /**
     * Method to get String line of chain.
     *
     * @return
     */
    public String getLine() {
        return vertsFromGlobalGraph;
    }

    /**
     * Method to get String line of chain.
     *
     * @return
     */
    public boolean isDeleted() {
        return isDeleted == true;
    }

    /**
     * Method to get String line of chain.
     */
    public void setDeleted() {
        isDeleted = true;
    }

    /**
     * Set weight for cost function.
     *
     * @param newWeight
     */
    public void setWeight(int newWeight) {
        weight = newWeight;
    }

    /**
     * Get weight to count cost function.
     *
     * @return
     */
    public int getWeight() {
        return weight;
    }

    /**
     * Add weight to count cost function.
     */
    public void addWeight() {
        Accessory.printLog("WIncrease. " + weight);
        weight += 2;
    }
}
