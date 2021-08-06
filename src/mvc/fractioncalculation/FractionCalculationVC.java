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

import auxiliary.FileType;
import auxiliary.FilesIO;
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
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import model.Constants;
import model.FractionCalculationModel;
import model.FractionEquation;
import model.MultispectralPhasor;
import model.ReferencePoint;
import model.Spectrum;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import views.common.ColorSource;
import views.graph.Graph;
import views.graph.GraphDelegate;
import views.list.ListView;
import views.list.ListViewDelegate;
import views.list.Listeable;

/**
 * Fraction calculation View Controller.
 *
 * @author L.B.P.Socas
 */
public final class FractionCalculationVC implements EquationsSettedDelegate {

    // ######### Public API ##########
    
    /**
     * Creates a new Fraction calculation View Controller.
     *
     * @param owner the frame that owns this dialog.
     */
    public FractionCalculationVC(JFrame owner) {
        view = new JDialog(owner, "Fraction calculation", Dialog.ModalityType.DOCUMENT_MODAL);
        spectraList = new ListView(
                12,
                "Spectra",
                new ListDelegateHandler(ListDelegateHandler.SPECTRA_LIST)
        );
        assayList = new ListView(
                12,
                "Assays",
                new ListDelegateHandler(ListDelegateHandler.ASSAY_LIST)
        );
        referencePointsList = new ListView(
                12,
                "Reference points",
                new ListDelegateHandler(ListDelegateHandler.REFERENCE_LIST)
        );
        phasorsGraphHandler = new PhasorsGraphDelegateHandler();
        phasorGraph = new Graph(
                1.0 / 1.0,
                phasorsGraphHandler
        );
        phasorGraph.resetAxis(
                new String[]{"G", "S"},
                new String[]{null, null},
                new Color[]{Color.BLACK, Color.BLACK}
        );
        phasorGraph.axisNumberOfFigures = new double[]{0.01, 0.01};
        phasorGraph.showXAxisValues = true;
        phasorGraph.showYAxisValues = true;
        layoutComponents();
        initializeComponents();
        updatePhasorPanel();
        updateButtons();
        updateLabel();
        showFrame();
    }

    @Override
    public void equationsSetted() {
        equationsSetted = true;
        model.recalculateFractions();
        updateLabel();
        updateButtons();
    }

    // ######### Private implementation ##########

    /* Attributes */
    
    private FractionCalculationModel model = new FractionCalculationModel();
    private boolean savingImage = false;
    private boolean freezeComboBox = false;
    private boolean equationsSetted = false;
    private int assaySelectedIndex = -1;
    private int spectraSelectedIndex = -1;
    private int firstReferenceSelectedIndex = -1;
    private int[] selectedReferenceIndices = null;

    /* Visual components */
    
    private final ListView spectraList;
    private final ListView assayList;
    private final ListView referencePointsList;
    private final Graph phasorGraph;
    private final JComboBox cmbHarmonic = new JComboBox(Constants.harmonicStr());
    private final JLabel fractionLabel = new JLabel("fraction value for selected spectrum");
    private final JButton btnLoadMSPList = new JButton("Load data");
    private final JButton btnSetEquations = new JButton("Set equation");
    private final JButton btnAddFromPoint = new JButton("Add selected");
    private final JButton btnAddManual = new JButton("Add manual");
    private final JButton btnLoadPoints = new JButton("Load points");
    private final JButton btnRemove = new JButton("Remove");
    private final JButton btnMoveUp = new JButton("Move up");
    private final JButton btnMoveDown = new JButton("Move down");
    private final JButton btnSave = new JButton("Save");
    private final JButton btnExport = new JButton("Export");
    private final JButton btnSetZoom = new JButton("Set zoom");
    private final JDialog view;
    private EquationsDialog equationsDialog;

    /* Handlers */
    
    private final PhasorsGraphDelegateHandler phasorsGraphHandler;

    /* Visual methods */
    
    private void layoutComponents() {
        JPanel phasorPanel = new JPanel();
        phasorPanel.setLayout(new BoxLayout(phasorPanel, BoxLayout.PAGE_AXIS));
        phasorPanel.add(new JLabel("Phasors"));
        phasorPanel.add(phasorGraph);

        JPanel topPanel = new JPanel(new GridLayout(2, 2, 5, 5));
        topPanel.add(assayList);
        topPanel.add(spectraList);
        topPanel.add(referencePointsList);
        topPanel.add(phasorPanel);

        JPanel shortButtonsPanel0 = new JPanel(new GridLayout(1, 2, 5, 5));
        shortButtonsPanel0.add(btnAddFromPoint);
        shortButtonsPanel0.add(btnAddManual);

        JPanel shortButtonsPanel1 = new JPanel(new GridLayout(1, 2, 5, 5));
        shortButtonsPanel1.add(btnLoadPoints);
        shortButtonsPanel1.add(btnRemove);

        JPanel shortButtonsPanel2 = new JPanel(new GridLayout(1, 2, 5, 5));
        shortButtonsPanel2.add(btnMoveUp);
        shortButtonsPanel2.add(btnMoveDown);

        JPanel shortButtonsPanel3 = new JPanel(new GridLayout(1, 2, 5, 5));
        shortButtonsPanel3.add(btnLoadMSPList);
        shortButtonsPanel3.add(btnSetEquations);

        JPanel shortButtonsPanel4 = new JPanel(new GridLayout(1, 2, 5, 5));
        shortButtonsPanel4.add(cmbHarmonic);
        shortButtonsPanel4.add(btnSetZoom);

        JPanel shortButtonsPanel5 = new JPanel(new GridLayout(1, 2, 5, 5));
        shortButtonsPanel5.add(btnSave);
        shortButtonsPanel5.add(btnExport);

        JPanel leftButtonsPanel = new JPanel(new GridLayout(3, 1, 5, 5));
        leftButtonsPanel.add(shortButtonsPanel0);
        leftButtonsPanel.add(shortButtonsPanel1);
        leftButtonsPanel.add(shortButtonsPanel2);

        JPanel rightButtonsPanel = new JPanel(new GridLayout(3, 1, 5, 5));
        rightButtonsPanel.add(shortButtonsPanel3);
        rightButtonsPanel.add(shortButtonsPanel4);
        rightButtonsPanel.add(shortButtonsPanel5);

        JPanel bottomPanel = new JPanel(new GridLayout(1, 2, 5, 5));
        bottomPanel.add(leftButtonsPanel);
        bottomPanel.add(rightButtonsPanel);

        fractionLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.PAGE_AXIS));
        contentPanel.add(topPanel);
        contentPanel.add(Box.createVerticalStrut(5));
        contentPanel.add(bottomPanel);
        contentPanel.add(Box.createVerticalStrut(5));
        contentPanel.add(fractionLabel);

        view.getContentPane().setLayout(new BorderLayout(5, 5));
        view.getContentPane().add(Box.createVerticalStrut(5), BorderLayout.NORTH);
        view.getContentPane().add(Box.createHorizontalStrut(5), BorderLayout.WEST);
        view.getContentPane().add(Box.createVerticalStrut(5), BorderLayout.SOUTH);
        view.getContentPane().add(Box.createHorizontalStrut(5), BorderLayout.EAST);
        view.getContentPane().add(contentPanel, BorderLayout.CENTER);
    }

    private void initializeComponents() {
        btnAddFromPoint.addActionListener((ActionEvent e) -> {
            String pointName = JOptionPane.showInputDialog("Reference point name", "ref" + (model.getPointsAt(selectedHarmonicIndex()).size() + 1));
            if (pointName == null) {
                return;
            }
            for (int i = 0; i < Constants.harmonicOptions.length; i++) {
                int[] harmonicOption = Constants.harmonicOptions[i];
                double[] harmonic = new double[]{harmonicOption[0], harmonicOption[1]};
                double[] coordinates = model.assays.get(assaySelectedIndex).getPhasor(spectraSelectedIndex, harmonic);
                addReferencePoint(pointName, coordinates, i);
            }
        });
        btnAddManual.addActionListener((ActionEvent e) -> {
            String pointName = JOptionPane.showInputDialog("Reference point name", "ref" + (model.getPointsAt(selectedHarmonicIndex()).size() + 1));
            if (pointName == null) {
                return;
            }
            addReferencePoint(pointName, null, -1);
        });
        btnLoadPoints.addActionListener((ActionEvent e) -> {
            loadReferencePoints();
        });
        btnLoadMSPList.addActionListener((ActionEvent e) -> {
            loadMSP();
        });
        btnSetEquations.addActionListener((ActionEvent e) -> {
            setEquations();
        });
        btnRemove.addActionListener((ActionEvent e) -> {
            ArrayList<ReferencePoint> pointsToRemove = new ArrayList<>();
            for (int i = 0; i < selectedReferenceIndices.length; i++) {
                pointsToRemove.add(model.getPointsAt(selectedHarmonicIndex()).get(selectedReferenceIndices[i]));
            }
            model.getPointsAt(selectedHarmonicIndex()).removeAll(pointsToRemove);
            if (model.getPointsAt(selectedHarmonicIndex()).size() > 0) {
                selectedReferenceIndices = new int[]{0};
                firstReferenceSelectedIndex = 0;
            } else {
                selectedReferenceIndices = null;
                firstReferenceSelectedIndex = -1;
            }
            referencePointsList.updateList();
            phasorGraph.updateGraph(phasorsGraphHandler);
            updateButtons();
        });
        btnMoveUp.addActionListener((ActionEvent e) -> {
            int index = selectedReferenceIndices[0];
            int harmonicIndex = selectedHarmonicIndex();
            ReferencePoint temp = model.getPointsAt(harmonicIndex).remove(selectedReferenceIndices[0]);
            model.getPointsAt(harmonicIndex).add(index - 1, temp);
            selectedReferenceIndices[0]--;
            firstReferenceSelectedIndex = selectedReferenceIndices[0];
            referencePointsList.updateList();
            phasorGraph.updateGraph(phasorsGraphHandler);
            updateButtons();
        });
        btnMoveDown.addActionListener((ActionEvent e) -> {
            int index = selectedReferenceIndices[0];
            int harmonicIndex = selectedHarmonicIndex();
            ReferencePoint temp = model.getPointsAt(harmonicIndex).remove(selectedReferenceIndices[0]);
            model.getPointsAt(harmonicIndex).add(index + 1, temp);
            selectedReferenceIndices[0]++;
            firstReferenceSelectedIndex = selectedReferenceIndices[0];
            referencePointsList.updateList();
            phasorGraph.updateGraph(phasorsGraphHandler);
            updateButtons();
        });
        btnSave.addActionListener((ActionEvent e) -> {
            save(false);
        });
        btnExport.addActionListener((ActionEvent e) -> {
            save(true);
        });
        btnSetZoom.addActionListener((ActionEvent e) -> {
            setZoom();
        });
        cmbHarmonic.addActionListener((ActionEvent e) -> {
            if (!freezeComboBox) {
                int harmonicIndex = cmbHarmonic.getSelectedIndex();
                int[] harmonic = Constants.harmonicOptions[harmonicIndex];
                for (MultispectralPhasor assay : model.assays) {
                    assay.harmonic = new double[]{harmonic[0], harmonic[1]};
                }
                if (!model.getPointsAt(harmonicIndex).isEmpty()) {
                    selectedReferenceIndices = new int[]{0};
                } else {
                    selectedReferenceIndices = null;
                }
                referencePointsList.updateList();
                phasorGraph.updateGraph(phasorsGraphHandler);
                updateButtons();
            }
        });
    }

    private void updatePhasorPanel() {
        btnSetZoom.setEnabled(!model.assays.isEmpty());
        cmbHarmonic.setEnabled(!model.assays.isEmpty());
        if (model.assays.isEmpty()) {
            btnSetZoom.setText("Set zoom");
        } else {
            if (model.assays.get(assaySelectedIndex).isZoomed) {
                btnSetZoom.setText("Reset");
            } else {
                btnSetZoom.setText("Set zoom");
            }
            freezeComboBox = true;
            cmbHarmonic.setSelectedIndex(selectedHarmonicIndex());
            freezeComboBox = false;
        }
    }

    private void updateButtons() {
        btnAddManual.setEnabled(!model.assays.isEmpty());
        btnLoadPoints.setEnabled(!model.assays.isEmpty());
        btnSave.setEnabled(!model.assays.isEmpty());
        btnSetEquations.setEnabled(!model.assays.isEmpty());
        btnExport.setEnabled(equationsSetted && firstReferenceSelectedIndex >= 0);
        if (model.assays.isEmpty()) {
            btnAddFromPoint.setEnabled(false);
            btnRemove.setEnabled(false);
            btnMoveUp.setEnabled(false);
            btnMoveDown.setEnabled(false);
            btnSetEquations.setEnabled(false);
        } else {
            int harmonicIndex = selectedHarmonicIndex();
            btnAddFromPoint.setEnabled(spectraSelectedIndex < model.assays.get(assaySelectedIndex).spectra.size());
            btnRemove.setEnabled(selectedReferenceIndices != null && !model.getPointsAt(selectedHarmonicIndex()).isEmpty());
            if (selectedReferenceIndices != null
                    && selectedReferenceIndices.length == 1
                    && !model.getPointsAt(selectedHarmonicIndex()).isEmpty()) {
                int index = selectedReferenceIndices[0];
                btnMoveUp.setEnabled(index != 0);
                btnMoveDown.setEnabled(index != model.getPointsAt(harmonicIndex).size() - 1);
            } else {
                btnMoveUp.setEnabled(false);
                btnMoveDown.setEnabled(false);
            }
        }
    }

    private void showFrame() {
        view.pack();
        view.setLocationRelativeTo(null);
        view.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        view.setVisible(true);
        view.setResizable(false);
    }

    private void updateLabel() {
        ArrayList<ReferencePoint> points = model.getPointsAt(cmbHarmonic.getSelectedIndex());
        if (model.calculator.systemIsCorrect() && !points.isEmpty()) {
            double fraction = model.getFractionFor(assaySelectedIndex, spectraSelectedIndex, firstReferenceSelectedIndex);
            String refName = points.get(firstReferenceSelectedIndex).getName();
            String fractionStr = MathFeatures.getStringOfValueRoundedByError(false, fraction, 0.001);
            String text = "reference: " + refName + "; fraction value: " + fractionStr;
            fractionLabel.setText(text);
        } else {
            fractionLabel.setText(" ");
        }
    }

    /* Helper methods */
    
    private int selectedHarmonicIndex() {
        if (assaySelectedIndex < 0) {
            return 0;
        }
        double[] harmonic = model.assays.get(assaySelectedIndex).harmonic;
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
        ColorSource dotsColorSource = Constants.colorSources[model.assays.get(assaySelectedIndex).colorIndex];
        Color[] dotColors = dotsColorSource.getColors(model.assays.get(assaySelectedIndex).spectra.size());
        Color[] referencesColor = new Color[0];
        if (selectedReferenceIndices != null) {
            referencesColor = new Color[selectedReferenceIndices.length];
            for (int i = 0; i < referencesColor.length; i++) {
                referencesColor[i] = Color.black;
                if (selectedReferenceIndices[i] == firstReferenceSelectedIndex) {
                    referencesColor[i] = Color.red;
                }
            }
        }
        Color[] joinedColors = new Color[dotColors.length + referencesColor.length];
        System.arraycopy(dotColors, 0, joinedColors, 0, dotColors.length);
        System.arraycopy(referencesColor, 0, joinedColors, dotColors.length, referencesColor.length);
        return ColorSource.colorCycle(joinedColors, "");
    }

    private void zoom(boolean zoomed, double minX, double maxX, double maxY, double minY) {
        model.assays.get(assaySelectedIndex).minGZoom = Math.max(minX, -1);
        model.assays.get(assaySelectedIndex).maxGZoom = Math.min(maxX, 1);
        model.assays.get(assaySelectedIndex).minSZoom = Math.max(minY, -1);
        model.assays.get(assaySelectedIndex).maxSZoom = Math.min(maxY, 1);
        model.assays.get(assaySelectedIndex).isZoomed = zoomed;
        phasorGraph.updateGraph(phasorsGraphHandler);
        updatePhasorPanel();
    }

    /* Actions methods */
    
    private void addReferencePoint(String pointName, double[] coordinates, int harmonicIndex) {
        ReferencePoint newPoint;
        double gValue;
        double sValue;
        if (coordinates == null) {
            try {
                String g = JOptionPane.showInputDialog("G-coordinate", 0.0);
                if (g == null) {
                    return;
                }
                String s = JOptionPane.showInputDialog("S-coordinate", 0.0);
                if (s == null) {
                    return;
                }
                gValue = Double.parseDouble(g);
                sValue = Double.parseDouble(s);
                if (gValue > 1 || gValue < -1 || sValue > 1 || sValue < -1) {
                    throw new Exception();
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(
                        null,
                        "Wrong number!",
                        "Something is wrong...",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
        } else {
            gValue = coordinates[0];
            sValue = coordinates[1];
        }
        newPoint = new ReferencePoint(harmonicIndex, pointName, gValue, sValue);
        if (model.getPointsAt(harmonicIndex).contains(newPoint)) {
            JOptionPane.showMessageDialog(
                    null,
                    "That point exists already!",
                    "Something is wrong...",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }
        model.getPointsAt(harmonicIndex).add(newPoint);
        selectedReferenceIndices = new int[]{0};
        firstReferenceSelectedIndex = 0;
        referencePointsList.updateList();
        phasorGraph.updateGraph(phasorsGraphHandler);
        updateButtons();
    }

    private void loadMSP() {
        try {
            FileFilter[] filters = new FileFilter[]{
                new FileNameExtensionFilter(FileType.spr.formatDescription(), FileType.spr.toString()),
                new FileNameExtensionFilter(FileType.sprl.formatDescription(), FileType.sprl.toString()),
                new FileNameExtensionFilter(FileType.sprf.formatDescription(), FileType.sprf.toString())
            };
            ArrayList<Object> loadedFiles = FilesIO.loadFiles(filters);
            model.assays.clear();
            switch (FilesIO.lastFormatSelected) {
                case spr:
                    for (Object file : loadedFiles) {
                        model.assays.add((MultispectralPhasor) file);
                    }
                    break;
                case sprl:
                    for (Object file : loadedFiles) {
                        model.assays.addAll((ArrayList<MultispectralPhasor>) file);
                    }
                    break;
                case sprf:
                    model = (FractionCalculationModel) loadedFiles.get(0);
                    break;
                default:
                    throw new Exception("Problem loading format");
            }
            assaySelectedIndex = model.assays.size() - 1;
            spectraSelectedIndex = model.assays.get(assaySelectedIndex).spectra.size() - 1;
            selectedReferenceIndices = (model.getPointsAt(selectedHarmonicIndex()).isEmpty()) ? null : new int[]{0};
            assayList.updateList();
            spectraList.updateList();
            referencePointsList.updateList();
            updateButtons();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(
                    null,
                    ex.getMessage(),
                    "Something is wrong...",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadReferencePoints() {
        try {
            FileFilter[] filters = new FileFilter[]{
                new FileNameExtensionFilter(FileType.rfp.formatDescription(), FileType.rfp.toString())
            };
            ArrayList<Object> loadedFiles = FilesIO.loadFiles(filters);
            for (Object obj : loadedFiles) {
                ArrayList<ReferencePoint> points = (ArrayList<ReferencePoint>) obj;
                for (ReferencePoint refPoint : points) {
                    boolean newOne = true;
                    int harmonicIndex = refPoint.getHarmonicIndex();
                    for (ReferencePoint point : model.getPointsAt(harmonicIndex)) {
                        if (refPoint.equals(point)) {
                            newOne = false;
                            break;
                        }
                    }
                    if (newOne) {
                        model.getPointsAt(harmonicIndex).add(refPoint);
                    }
                }
            }
            selectedReferenceIndices = new int[]{0};
            firstReferenceSelectedIndex = 0;
            referencePointsList.updateList();
            updateButtons();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(
                    null,
                    ex.getMessage(),
                    "Something is wrong...",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void save(boolean export) {
        int harmonicIndex = selectedHarmonicIndex();
        boolean hasPoints = !model.getPointsAt(harmonicIndex).isEmpty();
        try {
            if (export) {
                XSSFWorkbook workbook = new XSSFWorkbook();
                for (MultispectralPhasor temp : model.assays) {
                    XSSFSheet sheet = workbook.createSheet(temp.getName());
                    String harmonicStr = Constants.harmonicStr()[harmonicIndex];
                    sheet.createRow(0).createCell(0).setCellValue(harmonicStr);
                    sheet.getRow(0).createCell(1).setCellValue("G");
                    sheet.getRow(0).createCell(2).setCellValue("S");
                    sheet.getRow(0).createCell(3).setCellValue("");
                    sheet.getRow(0).createCell(4).setCellValue("Equation system used");
                    sheet.getRow(0).createCell(5).setCellValue("");
                    for (int i = 0; i < selectedReferenceIndices.length; i++) {
                        int index = selectedReferenceIndices[i];
                        String header = "f(" + model.getPointsAt(harmonicIndex).get(index).getName() + ")";
                        sheet.getRow(0).createCell(i + 6).setCellValue(header);
                    }
                    for (int i = 0; i < temp.spectra.size(); i++) {
                        double[] fractions = model.calculator.getFractions(temp, i);
                        double[] harmonic = temp.harmonic;
                        double[] phasor = temp.getPhasor(i, harmonic);
                        sheet.createRow(i + 1).createCell(0).setCellValue(temp.spectra.get(i).getName());
                        sheet.getRow(i + 1).createCell(1).setCellValue(phasor[0]);
                        sheet.getRow(i + 1).createCell(2).setCellValue(phasor[1]);
                        for (int j = 0; j < selectedReferenceIndices.length; j++) {
                            int index = selectedReferenceIndices[j];
                            sheet.getRow(i + 1).createCell(j + 6).setCellValue(fractions[index]);
                        }
                    }
                    for (int i = 0; i < model.calculator.system.size(); i++) {
                        FractionEquation equation = model.calculator.system.get(i);
                        if (i >= temp.spectra.size()) {
                            sheet.createRow(i + 1).createCell(3).setCellValue("");
                        } else {
                            sheet.getRow(i + 1).createCell(3).setCellValue("");
                        }
                        sheet.getRow(i + 1).createCell(4).setCellValue(equation.getName());
                        sheet.getRow(i + 1).createCell(5).setCellValue("");
                    }
                }
                FilesIO.saveExcel("Fraction calculation", workbook);
                return;
            }
            String msgText;
            if (hasPoints) {
                msgText = "Save image?\nSelect NO for other options.";
            } else {
                msgText = "Save image?\nSelect NO for saving all data.";
            }
            int optionSelected = JOptionPane.showConfirmDialog(
                    null,
                    msgText,
                    "Saving data",
                    JOptionPane.YES_NO_OPTION
            );
            if (optionSelected == JOptionPane.CANCEL_OPTION) {
                return;
            }
            if (optionSelected == JOptionPane.YES_OPTION) {
                savingImage = true;
                phasorGraph.updateGraph(phasorsGraphHandler);
                FilesIO.saveImage("FractionAnalysis", phasorGraph, 10);
                savingImage = false;
                phasorGraph.updateGraph(phasorsGraphHandler);
                return;
            }
            if (hasPoints) {
                int option2Selected = JOptionPane.showConfirmDialog(
                        null,
                        "Save only references?\nSelect NO for saving all data.",
                        "Save",
                        JOptionPane.YES_NO_OPTION
                );
                if (option2Selected == JOptionPane.CANCEL_OPTION) {
                    return;
                }
                if (option2Selected == JOptionPane.YES_OPTION) {
                    int option3Selected = JOptionPane.showConfirmDialog(
                            null,
                            "Average all in one?\nSelect NO for saving as separate points.",
                            "Save",
                            JOptionPane.YES_NO_OPTION
                    );
                    if (option3Selected == JOptionPane.CANCEL_OPTION) {
                        return;
                    }
                    if (option3Selected == JOptionPane.YES_OPTION) {
                        String pointName = JOptionPane.showInputDialog(
                                "Reference point name",
                                "averaged"
                        );
                        if (pointName == null) {
                            return;
                        }
                        ArrayList<ReferencePoint> averaged = new ArrayList<>();
                        double g = 0;
                        double s = 0;
                        int count = 0;
                        for (ReferencePoint point : model.getPointsAt(harmonicIndex)) {
                            double[] coor = point.getCoor();
                            g += coor[0];
                            s += coor[1];
                            count++;
                        }
                        ReferencePoint newPoint = new ReferencePoint(harmonicIndex, pointName, g / count, s / count);
                        averaged.add(newPoint);
                        FilesIO.saveFile(
                                averaged,
                                FileType.rfp,
                                pointName
                        );
                        return;
                    }
                    if (option3Selected == JOptionPane.NO_OPTION) {
                        ArrayList<ReferencePoint> pointsToSave = new ArrayList<>();
                        for (int i = 0; i < selectedReferenceIndices.length; i++) {
                            int index = selectedReferenceIndices[i];
                            pointsToSave.add(model.getPointsAt(harmonicIndex).get(index));
                        }
                        FilesIO.saveFile(
                                pointsToSave,
                                FileType.rfp,
                                "reference points"
                        );
                        return;
                    }
                    return;
                }
            }
            FilesIO.saveFile(
                    model,
                    FileType.sprf,
                    "New fraction calculation file"
            );
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(
                    null,
                    ex.getMessage(),
                    "Something is wrong...",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void setZoom() {
        if (model.assays.get(assaySelectedIndex).isZoomed) {
            model.assays.get(assaySelectedIndex).minGZoom = -1.0;
            model.assays.get(assaySelectedIndex).maxGZoom = 1.0;
            model.assays.get(assaySelectedIndex).minSZoom = -1.0;
            model.assays.get(assaySelectedIndex).maxSZoom = 1.0;
            model.assays.get(assaySelectedIndex).isZoomed = false;
            phasorGraph.updateGraph(phasorsGraphHandler);
            updatePhasorPanel();
            return;
        }
        double[] insertedValues = new double[4];
        String[] messages = new String[]{
            "Insert x minimum",
            "Insert x maximum",
            "Insert y minimum",
            "Insert y maximum"
        };
        double[] currentValues = new double[]{
            model.assays.get(assaySelectedIndex).minGZoom,
            model.assays.get(assaySelectedIndex).maxGZoom,
            model.assays.get(assaySelectedIndex).minSZoom,
            model.assays.get(assaySelectedIndex).maxSZoom
        };
        boolean cancel = false;
        for (int i = 0; i < messages.length; i++) {
            String textInserted = JOptionPane.showInputDialog(messages[i], currentValues[i]);
            if (textInserted == null) {
                cancel = true;
                break;
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
        if (!cancel) {
            model.assays.get(assaySelectedIndex).minGZoom = insertedValues[0];
            model.assays.get(assaySelectedIndex).maxGZoom = insertedValues[1];
            model.assays.get(assaySelectedIndex).minSZoom = insertedValues[2];
            model.assays.get(assaySelectedIndex).maxSZoom = insertedValues[3];
            model.assays.get(assaySelectedIndex).isZoomed = true;
            phasorGraph.updateGraph(phasorsGraphHandler);
            updatePhasorPanel();
        }
    }

    private void setEquations() {
        String message = "Insert number of components";
        int value = 0;
        String textInserted = JOptionPane.showInputDialog(message, 2);
        if (textInserted == null) {
            return;
        }
        ArrayList<String> warnings = new ArrayList<>();
        try {
            try {
                value = Integer.parseInt(textInserted);
            } catch (Exception ex) {
                throw new Exception("Wrong number format.");
            }
            if (value < 2) {
                throw new Exception("The number of components must be higher than 1.");
            }
            int sum = 0;
            for (int i = 0; i < model.calculator.points.length; i++) {
                ArrayList<ReferencePoint> points = model.calculator.points[i];
                if (points.size() > 0) {
                    if (points.size() < value) {
                        warnings.add(Constants.harmonicStr()[i]);
                    } else {
                        sum++;
                    }
                }
            }
            if (2 * sum < value - 1) {
                throw new Exception("There is not enough information to contruct a \n"
                        + "system of equations for " + value + " components.");
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(
                    null,
                    ex.getMessage(),
                    "Something is wrong...",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (!warnings.isEmpty()) {
            String warningMessage = "The following harmonics are missing some reference points and they cannot be used:";
            for (String warning : warnings) {
                warningMessage += "\n" + warning;
            }
            JOptionPane.showMessageDialog(
                    null,
                    warningMessage,
                    "Missing reference points",
                    JOptionPane.WARNING_MESSAGE);
        }
        model.calculator.setNumberOfComponents(value);
        model.calculator.system.clear();
        equationsSetted = false;
        equationsDialog = new EquationsDialog(model.calculator, view, this);
        equationsDialog.pack();
        equationsDialog.setLocationRelativeTo(view);
        equationsDialog.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        equationsDialog.setResizable(false);
        equationsDialog.setVisible(true);
    }

    /* List Handler */
    
    private class ListDelegateHandler implements ListViewDelegate {

        static final int SPECTRA_LIST = 0;
        static final int ASSAY_LIST = 1;
        static final int REFERENCE_LIST = 2;

        final int type;

        ListDelegateHandler(int type) {
            this.type = type;
        }

        @Override
        public boolean canSelectMultipleRows() {
            return type == REFERENCE_LIST;
        }

        @Override
        public ArrayList<Listeable> getData() {
            if (model.assays.isEmpty()) {
                return null;
            }
            switch (type) {
                case ASSAY_LIST:
                    ArrayList<Listeable> list = new ArrayList<>();
                    for (MultispectralPhasor phasor : model.assays) {
                        list.add(phasor);
                    }
                    return list;
                case SPECTRA_LIST:
                    if (assaySelectedIndex >= 0) {
                        list = new ArrayList<>();
                        for (Spectrum spectrum : model.assays.get(assaySelectedIndex).spectra) {
                            list.add(spectrum);
                        }
                        return list;
                    }
                    return null;
                case REFERENCE_LIST:
                    list = new ArrayList<>();
                    for (ReferencePoint point : model.getPointsAt(selectedHarmonicIndex())) {
                        list.add(point);
                    }
                    return list;
            }
            return null;
        }

        @Override
        public int[] getSelectedIndices() {
            switch (type) {
                case ASSAY_LIST:
                    return new int[]{assaySelectedIndex};
                case SPECTRA_LIST:
                    return new int[]{spectraSelectedIndex};
                case REFERENCE_LIST:
                    return selectedReferenceIndices;
            }
            return null;
        }

        @Override
        public void selectionChanged(int[] newIndices) {
            if (model.assays.isEmpty()) {
                return;
            }
            if (newIndices == null || newIndices.length == 0) {
                return;
            }
            switch (type) {
                case ASSAY_LIST:
                    assaySelectedIndex = newIndices[0];
                    spectraSelectedIndex = 0;
                    spectraList.updateList();
                    updatePhasorPanel();
                    break;
                case SPECTRA_LIST:
                    spectraSelectedIndex = newIndices[0];
                    phasorGraph.updateGraph(phasorsGraphHandler);
                    break;
                case REFERENCE_LIST:
                    if (newIndices.length == 1) {
                        firstReferenceSelectedIndex = newIndices[0];
                    }
                    selectedReferenceIndices = newIndices;
                    phasorGraph.updateGraph(phasorsGraphHandler);
                    updateButtons();
                    break;
            }
            updateLabel();
        }

    }

    /* Phasor Graph Handler */
    
    private class PhasorsGraphDelegateHandler extends GraphDelegate {

        @Override
        public boolean canZoom() {
            return !model.assays.get(assaySelectedIndex).isZoomed;
        }

        @Override
        public double[][] curveValues(int yAxisIndex, int curveIndex) {
            double[] x = new double[selectedReferenceIndices.length + 1];
            double[] y = new double[selectedReferenceIndices.length + 1];
            for (int i = 0; i < selectedReferenceIndices.length; i++) {
                int index = selectedReferenceIndices[i];
                double[] tempCoord = model.getPointsAt(selectedHarmonicIndex()).get(index).getCoor();
                x[i] = tempCoord[0];
                y[i] = tempCoord[1];
            }
            int index = selectedReferenceIndices[0];
            double[] tempCoord = model.getPointsAt(selectedHarmonicIndex()).get(index).getCoor();
            x[selectedReferenceIndices.length] = tempCoord[0];
            y[selectedReferenceIndices.length] = tempCoord[1];
            return new double[][]{x, y};
        }

        @Override
        public ColorSource curvesColorSource(int yAxisIndex) {
            return ColorSource.fixedColor(Color.blue, "Blue");
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
            int numberOfSpectra = model.assays.get(assaySelectedIndex).spectra.size();
            if (dotIndex >= numberOfSpectra) {
                return DotShape.cross;
            } else {
                return model.assays.get(assaySelectedIndex).dotShape;
            }
        }

        @Override
        public double[] dotValue(int yAxisIndex, int dotIndex) {
            int numberOfSpectra = model.assays.get(assaySelectedIndex).spectra.size();
            if (dotIndex >= numberOfSpectra) {
                dotIndex -= numberOfSpectra;
                int index = selectedReferenceIndices[dotIndex];
                return model.getPointsAt(selectedHarmonicIndex()).get(index).getCoor();
            } else {
                double[] harmonic = model.assays.get(assaySelectedIndex).harmonic;
                return model.assays.get(assaySelectedIndex).getPhasor(dotIndex, harmonic);
            }
        }

        @Override
        public double[] fixedXExtremes() {
            if (validData()) {
                return new double[]{
                    model.assays.get(assaySelectedIndex).minGZoom,
                    model.assays.get(assaySelectedIndex).maxGZoom
                };
            }
            return new double[]{-1, 1};
        }

        @Override
        public double[] fixedYExtremes(int index) {
            if (validData()) {
                return new double[]{
                    model.assays.get(assaySelectedIndex).minSZoom,
                    model.assays.get(assaySelectedIndex).maxSZoom
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
            if (selectedReferenceIndices != null && selectedReferenceIndices.length >= 2) {
                return 1;
            }
            return 0;
        }

        @Override
        public int numberOfDots(int yAxisIndex) {
            if (!validData()) {
                return 0;
            }
            int totalDots = model.assays.get(assaySelectedIndex).spectra.size();
            if (selectedReferenceIndices != null && !model.getPointsAt(selectedHarmonicIndex()).isEmpty()) {
                return totalDots + selectedReferenceIndices.length;
            }
            return totalDots;
        }

        @Override
        public int numberOfVerticalLines() {
            return 0;
        }

        @Override
        public int[] selectedDots(int yAxisIndex) {
            if (savingImage) {
                int[] allSelected = new int[numberOfDots(yAxisIndex)];
                for (int i = 0; i < allSelected.length; i++) {
                    allSelected[i] = i;
                }
                return allSelected;
            }
            if (!validData()) {
                return new int[]{};
            }
            if (!model.getPointsAt(selectedHarmonicIndex()).isEmpty()) {
                int totalDots = numberOfDots(yAxisIndex);
                int[] dotsSelected = new int[selectedReferenceIndices.length + 1];
                for (int i = 0; i < selectedReferenceIndices.length; i++) {
                    dotsSelected[i] = totalDots - selectedReferenceIndices.length + i;
                }
                dotsSelected[selectedReferenceIndices.length] = spectraSelectedIndex;
                return dotsSelected;
            }
            return new int[]{spectraSelectedIndex};
        }

        @Override
        public boolean userInteraction() {
            return true;
        }

        @Override
        public boolean validData() {
            return assaySelectedIndex >= 0
                    && spectraSelectedIndex >= 0
                    && !model.assays.isEmpty();
        }

        @Override
        public void zoomed(double minX, double maxX, double maxY, double minY) {
            zoom(true, minX, maxX, maxY, minY);
        }

    }

}
