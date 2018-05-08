/* -*- tab-width: 4 -*-
 *
 * Electric(tm) VLSI Design System
 *
 * File: FilterDesignWindow.java
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
package com.sun.electric.tool.dcs.FilterDesign;

import com.sun.electric.tool.user.dialogs.EModelessDialog;
import java.awt.Frame;
import javax.swing.JOptionPane;

public class FilterDesignWindow extends EModelessDialog {

    /**
     * Creates new form Array
     */
    private FilterDesignWindow(Frame parent) {
        super(parent);
        initComponents();
        pack();
    }

    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        spacing = new javax.swing.ButtonGroup();
        cancel = new javax.swing.JButton();
        ok = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        xRepeat = new javax.swing.JTextField();
        flipAlternateColumns = new javax.swing.JCheckBox();
        staggerAlternateColumns = new javax.swing.JCheckBox();
        centerXAboutOriginal = new javax.swing.JCheckBox();
        jLabel2 = new javax.swing.JLabel();
        yRepeat = new javax.swing.JTextField();
        flipAlternateRows = new javax.swing.JCheckBox();
        staggerAlternateRows = new javax.swing.JCheckBox();
        centerYAboutOriginal = new javax.swing.JCheckBox();
        jSeparator1 = new javax.swing.JSeparator();
        xOverlapLabel = new javax.swing.JLabel();
        xSpacing = new javax.swing.JTextField();
        spaceByEdgeOverlap = new javax.swing.JRadioButton();
        spaceByCenterlineDistance = new javax.swing.JRadioButton();
        yOverlapLabel = new javax.swing.JLabel();
        ySpacing = new javax.swing.JTextField();
        spaceByEssentialBnd = new javax.swing.JRadioButton();
        spaceByMeasuredDistance = new javax.swing.JRadioButton();
        linearDiagonalArray = new javax.swing.JCheckBox();
        generateArrayIndices = new javax.swing.JCheckBox();
        onlyDRCCorrect = new javax.swing.JCheckBox();
        transposePlacement = new javax.swing.JCheckBox();
        apply = new javax.swing.JButton();
        draw = new javax.swing.JButton();

        getContentPane().setLayout(new java.awt.GridBagLayout());

        setTitle("Array");
        setName("");
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                //closeDialog(evt);
            }
        });

        cancel.setText("Cancel");
        cancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                //cancel(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 13;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        getContentPane().add(cancel, gridBagConstraints);

        ok.setText("OK");
        ok.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                //ok(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 13;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        getContentPane().add(ok, gridBagConstraints);

        jLabel1.setText("X repeat factor:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        getContentPane().add(jLabel1, gridBagConstraints);

        xRepeat.setColumns(6);
        xRepeat.setText(" ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 3;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        getContentPane().add(xRepeat, gridBagConstraints);

        flipAlternateColumns.setText("Flip alternate columns");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 0, 4);
        getContentPane().add(flipAlternateColumns, gridBagConstraints);

        staggerAlternateColumns.setText("Stagger alternate columns");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 4, 0, 4);
        getContentPane().add(staggerAlternateColumns, gridBagConstraints);

        centerXAboutOriginal.setText("Center about original");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 4, 4, 4);
        getContentPane().add(centerXAboutOriginal, gridBagConstraints);

        jLabel2.setText("Y repeat factor:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridheight = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        getContentPane().add(jLabel2, gridBagConstraints);

        yRepeat.setColumns(6);
        yRepeat.setText(" ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridheight = 3;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        getContentPane().add(yRepeat, gridBagConstraints);

        flipAlternateRows.setText("Flip alternate rows");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 0, 4);
        getContentPane().add(flipAlternateRows, gridBagConstraints);

        staggerAlternateRows.setText("Stagger alternate rows");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 4, 0, 4);
        getContentPane().add(staggerAlternateRows, gridBagConstraints);

        centerYAboutOriginal.setText("Center about original");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 4, 4, 4);
        getContentPane().add(centerYAboutOriginal, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        getContentPane().add(jSeparator1, gridBagConstraints);

        xOverlapLabel.setText("X edge overlap:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        getContentPane().add(xOverlapLabel, gridBagConstraints);

        xSpacing.setColumns(6);
        xSpacing.setText(" ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        getContentPane().add(xSpacing, gridBagConstraints);

        spacing.add(spaceByEdgeOverlap);
        spaceByEdgeOverlap.setText("Space by edge overlap");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        getContentPane().add(spaceByEdgeOverlap, gridBagConstraints);

        spacing.add(spaceByCenterlineDistance);
        spaceByCenterlineDistance.setText("Space by centerline distance");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        getContentPane().add(spaceByCenterlineDistance, gridBagConstraints);

        yOverlapLabel.setText("Y edge overlap:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        getContentPane().add(yOverlapLabel, gridBagConstraints);

        ySpacing.setColumns(6);
        ySpacing.setText(" ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        getContentPane().add(ySpacing, gridBagConstraints);

        spacing.add(spaceByEssentialBnd);
        spaceByEssentialBnd.setText("Space by cell essential bound");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        getContentPane().add(spaceByEssentialBnd, gridBagConstraints);

        spacing.add(spaceByMeasuredDistance);
        spaceByMeasuredDistance.setText("Space by last measured distance");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 10;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        getContentPane().add(spaceByMeasuredDistance, gridBagConstraints);

        linearDiagonalArray.setText("Linear diagonal array");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 11;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 2, 4);
        getContentPane().add(linearDiagonalArray, gridBagConstraints);

        generateArrayIndices.setText("Generate array indices");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 12;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 4, 2, 4);
        getContentPane().add(generateArrayIndices, gridBagConstraints);

        onlyDRCCorrect.setText("Only place entries that are DRC correct");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 13;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 4, 2, 4);
        getContentPane().add(onlyDRCCorrect, gridBagConstraints);

        transposePlacement.setText("Transpose placement ordering");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 14;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 4, 4, 4);
        getContentPane().add(transposePlacement, gridBagConstraints);

        apply.setText("Apply");
        apply.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                //applyActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 11;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        getContentPane().add(apply, gridBagConstraints);

        draw.setText("Draw");
        draw.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                //drawActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 11;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        getContentPane().add(draw, gridBagConstraints);

        pack();
    }// </editor-fold> 

    // Variables declaration - do not modify                     
    private javax.swing.JButton apply;
    private javax.swing.JButton cancel;
    private javax.swing.JCheckBox centerXAboutOriginal;
    private javax.swing.JCheckBox centerYAboutOriginal;
    private javax.swing.JButton draw;
    private javax.swing.JCheckBox flipAlternateColumns;
    private javax.swing.JCheckBox flipAlternateRows;
    private javax.swing.JCheckBox generateArrayIndices;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JCheckBox linearDiagonalArray;
    private javax.swing.JButton ok;
    private javax.swing.JCheckBox onlyDRCCorrect;
    private javax.swing.JRadioButton spaceByCenterlineDistance;
    private javax.swing.JRadioButton spaceByEdgeOverlap;
    private javax.swing.JRadioButton spaceByEssentialBnd;
    private javax.swing.JRadioButton spaceByMeasuredDistance;
    private javax.swing.ButtonGroup spacing;
    private javax.swing.JCheckBox staggerAlternateColumns;
    private javax.swing.JCheckBox staggerAlternateRows;
    private javax.swing.JCheckBox transposePlacement;
    private javax.swing.JLabel xOverlapLabel;
    private javax.swing.JTextField xRepeat;
    private javax.swing.JTextField xSpacing;
    private javax.swing.JLabel yOverlapLabel;
    private javax.swing.JTextField yRepeat;
    private javax.swing.JTextField ySpacing;
    // End of variables declaration         

}
