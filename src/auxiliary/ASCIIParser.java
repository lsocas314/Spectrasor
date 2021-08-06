/*
 * Copyright (C) 2021 L.B.P.Socas
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package auxiliary;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;

/**
 * Static class for general ASCII data parser.
 *
 * @author L.B.P.Socas
 */
final class ASCIIParser {

    // ######### Internal API ##########
    
    static String[][] loadPath(String path, String delimiter) throws Exception {
        ArrayList<String[]> dataTemp;
        String[][] data;
        try (BufferedReader fileBR = new BufferedReader(new FileReader(path))) {
            dataTemp = new ArrayList<>();
            String line;
            while ((line = fileBR.readLine()) != null) {
                dataTemp.add(line.split(delimiter));
            }
        } catch (Exception e) {
            throw new Exception("Error loading ASCII :( ...");
        }
        data = new String[dataTemp.size()][getMax(dataTemp)];
        for (int i = 0; i < data.length; i++) {
            for (int j = 0; j < data[i].length; j++) {
                if (dataTemp.get(i).length > j) {
                    data[i][j] = dataTemp.get(i)[j];
                } else {
                    data[i][j] = "NONE";
                }
            }
        }
        return data;
    }

    // ######### Private implementation ##########
    
    private static int getMax(ArrayList<String[]> dataTemp) {
        int temp = 0;
        for (String[] dataTemp1 : dataTemp) {
            if (dataTemp1.length > temp) {
                temp = dataTemp1.length;
            }
        }
        return temp;
    }

}
