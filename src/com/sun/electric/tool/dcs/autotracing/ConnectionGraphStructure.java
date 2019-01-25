/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sun.electric.tool.dcs.autotracing;

import com.sun.electric.tool.dcs.Exceptions.InvalidInputException;
import com.sun.electric.tool.dcs.SpecificStructures.ImmutableUnorderedPairOfStrings;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Class incapsulates the importing logic that shouldn't be used after creation.
 */
public final class ConnectionGraphStructure extends AbstractGraphStructure {

    private final Map<String, Vertex> vertMap; //keeps all vertices
    private final Map<String, LinkedList<String>> adjacencyMap; // keeps main vertex as String and LinkedList with all connected verteces
    private final Map<ImmutableUnorderedPairOfStrings, String> edgeMap; // keeps edges as values for vertice+vertice pair as key
    private final List<String> externalVertexList; // keeps names of external verteces

    /**
     * Constructor shouldn't allow more than one instance of class.
     *
     * @param graphFile is the file with graph structure
     */
    public ConnectionGraphStructure(File graphFile) {
        this.vertMap = new HashMap<>();
        this.adjacencyMap = new HashMap<>();
        this.edgeMap = new HashMap<>();
        importGraph(graphFile);
        this.externalVertexList = getListOfExternalVerteces();
    }

    /**
     * Copy constructor from structure of another graph.
     *
     * @param structure
     */
    public ConnectionGraphStructure(ConnectionGraphStructure structure) {
        CloneGraphStructure clone = new CloneGraphStructure();
        // be careful for clone because it must not make useless copies of graphs (graph should be copied only once).
        this.vertMap = clone.createDeepCopyOfMap(structure.getVertMap());
        this.adjacencyMap = clone.createDeepCopyOfMap(structure.getAdjacencyMap());
        this.edgeMap = clone.createDeepCopyOfMap(structure.getEdgeMap());
        this.externalVertexList = new ArrayList(structure.getExternalVertexList());
    }

    /**
     * Method to delete vertex from all structure's collections, for now there
     * are 3 maps and 1 list.
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
        //delete initial context
        adjacencyMap.remove(vertName);
    }

    /**
     * Method to delete vertex from all structure's collections, for now there
     * are 3 maps and 1 list.
     *
     * @param vert
     */
    public void deleteVertexFromStructure(Vertex vert) {
        deleteVertexFromStructure(vert.getContext());
    }

    /**
     * Method to get list with only external verteces from main map of verteces.
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
        // not sure if it's better than existing code
        /*vertMap.entrySet().stream().filter((entry) -> entry.getValue().isExternal())
                .map((entry) -> entry.getKey())
                .collect(Collectors.toList());*/
        return extVertexList;
    }

    /**
     * Satellite method that is used by importGraph(), Structure is fixed:
     * "address:Vert1 Vert2", Method adds vertices to the main Set and insert
     * edge value to map.
     *
     * @param line
     */
    @Override
    protected void importOneLine(String line) {
        String[] split = line.split(":");
        String[] vertices = split[1].split(" ");
        String adr = split[0];
        //should I verify input with regex?
        if (vertices.length != 2) {
            throw new InvalidInputException("Incorrect structure of graph file, number of verteces isn't equal 2");
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
                throw new IllegalArgumentException("Vertex with name " + name + " already exists");
            }
        } else {
            Vertex vert = new Vertex(name);
            getVertMap().put(name, vert);
        }
    }

    /**
     * Method to tie edges with vertex pairs. pair is immutable so reverse situation is ok.
     *
     * @param vert1
     * @param vert2
     * @param adr
     */
    private void handleEdges(String vert1, String vert2, String adr) {
        ImmutableUnorderedPairOfStrings pair = new ImmutableUnorderedPairOfStrings(vert1, vert2);
        getEdgeMap().put(pair, adr);
    }

    /**
     * Method to fill adjacencyMap as vertex -> ajacencyList(Vertex). Should be
     * used twice for vert1+vert2 and inverse.
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
        adjacencyList.add(getVertMap().get(vert2).getContext());
        // handle reverse situation
        adjacencyList = getAdjacencyMap().get(vert2);
        if (adjacencyList == null) {
            adjacencyList = new LinkedList<>();
            getAdjacencyMap().put(vert2, adjacencyList);
        }
        // check vert1 existance in list?
        adjacencyList.add(getVertMap().get(vert1).getContext());
    }

    /**
     * @return the vertMap
     */
    public Map<String, Vertex> getVertMap() {
        return vertMap;
    }

    /**
     * @return the adjacencyMap
     */
    public Map<String, LinkedList<String>> getAdjacencyMap() {
        return adjacencyMap;
    }

    /**
     * @return the edgeMap
     */
    public Map<ImmutableUnorderedPairOfStrings, String> getEdgeMap() {
        return edgeMap;
    }

    /**
     * @return the externalVertexList
     */
    public List<String> getExternalVertexList() {
        return externalVertexList;
    }

}
