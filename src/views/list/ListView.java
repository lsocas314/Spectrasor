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

import java.awt.Component;
import java.util.ArrayList;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;

/**
 * Define a view for presenting a list of elements in the user interface.
 *
 * @author L.B.P.Socas
 */
public final class ListView extends JPanel {
    
    // ######### Public API ##########
    
    /**
     * Construct a new ListView with a specified number of visible rows and a title.
     *
     * @param rows number of visible rows in the list.
     * @param title the title to show above the list.
     * @param delegate the delegation handler.
     */
    public ListView(int rows, String title, ListViewDelegate delegate) {
        this.delegate = delegate;
        this.title = title;
        loadedList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        loadedList.setLayoutOrientation(JList.VERTICAL);
        loadedList.setSelectedIndex(-1);
        loadedList.addListSelectionListener((ListSelectionEvent e) -> {
            this.delegate.selectionChanged(loadedList.getSelectedIndices());
        });
        loadedList.setVisibleRowCount(rows);
        updateList();
        layoutComponents();
    }

    /**
     * Get the indices of the selected elements in the list.
     *
     * @return an array with the indices of the selected elements in the list.
     */
    public int[] getSelectedIndices() {
        return loadedList.getSelectedIndices();
    }

    /**
     * Update the elements of the list through the delegation handler.
     */
    public void updateList() {
        if (delegate == null) {
            loadedList.setEnabled(false);
            loadedList.setListData(new String[]{"Nothing loaded."});
            loadedList.setSelectedIndex(-1);
            loadedList.setEnabled(false);
            return;
        }
        ArrayList<Listeable> loadedData = delegate.getData();
        if (loadedData == null || loadedData.isEmpty()) {
            loadedList.setEnabled(false);
            loadedList.setListData(new String[]{"Nothing loaded."});
            loadedList.setSelectedIndex(-1);
            loadedList.setEnabled(false);
            delegate.selectionChanged(new int[]{-1});
        } else {
            String[] loadedDataNames = new String[loadedData.size()];
            for (int i = 0; i < loadedDataNames.length; i++) {
                loadedDataNames[i] = loadedData.get(i).getName();
            }
            int[] selectedIndices = delegate.getSelectedIndices();
            loadedList.setListData(loadedDataNames);
            loadedList.setSelectedIndices(selectedIndices);
            loadedList.setEnabled(true);
        }
        if (delegate.canSelectMultipleRows()) {
            loadedList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        } else {
            loadedList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        }
    }
    
    // ######### Private implementation ##########

    private final JList loadedList = new JList();
    private final ListViewDelegate delegate;
    private String title = null;

    private void layoutComponents() {
        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        JScrollPane listScrollPane = new JScrollPane(loadedList);
        listScrollPane.setAlignmentX(Component.CENTER_ALIGNMENT);
        if (title != null) {
            JLabel tilteLabel = new JLabel(title);
            tilteLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            add(tilteLabel);
        }
        add(listScrollPane);
    }

}
