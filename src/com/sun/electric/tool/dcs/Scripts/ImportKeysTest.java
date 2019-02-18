/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sun.electric.tool.dcs.Scripts;

import com.sun.electric.database.EditingPreferences;
import com.sun.electric.database.geometry.btree.unboxed.Pair;
import com.sun.electric.database.hierarchy.Cell;
import com.sun.electric.database.topology.ArcInst;
import com.sun.electric.database.topology.NodeInst;
import com.sun.electric.database.topology.PortInst;
import com.sun.electric.database.variable.Variable;
import com.sun.electric.technology.ArcProto;
import com.sun.electric.technology.technologies.Generic;
import com.sun.electric.tool.Job;
import com.sun.electric.tool.JobException;
import com.sun.electric.tool.dcs.Accessory;
import com.sun.electric.tool.dcs.Data.MemoryMap;
import com.sun.electric.tool.dcs.Data.ProjectConfiguration;
import com.sun.electric.tool.dcs.InputFactory;
import com.sun.electric.tool.user.User;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import javax.swing.JFileChooser;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;

/**
 *
 * @author Astepanov
 */
public class ImportKeysTest {


    private static ImportKeysTest ImportKeysTest;
    private static final LinkedList<String> allLineConfig = new LinkedList<>();

    public static ImportKeysTest getImportKeysTest() {
        if (ImportKeysTest == null) {
            ImportKeysTest = new ImportKeysTest();
        }
        return ImportKeysTest;
    }

    /**
     * Method to get path to configuration file with JFileChooser.
     *
     * @return
     */
    private String getPathFromUser() {
        JFileChooser chooser = new JFileChooser();
        File Dir = new File("../");
        String pathToFile;
        chooser.setCurrentDirectory(Dir);
        //FileNameExtensionFilter filter = new FileNameExtensionFilter("txt");
        //chooser.setFileFilter(filter);
        chooser.setDialogTitle("Import Keys");
        chooser.setAcceptAllFileFilterUsed(false);

        if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
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

    /**
     * Main function to import.
     */
    public void importFunction() {
        String path = getPathFromUser();
        if (path != null) {
            readFileConfigAndGeneratesLinkedList(path);
            formAGlobalHashMapInGAdrParameters();
        }
    }

    /**
     * This method reads the configuration sequence import file and generates
     * LinkedList.
     */
    private LinkedList<String> readFileConfigAndGeneratesLinkedList(String path) {

        try {
            BufferedReader reader = InputFactory.bufferedReader(new File(path));
            String configLine;
            while ((configLine = reader.readLine()) != null) {
                allLineConfig.add(configLine);
            }
        } catch (IOException e) {
            //FIX: обработка ошибки.
            e.printStackTrace();
        }
        return allLineConfig;
    }

    /**
     * This method checks the availability of import keys from a file in this
     * block. Generates an array with a list of all keys used. Removes used keys
     * from the list of configuration keys
     */
    private static ArrayList<String> GeneratesAnArrayWithListOfAllKeysUsed(String parameter) {
        MemoryMap mm = MemoryMap.getInstance();
        ArrayList<String> keyArrayConfig = new ArrayList<>();
        Iterator<String> iteratorKeyConfigImport = allLineConfig.descendingIterator();
        while (iteratorKeyConfigImport.hasNext()) {
            String keyImport = iteratorKeyConfigImport.next();
            String keyInBlock = mm.getInternalAddressImport(parameter, keyImport);
            if (keyInBlock != null) {
                keyArrayConfig.add(keyInBlock);
                iteratorKeyConfigImport.remove();
            }
            if (allLineConfig.isEmpty()) {
                break;
            }
        }

        return keyArrayConfig;
    }

    /**
     * Create a hashMap for blocks with gAdr parameters.
     */
    private static void formAGlobalHashMapInGAdrParameters() {
        Cell curcell = Job.getUserInterface().getCurrentCell();
        if (curcell == null) {
            //FIX: давай выкинем HardFunctionalException.
            Accessory.showMessage("No schema selected.");
            return;
        }
        Iterator<NodeInst> iteratorNodeInst = curcell.getNodes();
        while (iteratorNodeInst.hasNext()) {
            NodeInst nodeInstBlock = iteratorNodeInst.next();
            Iterator<Variable> iteratorParameters = nodeInstBlock.getParameters();
            while (iteratorParameters.hasNext()) {
                Variable parameterNode = iteratorParameters.next();
                ArrayList<String> arrayOfKeysUsedInTheBlock = new ArrayList<>();
                if (parameterNode.toString().contains("gAdr")) {
                    String parameterKey = parameterNode.getObject().toString();
                    //22h||01h@R1
                    parameterKey = parameterKey.substring(0, parameterKey.lastIndexOf("h"));
                    //22||01
                    arrayOfKeysUsedInTheBlock = GeneratesAnArrayWithListOfAllKeysUsed(parameterKey);
                } else if (parameterNode.toString().contains("uniq")) {
                    String parameterKey = parameterNode.getObject().toString();
                    arrayOfKeysUsedInTheBlock = GeneratesAnArrayWithListOfAllKeysUsed(parameterKey);
                }
                if (arrayOfKeysUsedInTheBlock != null) {
                    keyСlosureInBlock(arrayOfKeysUsedInTheBlock, nodeInstBlock);
                }
            }
        }
    }

    /**
     * This method closes all found keys in this NodeInst.
     */
    private static void keyСlosureInBlock(ArrayList<String> arrayOfKeysUsedInTheBlock, NodeInst nodeInstBlock) {
        HashSet<com.sun.electric.tool.dcs.SpecificStructures.Pair<PortInst, PortInst>> test = new HashSet<>();
        for (String keyBlock : arrayOfKeysUsedInTheBlock) {
            PortInst firstPort = null;
            PortInst secondPort = null;
            Iterator<PortInst> itr = nodeInstBlock.getPortInsts();
            while (itr.hasNext()) {
                PortInst port = itr.next();
                String nameKey = port.toString();
                if ((nameKey.contains(ProjectConfiguration.getPinPatternHead()
                        + keyBlock + ProjectConfiguration.getPinPatternTailFirst()))) {
                    firstPort = port;
                } else if ((nameKey.contains(ProjectConfiguration.getPinPatternHead()
                        + keyBlock + ProjectConfiguration.getPinPatternTailSecond()))) {
                    secondPort = port;
                }
                if ((firstPort != null) && (secondPort != null)) {
                    test.add(new com.sun.electric.tool.dcs.SpecificStructures.Pair<>(firstPort, secondPort));
                    double size = 0.5;
                    ArcProto arc = Generic.tech().universal_arc;
                    new CreateNewArc(arc, firstPort, secondPort, size);
                    break;
                }
            }
        }
        /*double size = 0.5;
        ArcProto arc = Generic.tech().universal_arc;
        new CreateLotsOfNewArcs(arc, test, size);*/
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

    /**
     * Class for "CreateNewArc", class realises createNewArc Job to avoid
     * "database changes are forbidden" error.
     */
    private static class CreateLotsOfNewArcs extends Job {

        private ArcProto ap;
        private double size;
        private final HashSet<com.sun.electric.tool.dcs.SpecificStructures.Pair<PortInst, PortInst>> setOfPortPairs;

        public CreateLotsOfNewArcs(ArcProto arc, HashSet<com.sun.electric.tool.dcs.SpecificStructures.Pair<PortInst, PortInst>> setOfPortPairs, double size) {
            super("Create New Arc", User.getUserTool(), Job.Type.CHANGE, null, null, Job.Priority.USER);
            this.ap = arc;
            this.setOfPortPairs = setOfPortPairs;
            this.size = size;
            startJob();
        }

        @Override
        public boolean doIt() throws JobException {
            EditingPreferences ep = EditingPreferences.getInstance();
            setOfPortPairs.stream().map((portsPair) -> {
                PortInst firstPort = portsPair.getFirstObject();
                PortInst secondPort = portsPair.getSecondObject();
                ArcInst newArc = ArcInst.makeInstance(ap, ep, firstPort, secondPort);
                return newArc;
            }).forEachOrdered((newArc) -> {
                newArc.setLambdaBaseWidth(size);
            });
            return true;
        }
    }

}
