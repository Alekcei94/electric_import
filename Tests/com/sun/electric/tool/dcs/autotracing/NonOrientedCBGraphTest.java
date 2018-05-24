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

    private static ConnectionGraphFactory fab;

    public NonOrientedCBGraphTest() {
    }

    @BeforeClass
    public static void setUpClass() {
        fab = new NonOrientedCBGraph.CBFactory();
        ConnectionGraphInterface instance = fab.createConnectionGraphCBLarge("CB<100"); //used to create first UNMODIFIED copy
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {

    }

    @After
    public void tearDown() {
    }

    /*

    * Method reflection global variable String[] globVerts.
     */
    private String[] getReflectionGlobVerts(Class example, ConnectionGraphInterface sc) throws Exception {
        Field varible = example.getDeclaredField("globVerts");
        varible.setAccessible(true);
        String[] globVerts = (String[]) varible.get(sc);
        return globVerts;
    }

    /*
    *Use Reflection metod FindVertex original
     */
    private int getReflectionMethodFindVertex(Class example, ConnectionGraphInterface sc, String connectedVertices) throws Exception {
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
    private int getReflectionFindIntForLinksMatrix(Class example, ConnectionGraphInterface sc, String findThis) throws Exception {
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

    /*
     * Test of FindIntForLinksMatrix method, of class NonOrientedCBGraph.
     */
    @Test
    public void testFindIntForLinksMatrix() throws Exception {
        System.out.println("findIntForLinksMatrix");
        
        Class CBGraph = Class.forName("com.sun.electric.tool.dcs.autotracing.NonOrientedCBGraph");
        ConnectionGraphInterface sc = (new NonOrientedCBGraph.CBFactory()).createConnectionGraphCBLarge("CB<0");
        String[] globVerts = getReflectionGlobVerts(CBGraph, sc);

        int[] result = new int[10];
        int[] expectedResult = new int[10];
        for (int i = 0; i < 10; i++) {
            int randomNumber = 0 + (int) (Math.random() * globVerts.length);
            String parametr = globVerts[randomNumber];
            result[i] = getReflectionFindIntForLinksMatrix(CBGraph, sc, parametr);
            expectedResult[i] = randomNumber;
        }
        assertArrayEquals(result, expectedResult);
    }

    @Test
    public void testGetCloseVerteces() throws ClassNotFoundException, Exception {
        System.out.println("getCloseVerteces");

        Class CBClass = Class.forName("com.sun.electric.tool.dcs.autotracing.NonOrientedCBGraph");
        Class[] paramTypes = new Class[]{String.class};
        
        ConnectionGraphInterface nocbg = (new NonOrientedCBGraph.CBFactory()).createConnectionGraphCBLarge("CB<0");
        
        Field vertexCountField = CBClass.getDeclaredField("vertexCount");
        vertexCountField.setAccessible(true);
        int input = 5;
        vertexCountField.set(nocbg, input);

        Field vertArrayField = CBClass.getDeclaredField("vertexArray");
        vertArrayField.setAccessible(true);
        Vertex[] vertArray = new Vertex[]{new Vertex("0"), new Vertex("1"), new Vertex("2"), new Vertex("3"), new Vertex("4"),
            new Vertex("5"), new Vertex("6"), null, new Vertex("8"), new Vertex("9")};
        vertArrayField.set(nocbg, vertArray);

        Field matrixField = CBClass.getDeclaredField("matrix");
        matrixField.setAccessible(true);
        int[][] matrix = new int[10][10];
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix.length; j++) {
                if ((i > j)&&(vertArray[i]!=null)&&(vertArray[j]!=null)) {
                    if ((j % 2 == 0) || (i % 3 == 0)) {
                        matrix[i][j] = 1;
                        matrix[j][i] = 1;
                    } else {
                        matrix[i][j] = 0;
                        matrix[j][i] = 0;
                    }
                }
            }
        }
        matrixField.set(nocbg, matrix);
        
        paramTypes = new Class[]{int.class};
        Method getCloseVerteces = CBClass.getDeclaredMethod("getCloseVerteces", paramTypes);
        getCloseVerteces.setAccessible(true);

        Integer[] result = (Integer[]) getCloseVerteces.invoke(nocbg, input);
        for(Integer inti : result) {
            if(matrix[inti][input] == 0) {
                assert false;
            } 
        }
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
}
