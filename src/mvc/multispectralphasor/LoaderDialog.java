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
package mvc.multispectralphasor;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import model.Spectrum;

/**
 * Dialog used to define the data to be loaded.
 *
 * @author L.B.P.Socas
 */
final class LoaderDialog extends JDialog {
    
    // ######### Internal implementation ##########
    
    interface LoaderDelegate {
        
        void loadSpectra(String name, ArrayList<Spectrum> spectra);
        
    }
    
    LoaderDialog(int amountOfLists, ArrayList<Spectrum> allLoaded, LoaderDelegate delegate, JDialog owner) {
        super(owner, "Load new spectra", Dialog.ModalityType.DOCUMENT_MODAL);
        this.allLoaded = allLoaded;
        this.delegate = delegate;
        nameText = new JTextField("List" + (amountOfLists + 1));
        check = new JCheckBox[allLoaded.size()];
        for (int i = 0; i < allLoaded.size(); i++) {
            check[i] = new JCheckBox(allLoaded.get(i).getName());
            check[i].setSelected(true);
        }
        addButtonListeners();
        layoutComponents();
    }
    
    // ######### Private implementation ##########
    
    private final ArrayList<Spectrum> allLoaded;
    private final LoaderDelegate delegate;
    private final JTextField nameText;
    private final JCheckBox[] check;
    private final JButton btnLoad = new JButton("Load");
    private final JButton btnCancel = new JButton("Cancel");
    
    private void addButtonListeners() {
        btnLoad.addActionListener((ActionEvent e) -> {
            ArrayList<Spectrum> spectraToLoad = new ArrayList<>();
            for (int i = 0; i < check.length; i++) {
                if (check[i].isSelected()) {
                    spectraToLoad.add(allLoaded.get(i));
                }
            }
            delegate.loadSpectra(nameText.getText(), spectraToLoad);
            dispose();
        });
        btnCancel.addActionListener((ActionEvent e) -> {
            dispose();
        });
    }
    
    private void layoutComponents() {
        JPanel spectraPanel = new JPanel(new GridLayout(check.length, 1, 5, 5));
        for (JCheckBox box : check) {
            spectraPanel.add(box);
        }
        JScrollPane allCheckPanel = new JScrollPane(spectraPanel);
        Dimension d = allCheckPanel.getPreferredSize();
        d.height = 200;
        allCheckPanel.setPreferredSize(d);
        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 5, 5));
        buttonPanel.add(btnCancel);
        buttonPanel.add(btnLoad);
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.PAGE_AXIS));
        contentPanel.add(nameText);
        contentPanel.add(allCheckPanel);
        contentPanel.add(buttonPanel);
        getContentPane().setLayout(new BorderLayout(5, 5));
        getContentPane().add(Box.createVerticalStrut(5), BorderLayout.NORTH);
        getContentPane().add(Box.createHorizontalStrut(5), BorderLayout.WEST);
        getContentPane().add(Box.createVerticalStrut(5), BorderLayout.SOUTH);
        getContentPane().add(Box.createHorizontalStrut(5), BorderLayout.EAST);
        getContentPane().add(contentPanel, BorderLayout.CENTER);
    }
    
}
