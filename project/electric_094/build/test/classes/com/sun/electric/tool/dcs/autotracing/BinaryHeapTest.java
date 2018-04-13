/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sun.electric.tool.dcs.autotracing;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author diivanov
 */
public class BinaryHeapTest {

    /**
     * Test of getKeyOfMinValueElement method, of class BinaryHeap.
     */
    @Test
    public void testGetKeyOfMinValueElement() {
        System.out.println("getKeyOfMinValueElement");
        BinaryHeap heap = new BinaryHeap();
        heap.add(141, 0);
        heap.add(183, 15);
        heap.add(84, 31);
        heap.add(250, 4);
        heap.add(379, 50);
        int result = heap.getValueOfMinKeyElement();
        assertEquals(31, result);
        result = heap.getValueOfMinKeyElement();
        assertEquals(0, result);
        result = heap.getValueOfMinKeyElement();
        assertEquals(15, result);
        result = heap.getValueOfMinKeyElement();
        assertEquals(4, result);
        result = heap.getValueOfMinKeyElement();
        assertEquals(50, result);
    }
    
}
