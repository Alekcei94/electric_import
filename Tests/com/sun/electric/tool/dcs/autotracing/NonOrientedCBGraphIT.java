/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sun.electric.tool.dcs.autotracing;

import com.sun.electric.tool.dcs.Accessory;
import com.sun.electric.tool.dcs.ConstantsAndPrefs;
import com.sun.electric.tool.dcs.Pair;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.List;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

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
     * Test of getLabel method, of class NonOrientedCBGraph.
     */
    @Test
    public void testCreateAndGetResult() throws FileNotFoundException, IOException, ClassNotFoundException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
        ConnectionGraphInterface nocbg = (new NonOrientedCBGraph.CBFactory()).createConnectionGraphCBLarge("CB<0");
        String elemFrom = "Y5";
        String elemTo = "K7";
        nocbg.getConfigurationPath(elemFrom, elemTo);

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
                                continue;
                            }
                            else if (Integer.valueOf(keyMatrix[i][j]) == num) {
                                System.out.println(vertArray[i].getLabel() + " " + vertArray[j].getLabel());
                                break;
                            }
                        }

                    }
                }
            }
        }

    }
}
