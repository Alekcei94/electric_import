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

import com.sun.electric.tool.dcs.Data.LinksHolder;
import com.sun.electric.tool.user.dialogs.ExecDialog;
import com.sun.electric.tool.user.ui.TopLevel;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import javax.swing.JOptionPane;

/**
 * Class is needed to have constant access to utility methods like writing to
 * text file or starting/checking 1-touch timer.
 *
 */
public class Accessory {

    /**
     * private constructor prohibits creating objects of this class.
     */
    private Accessory() {
        throw new AssertionError();
    }

    public static void writeToLog(String text) {
        write("C:/dcsEle/log.txt", text);
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
            System.out.println("IOException: " + ioe.getMessage());
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
        //TODO: should print only with option.
        System.out.println(s);
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
     * Just iteratively count all strings.
     *
     * @param file
     * @return
     */
    public static int getStringCount(File file) throws IOException {
        int counter = 0;
        try(BufferedReader br = InputFactory.bufferedReader(file)) {
            String line;
            while(br.readLine() != null) {
                counter++;
            }
            return counter;
        }
    }

    private static long timeStart;

    /**
     * Timer starts here.
     */
    public static long timeStart() {
        if ((timeStart != 0L) || (timeStart > 300000L)) {
            long deltaTime = (System.currentTimeMillis() - timeStart);
            timeStart = System.currentTimeMillis();
            return deltaTime;
        } else {
            timeStart = System.currentTimeMillis();
            return 0;
        }
    }
    
    /**
     * Method executes application with absolute path as path.
     * @param path
     * @param extension 
     */
    public static void executeApplication(String path, String extension) {
        ExecDialog dialog = new ExecDialog(TopLevel.getCurrentJFrame(), false);
        File dir = new File(LinksHolder.getXCADPath());
        String command = path + extension;
        dialog.startProcess(command, null, dir);
    }
}
