/* Electric(tm) VLSI Design System
 *
 * File: FilterDesignWindowUIFrame.java
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
package com.sun.electric.tool.dcs.Design;

import com.sun.electric.tool.Job;
import com.sun.electric.tool.JobException;
import com.sun.electric.tool.dcs.Accessory;
import com.sun.electric.tool.dcs.Data.LinksHolder;
import com.sun.electric.tool.user.User;
import com.sun.electric.tool.user.dialogs.EModelessDialog;
import com.sun.electric.tool.user.ui.TopLevel;
import java.awt.CardLayout;
import java.awt.Frame;
import java.awt.Image;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

/**
 *
 * @author diivanov
 */
public class FilterDesignWindowUIFrame extends EModelessDialog {

    private static FilterDesignWindowUIFrame theDialog;
    private final String[] nameListFilters = new String[3];
    private final List<BuildFilters> objectConfigFiltersAutosave = new ArrayList<>();

    /**
     * Creates new form FilterDesignWindowUIFrame
     */
    public FilterDesignWindowUIFrame(Frame parent) {
        super(parent);
        initComponents();
        initMyComponents();
    }

    public static FilterDesignWindowUIFrame getFilterDesignWindowUIFrame() {
        return theDialog;
    }

    /**
     * This method return status enable.
     */
    public boolean getEnableStatus() {
        return enable.isSelected();
    }

    /**
     * This method read config Filters in file in address ./filterDesing.txt .
     */
    private List<String> readFileConfigFilters() {
        List<String> configFilterInFile = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(LinksHolder.getFilterConfig()))) {
            String line;
            while ((line = br.readLine()) != null) {
                configFilterInFile.add(line);
            }
            return configFilterInFile;
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
        return configFilterInFile;
    }

    /**
     * This method read config Filters in file in address ./filterDesing.txt .
     */
    private void writeFileConfigFilters(List<String> configFilterInFile) {
        try (FileWriter writer = new FileWriter(LinksHolder.getFilterConfig(), false)) {
            for (String config : configFilterInFile) {
                writer.write(config);
            }
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }

    /**
     * This method creates a form for entering an address and a name for Import
     * the configuration.
     */
    private String useImportFile() {
        JFileChooser chooser = new JFileChooser();
        File Dir = new File(LinksHolder.getPathFiltetrs());
        String pathToFile;
        chooser.setCurrentDirectory(Dir);
        chooser.setDialogTitle("Import config");
        chooser.setAcceptAllFileFilterUsed(false);
        if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            pathToFile = chooser.getSelectedFile().getAbsolutePath();
            if (!pathToFile.contains(".fnt")) {
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
     * The method forms a name for the history.
     */
    private String formNameFileHistory(Map<String, String> infoMap) {
        String name = "";
        String[] list = infoMap.toString().split(",");
        for (int i = 0; i < list.length; i++) {
            String[] say = list[i].split("=");
            if (i == 0) {
                name += say[1].replace("}", "") + " [";
            } else if (i == list.length - 1) {
                name += say[1].replace("}", "") + "] ";
            } else {
                name += say[1].replace("}", "") + ", ";
            }
        }
        return name;
    }

    /**
     * This method checks for the presence of a file with the same name. return
     * fase - save file return truen - not save
     */

    private boolean serchAddressName(String name) throws IOException {
        File dir = new File(LinksHolder.getPathFiltetrs());
        String[] thisName;
        name = name.replace("\\", "/");
        thisName = name.split("/");
        File[] arrFiles = dir.listFiles();
        List<File> lst = Arrays.asList(arrFiles);
        String[] address;
        boolean flag = false;
        for (File i : lst) {
            String nameAddress = i.toString().replace('\\', '/');
            address = nameAddress.split("/");
            if (address[3].equals(thisName[thisName.length - 1])) {
                if (JOptionPane.showConfirmDialog(null, "Such file exists.") != 0) {
                    flag = true;
                }
                break;
            }
        }
        return flag;
    }

    /**
     * This method creates a form for entering an address and a name for Export
     * the configuration.
     */
    private String useExportFile() {
        JFileChooser chooser = new JFileChooser();
        File Dir = new File(LinksHolder.getPathFiltetrs());
        String pathToFile;
        chooser.setCurrentDirectory(Dir);
        chooser.setDialogTitle("Export config");
        chooser.setAcceptAllFileFilterUsed(false);

        if (chooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
            pathToFile = chooser.getSelectedFile().getAbsolutePath();
            pathToFile = pathToFile.replace(".fnt", "");
        } else {
            System.out.println("No Selection.");
            return null;
        }
        return pathToFile;
    }

    /*
     * This method form Map <Name block, parametre>.
     */
    private Map<String, String> exportConfigFiltersInFile() {
        Map<String, String> paramFiltres = new HashMap<>();
        if (ButterRadioButton.isSelected()) {
            paramFiltres.put(ButterRadioButton.getName(), String.valueOf(ButterRadioButton.isSelected()));
            paramFiltres.put(jLabel5.getText(), jTextField5.getText());
            paramFiltres.put(jLabel2.getText(), jTextField1.getText());
            paramFiltres.put(jLabel6.getText(), String.valueOf(filterTypeChoiceComboBox.getSelectedIndex()));
        } else if (ChebyRadioButton.isSelected()) {
            paramFiltres.put(ChebyRadioButton.getName(), String.valueOf(ChebyRadioButton.isSelected()));
            paramFiltres.put(jLabel8.getText(), jTextField6.getText());
            paramFiltres.put(jLabel4.getText(), jTextField2.getText());
            paramFiltres.put(jLabel10.getText(), jTextField3.getText());
            paramFiltres.put(jLabel9.getText(), String.valueOf(filterTypeChoiceComboBox1.getSelectedIndex()));
        } else if (EllipticRadioButton.isSelected()) {
            paramFiltres.put(EllipticRadioButton.getName(), nameEllipticPanel.getText());
            paramFiltres.put(orderPanel.getText(), ellipticOrderTextField.getText());
            paramFiltres.put(maximumRipplePanel.getText(), ellipticMaximumRippleTextField.getText());
            paramFiltres.put(minimumAttenuationPanel.getText(), ellipticMinimumAttenuationTextField.getText());
            paramFiltres.put(cutoffFrequencyPanel.getText(), ellipticCutoffFrequencyTextField.getText());
            paramFiltres.put(filterTypePanel.getText(), String.valueOf(ellipticFilterTypeChoiceComboBox.getSelectedIndex()));
        } else if (BesselRadioButton.isSelected()) {
            paramFiltres.put(BesselRadioButton.getName(), String.valueOf(BesselRadioButton.isSelected()));
            paramFiltres.put(jLabel19.getText(), jTextField11.getText());
            paramFiltres.put(jLabel21.getText(), jTextField12.getText());
            paramFiltres.put(jLabel20.getText(), String.valueOf(filterTypeChoiceComboBox3.getSelectedIndex()));
        }
        return paramFiltres;
    }

    /**
     * Allignment of comboBox doesn't work in netbeans' GI, We have only one
     * filter so let's start with elliptic.
     */
    private void initMyComponents() {
        ((JLabel) ellipticFilterTypeChoiceComboBox.getRenderer()).setHorizontalAlignment(JLabel.CENTER);
        EllipticRadioButton.doClick();
    }

    /**
     * Start building filter with python script, joins to build button.
     */
    private void formCmdRequestForPython() {
        if (EllipticRadioButton.isSelected()) {
            System.out.println("Filter design process started.");
            String path = "FilterScripts/FilterDesign.py"; /// ВОПРОС к Диме
            String order = ellipticOrderTextField.getText();
            String rp = ellipticMaximumRippleTextField.getText();
            String rs = ellipticMinimumAttenuationTextField.getText();
            String Wn = ellipticCutoffFrequencyTextField.getText();
            String fType = (String) ellipticFilterTypeChoiceComboBox.getSelectedItem();
            String[] cmd = {"python", path, order, rp, rs, Wn, fType};
            System.out.println(Arrays.toString(cmd));
            ProcessBuilder pb = new ProcessBuilder(cmd);
            String s = Paths.get(".").toAbsolutePath().normalize().toString();
            pb.redirectOutput(ProcessBuilder.Redirect.INHERIT);
            //System.out.println(s);
            File file = new File(s);
            pb.directory(file);
            pb.inheritIO();
            try {
                Accessory.showMessage("started");
                pb.start().waitFor(120, TimeUnit.SECONDS); // not sure about 120secs
                Accessory.showMessage("done");
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

    private void reloadImage() throws IOException {
        this.repaint();
    }

    /**
     * Method to serialize image and some text data into one file.
     */
    private void serializeFilterObject(String pathWhereSerialize, String pathToImage,
            Map<String, String> infoMap, List<String> configList) throws IOException {
        SerializableImageWithTextObject sii = new SerializableImageWithTextObject(
                pathToImage, infoMap, configList);
        ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(pathWhereSerialize));
        out.writeObject(sii);
    }

    /**
     * Method to deserialize filter object, Map<String, String> is holding
     * information of filter parameters: filterType -> Elliptic, order -> 6 ...,
     * List<String> configList consists of configuration Strings.
     *
     * @param pathWhereDeserialize
     * @return
     * @throws IOException
     * @throws ClassNotFoundException
     */
    private SerializableImageWithTextObject deserializeFilterObject(String pathFromDeserialize) throws IOException, ClassNotFoundException {
        ObjectInputStream in = new ObjectInputStream(new FileInputStream(pathFromDeserialize));
        return (SerializableImageWithTextObject) in.readObject();

    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        chooseTypeFilterGroup = new javax.swing.ButtonGroup();
        ButterRadioButton = new javax.swing.JRadioButton();
        ChebyRadioButton = new javax.swing.JRadioButton();
        EllipticRadioButton = new javax.swing.JRadioButton();
        BesselRadioButton = new javax.swing.JRadioButton();
        StartButton = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jPanelAfc = new ResizableImagePane();
        jPanelForCardLayout = new javax.swing.JPanel();
        jPanelButter = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        filterTypeChoiceComboBox = new javax.swing.JComboBox<>();
        jLabel6 = new javax.swing.JLabel();
        jTextField5 = new javax.swing.JTextField();
        jPanelElliptic = new javax.swing.JPanel();
        maximumRipplePanel = new javax.swing.JLabel();
        nameEllipticPanel = new javax.swing.JLabel();
        orderPanel = new javax.swing.JLabel();
        filterTypePanel = new javax.swing.JLabel();
        cutoffFrequencyPanel = new javax.swing.JLabel();
        minimumAttenuationPanel = new javax.swing.JLabel();
        ellipticOrderTextField = new javax.swing.JTextField();
        ellipticMaximumRippleTextField = new javax.swing.JTextField();
        ellipticMinimumAttenuationTextField = new javax.swing.JTextField();
        ellipticCutoffFrequencyTextField = new javax.swing.JTextField();
        ellipticFilterTypeChoiceComboBox = new javax.swing.JComboBox<>();
        jPanelCheby = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jTextField2 = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        filterTypeChoiceComboBox1 = new javax.swing.JComboBox<>();
        jLabel9 = new javax.swing.JLabel();
        jTextField6 = new javax.swing.JTextField();
        jTextField3 = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        jPanelBessel = new javax.swing.JPanel();
        jLabel18 = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        filterTypeChoiceComboBox3 = new javax.swing.JComboBox<>();
        jLabel20 = new javax.swing.JLabel();
        jTextField11 = new javax.swing.JTextField();
        jTextField12 = new javax.swing.JTextField();
        jLabel21 = new javax.swing.JLabel();
        importKey = new javax.swing.JButton();
        exportKey = new javax.swing.JButton();
        enable = new javax.swing.JCheckBox();
        jScrollPane1 = new javax.swing.JScrollPane();
        historyList = new javax.swing.JList<>();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setIconImages(null);
        setPreferredSize(new java.awt.Dimension(1400, 800));
        setResizable(false);

        chooseTypeFilterGroup.add(ButterRadioButton);
        ButterRadioButton.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        ButterRadioButton.setForeground(new java.awt.Color(75, 75, 75));
        ButterRadioButton.setText("Butterworth");
        ButterRadioButton.setEnabled(false);
        ButterRadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ButterRadioButtonActionPerformed(evt);
            }
        });

        chooseTypeFilterGroup.add(ChebyRadioButton);
        ChebyRadioButton.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        ChebyRadioButton.setForeground(new java.awt.Color(75, 75, 75));
        ChebyRadioButton.setText("Chebyshev");
        ChebyRadioButton.setEnabled(false);
        ChebyRadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ChebyRadioButtonActionPerformed(evt);
            }
        });

        chooseTypeFilterGroup.add(EllipticRadioButton);
        EllipticRadioButton.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        EllipticRadioButton.setForeground(new java.awt.Color(75, 75, 75));
        EllipticRadioButton.setSelected(true);
        EllipticRadioButton.setText("Elliptic");
        EllipticRadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                EllipticRadioButtonActionPerformed(evt);
            }
        });

        chooseTypeFilterGroup.add(BesselRadioButton);
        BesselRadioButton.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        BesselRadioButton.setForeground(new java.awt.Color(75, 75, 75));
        BesselRadioButton.setText("Bessel");
        BesselRadioButton.setEnabled(false);
        BesselRadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BesselRadioButtonActionPerformed(evt);
            }
        });

        StartButton.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        StartButton.setForeground(new java.awt.Color(75, 75, 75));
        StartButton.setText("Build Filter");
        StartButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                StartButtonActionPerformed(evt);
            }
        });

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(75, 75, 75));
        jLabel1.setText("CHOOSE TYPE");

        jPanelAfc.setBackground(new java.awt.Color(255, 255, 255));

        javax.swing.GroupLayout jPanelAfcLayout = new javax.swing.GroupLayout(jPanelAfc);
        jPanelAfc.setLayout(jPanelAfcLayout);
        jPanelAfcLayout.setHorizontalGroup(
            jPanelAfcLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 610, Short.MAX_VALUE)
        );
        jPanelAfcLayout.setVerticalGroup(
            jPanelAfcLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 490, Short.MAX_VALUE)
        );

        jPanelForCardLayout.setLayout(new java.awt.CardLayout());

        jPanelButter.setPreferredSize(new java.awt.Dimension(495, 636));

        jLabel2.setBackground(new java.awt.Color(255, 255, 255));
        jLabel2.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setText("Cutoff frequency");
        jLabel2.setFocusable(false);
        jLabel2.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);

        jLabel3.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel3.setText("Butterworth Filter");
        jLabel3.setFocusable(false);
        jLabel3.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);

        jTextField1.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jTextField1.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jTextField1.setText("100000");
        jTextField1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField1ActionPerformed(evt);
            }
        });

        jLabel5.setBackground(new java.awt.Color(255, 255, 255));
        jLabel5.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel5.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel5.setText("Order");
        jLabel5.setFocusable(false);
        jLabel5.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);

        filterTypeChoiceComboBox.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        filterTypeChoiceComboBox.setMaximumRowCount(4);
        filterTypeChoiceComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Lowpass", "Highpass", "Bandpass", "Bandstop" }));
        filterTypeChoiceComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                filterTypeChoiceComboBoxActionPerformed(evt);
            }
        });

        jLabel6.setBackground(new java.awt.Color(255, 255, 255));
        jLabel6.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel6.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel6.setText("Filter type");
        jLabel6.setFocusable(false);
        jLabel6.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);

        jTextField5.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jTextField5.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jTextField5.setText("2");
        jTextField5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField5ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanelButterLayout = new javax.swing.GroupLayout(jPanelButter);
        jPanelButter.setLayout(jPanelButterLayout);
        jPanelButterLayout.setHorizontalGroup(
            jPanelButterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelButterLayout.createSequentialGroup()
                .addGroup(jPanelButterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanelButterLayout.createSequentialGroup()
                        .addGap(30, 30, 30)
                        .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 330, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanelButterLayout.createSequentialGroup()
                        .addGap(20, 20, 20)
                        .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, 0)
                        .addComponent(jTextField5, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanelButterLayout.createSequentialGroup()
                        .addGap(20, 20, 20)
                        .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, 0)
                        .addComponent(filterTypeChoiceComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanelButterLayout.createSequentialGroup()
                        .addGap(20, 20, 20)
                        .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, 0)
                        .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(39, Short.MAX_VALUE))
        );
        jPanelButterLayout.setVerticalGroup(
            jPanelButterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelButterLayout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(jPanelButterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanelButterLayout.createSequentialGroup()
                        .addGap(20, 20, 20)
                        .addComponent(jTextField5, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGroup(jPanelButterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanelButterLayout.createSequentialGroup()
                        .addGap(30, 30, 30)
                        .addComponent(filterTypeChoiceComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGroup(jPanelButterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanelButterLayout.createSequentialGroup()
                        .addGap(20, 20, 20)
                        .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        ((JLabel) filterTypeChoiceComboBox.getRenderer()).setHorizontalAlignment(JLabel.CENTER);

        jPanelForCardLayout.add(jPanelButter, "card1");

        jPanelElliptic.setPreferredSize(new java.awt.Dimension(495, 500));

        maximumRipplePanel.setBackground(new java.awt.Color(255, 255, 255));
        maximumRipplePanel.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        maximumRipplePanel.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        maximumRipplePanel.setText("Maximum ripple");
        maximumRipplePanel.setFocusable(false);
        maximumRipplePanel.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);

        nameEllipticPanel.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        nameEllipticPanel.setForeground(new java.awt.Color(75, 75, 75));
        nameEllipticPanel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        nameEllipticPanel.setText("Elliptic Filter");
        nameEllipticPanel.setFocusable(false);
        nameEllipticPanel.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);

        orderPanel.setBackground(new java.awt.Color(255, 255, 255));
        orderPanel.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        orderPanel.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        orderPanel.setText("Order");
        orderPanel.setFocusable(false);
        orderPanel.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);

        filterTypePanel.setBackground(new java.awt.Color(255, 255, 255));
        filterTypePanel.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        filterTypePanel.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        filterTypePanel.setText("Filter type");
        filterTypePanel.setFocusable(false);
        filterTypePanel.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);

        cutoffFrequencyPanel.setBackground(new java.awt.Color(255, 255, 255));
        cutoffFrequencyPanel.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        cutoffFrequencyPanel.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        cutoffFrequencyPanel.setText("Cutoff frequency");
        cutoffFrequencyPanel.setFocusable(false);
        cutoffFrequencyPanel.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);

        minimumAttenuationPanel.setBackground(new java.awt.Color(255, 255, 255));
        minimumAttenuationPanel.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        minimumAttenuationPanel.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        minimumAttenuationPanel.setText("Minimum attenuation");
        minimumAttenuationPanel.setFocusable(false);
        minimumAttenuationPanel.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);

        ellipticOrderTextField.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        ellipticOrderTextField.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        ellipticOrderTextField.setText("6");
        ellipticOrderTextField.setMinimumSize(new java.awt.Dimension(25, 46));
        ellipticOrderTextField.setPreferredSize(new java.awt.Dimension(25, 46));
        ellipticOrderTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ellipticOrderTextFieldActionPerformed(evt);
            }
        });

        ellipticMaximumRippleTextField.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        ellipticMaximumRippleTextField.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        ellipticMaximumRippleTextField.setText("0.1");
        ellipticMaximumRippleTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ellipticMaximumRippleTextFieldActionPerformed(evt);
            }
        });

        ellipticMinimumAttenuationTextField.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        ellipticMinimumAttenuationTextField.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        ellipticMinimumAttenuationTextField.setText("60");
        ellipticMinimumAttenuationTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ellipticMinimumAttenuationTextFieldActionPerformed(evt);
            }
        });

        ellipticCutoffFrequencyTextField.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        ellipticCutoffFrequencyTextField.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        ellipticCutoffFrequencyTextField.setText("0.05");
        ellipticCutoffFrequencyTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ellipticCutoffFrequencyTextFieldActionPerformed(evt);
            }
        });

        ellipticFilterTypeChoiceComboBox.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        ellipticFilterTypeChoiceComboBox.setMaximumRowCount(4);
        ellipticFilterTypeChoiceComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "low", "high", "pass", "stop" }));
        ellipticFilterTypeChoiceComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ellipticFilterTypeChoiceComboBoxActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanelEllipticLayout = new javax.swing.GroupLayout(jPanelElliptic);
        jPanelElliptic.setLayout(jPanelEllipticLayout);
        jPanelEllipticLayout.setHorizontalGroup(
            jPanelEllipticLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelEllipticLayout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addGroup(jPanelEllipticLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(nameEllipticPanel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanelEllipticLayout.createSequentialGroup()
                        .addGroup(jPanelEllipticLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(minimumAttenuationPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(cutoffFrequencyPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(maximumRipplePanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(filterTypePanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(orderPanel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(jPanelEllipticLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(ellipticOrderTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(ellipticMaximumRippleTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(ellipticMinimumAttenuationTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(ellipticCutoffFrequencyTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(ellipticFilterTypeChoiceComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap())
        );
        jPanelEllipticLayout.setVerticalGroup(
            jPanelEllipticLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelEllipticLayout.createSequentialGroup()
                .addComponent(nameEllipticPanel)
                .addGap(18, 18, 18)
                .addGroup(jPanelEllipticLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(orderPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 32, Short.MAX_VALUE)
                    .addComponent(ellipticOrderTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanelEllipticLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(filterTypePanel)
                    .addComponent(ellipticFilterTypeChoiceComboBox, javax.swing.GroupLayout.DEFAULT_SIZE, 32, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanelEllipticLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(maximumRipplePanel)
                    .addComponent(ellipticMaximumRippleTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanelEllipticLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(minimumAttenuationPanel)
                    .addComponent(ellipticMinimumAttenuationTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanelEllipticLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cutoffFrequencyPanel)
                    .addComponent(ellipticCutoffFrequencyTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(12, Short.MAX_VALUE))
        );

        ((JLabel) filterTypeChoiceComboBox.getRenderer()).setHorizontalAlignment(JLabel.CENTER);

        jPanelForCardLayout.add(jPanelElliptic, "card3");

        jPanelCheby.setPreferredSize(new java.awt.Dimension(495, 636));
        jPanelCheby.setLayout(null);

        jLabel4.setBackground(new java.awt.Color(255, 255, 255));
        jLabel4.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel4.setText("Maximum ripple");
        jLabel4.setFocusable(false);
        jLabel4.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jPanelCheby.add(jLabel4);
        jLabel4.setBounds(20, 240, 170, 80);

        jLabel7.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        jLabel7.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel7.setText("Chebyshev Filter");
        jLabel7.setFocusable(false);
        jLabel7.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jPanelCheby.add(jLabel7);
        jLabel7.setBounds(30, 10, 330, 70);

        jTextField2.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jTextField2.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jTextField2.setText("5");
        jTextField2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField2ActionPerformed(evt);
            }
        });
        jPanelCheby.add(jTextField2);
        jTextField2.setBounds(190, 260, 180, 40);

        jLabel8.setBackground(new java.awt.Color(255, 255, 255));
        jLabel8.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel8.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel8.setText("Order");
        jLabel8.setFocusable(false);
        jLabel8.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jPanelCheby.add(jLabel8);
        jLabel8.setBounds(20, 80, 170, 80);

        filterTypeChoiceComboBox1.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        filterTypeChoiceComboBox1.setMaximumRowCount(4);
        filterTypeChoiceComboBox1.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Lowpass", "Highpass", "Bandpass", "Bandstop" }));
        jPanelCheby.add(filterTypeChoiceComboBox1);
        filterTypeChoiceComboBox1.setBounds(190, 190, 180, 30);
        ((JLabel) filterTypeChoiceComboBox.getRenderer()).setHorizontalAlignment(JLabel.CENTER);

        jLabel9.setBackground(new java.awt.Color(255, 255, 255));
        jLabel9.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel9.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel9.setText("Filter type");
        jLabel9.setFocusable(false);
        jLabel9.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jPanelCheby.add(jLabel9);
        jLabel9.setBounds(20, 160, 170, 80);

        jTextField6.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jTextField6.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jTextField6.setText("2");
        jTextField6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField6ActionPerformed(evt);
            }
        });
        jPanelCheby.add(jTextField6);
        jTextField6.setBounds(190, 100, 180, 40);

        jTextField3.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jTextField3.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jTextField3.setText("10000");
        jTextField3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField3ActionPerformed(evt);
            }
        });
        jPanelCheby.add(jTextField3);
        jTextField3.setBounds(190, 340, 180, 40);

        jLabel10.setBackground(new java.awt.Color(255, 255, 255));
        jLabel10.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel10.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel10.setText(" Cutoff frequency");
        jLabel10.setFocusable(false);
        jLabel10.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jPanelCheby.add(jLabel10);
        jLabel10.setBounds(20, 320, 170, 80);

        jPanelForCardLayout.add(jPanelCheby, "card2");

        jPanelBessel.setPreferredSize(new java.awt.Dimension(495, 636));
        jPanelBessel.setLayout(null);

        jLabel18.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        jLabel18.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel18.setText("Bessel Filter");
        jLabel18.setFocusable(false);
        jLabel18.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jPanelBessel.add(jLabel18);
        jLabel18.setBounds(30, 10, 330, 70);

        jLabel19.setBackground(new java.awt.Color(255, 255, 255));
        jLabel19.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel19.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel19.setText("Order");
        jLabel19.setFocusable(false);
        jLabel19.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jPanelBessel.add(jLabel19);
        jLabel19.setBounds(20, 80, 170, 80);

        filterTypeChoiceComboBox3.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        filterTypeChoiceComboBox3.setMaximumRowCount(4);
        filterTypeChoiceComboBox3.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Lowpass", "Highpass", "Bandpass", "Bandstop" }));
        jPanelBessel.add(filterTypeChoiceComboBox3);
        filterTypeChoiceComboBox3.setBounds(190, 190, 180, 30);
        ((JLabel) filterTypeChoiceComboBox.getRenderer()).setHorizontalAlignment(JLabel.CENTER);

        jLabel20.setBackground(new java.awt.Color(255, 255, 255));
        jLabel20.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel20.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel20.setText("Filter type");
        jLabel20.setFocusable(false);
        jLabel20.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jPanelBessel.add(jLabel20);
        jLabel20.setBounds(20, 160, 170, 80);

        jTextField11.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jTextField11.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jTextField11.setText("2");
        jTextField11.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField11ActionPerformed(evt);
            }
        });
        jPanelBessel.add(jTextField11);
        jTextField11.setBounds(190, 100, 180, 40);

        jTextField12.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jTextField12.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jTextField12.setText("10000");
        jTextField12.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField12ActionPerformed(evt);
            }
        });
        jPanelBessel.add(jTextField12);
        jTextField12.setBounds(190, 260, 180, 40);

        jLabel21.setBackground(new java.awt.Color(255, 255, 255));
        jLabel21.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel21.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel21.setText(" Cutoff frequency");
        jLabel21.setFocusable(false);
        jLabel21.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jPanelBessel.add(jLabel21);
        jLabel21.setBounds(20, 240, 170, 80);

        jPanelForCardLayout.add(jPanelBessel, "card4");

        importKey.setText("Import");
        importKey.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                importKeyActionPerformed(evt);
            }
        });

        exportKey.setText("Export");
        exportKey.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exportKeyActionPerformed(evt);
            }
        });

        enable.setText(" use filter");
        enable.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                enableActionPerformed(evt);
            }
        });

        historyList.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                historyListMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(historyList);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(34, 34, 34)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                                .addGap(84, 84, 84))
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addGap(16, 16, 16)
                                        .addComponent(jLabel1))
                                    .addComponent(ButterRadioButton)
                                    .addComponent(ChebyRadioButton)
                                    .addComponent(EllipticRadioButton)
                                    .addComponent(BesselRadioButton)
                                    .addComponent(enable, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(150, 150, 150)))
                        .addComponent(jPanelAfc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(8, 8, 8)
                                .addComponent(jPanelForCardLayout, javax.swing.GroupLayout.PREFERRED_SIZE, 409, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addGap(27, 27, 27)
                                .addComponent(StartButton, javax.swing.GroupLayout.PREFERRED_SIZE, 380, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(importKey)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(exportKey)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(70, 70, 70)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanelAfc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanelForCardLayout, javax.swing.GroupLayout.PREFERRED_SIZE, 270, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(19, 19, 19)
                        .addComponent(StartButton))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addGap(10, 10, 10)
                        .addComponent(ButterRadioButton)
                        .addGap(3, 3, 3)
                        .addComponent(ChebyRadioButton)
                        .addGap(3, 3, 3)
                        .addComponent(EllipticRadioButton)
                        .addGap(3, 3, 3)
                        .addComponent(BesselRadioButton)
                        .addGap(80, 80, 80)
                        .addComponent(enable, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 54, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(importKey)
                    .addComponent(exportKey))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void ButterRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ButterRadioButtonActionPerformed
        CardLayout card = (CardLayout) jPanelForCardLayout.getLayout();
        card.show(jPanelForCardLayout, "card1");
    }//GEN-LAST:event_ButterRadioButtonActionPerformed

    private void ChebyRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ChebyRadioButtonActionPerformed
        CardLayout card = (CardLayout) jPanelForCardLayout.getLayout();
        card.show(jPanelForCardLayout, "card2");
    }//GEN-LAST:event_ChebyRadioButtonActionPerformed

    private void EllipticRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_EllipticRadioButtonActionPerformed
        CardLayout card = (CardLayout) jPanelForCardLayout.getLayout();
        card.show(jPanelForCardLayout, "card3");
    }//GEN-LAST:event_EllipticRadioButtonActionPerformed

    private void BesselRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BesselRadioButtonActionPerformed
        CardLayout card = (CardLayout) jPanelForCardLayout.getLayout();
        card.show(jPanelForCardLayout, "card4");
    }//GEN-LAST:event_BesselRadioButtonActionPerformed

    private void jTextField1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField1ActionPerformed

    private void jTextField5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField5ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField5ActionPerformed

    private void jTextField2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField2ActionPerformed

    private void jTextField6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField6ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField6ActionPerformed

    private void jTextField3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField3ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField3ActionPerformed

    private void ellipticMaximumRippleTextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ellipticMaximumRippleTextFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_ellipticMaximumRippleTextFieldActionPerformed

    private void ellipticOrderTextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ellipticOrderTextFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_ellipticOrderTextFieldActionPerformed

    private void ellipticCutoffFrequencyTextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ellipticCutoffFrequencyTextFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_ellipticCutoffFrequencyTextFieldActionPerformed

    private void ellipticMinimumAttenuationTextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ellipticMinimumAttenuationTextFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_ellipticMinimumAttenuationTextFieldActionPerformed

    private void jTextField11ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField11ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField11ActionPerformed

    private void jTextField12ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField12ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField12ActionPerformed

    private void StartButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_StartButtonActionPerformed
        formCmdRequestForPython();
        try {
            reloadImage();
            List<String> config = readFileConfigFilters();
            Map<String, String> exportConfigFilters = exportConfigFiltersInFile();
            String nameFilters = formNameFileHistory(exportConfigFilters);
            setHistoruList(nameFilters, exportConfigFilters, config);
        } catch (Exception e) {
            e.printStackTrace();
            Accessory.showMessage("Image is not forming properly");
        }
    }//GEN-LAST:event_StartButtonActionPerformed

    private void ellipticFilterTypeChoiceComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ellipticFilterTypeChoiceComboBoxActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_ellipticFilterTypeChoiceComboBoxActionPerformed

    private void filterTypeChoiceComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_filterTypeChoiceComboBoxActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_filterTypeChoiceComboBoxActionPerformed

    private void importKeyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_importKeyActionPerformed
        String addressImportFile = useImportFile();
        if (addressImportFile == null) {
            return;
        }
        SerializableImageWithTextObject readFileSaveConfigFilters;
        try {
            readFileSaveConfigFilters = deserializeFilterObject(addressImportFile);
            addressImportFile = addressImportFile.replace("\\", "/");
            String[] nameAddress = addressImportFile.split("/");
            setHistoruList(nameAddress[nameAddress.length - 1], readFileSaveConfigFilters.getInfo(), readFileSaveConfigFilters.getConfig());
            writeFileConfigFilters(readFileSaveConfigFilters.getConfig());
            setParam(readFileSaveConfigFilters.getInfo(), readFileSaveConfigFilters.getImage());

        } catch (IOException | ClassNotFoundException ex) {
            Logger.getLogger(FilterDesignWindowUIFrame.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_importKeyActionPerformed

    private void exportKeyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exportKeyActionPerformed
        String adresSave = useExportFile();
        if (adresSave == null) {
            return;
        }
        adresSave = adresSave + ".fnt";
        List<String> config = readFileConfigFilters();
        Map<String, String> exportConfigFilters = exportConfigFiltersInFile();
        try {
            if (!serchAddressName(adresSave)) {
                serializeFilterObject(adresSave, "./filterDesignResult.png", exportConfigFilters, config);
                JOptionPane.showMessageDialog(null, "File saved.");
            } else {
                JOptionPane.showMessageDialog(null, "File not saved.");
            }
        } catch (IOException ex) {
            Logger.getLogger(FilterDesignWindowUIFrame.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_exportKeyActionPerformed

    /*
    * This method set historu list.
     */
    private void setHistoruList(String nameFilters, Map<String, String> exportConfigFilters, List<String> config) {
        boolean flag = true;
        if (objectConfigFiltersAutosave.size() != 0) {
            for (BuildFilters object : objectConfigFiltersAutosave) {
                if (object.getName().equals(nameFilters)) {
                    flag = false;
                    break;
                }
            }
        }
        if (flag) {
            setListHistoruName(nameFilters);
            SerializableImageWithTextObject objectFilters = new SerializableImageWithTextObject("filterDesignResult.png", exportConfigFilters, config);
            objectConfigFiltersAutosave.add(0, new BuildFilters(nameFilters, objectFilters));
            historyList.setListData(nameListFilters);
        }
        if (objectConfigFiltersAutosave.size() > 3) {
            objectConfigFiltersAutosave.remove(3);
        }
    }

    /*
     * This method form MAP<name line, param line>.
     */
    private void setParam(Map<String, String> infoInMap, Image image) {
        ellipticOrderTextField.setText(infoInMap.get("Order"));
        ellipticMaximumRippleTextField.setText(infoInMap.get("Maximum ripple"));
        ellipticMinimumAttenuationTextField.setText(infoInMap.get("Minimum attenuation"));
        ellipticCutoffFrequencyTextField.setText(infoInMap.get("Cutoff frequency"));
        ellipticFilterTypeChoiceComboBox.setSelectedIndex(Integer.parseInt(infoInMap.get("Filter type")));
        ResizableImagePane jPanelInResizableImagePaneObject = (ResizableImagePane) jPanelAfc;
        jPanelInResizableImagePaneObject.setImage(image);
    }

    private void historyListMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_historyListMouseClicked
        String nameHistoryList = historyList.getSelectedValue();
        if (nameHistoryList == null) {
            return;
        }
        for (BuildFilters object : objectConfigFiltersAutosave) {
            if (nameHistoryList.equals(object.getName())) {
                SerializableImageWithTextObject parameters = object.getSerializableImageWithTextObject();
                setParam(parameters.getInfo(), parameters.getImage());
                writeFileConfigFilters(parameters.getConfig());
            }
        }
    }//GEN-LAST:event_historyListMouseClicked

    private void enableActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_enableActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_enableActionPerformed

    /**
     * Invoke frame.
     */
    public static void invokeFilterUI() throws InterruptedException, InvocationTargetException {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(FilterDesignWindowUIFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(FilterDesignWindowUIFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(FilterDesignWindowUIFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(FilterDesignWindowUIFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        if (theDialog == null) {
            JFrame jf = null;
            if (TopLevel.isMDIMode()) {
                jf = TopLevel.getCurrentJFrame();
            }
            theDialog = new FilterDesignWindowUIFrame(jf);
        }

        theDialog.setVisible(true);
        //}
        //});
    }

    /*
     * This method form hisory list.
     */
    public void setListHistoruName(String name) {
        nameListFilters[2] = nameListFilters[1];
        nameListFilters[1] = nameListFilters[0];
        nameListFilters[0] = name;
    }

    /**
     * Class for "InitiateForm", class initiates the form with filters.
     */
    public static class InitiateForm extends Job {

        public InitiateForm() {
            super("Initiate Form", User.getUserTool(), Job.Type.CHANGE, null, null, Job.Priority.USER);
            startJob();
        }

        @Override
        public boolean doIt() throws JobException {
            try {
                invokeFilterUI();
            } catch (Exception e) {
                e.printStackTrace();
            }

            return true;
        }
    }


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JRadioButton BesselRadioButton;
    private javax.swing.JRadioButton ButterRadioButton;
    private javax.swing.JRadioButton ChebyRadioButton;
    private javax.swing.JRadioButton EllipticRadioButton;
    private javax.swing.JButton StartButton;
    private javax.swing.ButtonGroup chooseTypeFilterGroup;
    private javax.swing.JLabel cutoffFrequencyPanel;
    private javax.swing.JTextField ellipticCutoffFrequencyTextField;
    private javax.swing.JComboBox<String> ellipticFilterTypeChoiceComboBox;
    private javax.swing.JTextField ellipticMaximumRippleTextField;
    private javax.swing.JTextField ellipticMinimumAttenuationTextField;
    private javax.swing.JTextField ellipticOrderTextField;
    private javax.swing.JCheckBox enable;
    private javax.swing.JButton exportKey;
    private javax.swing.JComboBox<String> filterTypeChoiceComboBox;
    private javax.swing.JComboBox<String> filterTypeChoiceComboBox1;
    private javax.swing.JComboBox<String> filterTypeChoiceComboBox3;
    private javax.swing.JLabel filterTypePanel;
    private javax.swing.JList<String> historyList;
    private javax.swing.JButton importKey;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanelAfc;
    private javax.swing.JPanel jPanelBessel;
    private javax.swing.JPanel jPanelButter;
    private javax.swing.JPanel jPanelCheby;
    private javax.swing.JPanel jPanelElliptic;
    private javax.swing.JPanel jPanelForCardLayout;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextField11;
    private javax.swing.JTextField jTextField12;
    private javax.swing.JTextField jTextField2;
    private javax.swing.JTextField jTextField3;
    private javax.swing.JTextField jTextField5;
    private javax.swing.JTextField jTextField6;
    private javax.swing.JLabel maximumRipplePanel;
    private javax.swing.JLabel minimumAttenuationPanel;
    private javax.swing.JLabel nameEllipticPanel;
    private javax.swing.JLabel orderPanel;
    // End of variables declaration//GEN-END:variables

    /*
     * This class forms an object which contains all the information about the current configuration.
     */
    public class BuildFilters {

        private String name;
        private SerializableImageWithTextObject objectInfo;

        public BuildFilters(String name, SerializableImageWithTextObject ObjectInfo) {
            this.objectInfo = ObjectInfo;
            this.name = name;
        }

        public SerializableImageWithTextObject getSerializableImageWithTextObject() {
            return objectInfo;
        }

        public String getName() {
            return name;
        }
    }
}
