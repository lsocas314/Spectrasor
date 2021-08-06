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
package views.list;

import java.util.ArrayList;

/**
 * Abstract interface to handle ListView delegation.
 *
 * @author L.B.P.Socas
 */
public interface ListViewDelegate {

    /**
     * Called when the user changes the selected elements of the list.
     *
     * @param newIndices the indices of the new selected elements.
     */
    void selectionChanged(int[] newIndices);

    /**
     * Defines if is possible to select multiple rows in the ListView.
     *
     * @return true if is possible to select more than one row, false otherwise.
     */
    boolean canSelectMultipleRows();

    /**
     * Get the list of all the listeable elements currently in the ListView.
     *
     * @return the list of all the elements in the ListView.
     */
    ArrayList<Listeable> getData();

    /**
     * Get the indices for the selected elements on the ListView.
     *
     * @return he indices for the selected elements.
     */
    int[] getSelectedIndices();

}
