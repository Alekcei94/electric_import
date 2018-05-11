/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sun.electric.tool.dcs.autotracing;

import com.sun.electric.tool.dcs.Pair;
import java.io.BufferedReader;

import java.io.FileNotFoundException;
import java.util.List;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import java.lang.reflect.Field;

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
        System.out.println("getLabel+");

        ConnectionGraphInterface instance = fab.createConnectionGraph("CB<100");

        String expResult = "CB<100";
        String result = instance.getLabel();
        assertEquals(expResult, result);

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
    }

    /*
     * Test of FindIntForLinksMatrix method, of class NonOrientedCBGraph.
     */
    @Test
<<<<<<< HEAD
    public void testimportGraphFromFile() throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        System.out.println("importGraphFromFile()");
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
}
