/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sun.electric.tool.dcs.autotracing;

import com.sun.electric.database.topology.NodeInst;
import java.io.File;
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
public class AccessoryTest {
    
    public AccessoryTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
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
     * Test of toBinary method, of class Accessory.
     */
    @Test
    public void testToBinary() {
        System.out.println("toBinary");
        testToBinaryWithInput(140,8, new boolean[]{true, false, false, false, true, true, false, false});
        testToBinaryWithInput(140,9, new boolean[]{false, true, false, false, false, true, true, false, false});
    }
    
    private void testToBinaryWithInput(int number, int base, boolean[] expectedResult) {
        boolean[] result = Accessory.toBinary(number, base);
        assertArrayEquals(expectedResult, result);
    }

}
