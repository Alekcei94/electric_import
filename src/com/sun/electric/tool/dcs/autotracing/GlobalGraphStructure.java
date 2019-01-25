/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sun.electric.tool.dcs.autotracing;

import com.sun.electric.tool.dcs.Exceptions.InvalidStructureError;
import com.sun.electric.tool.dcs.SpecificStructures.ImmutableUnorderedPairOfStrings;
import com.sun.electric.tool.dcs.autotracing.Chain.ChainElement;
import com.sun.electric.tool.dcs.autotracing.Interfaces.IConnectable;
import java.io.File;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Class incapsulates the importing logic that shouldn't be used after creation.
 */
public final class GlobalGraphStructure extends AbstractGraphStructure {

    private final Map<String, Chain> vertMap; //keeps all vertices
    private final Map<String, LinkedList<String>> adjacencyMap; // keeps main vertex as String and LinkedList with all connected verteces
    private final Map<ImmutableUnorderedPairOfStrings, LinkedList<String>> edgeMap; // keeps edges as values for vertice+vertice pair as key

    private final HashMap<String, IConnectable> connectionMap;

    /**
     * Constructor shouldn't allow more than one instance of class.
     *
     * @param graphFile is the file with graph structure
     */
    public GlobalGraphStructure(File graphFile) {
        this.vertMap = new HashMap<>();
        this.adjacencyMap = new HashMap<>();
        this.edgeMap = new HashMap<>();
        this.connectionMap = new HashMap<>();
        importGraph(graphFile);
    }

    /**
     * Copy constructor.
     *
     * @param structure
     */
    public GlobalGraphStructure(GlobalGraphStructure structure) {
        CloneGraphStructure clone = new CloneGraphStructure();
        this.vertMap = clone.createDeepCopyOfMap(structure.getVertMap());
        this.adjacencyMap = clone.createDeepCopyOfMap(structure.getAdjacencyMap());
        this.edgeMap = clone.createDeepCopyOfMap(structure.getEdgeMap());
        this.connectionMap = clone.createDeepCopyOfMap(structure.getConnectionMap());
    }

    /**
     * Method to delete vertex from all structure's collections, for now there
     * are 3 maps and 1 list.
     *
     * @param vertContext
     */
    public void deleteVertexFromStructure(String vertContext) {
        vertMap.remove(vertContext);
        LinkedList<String> conVertList = adjacencyMap.get(vertContext);
        // remove from edgeMap
        for (String conVert : conVertList) {
            edgeMap.remove(new ImmutableUnorderedPairOfStrings(vertContext, conVert));
        }
        //remove from all other LinkedLists
        for (String conVert : conVertList) {
            adjacencyMap.get(conVert).remove(vertContext);
        }
        //delete initial context
        adjacencyMap.remove(vertContext);
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
     * Satellite method that is used by importGraph(), Structure is fixed:
     * "address:Vert1 Vert2", Method adds vertices to the main Set and insert
     * edge value to map.
     *
     * @param line
     */
    @Override
    protected void importOneLine(String line) {
        Chain chain = addVertex(line, true);
        handleAdjacency(chain);
    }

    /**
     * Add vertex to the Map of vertices.
     *
     * @throws IllegalArgumentException if vertex was in Set before.
     * @param name
     * @param throwIfExists
     */
    private Chain addVertex(String vertexLine, boolean throwIfExists) {
        Chain chain = new Chain(vertexLine);
        String simplifiedName = chain.getSimplifiedName();

        if (throwIfExists && vertMap.get(simplifiedName) != null) {
            throw new InvalidStructureError("Illegal attempt to add one chain twice: " + simplifiedName);
        }

        vertMap.put(simplifiedName, chain);
        return chain;
    }

    /**
     * Method to tie edges with vertex pairs. Automatically added reverse pair
     * to list.
     *
     * @param vert1
     * @param vert2
     * @param adr
     */
    private void handleEdges(Chain chain) {
        // Method is not used because it's faster to get chain/chain/intersectionList from handleAdjacency method
        // should be checked coz I'm not sure if it is working
        List<ChainElement> connectionsList = chain.getConnectionChainElementsList();
        for (Chain otherChain : vertMap.values()) {
            List<ChainElement> otherConnectionList = otherChain.getConnectionChainElementsList();
            List<ChainElement> intersectionsList = connectionsList.stream()
                    .filter(otherConnectionList::contains)
                    .collect(Collectors.toList());
            handleEdge(chain, otherChain, intersectionsList);
        }
    }

    /**
     * Method to fill adjacencyMap as vertex -> ajacencyList(Vertex).
     *
     * @param vert1
     * @param vert2
     */
    private void handleAdjacency(Chain chain) {
        List<ChainElement> connectionsList = chain.getConnectionChainElementsList();
        //TODO: avoid doublerunning chains and consequently double edges
        for (Chain otherChain : vertMap.values()) {
            if (chain == otherChain) {
                continue;
            }
            List<ChainElement> otherConnectionList = otherChain.getConnectionChainElementsList();
            //this function is adjacency+edges, other way it should be findAny instead of collect.
            List<ChainElement> intersectionsList = connectionsList.stream()
                    .filter((elem) -> {
                        return otherConnectionList.stream().anyMatch(elem::isConnectedTo);
                    })
                    .collect(Collectors.toList());
            if (!intersectionsList.isEmpty()) {
                addAdjacencyForChains(chain, otherChain);
                addAdjacencyForChains(otherChain, chain);
            }
            // handle edges is excess operation but it's faster to handle it here.
            handleEdge(chain, otherChain, intersectionsList);
            //
        }
    }

    /**
     * Method to get edges for graph structure. Use it only once coz of
     * immutable pair.
     *
     * @param first
     * @param second
     * @param intersectionsList
     */
    private void handleEdge(Chain first, Chain second, List<ChainElement> intersectionsList) {
        ImmutableUnorderedPairOfStrings pair
                = new ImmutableUnorderedPairOfStrings(first.getSimplifiedName(), second.getSimplifiedName());
        for (ChainElement elem : intersectionsList) {
            IConnectable localGraph = connectionMap.get(elem.getNameAndAddr());
            if (localGraph == null) {
                localGraph = ConnectionGraph.ConnectionFactory
                        .createConnectionGraph(elem.getNameAndAddr(), elem.getName());
                connectionMap.put(elem.getNameAndAddr(), localGraph);

            }
            LinkedList<String> edgesList = edgeMap.get(pair);
            if (edgesList == null) {
                edgesList = new LinkedList<>();
                edgeMap.put(pair, edgesList);
            }
            edgesList.add(localGraph.getName());
        }
    }

    /**
     * Method to add info about adjancy into adjacencyMap. Use it twice to get
     * adjacenct for both chains.
     *
     * @param chain
     * @param otherChain
     */
    private void addAdjacencyForChains(Chain chain, Chain otherChain) {
        LinkedList<String> adjList = adjacencyMap.get(chain.getSimplifiedName());
        if (adjList == null) {
            adjList = new LinkedList<>();
            adjacencyMap.put(chain.getSimplifiedName(), adjList);
        }
        adjList.add(otherChain.getSimplifiedName());
    }

    /**
     * @return the vertMap
     */
    public Map<String, Chain> getVertMap() {
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
    public Map<ImmutableUnorderedPairOfStrings, LinkedList<String>> getEdgeMap() {
        return edgeMap;
    }

    /**
     * @return the connectionSet
     */
    public HashMap<String, IConnectable> getConnectionMap() {
        return connectionMap;
    }

}
