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
package mvc.fractioncalculation;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import model.Constants;
import model.FractionCalculator;
import model.FractionEquation;
import model.ReferencePoint;
import views.list.ListView;
import views.list.ListViewDelegate;
import views.list.Listeable;

/**
 * A dialog to set the system of linear equations to use for fraction
 * calculation.
 *
 * @author L.B.P.Socas
 */
final class EquationsDialog extends JDialog {

    // ######### Internal implementation ##########
    
    EquationsDialog(FractionCalculator model, JDialog owner, EquationsSettedDelegate delegate) {
        super(owner, "Set equations for a " + model.getNumberOfComponents() + " components system", Dialog.ModalityType.DOCUMENT_MODAL);
        this.model = model;
        this.delegate = delegate;
        referencePointsList = new ListView(
                7,
                "Reference points",
                new ListDelegateHandler(ListDelegateHandler.REFERENCE_LIST)
        );
        equationList = new ListView(
                7,
                "Equations system",
                new ListDelegateHandler(ListDelegateHandler.EQUATIONS_LIST)
        );
        cmbHarmonic.setSelectedIndex(harmonicIndex);
        layoutComponents();
        initializeComponents();
        selectedReferenceIndex = model.points[harmonicIndex].size() - 1;
        referencePointsList.updateList();
        selectedEquationIndex = model.system.size() - 1;
        equationList.updateList();
        updateButtons();
    }
    
    // ######### Private implementation ##########

    /* Attributes */
    
    private final FractionCalculator model;
    private boolean unityEquationAdded = false;
    private int selectedReferenceIndex = -1;
    private int selectedEquationIndex = -1;
    private int harmonicIndex = 3;

    /* Visual components */
    
    private final EquationsSettedDelegate delegate;
    private final ListView equationList;
    private final ListView referencePointsList;
    private final JComboBox cmbHarmonic = new JComboBox(Constants.harmonicStr());
    private final JButton btnAddGEquation = new JButton("Add G equations >>>");
    private final JButton btnAddSEquation = new JButton("Add S equations >>>");
    private final JButton btnAddUnityEquation = new JButton("Add fraction sum equation");
    private final JButton btnRemoveEquation = new JButton("<<< Remove equation");
    private final JButton btnDone = new JButton("Done");

    /* Visual methods */
    
    private void layoutComponents() {
        JPanel mainButtonsPanel = new JPanel(new GridLayout(5, 1, 5, 5));
        mainButtonsPanel.add(cmbHarmonic);
        mainButtonsPanel.add(btnAddGEquation);
        mainButtonsPanel.add(btnAddSEquation);
        mainButtonsPanel.add(btnAddUnityEquation);
        mainButtonsPanel.add(btnRemoveEquation);

        JLabel settingsTitle = new JLabel("Settings");
        settingsTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        mainButtonsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.PAGE_AXIS));
        centerPanel.add(settingsTitle);
        centerPanel.add(Box.createVerticalStrut(5));
        centerPanel.add(mainButtonsPanel);

        JPanel topPanel = new JPanel(new GridLayout(1, 3, 5, 5));
        topPanel.add(referencePointsList);
        topPanel.add(centerPanel);
        topPanel.add(equationList);

        JPanel bottomButtonsPanel = new JPanel();
        bottomButtonsPanel.setLayout(new BoxLayout(bottomButtonsPanel, BoxLayout.LINE_AXIS));
        bottomButtonsPanel.add(btnDone);

        topPanel.setAlignmentX(Component.RIGHT_ALIGNMENT);
        bottomButtonsPanel.setAlignmentX(Component.RIGHT_ALIGNMENT);

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.PAGE_AXIS));
        contentPanel.add(topPanel);
        contentPanel.add(Box.createVerticalStrut(5));
        contentPanel.add(bottomButtonsPanel);

        getContentPane().setLayout(new BorderLayout(5, 5));
        getContentPane().add(Box.createVerticalStrut(5), BorderLayout.NORTH);
        getContentPane().add(Box.createHorizontalStrut(5), BorderLayout.WEST);
        getContentPane().add(Box.createVerticalStrut(5), BorderLayout.SOUTH);
        getContentPane().add(Box.createHorizontalStrut(5), BorderLayout.EAST);
        getContentPane().add(contentPanel, BorderLayout.CENTER);
    }

    private void initializeComponents() {
        btnAddGEquation.addActionListener((ActionEvent e) -> {
            addEquation(FractionEquation.G_COORD);
        });
        btnAddSEquation.addActionListener((ActionEvent e) -> {
            addEquation(FractionEquation.S_COORD);
        });
        btnAddUnityEquation.addActionListener((ActionEvent e) -> {
            addEquation(FractionEquation.UNITY_COORD);
        });
        btnRemoveEquation.addActionListener((ActionEvent e) -> {
            FractionEquation temp = model.system.remove(selectedEquationIndex);
            if (temp.isUnityEquation()) {
                unityEquationAdded = false;
            }
            selectedEquationIndex = model.system.size() - 1;
            equationList.updateList();
            updateButtons();
        });
        btnDone.addActionListener((ActionEvent e) -> {
            if (model.systemIsCorrect()) {
                delegate.equationsSetted();
                dispose();
            } else {
                JOptionPane.showMessageDialog(
                        null,
                        "This system is not soluble and no fraction will be calculated",
                        "Something is wrong...",
                        JOptionPane.ERROR_MESSAGE);
                dispose();
            }
        });
        cmbHarmonic.addActionListener((ActionEvent e) -> {
            harmonicIndex = cmbHarmonic.getSelectedIndex();
            selectedReferenceIndex = model.points[harmonicIndex].size() - 1;
            referencePointsList.updateList();
            updateButtons();
        });
    }

    private void updateButtons() {
        btnAddGEquation.setEnabled(validData());
        btnAddSEquation.setEnabled(validData());
        btnAddUnityEquation.setEnabled(!unityEquationAdded);
        btnRemoveEquation.setEnabled(selectedEquationIndex >= 0);
    }

    /* Helper methods */
    
    private boolean validData() {
        return model.points[harmonicIndex].size() >= model.getNumberOfComponents();
    }

    private void addEquation(int coordType) {
        if (coordType == FractionEquation.UNITY_COORD) {
            model.system.add(new FractionEquation(model.getNumberOfComponents()));
            unityEquationAdded = true;
        } else {
            double[] values = new double[model.getNumberOfComponents()];
            for (int i = 0; i < values.length; i++) {
                values[i] = model.points[harmonicIndex].get(i).getCoor()[coordType];
            }
            FractionEquation newEquation = new FractionEquation(coordType, harmonicIndex, values);
            if (model.system.contains(newEquation)) {
                JOptionPane.showMessageDialog(
                        null,
                        "That euqation is already in the system!",
                        "Something is wrong...",
                        JOptionPane.ERROR_MESSAGE);
            } else {
                model.system.add(newEquation);
            }
        }
        selectedEquationIndex = model.system.size() - 1;
        equationList.updateList();
        updateButtons();
    }

    /* List Handler */
    
    private class ListDelegateHandler implements ListViewDelegate {

        static final int REFERENCE_LIST = 0;
        static final int EQUATIONS_LIST = 1;

        final int type;

        ListDelegateHandler(int type) {
            this.type = type;
        }

        @Override
        public boolean canSelectMultipleRows() {
            return false;
        }

        @Override
        public ArrayList<Listeable> getData() {
            switch (type) {
                case EQUATIONS_LIST:
                    ArrayList<Listeable> list = new ArrayList<>();
                    for (FractionEquation equation : model.system) {
                        list.add(equation);
                    }
                    return list;
                case REFERENCE_LIST:
                    list = new ArrayList<>();
                    for (ReferencePoint point : model.points[harmonicIndex]) {
                        list.add(point);
                    }
                    return list;
            }
            return null;
        }

        @Override
        public int[] getSelectedIndices() {
            switch (type) {
                case EQUATIONS_LIST:
                    return new int[]{selectedEquationIndex};
                case REFERENCE_LIST:
                    return new int[]{selectedReferenceIndex};
            }
            return null;
        }

        @Override
        public void selectionChanged(int[] newIndices) {
            if (newIndices == null || newIndices.length == 0) {
                return;
            }
            switch (type) {
                case EQUATIONS_LIST:
                    selectedEquationIndex = newIndices[0];
                    break;
                case REFERENCE_LIST:
                    selectedReferenceIndex = newIndices[0];
                    updateButtons();
                    break;
            }
        }

    }

}
