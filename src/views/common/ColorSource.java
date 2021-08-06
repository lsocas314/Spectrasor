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
package views.common;

import java.awt.Color;

/**
 * A class defining a color source used for graphs view. Member of the View part of the MVC.
 *
 * @author L.B.P.Socas
 */
public final class ColorSource {
    
    // ######### Public API ##########

    /**
     * Creates a color source through a color cycle.
     *
     * @param color an array of colors to cycle.
     * @param name a name for the ColorSource object.
     * @return a new ColorSource object.
     */
    public static ColorSource colorCycle(Color[] color, String name) {
        ColorSource generator = new ColorSource(name);
        generator.variableColor = null;
        generator.colorPoints = color;
        return generator;
    }

    /**
     * Creates a color source with a fixed color.
     *
     * @param color the fixed color.
     * @param name a name for the ColorSource object.
     * @return a new ColorSource object.
     */
    public static ColorSource fixedColor(Color color, String name) {
        ColorSource generator = new ColorSource(name);
        generator.variableColor = null;
        generator.colorPoints = new Color[]{color};
        return generator;
    }

    /**
     * Creates a rainbow color source from blue to red.
     *
     * @return a new ColorSource object.
     */
    public static ColorSource rainbowFromBlueToRed() {
        ColorSource generator = new ColorSource("Blue-red");
        generator.variableColor = new int[]{
            ColorSource.RED + ColorSource.BLUE,
            ColorSource.GREEN,
            ColorSource.BLUE,
            ColorSource.RED,
            ColorSource.GREEN
        };
        generator.colorPoints = new Color[]{
            new Color(128, 0, 128),
            new Color(0, 0, 255),
            new Color(0, 255, 255),
            new Color(0, 255, 0),
            new Color(255, 255, 0),
            new Color(255, 0, 0)
        };
        return generator;
    }

    /**
     * Creates a rainbow color source from red to blue.
     *
     * @return a new ColorSource object.
     */
    public static ColorSource rainbowFromRedToBlue() {
        ColorSource generator = new ColorSource("Red-blue");
        generator.variableColor = new int[]{
            ColorSource.GREEN,
            ColorSource.RED,
            ColorSource.BLUE,
            ColorSource.GREEN,
            ColorSource.RED + ColorSource.BLUE
        };
        generator.colorPoints = new Color[]{
            new Color(255, 0, 0),
            new Color(255, 255, 0),
            new Color(0, 255, 0),
            new Color(0, 255, 255),
            new Color(0, 0, 255),
            new Color(128, 0, 128)
        };
        return generator;
    }

    /**
     * Get the ColorSource name.
     *
     * @return the ColorSource name
     */
    public String getName() {
        return name;
    }
    
    
    /**
     * Get an array of colors generated through this color source.
     *
     * @param numberOfColors the number of colors to generate.
     * @return an array of the generated colors.
     */
    public Color[] getColors(int numberOfColors) {
        if (colorPoints == null) {
            return null;
        }
        if (variableColor == null) {
            Color[] result = new Color[numberOfColors];
            for (int i = 0; i < numberOfColors; i++) {
                result[i] = colorPoints[i % colorPoints.length];
            }
            return result;
        }
        if (!isPossible(numberOfColors)) {
            return null;
        }
        Color[] fullGradient = new Color[variableColor.length * numberOfColors];
        int l = 0;
        for (int i = 0; i < variableColor.length; i++) {
            int redStep = 0;
            int greenStep = 0;
            int blueStep = 0;
            switch (variableColor[i]) {
                case RED:
                    redStep = (colorPoints[i + 1].getRed() - colorPoints[i].getRed()) / numberOfColors;
                    break;
                case GREEN:
                    greenStep = (colorPoints[i + 1].getGreen() - colorPoints[i].getGreen()) / numberOfColors;
                    break;
                case BLUE:
                    blueStep = (colorPoints[i + 1].getBlue() - colorPoints[i].getBlue()) / numberOfColors;
                    break;
                case RED + GREEN:
                    redStep = (colorPoints[i + 1].getRed() - colorPoints[i].getRed()) / numberOfColors;
                    greenStep = (colorPoints[i + 1].getGreen() - colorPoints[i].getGreen()) / numberOfColors;
                    break;
                case RED + BLUE:
                    redStep = (colorPoints[i + 1].getRed() - colorPoints[i].getRed()) / numberOfColors;
                    blueStep = (colorPoints[i + 1].getBlue() - colorPoints[i].getBlue()) / numberOfColors;
                    break;
                case GREEN + BLUE:
                    greenStep = (colorPoints[i + 1].getGreen() - colorPoints[i].getGreen()) / numberOfColors;
                    blueStep = (colorPoints[i + 1].getBlue() - colorPoints[i].getBlue()) / numberOfColors;
                    break;
                case RED + GREEN + BLUE:
                    redStep = (colorPoints[i + 1].getRed() - colorPoints[i].getRed()) / numberOfColors;
                    greenStep = (colorPoints[i + 1].getGreen() - colorPoints[i].getGreen()) / numberOfColors;
                    blueStep = (colorPoints[i + 1].getBlue() - colorPoints[i].getBlue()) / numberOfColors;
                    break;
            }
            for (int j = 0; j < numberOfColors; j++) {
                fullGradient[l] = new Color(
                        colorPoints[i].getRed() + j * redStep,
                        colorPoints[i].getGreen() + j * greenStep,
                        colorPoints[i].getBlue() + j * blueStep
                );
                l++;
            }
        }
        Color[] result = new Color[numberOfColors];
        for (int i = 0; i < numberOfColors; i++) {
            result[i] = fullGradient[i * variableColor.length];
        }
        return result;
    }
    
    // ######### Private implementation ##########

    private ColorSource(String name) {
        this.name = name;
    }
    
    private static final int BLUE = 7;
    private static final int GREEN = 5;
    private static final int RED = 3;
    
    private final String name;
    private Color[] colorPoints;
    private int[] variableColor;

    private boolean isPossible(int numberOfColors) {
        if (colorPoints.length < 2) {
            return false;
        }
        if (numberOfColors < 1) {
            return false;
        }
        if (variableColor.length != colorPoints.length - 1) {
            return false;
        }
        for (int i = 0; i < variableColor.length; i++) {
            switch (variableColor[i]) {
                case RED:
                case GREEN:
                case BLUE:
                case RED + GREEN:
                case RED + BLUE:
                case GREEN + BLUE:
                case RED + GREEN + BLUE:
                    continue;
                default:
                    return false;
            }
        }
        return true;
    }

}
