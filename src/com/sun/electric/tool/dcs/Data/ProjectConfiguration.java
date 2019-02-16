/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sun.electric.tool.dcs.Data;

/**
 *
 * @author Astepanov
 */
public class ProjectConfiguration {

    private static final String pinPatternHead = "mAd";
    private static final String pinPatternTailFirst = "_1";
    private static final String pinPatternTailSecond = "_2";

    /**
     * mAd This method returns the head of the pin entry form. (mAd..._1)
     *
     * @return pinPatternHead
     */
    public static String getPinPatternHead() {
        return pinPatternHead;
    }

    /**
     * _1 This method returns the tail of the pin entry form. (mAd..._1)
     *
     * @return pinPatternTailFirst
     */
    public static String getPinPatternTailFirst() {
        return pinPatternTailFirst;
    }

    public static String getPinPatternTailSecond() {
        return pinPatternTailSecond;
    }
}
