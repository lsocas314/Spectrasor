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
 * Class containing the model elements for fraction calculation.
 *
 * @author L.B.P.Socas
 */
public final class FractionCalculationModel implements Serializable {

    // ######### Public API ##########
    
    /**
     * List of the assays in the model.
     */
    public final ArrayList<MultispectralPhasor> assays = new ArrayList<>();

    /**
     * A FractionCalculator object used to perform unmixing.
     */
    public final FractionCalculator calculator = new FractionCalculator();

    /**
     * Get the reference points stored in the model for a certain harmonic
     * index.
     *
     * @param harmonicIndex the index of the interest harmonic value.
     * @return a list of the reference points stored for the specified harmonic.
     */
    public ArrayList<ReferencePoint> getPointsAt(int harmonicIndex) {
        return calculator.points[harmonicIndex];
    }

    /**
     * Perform a recalculation of the stored fraction values based on the
     * current model.
     */
    public void recalculateFractions() {
        currentFractions.clear();
        for (MultispectralPhasor assay : assays) {
            double[][] tempValues = new double[assay.spectra.size()][];
            for (int i = 0; i < assay.spectra.size(); i++) {
                tempValues[i] = calculator.getFractions(assay, i);
            }
            currentFractions.add(tempValues);
        }
    }

    /**
     * Get the fraction value for a specified assay, spectrum and reference.
     *
     * @param assayIndex the index of the assay of interest.
     * @param spectrumIndex the index of the spectrum of interest
     * @param referenceIndex the index of the reference of interest
     * @return
     */
    public double getFractionFor(int assayIndex, int spectrumIndex, int referenceIndex) {
        return currentFractions.get(assayIndex)[spectrumIndex][referenceIndex];
    }

    // ######### Private implementation ##########
    
    private final ArrayList<double[][]> currentFractions = new ArrayList<>();

}
