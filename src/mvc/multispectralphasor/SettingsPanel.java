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
import java.awt.Component;
import java.awt.GridLayout;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import model.Constants;
import model.Spectrum;
import views.graph.GraphDelegate;

/**
 * The Settings panel for the MSP ViewController.
 *
 * @author L.B.P.Socas
 */
final class SettingsPanel extends JPanel {
    
    // ######### Internal implementation ##########

    SettingsPanel() {
        String[] shapes = new String[dotShapes.length];
        for (int i = 0; i < shapes.length; i++) {
            shapes[i] = dotShapes[i].description();
        }
        cmbShapes = new JComboBox(shapes);
        cmbColors = new JComboBox(Constants.colorOptions());
        cmbHarmonic = new JComboBox(Constants.harmonicStr());
        cmbXUnits = new JComboBox(Spectrum.X_UNITS_OPTIONS);
        layoutComponents();
    }

    final JComboBox cmbColors;
    final JComboBox cmbHarmonic;
    final JComboBox cmbShapes;
    final JComboBox cmbXUnits;
    final JButton btnLoad = new JButton("Load");
    final JButton btnSave = new JButton("Save");
    final JButton btnRemove = new JButton("Remove");
    final JButton btnClear = new JButton("Clear");
    final JButton btnZoom = new JButton("Set zoom");
    final JButton btnRanges = new JButton("Set ranges");
    final JLabel lblGValue = new JLabel("G = -----");
    final JLabel lblSValue = new JLabel("S = -----");
    final JLabel lblThetaValue = new JLabel("θ = -----");
    final JLabel lblRoValue = new JLabel("ρ = -----");

    final GraphDelegate.DotShape[] dotShapes = new GraphDelegate.DotShape[]{
        GraphDelegate.DotShape.rectangle,
        GraphDelegate.DotShape.circle,
        GraphDelegate.DotShape.triangle,
        GraphDelegate.DotShape.cross
    };
    
    // ######### Private implementation ##########

    private void layoutComponents() {
        JPanel upperButtons = new JPanel(new GridLayout(3, 2, 5, 5));
        upperButtons.add(btnLoad);
        upperButtons.add(btnSave);
        upperButtons.add(btnRemove);
        upperButtons.add(btnClear);
        upperButtons.add(btnRanges);
        upperButtons.add(btnZoom);
        JPanel colorPanel = new JPanel();
        colorPanel.setLayout(new BoxLayout(colorPanel, BoxLayout.LINE_AXIS));
        colorPanel.add(new JLabel("Colors: "));
        colorPanel.add(cmbColors);
        JPanel shapePanel = new JPanel();
        shapePanel.setLayout(new BoxLayout(shapePanel, BoxLayout.LINE_AXIS));
        shapePanel.add(new JLabel("Shapes: "));
        shapePanel.add(cmbShapes);
        JPanel harmonicPanel = new JPanel();
        harmonicPanel.setLayout(new BoxLayout(harmonicPanel, BoxLayout.LINE_AXIS));
        harmonicPanel.add(new JLabel("Harmonic: "));
        harmonicPanel.add(cmbHarmonic);
        JPanel unitsPanel = new JPanel();
        unitsPanel.setLayout(new BoxLayout(unitsPanel, BoxLayout.LINE_AXIS));
        unitsPanel.add(new JLabel("X units: "));
        unitsPanel.add(cmbXUnits);
        JPanel phasorUpperSettings = new JPanel(new GridLayout(1, 2, 5, 5));
        phasorUpperSettings.add(unitsPanel);
        phasorUpperSettings.add(harmonicPanel);
        JPanel phasorLowerSettings = new JPanel(new GridLayout(1, 2, 5, 5));
        phasorLowerSettings.add(colorPanel);
        phasorLowerSettings.add(shapePanel);
        JPanel phasorSettings = new JPanel(new GridLayout(2, 1, 5, 5));
        phasorSettings.add(phasorUpperSettings);
        phasorSettings.add(phasorLowerSettings);
        JPanel phasorInfo = new JPanel(new GridLayout(1, 4, 5, 5));
        phasorInfo.add(lblGValue);
        phasorInfo.add(lblSValue);
        phasorInfo.add(lblRoValue);
        phasorInfo.add(lblThetaValue);
        upperButtons.setAlignmentX(Component.CENTER_ALIGNMENT);
        phasorSettings.setAlignmentX(Component.CENTER_ALIGNMENT);
        phasorInfo.setAlignmentX(Component.CENTER_ALIGNMENT);
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.PAGE_AXIS));
        contentPanel.add(upperButtons);
        contentPanel.add(Box.createVerticalStrut(5));
        contentPanel.add(phasorSettings);
        contentPanel.add(Box.createVerticalStrut(5));
        contentPanel.add(phasorInfo);
        setLayout(new BorderLayout(3, 3));
        add(Box.createVerticalStrut(15), BorderLayout.NORTH);
        add(Box.createHorizontalStrut(3), BorderLayout.WEST);
        add(Box.createVerticalStrut(5), BorderLayout.SOUTH);
        add(Box.createHorizontalStrut(3), BorderLayout.EAST);
        add(contentPanel, BorderLayout.CENTER);
    }

}
