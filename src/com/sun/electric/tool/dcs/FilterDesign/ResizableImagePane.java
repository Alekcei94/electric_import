/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sun.electric.tool.dcs.FilterDesign;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import javax.swing.ImageIcon;

/**
 *
 * @author diivanov
 */

public final class ResizableImagePane extends javax.swing.JPanel {

    private Image img;

        public ResizableImagePane() {
            initComponents();
            ImageIcon ii = new ImageIcon("filterDesignResult.png"); // hardcoding,

            // should be changed later
            this.setImage(ii.getImage());
        }

        public void setImage(Image value) {
            if (img != value) {
                this.img = value;
            }
        }
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (img != null) {
                //img.flush();
                //ImageIcon ii = new ImageIcon("filterDesignResult.png");
                //this.setImage(ii.getImage());

                Graphics2D g2d = (Graphics2D) g.create();
                g2d.drawImage(img, 0, 0, this.getWidth(), this.getHeight(), this);
                g2d.dispose();
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

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}