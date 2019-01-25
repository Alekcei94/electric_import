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
import com.sun.electric.tool.dcs.Data.Constants;
import com.sun.electric.tool.dcs.Data.LinksHolder;
import com.sun.electric.tool.dcs.Exceptions.HardFunctionalException;
import com.sun.electric.tool.dcs.SpecificStructures.ImmutableUnorderedPairOfStrings;
import com.sun.electric.tool.dcs.SpecificStructures.Pair;
import com.sun.electric.tool.dcs.autotracing.Interfaces.IConnectable;
import com.sun.electric.tool.dcs.autotracing.Interfaces.ICopyable;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.slf4j.LoggerFactory;

/**
 * Class to implement connection graph structure (ConnectionBox, mux4_1 etc).
 * Contract: reset shouldn't be initiated outside of object. Contract: method
 * should be checked as unready after all changes inside, ready after full
 * rebuilding linksMatrix and structure.
 */
public class ConnectionGraph implements IConnectable, ICopyable {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(ConnectionGraph.class);

    // Main structure of connection graph including import methods
    private final ConnectionGraphStructure STRUCTURE;
    // Object to manage deikstra algorithm
    private final Deikstra DEIKSTRA;
    // Object to manage all weights between external verteces.
    private final LinksMatrix LINKS_MATRIX;
    private final String graphName;
    
    private final boolean uniq;
    private final String addr;

    private boolean ready;
    private Future<Boolean> update;

    // BinaryHeaps are creating with the factory
    private static final BinaryHeap.BinaryHeapFactory HEAP_FAB = new BinaryHeap.BinaryHeapFactory();
    private static ExecutorService threadPool
            = Executors.newCachedThreadPool();

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
        getReady();
        STRUCTURE.deleteVertexFromStructure(key);
        setNotReady();
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
        getReady();
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
    public List<String> getConfigurationPath(String elemFrom, String elemTo,
            boolean doDelete) {
        getReady();

        List<String> returnList = DEIKSTRA.deikstra(elemFrom, elemTo, doDelete);

        if (doDelete) {
            setNotReady();
        }
        return returnList;
    }

    @Override
    public IConnectable copySelf() {
        getReady();
        return new ConnectionGraph(this);
    }

    /**
     * Equal names must show equal elements (use global address as part of the
     * name).
     *
     * @param con
     * @return
     */
    @Override
    public boolean equals(Object con) {
        if (con == null) {
            return false;
        } else if (!(con instanceof ConnectionGraph)) {
            return false;
        }
        return this.getName().equals(((ConnectionGraph) con).getName());
    }

    /**
     * Get hashCode for object.
     *
     * @return
     */
    @Override
    public int hashCode() {
        return this.getName().hashCode();
    }

    /**
     * Import default graph while constructing new object.
     */
    private ConnectionGraph(String name, File importFile) {
        this.STRUCTURE = new ConnectionGraphStructure(importFile);
        //this.graphName = graphName;
        this.graphName = getNameAndAddrFromGraphName(name).getFirstObject();
        this.addr = getNameAndAddrFromGraphName(name).getSecondObject();
        this.uniq = isUniqAddr(this.graphName);
        this.DEIKSTRA = new Deikstra(this.addr);
        this.LINKS_MATRIX = new LinksMatrix();
        setNotReady();
    }

    /**
     * Copy constructor for connection graph. Last state copied instead of
     * initial state.
     */
    private ConnectionGraph(ConnectionGraph conGraph) {
        this.STRUCTURE = new ConnectionGraphStructure(conGraph.getStructure());
        //this.LINKS_MATRIX = new LinksMatrix();
        //this.graphName = graphName;
        this.graphName = conGraph.graphName;
        this.addr = conGraph.addr;
        this.ready = conGraph.ready;
        this.uniq = conGraph.uniq;
        this.DEIKSTRA = new Deikstra(addr);
        LINKS_MATRIX = new LinksMatrix(conGraph.LINKS_MATRIX);
    }

    private Pair<String, String> getNameAndAddrFromGraphName(String graphName) {
        String[] split = graphName.split(Constants.getSplitter());
        if (split.length != 2) {
            throw new AssertionError("Incorrect graph structure, illegal name " + graphName);
        }
        return new Pair(split[0], split[1]);
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
        return graphName + "." + addr;
    }

    private void getReady() {
        if (this.ready == true) {
            return;
        }
        try {
            if (update.get(10, TimeUnit.SECONDS) == false) {
                throw new HardFunctionalException("Future is not getting result");
            }
        } catch (InterruptedException | ExecutionException | TimeoutException ex) {
            logger.error(ex.getMessage());
            throw new AssertionError(ex.getMessage());
        }
        this.ready = true;

    }

    private void setNotReady() {
        this.ready = false;
        //Accessory.timeStart();
        update = threadPool.submit(() -> {
            LINKS_MATRIX.updateLinksMatrix();
            return true;
        });
    }
    
    /**
     * Method to check if this graph has uniq address.
     * @param graphName 
     */
    private boolean isUniqAddr(String graphName) {
        for(String block : Constants.getUniqConnectionElementNames()) {
            if(graphName.equals(block)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Links matrix class describes all-to-all pathes in graph. IMPORTANT:
     * should be updated with every change to structure.
     */
    private class LinksMatrix {

        // map keeps all internalLinksMatrix that shows distance from each vertex to all others.
        // CONTRACT : don't change this map, if you want to change, create another one.
        private Map<ImmutableUnorderedPairOfStrings, Integer> mapOfLinks;

        /**
         * Main constructor, default coz actions are not required.
         */
        private LinksMatrix() {

        }

        /**
         * Copy constructor for links matrix. Shallow copy...
         *
         * @param links
         */
        private LinksMatrix(LinksMatrix links) {
            // it's ok coz immutable keys and values AND contract for map.
            mapOfLinks = links.getMatrix();
        }

        /**
         * Method to update mapOfLinks that is used in getWeight() of connection
         * in local graph. For each vertex method initiates deikstra and get
         * distances to other verteces.
         */
        private void updateLinksMatrix() throws HardFunctionalException {
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
         * Method to get distance between verteces, using updateLinksMatrix if
         * needed.
         *
         * @param vertexFrom
         * @param vertexTo
         * @return
         */
        private int getWeight(String vertexFrom, String vertexTo) {
            Integer i = mapOfLinks.get(new ImmutableUnorderedPairOfStrings(vertexFrom, vertexTo));
            if(i == null) {
                showStructure();
                ImmutableUnorderedPairOfStrings iupos = new ImmutableUnorderedPairOfStrings(vertexFrom, vertexTo);
                throw new HardFunctionalException("Null in graph " + graphName 
                        +  " as: " + iupos);
            }
            return i;
        }

        private Map<ImmutableUnorderedPairOfStrings, Integer> getMatrix() {
            return mapOfLinks;
        }
    }

    /**
     * Class to implement deikstra method and all it's internal logic
     */
    private class Deikstra {

        private final String addr; // address of current graph to get it to path

        private Deikstra(String addr) {
            this.addr = addr;
        }

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
         * Main deikstra function, count minimum distance between VertexFrom and
         * VertexTo. IMPORTANT: don't forget to reset graph after you finish
         * your operations.
         *
         * @param vertexFrom
         */
        private List<String> deikstra(String vertexFrom, String vertexTo, boolean doDelete) throws HardFunctionalException {
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
        private void deikstra(String vertexFrom) throws HardFunctionalException {
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
         * @param vertexTo
         */
        private List<String> deikstraBackway(Vertex vertexFrom, Vertex vertexTo, boolean doDelete) throws HardFunctionalException {
            Map<ImmutableUnorderedPairOfStrings, String> edgeMap = STRUCTURE.getEdgeMap();
            Vertex currentVertex = vertexTo;

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
                        configPath.add(this.addr + key); // add each key that we passed to configuration
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
        private List<Vertex> getCloseVerteces(Vertex main) throws HardFunctionalException {
            if (main == null) {
                throw new HardFunctionalException("Null vertex input");
            }
            List<String> vertexStringList = STRUCTURE.getAdjacencyMap().get(main.getContext());
            Map<String, Vertex> vertMap = STRUCTURE.getVertMap();
            List<Vertex> vertexAjacencyList = new ArrayList<>();
            for (String vertexString : vertexStringList) {
                vertexAjacencyList.add(vertMap.get(vertexString));
            }
            return vertexAjacencyList;
        }

        private List<Vertex> getCloseVerteces(String mainName) throws HardFunctionalException {
            return getCloseVerteces(STRUCTURE.getVertMap().get(mainName));
        }
    }

    /**
     * Factory to get some types of connection graphs.
     */
    public static class ConnectionFactory {

        private static final Map<ImmutableUnorderedPairOfStrings, ConnectionGraph> graphMap = new HashMap<>();

        /**
         * Method to create only CB graph.
         *
         * @param graphName
         * @param graphType
         * @return
         * @throws com.sun.electric.tool.dcs.Exceptions.HardFunctionalException
         */
        public static IConnectable createConnectionGraph(String graphName, String graphType) throws HardFunctionalException {
            File importFile = new File(LinksHolder.getPathTo(graphType));
            if (importFile == null) {
                throw new HardFunctionalException("Connection graph file is not found.");
            }
            return createConnectionGraphFromFile(graphName, importFile);
        }

        /**
         * Factory method to create graph using name and "import graph" file.
         *
         * @param graphName
         * @param importFile
         * @return
         */
        public static IConnectable createConnectionGraphFromFile(String graphName, File importFile) {
            //Accessory.writeToLog(String.valueOf(Accessory.timeStart()));
            ConnectionGraph conGraph = graphMap.get(
                    new ImmutableUnorderedPairOfStrings(graphName, importFile.getName()));
            if (conGraph == null) {
                conGraph = new ConnectionGraph(graphName, importFile);
                graphMap.put(
                        new ImmutableUnorderedPairOfStrings(graphName, importFile.getName()), conGraph);
            }
            conGraph.getReady();
            return new ConnectionGraph(conGraph);
        }
    }

}
