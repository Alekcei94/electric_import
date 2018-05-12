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
import java.lang.reflect.Constructor;
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
import org.junit.Assert;

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
    * Method get reflection method ImportGraphFromFile.
     */
    private void getReflectionMethodAddVertex(Class example, NonOrientedCBGraph sc, String lable) throws Exception {
        Class[] paramTypes = new Class[]{String.class};
        Method addVertex = example.getDeclaredMethod("addVertex", paramTypes);
        addVertex.setAccessible(true);
        addVertex.invoke(sc, lable);
    }

    /*
    * Method get reflection method ImportGraphFromFile.
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
    private int[][] getReflectionMatrix(Class example, NonOrientedCBGraph sc) throws Exception {
        Field varible = example.getDeclaredField("matrix");
        varible.setAccessible(true);
        int[][] matrix = (int[][]) varible.get(sc);
        return matrix;
    }

    /*
    * Method reflection global variable List<Pair<String, String>> usedExternalPinsInGraph.
     */
    private List<Pair<String, String>> getUsedExternalPinsInGraph(Class example, NonOrientedCBGraph sc) throws Exception {
        Field varible = example.getDeclaredField("usedExternalPinsInGraph");
        varible.setAccessible(true);
        List<Pair<String, String>> usedExternalPinsInGraph = (List<Pair<String, String>>) varible.get(sc);
        return usedExternalPinsInGraph;
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
    private Integer[] getCloseVerteces(Class example, NonOrientedCBGraph sc, int getCloseVertecesParametr) throws Exception {
        Class[] paramTypes = new Class[]{int.class};
        Method getCloseVerteces = example.getDeclaredMethod("getCloseVerteces", paramTypes);
        getCloseVerteces.setAccessible(true);
        Integer[] getCloseVertecesResult = (Integer[]) getCloseVerteces.invoke(sc, getCloseVertecesParametr);
        return getCloseVertecesResult;
    }

    /*
    *Use Reflection metod findIntForLinksMatrix original
     */
    private int findIntForLinksMatrix(Class example, NonOrientedCBGraph sc, String findThis) throws Exception {
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
        /*System.out.println("getLabel+");

        ConnectionGraphInterface instance = fab.createConnectionGraph("CB<100");

        String expResult = "CB<100";
        String result = instance.getLabel();
        assertEquals(expResult, result);*/

    }

    /**
     * Test of deleteKeyFromCBGraph method, of class NonOrientedCBGraph.
     */
    @Test
    public void testDeleteKeyFromCBGraph() {
        System.out.println("deleteKeyFromCBGraph");
        String key = "";
        NonOrientedCBGraph instance = null;
        List<Pair<String, String>> expResult = null;
        List<Pair<String, String>> result = instance.deleteKeyFromCBGraph(key);
        assertEquals(expResult, result);
    }

    /**
     * Test of getConfigurationPath method, of class NonOrientedCBGraph.
     */
    @Test
    public void testGetConfigurationPath() {
        System.out.println("getConfigurationPath");
        String elemFrom = "";
        String elemTo = "";
        NonOrientedCBGraph instance = null;
        instance.getConfigurationPath(elemFrom, elemTo);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of doDeleteUsedVerts method, of class NonOrientedCBGraph.
     */
    @Test
    public void testDoDeleteUsedVerts() {
        System.out.println("doDeleteUsedVerts");
        NonOrientedCBGraph instance = null;
        List<Pair<String, String>> expResult = null;
        List<Pair<String, String>> result = instance.doDeleteUsedVerts();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of refreshLinksMatrix method, of class NonOrientedCBGraph.
     */
    @Test
    public void testRefreshLinksMatrix() {
        System.out.println("refreshLinksMatrix");
        NonOrientedCBGraph instance = null;
        instance.refreshLinksMatrix();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getAdditionalUsedExternalPins method, of class
     * NonOrientedCBGraph.
     */
    @Test
    public void testGetAdditionalUsedExternalPins() throws Exception {
        System.out.println("getAdditionalUsedExternalPins");
        
        Class example = Class.forName("com.sun.electric.tool.dcs.autotracing.NonOrientedCBGraph");
        Class[] paramTypes = new Class[]{String.class};
        Constructor Constrct = example.getDeclaredConstructor(paramTypes);
        Constrct.setAccessible(true);
        NonOrientedCBGraph sc = (NonOrientedCBGraph) Constrct.newInstance("CB");
        
        
        List<String> ArrayNameVertex = new ArrayList<>(testReadFileFromMethodFindVertex());
        for (int i = 0; i < ArrayNameVertex.size(); i++) {
            getReflectionMethodAddVertex(example, sc, ArrayNameVertex.get(i));
        }
        for (int i = 0; i < ArrayNameVertex.size(); i++) {
            int randomNumber = 0 + (int) (Math.random() * ArrayNameVertex.size());
            getReflectionMethodDeleteVertex(example, sc, randomNumber);
        }
        List<Pair<String, String>> result = sc.getAdditionalUsedExternalPins();
        List<Pair<String, String>> expectedResult = getUsedExternalPinsInGraph(example, sc);
        System.out.println(result.size());
        //  fail("The test case is a prototype.");
        //assertArrayEquals(result, expectedResult);
    }

    /*
     * Test of FindIntForLinksMatrix method, of class NonOrientedCBGraph.
     */
    @Test
    public void testFindIntForLinksMatrix() throws Exception {
        System.out.println("findIntForLinksMatrix");
        Class example = Class.forName("com.sun.electric.tool.dcs.autotracing.NonOrientedCBGraph");
        Class[] paramTypes = new Class[]{String.class};
        Constructor Constrct = example.getDeclaredConstructor(paramTypes);
        Constrct.setAccessible(true);
        NonOrientedCBGraph sc = (NonOrientedCBGraph) Constrct.newInstance("CB");

        String[] globVerts = getReflectionGlobVerts(example, sc);

        int[] result = new int[10];
        int[] expectedResult = new int[10];
        for (int i = 0; i < 10; i++) {
            int randomNumber = 0 + (int) (Math.random() * globVerts.length);
            String parametr = globVerts[randomNumber];
            result[i] = findIntForLinksMatrix(example, sc, parametr);
            expectedResult[i] = randomNumber;
        }
        assertArrayEquals(result, expectedResult);
    }

    @Test
    public void testGetCloseVerteces() throws ClassNotFoundException, Exception {
        System.out.println("getCloseVerteces");
        Class example = Class.forName("com.sun.electric.tool.dcs.autotracing.NonOrientedCBGraph");
        Class[] paramTypes = new Class[]{String.class};
        Constructor Constrct = example.getDeclaredConstructor(paramTypes);
        Constrct.setAccessible(true);
        NonOrientedCBGraph sc = (NonOrientedCBGraph) Constrct.newInstance("CB");

        int parametr = 0;//to think
        Integer[] result;
        result = getCloseVerteces(example, sc, parametr);

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
        Class[] paramTypes = new Class[]{String.class};
        Constructor Constrct = example.getDeclaredConstructor(paramTypes);
        Constrct.setAccessible(true);
        NonOrientedCBGraph sc = (NonOrientedCBGraph) Constrct.newInstance("CB");

        List<String> vertexFromFile = new ArrayList<>(testReadFileFromMethodFindVertex());

        int[] result = new int[10];
        int[] expectedResult = new int[10];
        for (int j = 0; j < 10; j++) {
            int randomNumber = 0 + (int) (Math.random() * vertexFromFile.size());
            String vertex = vertexFromFile.get(randomNumber);
            result[j] = getReflectionMethodFindVertex(example, sc, vertex);
            for (int i = 0; i < vertexFromFile.size(); i++) {
                if (vertexFromFile.get(i).equals(vertex)) {
                    expectedResult[j] = i;
                }
            }
        }
        assertArrayEquals(result, expectedResult);

    }

    @Test
    public void testImportGraphFromFile() throws ClassNotFoundException, Exception {
        System.out.println("importGraphFromFile+");
        Class example = Class.forName("com.sun.electric.tool.dcs.autotracing.NonOrientedCBGraph");
        Class[] paramTypes = new Class[]{String.class};
        Constructor Constrct = example.getDeclaredConstructor(paramTypes);
        Constrct.setAccessible(true);
        NonOrientedCBGraph sc = (NonOrientedCBGraph) Constrct.newInstance("CB");

        getReflectionMethodImportGraphFromFile(example, sc);

        List<Pair<String, String>> arrayVerticesInFile = new ArrayList<>(readFileWritedArrayPair());
        List<Pair<String, String>> arrayVerticesInTest = new ArrayList<>(listPairInTest(new int[]{52, 1, 25, 35, 100, 11, 21, 10, 15, 123}, arrayVerticesInFile));

        String[] getNameVetricesFerstPair = new String[arrayVerticesInTest.size()];
        String[] getNameVetricesSecondPair = new String[arrayVerticesInTest.size()];

        for (int i = 0; i < arrayVerticesInTest.size(); i++) {
            getNameVetricesFerstPair[i] = arrayVerticesInTest.get(i).getFirstObject();
            getNameVetricesSecondPair[i] = arrayVerticesInTest.get(i).getSecondObject();
        }

        int[] coordinatesMatrixI = getIntReflectionMethodFindVertex(example, sc, getNameVetricesFerstPair);
        int[] coordinatesMatrixJ = getIntReflectionMethodFindVertex(example, sc, getNameVetricesSecondPair);
        int[][] matrix = getReflectionMatrix(example, sc);

        int[] result_0 = new int[coordinatesMatrixI.length];
        int[] result_1 = new int[coordinatesMatrixJ.length];

        int[] realResult = new int[coordinatesMatrixJ.length];

        for (int i = 0; i < coordinatesMatrixI.length; i++) {
            result_0[i] = matrix[coordinatesMatrixI[i]][coordinatesMatrixJ[i]];
            result_1[i] = matrix[coordinatesMatrixJ[i]][coordinatesMatrixI[i]];
            realResult[i] = 1;
        }

        assertArrayEquals(result_0, realResult);
        assertArrayEquals(result_1, realResult);
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
                    vertexFromFile.add(connectedVertices[i]);
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
        Class[] paramTypes = new Class[]{String.class};
        Method findVertex = example.getDeclaredMethod("findVertex", paramTypes);
        findVertex.setAccessible(true);
        int[] arrayNumConnectedVertices = new int[connectedVertices.length];
        for (int i = 0; i < connectedVertices.length; i++) {
            arrayNumConnectedVertices[i] = (int) findVertex.invoke(sc, connectedVertices[i]);
        }
        return arrayNumConnectedVertices;
    }
}
