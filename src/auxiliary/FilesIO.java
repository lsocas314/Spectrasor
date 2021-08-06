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

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * Static class for general input/output of files.
 *
 * @author L.B.P.Socas
 */
public final class FilesIO {

    // ######### Public API ##########
    
    /**
     * Stores the last file type selected by the user.
     */
    public static FileType lastFormatSelected = null;

    /**
     * Load data files for provided filters and specifying a constant to define
     * the x axis type.
     *
     * @param filters an array of FileFilter object that contains information
     * about the files that can be loaded.
     * @return a generic array containing the objects loaded from each file.
     * @throws Exception if there is a problem loading the files.
     */
    public static ArrayList<Object> loadFiles(FileFilter[] filters) throws Exception {
        File[] files = chooseFiles("Select files to load...", filters);
        if (files == null) {
            throw new Exception("No files loaded :/");
        }
        if (lastFormatSelected == null) {
            throw new Exception("Wrong format =(");
        }
        try {
            ArrayList<Object> loaded = new ArrayList<>();
            switch (lastFormatSelected) {
                case csv:
                    return loadASCIIRawFile(files, ",");
                case spr:
                case sprl:
                case rfp:
                case sprf:
                    for (File file : files) {
                        FileInputStream inputStream = new FileInputStream(file);
                        ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);
                        loaded.add(objectInputStream.readObject());
                        objectInputStream.close();
                        inputStream.close();
                    }
                    return loaded;
                default:
                    return null;
            }
        } catch (Exception e) {
            throw new Exception("Error loading file :( ...");
        }
    }

    /**
     * Save a provided panel as a PNG image.
     *
     * @param name the filename suggested.
     * @param panel a JPanle object used to construct the image.
     * @param scale a factor used to scale the panel before constructing the
     * image.
     * @throws Exception if there is a problem saving the file.
     */
    public static void saveImage(String name, JPanel panel, int scale) throws Exception {
        JFileChooser fc = new JFileChooser();
        fc.setCurrentDirectory(lastDirectory);
        fc.setDialogTitle("Save image");
        fc.setSelectedFile(new File(name + ".png"));
        fc.setMultiSelectionEnabled(false);
        fc.setAcceptAllFileFilterUsed(false);
        fc.setFileFilter(new FileNameExtensionFilter("Images", "png"));
        int returnVal = fc.showSaveDialog(null);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            try {
                String path = fc.getSelectedFile().getPath();
                File newFile;
                if (!path.endsWith(".png")) {
                    newFile = new File(path + ".png");
                } else {
                    newFile = new File(path);
                }
                BufferedImage image = new BufferedImage(scale * panel.getWidth(), scale * panel.getHeight(), BufferedImage.TYPE_INT_ARGB);
                Graphics2D g2d = image.createGraphics();
                g2d.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
                g2d.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_ENABLE);
                g2d.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
                g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                g2d.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
                g2d.setTransform(AffineTransform.getScaleInstance(scale, scale));
                panel.print(g2d);
                g2d.dispose();
                ImageIO.write(image, "png", newFile);
                lastDirectory = fc.getCurrentDirectory();
            } catch (Exception e) {
                throw new Exception("Error saving file :( ...");
            }
        }
    }

    /**
     * Create and save an object with a custom file format.
     *
     * @param object the object to save.
     * @param format the file format.
     * @param name the filename suggested.
     * @throws Exception if there is a problem saving the file.
     */
    public static void saveFile(Object object, FileType format, String name) throws Exception {
        String extension = format.toString();
        String title;
        switch (format) {
            case spr:
                title = "Save spectrasor (.spr) file...";
                break;
            case sprl:
                title = "Save spectrasor list (.sprl) file...";
                break;
            case rfp:
                title = "Save reference point(s) (.rfp) file...";
                break;
            case sprf:
                title = "Save spectrasor fraction analysis (.sprf) file...";
                break;
            default:
                throw new Exception("Error saving file :( ...");
        }
        JFileChooser fc = new JFileChooser();
        fc.setCurrentDirectory(lastDirectory);
        fc.setDialogTitle(title);
        fc.setSelectedFile(new File(name + "." + extension));
        fc.setMultiSelectionEnabled(false);
        fc.setAcceptAllFileFilterUsed(false);
        fc.setFileFilter(new FileNameExtensionFilter(format.formatDescription(), extension));
        int returnVal = fc.showSaveDialog(null);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            try {
                String path = fc.getSelectedFile().getPath();
                File newFile;
                if (!path.endsWith("." + extension)) {
                    newFile = new File(path + "." + extension);
                } else {
                    newFile = new File(path);
                }
                FileOutputStream fileOutputStream = new FileOutputStream(newFile);
                ObjectOutputStream outputStream = new ObjectOutputStream(fileOutputStream);
                outputStream.writeObject(object);
                outputStream.flush();
                outputStream.close();
                fileOutputStream.close();
                lastDirectory = fc.getCurrentDirectory();
            } catch (Exception e) {
                throw new Exception("Error saving file :( ...");
            }
        }
    }

    /**
     * Create and save an Excel file.
     *
     * @param name the filename suggested.
     * @param workbook an XSSFWorkbook object containing the excel to save.
     * @throws Exception if there is a problem saving the file.
     */
    public static void saveExcel(String name, XSSFWorkbook workbook) throws Exception {
        String extension = "xlsx";
        JFileChooser fc = new JFileChooser();
        fc.setCurrentDirectory(lastDirectory);
        fc.setDialogTitle("Save excel file...");
        fc.setSelectedFile(new File(name + "." + extension));
        fc.setMultiSelectionEnabled(false);
        fc.setAcceptAllFileFilterUsed(false);
        fc.setFileFilter(new FileNameExtensionFilter("Excel files (.xlsx)", extension));
        int returnVal = fc.showSaveDialog(null);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            try {
                String path = fc.getSelectedFile().getPath();
                File newFile;
                if (!path.endsWith("." + extension)) {
                    newFile = new File(path + "." + extension);
                } else {
                    newFile = new File(path);
                }
                FileOutputStream fileOutputStream = new FileOutputStream(newFile);
                workbook.write(fileOutputStream);
                lastDirectory = fc.getCurrentDirectory();
                fileOutputStream.close();
            } catch (Exception e) {
                throw new Exception("Error saving file :( ...");
            }
        }
    }

    // ######### Private implementation ##########
    
    private static File lastDirectory;

    private static File[] chooseFiles(String title, FileFilter[] filters) {
        JFileChooser fc = new JFileChooser();
        fc.setCurrentDirectory(lastDirectory);
        fc.setMultiSelectionEnabled(true);
        fc.setDialogTitle(title);
        fc.setAcceptAllFileFilterUsed(false);
        for (FileFilter filter : filters) {
            fc.addChoosableFileFilter(filter);
        }
        int returnVal = fc.showOpenDialog(null);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            String extension = ((FileNameExtensionFilter) fc.getFileFilter()).getExtensions()[0];
            try {
                lastFormatSelected = FileType.valueOf(extension);
            } catch (Exception e) {
                lastFormatSelected = null;
            }
            lastDirectory = fc.getCurrentDirectory();
            return fc.getSelectedFiles();
        } else {
            lastFormatSelected = null;
            return null;
        }
    }

    private static ArrayList<Object> loadASCIIRawFile(File[] files, String delimiter) throws Exception {
        ArrayList<Object> loaded = new ArrayList<>();
        for (File file : files) {
            try {
                String[][] csvTemp = ASCIIParser.loadPath(file.getPath(), delimiter);
                double[][] data = new double[csvTemp[0].length][csvTemp.length];
                String name = file.getName();
                for (int i = 0; i < csvTemp[0].length; i++) {
                    for (int j = 0; j < csvTemp.length; j++) {
                        if (i == 0 && j == 0) {
                            continue;
                        }
                        data[i][j] = Double.parseDouble(csvTemp[j][i]);
                    }
                }
                loaded.add(new RawData(name, data));
            } catch (Exception e) {
                throw new Exception("Error loading ASCII :( ...");
            }
        }
        return loaded;
    }

}
