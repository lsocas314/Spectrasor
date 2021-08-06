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
package views.graph;

/**
 * Class defining a curve in a graph.
 *
 * @author L.B.P.Socas
 */
final class Curve {
    
    // ######### Internal implementation ##########
    
    static final float SELECTED_CURVE_WIDTH = 4.0f;
    static final float UNSELECTED_CURVE_WIDTH = 2.0f;
    static final int X_INDEX = 0;
    static final int Y_INDEX = 1;

    final double[][] data;

    boolean selected = false;

    Curve(double[] xData, double[] yData) {
        data = new double[][]{xData, yData};
    }

    boolean contains(double x, double y, double width, double height) {
        double tempX;
        double tempY;
        int numberOfPoints = Math.min(data[X_INDEX].length, data[Y_INDEX].length);
        double xRight = x + width;
        double yTop = y + height;
        double xLeft = x - width;
        double yBottom = y - height;
        for (int i = 1; i < numberOfPoints; i++) {
            tempX = data[X_INDEX][i];
            tempY = data[Y_INDEX][i];
            if (tempX >= xLeft && tempX <= xRight && tempY >= yBottom && tempY <= yTop) {
                return true;
            }
        }
        return false;
    }

    float curveWidth() {
        return (selected) ? SELECTED_CURVE_WIDTH : UNSELECTED_CURVE_WIDTH;
    }

    double getMax(int dataIndex) {
        return auxiliary.MathFeatures.getMax(data[dataIndex]);
    }

    double getMin(int dataIndex) {
        return auxiliary.MathFeatures.getMin(data[dataIndex]);
    }

}
