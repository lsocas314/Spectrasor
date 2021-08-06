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

import views.list.Listeable;

/**
 * Class that defines a linear equation used to for fraction calculation.
 *
 * @author L.B.P.Socas
 */
public final class FractionEquation implements Listeable {

    // ######### Public API ##########
    
    /**
     * Constant defining the equation type as a G coordinate equation.
     */
    public static final int G_COORD = 0;

    /**
     * Constant defining the equation type as a S coordinate equation.
     */
    public static final int S_COORD = 1;

    /**
     * Constant defining the equation type as the fraction sum constrain
     * equation.
     */
    public static final int UNITY_COORD = 2;

    /**
     * Creates a fraction sum constrain equation for a specified number of
     * components.
     *
     * @param numberOfComponents the number of components in the equation.
     */
    public FractionEquation(int numberOfComponents) {
        coordType = UNITY_COORD;
        refValues = new double[numberOfComponents];
        for (int i = 0; i < refValues.length; i++) {
            refValues[i] = 1;
        }
        harmonicIndex = -1;
    }

    /**
     * Creates a linear equation used for fraction calculation by defining a
     * type and a list of references.
     *
     * @param coordType the type of the equation, either {@code G_COORD} or
     * {@code S_COORD}.
     * @param harmonicIndex the index of the harmonic for the equation.
     * @param refValues the list of the reference values defining the equation
     * coefficients.
     */
    public FractionEquation(int coordType, int harmonicIndex, double[] refValues) {
        this.coordType = coordType;
        this.harmonicIndex = harmonicIndex;
        this.refValues = refValues;
    }

    /**
     * Get the type of the equation.
     *
     * @return the type of the equation, one of {@code G_COORD}, {@code S_COORD}
     * or {@code UNITY_COORD} values.
     */
    public int getCoordType() {
        return coordType;
    }

    /**
     * Get the harmonic index of the equation.
     *
     * @return the harmonic index of the equation
     */
    public int getHarmonicIndex() {
        return harmonicIndex;
    }

    /**
     * Get the reference values (coefficients) for the equation.
     *
     * @return the reference values of the equation.
     */
    public double[] getRefValues() {
        return refValues;
    }

    /**
     * Ask if the equation is the fraction sum constrain equation.
     *
     * @return true if the equation type is {@code UNITY_COORD}, false
     * otherwise.
     */
    public boolean isUnityEquation() {
        return coordType == UNITY_COORD;
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
        FractionEquation point = (FractionEquation) obj;
        if (this.coordType != point.coordType) {
            return false;
        }
        return this.harmonicIndex == point.harmonicIndex;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 71 * hash + this.coordType;
        hash = 71 * hash + this.harmonicIndex;
        return hash;
    }

    @Override
    public String getName() {
        if (coordType == UNITY_COORD) {
            return "Sum of fractions";
        }
        String harmonicStr = Constants.harmonicStr()[harmonicIndex];
        String coordStr = (coordType == G_COORD) ? "G coordinates" : "S coordinates";
        return coordStr + " at " + harmonicStr;
    }

    // ######### Private implementation ##########
    
    private final int coordType;
    private final int harmonicIndex;
    private final double[] refValues;

}
