/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sun.electric.tool.dcs;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Scanner;

/**
 *
 * @author diivanov
 */
public class InputFactory {
    public static InputStreamReader inStreamReader(InputStream in){
        return new InputStreamReader(in);
    }
    public static BufferedReader bufferedReader(InputStream in){
        return new BufferedReader(inStreamReader(in));
    }
    public static BufferedReader bufferedReader(File textFile) throws FileNotFoundException{
        return new BufferedReader(new FileReader(textFile));
    }
    public static Scanner scanner(InputStream in){
        return new Scanner(inStreamReader(in));
    }
}