/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sun.electric.tool.dcs.autotracing;

import com.sun.electric.tool.dcs.Accessory;
import com.sun.electric.tool.dcs.ConstantsAndPrefs;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author diivanov
 */
public class NonOrientedCBGraphIT {

    public NonOrientedCBGraphIT() {
    }

    @BeforeClass
    public static void setUpClass() {
        Accessory.cleanFile(ConstantsAndPrefs.getPathTo("config"));
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

    /**
     * Integration test of all public methods in class NonOrientedCBGraph.
     */
    @Test
    public void testCreateAndGetResult() throws IOException, ClassNotFoundException,
            NoSuchFieldException, IllegalArgumentException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {

        System.out.println("testCreateAndGetResult");
        ConnectionGraphInterface nocbg = (new NonOrientedCBGraph.CBFactory()).createConnectionGraphCBLarge("CB<0");
        String elemFrom = "X5";
        String elemTo = "Y7";
        ArrayList<String> listOfPathes = getConfig(nocbg, elemFrom, elemTo);
        assert !listOfPathes.isEmpty();

        System.out.println("testGetWeight");
        int weight = testGetWeight(nocbg, elemFrom, elemTo);
        assert weight == listOfPathes.size();

    }

    private ArrayList<String> getConfig(ConnectionGraphInterface nocbg, String elemFrom, String elemTo) throws ClassNotFoundException, IllegalArgumentException,
            NoSuchFieldException, IllegalAccessException, FileNotFoundException, IOException {
        nocbg.getConfigurationPath(elemFrom, elemTo);

        ArrayList<String> listOfPathes = new ArrayList<>();

        Class CBClass = Class.forName("com.sun.electric.tool.dcs.autotracing.NonOrientedCBGraph");
        Field vertArrayField = CBClass.getDeclaredField("vertexArray");
        vertArrayField.setAccessible(true);
        Vertex[] vertArray = (Vertex[]) vertArrayField.get(nocbg);

        Field keyMatrixField = CBClass.getDeclaredField("keyMatrix");
        keyMatrixField.setAccessible(true);
        String[][] keyMatrix = (String[][]) keyMatrixField.get(nocbg);

        File file = new File(ConstantsAndPrefs.getPathTo("config"));
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                int num = Integer.valueOf(line);
                for (int i = 0; i < keyMatrix.length; i++) {
                    for (int j = 0; j < keyMatrix.length; j++) {
                        if (i > j) {
                            if (keyMatrix[i][j].equals("")) {
                            } else if (Integer.valueOf(keyMatrix[i][j]) == num) {
                                System.out.println(vertArray[i].getLabel() + " " + vertArray[j].getLabel());
                                listOfPathes.add(vertArray[i].getLabel() + " " + vertArray[j].getLabel());
                                break;
                            }
                        }

                    }
                }
            }
        }
        return listOfPathes;
    }

    /**
     * Test of getWeight method of class NonOrientedCBGraph.
     *
     * @param nocbg input graph(object)
     * @param elemFrom path from this element
     * @param elemTo path to this element
     * @return return the number of elements in path from one to another
     */
    public int testGetWeight(ConnectionGraphInterface nocbg, String elemFrom, String elemTo) {
        int i = nocbg.getWeight(elemFrom, elemTo);
        return i;
    }

    /* Get matrix field
            Field matrixField = CBClass.getDeclaredField("matrix");
        matrixField.setAccessible(true);
        int[][] matrix = (int[][]) matrixField.get(nocbg);
        for(int i=0;i<matrix.length;i++) {
            for(int j=0;j<matrix.length;j++) {
                System.out.println(matrix[i][j]);
            }
        }
     */
}
