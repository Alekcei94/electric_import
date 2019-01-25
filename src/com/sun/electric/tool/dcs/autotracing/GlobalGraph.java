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

import com.sun.electric.tool.dcs.Accessory;
import com.sun.electric.tool.dcs.Data.LinksHolder;
import com.sun.electric.tool.dcs.Exceptions.HardFunctionalException;
import com.sun.electric.tool.dcs.SpecificStructures.ImmutableUnorderedPairOfStrings;
import com.sun.electric.tool.dcs.SpecificStructures.Pair;
import com.sun.electric.tool.dcs.autotracing.Chain.ChainElement;
import com.sun.electric.tool.dcs.autotracing.Interfaces.IConnectable;
import com.sun.electric.tool.dcs.autotracing.Interfaces.ICopyable;
import com.sun.electric.tool.dcs.autotracing.Interfaces.ITraceable;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 */
public class GlobalGraph implements ITraceable, ICopyable {

    private final String graphName;
    private final GlobalGraphStructure STRUCTURE;
    private final Deikstra DEIKSTRA;

    // BinaryHeaps are creating with the factory
    private static final BinaryHeap.BinaryHeapFactory HEAP_FAB = new BinaryHeap.BinaryHeapFactory();
    private static final Logger logger = LoggerFactory.getLogger(GlobalGraph.class);

    /**
     * Main constructor for global graph.
     *
     * @param graphName
     * @param importStructure
     */
    private GlobalGraph(String graphName, File importStructure) {
        this.STRUCTURE = new GlobalGraphStructure(importStructure);
        this.graphName = graphName;
        this.DEIKSTRA = new Deikstra();
    }

    /**
     * Copy constructor for global graph.
     *
     * @param graphName
     * @param original
     */
    private GlobalGraph(String graphName, GlobalGraph original) {
        this.STRUCTURE = new GlobalGraphStructure(original.getStructure());
        this.graphName = graphName;
        this.DEIKSTRA = new Deikstra();
    }

    /**
     * ONLY METHOD TO DEVELOP, DELETE AFTER.
     */
    public void showStructure() {
        //System.out.println(STRUCTURE.getVertMap().toString());
        //System.out.println(STRUCTURE.getAdjacencyMap().toString());
        //System.out.println(STRUCTURE.getEdgeMap().toString());
        Map map = STRUCTURE.getVertMap();
        map.forEach((key, value) -> {
            System.out.println(key + " : " + value);
        });
        map = STRUCTURE.getAdjacencyMap();
        map.forEach((key, value) -> {
            System.out.println(key + " : " + value);
        });
        map = STRUCTURE.getEdgeMap();
        map.forEach((key, value) -> {
            System.out.println(key + " : " + value);
        });
    }

    /**
     * Method to get path between verteces.
     * @param vertexFrom
     * @param pattern
     * @param doDelete
     * @return list of keys or null if path is not found.
     */
    @Override
    public List<String> getConfigurationPath(String vertexFrom, String pattern,
            boolean doDelete) {
        //Chain chain = getChainFromPattern(vertexFrom); ???
        Chain chain = STRUCTURE.getVertMap().get(vertexFrom);
        if (chain == null) {
            throw new AssertionError("Chain with name " + vertexFrom + " is not found.");
        }
        return DEIKSTRA.deikstra(chain, pattern, doDelete);
    }

    /**
     * Method to get the name of this graph.
     *
     * @return
     */
    @Override
    public String getName() {
        return graphName;
    }

    /**
     * Method to get Chain that meets the requirements.
     *
     * @param vertex
     * @return
     */
    private Chain getChainFromPattern(String vertex) {
        Map<String, Chain> map = STRUCTURE.getVertMap();
        Accessory.writeToLog(vertex);
        for (String str : map.keySet()) {
            if (str.contains(vertex)) {
                return map.get(str);
            }
        }
        throw new HardFunctionalException("Pattern isn't found in structure");
    }

    /**
     * Method to get structure from this global graph. Used in copy constructor.
     *
     * @return
     */
    private GlobalGraphStructure getStructure() {
        return STRUCTURE;
    }

    /**
     * Class to implement deikstra method and all it's internal logic
     */
    private class Deikstra {

        /**
         * Method to get distance to vertex after deikstra method. Be careful to
         * reset pathes after getting result.
         *
         * @param vertexTo
         * @return
         */
        private int getDistanceTo(String vertexTo) throws HardFunctionalException {
            if (vertexTo == null) {
                throw new HardFunctionalException("Null in Deikstra's getDistance method");
            }
            Vertex vert = STRUCTURE.getVertMap().get(vertexTo);
            if (vert.getPathCount() == Vertex.getMaxPathCount()) {
                throw new AssertionError("Algorithm failed");
            }
            return vert.getPathCount();
        }

        /**
         * Method to get minimum distance between two verteces.
         *
         * @param vertexFrom
         * @param VertexTo
         * @return minimum distance or -1 if way was not found
         */
        private Pair<String, Integer> getGraphWithMinimumDistance(Chain vertexFrom, Chain vertexTo) {
            ImmutableUnorderedPairOfStrings pair
                    = new ImmutableUnorderedPairOfStrings(vertexFrom.getSimplifiedName(), vertexTo.getSimplifiedName());
            LinkedList<String> connectionGraphs = STRUCTURE.getEdgeMap().get(pair);
            int minimum = Vertex.getMaxPathCount();
            String best = null;
            for (String conGraphStr : connectionGraphs) {
                IConnectable conGraph = STRUCTURE.getConnectionMap().get(conGraphStr);
                ChainElement ceFrom = vertexFrom.getChainElementWithContext(conGraphStr);
                ChainElement ceTo = vertexTo.getChainElementWithContext(conGraphStr);
                int distance = conGraph.getWeight(ceFrom.getPort(), ceTo.getPort());
                if (distance < minimum) {
                    minimum = distance;
                    best = conGraphStr;
                }
            }
            if (best == null) {
                throw new HardFunctionalException("Null in minimum distance.");
            }
            //TODO: check for non-existance of path
            return new Pair<>(best, minimum);
        }

        /**
         * Method to get minimum distance between two verteces.
         *
         * @param vertexFrom
         * @param VertexTo
         * @return minimum distance or -1 if way was not found
         */
        private int getDistanceBetween(Chain vertexFrom, Chain vertexTo) {
            ImmutableUnorderedPairOfStrings pair
                    = new ImmutableUnorderedPairOfStrings(vertexFrom.getSimplifiedName(), vertexTo.getSimplifiedName());
            List<String> connectionGraphs = STRUCTURE.getEdgeMap().get(pair);
            int minimum = Vertex.getMaxPathCount();
            for (String conGraphStr : connectionGraphs) {
                IConnectable conGraph = STRUCTURE.getConnectionMap().get(conGraphStr);
                ChainElement ceFrom = vertexFrom.getChainElementWithContext(conGraphStr);
                ChainElement ceTo = vertexTo.getChainElementWithContext(conGraphStr);
                int distance = conGraph.getWeight(ceFrom.getPort(), ceTo.getPort());
                if (distance < minimum) {
                    minimum = distance;
                }
            }
            return (minimum == Vertex.getMaxPathCount()) ? -1 : minimum;
        }

        private List<String> deikstra(Chain currentVertex, String pattern,
                boolean doDelete) {
            BinaryHeap heap = HEAP_FAB.createBinaryHeap();
            Chain lastVertex = null;

            currentVertex.setPathCount(0);
            heap.add(currentVertex, currentVertex.getPathCount());

            Chain closestVertex;
            while ((closestVertex = (Chain) heap.getKeyOfMinValueElement()) != null) {
                if (closestVertex.fitsPattern(pattern)) {
                    lastVertex = closestVertex;
                    break;
                }
                closestVertex.setVisited(true);
                List<Chain> connectedVerteces = getCloseVerteces(closestVertex);
                for (Chain vert : connectedVerteces) {
                    //System.out.println(vert.toString());
                    if (vert.isVisited()) {
                        continue;
                    }
                    int weight = getDistanceBetween(closestVertex, vert)
                            + (vert.getWeight() + closestVertex.getWeight()) / 2;
                    if (vert.getPathCount() > (closestVertex.getPathCount() + weight)) {
                        vert.setPathCount(closestVertex.getPathCount() + weight);
                    }
                    heap.add(vert, vert.getPathCount());
                }
            }

            if (lastVertex == null) {
                return null;
            }
            List<String> keys = deikstraBackway(currentVertex, lastVertex, doDelete);
            return keys;
        }

        /**
         * Main deikstra function, count minimum distance between VertexFrom and
         * all other verteces.
         *
         * @param vertexFrom
         */
        private List<String> deikstra(String vertexFrom, String pattern,
                boolean doDelete) throws HardFunctionalException {
            Chain currentVertex = STRUCTURE.getVertMap().get(vertexFrom);
            return deikstra(currentVertex, pattern, doDelete);
        }

        /**
         * Method to count distance between 2 specific verteces. Works only
         * inside main deikstra method.
         *
         * @param vertexFrom
         * @param VertexTo
         */
        private List<String> deikstraBackway(Chain vertexFrom,
                Chain VertexTo, boolean doDelete) throws HardFunctionalException {
            Chain currentVertex = VertexTo;

            List<Vertex> vertecesToDeleteList = new ArrayList<>();
            List<String> configPath = new ArrayList<>();
            vertecesToDeleteList.add(currentVertex);

            while (!currentVertex.getSimplifiedName().equals(vertexFrom.getSimplifiedName())) {
                List<Chain> closestVerteces = getCloseVerteces(currentVertex);
                boolean nextStep = false;
                for (Chain vert : closestVerteces) {
                    Pair<String, Integer> bestWeight
                            = getGraphWithMinimumDistance(currentVertex, vert);
                    // distance + weightFirst/2 + weightSecond/2
                    int weight = bestWeight.getSecondObject()
                            + (vert.getWeight() + currentVertex.getWeight()) / 2;
                    if ((currentVertex.getPathCount() - vert.getPathCount()) == weight) {
                        vertecesToDeleteList.add(vert);
                        
                        IConnectable conGraph = STRUCTURE.getConnectionMap().get(bestWeight.getFirstObject());
                        ChainElement ceFrom = currentVertex.getChainElementWithContext(bestWeight.getFirstObject());
                        ChainElement ceTo = vert.getChainElementWithContext(bestWeight.getFirstObject());
                        
                        List<String> pathKeys = conGraph
                                .getConfigurationPath(ceFrom.getPort(),
                                        ceTo.getPort(), doDelete);

                        configPath.addAll(pathKeys); // add each key that we passed to configuration
                        currentVertex = vert;
                        nextStep = true;
                        break;
                    }
                }
                if (nextStep == false) {
                    Accessory.showMessage("Deikstra method failed");
                    // TODO: that should be functional or step failed exception
                    logger.error("Bad vertex: " + currentVertex.toString() + " " + currentVertex.getPathCount());
                    throw new HardFunctionalException("Deikstra method failed");
                    //return null;
                }
            }

            if (doDelete) {
                deleteVerteces(vertecesToDeleteList);
            }
            return configPath;
        }

        /**
         * Just reset after any action, reset isChanged flag too.
         */
        private void resetPathes() {
            Collection<Chain> vertColl = STRUCTURE.getVertMap().values();
            for (Vertex vert : vertColl) {
                vert.setVisited(false);
                vert.resetPathCount();
            }
        }

        /**
         * Translate delete vertex command to structure's method.
         *
         * @param vertecesToDeleteList
         */
        private void deleteVerteces(List<Vertex> vertecesToDeleteList) {
            for (Vertex vert : vertecesToDeleteList) {
                STRUCTURE.deleteVertexFromStructure(vert);
            }
        }

        /**
         * Method to get nearby verteces using structure's adjacencyMap.
         *
         * @param main
         * @return
         */
        private List<Chain> getCloseVerteces(Chain main) throws HardFunctionalException {
            if (main == null) {
                throw new HardFunctionalException("Null vertex input");
            }
            List<String> vertexStringList = 
                    STRUCTURE.getAdjacencyMap().get(main.getSimplifiedName());
            //chain
            Map<String, Chain> vertMap = STRUCTURE.getVertMap();
            List<Chain> vertexAjacencyList = new ArrayList<>();
            for (String vertexString : vertexStringList) {
                vertexAjacencyList.add(vertMap.get(vertexString));
            }
            return vertexAjacencyList;
        }

        private List<Chain> getCloseVerteces(String mainName) throws HardFunctionalException {
            return getCloseVerteces(STRUCTURE.getVertMap().get(mainName));
        }
    }

    /**
     *
     */
    public static class GlobalFactory {

        private static final Map<String, GlobalGraph> graphMap = new HashMap<>();

        /**
         * Method to create only CB graph, won't be needed after.
         *
         * @param graphName
         * @return
         */
        public static GlobalGraph createGlobalGraph(String graphName) {
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
            GlobalGraph globalGraph = graphMap.get(importFile.getAbsolutePath());
            if (globalGraph == null) {
                globalGraph = new GlobalGraph(graphName, importFile);
                graphMap.put(importFile.getAbsolutePath(), globalGraph);
            }
            return new GlobalGraph(graphName, globalGraph);
        }
    }
}
