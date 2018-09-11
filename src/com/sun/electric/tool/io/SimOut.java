/* -*- tab-width: 4 -*-
 *
 * Electric(tm) VLSI Design System
 *
 * File: SimOut.java
 *
 * Copyright (c) 2003, Static Free Software. All rights reserved.
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
package com.sun.electric.tool.io;

import com.sun.electric.database.geometry.PolyBase;
import com.sun.electric.database.hierarchy.Cell;
import com.sun.electric.database.variable.VarContext;
import com.sun.electric.tool.Job;
import com.sun.electric.tool.JobException;
import com.sun.electric.tool.dcs.Accessory;
import com.sun.electric.tool.io.output.Output;
import com.sun.electric.tool.simulation.SimulationTool;
import com.sun.electric.tool.user.User;
import com.sun.electric.tool.user.dialogs.OpenFile;
import com.sun.electric.tool.user.ui.EditWindow;
import com.sun.electric.tool.user.ui.WindowContent;
import com.sun.electric.tool.user.ui.WindowFrame;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;

/**
 *
 * @author diivanov
 */
public class SimOut {

    public static void executeNgSpice(String pathInput, String pathOutput) {
        String path = "../Spice64/bin/ngspice.exe";
        ProcessBuilder pb = new ProcessBuilder(path,
                "-b",
                pathInput,
                "-r",
                pathOutput);
        System.out.println(path+
                " -b "+
                pathInput+
                " -r "+
                pathOutput);
        String s = Paths.get(".").toAbsolutePath().normalize().toString();
        pb.redirectOutput(ProcessBuilder.Redirect.INHERIT);
        System.out.println(s);
        File file = new File(s);
        pb.directory(file);
        pb.inheritIO();
        try {
            Process p = pb.start();
            System.out.println("start");
            executeLTSpice(p, s, pathOutput);
            System.out.println("finish");
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
    
    private static void executeLTSpice(Process p, String place, String pathOutput) throws InterruptedException {
        p.waitFor();
        if(p.exitValue() != 0) {
            Accessory.showMessage("NgSpice terminated incorrectly.");
            return;
        }
        String path = "../lt/scad3.exe";
        ProcessBuilder pb = new ProcessBuilder(path,
                pathOutput);
        System.out.println(path + " " +  
                pathOutput);
        pb.redirectOutput(ProcessBuilder.Redirect.INHERIT);
        File file = new File(place);
        pb.directory(file);
        pb.inheritIO();
        try {
            Process p2 = pb.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * This method implements the export cell command for different export
     * types. It is interactive, and pops up a dialog box.
     */
    public static void exportCommand(FileType type, boolean isNetlist) {
        // synchronization of PostScript is done first because no window is needed
        if (type != FileType.SPICE) {
            assert false;
        }

        WindowFrame wf = WindowFrame.getCurrentWindowFrame(false);
        WindowContent wnd = (wf != null) ? wf.getContent() : null;

        if (wnd == null) {
            System.out.println("No current window");
            return;
        }
        Cell cell = wnd.getCell();
        if (cell == null) {
            System.out.println("No cell in this window");
            return;
        }
        VarContext context = (wnd instanceof EditWindow) ? ((EditWindow) wnd).getVarContext() : null;

        List<PolyBase> override = null;

//		String [] extensions = type.getExtensions();
        // In case of GDS, it would be better to name the ouputfile with the library name rather than cell name
        // when all cells are included in the export.
        String rootName = (type == FileType.GDS && IOTool.isGDSWritesEntireLibrary()) ? cell.getLibrary().getName() : cell.getName();
        String filePath = rootName + "." + type.getFirstExtension();

        // special case for spice
        if (type == FileType.SPICE
                && !SimulationTool.getSpiceRunChoice().equals(SimulationTool.spiceRunChoiceDontRun)) {
            // check if user specified working dir
            if (SimulationTool.getSpiceUseRunDir()) {
                filePath = SimulationTool.getSpiceRunDir() + File.separator + filePath;
            } else {
                filePath = User.getWorkingDirectory() + File.separator + filePath;
            }
            // check for automatic overwrite
            if (User.isShowFileSelectionForNetlists() && !SimulationTool.getSpiceOutputOverwrite()) {
                String saveDir = User.getWorkingDirectory();
                filePath = OpenFile.chooseOutputFile(type, null, filePath);
                User.setWorkingDirectory(saveDir);
                if (filePath == null) {
                    return;
                }
            }

            Output.exportCellCommand(cell, context, filePath, type, override);
            //new SimulateWithPath(filePath, filePath);
            return;
        }

        filePath = User.getWorkingDirectory() + File.separator + filePath;
        System.out.println(filePath + " simulation");
        Output.exportCellCommand(cell, context, filePath, type, override);
        new SimulateWithPath(filePath);
    }

    /**
     * Class to write a library in a CHANGE Job. Used by regressions that need
     * to queue the output after existing change jobs.
     */
    public static class SimulateWithPath extends Job {

        private String inputPath;
        private String outputPath;

        public SimulateWithPath(String inputPath) {
            super("Simulation", User.getUserTool(), Job.Type.CHANGE, null, null, Job.Priority.USER);
            this.inputPath = inputPath;
            this.outputPath = getRaw(inputPath);
            startJob();
        }

        public boolean doIt() throws JobException {
            executeNgSpice(inputPath, outputPath);
            return true;
        }
        
        private String getRaw(String path) {
            String[] split = path.split("\\.");
            split[split.length-1] = "raw";
            String out = "";
            for(String spl : split) {
                out = out.concat(spl);
                if(!spl.equals("raw")) {
                    out = out.concat(".");
                }
            }
            return out;
        }

        public void terminateOK() {

        }
    }
}
