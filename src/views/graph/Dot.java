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
 * Class defining a dot in a graph.
 *
 * @author L.B.P.Socas
 */
final class Dot {

    // ######### Internal implementation ##########
    
    final double x;
    final double y;
    final GraphDelegate.DotShape shape;

    boolean selected = false;

    Dot(double x, double y, GraphDelegate.DotShape shape) {
        this.x = x;
        this.y = y;
        this.shape = shape;
    }

}
