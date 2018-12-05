/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sun.electric.tool.dcs.autotracing;

import com.sun.electric.tool.dcs.Accessory;
import com.sun.electric.tool.dcs.Exceptions.InvalidInputException;
import com.sun.electric.tool.dcs.SpecificStructures.ImmutableUnorderedPairOfStrings;
import com.sun.electric.tool.dcs.SpecificStructures.Pair;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Class incapsulates the importing logic that shouldn't be used after
 * creation.
 */
public class GraphStructure {

    private final Map<String, Vertex> vertMap; //keeps all vertices
    private final Map<String, LinkedList<String>> adjacencyMap; // keeps main vertex as String and LinkedList with all connected verteces
    private final Map<ImmutableUnorderedPairOfStrings, String> edgeMap; // keeps edges as values for vertice+vertice pair as key
    private final List<String> externalVertexList; // keeps names of external verteces
    private final ITraceable graph;

    /**
     * Constructor shouldn't allow more than one instance of class.
     */
    public GraphStructure(File graphFile, ITraceable graph) {
        this.graph = graph;
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
    public GraphStructure(GraphStructure structure, ITraceable graph) {
        this.graph = graph;
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
    public void deleteVertexFromStructure(String vertName) {
        vertMap.remove(vertName);
        externalVertexList.remove(vertName); //remove if exists
        LinkedList<String> conVertList = adjacencyMap.get(vertName);
        // remove from edgeMap
        for (String conVert : conVertList) {
            edgeMap.remove(new ImmutableUnorderedPairOfStrings(vertName, conVert));
        }
        //remove from all other LinkedLists
        for (String conVert : conVertList) {
            adjacencyMap.get(conVert).remove(vertName);
        }
    }
    
    /**
     * Method to delete vertex from all structure's collections, for now
     * there are 3 maps and 1 list.
     *
     * @param vert
     */
    public void deleteVertexFromStructure(Vertex vert) {
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
        try (final BufferedReader br = new BufferedReader(new FileReader(graphFile))) {
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
    protected void importOneLine(String line) {
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
     * Telescoping method.
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
        ImmutableUnorderedPairOfStrings pair = new ImmutableUnorderedPairOfStrings(vert1, vert2);
        getEdgeMap().put(pair, adr);
        // handle reverse situation
        ImmutableUnorderedPairOfStrings pair2 = new ImmutableUnorderedPairOfStrings(vert2, vert1);
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
    Map<String, Vertex> getVertMap() {
        return vertMap;
    }

    /**
     * @return the adjacencyMap
     */
    Map<String, LinkedList<String>> getAdjacencyMap() {
        return adjacencyMap;
    }

    /**
     * @return the edgeMap
     */
    Map<ImmutableUnorderedPairOfStrings, String> getEdgeMap() {
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
