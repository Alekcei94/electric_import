/* Electric(tm) VLSI Design System
 *
 * File: BinaryHeap.java
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

import java.util.ArrayList;

/**
 * This class implements typical BinaryHeap functionality and is used as is.
 * Value sorting was added as extension to basic functionality.
 * 0 11 2222 3333333 tree.
 */
public class BinaryHeap {

    private ArrayList<Pair<Integer, Integer>> pairList;

    /**
     * Constructor of heap.
     */
    public BinaryHeap() {
        pairList = new ArrayList<>();
    }

    /**
     * Method to get full size of heap
     */
    private int getKeyHeapSize() {
        return this.pairList.size();
    }

    /**
     * Method to add element to heap, Method is using key-value pair as element.
     * Replacing value of pair if it's less than original.
     * @param value
     * @param key
     */
    public void add(int value, int key) {
        boolean exist = false;
        Pair<Integer, Integer> existingPair = null;
        for (Pair<Integer, Integer> pair : pairList) {
            if (pair.getFirstObject().equals(key)) {
                exist = true;
                existingPair = pair;
            }
        }
        if (exist) {
            int oldVal = existingPair.getSecondObject();
            if (value < oldVal) {
                existingPair.setSecondObject(value);
                heapifyUp(pairList.indexOf(existingPair));
            }
        } else {
            Pair<Integer, Integer> pair = new Pair<>(key, value);
            pairList.add(pair);
            heapifyUp(getKeyHeapSize() - 1);
        }
    }

    /**
     * Reestablish heap property for element (going only up from its position).
     */
    private void heapifyUp(int i) {
        if (i == 0) {
            return;
        }
        int parent = (i - 1) / 2;

        while (i > 0 && pairList.get(parent).getSecondObject() > pairList.get(i).getSecondObject()) {
            Pair<Integer, Integer> tempPair = pairList.get(i);
            pairList.set(i, pairList.get(parent));
            pairList.set(parent, tempPair);

            i = parent;
            parent = (i - 1) / 2;
        }
    }

    /**
     * Reestablish heap property for element (going only down from its position).
     * heapifyDown is used when we're deleting minimum element.
     */
    private void heapifyDown(int i) {
        int leftChild;
        int rightChild;
        int largestChild;

        for (;;) {
            leftChild = 2 * i + 1;
            rightChild = 2 * i + 2;
            largestChild = i;

            if (leftChild < getKeyHeapSize() && pairList.get(leftChild).getSecondObject() < pairList.get(largestChild).getSecondObject()) {
                largestChild = leftChild;
            }

            if (rightChild < getKeyHeapSize() && pairList.get(rightChild).getSecondObject() < pairList.get(largestChild).getSecondObject()) {
                largestChild = rightChild;
            }

            if (largestChild == i) {
                break;
            }
            
            Pair<Integer, Integer> tempPair = pairList.get(i);
            pairList.set(i, pairList.get(largestChild));
            pairList.set(largestChild, tempPair);

            i = largestChild;
        }
    }

    /**
     * Method to pop value of element with minimum key.
     * 
     * @return
     */
    public int getValueOfMinKeyElement() {
        if (getKeyHeapSize() == 0) {
            return -1;
        }
        
        int result = pairList.get(0).getFirstObject();
        pairList.set(0, pairList.get(getKeyHeapSize() - 1));
        pairList.remove(getKeyHeapSize() - 1);
        
        heapifyDown(0);
        return result;
    }

}
