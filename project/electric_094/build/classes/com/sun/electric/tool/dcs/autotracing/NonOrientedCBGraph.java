/* Electric(tm) VLSI Design System
 *
 * File: NonOrientedCBGraph.java
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
import com.sun.electric.tool.dcs.ConstantsAndPrefs;
import com.sun.electric.tool.dcs.FunctionalException;
import com.sun.electric.tool.dcs.Pair;
import java.util.ArrayList;
import java.util.List;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;

/**
 * This class is used to describe local CB graph, this global graph is the
 * complex of verteces and links between them.
 */
public final class NonOrientedCBGraph implements ConnectionGraphInterface {

    private static final BinaryHeap.BinaryHeapFactory HEAP_FAB = new BinaryHeap.BinaryHeapFactory();

    private List<Pair<String, String>> usedExternalPinsInGraph = new ArrayList<>();

    private final String graphName;
    private Vertex[] vertexArray; // Array of Vertices
    private int vertexCount;

    private final int VERTEX_MAX = 124;

    private final int GLOBAL_VERTS = 52;
    private final String[] globVerts = {"X0", "X1", "X2", "X3", "X4", "X5", "X6", "X7", "X8", "X9", "X10", "X11", "X12", "X13",
        "Y0", "Y1", "Y2", "Y3", "Y4", "Y5", "Y6", "Y7", "Y8", "Y9", "Y10", "Y11", "Y12", "Y13",
        "Z0", "Z1", "Z2", "Z3", "Z4", "Z5", "Z6", "Z7", "Z8", "Z9", "Z10", "Z11", "Z12", "Z13",
        "K0", "K1", "K2", "K3", "K4", "K5", "K6", "K7", "K8", "K9", "K10", "K11", "K12", "K13"};

    private int[][] matrix; // Adjacency matrix
    private String[][] keyMatrix; // matrix for key values (key number in CB.trc)
    private int linksMatrix[][];

    private List<Integer> VertToDeleteList = new ArrayList<>();

    /**
     * Constructor: Parent, graphName required to be type: "CB_216", add adj
     * matrix, initialise Vertex Array, constructor: Child, initialise internal
     * matrix with size of GLOBAL_VERTS. @param graphName @param graphName.
     *
     * @param graphName the name of graph
     * @param creator the creator of graph
     */
    private NonOrientedCBGraph(String graphName) {
        this.graphName = graphName;
        Init();
        importGraphFromFile();
        linksMatrix = new int[GLOBAL_VERTS][GLOBAL_VERTS];
    }

    /**
     * Initialise matrix and vertexArray, should be overriden for another
     * matrixes.
     */
    private void Init() {
        matrix = new int[VERTEX_MAX][VERTEX_MAX];
        keyMatrix = new String[VERTEX_MAX][VERTEX_MAX];
        vertexArray = new Vertex[VERTEX_MAX];
        for (int i = 0; i < VERTEX_MAX; i++) {
            for (int j = 0; j < VERTEX_MAX; j++) {
                keyMatrix[i][j] = "";
            }
        }
    }

    /**
     * Method to get the name of this CB graph.
     *
     * @return the name of local graph.
     */
    @Override
    public String getLabel() {
        return graphName;
    }

    /**
     * Method is deleting verteces from local CB graph according to keys.
     *
     * @param key is the string that should be deleted.
     * @return external pins which should be marked as used coz there is USED
     * DIRECT connection to them in CB graph.
     */
    @Override
    public List<Pair<String, String>> deleteKeyFromCBGraph(String key) {
        int keyNum = findVertex(key);
        if (keyNum != -1) {
            deleteVertex(keyNum);
        }
        refreshLinksMatrix();
        return getAdditionalUsedExternalPins();
    }

    /**
     * Only developer method, method to print internal links matrix.
     */
    public void getLinksMatrix() {
        String line = "";
        for (int i = 0; i < GLOBAL_VERTS; i++) {
            for (int j = 0; j < GLOBAL_VERTS; j++) {
                line += String.valueOf(linksMatrix[i][j]);
                line += " ";
            }
            System.out.println("linex " + line);
            line = "";
        }
    }

    /**
     * Method to get the length of path using links matrix,
     *
     * @param elemFrom external pin
     * @param elemTo external pin
     * @return
     * @Params elemFrom, elemTo mean .
     */
    @Override
    public int getWeight(String elemFrom, String elemTo) {
        return linksMatrix[findIntForLinksMatrix(elemFrom)][findIntForLinksMatrix(elemTo)];
    }

    /**
     * Method to find path and write real keys in CB,
     *
     * @param elemFrom
     * @param elemTo
     * @Params elemFrom, elemTo mean.
     */
    @Override
    public void getConfigurationPath(String elemFrom, String elemTo) {
        deikstra(findVertex(elemFrom));
        deikstra_backway_with_config(findVertex(elemTo), findVertex(elemFrom), true);
        resetVertices();
    }

    /**
     * Method to delete vertices that was used in previous autotracing steps.
     *
     * @return external pins which should be marked as used coz there is USED
     * DIRECT connection to them in CB graph.
     */
    public List<Pair<String, String>> doDeleteUsedVerts() {
        Iterator<Integer> deleteItr = VertToDeleteList.iterator();
        while (deleteItr.hasNext()) {
            deleteVertex(deleteItr.next());
            deleteItr.remove();
        }
        refreshLinksMatrix();
        return getAdditionalUsedExternalPins();
    }

    /**
     * Method using deikstra algorith to find weights between global CB vertices
     * and refresh links matrix.
     */
    public void refreshLinksMatrix() {
        for (int i = 0; i < GLOBAL_VERTS; i++) {
            for (int j = 0; j < GLOBAL_VERTS; j++) {
                this.linksMatrix[i][j] = 0;
            }
        }
        for (String vert : globVerts) {
            int vertNum = findVertex(vert);
            if (vertNum != -1) {
                deikstraFindAll(vertNum);
            }
        }
    }

    /**
     * Reset for using in another Global graph.
     */
    public void reset() {
        VertToDeleteList = new ArrayList<>();
    }

    /**
     * Method to form new vertex in graph, TODO: remove check-for-same-label
     * block.
     *
     * @param vertexInfo
     * @param label
     * @return
     */
    private boolean addVertex(String label) {
        //check for same label
        for (int j = 0; j < vertexCount; j++) {
            if (vertexArray[j] != null) {
                if (vertexArray[j].getLabel().equals(label)) {
                    return false;
                }
            }
        }
        vertexArray[vertexCount++] = new Vertex(label);
        return true;
    }

    /**
     * Method to delete vertex from graph, ALWAYS USE
     * GetAdditionalUsedExternalPins AFTER AND DELETE RELATED CHAINS.
     *
     * @param count
     * @Param count is the number of vertex which should be deleted.
     */
    private void deleteVertex(int count) {
        if (count == -1) {
            return;
        }
        if (vertexArray[count] != null) {
            for (int i = 0; i < vertexCount; i++) {
                matrix[i][count] = 0;
                matrix[count][i] = 0;
            }
            String label = vertexArray[count].getLabel();
            vertexArray[count] = null;
            Accessory.printLog(graphName);
            Accessory.printLog("label " + label);

            Pair<String, String> pairToDelete = new Pair<>(graphName, label);
            usedExternalPinsInGraph.add(pairToDelete);
            //CREATOR.deleteChainCozUsedVertex(graphName, label); // DELETED COZ NEEDED INDEPENDENT MODULE
        }
    }

    public List<Pair<String, String>> getAdditionalUsedExternalPins() {
        List<Pair<String, String>> newArrayList = new ArrayList<>();
        for (Pair<String, String> pair : usedExternalPinsInGraph) {
            newArrayList.add(pair);
        }
        usedExternalPinsInGraph = new ArrayList<>();
        return newArrayList;
    }

    /**
     * Method to delete vertex from graph,
     *
     * @param count
     * @Param count is the number of vertex which should be deleted.
     */
    private void deleteVertexNotForAuto(int count) {
        if (count == -1) {
            return;
        }
        if (vertexArray[count] != null) {
            for (int i = 0; i < vertexCount; i++) {
                matrix[i][count] = 0;
                matrix[count][i] = 0;
            }
            vertexArray[count] = null;
        }
    }

    /**
     * Method to reset all pathcounts of vertices in graph.
     */
    private void resetVertices() {
        for (int i = 0; i < vertexCount; i++) {
            if (vertexArray[i] != null) {
                vertexArray[i].resetPathCount();
            }
        }
    }

    /**
     * Method returns the array of integers for position of adj vertices ,
     * Returns null when nothing found,
     *
     * @param v
     * @return
     * @Param v is the current vertex in graph.
     */
    private Integer[] getCloseVerteces(int v) {
        List<Integer> Verts = new ArrayList<>();
        for (int j = 0; j < vertexCount; j++) {
            if (vertexArray[j] != null) {
                if ((matrix[v][j] > 0) && (vertexArray[j].getVisited() == false)) {
                    Verts.add(j);
                }
            }
        }
        if (!Verts.isEmpty()) {
            Integer a[] = new Integer[Verts.size()];
            a = Verts.toArray(a);
            return a;
        } else {
            Integer a[] = new Integer[0];
            return a; //no more verteces here
        }
    }

    /**
     * Method is deleting verteces from local CB graph according to keys,
     * additive method to UseSchemeConfiguration.
     */
    /*private void deleteKeyFromCBGraph(Integer key) throws IOException {
        try (BufferedReader autotraReader = new BufferedReader(new FileReader(new File(ConstantsAndPrefs.getPathTo("connection box"))))) {
            String line;
            while ((line = autotraReader.readLine()) != null) {
                String[] p = line.split(" : ");
                if (Objects.equals(Integer.valueOf(p[1]), key)) {
                    String[] s = p[0].split(" -- ");
                    deleteVertex(findVertex(s[0]));
                    deleteVertex(findVertex(s[1]));
                }
            }
        }
    }*/
    /**
     * Method is used to cover full graph from 1 point and to count lengths of
     * the ways,
     *
     * @Param startPoint shows the number of vertice in main matrix, method used
     * in cycle to cover all vertices.
     */
    private void deikstraFindAll(int startPoint) {
        deikstra(startPoint);
        renewOneLineForLinksMatrix(startPoint);
        resetVertices();
    }

    /**
     * Method is used to cover full graph from 1 point and to count the length
     * of the ways, one of the local deikstraFindAll methods.
     */
    private void deikstra(int startPoint) {
        GraphHeapInterface heap = HEAP_FAB.createBinaryHeap();
        int curPathCount;
        Integer closestVertex;
        int currentVertex = startPoint;

        vertexArray[currentVertex].setVisited(true);
        vertexArray[currentVertex].setPathCount(0);

        heap.add(vertexArray[currentVertex].getPathCount(), currentVertex);

        int counter = 0;
        while ((closestVertex = heap.getValueOfMinKeyElement()) != -1) {
            counter++;
            assert counter < 1000;
            vertexArray[closestVertex].setVisited(true);
            Integer[] a = getCloseVerteces(closestVertex);
            if (a == null) {
                continue;
            }

            for (Integer a1 : a) {
                currentVertex = a1;
                if (((vertexArray[currentVertex].getPathCount()) > (curPathCount = (vertexArray[closestVertex].getPathCount()
                        + matrix[currentVertex][closestVertex]))) && (matrix[currentVertex][closestVertex] != 0)) {
                    vertexArray[currentVertex].setPathCount(curPathCount);
                }
                heap.add(vertexArray[currentVertex].getPathCount(), currentVertex);
            }
        }
        // reset all paths
        for (int j = 0; j < vertexCount; j++) {
            if (vertexArray[j] != null) {
                vertexArray[j].setVisited(false);
            }
        }
    }

    /**
     * This method use deikstra results to find all verteces used by this way,
     * this method can delete vertices to dynamic modification of graph,
     *
     * @Param doDelete is true to delete all vertices on the way,
     * @Params startPoint and endPoint show the edges of needed way.
     */
    private void deikstra_backway_with_config(int endPoint, int startPoint, boolean doDelete) {
        int currentVertex = endPoint;
        VertToDeleteList.add(currentVertex);
        Integer[] a;
        int counter = 0;
        do {
            a = getCloseVerteces(currentVertex);
            for (Integer a1 : a) {
                if (((vertexArray[currentVertex].getPathCount() - vertexArray[a1].getPathCount()) == matrix[currentVertex][a1]) && (matrix[currentVertex][a1] != 0)) {
                    int labelNumber = Integer.parseInt(getLabel().split("<")[1]);
                    labelNumber += Integer.parseInt(keyMatrix[currentVertex][a1]);
                    Accessory.write(ConstantsAndPrefs.getPathTo("config"), String.valueOf(labelNumber));
                    currentVertex = a1;
                    VertToDeleteList.add(currentVertex);
                    break;
                }
            }
            counter++;
            assert counter < 1000;
        } while (currentVertex != startPoint);
    }

    /**
     * imports CB graph file.
     */
    private void importGraphFromFile() {
        File fileForImport = new File(ConstantsAndPrefs.getPathTo("connection box"));
        try {
            importGraphFromFile(fileForImport);
        } catch (IOException | FunctionalException ioe) {
            ioe.printStackTrace(System.out);
        }
    }

    /**
     * Method renewing links matrix, method implements the additive part to
     * deikstra method function which is used specifically to ready CB block
     * (X11-X16, X21-X26, Y11-Y16, Y21-Y26),
     *
     * @Param startPoint shows the number of vertice in main matrix.
     */
    private void renewOneLineForLinksMatrix(int startPoint) {
        int internalInt = findIntForLinksMatrix(vertexArray[startPoint].getLabel());
        int InternalSecondInt;
        for (int i = 0; i < vertexCount; i++) {
            if ((vertexArray[i] != null) && (i != startPoint)) {
                for (String vert : globVerts) {
                    if (vertexArray[i].getLabel().equals(vert)) {
                        // linksMatrix element is 0 if there is no path.
                        if (vertexArray[i].getPathCount() == vertexArray[i].getMaxPathCount()) {
                            continue;
                        }

                        InternalSecondInt = findIntForLinksMatrix(vertexArray[i].getLabel());
                        linksMatrix[internalInt][InternalSecondInt] = vertexArray[i].getPathCount();
                        linksMatrix[InternalSecondInt][internalInt] = vertexArray[i].getPathCount();
                    }
                }
            }
        }
    }

    /**
     * Method finds the number of vertice in internal links matrix,
     *
     * @Param find this is the label of vertex in arrays.
     */
    private int findIntForLinksMatrix(String findThis) {
        for (int j = 0; j < globVerts.length; j++) {
            String vert = globVerts[j];
            if (findThis.equals(vert)) {
                return j;
            }
        }
        Accessory.printLog("Something went wrong, variable is " + findThis);
        return -1;
    }

    // Next will be internal methods to create new objects
    /**
     * Method to add edge to adj matrix.
     */
    private void addPoint(int begin, int end, int weight) {
        matrix[begin][end] = weight;
        matrix[end][begin] = weight;
    }

    /**
     * Method to add edge to key values matrix.
     */
    private void addKeyPoint(int begin, int end, String weight) {
        // why was this variable named weight?
        keyMatrix[begin][end] = weight;
        keyMatrix[end][begin] = weight;
    }

    /**
     * Method to find the number of vertex from it's label.
     */
    private int findVertex(String label) {
        for (int i = 0; i < vertexCount; i++) {
            if ((vertexArray[i] != null) && (vertexArray[i].getLabel().equals(label))) {
                return i;
            }
        }
        return -1;
    }

    /**
     * This method creates graph, adding vertices and points using data from
     * file,
     *
     * @Param graphList is file with adj list.
     */
    private void importGraphFromFile(File graphList) throws IOException, FunctionalException {
        try (BufferedReader graphListBufReader = new BufferedReader(new FileReader(graphList))) {
            String line;
            while ((line = graphListBufReader.readLine()) != null) {
                String[] connectsAndNumbers = line.split(" : "); 							// X11 a3:35
                String[] connectedVertices = connectsAndNumbers[0].split(" ");
                int conVertsLength = connectedVertices.length;
                int[] numConnectedVertices = new int[conVertsLength];
                for (int i = 0; i < conVertsLength; i++) {
                    addVertex(connectedVertices[i]);
                    numConnectedVertices[i] = findVertex(connectedVertices[i]);
                    if (numConnectedVertices[i] < 0) {
                        throw new FunctionalException("Local graph can not be imported.");
                    }
                }

                for (int i = 0; i < conVertsLength; i++) {
                    for (int j = 0; j < conVertsLength; j++) {
                        if ((numConnectedVertices[i] < numConnectedVertices[j]) && (Math.abs(i - j) == 1)) {
                            addPoint(numConnectedVertices[i], numConnectedVertices[j], 1);
                            addKeyPoint(numConnectedVertices[i], numConnectedVertices[j], connectsAndNumbers[1]);
                        }
                    }
                }
            }
        }
    }

    public static class CBFactory implements ConnectionGraphFactory {

        @Override
        public NonOrientedCBGraph createConnectionGraph(String graphName) {
            return new NonOrientedCBGraph(graphName);
        }
    }

}
