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
package auxiliary;

import java.text.DecimalFormat;

/**
 * Static class used for common mathematical operations.
 *
 * @author L.B.P.Socas
 */
public final class MathFeatures {

    // ######### Public API ##########
    
    /**
     * Find the index in an array of the (closest) value provided.
     *
     * @param value the value to seek in the array.
     * @param data the array to find the value.
     * @return the index in the array for the closest value found.
     */
    public static int findIndexFor(double value, double[] data) {
        double difference = Double.MAX_VALUE;
        int index = 0;
        for (int i = 0; i < data.length; i++) {
            double temp = Math.abs(value - data[i]);
            if (temp < difference) {
                difference = temp;
                index = i;
            }
        }
        return index;
    }

    /**
     * Get the maximum value in a provided array of numbers.
     *
     * @param input the array to find the maximum value.
     * @return the maximum value found in the array.
     */
    public static double getMax(double[] input) {
        double tempMax = -Double.MAX_VALUE;
        for (int i = 0; i < input.length; i++) {
            if (input[i] > tempMax) {
                tempMax = input[i];
            }
        }
        return tempMax;
    }

    /**
     * Get the minimum value in a provided array of numbers.
     *
     * @param input the array to find the minimum value.
     * @return the minimum value found in the array.
     */
    public static double getMin(double[] input) {
        double tempMin = Double.MAX_VALUE;
        for (int i = 0; i < input.length; i++) {
            if (input[i] < tempMin) {
                tempMin = input[i];
            }
        }
        return tempMin;
    }

    /**
     * Calculate the integral of an interval of a curve using the trapezoidal
     * rule.
     *
     * @param minX the minimum x value.
     * @param maxX the maximum x value.
     * @param inputX array containing the x values.
     * @param inputY array containing the y values.
     * @return the integration result for the specify interval.
     */
    public static double integrate(double minX, double maxX, double[] inputX, double[] inputY) {
        int index1 = findIndexFor(minX, inputX);
        int index2 = findIndexFor(maxX, inputX);
        int index0 = Math.min(index1, index2);
        int indexF = Math.max(index1, index2);
        double tempArea = 0;
        for (int i = index0; i < indexF - 1; i++) {
            tempArea += Math.abs(inputX[i + 1] - inputX[i]) * Math.abs(0.5 * (inputY[i] + inputY[i + 1]));
        }
        return tempArea;
    }

    /**
     * Create a new array by dividing each value of the input with a specify
     * divisor.
     *
     * @param divisor number to divide the array values.
     * @param input array with the values to be dived by the divisor.
     * @return a new array containing the division results.
     */
    public static double[] divideByValue(double divisor, double[] input) {
        double[] output = new double[input.length];
        for (int i = 0; i < input.length; i++) {
            output[i] = input[i] / divisor;
        }
        return output;
    }

    /**
     * Construct a string from a number by rounding it to the first significant
     * figure of a provided uncertainty.
     *
     * @param showError specify if the output string also includes the error
     * value separated by a ± operator.
     * @param value the value to be rounded.
     * @param error the uncertainty that defines the number of significant
     * figures.
     * @return a string containing the rounded number with/without the ± error.
     */
    public static String getStringOfValueRoundedByError(boolean showError, double value, double error) {
        if (error == 0) {
            return String.valueOf(value);
        }
        double errorSignificantDigits = Math.floor(Math.log10(error));
        double errorCorrectionFactor = Math.pow(10, errorSignificantDigits);
        double errorCorrected = error / errorCorrectionFactor;
        double errorRounded = Math.round(errorCorrected) * errorCorrectionFactor;
        double valueSignificantDigits = Math.floor(Math.log10(value));
        double valueCorrectionFactor;
        if (valueSignificantDigits < errorSignificantDigits) {
            valueCorrectionFactor = Math.pow(10, valueSignificantDigits);
        } else {
            valueCorrectionFactor = Math.pow(10, errorSignificantDigits);
        }
        double valueCorrected = value / valueCorrectionFactor;
        double valueRounded = Math.round(valueCorrected) * valueCorrectionFactor;
        DecimalFormat stringFormat = new DecimalFormat();
        stringFormat.setGroupingUsed(false);
        if (errorSignificantDigits > 0) {
            stringFormat.setMaximumFractionDigits(0);
            stringFormat.setMinimumFractionDigits(0);
        } else {
            stringFormat.setMaximumFractionDigits(-((int) errorSignificantDigits));
            stringFormat.setMinimumFractionDigits(-((int) errorSignificantDigits));
        }
        int maxIntegerDigits = (int) Math.max(errorSignificantDigits + 1, valueSignificantDigits + 1);
        maxIntegerDigits = (maxIntegerDigits <= 0) ? 1 : maxIntegerDigits;
        stringFormat.setMaximumIntegerDigits(maxIntegerDigits);
        if (showError) {
            return stringFormat.format(valueRounded) + " ± " + stringFormat.format(errorRounded);
        } else {
            return stringFormat.format(valueRounded);
        }
    }

}
