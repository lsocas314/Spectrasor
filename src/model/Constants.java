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
package model;

import java.awt.Color;
import views.common.ColorSource;

/**
 * Static class to store some constant values.
 *
 * @author L.B.P.Socas
 */
public final class Constants {

    // ######### Public API ##########
    
    /**
     * An array of the ColorSources objects used.
     */
    public static final ColorSource[] colorSources = new ColorSource[]{
        ColorSource.rainbowFromRedToBlue(),
        ColorSource.rainbowFromBlueToRed(),
        ColorSource.fixedColor(Color.black, "Black"),
        ColorSource.fixedColor(Color.red, "Red"),
        ColorSource.fixedColor(Color.blue, "Blue"),
        ColorSource.fixedColor(Color.green, "Green"),
        ColorSource.fixedColor(Color.cyan, "Cyan"),
        ColorSource.fixedColor(Color.magenta, "Magenta"),
        ColorSource.fixedColor(Color.yellow, "Yellow"),
        ColorSource.fixedColor(Color.orange, "Orange"),
        ColorSource.fixedColor(Color.pink, "Pink"),};

    /**
     * An array of the 2-dimensional harmonics used.
     */
    public static final int[][] harmonicOptions = new int[][]{
        {0, 1},
        {0, 2},
        {1, 0},
        {1, 1},
        {1, 2},
        {2, 0},
        {2, 1},
        {2, 2}
    };

    /**
     * Get the harmonic options as text.
     *
     * @return an array containing the string representation of the harmonics.
     */
    public static String[] harmonicStr() {
        String[] harmonicStr = new String[harmonicOptions.length];
        for (int i = 0; i < harmonicStr.length; i++) {
            harmonicStr[i] = "(n, m) = (" + harmonicOptions[i][0] + ", " + harmonicOptions[i][1] + ")";
        }
        return harmonicStr;
    }

    /**
     * Get the ColorSource options as text.
     *
     * @return an array containing the string representation of the ColorSource
     * used.
     */
    public static String[] colorOptions() {
        String[] colorsStr = new String[colorSources.length];
        for (int i = 0; i < colorSources.length; i++) {
            colorsStr[i] = colorSources[i].getName();
        }
        return colorsStr;
    }

}
