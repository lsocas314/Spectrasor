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

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Class to perform the fraction calculation by solving a system of linear
 * equations.
 *
 * @author L.B.P.Socas
 */
public final class FractionCalculator implements Serializable {

    // ######### Public API ##########
    
    /**
     * List of all the reference points in the model.
     */
    public final ArrayList<ReferencePoint>[] points = new ArrayList[]{
        new ArrayList<>(),
        new ArrayList<>(),
        new ArrayList<>(),
        new ArrayList<>(),
        new ArrayList<>(),
        new ArrayList<>(),
        new ArrayList<>(),
        new ArrayList<>()
    };

    /**
     * List of the equations to use for fraction calculation.
     */
    public final ArrayList<FractionEquation> system = new ArrayList<>();

    /**
     * Get the fraction values calculated for each component.
     *
     * @param phasor a MultispectralPhasor object for the assay of interest.
     * @param spectrumIndex the index of the spectra of interest.
     * @return an array containing the fractions calculated for each component.
     */
    public double[] getFractions(MultispectralPhasor phasor, int spectrumIndex) {
        if (system.isEmpty()) {
            return null;
        }
        double[][] systemMatrix = new double[numberOfComponents][];
        double[] bVector = new double[numberOfComponents];
        for (int i = 0; i < numberOfComponents; i++) {
            FractionEquation equation = system.get(i);
            double[] values = equation.getRefValues();
            if (values.length != numberOfComponents) {
                return null;
            }
            systemMatrix[i] = values;
            int coordType = equation.getCoordType();
            if (coordType == FractionEquation.UNITY_COORD) {
                bVector[i] = 1;
            } else {
                int harmonicIndex = equation.getHarmonicIndex();
                int[] harmonic = Constants.harmonicOptions[harmonicIndex];
                double[] phasorValues = phasor.getPhasor(spectrumIndex, new double[]{harmonic[0], harmonic[1]});
                bVector[i] = phasorValues[coordType];
            }
        }
        double mainDeterminant = determinant(systemMatrix, numberOfComponents);
        if (mainDeterminant == 0) {
            return null;
        }
        double[] result = new double[numberOfComponents];
        for (int i = 0; i < numberOfComponents; i++) {
            double[][] tempMatrix = new double[numberOfComponents][numberOfComponents];
            for (int j = 0; j < numberOfComponents; j++) {
                for (int k = 0; k < numberOfComponents; k++) {
                    if (k == i) {
                        tempMatrix[j][k] = bVector[j];
                    } else {
                        tempMatrix[j][k] = systemMatrix[j][k];
                    }
                }
            }
            double tempDeterminant = determinant(tempMatrix, numberOfComponents);
            result[i] = tempDeterminant / mainDeterminant;
        }
        return result;
    }

    /**
     * Set the number of components to solve.
     *
     * @param numberOfComponents number of components to solve by the system.
     */
    public void setNumberOfComponents(int numberOfComponents) {
        this.numberOfComponents = numberOfComponents;
    }

    /**
     * Get the number of components to solve.
     *
     * @return the number of components to solve by the system.
     */
    public int getNumberOfComponents() {
        return numberOfComponents;
    }

    /**
     * Determines if the system is correctly define, that is, the number of
     * equations is equal or higher than the number of components.
     *
     * @return true if the system is correctly define, false otherwise.
     */
    public boolean systemIsCorrect() {
        return !system.isEmpty() && system.size() >= numberOfComponents;
    }

    // ######### Private implementation ##########
    
    private int numberOfComponents = -1;

    private double determinant(double[][] matrix, int dimension) {
        if (dimension == 1) {
            return matrix[0][0];
        }
        double result = 0;
        double[][] cofactors;
        int sign = 1;
        for (int i = 0; i < dimension; i++) {
            cofactors = getCofactor(matrix, 0, i, dimension);
            result += sign * matrix[0][i] * determinant(cofactors, dimension - 1);
            sign = -sign;
        }
        return result;
    }

    private double[][] getCofactor(double[][] matrix, int p, int q, int dimension) {
        double[][] cofactor = new double[dimension - 1][dimension - 1];
        int i = 0;
        int j = 0;
        for (int row = 0; row < dimension; row++) {
            for (int col = 0; col < dimension; col++) {
                if (row != p && col != q) {
                    cofactor[i][j] = matrix[row][col];
                    j++;
                    if (j == dimension - 1) {
                        j = 0;
                        i++;
                    }
                }
            }
        }
        return cofactor;
    }

}
