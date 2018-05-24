/* Electric(tm) VLSI Design System
 *
 * File: ImportKeys.java
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
package com.sun.electric.tool.dcs.Scripts;

import com.sun.electric.database.topology.PortInst;
import com.sun.electric.technology.ArcProto;
import com.sun.electric.database.EditingPreferences;
import com.sun.electric.database.hierarchy.Cell;
import com.sun.electric.database.topology.ArcInst;

import com.sun.electric.tool.Job;
import com.sun.electric.tool.JobException;
import com.sun.electric.tool.user.User;
import java.io.File;
import java.io.IOException;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 * Class uses ImportKeys script algorithm to import keys to scheme from file.
 */
public class ImportKeys {

    private static ImportKeys importKeys;

    public static ImportKeys getInstance() {
        if (importKeys == null) {
            importKeys = new ImportKeys();
        }
        return importKeys;
    }

    private String getPathFromUser() {
        JFileChooser chooser = new JFileChooser();
        File Dir = new File("c:\\CYGELENG\\config");
        String pathToFile;
        chooser.setCurrentDirectory(Dir);
        FileNameExtensionFilter filter = new FileNameExtensionFilter("txt");
        chooser.setFileFilter(filter);
        chooser.setDialogTitle("Import Keys");
        //chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        chooser.setAcceptAllFileFilterUsed(false);

        if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            System.out.println("CurrentDirectory: " + chooser.getCurrentDirectory());
            System.out.println("SelectedFile: " + chooser.getSelectedFile());
            pathToFile = chooser.getSelectedFile().getAbsolutePath();
            if (!pathToFile.contains(".txt")) {
                System.out.println("Wrong File ");
                System.out.println(pathToFile);
                return null;
            }
        } else {
            System.out.println("No Selection ");
            return null;
        }
        return pathToFile;
    }

    public void ImportToCell(Cell cell) throws IOException {
        String pathToFile = getPathFromUser();
        if(pathToFile == null) {
            return;
        }
        
        System.out.println("Script started");
        

    }

    /**
     * Class for "CreateNewArc", class realises createNewArc Job to avoid
     * "database changes are forbidden" error.
     */
    private static class CreateNewArc extends Job {

        ArcProto ap;
        double size;
        PortInst firstPort;
        PortInst secondPort;

        public CreateNewArc(ArcProto arc, PortInst firstPort, PortInst secondPort, double size) {
            super("Create New Arc", User.getUserTool(), Job.Type.CHANGE, null, null, Job.Priority.USER);
            this.ap = arc;
            this.firstPort = firstPort;
            this.secondPort = secondPort;
            this.size = size;
            startJob();
        }

        @Override
        public boolean doIt() throws JobException {
            EditingPreferences ep = EditingPreferences.getInstance();
            ArcInst newArc = ArcInst.makeInstance(ap, ep, firstPort, secondPort);
            newArc.setLambdaBaseWidth(size);
            return true;
        }
    }
}
