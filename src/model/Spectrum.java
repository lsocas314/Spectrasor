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

import auxiliary.RawData;
import auxiliary.MathFeatures;
import views.list.Listeable;

/**
 * Class that defines a general spectrum object.
 *
 * @author L.B.P.Socas
 */
public final class Spectrum implements Listeable {

    // ######### Public API ##########
    
    /**
     * Constant storing the x axis unit options.
     */
    public static final String[] X_UNITS_OPTIONS = new String[]{
        "λ (nm)",
        "v (1/cm)",
        "v (1/µm)"
    };

    /**
     * The constant index value for wavelength units.
     */
    public static final int X_UNIT_WAVELENGTH_INDEX = 0;

    /**
     * The constant index value for wavenumber units in 1/cm.
     */
    public static final int X_UNIT_WAVENUMBER_CM_INDEX = 1;

    /**
     * The constant index value for wavenumber units in 1/µm.
     */
    public static final int X_UNIT_WAVENUMBER_UM_INDEX = 2;

    /**
     * The selected x axis units for this spectrum.
     */
    public int currentXUnitIndex = 0;

    /**
     * Construct a spectrum object from loaded raw data.
     *
     * @param rawData a RawData object containing spectrum information.
     * @param dataXUnitIndex the index for the x axis units.
     */
    public Spectrum(RawData rawData, int dataXUnitIndex) {
        this(rawData.getName(), dataXUnitIndex, rawData.getData());
    }

    /**
     * Construct a spectrum object with a given name and x axis units index.
     *
     * @param name the name that identify the spectrum.
     * @param dataXUnitIndex the index for the x axis units.
     * @param data a 2-dimensional array containing the excitation-emission
     * data. The first row ({@code data[][0]}) contains the excitation x values,
     * and the first column ({@code data[0][]}) contains the emission x values,
     * the rest of the array ({@code data[i][j]}) contains the intensity values
     * for the excitation at the i value and emission at the j value. The number
     * at {@code data[0][0]} is ignored.
     */
    public Spectrum(String name, int dataXUnitIndex, double[][] data) {
        this.name = name;
        excitationX = new double[X_UNITS_OPTIONS.length][data.length - 1];
        for (int i = 0; i < excitationX[dataXUnitIndex].length; i++) {
            excitationX[dataXUnitIndex][i] = data[i + 1][0];
        }
        emissionX = new double[X_UNITS_OPTIONS.length][data[0].length - 1];
        for (int i = 0; i < emissionX[dataXUnitIndex].length; i++) {
            emissionX[dataXUnitIndex][i] = data[0][i + 1];
        }
        intensities = new double[excitationX[dataXUnitIndex].length][emissionX[dataXUnitIndex].length];
        for (int i = 1; i < data.length; i++) {
            for (int j = 1; j < data[0].length; j++) {
                intensities[i - 1][j - 1] = data[i][j];
            }
        }
        currentXUnitIndex = dataXUnitIndex;
        fillXValuesFromCurrent();
    }

    /**
     * Get the excitation values at the current selected units.
     *
     * @return the excitation values at the current selected units.
     */
    public double[] getExcitationXs() {
        return excitationX[currentXUnitIndex];
    }

    /**
     * Get the emission values at the current selected units.
     *
     * @return the emission values at the current selected units.
     */
    public double[] getEmissionXs() {
        return emissionX[currentXUnitIndex];
    }

    /**
     * Get the 1-dimensional excitation spectrum at a specified emission value.
     *
     * @param emX the emission value used to obtain the excitation spectrum.
     * @return a 1-dimensional array containing the excitation spectrum for the
     * specified emission value.
     */
    public double[] getIntensitiesForEmissionAt(double emX) {
        int index = MathFeatures.findIndexFor(emX, emissionX[currentXUnitIndex]);
        double[] intensityValues = new double[excitationX[currentXUnitIndex].length];
        for (int i = 0; i < intensityValues.length; i++) {
            intensityValues[i] = intensities[i][index];
        }
        return intensityValues;
    }

    /**
     * Get the 1-dimensional emission spectrum at a specified excitation value.
     *
     * @param exX the excitation value used to obtain the emission spectrum.
     * @return a 1-dimensional array containing the emission spectrum for the
     * specified excitation value.
     */
    public double[] getIntensitiesForExcitationAt(double exX) {
        int index = MathFeatures.findIndexFor(exX, excitationX[currentXUnitIndex]);
        return intensities[index];
    }

    /**
     * Get the intensity value for a specified excitation/emission indices.
     *
     * @param exIndex the index of the excitation value.
     * @param emIndex the index of the emission value.
     * @return the intensity value at the {@code [exIndex][emIndex]} position.
     */
    public double getIntensityAt(int exIndex, int emIndex) {
        return intensities[exIndex][emIndex];
    }

    /**
     * Get the minimum excitation value of the spectrum.
     *
     * @return the minimum excitation value.
     */
    public double getMinExcitationValue() {
        return excitationX[currentXUnitIndex][0];
    }

    /**
     * Get the maximum excitation value of the spectrum.
     *
     * @return the maximum excitation value.
     */
    public double getMaxExcitationValue() {
        return excitationX[currentXUnitIndex][excitationX[currentXUnitIndex].length - 1];
    }

    /**
     * Get the minimum emission value of the spectrum.
     *
     * @return the minimum emission value.
     */
    public double getMinEmissionValue() {
        return emissionX[currentXUnitIndex][0];
    }

    /**
     * Get the maximum emission value of the spectrum.
     *
     * @return the maximum emission value.
     */
    public double getMaxEmissionValue() {
        return emissionX[currentXUnitIndex][emissionX[currentXUnitIndex].length - 1];
    }

    @Override
    public String getName() {
        return name;
    }

    // ######### Private implementation ##########
    
    private final String name;
    private final double[][] excitationX;
    private final double[][] emissionX;
    private final double[][] intensities;

    private void fillXValuesFromCurrent() {
        switch (currentXUnitIndex) {
            case X_UNIT_WAVELENGTH_INDEX:
                for (int i = 0; i < excitationX[currentXUnitIndex].length; i++) {
                    excitationX[X_UNIT_WAVENUMBER_CM_INDEX][i] = 10000000 / excitationX[currentXUnitIndex][i];
                }
                for (int i = 0; i < emissionX[currentXUnitIndex].length; i++) {
                    emissionX[X_UNIT_WAVENUMBER_CM_INDEX][i] = 10000000 / emissionX[currentXUnitIndex][i];
                }
                for (int i = 0; i < excitationX[currentXUnitIndex].length; i++) {
                    excitationX[X_UNIT_WAVENUMBER_UM_INDEX][i] = 1000 / excitationX[currentXUnitIndex][i];
                }
                for (int i = 0; i < emissionX[currentXUnitIndex].length; i++) {
                    emissionX[X_UNIT_WAVENUMBER_UM_INDEX][i] = 1000 / emissionX[currentXUnitIndex][i];
                }
                break;
            case X_UNIT_WAVENUMBER_CM_INDEX:
                for (int i = 0; i < excitationX[currentXUnitIndex].length; i++) {
                    excitationX[X_UNIT_WAVELENGTH_INDEX][i] = 10000000 / excitationX[currentXUnitIndex][i];
                }
                for (int i = 0; i < emissionX[currentXUnitIndex].length; i++) {
                    emissionX[X_UNIT_WAVELENGTH_INDEX][i] = 10000000 / emissionX[currentXUnitIndex][i];
                }
                for (int i = 0; i < excitationX[currentXUnitIndex].length; i++) {
                    excitationX[X_UNIT_WAVENUMBER_UM_INDEX][i] = excitationX[currentXUnitIndex][i] / 10000;
                }
                for (int i = 0; i < emissionX[currentXUnitIndex].length; i++) {
                    emissionX[X_UNIT_WAVENUMBER_UM_INDEX][i] = emissionX[currentXUnitIndex][i] / 10000;
                }
                break;
            case X_UNIT_WAVENUMBER_UM_INDEX:
                for (int i = 0; i < excitationX[currentXUnitIndex].length; i++) {
                    excitationX[X_UNIT_WAVELENGTH_INDEX][i] = 1000 / excitationX[currentXUnitIndex][i];
                }
                for (int i = 0; i < emissionX[currentXUnitIndex].length; i++) {
                    emissionX[X_UNIT_WAVELENGTH_INDEX][i] = 1000 / emissionX[currentXUnitIndex][i];
                }
                for (int i = 0; i < excitationX[currentXUnitIndex].length; i++) {
                    excitationX[X_UNIT_WAVENUMBER_CM_INDEX][i] = excitationX[currentXUnitIndex][i] * 10000;
                }
                for (int i = 0; i < emissionX[currentXUnitIndex].length; i++) {
                    emissionX[X_UNIT_WAVENUMBER_CM_INDEX][i] = emissionX[currentXUnitIndex][i] * 10000;
                }
                break;
        }
    }

}
