/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sun.electric.tool.dcs.autotracing;

import com.sun.electric.tool.dcs.Exceptions.InvalidInputException;
import com.sun.electric.tool.dcs.SpecificStructures.Pair;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 *
 */
public abstract class AbstractGraphStructure {
    
    /**
     * Method to import graph object from text file.
     *
     * @param graphFile
     */
    protected final void importGraph(File graphFile) {
        try (final BufferedReader br = new BufferedReader(new FileReader(graphFile))) {
            String line;
            while ((line = br.readLine()) != null) {
                importOneLine(line);
            }
        } catch(IOException ioe) {
            throw new AssertionError("Graph file is corrupted or doesn't exist.");
        }
    }
    /**
     * Method to handle one line of import file,
     * should be implemented with class' own logic.
     * @param line 
     */
    protected abstract void importOneLine(String line);
    
    /**
     * Class to implement cloning logic to use it as copy constructor.
     */
    protected class CloneGraphStructure {

        /**
         * Constructor executes cloning all complex objects of mother's class.
         */
        protected CloneGraphStructure() {
        }

        /**
         * Method to create deep copy of hashmap by copying every object while
         * iterating through map with copyObject method, Default copyObject
         * method works with Strings, Verteces, Pairs and several types of Lists
         * of these elements.
         *
         * @param mapToCopy
         * @return
         */
        protected <A, B> HashMap<A, B> createDeepCopyOfMap(Map<A, B> mapToCopy) {
            HashMap<A, B> copiedMap = new HashMap<>();
            mapToCopy.entrySet().forEach((entry) -> {
                A key = (A) entry.getKey();
                A copiedKey = (A) copyObject(key);
                B value = (B) entry.getValue();
                B copiedValue = (B) copyObject(value);
                copiedMap.put(copiedKey, copiedValue);
            });
            return copiedMap;
        }

        /**
         * Method to support deep copy of complex objects. Method should be
         * extended for new object types.
         *
         * @param object
         * @return
         */
        protected <E> Object copyObject(Object object) {
            if (object instanceof String) {
                return (String) object;
            } else if (object instanceof Pair) {
                // Pair's objects can't be deep copied so it must be used only with strings or other immutable classes.
                return new Pair(((Pair) object).getFirstObject(), ((Pair) object).getSecondObject());
            } else if (object instanceof Vertex) {
                return new Vertex((Vertex) object);
            } else if (object instanceof LinkedList) {
                List<E> list = (LinkedList<E>) object;
                LinkedList<E> copiedList = new LinkedList<>();
                for (E obj : list) {
                    copiedList.add((E) copyObject(obj));
                }
                return copiedList;
            } else if (object instanceof ArrayList) {
                List<E> list = (ArrayList<E>) object;
                ArrayList<E> copiedList = new ArrayList<>();
                for (E obj : list) {
                    copiedList.add((E) copyObject(obj));
                }
                return copiedList;
            } else {
                throw new InvalidInputException("Unexpected object type to copy: "
                        + object.toString());
            }
        }
    }
}
