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

import auxiliary.FileType;
import auxiliary.FilesIO;
import auxiliary.RawData;
import auxiliary.MathFeatures;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import model.Constants;
import model.MultispectralPhasor;
import model.Spectrum;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import views.common.ColorSource;
import views.graph.Graph;
import views.graph.GraphDelegate;
import views.list.Listeable;
import views.list.ListView;
import views.list.ListViewDelegate;

/**
 * MultispectralPhasor ViewController class.
 *
 * @author L.B.P.Socas
 */
public final class MultispectralPhasorVC {

    // ######### Public API ##########
    
    /**
     * Creates a new Multispectral phasor View Controller.
     *
     * @param owner the frame that owns this dialog.
     */
    public MultispectralPhasorVC(JFrame owner) {
        view = new JDialog(owner, "Multispectral phasor analysis", Dialog.ModalityType.APPLICATION_MODAL);
        settings = new SettingsPanel();
        excitationGraphHandler = new SpectraGraphDelegateHandler(SpectraGraphDelegateHandler.EXCITATION);
        excitationGraph = new Graph(
                1.9 / 1.0,
                excitationGraphHandler
        );
        emissionGraphHandler = new SpectraGraphDelegateHandler(SpectraGraphDelegateHandler.EMISSION);
        emissionGraph = new Graph(
                1.9 / 1.0,
                emissionGraphHandler
        );
        fullPhasorsGraphHandler = new PhasorsGraphDelegateHandler(PhasorsGraphDelegateHandler.NORMAL);
        phasorGraph = new Graph(
                1.0 / 1.0,
                fullPhasorsGraphHandler
        );
        zoomedPhasorsGraphHandler = new PhasorsGraphDelegateHandler(PhasorsGraphDelegateHandler.ZOOMED);
        phasorZoomGraph = new Graph(
                1.0 / 1.0,
                zoomedPhasorsGraphHandler
        );
        spectraList = new ListView(
                10,
                "               Spectra               ",
                new ListDelegateHandler(ListDelegateHandler.SPECTRA)
        );
        assayList = new ListView(
                10,
                "               Assays               ",
                new ListDelegateHandler(ListDelegateHandler.ASSAYS)
        );
        resetGraphs();
        layoutComponents();
        initializeSettings();
        updateSettings();
        view.pack();
        view.setLocationRelativeTo(owner);
        view.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        view.setVisible(true);
        view.setResizable(false);
    }

    // ######### Private implementation ##########

    /* Attributes */
    
    private final ArrayList<MultispectralPhasor> model = new ArrayList<>();
    private boolean freezeComboBox = false;
    private boolean savingImages = false;
    private int[] assaySelectedIndices;
    private int[] spectrumSelectedIndices;

    /* Visual components */
    
    private final Graph excitationGraph;
    private final Graph emissionGraph;
    private final Graph phasorGraph;
    private final Graph phasorZoomGraph;
    private final ListView assayList;
    private final ListView spectraList;
    private final SettingsPanel settings;
    private final JDialog view;

    /* Handlers */
    
    private final PhasorsGraphDelegateHandler fullPhasorsGraphHandler;
    private final PhasorsGraphDelegateHandler zoomedPhasorsGraphHandler;
    private final SpectraGraphDelegateHandler excitationGraphHandler;
    private final SpectraGraphDelegateHandler emissionGraphHandler;

    /* Dialogs */
    
    private LoaderDialog loader;

    /* Visual methods */
    
    private void layoutComponents() {
        JPanel leftPanel = new JPanel();
        assayList.setAlignmentX(Component.LEFT_ALIGNMENT);
        spectraList.setAlignmentX(Component.LEFT_ALIGNMENT);
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.PAGE_AXIS));
        leftPanel.add(assayList);
        leftPanel.add(spectraList);
        JLabel phasorLabel = new JLabel("Multispectral phasor");
        JLabel phasorZoomLabel = new JLabel("Multispectral - zoomed");
        JPanel fullPhasorPanel = new JPanel();
        fullPhasorPanel.setLayout(new BoxLayout(fullPhasorPanel, BoxLayout.PAGE_AXIS));
        JPanel zoomedPhasorPanel = new JPanel();
        zoomedPhasorPanel.setLayout(new BoxLayout(zoomedPhasorPanel, BoxLayout.PAGE_AXIS));
        phasorLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        phasorZoomLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        phasorGraph.setAlignmentX(Component.LEFT_ALIGNMENT);
        phasorZoomGraph.setAlignmentX(Component.LEFT_ALIGNMENT);
        fullPhasorPanel.add(phasorLabel);
        fullPhasorPanel.add(phasorGraph);
        zoomedPhasorPanel.add(phasorZoomLabel);
        zoomedPhasorPanel.add(phasorZoomGraph);
        JPanel allPhasors = new JPanel(new GridLayout(1, 2, 5, 5));
        allPhasors.add(fullPhasorPanel);
        allPhasors.add(zoomedPhasorPanel);
        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.LINE_AXIS));
        bottomPanel.add(allPhasors);
        bottomPanel.add(Box.createHorizontalStrut(5));
        bottomPanel.add(settings);
        JLabel excitationLabel = new JLabel("Excitation spectra");
        JLabel emissionLabel = new JLabel("Emission spectra");
        JPanel exSpPanel = new JPanel();
        exSpPanel.setLayout(new BoxLayout(exSpPanel, BoxLayout.PAGE_AXIS));
        excitationLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        excitationGraph.setAlignmentX(Component.LEFT_ALIGNMENT);
        exSpPanel.add(excitationLabel);
        exSpPanel.add(excitationGraph);
        JPanel emSpPanel = new JPanel();
        emSpPanel.setLayout(new BoxLayout(emSpPanel, BoxLayout.PAGE_AXIS));
        emissionLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        emissionGraph.setAlignmentX(Component.LEFT_ALIGNMENT);
        emSpPanel.add(emissionLabel);
        emSpPanel.add(emissionGraph);
        JPanel upperPanel = new JPanel(new GridLayout(1, 2, 5, 5));
        upperPanel.add(exSpPanel);
        upperPanel.add(emSpPanel);
        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new GridLayout(2, 1, 5, 5));
        rightPanel.add(upperPanel);
        rightPanel.add(bottomPanel);
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.LINE_AXIS));
        contentPanel.add(leftPanel);
        contentPanel.add(Box.createHorizontalStrut(5));
        contentPanel.add(rightPanel);
        view.getContentPane().setLayout(new BorderLayout(5, 5));
        view.getContentPane().add(Box.createVerticalStrut(5), BorderLayout.NORTH);
        view.getContentPane().add(Box.createHorizontalStrut(5), BorderLayout.WEST);
        view.getContentPane().add(Box.createVerticalStrut(5), BorderLayout.SOUTH);
        view.getContentPane().add(Box.createHorizontalStrut(5), BorderLayout.EAST);
        view.getContentPane().add(contentPanel, BorderLayout.CENTER);
    }

    private void initializeSettings() {
        settings.btnLoad.addActionListener((ActionEvent e) -> {
            loadAssay();
        });
        settings.btnSave.addActionListener((ActionEvent e) -> {
            saveAssay();
        });
        settings.btnRemove.addActionListener((ActionEvent e) -> {
            if (spectrumSelectedIndices != null) {
                for (int i = 0; i < spectrumSelectedIndices.length; i++) {
                    model.get(assaySelectedIndices[0]).spectra.remove(spectrumSelectedIndices[i]);
                }
                if (model.get(assaySelectedIndices[0]).spectra.isEmpty()) {
                    model.remove(assaySelectedIndices[0]);
                    assaySelectedIndices = (model.isEmpty()) ? null : new int[]{0};
                    assayList.updateList();
                } else {
                    spectrumSelectedIndices = (model.isEmpty()) ? null : new int[]{0};
                    spectraList.updateList();
                }
            }
        });
        settings.btnClear.addActionListener((ActionEvent e) -> {
            if (assaySelectedIndices != null) {
                if (assaySelectedIndices.length > 1) {
                    ArrayList<MultispectralPhasor> elementsToRemove = new ArrayList<>();
                    for (int i = 0; i < assaySelectedIndices.length; i++) {
                        elementsToRemove.add(model.get(assaySelectedIndices[i]));
                    }
                    model.removeAll(elementsToRemove);
                } else {
                    model.remove(assaySelectedIndices[0]);
                }
                assaySelectedIndices = (model.isEmpty()) ? null : new int[]{0};
                assayList.updateList();
            }
        });
        settings.btnRanges.addActionListener((ActionEvent e) -> {
            setRanges();
        });
        settings.btnZoom.addActionListener((ActionEvent e) -> {
            setZoom();
        });
        settings.cmbXUnits.addActionListener((ActionEvent e) -> {
            unitsChanged();
        });
        settings.cmbHarmonic.addActionListener((ActionEvent e) -> {
            int harmonicIndex = settings.cmbHarmonic.getSelectedIndex();
            int[] harmonic = Constants.harmonicOptions[harmonicIndex];
            model.get(assaySelectedIndices[0]).harmonic = new double[]{harmonic[0], harmonic[1]};
            phasorGraph.updateGraph(fullPhasorsGraphHandler);
            phasorZoomGraph.updateGraph(zoomedPhasorsGraphHandler);
            updateInfo();
        });
        settings.cmbShapes.addActionListener((ActionEvent e) -> {
            shapeChanged();
        });
        settings.cmbColors.addActionListener((ActionEvent e) -> {
            shapeChanged();
            emissionGraph.updateGraph(emissionGraphHandler);
            excitationGraph.updateGraph(excitationGraphHandler);
        });
    }

    private void updateSettings() {
        boolean validData = assaySelectedIndices != null && !model.isEmpty();
        settings.btnSave.setEnabled(validData);
        settings.btnRemove.setEnabled(validData && assaySelectedIndices.length == 1);
        settings.btnClear.setEnabled(validData);
        settings.btnRanges.setEnabled(validData);
        settings.btnZoom.setEnabled(validData);
        settings.cmbXUnits.setEnabled(validData);
        settings.cmbShapes.setEnabled(validData && assaySelectedIndices.length == 1);
        settings.cmbColors.setEnabled(validData && assaySelectedIndices.length == 1);
        settings.cmbHarmonic.setEnabled(validData && assaySelectedIndices.length == 1);
        if (validData && model.get(assaySelectedIndices[0]).isZoomed) {
            settings.btnZoom.setText("Reset zoom");
        } else {
            settings.btnZoom.setText("Set zoom");
        }
        if (validData) {
            freezeComboBox = true;
            settings.cmbXUnits.setSelectedIndex(model.get(assaySelectedIndices[0]).getXUnitsIndex());
            freezeComboBox = false;
        }
        if (validData && assaySelectedIndices.length == 1) {
            int listIndex = assaySelectedIndices[0];
            freezeComboBox = true;
            settings.cmbColors.setSelectedIndex(model.get(listIndex).colorIndex);
            settings.cmbShapes.setSelectedIndex(dotShapeIndex(model.get(listIndex).dotShape));
            settings.cmbHarmonic.setSelectedIndex(harmonicIndex(model.get(listIndex).harmonic));
            freezeComboBox = false;
        }
    }

    private void updateInfo() {
        boolean validData = assaySelectedIndices != null && !model.isEmpty();
        if (validData
                && assaySelectedIndices.length == 1
                && spectrumSelectedIndices.length == 1) {
            int listIndex = assaySelectedIndices[0];
            int spectrumIndex = spectrumSelectedIndices[0];
            double[] phasor = model.get(listIndex).getPhasor(spectrumIndex, model.get(listIndex).harmonic);
            double ro = Math.sqrt(phasor[0] * phasor[0] + phasor[1] * phasor[1]);
            double theta = getThetaValue(phasor[0], phasor[1]);
            String gValue = MathFeatures.getStringOfValueRoundedByError(false, phasor[0], 0.001);
            String sValue = MathFeatures.getStringOfValueRoundedByError(false, phasor[1], 0.001);
            String roValue = MathFeatures.getStringOfValueRoundedByError(false, ro, 0.001);
            String thetaValue = MathFeatures.getStringOfValueRoundedByError(false, theta, 0.001);
            settings.lblGValue.setText("G = " + gValue);
            settings.lblSValue.setText("S = " + sValue);
            settings.lblRoValue.setText("ρ = " + roValue);
            settings.lblThetaValue.setText("θ = " + thetaValue);
        } else {
            settings.lblGValue.setText("G = -----");
            settings.lblSValue.setText("S = -----");
            settings.lblThetaValue.setText("θ = -----");
            settings.lblRoValue.setText("ρ = -----");
        }
    }

    private void resetGraphs() {
        int selectedIndexForUnits = settings.cmbXUnits.getSelectedIndex();
        String xName = (selectedIndexForUnits == Spectrum.X_UNIT_WAVELENGTH_INDEX) ? "λ" : "v";
        String xUnit = (selectedIndexForUnits == Spectrum.X_UNIT_WAVELENGTH_INDEX) ? "nm"
                : (selectedIndexForUnits == Spectrum.X_UNIT_WAVENUMBER_CM_INDEX) ? "1/cm" : "1/um";
        Double xDigit = (selectedIndexForUnits == Spectrum.X_UNIT_WAVENUMBER_UM_INDEX) ? 0.01 : 1;
        excitationGraph.resetAxis(
                new String[]{xName, "F"},
                new String[]{xUnit, "AU"},
                new Color[]{Color.BLACK, Color.BLACK}
        );
        emissionGraph.resetAxis(
                new String[]{xName, "F"},
                new String[]{xUnit, "AU"},
                new Color[]{Color.BLACK, Color.BLACK}
        );
        phasorGraph.resetAxis(
                new String[]{"G", "S"},
                new String[]{null, null},
                new Color[]{Color.BLACK, Color.BLACK}
        );
        phasorZoomGraph.resetAxis(
                new String[]{"G", "S"},
                new String[]{null, null},
                new Color[]{Color.BLACK, Color.BLACK}
        );
        excitationGraph.axisNumberOfFigures = new double[]{xDigit, 0.01};
        emissionGraph.axisNumberOfFigures = new double[]{xDigit, 0.01};
        phasorGraph.axisNumberOfFigures = new double[]{0.01, 0.01};
        phasorZoomGraph.axisNumberOfFigures = new double[]{0.01, 0.01};
        excitationGraph.showXAxisValues = true;
        excitationGraph.showYAxisValues = true;
        emissionGraph.showXAxisValues = true;
        emissionGraph.showYAxisValues = true;
        phasorGraph.showXAxisValues = true;
        phasorGraph.showYAxisValues = true;
        phasorZoomGraph.showXAxisValues = true;
        phasorZoomGraph.showYAxisValues = true;
    }

    /* Settings actions methods */
    
    private void loadAssay() {
        try {
            FileFilter[] filters = new FileFilter[]{
                new FileNameExtensionFilter(FileType.csv.formatDescription(), FileType.csv.toString()),
                new FileNameExtensionFilter(FileType.spr.formatDescription(), FileType.spr.toString()),
                new FileNameExtensionFilter(FileType.sprl.formatDescription(), FileType.sprl.toString())
            };
            ArrayList<Object> loadedFiles = FilesIO.loadFiles(filters);
            switch (FilesIO.lastFormatSelected) {
                case spr:
                    for (Object file : loadedFiles) {
                        model.add((MultispectralPhasor) file);
                    }
                    assaySelectedIndices = new int[]{model.size() - 1};
                    spectrumSelectedIndices = new int[]{model.get(model.size() - 1).spectra.size() - 1};
                    assayList.updateList();
                    unitsChanged();
                    break;
                case sprl:
                    for (Object file : loadedFiles) {
                        model.addAll((ArrayList<MultispectralPhasor>) file);
                    }
                    assaySelectedIndices = new int[]{model.size() - 1};
                    spectrumSelectedIndices = new int[]{model.get(model.size() - 1).spectra.size() - 1};
                    assayList.updateList();
                    unitsChanged();
                    break;
                case csv:
                    int xUnits = askXUnits();
                    if (xUnits == -1) {
                        return;
                    }
                    ArrayList<Spectrum> loadedSpectra = new ArrayList<>();
                    for (Object file : loadedFiles) {
                        loadedSpectra.add(new Spectrum((RawData) file, xUnits));
                    }
                    loader = new LoaderDialog(model.size(), loadedSpectra, new LoaderDelegateHandler(), view);
                    loader.pack();
                    loader.setLocationRelativeTo(view);
                    loader.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                    loader.setResizable(false);
                    loader.setVisible(true);
                    break;
                default:
                    throw new Exception("Problem loading format");
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(
                    null,
                    ex.getMessage(),
                    "Something is wrong...",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void saveAssay() {
        JLabel mainMessage = new JLabel("What do you want to save?");
        mainMessage.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel nameMessage = new JLabel("Insert a name");
        JTextField name = new JTextField(10);
        name.setText(model.get(assaySelectedIndices[0]).getName());
        JPanel namePanel = new JPanel();
        namePanel.add(nameMessage);
        namePanel.add(name);
        namePanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        Object[] optionItems = {
            "Spactrasor file",
            "Excel file",
            "Images",
            "All at once"
        };
        JComboBox options = new JComboBox(optionItems);
        options.setAlignmentX(Component.CENTER_ALIGNMENT);
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
        panel.add(mainMessage);
        panel.add(Box.createVerticalStrut(5));
        panel.add(options);
        panel.add(Box.createVerticalStrut(5));
        panel.add(namePanel);
        int optionSelected = JOptionPane.showOptionDialog(
                null,
                panel,
                "Save",
                JOptionPane.YES_NO_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE,
                null,
                new String[]{"Save", "Cancel"},
                null
        );
        if (optionSelected == JOptionPane.YES_OPTION) {
            try {
                switch (options.getSelectedIndex()) {
                    case 0:
                        saveSpectrasorFile(name.getText());
                        break;
                    case 1:
                        saveExcelFile(name.getText());
                        break;
                    case 2:
                        saveImages(name.getText());
                        break;
                    default:
                        saveSpectrasorFile(name.getText());
                        saveExcelFile(name.getText());
                        saveImages(name.getText());
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(
                        null,
                        ex.getMessage(),
                        "Something is wrong...",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void setRanges() {
        double possibleMinExValue = -Double.MAX_VALUE;
        double possibleMaxExValue = Double.MAX_VALUE;
        double possibleMinEmValue = -Double.MAX_VALUE;
        double possibleMaxEmValue = Double.MAX_VALUE;
        double temp;
        for (int i = 0; i < assaySelectedIndices.length; i++) {
            temp = model.get(assaySelectedIndices[i]).minExX;
            if (temp > possibleMinExValue) {
                possibleMinExValue = temp;
            }
            temp = model.get(assaySelectedIndices[i]).minEmX;
            if (temp > possibleMinEmValue) {
                possibleMinEmValue = temp;
            }
            temp = model.get(assaySelectedIndices[i]).maxExX;
            if (temp < possibleMaxExValue) {
                possibleMaxExValue = temp;
            }
            temp = model.get(assaySelectedIndices[i]).maxEmX;
            if (temp < possibleMaxEmValue) {
                possibleMaxEmValue = temp;
            }
        }
        int selectedIndexForUnits = settings.cmbXUnits.getSelectedIndex();
        String xName = (selectedIndexForUnits == Spectrum.X_UNIT_WAVELENGTH_INDEX) ? "λ" : "v";
        String xUnit = (selectedIndexForUnits == Spectrum.X_UNIT_WAVELENGTH_INDEX) ? "nm"
                : (selectedIndexForUnits == Spectrum.X_UNIT_WAVENUMBER_CM_INDEX) ? "1/cm" : "1/um";
        Double xDigit = (selectedIndexForUnits == Spectrum.X_UNIT_WAVENUMBER_UM_INDEX) ? 0.01 : 1;
        String[] messages = new String[]{
            "Insert minimum excitation " + xName + " (" + xUnit + ")",
            "Insert maximum excitation " + xName + " (" + xUnit + ")",
            "Insert minimum emission " + xName + " (" + xUnit + ")",
            "Insert maximum emission " + xName + " (" + xUnit + ")"
        };
        double[] currentValues = new double[]{
            model.get(assaySelectedIndices[0]).exXValues[MultispectralPhasor.MIN_VALUE],
            model.get(assaySelectedIndices[0]).exXValues[MultispectralPhasor.MAX_VALUE],
            model.get(assaySelectedIndices[0]).emXValues[MultispectralPhasor.MIN_VALUE],
            model.get(assaySelectedIndices[0]).emXValues[MultispectralPhasor.MAX_VALUE]
        };
        double[] lowerBounds = new double[]{
            Math.min(possibleMinExValue, possibleMaxExValue),
            Math.min(possibleMinExValue, possibleMaxExValue),
            Math.min(possibleMinEmValue, possibleMaxEmValue),
            Math.min(possibleMinEmValue, possibleMaxEmValue)
        };
        double[] upperBounds = new double[]{
            Math.max(possibleMinExValue, possibleMaxExValue),
            Math.max(possibleMinExValue, possibleMaxExValue),
            Math.max(possibleMinEmValue, possibleMaxEmValue),
            Math.max(possibleMinEmValue, possibleMaxEmValue)
        };
        double[] insertedValues = new double[4];
        boolean cancel = false;
        for (int i = 0; i < messages.length; i++) {
            String textInserted = JOptionPane.showInputDialog(messages[i], currentValues[i]);
            if (textInserted == null) {
                cancel = true;
                break;
            }
            try {
                double value = Double.parseDouble(textInserted);
                if (value < lowerBounds[i] || value > upperBounds[i]) {
                    throw new Exception();
                }
                insertedValues[i] = value;
                if (i == 0 || i == 2) {
                    lowerBounds[i + 1] = value;
                }
            } catch (Exception e) {
                String lower = MathFeatures.getStringOfValueRoundedByError(false, lowerBounds[i], xDigit);
                String upper = MathFeatures.getStringOfValueRoundedByError(false, upperBounds[i], xDigit);
                JOptionPane.showMessageDialog(
                        null,
                        "Wrong value. Must be between " + lower + " and " + upper + " nm.",
                        "Something is wrong...",
                        JOptionPane.ERROR_MESSAGE
                );
                i--;
            }
        }
        if (!cancel) {
            for (int i = 0; i < assaySelectedIndices.length; i++) {
                double newSelectedExValue = (insertedValues[0] + insertedValues[1]) / 2;
                double newSelectedEmValue = (insertedValues[2] + insertedValues[3]) / 2;
                model.get(assaySelectedIndices[i]).exXValues[MultispectralPhasor.MIN_VALUE] = insertedValues[0];
                model.get(assaySelectedIndices[i]).exXValues[MultispectralPhasor.SELECTED_VALUE] = newSelectedExValue;
                model.get(assaySelectedIndices[i]).exXValues[MultispectralPhasor.MAX_VALUE] = insertedValues[1];
                model.get(assaySelectedIndices[i]).emXValues[MultispectralPhasor.MIN_VALUE] = insertedValues[2];
                model.get(assaySelectedIndices[i]).emXValues[MultispectralPhasor.SELECTED_VALUE] = newSelectedEmValue;
                model.get(assaySelectedIndices[i]).emXValues[MultispectralPhasor.MAX_VALUE] = insertedValues[3];
            }
            emissionGraph.updateGraph(emissionGraphHandler);
            excitationGraph.updateGraph(excitationGraphHandler);
            phasorGraph.updateGraph(fullPhasorsGraphHandler);
            phasorZoomGraph.updateGraph(zoomedPhasorsGraphHandler);
            updateInfo();
        }
    }

    private void setZoom() {
        double minX;
        double maxX;
        double minY;
        double maxY;
        boolean zoomed;
        if (model.get(assaySelectedIndices[0]).isZoomed) {
            minX = -1;
            maxX = 1;
            minY = -1;
            maxY = 1;
            zoomed = false;
        } else {
            double[] insertedValues = new double[4];
            String[] messages = new String[]{
                "Insert x minimum",
                "Insert x maximum",
                "Insert y minimum",
                "Insert y maximum"
            };
            double[] currentValues = new double[]{-1, 1, -1, 1};
            for (int i = 0; i < messages.length; i++) {
                String textInserted = JOptionPane.showInputDialog(messages[i], currentValues[i]);
                if (textInserted == null) {
                    return;
                }
                try {
                    double value = Double.parseDouble(textInserted);
                    if (value < -1 || value > 1) {
                        throw new Exception("");
                    }
                    if (i == 1 && value <= insertedValues[0]) {
                        throw new Exception("Wrong x maximum");
                    }
                    if (i == 3 && value <= insertedValues[2]) {
                        throw new Exception("Wrong y maximum");
                    }
                    insertedValues[i] = value;
                } catch (Exception e) {
                    String message;
                    switch (e.getMessage()) {
                        case "Wrong x maximum":
                            message = "Wrong maximum. Must be higher than " + insertedValues[0];
                            break;
                        case "Wrong y maximum":
                            message = "Wrong maximum. Must be higher than " + insertedValues[2];
                            break;
                        default:
                            message = "Wrong number. Must be between -1 and 1.";
                            break;
                    }
                    i--;
                    JOptionPane.showMessageDialog(null, message, "Something is wrong...", JOptionPane.ERROR_MESSAGE);
                }
            }
            minX = insertedValues[0];
            maxX = insertedValues[1];
            minY = insertedValues[2];
            maxY = insertedValues[3];
            zoomed = true;
        }
        zoom(zoomed, minX, maxX, maxY, minY);
    }

    private void shapeChanged() {
        if (assaySelectedIndices == null) {
            return;
        }
        if (!freezeComboBox) {
            for (int i = 0; i < assaySelectedIndices.length; i++) {
                model.get(assaySelectedIndices[i]).dotShape = settings.dotShapes[settings.cmbShapes.getSelectedIndex()];
                model.get(assaySelectedIndices[i]).colorIndex = settings.cmbColors.getSelectedIndex();
            }
            phasorGraph.updateGraph(fullPhasorsGraphHandler);
            phasorZoomGraph.updateGraph(zoomedPhasorsGraphHandler);
        }
    }

    private void unitsChanged() {
        if (assaySelectedIndices == null) {
            return;
        }
        if (!freezeComboBox) {
            for (int i = 0; i < assaySelectedIndices.length; i++) {
                model.get(assaySelectedIndices[i]).setXUnitsIndex(settings.cmbXUnits.getSelectedIndex());
                model.get(assaySelectedIndices[i]).resetRanges();
            }
            resetGraphs();
            emissionGraph.updateGraph(emissionGraphHandler);
            excitationGraph.updateGraph(excitationGraphHandler);
            phasorGraph.updateGraph(fullPhasorsGraphHandler);
            phasorZoomGraph.updateGraph(zoomedPhasorsGraphHandler);
            updateInfo();
        }
    }

    /* Helper methods */
    
    private int askXUnits() {
        Object[] xOptions = Spectrum.X_UNITS_OPTIONS;
        Object selectionText = JOptionPane.showInputDialog(
                null,
                "Select x units for the data",
                "Unit selection",
                JOptionPane.QUESTION_MESSAGE,
                null,
                xOptions,
                xOptions[0]
        );
        if (selectionText == null) {
            return -1;
        }
        int selectedIndex = 0;
        for (int i = 0; i < xOptions.length; i++) {
            if (xOptions[i] == selectionText) {
                selectedIndex = i;
                break;
            }
        }
        return selectedIndex;
    }

    private int dotShapeIndex(GraphDelegate.DotShape dotShape) {
        for (int i = 0; i < settings.dotShapes.length; i++) {
            if (settings.dotShapes[i] == dotShape) {
                return i;
            }
        }
        return 0;
    }

    private int harmonicIndex(double[] harmonic) {
        int[] harmonicInt = new int[]{(int) harmonic[0], (int) harmonic[1]};
        for (int i = 0; i < Constants.harmonicOptions.length; i++) {
            if (Constants.harmonicOptions[i][0] == harmonicInt[0]
                    && Constants.harmonicOptions[i][1] == harmonicInt[1]) {
                return i;
            }
        }
        return 0;
    }

    private ColorSource getColorSource() {
        Color[][] allColors = new Color[assaySelectedIndices.length][];
        int sum = 0;
        for (int i = 0; i < assaySelectedIndices.length; i++) {
            ColorSource tempColorSource = Constants.colorSources[model.get(assaySelectedIndices[i]).colorIndex];
            allColors[i] = tempColorSource.getColors(model.get(assaySelectedIndices[i]).spectra.size());
            sum += allColors[i].length;
        }
        Color[] joinedColors = new Color[sum];
        int index = 0;
        for (Color[] colors : allColors) {
            for (Color color : colors) {
                joinedColors[index] = color;
                index++;
            }
        }
        return ColorSource.colorCycle(joinedColors, "");
    }

    private void zoom(boolean zoomed, double minX, double maxX, double maxY, double minY) {
        for (int i = 0; i < assaySelectedIndices.length; i++) {
            model.get(assaySelectedIndices[i]).minGZoom = Math.max(minX, -1);
            model.get(assaySelectedIndices[i]).maxGZoom = Math.min(maxX, 1);
            model.get(assaySelectedIndices[i]).minSZoom = Math.max(minY, -1);
            model.get(assaySelectedIndices[i]).maxSZoom = Math.min(maxY, 1);
            model.get(assaySelectedIndices[i]).isZoomed = zoomed;
        }
        phasorGraph.updateGraph(fullPhasorsGraphHandler);
        phasorZoomGraph.updateGraph(zoomedPhasorsGraphHandler);
        updateSettings();
    }

    private void saveSpectrasorFile(String name) throws Exception {
        if (assaySelectedIndices.length == 1) {
            FilesIO.saveFile(
                    model.get(assaySelectedIndices[0]),
                    FileType.spr,
                    name
            );
        } else {
            ArrayList<MultispectralPhasor> listToSave = new ArrayList<>();
            for (int i = 0; i < assaySelectedIndices.length; i++) {
                listToSave.add(model.get(assaySelectedIndices[i]));
            }
            FilesIO.saveFile(
                    listToSave,
                    FileType.sprl,
                    name
            );
        }
    }

    private void saveExcelFile(String name) throws Exception {
        XSSFWorkbook workbook = new XSSFWorkbook();
        String[] harmonicStr = Constants.harmonicStr();
        int selectedIndexForUnits = settings.cmbXUnits.getSelectedIndex();
        String xName = (selectedIndexForUnits == Spectrum.X_UNIT_WAVELENGTH_INDEX) ? "λ" : "v";
        String xUnit = (selectedIndexForUnits == Spectrum.X_UNIT_WAVELENGTH_INDEX) ? "nm"
                : (selectedIndexForUnits == Spectrum.X_UNIT_WAVENUMBER_CM_INDEX) ? "1/cm" : "1/um";
        Double xDigit = (selectedIndexForUnits == Spectrum.X_UNIT_WAVENUMBER_UM_INDEX) ? 0.01 : 1;
        for (int i = 0; i < assaySelectedIndices.length; i++) {
            XSSFSheet sheet = workbook.createSheet(model.get(i).getName());
            String min = MathFeatures.getStringOfValueRoundedByError(false, model.get(i).exXValues[MultispectralPhasor.MIN_VALUE], xDigit);
            String max = MathFeatures.getStringOfValueRoundedByError(false, model.get(i).exXValues[MultispectralPhasor.MAX_VALUE], xDigit);
            String exRange = "Excitation range: " + xName + " = ";
            exRange += min + " " + xUnit + " - " + max + " " + xUnit;
            min = MathFeatures.getStringOfValueRoundedByError(false, model.get(i).emXValues[MultispectralPhasor.MIN_VALUE], xDigit);
            max = MathFeatures.getStringOfValueRoundedByError(false, model.get(i).emXValues[MultispectralPhasor.MAX_VALUE], xDigit);
            String emRange = "Emission range: " + xName + " = ";
            emRange += min + " " + xUnit + " - " + max + " " + xUnit;
            String temp = MathFeatures.getStringOfValueRoundedByError(false, model.get(i).exXValues[MultispectralPhasor.SELECTED_VALUE], xDigit);
            String exSelected = "Exitation selected: " + xName + " = ";
            exSelected += temp + " " + xUnit;
            temp = MathFeatures.getStringOfValueRoundedByError(false, model.get(i).emXValues[MultispectralPhasor.SELECTED_VALUE], xDigit);
            String emSelected = "Emission selected: " + xName + " = ";
            emSelected += temp + " " + xUnit;
            sheet.createRow(0).createCell(0).setCellValue(exRange);
            sheet.createRow(1).createCell(0).setCellValue(emRange);
            sheet.createRow(2).createCell(0).setCellValue(exSelected);
            sheet.createRow(3).createCell(0).setCellValue(emSelected);
            sheet.createRow(4);
            sheet.createRow(5);
            for (int j = 0; j < model.get(i).spectra.size(); j++) {
                sheet.createRow(6 + j).createCell(0).setCellValue(model.get(i).spectra.get(j).getName());
                for (int k = 0; k < Constants.harmonicOptions.length; k++) {
                    sheet.getRow(4).createCell(4 * k + 1).setCellValue(harmonicStr[k]);
                    sheet.getRow(5).createCell(4 * k + 1).setCellValue("G");
                    sheet.getRow(5).createCell(4 * k + 2).setCellValue("S");
                    sheet.getRow(5).createCell(4 * k + 3).setCellValue("ρ");
                    sheet.getRow(5).createCell(4 * k + 4).setCellValue("θ (rad)");
                    double[] harmonic = new double[]{Constants.harmonicOptions[k][0], Constants.harmonicOptions[k][1]};
                    double[] phasor = model.get(i).getPhasor(j, harmonic);
                    double ro = Math.sqrt(phasor[0] * phasor[0] + phasor[1] * phasor[1]);
                    double theta = getThetaValue(phasor[0], phasor[1]);
                    sheet.getRow(j + 6).createCell(4 * k + 1).setCellValue(phasor[0]);
                    sheet.getRow(j + 6).createCell(4 * k + 2).setCellValue(phasor[1]);
                    sheet.getRow(j + 6).createCell(4 * k + 3).setCellValue(ro);
                    sheet.getRow(j + 6).createCell(4 * k + 4).setCellValue(theta);
                }
            }
        }
        FilesIO.saveExcel(name, workbook);
    }

    private void saveImages(String name) throws Exception {
        savingImages = true;
        phasorGraph.updateGraph(fullPhasorsGraphHandler);
        FilesIO.saveImage(name + "_fullPhasor", phasorGraph, 10);
        if (model.get(assaySelectedIndices[0]).isZoomed) {
            phasorZoomGraph.updateGraph(zoomedPhasorsGraphHandler);
            FilesIO.saveImage(name + "_zoomedPhasor", phasorZoomGraph, 10);
        }
        savingImages = false;
        phasorGraph.updateGraph(fullPhasorsGraphHandler);
        phasorZoomGraph.updateGraph(zoomedPhasorsGraphHandler);
    }

    private double getThetaValue(double x, double y) {
        if (x > 0 && y >= 0) {
            return Math.atan(y / x);
        }
        if (x == 0 && y > 0) {
            return Math.PI / 2;
        }
        if (x < 0) {
            return Math.atan(y / x) + Math.PI;
        }
        if (x == 0 && y < 0) {
            return 3 * Math.PI / 2;
        }
        if (x > 0 && y < 0) {
            return Math.atan(y / x) + 2 * Math.PI;
        }
        return Double.NaN;
    }

    /* Spectra Graph Handler */
    
    private class SpectraGraphDelegateHandler extends GraphDelegate {

        private static final int EXCITATION = 0;
        private static final int EMISSION = 1;

        private final int type;

        private SpectraGraphDelegateHandler(int type) {
            this.type = type;
        }

        @Override
        public ColorSource curvesColorSource(int yAxisIndex) {
            return getColorSource();
        }

        @Override
        public void curveSelected(int yAxisIndex, int curveIndex) {
            if (!validData() || assaySelectedIndices.length != 1) {
                return;
            }
            spectrumSelectedIndices = new int[]{curveIndex};
            spectraList.updateList();
            phasorGraph.updateGraph(fullPhasorsGraphHandler);
            phasorZoomGraph.updateGraph(zoomedPhasorsGraphHandler);
            excitationGraph.updateGraph(excitationGraphHandler);
            emissionGraph.updateGraph(emissionGraphHandler);
        }

        @Override
        public double[][] curveValues(int yAxisIndex, int curveIndex) {
            double[] x;
            double[] intensities;
            int listIndex = assaySelectedIndices[0];
            if (assaySelectedIndices.length > 1) {
                int sum = 0;
                int previousSum = 0;
                for (int i = 0; i < assaySelectedIndices.length; i++) {
                    sum += model.get(assaySelectedIndices[i]).spectra.size();
                    if (curveIndex < sum) {
                        listIndex = assaySelectedIndices[i];
                        curveIndex -= previousSum;
                        break;
                    } else {
                        previousSum += model.get(assaySelectedIndices[i]).spectra.size();
                    }
                }
            }
            if (type == EXCITATION) {
                x = model.get(listIndex).spectra.get(curveIndex).getExcitationXs();
                intensities = model.get(listIndex).getExcitationIntensitiesFor(curveIndex);
            } else {
                x = model.get(listIndex).spectra.get(curveIndex).getEmissionXs();
                intensities = model.get(listIndex).getEmissionIntensitiesFor(curveIndex);
            }
            return new double[][]{x, intensities};
        }

        @Override
        public int numberOfCurves(int yAxisIndex) {
            if (!validData()) {
                return 0;
            }
            int totalSpectra = 0;
            if (assaySelectedIndices.length > 1) {
                for (int i = 0; i < assaySelectedIndices.length; i++) {
                    totalSpectra += model.get(i).spectra.size();
                }
            } else {
                totalSpectra += model.get(assaySelectedIndices[0]).spectra.size();
            }
            return totalSpectra;
        }

        @Override
        public int numberOfDots(int yAxisIndex) {
            return 0;
        }

        @Override
        public int numberOfVerticalLines() {
            return (validData()) ? 3 : 0;
        }

        @Override
        public int[] selectedCurves(int yAxisIndex) {
            if (!validData() || assaySelectedIndices.length != 1) {
                return new int[]{};
            }
            return spectrumSelectedIndices;
        }

        @Override
        public boolean userInteraction() {
            return true;
        }

        @Override
        public boolean validData() {
            return assaySelectedIndices != null
                    && spectrumSelectedIndices != null
                    && !model.isEmpty();
        }

        @Override
        public void verticalLineMoved(int index, double newValue) {
            if (type == EXCITATION) {
                for (int l = 0; l < assaySelectedIndices.length; l++) {
                    double[] temp = model.get(assaySelectedIndices[l]).exXValues;
                    temp[index] = newValue;
                    for (int i = 0; i < temp.length - 1; i++) {
                        for (int j = 0; j < temp.length - i - 1; j++) {
                            if (temp[j + 1] < temp[j]) {
                                double aux = temp[j + 1];
                                temp[j + 1] = temp[j];
                                temp[j] = aux;
                            }
                        }
                    }
                    model.get(assaySelectedIndices[l]).exXValues = temp;
                }
            } else {
                for (int l = 0; l < assaySelectedIndices.length; l++) {
                    double[] temp = model.get(assaySelectedIndices[l]).emXValues;
                    temp[index] = newValue;
                    for (int i = 0; i < temp.length - 1; i++) {
                        for (int j = 0; j < temp.length - i - 1; j++) {
                            if (temp[j + 1] < temp[j]) {
                                double aux = temp[j + 1];
                                temp[j + 1] = temp[j];
                                temp[j] = aux;
                            }
                        }
                    }
                    model.get(assaySelectedIndices[l]).emXValues = temp;
                }
            }
            phasorGraph.updateGraph(fullPhasorsGraphHandler);
            phasorZoomGraph.updateGraph(zoomedPhasorsGraphHandler);
            excitationGraph.updateGraph(excitationGraphHandler);
            emissionGraph.updateGraph(emissionGraphHandler);
            updateInfo();
        }

        @Override
        public double verticalLineValue(int index) {
            if (!validData()) {
                return 0;
            }
            if (type == EXCITATION) {
                return model.get(assaySelectedIndices[0]).exXValues[index];
            }
            return model.get(assaySelectedIndices[0]).emXValues[index];
        }

    }

    /* Phasor Graph Handler */
    
    private class PhasorsGraphDelegateHandler extends GraphDelegate {

        private static final int NORMAL = 0;
        private static final int ZOOMED = 1;

        private final int type;

        private PhasorsGraphDelegateHandler(int type) {
            this.type = type;
        }

        @Override
        public boolean canZoom() {
            return (type == NORMAL);
        }

        @Override
        public double[][] curveValues(int yAxisIndex, int curveIndex) {
            double[] x = new double[]{
                model.get(assaySelectedIndices[0]).minGZoom,
                model.get(assaySelectedIndices[0]).maxGZoom,
                model.get(assaySelectedIndices[0]).maxGZoom,
                model.get(assaySelectedIndices[0]).minGZoom,
                model.get(assaySelectedIndices[0]).minGZoom
            };
            double[] y = new double[]{
                model.get(assaySelectedIndices[0]).minSZoom,
                model.get(assaySelectedIndices[0]).minSZoom,
                model.get(assaySelectedIndices[0]).maxSZoom,
                model.get(assaySelectedIndices[0]).maxSZoom,
                model.get(assaySelectedIndices[0]).minSZoom
            };
            return new double[][]{x, y};
        }

        @Override
        public ColorSource dotsColorSource(int yAxisIndex) {
            return getColorSource();
        }

        @Override
        public ColorSource dotsFillColorSource(int yAxisIndex) {
            return getColorSource();
        }

        @Override
        public DotShape dotShape(int yAxisIndex, int dotIndex) {
            if (!validData()) {
                return super.dotShape(yAxisIndex, dotIndex);
            }
            int listIndex = assaySelectedIndices[0];
            if (assaySelectedIndices.length > 1) {
                int sum = 0;
                for (int i = 0; i < assaySelectedIndices.length; i++) {
                    sum += model.get(assaySelectedIndices[i]).spectra.size();
                    if (dotIndex < sum) {
                        listIndex = assaySelectedIndices[i];
                        break;
                    }
                }
            }
            return model.get(listIndex).dotShape;
        }

        @Override
        public double[] dotValue(int yAxisIndex, int dotIndex) {
            int listIndex = assaySelectedIndices[0];
            if (assaySelectedIndices.length > 1) {
                int sum = 0;
                int previousSum = 0;
                for (int i = 0; i < assaySelectedIndices.length; i++) {
                    sum += model.get(assaySelectedIndices[i]).spectra.size();
                    if (dotIndex < sum) {
                        listIndex = assaySelectedIndices[i];
                        dotIndex -= previousSum;
                        break;
                    } else {
                        previousSum += model.get(assaySelectedIndices[i]).spectra.size();
                    }
                }
            }
            return model.get(listIndex).getPhasor(dotIndex, model.get(listIndex).harmonic);
        }

        @Override
        public double[] fixedXExtremes() {
            if (type == ZOOMED && validData()) {
                return new double[]{
                    model.get(assaySelectedIndices[0]).minGZoom,
                    model.get(assaySelectedIndices[0]).maxGZoom
                };
            }
            return new double[]{-1, 1};
        }

        @Override
        public double[] fixedYExtremes(int index) {
            if (type == ZOOMED && validData()) {
                return new double[]{
                    model.get(assaySelectedIndices[0]).minSZoom,
                    model.get(assaySelectedIndices[0]).maxSZoom
                };
            }
            return new double[]{-1, 1};
        }

        @Override
        public boolean isPolarGraph() {
            return true;
        }

        @Override
        public int numberOfCurves(int yAxisIndex) {
            if (!validData()) {
                return 0;
            }
            boolean showSquare = type == NORMAL && model.get(assaySelectedIndices[0]).isZoomed;
            return (showSquare) ? 1 : 0;
        }

        @Override
        public int numberOfDots(int yAxisIndex) {
            if (!validData()) {
                return 0;
            }
            int totalSpectra = 0;
            if (assaySelectedIndices.length > 1) {
                for (int i = 0; i < assaySelectedIndices.length; i++) {
                    totalSpectra += model.get(i).spectra.size();
                }
            } else {
                totalSpectra += model.get(assaySelectedIndices[0]).spectra.size();
            }
            return totalSpectra;
        }

        @Override
        public int numberOfVerticalLines() {
            return 0;
        }

        @Override
        public int[] selectedDots(int yAxisIndex) {
            if (savingImages) {
                int[] allSelected = new int[numberOfDots(yAxisIndex)];
                for (int i = 0; i < allSelected.length; i++) {
                    allSelected[i] = i;
                }
                return allSelected;
            }
            if (!validData() || assaySelectedIndices.length != 1) {
                return new int[]{};
            }
            return spectrumSelectedIndices;
        }

        @Override
        public boolean userInteraction() {
            return true;
        }

        @Override
        public boolean validData() {
            return assaySelectedIndices != null
                    && spectrumSelectedIndices != null
                    && !model.isEmpty();
        }

        @Override
        public void zoomed(double minX, double maxX, double maxY, double minY) {
            zoom(true, minX, maxX, maxY, minY);
        }

    }

    /* List Handler */
    
    private class ListDelegateHandler implements ListViewDelegate {

        private static final int ASSAYS = 0;
        private static final int SPECTRA = 1;

        private final int type;

        private ListDelegateHandler(int type) {
            this.type = type;
        }

        @Override
        public boolean canSelectMultipleRows() {
            return true;
        }

        @Override
        public ArrayList<Listeable> getData() {
            if (model.isEmpty()) {
                return null;
            }
            if (type == ASSAYS) {
                ArrayList<Listeable> list = new ArrayList<>();
                for (MultispectralPhasor phasor : model) {
                    list.add(phasor);
                }
                return list;
            }
            if (assaySelectedIndices == null || assaySelectedIndices.length != 1) {
                return null;
            }
            ArrayList<Listeable> list = new ArrayList<>();
            for (int i = 0; i < model.get(assaySelectedIndices[0]).spectra.size(); i++) {
                list.add(model.get(assaySelectedIndices[0]).spectra.get(i));
            }
            return list;
        }

        @Override
        public int[] getSelectedIndices() {
            return (type == SPECTRA) ? spectrumSelectedIndices : assaySelectedIndices;
        }

        @Override
        public void selectionChanged(int[] newIndices) {
            if (newIndices == null || newIndices.length == 0) {
                return;
            }
            if (type == SPECTRA) {
                spectrumSelectedIndices = newIndices;
                phasorGraph.updateGraph(fullPhasorsGraphHandler);
                phasorZoomGraph.updateGraph(zoomedPhasorsGraphHandler);
                excitationGraph.updateGraph(excitationGraphHandler);
                emissionGraph.updateGraph(emissionGraphHandler);
            } else {
                assaySelectedIndices = newIndices;
                spectrumSelectedIndices = new int[]{0};
                spectraList.updateList();
                updateSettings();
            }
            updateInfo();
        }
    }

    /* Loader Handler */
    
    private class LoaderDelegateHandler implements LoaderDialog.LoaderDelegate {

        @Override
        public void loadSpectra(String name, ArrayList<Spectrum> spectra) {
            if (spectra.isEmpty()) {
                return;
            }
            MultispectralPhasor newList = new MultispectralPhasor();
            newList.setName(name);
            newList.spectra = spectra;
            newList.resetRanges();
            model.add(newList);
            assaySelectedIndices = new int[]{model.size() - 1};
            spectrumSelectedIndices = new int[]{model.get(model.size() - 1).spectra.size() - 1};
            assayList.updateList();
            resetGraphs();
        }

    }

}
