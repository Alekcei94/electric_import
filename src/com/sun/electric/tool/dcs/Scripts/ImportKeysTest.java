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
import com.sun.electric.tool.dcs.Data.LinksHolder;
import com.sun.electric.tool.dcs.InputFactory;
import com.sun.electric.tool.user.User;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.swing.JFileChooser;

/**
 *
 * @author Astepanov
 */
public class ImportKeysTest {
 
    //REV: в аннотации используются **, а не *.

    //FIX: нужна ли такая область видимости?
    //FIX: должны ли они быть static?
    //REV: я бы всё-таки переименовал карты... Что-нибудь вроде uniqAddressMap/gAdrAddressMap.
    public static HashMap<String, Pair<String, String>> graphInAddressUniqParameters = new HashMap<>();
    public static HashMap<String, Pair<String, String>> graphInAddressgAdrParameters = new HashMap<>();
    private static ImportKeysTest ImportKeysTest;

    public static ImportKeysTest getImportKeysTest() {
        if (ImportKeysTest == null) {
            ImportKeysTest = new ImportKeysTest();
            //FIX: это должно формироваться в dcs/Data/MemoryMap
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
            //FIX: у нас нет логгера, нужна обработка ошибки
            Logger.getLogger(ImportKeysTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * HashMap<real key and address, Pair<name block ,address in block>>
     * (540012, Pair<MUX4in1_435, 000>)
     */
    private static void formAGlobalHashMapInUniqParameters(List<java.nio.file.Path> collect) {
        //FIX: второй раз проходить по всем файлам гораздо менее эффективно, чем
        //работать с уже находящейся в памяти структуре, формирование карт должно
        //происходить в Data/MemoryMap.java в момент прохода.
        
        //REV: было бы понятнее с циклом forEach.
        for (int i = 0; i < collect.size(); i++) {
            try {
                String address = collect.get(i).toString();
                //REV: создание bufferedReader одной строкой из InputFactory.
                FileInputStream fstream = new FileInputStream(address);
                BufferedReader br = new BufferedReader(new InputStreamReader(fstream));
                String strLine;
                String name = (new File(address)).getName();
                //REV: закоментированные куски кода, которые уже не пригодятся желательно удалять.
                //address = address.replace('\\', '/');
                //String[] name = address.split("/");
                String nameBlock = name.substring(0, name.lastIndexOf("."));
                //REV: strLine должен создаваться здесь, а не двумя строчками раньше,
                //связанный код должен быть локализован в одном месте.
                while ((strLine = br.readLine()) != null) {
                    String[] keys = strLine.split(" ");
                    //REV: думаю, что можно было бы записать одной строкой...
                    Pair<String, String> nameBlockAndKey = new Pair<>(nameBlock, keys[1]);
                    graphInAddressUniqParameters.put(keys[0], nameBlockAndKey);
                }
            } catch (IOException ex) {
                //FIX: обработка ошибки.
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

    //REV: Здесь стоит добавить аннотацию. В идеале, все публичные методы, кроме
    //геттеров и сеттеров должны содержать аннотации.
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
            //REV: я бы заменил здесь line на что-нибудь вроде key/configLine/...
            String line = reader.readLine();
            String key;
            String blockName;
            //REV: нестандартная запись прохода по всем строкам файла?
            //REV: то же самое, String line должен быть здесь.
            while (line != null) {
                if (graphInAddressgAdrParameters.get(line) != null) {
                    //REV: можно вытащить метод из этих трёх строк.
                    key = graphInAddressgAdrParameters.get(line).getValue();
                    blockName = graphInAddressgAdrParameters.get(line).getKey();
                    searchBlockAndPastKey(blockName, key);
                } else if (graphInAddressUniqParameters.get(line) != null) {
                    key = graphInAddressUniqParameters.get(line).getValue();
                    blockName = graphInAddressUniqParameters.get(line).getKey();
                    searchBlockAndPastKey(blockName, key);
                } else {
                    /*FIX: думаю, что здесь стоит выкинуть unchecked exception
                    т.к. если в импорте появятся ещё какие-то действия, они 
                    продолжатся даже в случае некорректной карты, чего нужно избегать.*/
                    Accessory.showMessage("Could not find key " + line + " .");
                    return;
                }
                line = reader.readLine();
            }
        } catch (IOException e) {
            //FIX: обработка ошибки.
            e.printStackTrace();
        }
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
        //REV: iteratot -> iterator.
        //FIX: требуется проверка типа, а не приведение. Iterator -> Iterator<NodeInst>.
        //REV: iteratotNodeInstList это же не list.
        Iterator iteratotNodeInstList = curcell.getNodes();
        while (iteratotNodeInstList.hasNext()) {
            NodeInst nodeInstBlock = (NodeInst) iteratotNodeInstList.next();
            //FIX: то же самое.
            //REV: это же не nodeIterator.
            Iterator nodeIterator = nodeInstBlock.getParameters();
            while (nodeIterator.hasNext()) {
                Variable parameterNode = (Variable) nodeIterator.next();
                /*REV: сейчас не обязательно, но в целом, такие вещи должны быть с equals.
                REV: также не обязательно, но лучше использовать что-нибудь вроде
                getName() вместо toString()*/
                if (parameterNode.toString().contains("gAdr")) {
                    String parameterKey = parameterNode.getObject().toString();
                    Iterator<PortInst> itr = nodeInstBlock.getPortInsts();
                    while (itr.hasNext()) {
                        String nameKey = itr.next().toString();
                        //FIX: hardcode mAd... _1, h (по этому пункту расскажу отдельно)
                        if ((nameKey.contains("mAd")) && ((nameKey.contains("_1")))) {
                            //Q: а зачем это вообще?
                            String nameBlock = nodeInstBlock.getName().replace("@", "_");
                            String key = nameKey.substring(nameKey.lastIndexOf("mAd") + 3, nameKey.lastIndexOf("_"));
                            //FIX: заменять, только если заканчивается на h.
                            String addressKey = parameterKey.replace("h", "") + key;
                            graphInAddressgAdrParameters.put(addressKey, new Pair<>(nameBlock, key));
                        }
                    }
                }
            }
        }
    }

    /**
     * param - Uniq/gAdr; blockName - AOP_12; key - 001;
     */
    private void searchBlockAndPastKey(String nameBlockInOption, String key) {
        //REV: nameBlockInOption имя блока в опции?
        Cell curcell = Job.getUserInterface().getCurrentCell();
        if (curcell == null) {
            //FIX: тот же момент, выдать ошибку.
            return;
        }
        //REV: iteratot -> iterator.
        //FIX: требуется проверка типа, а не приведение. Iterator -> Iterator<NodeInst>.
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
                    //FIX: одиночное создание арков будет очень медленно работать
                    //для большого количества ключей, требуется замена на CreateLotsOfNewArcs.
                    new CreateNewArc(arc, firstPort, secondPort, size);
                } else {
                    //FIX: то же самое, не следует что-то делать, если ключ не найден
                    //конфиг уже некорректный.
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
