/* Electric(tm) VLSI Design System
 *
 * File: Accessory.java
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
package com.sun.electric.tool.dcs;

import java.io.IOException;
import java.io.File;
import java.io.FileWriter;
import java.io.FileReader;
import java.io.BufferedReader;

import javax.swing.JOptionPane;

import java.util.ArrayList;
import java.util.Iterator;
import java.io.PrintWriter;

import com.sun.electric.database.hierarchy.Cell;
import com.sun.electric.tool.Job;
import com.sun.electric.database.topology.NodeInst;

/**
 * Class is needed to have constant access to utility methods like writing to text
 * file or starting/checking 1-touch timer.
 * @author diivanov
 */
public class Accessory {

    private static long timeStart;
    private static long deltaTime;

    /**
     * private constructor prohibits creating objects of this class.
     */
    private Accessory() {
        throw new AssertionError();
    }
    
    /**
     * Method to append string @text to file @fileName.
     *
     * @param fileName
     * @param text
     */
    public static void write(String fileName, String text) {
        try {
            try (FileWriter fw = new FileWriter(fileName, true)) {
                fw.write(text + "\n");
            }
        } catch (IOException ioe) {
            System.err.println("IOException: " + ioe.getMessage());
        }
    }
    

    /**
     * Method to show dialog to user.
     *
     * @param s
     */
    public static void showMessage(String s) {
        JOptionPane.showMessageDialog(null, s);
    }

    /**
     * Method to show log message to user when preferences allow this.
     *
     * @param s
     */
    public static void printLog(String s) {
        //if (ConstantsAndPrefs.isLogging()) {
            System.out.println(s);
        //}
    }

    /**
     * Method to recreate file with @fileName.
     *
     * @param fileName
     */
    public static void cleanFile(String fileName) {
        try {
            PrintWriter pw = new PrintWriter(fileName);
            pw.close();
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }

    /**
     * Method to decompose in powers of two.
     *
     * @param number
     * @param base
     * @return
     */
    public static boolean[] toBinary(int number, int base) {
        final boolean[] ret = new boolean[base];
        for (int i = 0; i < base; i++) {
            ret[base - 1 - i] = ((1 << i) & number) != 0;
        }
        return ret;
    }

    /**
     * String counter in file.
     *
     * @param file
     * @return
     */
    public static int getStringCount(File file) {
        int qr = 0;
        BufferedReader bufferedReader;
        try {
            FileReader fileReader = new FileReader(file);
            bufferedReader = new BufferedReader(fileReader);
            while (bufferedReader.readLine() != null) {
                qr++;
            }
            bufferedReader.close();
            return qr;
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        return 0;
    }

    /**
     * Timer starts here.
     */
    public static void timeStart() {
        if ((timeStart != 0L) || (timeStart > 300000L)) {
            deltaTime = (System.currentTimeMillis() - timeStart);
            timeStart = System.currentTimeMillis();
            System.out.println(deltaTime + " ms");
        } else {
            timeStart = System.currentTimeMillis();
        }

    }

    /**
     * Method to get array of INPUT-like chain (PADDR.PX1-6).
     *
     * @return
     */
    public static NodeInst[] getStartingNodeInsts() {
        Cell curcell = Job.getUserInterface().getCurrentCell();
        Iterator<NodeInst> itr = curcell.getNodes();
        ArrayList<NodeInst> inputList = new ArrayList<>();
        while (itr.hasNext()) {
            NodeInst ni = itr.next();
            if (ni.toString().contains("INPUT")) {
                inputList.add(ni);
            }
        }
        NodeInst[] nia = new NodeInst[0];
        nia = inputList.toArray(nia);
        return nia;
    }
}
