/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sun.electric.tool.dcs.autotracing;

import com.sun.electric.tool.dcs.Pair;
import java.io.BufferedReader;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;

/**
 *
 * @author Astepanov
 */
public class NonOrientedCBGraphTest {

    private ConnectionGraphFactory fab;

    public NonOrientedCBGraphTest() {
    }

    @BeforeClass
    public static void setUpClass() {

    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
        fab = new NonOrientedCBGraph.CBFactory();

    }

    @After
    public void tearDown() {
    }

    /*
    * Method get reflection method ImportGraphFromFile.
     */
    private void getReflectionMethodImportGraphFromFile(Class example, NonOrientedCBGraph sc) throws Exception {
        Method importGraphFromFile = example.getDeclaredMethod("importGraphFromFile");
        importGraphFromFile.setAccessible(true);
        importGraphFromFile.invoke(sc);
    }

    /*
    * Method get reflection method addVertex.
     */
    private boolean getReflectionMethodAddVertex(Class example, NonOrientedCBGraph sc, String lable) throws Exception {
        Class[] paramTypes = new Class[]{String.class};
        Method addVertex = example.getDeclaredMethod("addVertex", paramTypes);
        addVertex.setAccessible(true);
        boolean t = (boolean) addVertex.invoke(sc, lable);
        return t;
    }

    /*
    * Method get reflection method deleteVertex.
     */
    private void getReflectionMethodDeleteVertex(Class example, NonOrientedCBGraph sc, int count) throws Exception {
        Class[] paramTypes = new Class[]{int.class};
        Method deleteVertex = example.getDeclaredMethod("deleteVertex", paramTypes);
        deleteVertex.setAccessible(true);
        deleteVertex.invoke(sc, count);
    }

    /*
    * Method reflection global variable int[][] Matrix.
     */
    private int[][] getReflectionMatrix(Class example, ConnectionGraphInterface sc) throws Exception {
        Field varible = example.getDeclaredField("matrix");
        varible.setAccessible(true);
        int[][] matrix = (int[][]) varible.get(sc);
        return matrix;
    }

    /*
    * Method reflection global variable String[] globVerts.
     */
    private String[] getReflectionGlobVerts(Class example, NonOrientedCBGraph sc) throws Exception {
        Field varible = example.getDeclaredField("globVerts");
        varible.setAccessible(true);
        String[] globVerts = (String[]) varible.get(sc);
        return globVerts;
    }

    /*
    *Use Reflection metod FindVertex original
     */
    private int getReflectionMethodFindVertex(Class example, NonOrientedCBGraph sc, String connectedVertices) throws Exception {
        Class[] paramTypes = new Class[]{String.class};
        Method findVertex = example.getDeclaredMethod("findVertex", paramTypes);
        findVertex.setAccessible(true);
        int NumConnectedVertices = (int) findVertex.invoke(sc, connectedVertices);
        return NumConnectedVertices;
    }

    /*
    *Use Reflection metod getCloseVerteces original
     */
    private Integer[] getReflectionCloseVerteces(Class example, NonOrientedCBGraph sc, int getCloseVertecesParametr) throws Exception {
        Class[] paramTypes = new Class[]{int.class};
        Method getCloseVerteces = example.getDeclaredMethod("getCloseVerteces", paramTypes);
        getCloseVerteces.setAccessible(true);
        Integer[] getCloseVertecesResult = (Integer[]) getCloseVerteces.invoke(sc, getCloseVertecesParametr);
        return getCloseVertecesResult;
    }

    /*
    *Use Reflection metod findIntForLinksMatrix original
     */
    private int getReflectionFindIntForLinksMatrix(Class example, NonOrientedCBGraph sc, String findThis) throws Exception {
        Class[] paramTypes = new Class[]{String.class};
        Method findIntForLinksMatrix = example.getDeclaredMethod("findIntForLinksMatrix", paramTypes);
        findIntForLinksMatrix.setAccessible(true);
        int findIntForLinksMatrixResult = (int) findIntForLinksMatrix.invoke(sc, findThis);
        return findIntForLinksMatrixResult;
    }

    /**
     * Test of getLabel method, of class NonOrientedCBGraph.
     */
    @Test
    public void testGetLabel() {
        System.out.println("getLabel+");

        ConnectionGraphInterface instance = fab.createConnectionGraphCBLarge("CB<100");

        String expResult = "CB<100";
        String result = instance.getLabel();
        assertEquals(expResult, result);

    }

    /**
     * Test of deleteKeyFromCBGraph method, of class NonOrientedCBGraph.
     */
    @Test
    public void testDeleteKeyFromCBGraph() throws Exception {
        System.out.println("deleteKeyFromCBGraph");

        Class example = Class.forName("com.sun.electric.tool.dcs.autotracing.NonOrientedCBGraph");
        ConnectionGraphInterface sc = fab.createConnectionGraphCBLarge("CB");

       /* Method importGraphFromFile = example.getDeclaredMethod("importGraphFromFile");
        importGraphFromFile.setAccessible(true);
        importGraphFromFile.invoke(sc);*/

        Field varible = example.getDeclaredField("matrix");
        varible.setAccessible(true);
        int[][] matrix = (int[][]) varible.get(sc);

        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix.length; j++) {
                if (matrix[i][j] != 0) {
                    System.out.print(" 12 ");
                }
            }
        }

        NonOrientedCBGraph instance = null;
        List<Pair<String, String>> expResult = null;
        // List<Pair<String, String>> result = instance.deleteKeyFromCBGraph(key);
        //assertEquals(expResult, result);
    }

    /**
     * Test of getAdditionalUsedExternalPins method, of class
     * NonOrientedCBGraph.
     */
    @Test
    public void testGetAdditionalUsedExternalPins() throws Exception {
        System.out.println("getAdditionalUsedExternalPins");

        NonOrientedCBGraph sc = (NonOrientedCBGraph) fab.createConnectionGraphCBLarge("CB");
        Class example = Class.forName("com.sun.electric.tool.dcs.autotracing.NonOrientedCBGraph");

        Field varible = example.getDeclaredField("usedExternalPinsInGraph");
        varible.setAccessible(true);
        varible.set(sc, readFileWritedArrayPair());
        List<Pair<String, String>> usedExternalPinsInGraph = (List<Pair<String, String>>) varible.get(sc);

        List<Pair<String, String>> resultArray = sc.getAdditionalUsedExternalPins();
        for (int i = 0; i < resultArray.size(); i++) {
            boolean result = false;
            if ((usedExternalPinsInGraph.get(i).getFirstObject().equals(resultArray.get(i).getFirstObject())) && (usedExternalPinsInGraph.get(i).getSecondObject().equals(resultArray.get(i).getSecondObject()))) {
                result = true;
            }
            assertEquals(result, true);
        }
        usedExternalPinsInGraph = (List<Pair<String, String>>) varible.get(sc);
        assertEquals(usedExternalPinsInGraph.size(), 0);
    }

    /*
     * Test of FindIntForLinksMatrix method, of class NonOrientedCBGraph.
     */
    @Test
    public void testFindIntForLinksMatrix() throws Exception {
        System.out.println("findIntForLinksMatrix");
        Class example = Class.forName("com.sun.electric.tool.dcs.autotracing.NonOrientedCBGraph");
        NonOrientedCBGraph sc = (NonOrientedCBGraph) fab.createConnectionGraphCBLarge("CB");

        String[] globVerts = getReflectionGlobVerts(example, sc);

        int[] result = new int[10];
        int[] expectedResult = new int[10];
        for (int i = 0; i < 10; i++) {
            int randomNumber = 0 + (int) (Math.random() * globVerts.length);
            String parametr = globVerts[randomNumber];
            result[i] = getReflectionFindIntForLinksMatrix(example, sc, parametr);
            expectedResult[i] = randomNumber;
        }
        assertArrayEquals(result, expectedResult);
    }

    @Test
    public void testGetCloseVerteces() throws ClassNotFoundException, Exception {
        System.out.println("getCloseVerteces");
        Class example = Class.forName("com.sun.electric.tool.dcs.autotracing.NonOrientedCBGraph");
        NonOrientedCBGraph sc = (NonOrientedCBGraph) fab.createConnectionGraphCBLarge("CB");

        int parametr = 0;//to think
        Integer[] result;
        result = getReflectionCloseVerteces(example, sc, parametr);

        /* for (int i = 0; i < result.length; i++) {
            System.out.println(result[i]);
        }*/
        assertEquals(result, 0);
    }

    /**
     * Test of FindVertex private method, of class NonOrientedCBGraph.
     */
    @Test
    public void testFindVertex() throws ClassNotFoundException, Exception {
        System.out.println("findVertex");
        Class example = Class.forName("com.sun.electric.tool.dcs.autotracing.NonOrientedCBGraph");
        NonOrientedCBGraph sc = (NonOrientedCBGraph) fab.createConnectionGraphCBLarge("CB");

        List<String> vertexFromFile = new ArrayList<>(testReadFileFromMethodFindVertex());

        int result;
        int expectedResult = 3;
        Vertex[] vertexArrayq = new Vertex[5];

        Field varible = example.getDeclaredField("vertexArray");
        varible.setAccessible(true);
        vertexArrayq[0] = new Vertex("X1");
        vertexArrayq[1] = new Vertex("a3");
        vertexArrayq[2] = new Vertex("k7");
        vertexArrayq[3] = new Vertex("a4");
        vertexArrayq[4] = new Vertex("a1");
        varible.set(sc, vertexArrayq);
        Vertex[] vertexArray = (Vertex[]) varible.get(sc);

        String vertex = vertexFromFile.get(11);
        result = getReflectionMethodFindVertex(example, sc, vertex);

        assertEquals(result, expectedResult);

    }

    /*
    * Method serves for read file TestingMethod.info (produced using a special script) and split this
     */
    private List<String> testReadFileFromMethodFindVertex() throws IOException {

        try (BufferedReader graphListBufReader = new BufferedReader(new FileReader("./autotracing/TestingMethod.info"))) {
            List<String> vertexFromFile = new ArrayList<>();
            String line;
            while ((line = graphListBufReader.readLine()) != null) {
                String[] connectedVertices = line.split(" ");
                for (int i = 0; i < connectedVertices.length; i++) {
                    if (connectedVertices[i] != null) {
                        vertexFromFile.add(connectedVertices[i]);
                    }
                }
            }
            return vertexFromFile;
        }
    }

    /*
    * Method serves for read file CBGraph.trc (produced using a special script) and split this
     */
    private List<String> testReadFileFromMethodAllFindVertex() throws IOException {

        try (BufferedReader graphListBufReader = new BufferedReader(new FileReader("./autotracing/CBGraph.trc"))) {
            List<String> vertexFromFile = new ArrayList<>();
            String line;
            while ((line = graphListBufReader.readLine()) != null) {
                String[] connectsAndNumbers = line.split(":");
                String[] connectedVertices = connectsAndNumbers[1].split(" ");
                for (int i = 0; i < connectedVertices.length; i++) {
                    if (connectedVertices[i] != null) {
                        vertexFromFile.add(connectedVertices[i]);
                    }
                }
            }
            return vertexFromFile;
        }
    }

    /*
    * Method read file and forms ArrayList<Pari<nameVertices_0, nameVertices_1>> of all Vertices.
     */
    private List<Pair<String, String>> readFileWritedArrayPair() throws FileNotFoundException, IOException {
        try (BufferedReader graphListBufReader = new BufferedReader(new FileReader("./autotracing/CBGraph.trc"))) {
            String line;
            List<Pair<String, String>> arrayVerticesInFile = new ArrayList<>();
            while ((line = graphListBufReader.readLine()) != null) {
                String[] connectsAndNumbers = line.split(":");
                String[] connectedVertices = connectsAndNumbers[1].split(" ");
                Pair<String, String> pairVerticesInFile = new Pair<>(connectedVertices[0], connectedVertices[1]);
                arrayVerticesInFile.add(pairVerticesInFile);
            }
            return arrayVerticesInFile;
        }
    }

    /*
    * Method read ArrayList<Pari<nameVertices_0, nameVertices_1>> of all Vertices and forms ArrayList<Pari<nameVertices_0, nameVertices_1>> 
    * necessary for the test string in file CBGraph.trc
     */
    private List<Pair<String, String>> listPairInTest(int[] listIntTesting, List<Pair<String, String>> arrayVerticesInFile) {
        List<Pair<String, String>> arrayVerticesInTest = new ArrayList<>();
        for (int i = 0; i < listIntTesting.length; i++) {
            arrayVerticesInTest.add(arrayVerticesInFile.get(listIntTesting[i] - 1));
        }
        return arrayVerticesInTest;

    }

    /*
    * Method get method findVertex and form array of coordinates.
     */
    private int[] getIntReflectionMethodFindVertex(Class example, NonOrientedCBGraph sc, String[] connectedVertices) throws Exception {
        Class[] paramTypes = new Class[]{String.class
        };
        Method findVertex = example.getDeclaredMethod("findVertex", paramTypes);
        findVertex.setAccessible(true);
        int[] arrayNumConnectedVertices = new int[connectedVertices.length];
        for (int i = 0; i < connectedVertices.length; i++) {
            arrayNumConnectedVertices[i] = (int) findVertex.invoke(sc, connectedVertices[i]);
        }
        return arrayNumConnectedVertices;
    }
}
