/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sun.electric.tool.dcs.Scripts;

import com.sun.electric.database.EditingPreferences;
import com.sun.electric.database.geometry.btree.unboxed.Pair;
import java.util.Iterator;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.swing.JFileChooser;
import com.sun.electric.database.topology.NodeInst;
import com.sun.electric.database.topology.PortInst;
import com.sun.electric.database.hierarchy.Cell;
import com.sun.electric.database.topology.ArcInst;
import com.sun.electric.tool.Job;
import com.sun.electric.database.variable.Variable;
import com.sun.electric.technology.ArcProto;
import com.sun.electric.technology.technologies.Generic;
import com.sun.electric.tool.JobException;
import com.sun.electric.tool.dcs.Accessory;
import com.sun.electric.tool.dcs.Data.LinksHolder;
import com.sun.electric.tool.dcs.InputFactory;
import com.sun.electric.tool.user.User;
import java.io.FileReader;

/**
 *
 * @author Astepanov
 */
public class ImportKeysTest {

    public static HashMap<String, Pair<String, String>> graphInAddressUniqParameters = new HashMap<>();
    public static HashMap<String, Pair<String, String>> graphInAddressgAdrParameters = new HashMap<>();
    private static ImportKeysTest ImportKeysTest;

    public static ImportKeysTest getImportKeysTest() {
        if (ImportKeysTest == null) {
            ImportKeysTest = new ImportKeysTest();
            formAListInImport();
            formAGlobalHashMapInGAdrParameters();
        }
        return ImportKeysTest;
    }

    /**
     * Get the addresses of all files in this directory.
     */
    private static void formAListInImport() {
        try {
            List<java.nio.file.Path> collect = Files.walk(Paths.get(LinksHolder.getPathInDirectoryMAP()))
                    .filter(Files::isRegularFile)
                    .collect(Collectors.toList());
            formAGlobalHashMapInUniqParameters(collect);
        } catch (IOException ex) {
            Logger.getLogger(ImportKeysTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * HashMap<real key and address, Pair<name block ,address in block>>
     * (540012, Pair<MUX4in1_435, 000>)
     */
    private static void formAGlobalHashMapInUniqParameters(List<java.nio.file.Path> collect) {
        for (int i = 0; i < collect.size(); i++) {
            try {
                String address = collect.get(i).toString();
                FileInputStream fstream = new FileInputStream(address);
                BufferedReader br = new BufferedReader(new InputStreamReader(fstream));
                String strLine;
                String name = (new File(address)).getName();
                //address = address.replace('\\', '/');
                //String[] name = address.split("/");
                String nameBlock = name.substring(0, name.lastIndexOf("."));
                while ((strLine = br.readLine()) != null) {
                    String[] keys = strLine.split(" ");
                    Pair<String, String> nameBlockAndKey = new Pair<>(nameBlock, keys[1]);
                    graphInAddressUniqParameters.put(keys[0], nameBlockAndKey);
                }
            } catch (IOException ex) {
                System.out.println(ex.getMessage());
            }
        }
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
            return null;
        }
        return pathToFile;
    }

    public void importFunction() {
        String path = getPathFromUser();
        if (path != null) {
            openFileConfigAndSearchAddressKeys(path);
        }

    }

    /**
     * Open the file on the selected path, read all keys line by line. Find them
     * in one of the Pair, if not, then print a message about the absence of a
     * key to the console.
     */
    private void openFileConfigAndSearchAddressKeys(String path) {
        try {
            BufferedReader reader = InputFactory.bufferedReader(new File(path));
            String line = reader.readLine();
            String key;
            String nameBlock;
            while (line != null) {
                if (graphInAddressgAdrParameters.get(line) != null) {
                    key = graphInAddressgAdrParameters.get(line).getValue();
                    nameBlock = graphInAddressgAdrParameters.get(line).getKey();
                    searchBlockAndPastKey(nameBlock, key);
                } else if (graphInAddressUniqParameters.get(line) != null) {
                    key = graphInAddressUniqParameters.get(line).getValue();
                    nameBlock = graphInAddressUniqParameters.get(line).getKey();
                    searchBlockAndPastKey(nameBlock, key);
                } else {
                    Accessory.showMessage("Could not find key " + line + " .");
                    return;
                }
                line = reader.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Create a hashMap for blocks with gAdr parameters.
     */
    private static void formAGlobalHashMapInGAdrParameters() {
        Cell curcell = Job.getUserInterface().getCurrentCell();
        if (curcell == null) {
            Accessory.showMessage("No schema selected.");
            return;
        }
        Iterator iteratotNodeInstList = curcell.getNodes();
        while (iteratotNodeInstList.hasNext()) {
            NodeInst nodeInstBlock = (NodeInst) iteratotNodeInstList.next();
            Iterator nodeIterator = nodeInstBlock.getParameters();
            while (nodeIterator.hasNext()) {
                Variable parameterNode = (Variable) nodeIterator.next();
                if (parameterNode.toString().contains("gAdr")) {
                    String parameterKey = parameterNode.getObject().toString();
                    Iterator<PortInst> itr = nodeInstBlock.getPortInsts();
                    while (itr.hasNext()) {
                        String nameKey = itr.next().toString();
                        if ((nameKey.contains("mAd")) && ((nameKey.contains("_1")))) {
                            String nameBlock = nodeInstBlock.getName().replace("@", "_");
                            String key = nameKey.substring(nameKey.lastIndexOf("mAd") + 3, nameKey.lastIndexOf("_"));
                            String addressKey = parameterKey.replace("h", "") + key;
                            graphInAddressgAdrParameters.put(addressKey, new Pair<>(nameBlock, key));
                        }
                    }
                }
            }
        }
    }

    /**
     * param - Uniq/gAdr; nameBlock - AOP_12; key - 001;
     */
    private void searchBlockAndPastKey(String nameBlockInOption, String key) {
        Cell curcell = Job.getUserInterface().getCurrentCell();
        if (curcell == null) {
            return;
        }
        Iterator iteratotNodeInstList = curcell.getNodes();
        while (iteratotNodeInstList.hasNext()) {
            NodeInst nodeInstBlock = (NodeInst) iteratotNodeInstList.next();
            String nameRealBlock = nodeInstBlock.getName().replaceAll("@", "_");
            if (nameBlockInOption.contains(nameRealBlock)) {
                PortInst firstPort = null;
                PortInst secondPort = null;
                Iterator<PortInst> itr = nodeInstBlock.getPortInsts();
                while (itr.hasNext()) {
                    PortInst port = itr.next();
                    String nameKey = port.toString();
                    if ((firstPort != null) && (secondPort != null)) {
                        break;
                    }
                    if (nameKey.contains("mAd" + key + "_1")) {
                        firstPort = port;
                    } else if (nameKey.contains("mAd" + key + "_2")) {
                        secondPort = port;
                    }
                }
                if ((firstPort != null) && (secondPort != null)) {
                    double size = 0.5;
                    ArcProto arc = Generic.tech().universal_arc;
                    new CreateNewArc(arc, firstPort, secondPort, size);
                } else {
                    Accessory.showMessage("Could not find key. ");
                }
            }
        }
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
