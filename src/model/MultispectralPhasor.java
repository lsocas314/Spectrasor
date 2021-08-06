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
import java.util.ArrayList;
import views.graph.GraphDelegate;
import views.list.Listeable;

/**
 * Class that defines a general phasor object.
 *
 * @author L.B.P.Socas
 */
public final class MultispectralPhasor implements Listeable {

    // ######### Public API ##########
    
    /**
     * Constant used for the index of the minimum value of the spectra range.
     */
    public static final int MIN_VALUE = 0;

    /**
     * Constant used for the index of the selected value of the spectra range.
     */
    public static final int SELECTED_VALUE = 1;

    /**
     * Constant used for the index of the maximum value of the spectra range.
     */
    public static final int MAX_VALUE = 2;

    /**
     * The index of the color used to represent the phasor.
     */
    public int colorIndex;

    /**
     * The type of the symbol used to represent the phasor. A value of the
     * {@code GraphDelegate.DotShape} enumerator: rectangle, circle, triangle or
     * plusSign.
     */
    public GraphDelegate.DotShape dotShape = GraphDelegate.DotShape.rectangle;

    /**
     * The selected 2-dimensional harmonic value used to calculate the phasor.
     * The values {0, x} or {x, 0} represents the phasors in a single dimension,
     * emission or excitation respectively. The value {0,0} is not allowed.
     */
    public double[] harmonic = new double[]{1, 1};

    /**
     * True if the phasor's plot is zoomed, false otherwise.
     */
    public boolean isZoomed = false;

    /**
     * The minimum G value for a zoomed phasor's plot.
     */
    public double minGZoom = -1;

    /**
     * The minimum S value for a zoomed phasor's plot.
     */
    public double minSZoom = -1;

    /**
     * The maximum G value for a zoomed phasor's plot.
     */
    public double maxGZoom = 1;

    /**
     * The maximum S value for a zoomed phasor's plot.
     */
    public double maxSZoom = 1;

    /**
     * An array containing the minimum, maximum and selected excitation values.
     */
    public double[] exXValues;

    /**
     * An array containing the minimum, maximum and selected emission values.
     */
    public double[] emXValues;

    /**
     * The global minimum excitation value.
     */
    public double minExX;

    /**
     * The global maximum excitation value.
     */
    public double maxExX;

    /**
     * The global minimum emission value.
     */
    public double minEmX;

    /**
     * The global maximum emission value.
     */
    public double maxEmX;

    /**
     * A list of all the spectra loaded.
     */
    public ArrayList<Spectrum> spectra = new ArrayList<>();

    /**
     * Get the emission values for a specified spectrum at the selected
     * excitation value.
     *
     * @param spectrumIndex the index of the spectrum in the spectra list.
     * @return an array containing all the emission values at the selected
     * excitation.
     */
    public double[] getEmissionIntensitiesFor(int spectrumIndex) {
        return spectra.get(spectrumIndex).getIntensitiesForExcitationAt(exXValues[SELECTED_VALUE]);
    }

    /**
     * Get the excitation values for a specified spectrum at the selected
     * emission value.
     *
     * @param spectrumIndex the index of the spectrum in the spectra list.
     * @return an array containing all the excitation values at the selected
     * emission.
     */
    public double[] getExcitationIntensitiesFor(int spectrumIndex) {
        return spectra.get(spectrumIndex).getIntensitiesForEmissionAt(emXValues[SELECTED_VALUE]);
    }

    /**
     * Calculate the phasor value for a specific spectrum and specific harmonic.
     *
     * @param spectrumIndex the index of the spectrum in the spectra list.
     * @param harmonic the 2-dimensional harmonic value used to calculate the
     * phasor. The values {0, x} or {x, 0} represents the phasors in a single
     * dimension, emission or excitation respectively. The value {0,0} is not
     * allowed.
     * @return the calculated [G, S] phasor. Null if the harmonic is set to be
     * {0,0}
     */
    public double[] getPhasor(int spectrumIndex, double[] harmonic) {
        if (harmonic[0] == 0) {
            if (harmonic[1] == 0) {
                return null;
            }
            return getSinglePhasor(true, spectrumIndex, harmonic[1], exXValues[SELECTED_VALUE]);
        } else if (harmonic[1] == 0) {
            return getSinglePhasor(false, spectrumIndex, harmonic[0], emXValues[SELECTED_VALUE]);
        } else {
            return getMultispectralPhasor(spectrumIndex, harmonic);
        }
    }

    /**
     * Get the index for the x axis values units.
     *
     * @return the index that represents the x axis values units.
     */
    public int getXUnitsIndex() {
        if (spectra.isEmpty()) {
            return 0;
        }
        return spectra.get(0).currentXUnitIndex;
    }

    /**
     * Set the name that identify the spectra list.
     *
     * @param name the new name for this spectra list.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Set the index of the x axis values unit
     *
     * @param xUnitIndex the index of x axis values unit.
     */
    public void setXUnitsIndex(int xUnitIndex) {
        for (Spectrum spectrum : spectra) {
            spectrum.currentXUnitIndex = xUnitIndex;
        }
    }

    /**
     * Reset the ranges for the spectra list. Set the exXValues and the
     * emXValues arrays to store the global minimum and maximum values and set
     * the selected value to be in the middle of each range.
     */
    public void resetRanges() {
        minEmX = -Double.MAX_VALUE;
        maxEmX = Double.MAX_VALUE;
        minExX = -Double.MAX_VALUE;
        maxExX = Double.MAX_VALUE;
        for (Spectrum spectrum : spectra) {
            if (minExX < spectrum.getMinExcitationValue()) {
                minExX = spectrum.getMinExcitationValue();
            }
            if (maxExX > spectrum.getMaxExcitationValue()) {
                maxExX = spectrum.getMaxExcitationValue();
            }
            if (minEmX < spectrum.getMinEmissionValue()) {
                minEmX = spectrum.getMinEmissionValue();
            }
            if (maxEmX > spectrum.getMaxEmissionValue()) {
                maxEmX = spectrum.getMaxEmissionValue();
            }
        }
        exXValues = new double[]{
            Math.min(minExX, maxExX),
            (maxExX + minExX) / 2,
            Math.max(minExX, maxExX)
        };
        emXValues = new double[]{
            Math.min(minEmX, maxEmX),
            (maxEmX + minEmX) / 2,
            Math.max(minEmX, maxEmX)
        };
    }

    @Override
    public String getName() {
        return name;
    }

    // ######### Private implementation ##########
    
    private String name;

    private double[] getMultispectralPhasor(int spectrumIndex, double[] harmonic) {
        Spectrum spectrum = spectra.get(spectrumIndex);
        double[] fullExX = spectrum.getExcitationXs();
        double[] fullEmX = spectrum.getEmissionXs();
        int minExIndex = MathFeatures.findIndexFor(exXValues[MIN_VALUE], fullExX);
        int maxExIndex = MathFeatures.findIndexFor(exXValues[MAX_VALUE], fullExX);
        int minEmIndex = MathFeatures.findIndexFor(emXValues[MIN_VALUE], fullEmX);
        int maxEmIndex = MathFeatures.findIndexFor(emXValues[MAX_VALUE], fullEmX);
        double total = 0;
        double realPart = 0;
        double imaginaryPart = 0;
        int fromEx = Math.min(minExIndex, maxExIndex);
        int toEx = Math.max(minExIndex, maxExIndex);
        int fromEm = Math.min(minEmIndex, maxEmIndex);
        int toEm = Math.max(minEmIndex, maxEmIndex);
        double globalMin = Math.min(exXValues[MIN_VALUE], emXValues[MIN_VALUE]);
        double globalMax = Math.max(exXValues[MAX_VALUE], emXValues[MAX_VALUE]);
        for (int i = fromEx; i < toEx; i++) {
            double Lx = (fullExX[i] - globalMin) / (globalMax - globalMin);
            for (int j = fromEm; j < toEm; j++) {
                double Lm = (fullEmX[j] - globalMin) / (globalMax - globalMin);
                double intensity = spectrum.getIntensityAt(i, j);
                double cos = Math.cos(2 * Math.PI * (Lx * harmonic[0] + Lm * harmonic[1]));
                double sin = Math.sin(2 * Math.PI * (Lx * harmonic[0] + Lm * harmonic[1]));
                realPart += intensity * cos;
                imaginaryPart += intensity * sin;
                total += intensity;
            }
        }
        return new double[]{realPart / total, imaginaryPart / total};
    }

    private double[] getSinglePhasor(boolean emission, int spectrumIndex, double harmonic, double xValue) {
        Spectrum spectrum = spectra.get(spectrumIndex);
        double total = 0;
        double realPart = 0;
        double imaginaryPart = 0;
        int minIndex;
        int maxIndex;
        int fixedIndex;
        double[] fullXs;
        if (emission) {
            fullXs = spectrum.getEmissionXs();
            fixedIndex = MathFeatures.findIndexFor(xValue, spectrum.getExcitationXs());
            minIndex = MathFeatures.findIndexFor(emXValues[MIN_VALUE], fullXs);
            maxIndex = MathFeatures.findIndexFor(emXValues[MAX_VALUE], fullXs);
        } else {
            fullXs = spectrum.getExcitationXs();
            fixedIndex = MathFeatures.findIndexFor(xValue, spectrum.getEmissionXs());
            minIndex = MathFeatures.findIndexFor(exXValues[MIN_VALUE], fullXs);
            maxIndex = MathFeatures.findIndexFor(exXValues[MAX_VALUE], fullXs);
        }
        int from = Math.min(minIndex, maxIndex);
        int to = Math.max(minIndex, maxIndex);
        for (int i = from; i < to; i++) {
            double L = (fullXs[i] - fullXs[minIndex]) / (fullXs[maxIndex] - fullXs[minIndex]);
            int exIndex = (emission) ? fixedIndex : i;
            int emIndex = (emission) ? i : fixedIndex;
            double intensity = spectrum.getIntensityAt(exIndex, emIndex);
            double cos = Math.cos(2 * Math.PI * L * harmonic);
            double sin = Math.sin(2 * Math.PI * L * harmonic);
            realPart += intensity * cos;
            imaginaryPart += intensity * sin;
            total += intensity;
        }
        return new double[]{realPart / total, imaginaryPart / total};
    }

}
