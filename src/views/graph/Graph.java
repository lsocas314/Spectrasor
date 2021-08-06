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
package views.graph;

import auxiliary.MathFeatures;
import views.common.RotatedLabel;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

/**
 * A panel representing a graph in the user interface.
 *
 * @author L.B.P.Socas
 */
public final class Graph extends JPanel {

    // ######### Public API ##########
    
    /**
     * True if the graph should show the x axis values, false otherwise.
     */
    public boolean showXAxisValues = false;

    /**
     * True if the graph should show the y axis values, false otherwise.
     */
    public boolean showYAxisValues = false;

    /**
     * Define the {x, y} number of figures to show in the axis.
     */
    public double[] axisNumberOfFigures = new double[]{1, 1};

    /**
     * Construct a Graph view object by defining an aspect ratio.
     *
     * @param aspectRatio the width/height ratio of the graph.
     * @param delegate the delegation handler.
     */
    public Graph(double aspectRatio, GraphDelegate delegate) {
        this.aspectRatio = aspectRatio;
        setLayout(null);
        setBackground(Color.white);
        graphArea = new GraphArea(delegate);
        size = new Dimension();
        initializeChilds();
    }

    /**
     * Reset the axis of the graph.
     *
     * @param axisLabels the title of the axis.
     * @param axisUnits the units to show in the axis.
     * @param axisColor the color of the axis.
     */
    public void resetAxis(String[] axisLabels, String[] axisUnits, Color[] axisColor) {
        String[] axisText = new String[Math.min(axisLabels.length, axisUnits.length)];
        for (int i = 0; i < axisText.length; i++) {
            if (axisUnits[i] == null) {
                axisText[i] = axisLabels[i];
            } else {
                axisText[i] = axisLabels[i] + " (" + axisUnits[i] + ")";
            }
        }
        xAxisLabel.setText(axisText[Curve.X_INDEX]);
        xAxisLabel.setForeground(axisColor[Curve.X_INDEX]);
        yAxisLabel.setText(axisText[Curve.Y_INDEX]);
        yAxisLabel.setForeground(axisColor[Curve.Y_INDEX]);
        yMaxLabel.setForeground(axisColor[Curve.Y_INDEX]);
        yMinLabel.setForeground(axisColor[Curve.Y_INDEX]);
        repaint();
    }

    /**
     * Update the graph.
     *
     * @param delegate the delegation handler.
     */
    public void updateGraph(GraphDelegate delegate) {
        graphArea.updateData();
        int verticalLines = delegate.numberOfVerticalLines();
        if (verticalLines != 0) {
            if (verticalValues == null) {
                verticalValues = new JLabel[verticalLines];
                for (int i = 0; i < verticalValues.length; i++) {
                    verticalValues[i] = new JLabel();
                    add(verticalValues[i]);
                }
            } else if (verticalValues.length != verticalLines) {
                for (JLabel verticalValue : verticalValues) {
                    remove(verticalValue);
                }
                verticalValues = new JLabel[verticalLines];
                for (int i = 0; i < verticalValues.length; i++) {
                    verticalValues[i] = new JLabel();
                    add(verticalValues[i]);
                }
            }
            for (int i = 0; i < verticalValues.length; i++) {
                verticalValues[i].setForeground(yAxisLabel.getForeground());
                Double value = delegate.yValueForVerticalLine(i);
                if (value == null) {
                    verticalValues[i].setText("");
                } else {
                    String text = MathFeatures.getStringOfValueRoundedByError(false, value, 0.001);
                    String yAxis = yAxisLabel.getText();
                    int parenthesis1 = yAxis.indexOf("(");
                    int parenthesis2 = yAxis.indexOf(")");
                    String units = yAxis.substring(parenthesis1 + 1, parenthesis2);
                    text += " " + units;
                    verticalValues[i].setText(text);
                }
                int lblWidth = verticalValues[i].getGraphics().getFontMetrics().stringWidth(verticalValues[i].getText());
                int w = rightAnchor - leftAnchor;
                double xScale = w / (graphArea.xMax - graphArea.xMin);
                int xPos = (int) ((delegate.verticalLineValue(i) - graphArea.xMin) * xScale);
                int lblXPos = leftAnchor + xPos - (int) (lblWidth * (double) xPos / w);
                verticalValues[i].setBounds(lblXPos, 0, lblWidth, topAnchor);
            }
        } else if (verticalValues != null) {
            for (JLabel verticalValue : verticalValues) {
                remove(verticalValue);
            }
            verticalValues = null;
        }
        repaint();
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension((int) (aspectRatio * GraphArea.PREFERRED_GRAPH_SIZE), GraphArea.PREFERRED_GRAPH_SIZE);
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        Graphics2D g2d = (Graphics2D) g;
        resetAxisNumbers();
        drawBorder(g2d);
        drawAxis(g2d);
        graphArea.repaint();
    }

    @Override
    public void setBounds(int x, int y, int width, int height) {
        Dimension d = getPreferredSize();
        int newWidth = width;
        int newHeight = height;
        if (d.height == height) {
            newWidth = (int) (aspectRatio * height);
        } else if (d.width == width) {
            newHeight = (int) (width / aspectRatio);
        } else {
            if (width <= height) {
                newHeight = (int) (width / aspectRatio);
            } else {
                newWidth = (int) (aspectRatio * height);
            }
        }
        super.setBounds(x, y, newWidth, newHeight);
        size.width = newWidth;
        size.height = newHeight;
        resetAnchors();
        layoutChilds();
    }

    // ######### Private implementation ##########
    
    private final double aspectRatio;
    private final Dimension size;
    private final JLabel xAxisLabel = new JLabel();
    private final JLabel xMaxLabel = new JLabel();
    private final JLabel xMinLabel = new JLabel();
    private final RotatedLabel yAxisLabel = new RotatedLabel(RotatedLabel.Direction.verticalUp);
    private final RotatedLabel yMaxLabel = new RotatedLabel(RotatedLabel.Direction.verticalUp);
    private final RotatedLabel yMinLabel = new RotatedLabel(RotatedLabel.Direction.verticalUp);
    private final GraphArea graphArea;
    private int topAnchor = 0;
    private int bottomAnchor = 0;
    private int leftAnchor = 0;
    private int rightAnchor = 0;
    private JLabel[] verticalValues;

    private void drawAxis(Graphics2D g2d) {
        g2d.setColor(Color.black);
        int w = rightAnchor - leftAnchor + 1;
        int h = bottomAnchor - topAnchor + 1;
        g2d.drawRect(leftAnchor, topAnchor - 1, w, h);
    }

    private void drawBorder(Graphics2D g2d) {
        g2d.setColor(Color.black);
        g2d.drawRect(0, 0, size.width - 1, size.height - 1);
    }

    private void initializeChilds() {
        xMaxLabel.setText("");
        xMaxLabel.setHorizontalAlignment(SwingConstants.TRAILING);
        xMaxLabel.setVerticalAlignment(SwingConstants.CENTER);
        xMinLabel.setText("");
        xMinLabel.setHorizontalAlignment(SwingConstants.LEADING);
        xMinLabel.setVerticalAlignment(SwingConstants.CENTER);
        xAxisLabel.setHorizontalAlignment(SwingConstants.CENTER);
        add(xMaxLabel);
        add(xMinLabel);
        add(xAxisLabel);
        yMaxLabel.setText("");
        yMaxLabel.setHorizontalAlignment(SwingConstants.TRAILING);
        yMaxLabel.setVerticalAlignment(SwingConstants.CENTER);
        yMinLabel.setText("");
        yMinLabel.setHorizontalAlignment(SwingConstants.LEADING);
        yMinLabel.setVerticalAlignment(SwingConstants.CENTER);
        yAxisLabel.setHorizontalAlignment(SwingConstants.CENTER);
        add(yMaxLabel);
        add(yMinLabel);
        add(yAxisLabel);
        add(graphArea);
    }

    private void layoutChilds() {
        Rectangle boundsRect = new Rectangle(
                leftAnchor,
                bottomAnchor,
                rightAnchor - leftAnchor,
                size.height - bottomAnchor
        );
        xAxisLabel.setBounds(boundsRect);
        xMaxLabel.setBounds(boundsRect);
        xMinLabel.setBounds(boundsRect);
        boundsRect = new Rectangle(
                0,
                topAnchor,
                leftAnchor,
                bottomAnchor - topAnchor
        );
        yAxisLabel.setBounds(boundsRect);
        yMaxLabel.setBounds(boundsRect);
        yMinLabel.setBounds(boundsRect);
        graphArea.setBounds(leftAnchor + 1, topAnchor, rightAnchor - leftAnchor, bottomAnchor - topAnchor);
    }

    private void resetAnchors() {
        leftAnchor = (int) (GraphArea.AXIS_SPACE);
        topAnchor = (int) (GraphArea.AXIS_SPACE);
        rightAnchor = (int) (size.width - GraphArea.AXIS_SPACE);
        bottomAnchor = (int) (size.height - GraphArea.AXIS_SPACE);
    }

    private void resetAxisNumbers() {
        if (!showXAxisValues) {
            xMaxLabel.setText("");
            xMinLabel.setText("");
        } else {
            double[] xExtremes = graphArea.xExtremes();
            if (xExtremes[0] == -Double.MAX_VALUE) {
                xMinLabel.setText("");
            } else {
                String text = MathFeatures.getStringOfValueRoundedByError(false, xExtremes[0], axisNumberOfFigures[Curve.X_INDEX]);
                xMinLabel.setText(text);
            }
            if (xExtremes[1] == Double.MAX_VALUE) {
                xMaxLabel.setText("");
            } else {
                String text = MathFeatures.getStringOfValueRoundedByError(false, xExtremes[1], axisNumberOfFigures[Curve.X_INDEX]);
                xMaxLabel.setText(text);
            }
        }
        if (!showYAxisValues) {
            yMaxLabel.setText("");
            yMinLabel.setText("");
        } else {
            double[] yExtremes = graphArea.yExtremes();
            if (yExtremes[0] == -Double.MAX_VALUE) {
                yMinLabel.setText("");
            } else {
                String text = MathFeatures.getStringOfValueRoundedByError(false, yExtremes[0], axisNumberOfFigures[Curve.Y_INDEX]);
                yMinLabel.setText(text);
            }
            if (yExtremes[1] == Double.MAX_VALUE) {
                yMaxLabel.setText("");
            } else {
                String text = MathFeatures.getStringOfValueRoundedByError(false, yExtremes[1], axisNumberOfFigures[Curve.Y_INDEX]);
                yMaxLabel.setText(text);
            }
        }
    }

}
