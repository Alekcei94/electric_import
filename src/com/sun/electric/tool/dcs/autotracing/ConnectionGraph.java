/* Electric(tm) VLSI Design System
 *
 * File: ConnectionGraph.java
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

import com.sun.electric.tool.dcs.autotracing.Interfaces.ICopyable;
import com.sun.electric.tool.dcs.autotracing.Interfaces.IConnectable;
import com.sun.electric.tool.dcs.autotracing.Interfaces.ITraceable;
import com.sun.electric.tool.dcs.Accessory;
import com.sun.electric.tool.dcs.Data.LinksHolder;
import com.sun.electric.tool.dcs.SpecificStructures.ImmutableUnorderedPairOfStrings;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Class to implement connection graph structure (ConnectionBox, mux4_1 etc).
 * Contract: reset shouldn't be initiated outside of object. Contract: all
 * methods that change internal environment must toggle isChanged flag.
 * Contract: all methods that transfer information to user must check isChanged
 * and throw Exception if true.
 *
 * @author diivanov
 */
public class ConnectionGraph implements IConnectable, ICopyable {

    // Main structure of connection graph including import methods
    private final ConnectionGraphStructure STRUCTURE;
    // Object to manage deikstra algorithm
    private final Deikstra DEIKSTRA;
    // Object to manage all weights between external verteces.
    private final LinksMatrix LINKS_MATRIX;
    private final String graphName;

    // BinaryHeaps are creating with the factory
    private static final BinaryHeap.BinaryHeapFactory HEAP_FAB = new BinaryHeap.BinaryHeapFactory();

    /**
     * ONLY METHOD TO DEVELOP, DELETE AFTER.
     */
    public void showStructure() {
        System.out.println(STRUCTURE.getVertMap().toString());
        System.out.println(STRUCTURE.getAdjacencyMap().toString());
        System.out.println(STRUCTURE.getEdgeMap().toString());
        System.out.println(STRUCTURE.getExternalVertexList().toString());
    }

    /**
     * Method to delete local external vertex if it was used in global graph
     *
     * @param key
     */
    @Override
    public void deleteKeyFromGraph(String key) {
        STRUCTURE.deleteVertexFromStructure(key);
    }

    /**
     * Method to get distance between verteces.
     *
     * @param elemFrom
     * @param elemTo
     * @return
     */
    @Override
    public int getWeight(String elemFrom, String elemTo) {
        return LINKS_MATRIX.getWeight(elemFrom, elemTo);
    }

    /**
     * Method to get all addresses between verteces, remove them from graph if
     * needed.
     *
     * @param elemFrom
     * @param elemTo
     * @param doDelete
     * @return
     */
    @Override
    public List<String> getConfigurationPath(String elemFrom, String elemTo, boolean doDelete) {
        return DEIKSTRA.deikstra(elemFrom, elemTo, doDelete);
    }

    /**
     * Import default graph while constructing new object.
     */
    private ConnectionGraph(String graphName, File importFile) {
        STRUCTURE = new ConnectionGraphStructure(importFile);
        DEIKSTRA = new Deikstra();
        LINKS_MATRIX = new LinksMatrix();
        this.graphName = graphName;
    }

    /**
     * Copy constructor for connection graph.
     */
    private ConnectionGraph(String graphName, ConnectionGraph conGraph) {
        this.STRUCTURE = new ConnectionGraphStructure(conGraph.getStructure());
        DEIKSTRA = new Deikstra();
        LINKS_MATRIX = new LinksMatrix();
        this.graphName = graphName;
    }

    /**
     * Method to get structure from this connection graph. Used in copy
     * constructor.
     *
     * @return
     */
    private ConnectionGraphStructure getStructure() {
        return STRUCTURE;
    }

    /**
     * Returns label;
     *
     * @return
     */
    @Override
    public String getName() {
        return graphName;
    }

    /**
     * Links matrix class describes all-to-all pathes in graph. IMPORTANT:
     * should be updated with every change to structure.
     */
    private class LinksMatrix {
        
        // map keeps all internalLinksMatrix that shows distance from each vertex to all others.
        private Map<ImmutableUnorderedPairOfStrings, Integer> mapOfLinks;

        /**
         * Method to update mapOfLinks that is used in getWeight() of connection
         * in local graph. For each vertex method initiates deikstra and get
         * distances to other verteces.
         */
        private void updateLinksMatrix() {
            mapOfLinks = new HashMap<>();
            List<String> externalVertList = STRUCTURE.getExternalVertexList();
            // TO DO: fix problem with double running vert1->vert2 and vert2->vert1
            for (String vert1 : externalVertList) {
                DEIKSTRA.deikstra(vert1);
                for (String vert2 : externalVertList) {
                    if (!vert1.equals(vert2)) {
                        mapOfLinks.put(new ImmutableUnorderedPairOfStrings(vert1, vert2),
                                DEIKSTRA.getDistanceTo(vert2));
                    }

                }
                DEIKSTRA.resetPathes();
            }
        }

        /**
         * Method to get distance between verteces, using updateLinksMatrix if needed.
         * @param vertexFrom
         * @param vertexTo
         * @return 
         */
        private int getWeight(String vertexFrom, String vertexTo) {
            return mapOfLinks.get(new ImmutableUnorderedPairOfStrings(vertexFrom, vertexTo));
        }
    }

    /**
     * Class to implement deikstra method and all it's internal logic
     */
    private class Deikstra {
        /**
         * Method to get distance to vertex after deikstra method.
         * @param vertexTo
         * @return 
         */
        private int getDistanceTo(String vertexTo) {
            Vertex vert = STRUCTURE.getVertMap().get(vertexTo);
            if (vert.getPathCount() == vert.getMaxPathCount()) {
                throw new AssertionError("Algorithm failed");
            }
            return vert.getPathCount();
        }

        /**
         * Main deikstra function, count minimum distance between VertexFrom and
         * VertexTo. IMPORTANT: don't forget to reset graph after you finish
         * your operations.
         *
         * @param vertexFrom
         */
        private List<String> deikstra(String vertexFrom, String vertexTo, boolean doDelete) {
            deikstra(vertexFrom);
            List<String> configList = deikstraBackway(STRUCTURE.getVertMap().get(vertexFrom),
                    STRUCTURE.getVertMap().get(vertexTo), doDelete);
            resetPathes();
            return configList;
        }

        /**
         * Main deikstra function, count minimum distance between VertexFrom and
         * all other verteces. IMPORTANT: don't forget to reset graph after you
         *
         * @param vertexFrom
         */
        private void deikstra(String vertexFrom) {
            BinaryHeap heap = HEAP_FAB.createBinaryHeap();
            //init
            Vertex currentVertex = STRUCTURE.getVertMap().get(vertexFrom);
            //System.out.println(currentVertex.toString());

            currentVertex.setPathCount(0);
            heap.add(currentVertex, currentVertex.getPathCount());

            Vertex closestVertex;
            while ((closestVertex = heap.getKeyOfMinValueElement()) != null) {
                //System.out.println(closestVertex.toString());
                closestVertex.setVisited(true);
                List<Vertex> closestVerteces = getCloseVerteces(closestVertex);
                for (Vertex vert : closestVerteces) {
                    //System.out.println(vert.toString());
                    if (vert.isVisited()) {
                        continue;
                    }
                    if (vert.getPathCount() > (closestVertex.getPathCount() + 1)) {
                        vert.setPathCount(closestVertex.getPathCount() + 1);
                        //System.out.println(vert.getContext());
                        //System.out.println(vert.getPathCount());
                    }
                    heap.add(vert, vert.getPathCount());
                }
            }
        }

        /**
         * Method to count distance between 2 specific verteces. Works only
         * inside main deikstra method.
         *
         * @param vertexFrom
         * @param VertexTo
         */
        private List<String> deikstraBackway(Vertex vertexFrom, Vertex VertexTo, boolean doDelete) {
            Map<ImmutableUnorderedPairOfStrings, String> edgeMap = STRUCTURE.getEdgeMap();
            Vertex currentVertex = VertexTo;

            List<Vertex> vertecesToDeleteList = new ArrayList<>();
            vertecesToDeleteList.add(currentVertex);

            List<String> configPath = new ArrayList<>();
            do {
                List<Vertex> closestVerteces = getCloseVerteces(currentVertex);
                boolean nextStep = false;
                for (Vertex vert : closestVerteces) {
                    if ((currentVertex.getPathCount() - vert.getPathCount()) == 1) {
                        vertecesToDeleteList.add(vert);
                        String key = edgeMap.get(
                                new ImmutableUnorderedPairOfStrings(currentVertex.getContext(), vert.getContext()));
                        configPath.add(key); // add each key that we passed to configuration
                        currentVertex = vert;
                        nextStep = true;
                        break;
                    }
                }
                if (nextStep == false) {
                    Accessory.showMessage("Deikstra method failed");
                    // TODO: that should be functional or step failed exception
                    System.out.println("Bad vertex: " + currentVertex.toString() + " " + currentVertex.getPathCount());
                    throw new RuntimeException("Deikstra method failed");
                    //return null;
                }
            } while (!currentVertex.getContext().equals(vertexFrom.getContext()));

            if (doDelete) {
                deleteVerteces(vertecesToDeleteList);
            }
            return configPath;
        }

        /**
         * Just reset after any action, reset isChanged flag too.
         */
        private void resetPathes() {
            Collection<Vertex> vertColl = STRUCTURE.getVertMap().values();
            for (Vertex vert : vertColl) {
                vert.setVisited(false);
                vert.resetPathCount();
            }
        }

        /**
         * Translate delete vertex command to structure's method.
         * @param vertecesToDeleteList 
         */
        private void deleteVerteces(List<Vertex> vertecesToDeleteList) {
            for (Vertex vert : vertecesToDeleteList) {
                STRUCTURE.deleteVertexFromStructure(vert);
            }
        }

        /**
         * Method to get nearby verteces using structure's adjacencyMap.
         * @param main
         * @return
         */
        private List<Vertex> getCloseVerteces(Vertex main) {
            List<String> vertexStringList = STRUCTURE.getAdjacencyMap().get(main.getContext());
            Map<String, Vertex> vertMap = STRUCTURE.getVertMap();
            List<Vertex> vertexAjacencyList = new ArrayList<>();
            for (String vertexString : vertexStringList) {
                vertexAjacencyList.add(vertMap.get(vertexString));
            }
            return vertexAjacencyList;
        }

        private List<Vertex> getCloseVerteces(String mainName) {
            return getCloseVerteces(STRUCTURE.getVertMap().get(mainName));
        }
    }


    /**
     *
     */
    public static class ConnectionFactory {

        private static final Map<ImmutableUnorderedPairOfStrings, ConnectionGraph> graphMap = new HashMap<>();

        // TO DO: return interface.

        /**
         * Method to create only CB graph, won't be needed after.
         * @param graphName
         * @return
         */
        public static ConnectionGraph createConnectionGraph(String graphName) {
            File importFile = new File(LinksHolder.getPathTo("connection graph"));
            return createConnectionGraphFromFile(graphName, importFile);
        }

        /**
         * Factory method to create graph using name and "import graph" file.
         *
         * @param graphName
         * @param importFile
         * @return
         */
        public static ConnectionGraph createConnectionGraphFromFile(String graphName, File importFile) {
            ConnectionGraph conGraph = graphMap.get(
                    new ImmutableUnorderedPairOfStrings(graphName, importFile.getName()));
            if (conGraph == null) {
                conGraph = new ConnectionGraph(graphName, importFile);
                graphMap.put(
                        new ImmutableUnorderedPairOfStrings(graphName, importFile.getName()), conGraph);
            }
            return new ConnectionGraph(graphName, conGraph);
        }
    }

}
