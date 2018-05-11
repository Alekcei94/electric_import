/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sun.electric.tool.dcs.autotracing;

import com.sun.electric.tool.dcs.Pair;
import java.util.List;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

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

    /**
     * Test of getLabel method, of class NonOrientedCBGraph.
     */
    @Test
    public void testGetLabel() {//+
        System.out.println("getLabel");

        ConnectionGraphInterface instance = fab.createConnectionGraphCBLarge("CB<100");

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
    public void testGetAdditionalUsedExternalPins() {
        System.out.println("getAdditionalUsedExternalPins");
        NonOrientedCBGraph instance = (NonOrientedCBGraph) fab.createConnectionGraphCBLarge("CB<100");
        // NonOrientedCBGraph instance = null;
        //List<Pair<String, String>> expResult = ;
        List<Pair<String, String>> result = instance.getAdditionalUsedExternalPins();
        //assertEquals(expResult, result);
        assert false;
    }

    @Test
    public void testimportGraphFromFile() throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        System.out.println("importGraphFromFile()");
        Class example = Class.forName("com.sun.electric.tool.dcs.autotracing.NonOrientedCBGraph");
        NonOrientedCBGraph sc = (NonOrientedCBGraph) example.newInstance();
        
        // Обращение к методу
        //demoReflectionMethod(example, sc);
        NonOrientedCBGraph instance = (NonOrientedCBGraph) fab.createConnectionGraph("CB<100", new String[]{"a", "b"}, 124);
        //File expResult = new File("./autotracing/CBGraph.trc");
        //demoReflectionMethod();
        //assertEquals(expResult, result);
        assert false;
    }
}
