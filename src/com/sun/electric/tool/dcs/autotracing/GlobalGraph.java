/* Electric(tm) VLSI Design System
 *
 * File: GlobalGraph.java
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

import com.sun.electric.tool.dcs.Data.LinksHolder;
import com.sun.electric.tool.dcs.SpecificStructures.ImmutableUnorderedPairOfStrings;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author diivanov
 */
public class GlobalGraph implements IConnectable, ICopyable {

    public void deleteKeyFromGraph(String key);

    public String getName();

    public int getWeight(String elemFrom, String elemTo);

    public List<String> getConfigurationPath(String elemFrom, String elemTo, boolean doDelete);
    
    /**
     *
     */
    public static class ConnectionFactory {

        private static final Map<ImmutableUnorderedPairOfStrings, GlobalGraph> graphMap = new HashMap<>();

        // TO DO: return interface.

        /**
         * Method to create only CB graph, won't be needed after.
         * @param graphName
         * @return
         */
        public static ConnectionGraph createGlobalGraph(String graphName) {
            File importFile = new File(LinksHolder.getPathTo("global graph"));
            return createGlobalGraphFromFile(graphName, importFile);
        }

        /**
         * Factory method to create graph using name and "import graph" file.
         *
         * @param graphName
         * @param importFile
         * @return
         */
        public static GlobalGraph createGlobalGraphFromFile(String graphName, File importFile) {
            GlobalGraph conGraph = graphMap.get(
                    new ImmutableUnorderedPairOfStrings(graphName, importFile.getName()));
            if (conGraph == null) {
                conGraph = new GlobalGraph(graphName, importFile);
                graphMap.put(
                        new ImmutableUnorderedPairOfStrings(graphName, importFile.getName()), conGraph);
            }
            return new GlobalGraph(graphName, conGraph);
        }
    }
}
