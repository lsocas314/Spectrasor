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

import auxiliary.MathFeatures;
import views.list.Listeable;

/**
 * Phasor points used as reference for fraction calculation.
 *
 * @author L.B.P.Socas
 */
public final class ReferencePoint implements Listeable {

    // ######### Public API ##########
    
    /**
     * Creates a new reference point for a specified harmonic.
     *
     * @param harmonicIndex the harmonic index for the new point.
     * @param name the name to represent the point.
     * @param gCoor the G coordinate of the point.
     * @param sCoor the S coordinate of the point.
     */
    public ReferencePoint(int harmonicIndex, String name, double gCoor, double sCoor) {
        this.harmonicIndex = harmonicIndex;
        this.name = name;
        this.gCoor = gCoor;
        this.sCoor = sCoor;
    }

    /**
     * Get the coordinates for the reference point.
     *
     * @return a 2-dimensional array containing the coordinates of the point as
     * [G, S].
     */
    public double[] getCoor() {
        return new double[]{gCoor, sCoor};
    }

    /**
     * Get the harmonic index of the point.
     *
     * @return the harmonic index of the point.
     */
    public int getHarmonicIndex() {
        return harmonicIndex;
    }

    @Override
    public String getName() {
        String g = MathFeatures.getStringOfValueRoundedByError(false, gCoor, 0.001);
        String s = MathFeatures.getStringOfValueRoundedByError(false, sCoor, 0.001);
        return name + " (" + g + "; " + s + ")";
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (this == obj) {
            return true;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        ReferencePoint point = (ReferencePoint) obj;
        if (this.gCoor != point.gCoor) {
            return false;
        }
        return this.sCoor == point.sCoor;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 31 * hash + (int) (Double.doubleToLongBits(this.gCoor) ^ (Double.doubleToLongBits(this.gCoor) >>> 32));
        hash = 31 * hash + (int) (Double.doubleToLongBits(this.sCoor) ^ (Double.doubleToLongBits(this.sCoor) >>> 32));
        return hash;
    }

    // ######### Private implementation ##########
    
    private final int harmonicIndex;
    private final String name;
    private final double gCoor;
    private final double sCoor;

}
