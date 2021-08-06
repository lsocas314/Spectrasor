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

/**
 * Class to storage raw ASCII data.
 *
 * @author L.B.P.Socas
 */
public final class RawData {

    // ######### Public API ##########
    
    /**
     * Create an instance of raw ASCII data specifying a name and x unit type.
     *
     * @param name the name to identify the loaded data.
     * @param data 2-dimension array containing the numeric data loaded.
     */
    public RawData(String name, double[][] data) {
        this.name = name;
        this.data = data;
    }

    /**
     * Get the name attribute.
     *
     * @return the name to identify the loaded data.
     */
    public String getName() {
        return name;
    }

    /**
     * Get the data attribute.
     *
     * @return 2-dimension array containing the numeric data loaded.
     */
    public double[][] getData() {
        return data;
    }

    // ######### Private implementation ##########
    
    private final String name;
    private final double[][] data;

}
