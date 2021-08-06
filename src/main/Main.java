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
package main;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.imageio.ImageIO;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import mvc.fractioncalculation.FractionCalculationVC;
import mvc.multispectralphasor.MultispectralPhasorVC;

/**
 * Entry point class
 *
 * @author L.B.P.Socas
 */
public class Main {
    
    // ######### Public API ##########

    /**
     * Entry point method
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        new Main();
    }
    
    // ######### Private implementation ##########

    private final JFrame view;
    private final JButton multispectralPhasors;
    private final JButton fractionCalculation;
    private final JLabel description;

    private Main() {
        view = new JFrame("Spectrasor");
        multispectralPhasors = new JButton();
        fractionCalculation = new JButton();
        try {
            Image i = ImageIO.read(getClass().getResource("/resources/MSPIcon.png"));
            multispectralPhasors.setIcon(new ImageIcon(i));
            i = ImageIO.read(getClass().getResource("/resources/FractionIcon.png"));
            fractionCalculation.setIcon(new ImageIcon(i));
        } catch (Exception e) {
            
        }
        description = new JLabel("Select a tool to use");
        multispectralPhasors.setMargin(new Insets(0, 0, 0, 0));
        multispectralPhasors.setBackground(Color.WHITE);
        multispectralPhasors.setFocusable(false);
        multispectralPhasors.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseEntered(MouseEvent e) {
                description.setText("Multispectral phasors tool...");
            }

            @Override
            public void mouseExited(MouseEvent e) {
                description.setText("Select a tool to use");
            }

        });
        multispectralPhasors.addActionListener((ActionEvent e) -> {
           new MultispectralPhasorVC(view);
        });
        fractionCalculation.setMargin(new Insets(0, 0, 0, 0));
        fractionCalculation.setBackground(Color.WHITE);
        fractionCalculation.setFocusable(false);
        fractionCalculation.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseEntered(MouseEvent e) {
                description.setText("Components unmixing tool...");
            }

            @Override
            public void mouseExited(MouseEvent e) {
                description.setText("Select a tool to use");
            }

        });
        fractionCalculation.addActionListener((ActionEvent e) -> {
            new FractionCalculationVC(view);
        });
        layoutComponents();
        view.pack();
        view.setLocationRelativeTo(null);
        view.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        view.setResizable(false);
        view.setVisible(true);
    }

    private void layoutComponents() {
        JPanel buttonsPanel = new JPanel(new GridLayout(1, 2, 10, 10));
        buttonsPanel.add(multispectralPhasors);
        buttonsPanel.add(fractionCalculation);
        
        description.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.PAGE_AXIS));
        contentPanel.add(buttonsPanel);
        contentPanel.add(Box.createVerticalStrut(5));
        contentPanel.add(description);
        
        view.getContentPane().setLayout(new BorderLayout(5, 5));
        view.getContentPane().add(Box.createVerticalStrut(5), BorderLayout.NORTH);
        view.getContentPane().add(Box.createHorizontalStrut(5), BorderLayout.WEST);
        view.getContentPane().add(Box.createVerticalStrut(5), BorderLayout.SOUTH);
        view.getContentPane().add(Box.createHorizontalStrut(5), BorderLayout.EAST);
        view.getContentPane().add(contentPanel, BorderLayout.CENTER);
    }

}
