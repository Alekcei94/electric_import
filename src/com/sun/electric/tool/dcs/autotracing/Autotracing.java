/* Electric(tm) VLSI Design System
 *
 * File: Autotracing.java
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

import com.sun.electric.tool.Job;
import com.sun.electric.tool.JobException;
import com.sun.electric.tool.Tool;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.*;

/**
 * This class is used as a controller for autotracing system All methods here is
 * just links for other classes' methods (MV model)
 */
public class Autotracing extends Tool {

    /**
     * the Autotracing tool.
     */
    private static final Autotracing tool = new Autotracing();

    /**
     * The constructor sets up the Autotracing tool.
     */
    private Autotracing() {
        super("autotracing");
    }

    /**
     * Method to initialize the Autotracing tool.
     */
    @Override
    public void init() {
    }

    /**
     * Method to retrieve the singleton associated with the Autotracing tool,
     *
     * @return the Autotracing tool.
     */
    public static Autotracing getAutotracingTool() {
        return tool;
    }


    /**
     * This class starts the autotracing proccess as Job;
     */
    private static class MakeTrace extends Job {

        private JFrame frame;

        protected MakeTrace() {
            super("Make Trace", Autotracing.getAutotracingTool(), Job.Type.CHANGE, null, null, Job.Priority.USER);
            startJob();
        }

        @Override
        public boolean doIt() throws JobException {
            createAndShowGUI(true);
            //SimpleAutotracing.getSimpleAutotracing().startTrace();
            createAndShowGUI(false);
            return true;
        }

        @Override
        public void terminateOK() {

        }

        /**
         * Method the progress bar appear and disappear,
         *
         * @Param start = true to show and false to drop progress bar.
         */
        public void createAndShowGUI(boolean start) {
            if (start) {
                frame = new JFrame("Progress");
                frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
                frame.addWindowListener(new WindowAdapter() {
                    @Override
                    public void windowClosing(WindowEvent e) {
                        e.getWindow().dispose();
                        //SimpleAutotracing.getSimpleAutotracing().setExitPressed();
                        Accessory.showMessage("Autotracing will be stopped after 1 step.");
                    }
                });

                JLabel label = new JLabel("Please wait...");
                frame.getContentPane().add(label);

                JProgressBar progressBar = new JProgressBar();
                progressBar.setIndeterminate(true);

                frame.getContentPane().add(progressBar);

                frame.pack();
                frame.setVisible(true);
                frame.setResizable(false);
            } else {
                frame.dispose();
            }

        }
    }

}
