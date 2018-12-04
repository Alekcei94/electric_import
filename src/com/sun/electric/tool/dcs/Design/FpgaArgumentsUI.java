/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sun.electric.tool.dcs.Design;

import com.sun.electric.tool.Job;
import com.sun.electric.tool.JobException;
import com.sun.electric.tool.user.User;
import com.sun.electric.tool.user.dialogs.EModelessDialog;
import com.sun.electric.tool.user.ui.TopLevel;
import java.awt.Frame;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JTextField;

/**
 *
 * @author diivanov
 */
public class FpgaArgumentsUI extends EModelessDialog {

    private static FpgaArgumentsUI fpgaUI;
    private static final Map<String, ParameterObject> parametersMap = new HashMap<>();

    /**
     * Creates new form FilterDesignWindowUIFrame
     */
    public FpgaArgumentsUI(Frame parent) {
        super(parent);
        initComponents();
    }

    /**
     * Invoke frame.
     */
    public static void invokeFpgaUI() throws InterruptedException, InvocationTargetException {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
 /*try {
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
        }*/
        //</editor-fold>

        if (fpgaUI == null) {
            JFrame jf = null;
            if (TopLevel.isMDIMode()) {
                jf = TopLevel.getCurrentJFrame();
            }
            fpgaUI = new FpgaArgumentsUI(jf);
        }

        fpgaUI.setVisible(true);

    }

    /**
     * Method to get list of arguments for filter processing.
     * @return 
     */
    public static List<String> getArgumentList() {
        List<String> argumentList = new ArrayList<>();
        parametersMap.entrySet().forEach((entry) -> {
            ParameterObject pObj = (ParameterObject) entry.getValue();
            if (!pObj.isRun()) {
                return;
            }
            String result = pObj.getParameterName();
            if (pObj.hasTextField()) {
                result += " " + pObj.getParameterValue();
            }
            argumentList.add(result);
        });
        return argumentList;
    }
    
    public static String getTopArgument() {
        String argument = "-top " + fpgaUI.topModuleTextField.getText();
        return argument;
    }
    
    public static boolean checkForTopArgument() {
        if(fpgaUI == null) {
            return false;
        } else if(fpgaUI.topModuleTextField.getText().equals("")) {
            return false;
        }
        return true;
    }

    private void addParameter(String parameterName, JComboBox parameterComboBox, JTextField textField) {
        ParameterObject paramObj = parametersMap.get(parameterName);
        if (paramObj == null) {
            paramObj = new ParameterObject(parameterName);
            parametersMap.put(parameterName, paramObj);
        }
        if (parameterComboBox != null) {
            paramObj.setParameterComboBox(parameterComboBox);
        }
        if (textField != null) {
            paramObj.setTextField(textField);
        }
    }

    private void addButton(String parameterName, JButton button) {
        ParameterObject paramObj = parametersMap.get(parameterName);
        if (paramObj == null) {
            paramObj = new ParameterObject(parameterName);
            parametersMap.put(parameterName, paramObj);
        }
        paramObj.setOpenButton(button);
    }

    private void setTextByOpenButtonToParameterObject(JButton button) {
        parametersMap.entrySet().stream().filter((entry) -> {
            ParameterObject pObj = (ParameterObject) entry.getValue();
            if (pObj.getOpenButton() == button) {
                String absPath = getUserDefinedAbsolutePath();
                if(absPath != null) {
                    pObj.getTextField().setText(absPath);
                }
                return true;
            }
            return false;
        }).findFirst();
    }

    /**
     * Method to get path by from user.
     * Using typical JFileChooser.
     * @return 
     */
    private String getUserDefinedAbsolutePath() {
        JFrame jf = null;
        if (TopLevel.isMDIMode()) {
            jf = TopLevel.getCurrentJFrame();
        }
        JFileChooser chooser = new JFileChooser();
        int returnVal = chooser.showOpenDialog(jf);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File f = chooser.getSelectedFile();
            return f.getAbsolutePath();
        }
        return null;
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
                invokeFpgaUI();
            } catch (Exception e) {
                e.printStackTrace();
            }

            return true;
        }
    }

    private class ParameterObject {

        private JComboBox parameterComboBox;
        private JTextField textField;
        private JButton openButton;

        private final String parameterName;

        private ParameterObject(String parameter) {
            this.parameterName = parameter;
        }

        public boolean hasTextField() {
            return textField != null;
        }

        /**
         * @return the run
         */
        public boolean isRun() {
            String result = (String) parameterComboBox.getSelectedItem();
            return result.equals("Yes");
        }

        /**
         * @return the parameterValue
         */
        public String getParameterValue() {
            return textField.getText();
        }

        /**
         * @return the parameterName
         */
        public String getParameterName() {
            return parameterName;
        }

        /**
         * @return the parameterComboBox
         */
        public JComboBox getParameterComboBox() {
            return parameterComboBox;
        }

        /**
         * @param parameterComboBox the parameterComboBox to set
         */
        public void setParameterComboBox(JComboBox parameterComboBox) {
            this.parameterComboBox = parameterComboBox;
        }

        /**
         * @return the textField
         */
        public JTextField getTextField() {
            return textField;
        }

        /**
         * @param textField the textField to set
         */
        public void setTextField(JTextField textField) {
            this.textField = textField;
        }

        public boolean verifyObject() {
            if (parameterComboBox == null && textField == null) {
                return false;
            }
            if (this.isRun() && this.hasTextField() && textField.getText().equals("")) {
                return false;
            }
            return true;
        }

        /**
         * @param openButton the openButton to set
         */
        public void setOpenButton(JButton openButton) {
            this.openButton = openButton;
        }

        /**
         * @return the openButton
         */
        public JButton getOpenButton() {
            return openButton;
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        labelPanel = new javax.swing.JPanel();
        mainLabel = new javax.swing.JLabel();
        mainScrollPanel = new javax.swing.JScrollPane();
        parametersPanel = new javax.swing.JPanel();
        turnOffLogicPanel = new javax.swing.JPanel();
        outputFileNamesLabel1 = new javax.swing.JLabel();
        turnOffLogicCutLabel = new javax.swing.JLabel();
        turnOffLogicYesNoComboBox = new javax.swing.JComboBox<>();
        turnOffLogicTextField1 = new javax.swing.JTextField();
        topModulePanel = new javax.swing.JPanel();
        topModuleLabel = new javax.swing.JLabel();
        topModuleTextField = new javax.swing.JTextField();
        turnOffAdditionalIOCellsPanel = new javax.swing.JPanel();
        turnOffAdditionalIOCellsLabel = new javax.swing.JLabel();
        turnOffAdditionalIOCellsCutLabel = new javax.swing.JLabel();
        turnOffAdditionalIOCellsYesNoComboBox = new javax.swing.JComboBox<>();
        useIOPlacementFilePanel = new javax.swing.JPanel();
        useIOPlacementFileLabel = new javax.swing.JLabel();
        useIOPlacementFileCutLabel = new javax.swing.JLabel();
        useIOPlacementFileYesNoComboBox = new javax.swing.JComboBox<>();
        useIOPlacementFileTextField = new javax.swing.JTextField();
        useIOPlacementFileOpenButton = new javax.swing.JButton();
        le_unionPanel = new javax.swing.JPanel();
        disableLEPairClusteringLabel = new javax.swing.JLabel();
        disableLEPairClusteringCutLabel = new javax.swing.JLabel();
        disableLEPairClusteringYesNoComboBox = new javax.swing.JComboBox<>();
        lePanel = new javax.swing.JPanel();
        useLEClusteringFileLabel = new javax.swing.JLabel();
        useLEClusteringFileCutLabel = new javax.swing.JLabel();
        useLEClusteringFileYesNoComboBox = new javax.swing.JComboBox<>();
        useLEClusteringFileTextField = new javax.swing.JTextField();
        useLEClusteringFileOpenButton = new javax.swing.JButton();
        pPanel = new javax.swing.JPanel();
        useExistingPlacementLabel = new javax.swing.JLabel();
        useExistingPlacementCutLabel = new javax.swing.JLabel();
        useExistingPlacementYesNoComboBox = new javax.swing.JComboBox<>();
        useExistingPlacementTextField = new javax.swing.JTextField();
        useExistingPlacementOpenButton = new javax.swing.JButton();
        saPanel = new javax.swing.JPanel();
        disableSimulatingAnnealingLabel = new javax.swing.JLabel();
        disableSimulatingAnnealingCutLabel = new javax.swing.JLabel();
        disableSimulatingAnnealingYesNoComboBox = new javax.swing.JComboBox<>();
        replacePanel = new javax.swing.JPanel();
        replaceLabel = new javax.swing.JLabel();
        replaceCutLabel = new javax.swing.JLabel();
        replaceYesNoComboBox = new javax.swing.JComboBox<>();
        outputFileNamesPanel2 = new javax.swing.JPanel();
        sa_lmLabel = new javax.swing.JLabel();
        sa_lmCutLabel = new javax.swing.JLabel();
        sa_lmYesNoComboBox = new javax.swing.JComboBox<>();
        sa_lmTextField = new javax.swing.JTextField();
        orderPanel = new javax.swing.JPanel();
        orderLabel = new javax.swing.JLabel();
        orderCutLabel = new javax.swing.JLabel();
        orderYesNoComboBox = new javax.swing.JComboBox<>();
        orderTextField = new javax.swing.JTextField();
        v_hPanel = new javax.swing.JPanel();
        v_hLabel = new javax.swing.JLabel();
        v_hCutLabel = new javax.swing.JLabel();
        v_hYesNoComboBox = new javax.swing.JComboBox<>();
        v_hTextField = new javax.swing.JTextField();
        path_wPanel = new javax.swing.JPanel();
        path_wLabel = new javax.swing.JLabel();
        path_wCutLabel = new javax.swing.JLabel();
        path_wYesNoComboBox = new javax.swing.JComboBox<>();
        path_wTextField = new javax.swing.JTextField();
        v_pPanel = new javax.swing.JPanel();
        v_pLabel = new javax.swing.JLabel();
        v_pCutLabel = new javax.swing.JLabel();
        v_pYesNoComboBox = new javax.swing.JComboBox<>();
        v_pTextField = new javax.swing.JTextField();
        path_lPanel = new javax.swing.JPanel();
        path_lLabel = new javax.swing.JLabel();
        path_lCutLabel = new javax.swing.JLabel();
        path_lYesNoComboBox = new javax.swing.JComboBox<>();
        path_lTextField = new javax.swing.JTextField();
        clk_1Panel = new javax.swing.JPanel();
        clk_1Label = new javax.swing.JLabel();
        clk_1CutLabel3 = new javax.swing.JLabel();
        clk_1YesNoComboBox = new javax.swing.JComboBox<>();
        clk_1TextField = new javax.swing.JTextField();
        clk_4Panel = new javax.swing.JPanel();
        clk_4Label = new javax.swing.JLabel();
        clk_4CutLabel = new javax.swing.JLabel();
        clk_4YesNoComboBox = new javax.swing.JComboBox<>();
        clk_4TextField = new javax.swing.JTextField();
        clk_3Panel = new javax.swing.JPanel();
        clk_3Label = new javax.swing.JLabel();
        clk_3CutLabel = new javax.swing.JLabel();
        clk_3YesNoComboBox = new javax.swing.JComboBox<>();
        clk_3TextField = new javax.swing.JTextField();
        clk_2Panel = new javax.swing.JPanel();
        clk_2Label = new javax.swing.JLabel();
        clk_2CutLabel = new javax.swing.JLabel();
        clk_2YesNoComboBox = new javax.swing.JComboBox<>();
        clk_2TextField = new javax.swing.JTextField();
        outputFileNamesPanel4 = new javax.swing.JPanel();
        outputFileNamesLabel = new javax.swing.JLabel();
        outputFileNamesCutLabel = new javax.swing.JLabel();
        outputFileNamesYesNoComboBox = new javax.swing.JComboBox<>();
        outputFileNamesTextField = new javax.swing.JTextField();
        outputFileNamesPanel5 = new javax.swing.JPanel();
        outputFileNamesLabel3 = new javax.swing.JLabel();
        outputFileNamesCutLabel2 = new javax.swing.JLabel();
        outputFileNamesYesNoComboBox2 = new javax.swing.JComboBox<>();
        outputFileNamesTextField2 = new javax.swing.JTextField();
        okCancelButtonPanel = new javax.swing.JPanel();
        okButton = new javax.swing.JButton();
        cancelButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setMaximumSize(new java.awt.Dimension(32000, 32000));
        setMinimumSize(new java.awt.Dimension(760, 370));
        setPreferredSize(new java.awt.Dimension(760, 800));
        setSize(new java.awt.Dimension(760, 800));
        getContentPane().setLayout(new java.awt.GridBagLayout());

        labelPanel.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        labelPanel.setPreferredSize(new java.awt.Dimension(600, 31));

        mainLabel.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        mainLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        mainLabel.setText("Run settings");

        javax.swing.GroupLayout labelPanelLayout = new javax.swing.GroupLayout(labelPanel);
        labelPanel.setLayout(labelPanelLayout);
        labelPanelLayout.setHorizontalGroup(
            labelPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(mainLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 760, Short.MAX_VALUE)
        );
        labelPanelLayout.setVerticalGroup(
            labelPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(mainLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 31, Short.MAX_VALUE)
        );

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 676;
        gridBagConstraints.ipady = 11;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        getContentPane().add(labelPanel, gridBagConstraints);

        mainScrollPanel.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        mainScrollPanel.getVerticalScrollBar().setUnitIncrement(25);

        parametersPanel.setAutoscrolls(true);
        parametersPanel.setPreferredSize(new java.awt.Dimension(740, 690));
        parametersPanel.setLayout(new java.awt.GridBagLayout());

        turnOffLogicPanel.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        turnOffLogicPanel.setMaximumSize(new java.awt.Dimension(600, 800));

        outputFileNamesLabel1.setFont(new java.awt.Font("Segoe UI", 0, 12)); // NOI18N
        outputFileNamesLabel1.setText("Turn off the logic and DFF joining");

        turnOffLogicCutLabel.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        turnOffLogicCutLabel.setText("[-nojoin]");
        turnOffLogicCutLabel.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);

        turnOffLogicYesNoComboBox.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        turnOffLogicYesNoComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "No", "Yes" }));

        turnOffLogicTextField1.setFont(new java.awt.Font("Segoe UI", 0, 12)); // NOI18N

        javax.swing.GroupLayout turnOffLogicPanelLayout = new javax.swing.GroupLayout(turnOffLogicPanel);
        turnOffLogicPanel.setLayout(turnOffLogicPanelLayout);
        turnOffLogicPanelLayout.setHorizontalGroup(
            turnOffLogicPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(turnOffLogicPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(outputFileNamesLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 242, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(turnOffLogicCutLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(turnOffLogicYesNoComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(turnOffLogicTextField1, javax.swing.GroupLayout.DEFAULT_SIZE, 298, Short.MAX_VALUE)
                .addContainerGap())
        );
        turnOffLogicPanelLayout.setVerticalGroup(
            turnOffLogicPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(turnOffLogicPanelLayout.createSequentialGroup()
                .addGroup(turnOffLogicPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(turnOffLogicCutLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(turnOffLogicYesNoComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(turnOffLogicTextField1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, turnOffLogicPanelLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(outputFileNamesLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );

        addParameter("-nojoin", turnOffLogicYesNoComboBox, null);
        addParameter("-nojoin", null, turnOffLogicTextField1);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 292;
        gridBagConstraints.ipady = -7;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(11, 0, 0, 10);
        parametersPanel.add(turnOffLogicPanel, gridBagConstraints);

        topModulePanel.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        topModulePanel.setMaximumSize(new java.awt.Dimension(600, 800));
        topModulePanel.setPreferredSize(new java.awt.Dimension(600, 31));

        topModuleLabel.setFont(new java.awt.Font("Segoe UI", 0, 12)); // NOI18N
        topModuleLabel.setText("Name of top module");

        topModuleTextField.setFont(new java.awt.Font("Segoe UI", 0, 12)); // NOI18N
        topModuleTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                topModuleTextFieldActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout topModulePanelLayout = new javax.swing.GroupLayout(topModulePanel);
        topModulePanel.setLayout(topModulePanelLayout);
        topModulePanelLayout.setHorizontalGroup(
            topModulePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(topModulePanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(topModuleLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 242, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(topModuleTextField)
                .addContainerGap())
        );
        topModulePanelLayout.setVerticalGroup(
            topModulePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(topModulePanelLayout.createSequentialGroup()
                .addComponent(topModuleTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, topModulePanelLayout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(topModuleLabel)
                .addGap(15, 15, 15))
        );

        addParameter("-0", null, topModuleTextField);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 476;
        gridBagConstraints.ipady = -7;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(9, 0, 0, 10);
        parametersPanel.add(topModulePanel, gridBagConstraints);

        turnOffAdditionalIOCellsPanel.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        turnOffAdditionalIOCellsPanel.setMaximumSize(new java.awt.Dimension(600, 800));

        turnOffAdditionalIOCellsLabel.setFont(new java.awt.Font("Segoe UI", 0, 12)); // NOI18N
        turnOffAdditionalIOCellsLabel.setText("Turn off the generation of additional IO cells");

        turnOffAdditionalIOCellsCutLabel.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        turnOffAdditionalIOCellsCutLabel.setText("[-nobuf]");
        turnOffAdditionalIOCellsCutLabel.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);

        turnOffAdditionalIOCellsYesNoComboBox.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        turnOffAdditionalIOCellsYesNoComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "No", "Yes" }));

        javax.swing.GroupLayout turnOffAdditionalIOCellsPanelLayout = new javax.swing.GroupLayout(turnOffAdditionalIOCellsPanel);
        turnOffAdditionalIOCellsPanel.setLayout(turnOffAdditionalIOCellsPanelLayout);
        turnOffAdditionalIOCellsPanelLayout.setHorizontalGroup(
            turnOffAdditionalIOCellsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(turnOffAdditionalIOCellsPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(turnOffAdditionalIOCellsLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 242, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(turnOffAdditionalIOCellsCutLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(turnOffAdditionalIOCellsYesNoComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(314, Short.MAX_VALUE))
        );
        turnOffAdditionalIOCellsPanelLayout.setVerticalGroup(
            turnOffAdditionalIOCellsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(turnOffAdditionalIOCellsPanelLayout.createSequentialGroup()
                .addGroup(turnOffAdditionalIOCellsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(turnOffAdditionalIOCellsYesNoComboBox, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(turnOffAdditionalIOCellsCutLabel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(turnOffAdditionalIOCellsLabel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        addParameter("-nobuf", turnOffAdditionalIOCellsYesNoComboBox, null);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 304;
        gridBagConstraints.ipady = -7;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(11, 0, 0, 10);
        parametersPanel.add(turnOffAdditionalIOCellsPanel, gridBagConstraints);

        useIOPlacementFilePanel.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        useIOPlacementFilePanel.setMaximumSize(new java.awt.Dimension(600, 800));
        useIOPlacementFilePanel.setPreferredSize(new java.awt.Dimension(603, 31));

        useIOPlacementFileLabel.setFont(new java.awt.Font("Segoe UI", 0, 12)); // NOI18N
        useIOPlacementFileLabel.setText("Use IOs placement file");

        useIOPlacementFileCutLabel.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        useIOPlacementFileCutLabel.setText("[-inout]");
        useIOPlacementFileCutLabel.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);

        useIOPlacementFileYesNoComboBox.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        useIOPlacementFileYesNoComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "No", "Yes" }));

        useIOPlacementFileTextField.setFont(new java.awt.Font("Segoe UI", 0, 12)); // NOI18N

        useIOPlacementFileOpenButton.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        useIOPlacementFileOpenButton.setText("Open");
        useIOPlacementFileOpenButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                useIOPlacementFileOpenButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout useIOPlacementFilePanelLayout = new javax.swing.GroupLayout(useIOPlacementFilePanel);
        useIOPlacementFilePanel.setLayout(useIOPlacementFilePanelLayout);
        useIOPlacementFilePanelLayout.setHorizontalGroup(
            useIOPlacementFilePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(useIOPlacementFilePanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(useIOPlacementFileLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 242, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(useIOPlacementFileCutLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(useIOPlacementFileYesNoComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(useIOPlacementFileTextField, javax.swing.GroupLayout.DEFAULT_SIZE, 188, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(useIOPlacementFileOpenButton, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        useIOPlacementFilePanelLayout.setVerticalGroup(
            useIOPlacementFilePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(useIOPlacementFilePanelLayout.createSequentialGroup()
                .addGroup(useIOPlacementFilePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(useIOPlacementFileOpenButton, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(useIOPlacementFileTextField, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(useIOPlacementFileYesNoComboBox, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(useIOPlacementFileCutLabel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(useIOPlacementFileLabel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(0, 4, Short.MAX_VALUE))
        );

        addParameter("-inout", useIOPlacementFileYesNoComboBox, null);
        addParameter("-inout", null, useIOPlacementFileTextField);
        addButton("-inout", useIOPlacementFileOpenButton);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 182;
        gridBagConstraints.ipady = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(11, 0, 0, 10);
        parametersPanel.add(useIOPlacementFilePanel, gridBagConstraints);

        le_unionPanel.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        le_unionPanel.setMaximumSize(new java.awt.Dimension(600, 800));

        disableLEPairClusteringLabel.setFont(new java.awt.Font("Segoe UI", 0, 12)); // NOI18N
        disableLEPairClusteringLabel.setText("Disable LE pair clustering");

        disableLEPairClusteringCutLabel.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        disableLEPairClusteringCutLabel.setText("[-le_union]");
        disableLEPairClusteringCutLabel.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);

        disableLEPairClusteringYesNoComboBox.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        disableLEPairClusteringYesNoComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "No", "Yes" }));

        javax.swing.GroupLayout le_unionPanelLayout = new javax.swing.GroupLayout(le_unionPanel);
        le_unionPanel.setLayout(le_unionPanelLayout);
        le_unionPanelLayout.setHorizontalGroup(
            le_unionPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(le_unionPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(disableLEPairClusteringLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 242, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(disableLEPairClusteringCutLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(disableLEPairClusteringYesNoComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(314, Short.MAX_VALUE))
        );
        le_unionPanelLayout.setVerticalGroup(
            le_unionPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(le_unionPanelLayout.createSequentialGroup()
                .addGroup(le_unionPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(disableLEPairClusteringYesNoComboBox, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(disableLEPairClusteringCutLabel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(disableLEPairClusteringLabel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(0, 4, Short.MAX_VALUE))
        );

        addParameter("-le_union", disableLEPairClusteringYesNoComboBox, null);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 304;
        gridBagConstraints.ipady = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(11, 0, 0, 10);
        parametersPanel.add(le_unionPanel, gridBagConstraints);

        lePanel.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        lePanel.setMaximumSize(new java.awt.Dimension(600, 800));
        lePanel.setPreferredSize(new java.awt.Dimension(600, 31));

        useLEClusteringFileLabel.setFont(new java.awt.Font("Segoe UI", 0, 12)); // NOI18N
        useLEClusteringFileLabel.setText("Use LEs clustering file");

        useLEClusteringFileCutLabel.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        useLEClusteringFileCutLabel.setText("[-le]");
        useLEClusteringFileCutLabel.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);

        useLEClusteringFileYesNoComboBox.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        useLEClusteringFileYesNoComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "No", "Yes" }));

        useLEClusteringFileTextField.setFont(new java.awt.Font("Segoe UI", 0, 12)); // NOI18N

        useLEClusteringFileOpenButton.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        useLEClusteringFileOpenButton.setText("Open");
        useLEClusteringFileOpenButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                useLEClusteringFileOpenButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout lePanelLayout = new javax.swing.GroupLayout(lePanel);
        lePanel.setLayout(lePanelLayout);
        lePanelLayout.setHorizontalGroup(
            lePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(lePanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(useLEClusteringFileLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 242, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(useLEClusteringFileCutLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(useLEClusteringFileYesNoComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(useLEClusteringFileTextField, javax.swing.GroupLayout.DEFAULT_SIZE, 188, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(useLEClusteringFileOpenButton, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        lePanelLayout.setVerticalGroup(
            lePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(lePanelLayout.createSequentialGroup()
                .addGroup(lePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(useLEClusteringFileOpenButton, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(useLEClusteringFileTextField, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(useLEClusteringFileYesNoComboBox, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(useLEClusteringFileCutLabel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(useLEClusteringFileLabel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(0, 4, Short.MAX_VALUE))
        );

        addParameter("-le", useLEClusteringFileYesNoComboBox, null);
        addParameter("-le", null, useLEClusteringFileTextField);
        addButton("-le", useLEClusteringFileOpenButton);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 182;
        gridBagConstraints.ipady = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(11, 0, 0, 10);
        parametersPanel.add(lePanel, gridBagConstraints);

        pPanel.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        pPanel.setMaximumSize(new java.awt.Dimension(600, 800));
        pPanel.setPreferredSize(new java.awt.Dimension(683, 24));

        useExistingPlacementLabel.setFont(new java.awt.Font("Segoe UI", 0, 12)); // NOI18N
        useExistingPlacementLabel.setText("Use existing placement file");

        useExistingPlacementCutLabel.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        useExistingPlacementCutLabel.setText("[-p]");
        useExistingPlacementCutLabel.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);

        useExistingPlacementYesNoComboBox.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        useExistingPlacementYesNoComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "No", "Yes" }));

        useExistingPlacementTextField.setFont(new java.awt.Font("Segoe UI", 0, 12)); // NOI18N

        useExistingPlacementOpenButton.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        useExistingPlacementOpenButton.setText("Open");
        useExistingPlacementOpenButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                useExistingPlacementOpenButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pPanelLayout = new javax.swing.GroupLayout(pPanel);
        pPanel.setLayout(pPanelLayout);
        pPanelLayout.setHorizontalGroup(
            pPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(useExistingPlacementLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 242, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(useExistingPlacementCutLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(useExistingPlacementYesNoComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(useExistingPlacementTextField, javax.swing.GroupLayout.DEFAULT_SIZE, 188, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(useExistingPlacementOpenButton, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        pPanelLayout.setVerticalGroup(
            pPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pPanelLayout.createSequentialGroup()
                .addComponent(useExistingPlacementLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
            .addGroup(pPanelLayout.createSequentialGroup()
                .addGroup(pPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(useExistingPlacementOpenButton, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(useExistingPlacementTextField, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(useExistingPlacementYesNoComboBox, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(useExistingPlacementCutLabel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(0, 0, Short.MAX_VALUE))
        );

        addParameter("-p", useExistingPlacementYesNoComboBox, null);
        addParameter("-p", null, useExistingPlacementTextField);
        addButton("-p", useExistingPlacementOpenButton);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 182;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(11, 0, 0, 10);
        parametersPanel.add(pPanel, gridBagConstraints);

        saPanel.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        saPanel.setMaximumSize(new java.awt.Dimension(600, 34));

        disableSimulatingAnnealingLabel.setFont(new java.awt.Font("Segoe UI", 0, 12)); // NOI18N
        disableSimulatingAnnealingLabel.setText("Disable Simulating annealing");

        disableSimulatingAnnealingCutLabel.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        disableSimulatingAnnealingCutLabel.setText("[-sa]");
        disableSimulatingAnnealingCutLabel.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);

        disableSimulatingAnnealingYesNoComboBox.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        disableSimulatingAnnealingYesNoComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "No", "Yes" }));

        javax.swing.GroupLayout saPanelLayout = new javax.swing.GroupLayout(saPanel);
        saPanel.setLayout(saPanelLayout);
        saPanelLayout.setHorizontalGroup(
            saPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(saPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(disableSimulatingAnnealingLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 242, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(disableSimulatingAnnealingCutLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(disableSimulatingAnnealingYesNoComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(314, Short.MAX_VALUE))
        );
        saPanelLayout.setVerticalGroup(
            saPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(saPanelLayout.createSequentialGroup()
                .addGroup(saPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(disableSimulatingAnnealingCutLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(disableSimulatingAnnealingYesNoComboBox, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, saPanelLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(disableSimulatingAnnealingLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );

        addParameter("-sa", disableSimulatingAnnealingYesNoComboBox, null);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 304;
        gridBagConstraints.ipady = -4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(11, 0, 0, 10);
        parametersPanel.add(saPanel, gridBagConstraints);

        replacePanel.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        replacePanel.setMaximumSize(new java.awt.Dimension(600, 800));
        replacePanel.setPreferredSize(new java.awt.Dimension(683, 24));

        replaceLabel.setFont(new java.awt.Font("Segoe UI", 0, 12)); // NOI18N
        replaceLabel.setText("Enable auto replacement for IO cells");
        replaceLabel.setPreferredSize(new java.awt.Dimension(223, 20));

        replaceCutLabel.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        replaceCutLabel.setText("[-replace]");
        replaceCutLabel.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        replaceCutLabel.setPreferredSize(null);

        replaceYesNoComboBox.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        replaceYesNoComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "No", "Yes" }));

        javax.swing.GroupLayout replacePanelLayout = new javax.swing.GroupLayout(replacePanel);
        replacePanel.setLayout(replacePanelLayout);
        replacePanelLayout.setHorizontalGroup(
            replacePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(replacePanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(replaceLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 242, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(replaceCutLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(replaceYesNoComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(314, Short.MAX_VALUE))
        );
        replacePanelLayout.setVerticalGroup(
            replacePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(replacePanelLayout.createSequentialGroup()
                .addGroup(replacePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(replaceYesNoComboBox, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(replaceCutLabel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(replaceLabel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(0, 4, Short.MAX_VALUE))
        );

        addParameter("-replace", replaceYesNoComboBox, null);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 10;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 304;
        gridBagConstraints.ipady = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(11, 0, 0, 10);
        parametersPanel.add(replacePanel, gridBagConstraints);

        outputFileNamesPanel2.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        outputFileNamesPanel2.setMaximumSize(new java.awt.Dimension(600, 800));
        outputFileNamesPanel2.setPreferredSize(new java.awt.Dimension(683, 24));

        sa_lmLabel.setFont(new java.awt.Font("Segoe UI", 0, 12)); // NOI18N
        sa_lmLabel.setText("Simulating annealing inner loop multiplier");
        sa_lmLabel.setPreferredSize(new java.awt.Dimension(223, 20));

        sa_lmCutLabel.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        sa_lmCutLabel.setText("[-sa_lm]");
        sa_lmCutLabel.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        sa_lmCutLabel.setPreferredSize(null);

        sa_lmYesNoComboBox.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        sa_lmYesNoComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "No", "Yes" }));

        sa_lmTextField.setFont(new java.awt.Font("Segoe UI", 0, 12)); // NOI18N
        sa_lmTextField.setText("300");

        javax.swing.GroupLayout outputFileNamesPanel2Layout = new javax.swing.GroupLayout(outputFileNamesPanel2);
        outputFileNamesPanel2.setLayout(outputFileNamesPanel2Layout);
        outputFileNamesPanel2Layout.setHorizontalGroup(
            outputFileNamesPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(outputFileNamesPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(sa_lmLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 242, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(sa_lmCutLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(sa_lmYesNoComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(sa_lmTextField, javax.swing.GroupLayout.DEFAULT_SIZE, 298, Short.MAX_VALUE)
                .addContainerGap())
        );
        outputFileNamesPanel2Layout.setVerticalGroup(
            outputFileNamesPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(outputFileNamesPanel2Layout.createSequentialGroup()
                .addGroup(outputFileNamesPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(sa_lmTextField, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(sa_lmYesNoComboBox, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(sa_lmCutLabel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(sa_lmLabel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(0, 4, Short.MAX_VALUE))
        );

        addParameter("-sa_lm", sa_lmYesNoComboBox, null);
        addParameter("-sa_lm", null, sa_lmTextField);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 292;
        gridBagConstraints.ipady = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(11, 0, 0, 10);
        parametersPanel.add(outputFileNamesPanel2, gridBagConstraints);

        orderPanel.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        orderPanel.setMaximumSize(new java.awt.Dimension(600, 800));
        orderPanel.setPreferredSize(new java.awt.Dimension(683, 24));

        orderLabel.setFont(new java.awt.Font("Segoe UI", 0, 12)); // NOI18N
        orderLabel.setText("Use net routing ordering file");
        orderLabel.setPreferredSize(new java.awt.Dimension(223, 20));

        orderCutLabel.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        orderCutLabel.setText("[-order]");
        orderCutLabel.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        orderCutLabel.setPreferredSize(null);

        orderYesNoComboBox.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        orderYesNoComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "No", "Yes" }));

        orderTextField.setFont(new java.awt.Font("Segoe UI", 0, 12)); // NOI18N

        javax.swing.GroupLayout orderPanelLayout = new javax.swing.GroupLayout(orderPanel);
        orderPanel.setLayout(orderPanelLayout);
        orderPanelLayout.setHorizontalGroup(
            orderPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(orderPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(orderLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 242, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(orderCutLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(orderYesNoComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(orderTextField, javax.swing.GroupLayout.DEFAULT_SIZE, 298, Short.MAX_VALUE)
                .addContainerGap())
        );
        orderPanelLayout.setVerticalGroup(
            orderPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(orderPanelLayout.createSequentialGroup()
                .addGroup(orderPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(orderTextField, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(orderYesNoComboBox, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(orderCutLabel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(orderLabel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(0, 4, Short.MAX_VALUE))
        );

        addParameter("-order", orderYesNoComboBox, null);
        addParameter("-order", null, orderTextField);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 11;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 292;
        gridBagConstraints.ipady = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(11, 0, 0, 10);
        parametersPanel.add(orderPanel, gridBagConstraints);

        v_hPanel.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        v_hPanel.setMaximumSize(new java.awt.Dimension(600, 800));
        v_hPanel.setPreferredSize(new java.awt.Dimension(683, 24));

        v_hLabel.setFont(new java.awt.Font("Segoe UI", 0, 12)); // NOI18N
        v_hLabel.setText("PathFind: Vertex history cost factor");
        v_hLabel.setPreferredSize(new java.awt.Dimension(223, 20));

        v_hCutLabel.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        v_hCutLabel.setText("[-v_h]");
        v_hCutLabel.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        v_hCutLabel.setPreferredSize(null);

        v_hYesNoComboBox.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        v_hYesNoComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "No", "Yes" }));

        v_hTextField.setFont(new java.awt.Font("Segoe UI", 0, 12)); // NOI18N
        v_hTextField.setText("0.10");

        javax.swing.GroupLayout v_hPanelLayout = new javax.swing.GroupLayout(v_hPanel);
        v_hPanel.setLayout(v_hPanelLayout);
        v_hPanelLayout.setHorizontalGroup(
            v_hPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(v_hPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(v_hLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 242, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(v_hCutLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(v_hYesNoComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(v_hTextField, javax.swing.GroupLayout.DEFAULT_SIZE, 298, Short.MAX_VALUE)
                .addContainerGap())
        );
        v_hPanelLayout.setVerticalGroup(
            v_hPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(v_hPanelLayout.createSequentialGroup()
                .addGroup(v_hPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(v_hTextField, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(v_hYesNoComboBox, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(v_hCutLabel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(v_hLabel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(0, 4, Short.MAX_VALUE))
        );

        addParameter("-v_h", v_hYesNoComboBox, null);
        addParameter("-v_h", null, v_hTextField);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 12;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 292;
        gridBagConstraints.ipady = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(11, 0, 0, 10);
        parametersPanel.add(v_hPanel, gridBagConstraints);

        path_wPanel.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        path_wPanel.setMaximumSize(new java.awt.Dimension(600, 800));
        path_wPanel.setPreferredSize(new java.awt.Dimension(683, 24));

        path_wLabel.setFont(new java.awt.Font("Segoe UI", 0, 12)); // NOI18N
        path_wLabel.setText("PathFind: Maximum path weight");
        path_wLabel.setPreferredSize(new java.awt.Dimension(223, 20));

        path_wCutLabel.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        path_wCutLabel.setText("[-path_w]");
        path_wCutLabel.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        path_wCutLabel.setPreferredSize(null);

        path_wYesNoComboBox.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        path_wYesNoComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "No", "Yes" }));

        path_wTextField.setFont(new java.awt.Font("Segoe UI", 0, 12)); // NOI18N
        path_wTextField.setText("300");

        javax.swing.GroupLayout path_wPanelLayout = new javax.swing.GroupLayout(path_wPanel);
        path_wPanel.setLayout(path_wPanelLayout);
        path_wPanelLayout.setHorizontalGroup(
            path_wPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(path_wPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(path_wLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 242, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(path_wCutLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(path_wYesNoComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(path_wTextField, javax.swing.GroupLayout.DEFAULT_SIZE, 298, Short.MAX_VALUE)
                .addContainerGap())
        );
        path_wPanelLayout.setVerticalGroup(
            path_wPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(path_wPanelLayout.createSequentialGroup()
                .addGroup(path_wPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(path_wTextField, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(path_wYesNoComboBox, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(path_wCutLabel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(path_wLabel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(0, 4, Short.MAX_VALUE))
        );

        addParameter("-path_w", path_wYesNoComboBox, null);
        addParameter("-path_w", null, path_wTextField);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 14;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 292;
        gridBagConstraints.ipady = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(11, 0, 0, 10);
        parametersPanel.add(path_wPanel, gridBagConstraints);

        v_pPanel.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        v_pPanel.setMaximumSize(new java.awt.Dimension(600, 800));
        v_pPanel.setPreferredSize(new java.awt.Dimension(683, 24));

        v_pLabel.setFont(new java.awt.Font("Segoe UI", 0, 12)); // NOI18N
        v_pLabel.setText("PathFind: Vertex penalty cost factor");
        v_pLabel.setPreferredSize(new java.awt.Dimension(223, 20));

        v_pCutLabel.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        v_pCutLabel.setText("[-v_p]");
        v_pCutLabel.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        v_pCutLabel.setPreferredSize(null);

        v_pYesNoComboBox.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        v_pYesNoComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "No", "Yes" }));

        v_pTextField.setFont(new java.awt.Font("Segoe UI", 0, 12)); // NOI18N
        v_pTextField.setText("0.50");

        javax.swing.GroupLayout v_pPanelLayout = new javax.swing.GroupLayout(v_pPanel);
        v_pPanel.setLayout(v_pPanelLayout);
        v_pPanelLayout.setHorizontalGroup(
            v_pPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(v_pPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(v_pLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 242, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(v_pCutLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(v_pYesNoComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(v_pTextField, javax.swing.GroupLayout.DEFAULT_SIZE, 298, Short.MAX_VALUE)
                .addContainerGap())
        );
        v_pPanelLayout.setVerticalGroup(
            v_pPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(v_pPanelLayout.createSequentialGroup()
                .addGroup(v_pPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(v_pTextField, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(v_pYesNoComboBox, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(v_pCutLabel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(v_pLabel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(0, 4, Short.MAX_VALUE))
        );

        addParameter("-v_p", v_pYesNoComboBox, null);
        addParameter("-v_p", null, v_pTextField);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 13;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 292;
        gridBagConstraints.ipady = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(11, 0, 0, 10);
        parametersPanel.add(v_pPanel, gridBagConstraints);

        path_lPanel.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        path_lPanel.setMaximumSize(new java.awt.Dimension(600, 800));
        path_lPanel.setPreferredSize(new java.awt.Dimension(683, 24));

        path_lLabel.setFont(new java.awt.Font("Segoe UI", 0, 12)); // NOI18N
        path_lLabel.setText("PathFind: Maxumum path length");
        path_lLabel.setPreferredSize(new java.awt.Dimension(223, 20));

        path_lCutLabel.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        path_lCutLabel.setText("[-path_l]");
        path_lCutLabel.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        path_lCutLabel.setPreferredSize(null);

        path_lYesNoComboBox.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        path_lYesNoComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "No", "Yes" }));

        path_lTextField.setFont(new java.awt.Font("Segoe UI", 0, 12)); // NOI18N
        path_lTextField.setText("250");

        javax.swing.GroupLayout path_lPanelLayout = new javax.swing.GroupLayout(path_lPanel);
        path_lPanel.setLayout(path_lPanelLayout);
        path_lPanelLayout.setHorizontalGroup(
            path_lPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(path_lPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(path_lLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 242, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(path_lCutLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(path_lYesNoComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(path_lTextField, javax.swing.GroupLayout.DEFAULT_SIZE, 298, Short.MAX_VALUE)
                .addContainerGap())
        );
        path_lPanelLayout.setVerticalGroup(
            path_lPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(path_lPanelLayout.createSequentialGroup()
                .addGroup(path_lPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(path_lTextField, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(path_lYesNoComboBox, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(path_lCutLabel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(path_lLabel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(0, 4, Short.MAX_VALUE))
        );

        addParameter("-path_l", path_lYesNoComboBox, null);
        addParameter("-path_l", null, path_lTextField);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 15;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 292;
        gridBagConstraints.ipady = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(11, 0, 0, 10);
        parametersPanel.add(path_lPanel, gridBagConstraints);

        clk_1Panel.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        clk_1Panel.setMaximumSize(new java.awt.Dimension(600, 800));
        clk_1Panel.setPreferredSize(new java.awt.Dimension(683, 24));

        clk_1Label.setFont(new java.awt.Font("Segoe UI", 0, 12)); // NOI18N
        clk_1Label.setText("Set CLK node to GCLK_1");
        clk_1Label.setPreferredSize(new java.awt.Dimension(223, 20));

        clk_1CutLabel3.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        clk_1CutLabel3.setText("[-clk_1]");
        clk_1CutLabel3.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        clk_1CutLabel3.setPreferredSize(null);

        clk_1YesNoComboBox.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        clk_1YesNoComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "No", "Yes" }));

        clk_1TextField.setFont(new java.awt.Font("Segoe UI", 0, 12)); // NOI18N

        javax.swing.GroupLayout clk_1PanelLayout = new javax.swing.GroupLayout(clk_1Panel);
        clk_1Panel.setLayout(clk_1PanelLayout);
        clk_1PanelLayout.setHorizontalGroup(
            clk_1PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(clk_1PanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(clk_1Label, javax.swing.GroupLayout.PREFERRED_SIZE, 242, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(clk_1CutLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(clk_1YesNoComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(clk_1TextField, javax.swing.GroupLayout.DEFAULT_SIZE, 298, Short.MAX_VALUE)
                .addContainerGap())
        );
        clk_1PanelLayout.setVerticalGroup(
            clk_1PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(clk_1PanelLayout.createSequentialGroup()
                .addGroup(clk_1PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(clk_1TextField, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(clk_1YesNoComboBox, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(clk_1CutLabel3, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(clk_1Label, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(0, 4, Short.MAX_VALUE))
        );

        addParameter("-clk_1", clk_1YesNoComboBox, null);
        addParameter("-clk_1", null, clk_1TextField);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 16;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 292;
        gridBagConstraints.ipady = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(6, 0, 0, 10);
        parametersPanel.add(clk_1Panel, gridBagConstraints);

        clk_4Panel.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        clk_4Panel.setMaximumSize(new java.awt.Dimension(600, 800));
        clk_4Panel.setPreferredSize(new java.awt.Dimension(683, 24));

        clk_4Label.setFont(new java.awt.Font("Segoe UI", 0, 12)); // NOI18N
        clk_4Label.setText("Set CLK node to GCLK_4");
        clk_4Label.setPreferredSize(new java.awt.Dimension(223, 20));

        clk_4CutLabel.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        clk_4CutLabel.setText("[-clk_4]");
        clk_4CutLabel.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        clk_4CutLabel.setPreferredSize(null);

        clk_4YesNoComboBox.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        clk_4YesNoComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "No", "Yes" }));

        clk_4TextField.setFont(new java.awt.Font("Segoe UI", 0, 12)); // NOI18N

        javax.swing.GroupLayout clk_4PanelLayout = new javax.swing.GroupLayout(clk_4Panel);
        clk_4Panel.setLayout(clk_4PanelLayout);
        clk_4PanelLayout.setHorizontalGroup(
            clk_4PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(clk_4PanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(clk_4Label, javax.swing.GroupLayout.PREFERRED_SIZE, 242, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(clk_4CutLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(clk_4YesNoComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(clk_4TextField, javax.swing.GroupLayout.DEFAULT_SIZE, 298, Short.MAX_VALUE)
                .addContainerGap())
        );
        clk_4PanelLayout.setVerticalGroup(
            clk_4PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(clk_4PanelLayout.createSequentialGroup()
                .addGroup(clk_4PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(clk_4TextField, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(clk_4YesNoComboBox, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(clk_4CutLabel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(clk_4Label, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(0, 4, Short.MAX_VALUE))
        );

        addParameter("-clk_4", clk_4YesNoComboBox, null);
        addParameter("-clk_4", null, clk_4TextField);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 19;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 292;
        gridBagConstraints.ipady = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(11, 0, 41, 10);
        parametersPanel.add(clk_4Panel, gridBagConstraints);

        clk_3Panel.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        clk_3Panel.setMaximumSize(new java.awt.Dimension(600, 800));
        clk_3Panel.setPreferredSize(new java.awt.Dimension(683, 24));

        clk_3Label.setFont(new java.awt.Font("Segoe UI", 0, 12)); // NOI18N
        clk_3Label.setText("Set CLK node to GCLK_3");
        clk_3Label.setPreferredSize(new java.awt.Dimension(223, 20));

        clk_3CutLabel.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        clk_3CutLabel.setText("[-clk_3]");
        clk_3CutLabel.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        clk_3CutLabel.setPreferredSize(null);

        clk_3YesNoComboBox.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        clk_3YesNoComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "No", "Yes" }));

        clk_3TextField.setFont(new java.awt.Font("Segoe UI", 0, 12)); // NOI18N

        javax.swing.GroupLayout clk_3PanelLayout = new javax.swing.GroupLayout(clk_3Panel);
        clk_3Panel.setLayout(clk_3PanelLayout);
        clk_3PanelLayout.setHorizontalGroup(
            clk_3PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(clk_3PanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(clk_3Label, javax.swing.GroupLayout.PREFERRED_SIZE, 242, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(clk_3CutLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(clk_3YesNoComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(clk_3TextField, javax.swing.GroupLayout.DEFAULT_SIZE, 298, Short.MAX_VALUE)
                .addContainerGap())
        );
        clk_3PanelLayout.setVerticalGroup(
            clk_3PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(clk_3PanelLayout.createSequentialGroup()
                .addGroup(clk_3PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(clk_3TextField, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(clk_3YesNoComboBox, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(clk_3CutLabel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(clk_3Label, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(0, 4, Short.MAX_VALUE))
        );

        addParameter("-clk_3", clk_3YesNoComboBox, null);
        addParameter("-clk_3", null, clk_3TextField);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 18;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 292;
        gridBagConstraints.ipady = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(11, 0, 0, 10);
        parametersPanel.add(clk_3Panel, gridBagConstraints);

        clk_2Panel.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        clk_2Panel.setMaximumSize(new java.awt.Dimension(600, 800));
        clk_2Panel.setPreferredSize(new java.awt.Dimension(683, 24));

        clk_2Label.setFont(new java.awt.Font("Segoe UI", 0, 12)); // NOI18N
        clk_2Label.setText("Set CLK node to GCLK_2");
        clk_2Label.setPreferredSize(new java.awt.Dimension(223, 20));

        clk_2CutLabel.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        clk_2CutLabel.setText("[-clk_2]");
        clk_2CutLabel.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        clk_2CutLabel.setPreferredSize(null);

        clk_2YesNoComboBox.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        clk_2YesNoComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "No", "Yes" }));

        clk_2TextField.setFont(new java.awt.Font("Segoe UI", 0, 12)); // NOI18N

        javax.swing.GroupLayout clk_2PanelLayout = new javax.swing.GroupLayout(clk_2Panel);
        clk_2Panel.setLayout(clk_2PanelLayout);
        clk_2PanelLayout.setHorizontalGroup(
            clk_2PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(clk_2PanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(clk_2Label, javax.swing.GroupLayout.PREFERRED_SIZE, 242, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(clk_2CutLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(clk_2YesNoComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(clk_2TextField, javax.swing.GroupLayout.DEFAULT_SIZE, 298, Short.MAX_VALUE)
                .addContainerGap())
        );
        clk_2PanelLayout.setVerticalGroup(
            clk_2PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(clk_2PanelLayout.createSequentialGroup()
                .addGroup(clk_2PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(clk_2TextField, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(clk_2YesNoComboBox, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(clk_2CutLabel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(clk_2Label, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(0, 4, Short.MAX_VALUE))
        );

        addParameter("-clk_2", clk_2YesNoComboBox, null);
        addParameter("-clk_2", null, clk_2TextField);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 17;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 292;
        gridBagConstraints.ipady = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(11, 0, 0, 10);
        parametersPanel.add(clk_2Panel, gridBagConstraints);

        outputFileNamesPanel4.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        outputFileNamesPanel4.setMaximumSize(new java.awt.Dimension(600, 800));
        outputFileNamesPanel4.setPreferredSize(new java.awt.Dimension(600, 31));

        outputFileNamesLabel.setFont(new java.awt.Font("Segoe UI", 0, 12)); // NOI18N
        outputFileNamesLabel.setText("Output file names");

        outputFileNamesCutLabel.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        outputFileNamesCutLabel.setText("[-o]");
        outputFileNamesCutLabel.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);

        outputFileNamesYesNoComboBox.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        outputFileNamesYesNoComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "No", "Yes" }));

        outputFileNamesTextField.setFont(new java.awt.Font("Segoe UI", 0, 12)); // NOI18N

        javax.swing.GroupLayout outputFileNamesPanel4Layout = new javax.swing.GroupLayout(outputFileNamesPanel4);
        outputFileNamesPanel4.setLayout(outputFileNamesPanel4Layout);
        outputFileNamesPanel4Layout.setHorizontalGroup(
            outputFileNamesPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(outputFileNamesPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(outputFileNamesLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 242, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(outputFileNamesCutLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(outputFileNamesYesNoComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(outputFileNamesTextField, javax.swing.GroupLayout.DEFAULT_SIZE, 298, Short.MAX_VALUE)
                .addContainerGap())
        );
        outputFileNamesPanel4Layout.setVerticalGroup(
            outputFileNamesPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(outputFileNamesPanel4Layout.createSequentialGroup()
                .addGroup(outputFileNamesPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(outputFileNamesYesNoComboBox, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(outputFileNamesCutLabel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(outputFileNamesTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, outputFileNamesPanel4Layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(outputFileNamesLabel)
                .addGap(15, 15, 15))
        );

        addParameter("-0", outputFileNamesYesNoComboBox, null);
        addParameter("-0", null, topModuleTextField);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 292;
        gridBagConstraints.ipady = -7;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(6, 0, 0, 10);
        parametersPanel.add(outputFileNamesPanel4, gridBagConstraints);

        outputFileNamesPanel5.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        outputFileNamesPanel5.setMaximumSize(new java.awt.Dimension(600, 800));
        outputFileNamesPanel5.setPreferredSize(new java.awt.Dimension(600, 31));

        outputFileNamesLabel3.setFont(new java.awt.Font("Segoe UI", 0, 12)); // NOI18N
        outputFileNamesLabel3.setText("Output file names");

        outputFileNamesCutLabel2.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        outputFileNamesCutLabel2.setText("[-o]");
        outputFileNamesCutLabel2.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);

        outputFileNamesYesNoComboBox2.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        outputFileNamesYesNoComboBox2.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "No", "Yes" }));

        outputFileNamesTextField2.setFont(new java.awt.Font("Segoe UI", 0, 12)); // NOI18N

        javax.swing.GroupLayout outputFileNamesPanel5Layout = new javax.swing.GroupLayout(outputFileNamesPanel5);
        outputFileNamesPanel5.setLayout(outputFileNamesPanel5Layout);
        outputFileNamesPanel5Layout.setHorizontalGroup(
            outputFileNamesPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(outputFileNamesPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(outputFileNamesLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 242, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(outputFileNamesCutLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(outputFileNamesYesNoComboBox2, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(outputFileNamesTextField2, javax.swing.GroupLayout.DEFAULT_SIZE, 298, Short.MAX_VALUE)
                .addContainerGap())
        );
        outputFileNamesPanel5Layout.setVerticalGroup(
            outputFileNamesPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(outputFileNamesPanel5Layout.createSequentialGroup()
                .addGroup(outputFileNamesPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(outputFileNamesYesNoComboBox2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(outputFileNamesCutLabel2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(outputFileNamesTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, outputFileNamesPanel5Layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(outputFileNamesLabel3)
                .addGap(15, 15, 15))
        );

        addParameter("-0", outputFileNamesYesNoComboBox, null);
        addParameter("-0", null, topModuleTextField);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.ipadx = 292;
        gridBagConstraints.ipady = -7;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 0, 0, 10);
        parametersPanel.add(outputFileNamesPanel5, gridBagConstraints);

        mainScrollPanel.setViewportView(parametersPanel);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = 729;
        gridBagConstraints.ipady = 731;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(6, 0, 0, 0);
        getContentPane().add(mainScrollPanel, gridBagConstraints);

        okButton.setFont(new java.awt.Font("Segoe UI", 0, 12)); // NOI18N
        okButton.setText("Ok");
        okButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                okButtonActionPerformed(evt);
            }
        });

        cancelButton.setFont(new java.awt.Font("Segoe UI", 0, 12)); // NOI18N
        cancelButton.setText("Reset");
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout okCancelButtonPanelLayout = new javax.swing.GroupLayout(okCancelButtonPanel);
        okCancelButtonPanel.setLayout(okCancelButtonPanelLayout);
        okCancelButtonPanelLayout.setHorizontalGroup(
            okCancelButtonPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, okCancelButtonPanelLayout.createSequentialGroup()
                .addContainerGap(544, Short.MAX_VALUE)
                .addComponent(okButton, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cancelButton, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        okCancelButtonPanelLayout.setVerticalGroup(
            okCancelButtonPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, okCancelButtonPanelLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(okCancelButtonPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cancelButton)
                    .addComponent(okButton))
                .addContainerGap())
        );

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = 526;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 0, 11, 0);
        getContentPane().add(okCancelButtonPanel, gridBagConstraints);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelButtonActionPerformed
        this.dispose();
        JFrame jf = null;
        if (TopLevel.isMDIMode()) {
            jf = TopLevel.getCurrentJFrame();
        }
        fpgaUI = new FpgaArgumentsUI(jf);
        fpgaUI.setVisible(true);
    }//GEN-LAST:event_cancelButtonActionPerformed

    private void okButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_okButtonActionPerformed
        this.dispose();
    }//GEN-LAST:event_okButtonActionPerformed

    private void useIOPlacementFileOpenButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_useIOPlacementFileOpenButtonActionPerformed
        setTextByOpenButtonToParameterObject(useIOPlacementFileOpenButton);
    }//GEN-LAST:event_useIOPlacementFileOpenButtonActionPerformed

    private void useLEClusteringFileOpenButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_useLEClusteringFileOpenButtonActionPerformed
        setTextByOpenButtonToParameterObject(useLEClusteringFileOpenButton);
    }//GEN-LAST:event_useLEClusteringFileOpenButtonActionPerformed

    private void useExistingPlacementOpenButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_useExistingPlacementOpenButtonActionPerformed
        setTextByOpenButtonToParameterObject(useExistingPlacementOpenButton);
    }//GEN-LAST:event_useExistingPlacementOpenButtonActionPerformed

    private void topModuleTextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_topModuleTextFieldActionPerformed
        
    }//GEN-LAST:event_topModuleTextFieldActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton cancelButton;
    private javax.swing.JLabel clk_1CutLabel3;
    private javax.swing.JLabel clk_1Label;
    private javax.swing.JPanel clk_1Panel;
    private javax.swing.JTextField clk_1TextField;
    private javax.swing.JComboBox<String> clk_1YesNoComboBox;
    private javax.swing.JLabel clk_2CutLabel;
    private javax.swing.JLabel clk_2Label;
    private javax.swing.JPanel clk_2Panel;
    private javax.swing.JTextField clk_2TextField;
    private javax.swing.JComboBox<String> clk_2YesNoComboBox;
    private javax.swing.JLabel clk_3CutLabel;
    private javax.swing.JLabel clk_3Label;
    private javax.swing.JPanel clk_3Panel;
    private javax.swing.JTextField clk_3TextField;
    private javax.swing.JComboBox<String> clk_3YesNoComboBox;
    private javax.swing.JLabel clk_4CutLabel;
    private javax.swing.JLabel clk_4Label;
    private javax.swing.JPanel clk_4Panel;
    private javax.swing.JTextField clk_4TextField;
    private javax.swing.JComboBox<String> clk_4YesNoComboBox;
    private javax.swing.JLabel disableLEPairClusteringCutLabel;
    private javax.swing.JLabel disableLEPairClusteringLabel;
    private javax.swing.JComboBox<String> disableLEPairClusteringYesNoComboBox;
    private javax.swing.JLabel disableSimulatingAnnealingCutLabel;
    private javax.swing.JLabel disableSimulatingAnnealingLabel;
    private javax.swing.JComboBox<String> disableSimulatingAnnealingYesNoComboBox;
    private javax.swing.JPanel labelPanel;
    private javax.swing.JPanel lePanel;
    private javax.swing.JPanel le_unionPanel;
    private javax.swing.JLabel mainLabel;
    private javax.swing.JScrollPane mainScrollPanel;
    private javax.swing.JButton okButton;
    private javax.swing.JPanel okCancelButtonPanel;
    private javax.swing.JLabel orderCutLabel;
    private javax.swing.JLabel orderLabel;
    private javax.swing.JPanel orderPanel;
    private javax.swing.JTextField orderTextField;
    private javax.swing.JComboBox<String> orderYesNoComboBox;
    private javax.swing.JLabel outputFileNamesCutLabel;
    private javax.swing.JLabel outputFileNamesCutLabel2;
    private javax.swing.JLabel outputFileNamesLabel;
    private javax.swing.JLabel outputFileNamesLabel1;
    private javax.swing.JLabel outputFileNamesLabel3;
    private javax.swing.JPanel outputFileNamesPanel2;
    private javax.swing.JPanel outputFileNamesPanel4;
    private javax.swing.JPanel outputFileNamesPanel5;
    private javax.swing.JTextField outputFileNamesTextField;
    private javax.swing.JTextField outputFileNamesTextField2;
    private javax.swing.JComboBox<String> outputFileNamesYesNoComboBox;
    private javax.swing.JComboBox<String> outputFileNamesYesNoComboBox2;
    private javax.swing.JPanel pPanel;
    private javax.swing.JPanel parametersPanel;
    private javax.swing.JLabel path_lCutLabel;
    private javax.swing.JLabel path_lLabel;
    private javax.swing.JPanel path_lPanel;
    private javax.swing.JTextField path_lTextField;
    private javax.swing.JComboBox<String> path_lYesNoComboBox;
    private javax.swing.JLabel path_wCutLabel;
    private javax.swing.JLabel path_wLabel;
    private javax.swing.JPanel path_wPanel;
    private javax.swing.JTextField path_wTextField;
    private javax.swing.JComboBox<String> path_wYesNoComboBox;
    private javax.swing.JLabel replaceCutLabel;
    private javax.swing.JLabel replaceLabel;
    private javax.swing.JPanel replacePanel;
    private javax.swing.JComboBox<String> replaceYesNoComboBox;
    private javax.swing.JPanel saPanel;
    private javax.swing.JLabel sa_lmCutLabel;
    private javax.swing.JLabel sa_lmLabel;
    private javax.swing.JTextField sa_lmTextField;
    private javax.swing.JComboBox<String> sa_lmYesNoComboBox;
    private javax.swing.JLabel topModuleLabel;
    private javax.swing.JPanel topModulePanel;
    private javax.swing.JTextField topModuleTextField;
    private javax.swing.JLabel turnOffAdditionalIOCellsCutLabel;
    private javax.swing.JLabel turnOffAdditionalIOCellsLabel;
    private javax.swing.JPanel turnOffAdditionalIOCellsPanel;
    private javax.swing.JComboBox<String> turnOffAdditionalIOCellsYesNoComboBox;
    private javax.swing.JLabel turnOffLogicCutLabel;
    private javax.swing.JPanel turnOffLogicPanel;
    private javax.swing.JTextField turnOffLogicTextField1;
    private javax.swing.JComboBox<String> turnOffLogicYesNoComboBox;
    private javax.swing.JLabel useExistingPlacementCutLabel;
    private javax.swing.JLabel useExistingPlacementLabel;
    private javax.swing.JButton useExistingPlacementOpenButton;
    private javax.swing.JTextField useExistingPlacementTextField;
    private javax.swing.JComboBox<String> useExistingPlacementYesNoComboBox;
    private javax.swing.JLabel useIOPlacementFileCutLabel;
    private javax.swing.JLabel useIOPlacementFileLabel;
    private javax.swing.JButton useIOPlacementFileOpenButton;
    private javax.swing.JPanel useIOPlacementFilePanel;
    private javax.swing.JTextField useIOPlacementFileTextField;
    private javax.swing.JComboBox<String> useIOPlacementFileYesNoComboBox;
    private javax.swing.JLabel useLEClusteringFileCutLabel;
    private javax.swing.JLabel useLEClusteringFileLabel;
    private javax.swing.JButton useLEClusteringFileOpenButton;
    private javax.swing.JTextField useLEClusteringFileTextField;
    private javax.swing.JComboBox<String> useLEClusteringFileYesNoComboBox;
    private javax.swing.JLabel v_hCutLabel;
    private javax.swing.JLabel v_hLabel;
    private javax.swing.JPanel v_hPanel;
    private javax.swing.JTextField v_hTextField;
    private javax.swing.JComboBox<String> v_hYesNoComboBox;
    private javax.swing.JLabel v_pCutLabel;
    private javax.swing.JLabel v_pLabel;
    private javax.swing.JPanel v_pPanel;
    private javax.swing.JTextField v_pTextField;
    private javax.swing.JComboBox<String> v_pYesNoComboBox;
    // End of variables declaration//GEN-END:variables
}
