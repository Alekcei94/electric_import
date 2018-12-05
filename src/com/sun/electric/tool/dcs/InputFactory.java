/* Electric(tm) VLSI Design System
 *
 * File: InputFactory.java
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
package com.sun.electric.tool.dcs;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Scanner;

/**
 * Class to simplify readers logic.
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