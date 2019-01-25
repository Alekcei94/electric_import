/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sun.electric.tool.dcs.autotracing;

import com.sun.electric.tool.dcs.Accessory;
import com.sun.electric.tool.dcs.autotracing.Interfaces.ITraceable;
import java.util.List;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

/**
 *
 * @author diivanov
 */
public class AutotracingTest {

    public AutotracingTest() {
        System.out.println(Accessory.timeStart());
        ITraceable nogg = GlobalGraph.GlobalFactory.createGlobalGraph("first");
        System.out.println(Accessory.timeStart());
        ((GlobalGraph) nogg).showStructure();
        System.out.println(Accessory.timeStart());
        JFrame frame = new JFrame("InputDialog Example #1");
        String firstVertex = JOptionPane.showInputDialog(frame, "Write first vertex?");
        String secondVertex = JOptionPane.showInputDialog(frame, "Write Second vertex?");
        if(firstVertex != null && secondVertex != null) {
            List<String> list = nogg.getConfigurationPath(firstVertex, secondVertex, false);
            for(String str : list) {
                System.out.println(str);
            }
        }
    }
}
