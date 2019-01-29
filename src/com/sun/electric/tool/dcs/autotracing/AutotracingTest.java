/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sun.electric.tool.dcs.autotracing;

import com.sun.electric.tool.dcs.Accessory;
import com.sun.electric.tool.dcs.Data.BlockMap.BlockPattern;
import com.sun.electric.tool.dcs.Exceptions.NoPathFoundException;
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
        //((GlobalGraph) nogg).showStructure();
        //System.out.println(Accessory.timeStart());
        JFrame frame = new JFrame("InputDialog Example #1");
        String firstVertexStr = JOptionPane.showInputDialog(frame, "Write first vertex?");
        BlockPattern firstVertex = new BlockPattern(firstVertexStr.split(" ")[0],
                firstVertexStr.split(" ")[1], firstVertexStr.split(" ")[2]);
        String secondVertexStr = JOptionPane.showInputDialog(frame, "Write Second vertex?");
        BlockPattern secondVertex = new BlockPattern(secondVertexStr.split(" ")[0],
                secondVertexStr.split(" ")[1], secondVertexStr.split(" ")[2]);
        System.out.println(Accessory.timeStart());
        List<String> list;
        try {
            list = nogg.getConfigurationPath(firstVertex, secondVertex, false);
            for (String str : list) {
                System.out.println(str);
            }
        } catch (NoPathFoundException ex) {
            Accessory.showMessage("There is no path through this way.");
        }
        System.out.println(Accessory.timeStart());
    }
}
