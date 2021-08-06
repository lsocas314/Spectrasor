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

import java.io.Serializable;
import views.common.ColorSource;

/**
 * Abstract class to handle Graph delegation.
 *
 * @author L.B.P.Socas
 */
public abstract class GraphDelegate {

    /**
     * Enumerator defining the possible shapes for dots in a graph.
     */
    public enum DotShape implements Serializable {

        /**
         * Rectangular shape.
         */
        rectangle,

        /**
         * Circular shape.
         */
        circle,

        /**
         * Triangular shape.
         */
        triangle,

        /**
         * Cross shape.
         */
        cross;

        /**
         * Get the string describing the dot shape.
         *
         * @return a string describing the dot shape.
         */
        public String description() {
            switch (this) {
                case rectangle:
                    return "Rectangle";
                case circle:
                    return "Circle";
                case triangle:
                    return "Triangle";
                case cross:
                    return "Cross";
                default:
                    return null;
            }
        }

    }

    /**
     * Defines the possibility of the user to interact with the graph.
     *
     * @return true if the user can interact with the graph, false otherwise.
     */
    public abstract boolean userInteraction();

    /**
     * Defines if the graph contains valid data.
     *
     * @return true if the graph data is valid, false otherwise.
     */
    public abstract boolean validData();

    /**
     * Get the number of curves for a certain y axis in the graph.
     *
     * @param yAxisIndex the index for the y axis of interest.
     * @return the number of curves at the specify y axis.
     */
    public abstract int numberOfCurves(int yAxisIndex);

    /**
     * Get the number of dots for a certain y axis in the graph.
     *
     * @param yAxisIndex the index for the y axis of interest.
     * @return the number of dots at the specify y axis.
     */
    public abstract int numberOfDots(int yAxisIndex);

    /**
     * Define the number of vertical lines to include in the graph.
     *
     * @return the number of vertical lines to include in the graph.
     */
    public abstract int numberOfVerticalLines();

    /**
     * Define if the graph can be zoomed.
     *
     * @return true if the user can zoom the graph, false otherwise.
     */
    public boolean canZoom() {
        return false;
    }
    
    /**
     * Get the curve x-y values for a specified y axis and curve index.
     *
     * @param yAxisIndex the index for the y axis of interest.
     * @param curveIndex the index of the curve.
     * @return a 2-dimensional array with the curve x-y values.
     */
    public double[][] curveValues(int yAxisIndex, int curveIndex) {
        return null;
    }

    /**
     * Get the ColorSource used to define the curves colors.
     *
     * @param yAxisIndex the index for the y axis of interest.
     * @return the ColorSource for the curves.
     */
    public ColorSource curvesColorSource(int yAxisIndex) {
        return null;
    }

    /**
     * Get the dot shape for a specified y axis and dot index.
     *
     * @param yAxisIndex the index for the y axis of interest.
     * @param dotIndex the index of the dot.
     * @return the DotShape value of the specified dot.
     */
    public DotShape dotShape(int yAxisIndex, int dotIndex) {
        return DotShape.rectangle;
    }

    /**
     * Get the dot value for a specified y axis and dot index.
     *
     * @param yAxisIndex the index for the y axis of interest.
     * @param dotIndex the index of the dot.
     * @return the x,y value of the specified dot.
     */
    public double[] dotValue(int yAxisIndex, int dotIndex) {
        return null;
    }

    /**
     * Get the ColorSource used to define the dots stroke colors.
     *
     * @param yAxisIndex the index for the y axis of interest.
     * @return the ColorSource for the dots stroke.
     */
    public ColorSource dotsColorSource(int yAxisIndex) {
        return null;
    }

    /**
     * Get the ColorSource used to define the dots fill colors.
     *
     * @param yAxisIndex the index for the y axis of interest.
     * @return the ColorSource for the dots fill.
     */
    public ColorSource dotsFillColorSource(int yAxisIndex) {
        return dotsColorSource(yAxisIndex);
    }

    /**
     * Defines if the x axis contains fixed extremes.
     *
     * @return the values of the fixed x axis extremes of the graph.
     * Null if the graph x axis can be flexible.
     */
    public double[] fixedXExtremes() {
        return null;
    }

    /**
     * Defines if the y axis contains fixed extremes.
     *
     * @param yAxisIndex the index for the y axis of interest.
     * @return the values of the fixed y axis extremes of the graph.
     * Null if the graph y axis can be flexible.
     */
    public double[] fixedYExtremes(int yAxisIndex) {
        return null;
    }

    /**
     * Get the currently active y axis.
     *
     * @return the currently active y axis index.
     */
    public int getActiveYAxisIndex() {
        return 0;
    }

    /**
     * Define is the graph is in polar coordinates.
     *
     * @return true if the graph is in polar coordinates, false, otherwise.
     */
    public boolean isPolarGraph() {
        return false;
    }

    /**
     * Define the number of different y axis (1 by default).
     *
     * @return the number of different y axis in the graph.
     */
    public int numberOfYAxis() {
        return 1;
    }

    /**
     * Get the indices for selected curves.
     *
     * @param yAxisIndex the index for the y axis of interest.
     * @return the indices of the selected curves.
     */
    public int[] selectedCurves(int yAxisIndex) {
        return new int[]{};
    }

    /**
     * Get the indices for selected dots.
     *
     * @param yAxisIndex the index for the y axis of interest.
     * @return the indices of the selected dots.
     */
    public int[] selectedDots(int yAxisIndex) {
        return new int[]{};
    }

    /**
     * Get the x value of a specified vertical line.
     *
     * @param index the index of the vertical line.
     * @return the x value of the vertical line.
     */
    public double verticalLineValue(int index) {
        return 0;
    }
    
    /**
     * Get the y value of the selected curve at the vertical line position.
     *
     * @param index the index of the vertical line.
     * @return the y value of the selected curve at the vertical line position.
     */
    public Double yValueForVerticalLine(int index) {
        return null;
    }

    /**
     * Called when a curve is selected by the user.
     *
     * @param yAxisIndex the index for the y axis of interest.
     * @param curveIndex the index of the selected curve.
     */
    public void curveSelected(int yAxisIndex, int curveIndex) {
    }

    /**
     * Called when a vertical line is moved by the user.
     *
     * @param index the index of the vertical line.
     * @param newValue the new value of the vertical line.
     */
    public void verticalLineMoved(int index, double newValue) {
    }

    /**
     * Called when the graph is zoomed by the user.
     *
     * @param minX the minimum x value of the zoomed graph.
     * @param maxX the maximum x value of the zoomed graph.
     * @param maxY the minimum y value of the zoomed graph.
     * @param minY the maximum y value of the zoomed graph.
     */
    public void zoomed(double minX, double maxX, double maxY, double minY) {
    }

}
