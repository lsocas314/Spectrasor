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
 * Enumerator for of all the file types used.
 *
 * @author L.B.P.Socas
 */
public enum FileType {

    // ######### Public API ##########
    
    /**
     * Comma separated values.
     */
    csv,
    /**
     * Internal file format for spectra data.
     */
    spr,
    /**
     * Internal file format for a list of spectra data.
     */
    sprl,
    /**
     * Internal file format for storing reference points.
     */
    rfp,
    /**
     * Internal file format for spectra fraction analysis.
     */
    sprf;

    /**
     * Description of current file format value.
     *
     * @return A String describing the file format and its extension.
     */
    public final String formatDescription() {
        switch (this) {
            case csv:
                return "Comma separated values file (.csv)";
            case spr:
                return "Spectrasor file (.spr)";
            case sprl:
                return "Spectrasor list file (.sprl)";
            case rfp:
                return "Reference point(s) file (.rfp)";
            case sprf:
                return "Spectrasor fraction analysis file (.sprf)";
        }
        return null;
    }

}
