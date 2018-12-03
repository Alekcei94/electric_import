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

import com.sun.electric.tool.dcs.Accessory;
import com.sun.electric.tool.dcs.Data.LinksHolder;
import com.sun.electric.tool.dcs.Exceptions.InvalidInputException;
import com.sun.electric.tool.dcs.SpecificStructures.Pair;
import com.sun.electric.tool.dcs.SpecificStructures.ImmutableUnorderedPairOfStrings;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
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
        System.out.println(STRUCTURE.getListOfExternalVerteces().toString());
    }

    /**
     * Method to delete local external vertex if it was used in global graph
     *
     * @param key
     */
    @Override
    public void deleteKeyFromCBGraph(String key) {
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
     * avoid empty constructor.
     */
    private ConnectionGraph() {
        throw new AssertionError("Constructor shouldn't be used.");
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

        private boolean isChanged = true;
        // map keeps all internalLinksMatrix that shows distance from each vertex to all others.
        private Map<ImmutableUnorderedPairOfStrings, Integer> mapOfLinks;

        /**
         * Method to update mapOfLinks that is used in getWeight() of connection
         * in local graph. For each vertex method initiates deikstra and get
         * distances to other verteces.
         */
        private void updateLinksMatrix() {
            if (isChanged == false) {
                System.out.println("Trying to change unaltered structure");
                return;
            }
            mapOfLinks = new HashMap<>();
            List<String> externalVertList = STRUCTURE.getExternalVertexList();
            // TO DO: fix problem with double running vert1->vert2 and vert2->vert1
            for (String vert1 : externalVertList) {
                DEIKSTRA.deikstra(vert1);
                for (String vert2 : externalVertList) {
                    if (!vert1.equals(vert2)) {
                        //System.out.println("1: " + STRUCTURE.getVertMap().get(vert1).getPathCount());
                        //System.out.println("2: " + STRUCTURE.getVertMap().get(vert2).getPathCount());
                        mapOfLinks.put(new ImmutableUnorderedPairOfStrings(vert1, vert2),
                                DEIKSTRA.getDistanceTo(vert2));
                    }

                }
                DEIKSTRA.resetPathes();
            }
            this.setChanged(false);
        }

        /**
         * Method to get distance between verteces, using updateLinksMatrix if needed.
         * @param vertexFrom
         * @param vertexTo
         * @return 
         */
        private int getWeight(String vertexFrom, String vertexTo) {
            if (isChanged) {
                updateLinksMatrix();
            }
            return mapOfLinks.get(new ImmutableUnorderedPairOfStrings(vertexFrom, vertexTo));
        }

        private void setChanged(boolean set) {
            isChanged = set;
        }
    }

    /**
     * Class to implement deikstra method and all it's internal logic
     */
    private class Deikstra {

        // flag shows that some keys was deleted and graph wasn't updated after
        private boolean isChanged;

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
            if (isChanged()) {
                throw new AssertionError("Working with dirty file is not allowed."
                        + " Graph should be reset before another deikstra.");
            }

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
                        //System.out.println(vert.getName());
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
            if (doDelete) {
                setChanged(true);
            }

            Map<Pair<String, String>, String> edgeMap = STRUCTURE.getEdgeMap();
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
                        String key = edgeMap.get(new Pair<>(currentVertex.getName(), vert.getName()));
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
            } while (!currentVertex.getName().equals(vertexFrom.getName()));

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
            setChanged(false);
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
            List<String> vertexStringList = STRUCTURE.getAdjacencyMap().get(main.getName());
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

        /**
         * @return the isChanged
         */
        private boolean isChanged() {
            return isChanged;
        }

        /**
         * @param isChanged the isChanged to set
         */
        private void setChanged(boolean isChanged) {
            this.isChanged = isChanged;
            if (isChanged) {
                LINKS_MATRIX.setChanged(true);
            }
        }
    }

    /**
     * Class incapsulates the importing logic that shouldn't be used after
     * creation.
     */
    private class ConnectionGraphStructure {

        private final Map<String, Vertex> vertMap; //keeps all vertices
        private final Map<String, LinkedList<String>> adjacencyMap; // keeps main vertex as String and LinkedList with all connected verteces
        private final Map<Pair<String, String>, String> edgeMap; // keeps edges as values for vertice+vertice pair as key
        private final List<String> externalVertexList; // keeps names of external verteces

        /**
         * Constructor shouldn't allow more than one instance of class.
         */
        private ConnectionGraphStructure(File graphFile) {
            if (STRUCTURE != null) {
                throw new AssertionError("There should be only one connection graph structure");
            }
            this.vertMap = new HashMap<>();
            this.adjacencyMap = new HashMap<>();
            this.edgeMap = new HashMap<>();
            try {
                importGraph(graphFile);
            } catch (IOException ex) {
                Accessory.showMessage("Local graph file is corrupted.");
            }
            this.externalVertexList = getListOfExternalVerteces();
        }

        /**
         * Copy constructor.
         *
         * @param structure
         */
        private ConnectionGraphStructure(ConnectionGraphStructure structure) {
            if (STRUCTURE != null) {
                throw new AssertionError("There should be only one connection graph structure");
            }
            CloneGraphStructure clone = new CloneGraphStructure();
            this.vertMap = clone.createDeepCopyOfMap(structure.getVertMap());
            this.adjacencyMap = clone.createDeepCopyOfMap(structure.getAdjacencyMap());
            this.edgeMap = clone.createDeepCopyOfMap(structure.getEdgeMap());
            this.externalVertexList = new ArrayList(structure.getExternalVertexList());
        }

        /**
         * Method to delete vertex from all structure's collections, for now
         * there are 3 maps and 1 list.
         *
         * @param vertName
         */
        private void deleteVertexFromStructure(String vertName) {
            vertMap.remove(vertName);
            externalVertexList.remove(vertName); //remove if exists
            LinkedList<String> conVertList = adjacencyMap.get(vertName);
            // remove from edgeMap
            for (String conVert : conVertList) {
                edgeMap.remove(new Pair<>(vertName, conVert));
                edgeMap.remove(new Pair<>(conVert, vertName));
            }
            //remove from all other LinkedLists
            for (String conVert : conVertList) {
                adjacencyMap.get(conVert).remove(vertName);
            }
            LINKS_MATRIX.setChanged(true);

        }

        private void deleteVertexFromStructure(Vertex vert) {
            deleteVertexFromStructure(vert.getName());
        }

        /**
         * Method to get list with only external verteces from main map of
         * verteces.
         *
         * @return
         */
        private List<String> getListOfExternalVerteces() {
            List<String> extVertexList = new ArrayList<>();
            vertMap.entrySet().forEach((entry) -> {
                if (entry.getValue().isExternal()) {
                    extVertexList.add(entry.getKey());
                }
            });
            return extVertexList;
        }

        /**
         * Method to import graph object from text file.
         *
         * @param graphFile
         * @throws IOException
         */
        private void importGraph(File graphFile) throws IOException {
            try (BufferedReader br = new BufferedReader(new FileReader(graphFile))) {
                String line;
                while ((line = br.readLine()) != null) {
                    importOneLine(line);
                }
            }
        }

        /**
         * Satellite method that is used by importGraph(), Structure is fixed:
         * "address:Vert1 Vert2", Method adds vertices to the main Set and
         * insert edge value to map.
         *
         * @param line
         */
        private void importOneLine(String line) {
            String[] split = line.split(":");
            String[] vertices = split[1].split(" ");
            String adr = split[0];
            //should I verify input with regex?
            if (vertices.length != 2) {
                throw new InvalidInputException("Incorrect structure of graph file");
            }
            for (String vert : vertices) {
                addVertex(vert, false);
            }
            handleEdges(vertices[0], vertices[1], adr);
            handleAdjacency(vertices[0], vertices[1]);
        }

        /**
         * Add vertex to the Map of vertices.
         *
         * @throws IllegalArgumentException if vertex was in Set before.
         * @param name
         * @param throwIfExists
         */
        private void addVertex(String name, boolean throwIfExists) {
            if (getVertMap().get(name) != null) {
                if (throwIfExists) {
                    throw new IllegalArgumentException("Vertex with name" + name + "already exists");
                }
            } else {
                Vertex vert = new Vertex(name);
                getVertMap().put(name, vert);
            }
        }

        /**
         * Telescopic method.
         *
         * @param name
         */
        private void addVertex(String name) {
            addVertex(name, true);
        }

        /**
         * Method to tie edges with vertex pairs. Automatically added reverse
         * pair to list.
         *
         * @param vert1
         * @param vert2
         * @param adr
         */
        private void handleEdges(String vert1, String vert2, String adr) {
            Pair<String, String> pair = new Pair<>(vert1, vert2);
            getEdgeMap().put(pair, adr);
            // handle reverse situation
            Pair<String, String> pair2 = new Pair<>(vert2, vert1);
            getEdgeMap().put(pair2, adr);
        }

        /**
         * Method to fill adjacencyMap as vertex -> ajacencyList(Vertex). Should
         * be used twice for vert1+vert2 and inverse.
         *
         * @param vert1
         * @param vert2
         */
        private void handleAdjacency(String vert1, String vert2) {
            LinkedList<String> adjacencyList = getAdjacencyMap().get(vert1);
            if (adjacencyList == null) {
                adjacencyList = new LinkedList<>();
                getAdjacencyMap().put(vert1, adjacencyList);
            }
            // check vert2 existance in list?
            adjacencyList.add(getVertMap().get(vert2).getName());
            // handle reverse situation
            adjacencyList = getAdjacencyMap().get(vert2);
            if (adjacencyList == null) {
                adjacencyList = new LinkedList<>();
                getAdjacencyMap().put(vert2, adjacencyList);
            }
            // check vert1 existance in list?
            adjacencyList.add(getVertMap().get(vert1).getName());
        }

        /**
         * @return the vertMap
         */
        private Map<String, Vertex> getVertMap() {
            return vertMap;
        }

        /**
         * @return the adjacencyMap
         */
        private Map<String, LinkedList<String>> getAdjacencyMap() {
            return adjacencyMap;
        }

        /**
         * @return the edgeMap
         */
        private Map<Pair<String, String>, String> getEdgeMap() {
            return edgeMap;
        }

        /**
         * @return the externalVertexList
         */
        public List<String> getExternalVertexList() {
            return externalVertexList;
        }

        /**
         * Class to implement cloning logic to use it as copy constructor.
         */
        private class CloneGraphStructure {

            /**
             * Constructor executes cloning all complex objects of mother's
             * class.
             */
            private CloneGraphStructure() {
            }

            /**
             * Method to create deep copy of hashmap by copying every object
             * while iterating through map with copyObject method, Default
             * copyObject method works with Strings, Verteces, Pairs and several
             * types of Lists of these elements.
             *
             * @param mapToCopy
             * @return
             */
            private <A, B> HashMap<A, B> createDeepCopyOfMap(Map<A, B> mapToCopy) {
                HashMap<A, B> copiedMap = new HashMap<>();
                mapToCopy.entrySet().forEach((entry) -> {
                    A key = (A) entry.getKey();
                    A copiedKey = (A) copyObject(key);
                    B value = (B) entry.getValue();
                    B copiedValue = (B) copyObject(value);
                    copiedMap.put(copiedKey, copiedValue);
                });
                return copiedMap;
            }

            /**
             * Method to support deep copy of complex objects. Method should be
             * extended for new object types.
             *
             * @param object
             * @return
             */
            private <E> Object copyObject(Object object) {
                if (object instanceof String) {
                    return (String) object;
                } else if (object instanceof Pair) {
                    // Pair's objects can't be deep copied so it must be used only with strings or other immutable classes.
                    return new Pair(((Pair) object).getFirstObject(), ((Pair) object).getSecondObject());
                } else if (object instanceof Vertex) {
                    return new Vertex((Vertex) object);
                } else if (object instanceof LinkedList) {
                    List<E> list = (LinkedList<E>) object;
                    LinkedList<E> copiedList = new LinkedList<>();
                    for (E obj : list) {
                        copiedList.add((E) copyObject(obj));
                    }
                    return copiedList;
                } else if (object instanceof ArrayList) {
                    List<E> list = (ArrayList<E>) object;
                    ArrayList<E> copiedList = new ArrayList<>();
                    for (E obj : list) {
                        copiedList.add((E) copyObject(obj));
                    }
                    return copiedList;
                } else {
                    throw new InvalidInputException("Unexpected object type to copy.");
                }
            }
        }
    }

    /**
     *
     */
    public static class ConnectionFactory {

        private static final Map<Pair<String, String>, ConnectionGraph> graphMap = new HashMap<>();

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
            ConnectionGraph conGraph = graphMap.get(new Pair<>(graphName, importFile.getName()));
            if (conGraph == null) {
                conGraph = new ConnectionGraph(graphName, importFile);
                graphMap.put(new Pair<>(graphName, importFile.getName()), conGraph);
            }
            return new ConnectionGraph(graphName, conGraph);
        }
    }

}
