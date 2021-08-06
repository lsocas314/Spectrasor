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

import views.common.ColorSource;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import javax.swing.JPanel;

/**
 * Class defining a graph area to draw.
 *
 * @author L.B.P.Socas
 */
final class GraphArea extends JPanel {

    // ######### Internal implementation ##########
    
    static final int AXIS_SPACE = 20;
    static final int PREFERRED_GRAPH_SIZE = 200;
    static final float[] DASH = new float[]{10.0f, 2.0f};
    static final float DASH_WIDTH = 2.0f;
    static final double SELECTION_TOLERANCE = 0.03;
    static final float DOT_BORDER_WIDTH = 1.0f;
    static final float DOT_THICK_BORDER_WIDTH = 3.0f;
    static final double DOT_SIZE_PROPORTION = 0.03;

    double xMax = -Double.MAX_VALUE;
    double xMin = Double.MAX_VALUE;

    GraphArea(GraphDelegate delegate) {
        this.delegate = delegate;
        size = new Dimension();
        setBackground(Color.white);
        if (delegate.userInteraction()) {
            MouseHandler handler = new MouseHandler();
            addMouseListener(handler);
            addMouseMotionListener(handler);
        }
        updateData();
    }

    void updateData() {
        int numberOfYAxis = delegate.numberOfYAxis();
        curves = new ArrayList[numberOfYAxis];
        dots = new ArrayList[numberOfYAxis];
        yMin = new double[numberOfYAxis];
        yMax = new double[numberOfYAxis];
        for (int yAxis = 0; yAxis < numberOfYAxis; yAxis++) {
            curves[yAxis] = new ArrayList<>();
            for (int i = 0; i < delegate.numberOfCurves(yAxis); i++) {
                double[][] curveValues = delegate.curveValues(yAxis, i);
                curves[yAxis].add(new Curve(curveValues[Curve.X_INDEX], curveValues[Curve.Y_INDEX]));
            }
            int[] selectedCurveIndices = delegate.selectedCurves(yAxis);
            for (int i = 0; i < selectedCurveIndices.length; i++) {
                curves[yAxis].get(selectedCurveIndices[i]).selected = true;
            }
            dots[yAxis] = new ArrayList<>();
            for (int i = 0; i < delegate.numberOfDots(yAxis); i++) {
                double[] dotValue = delegate.dotValue(yAxis, i);
                GraphDelegate.DotShape shape = delegate.dotShape(yAxis, i);
                dots[yAxis].add(new Dot(dotValue[Curve.X_INDEX], dotValue[Curve.Y_INDEX], shape));
            }
            int[] selectedDotIndices = delegate.selectedDots(yAxis);
            for (int i = 0; i < selectedDotIndices.length; i++) {
                dots[yAxis].get(selectedDotIndices[i]).selected = true;
            }
        }
        verticalLines = new double[delegate.numberOfVerticalLines()];
        for (int i = 0; i < verticalLines.length; i++) {
            verticalLines[i] = delegate.verticalLineValue(i);
        }
        resetAxisExtremes();
    }

    double[] xExtremes() {
        if (!delegate.validData()) {
            return new double[]{-Double.MAX_VALUE, Double.MAX_VALUE};
        }
        return new double[]{xMin, xMax};
    }

    double[] yExtremes() {
        if (!delegate.validData()) {
            return new double[]{-Double.MAX_VALUE, Double.MAX_VALUE};
        }
        int activeYAxisIndex = delegate.getActiveYAxisIndex();
        return new double[]{yMin[activeYAxisIndex], yMax[activeYAxisIndex]};
    }
    
    @Override
    public void paint(Graphics g) {
        super.paint(g);
        Graphics2D g2d = (Graphics2D) g;
        if (delegate.isPolarGraph()) {
            drawPolarGrid(g2d);
        }
        if (delegate.validData()) {
            for (int i = 0; i < delegate.numberOfYAxis(); i++) {
                if (curves != null) {
                    drawCurves(i, g2d);
                }
                if (dots != null) {
                    drawDots(i, g2d);
                }
            }
            if (isZooming) {
                drawZooomSquare(g2d);
            }
            if (verticalLines != null) {
                drawVerticalLine(g2d);
            }
        }
    }

    @Override
    public void setBounds(int x, int y, int width, int height) {
        super.setBounds(x, y, width, height);
        size.width = width;
        size.height = height;
        repaint();
    }

    // ######### Private implementation ##########
    
    private final GraphDelegate delegate;
    private final Dimension size;
    private boolean isZooming = false;
    private int zoomXStart;
    private int zoomYStart;
    private int zoomXEnd;
    private int zoomYEnd;
    private double[] yMax;
    private double[] yMin;
    private double[] verticalLines;
    private ArrayList<Curve>[] curves;
    private ArrayList<Dot>[] dots;

    private void resetAxisExtremes() {
        xMax = -Double.MAX_VALUE;
        xMin = Double.MAX_VALUE;
        double[] fixedXExtremes = delegate.fixedXExtremes();
        boolean xFixed = false;
        if (fixedXExtremes != null) {
            xMin = fixedXExtremes[0];
            xMax = fixedXExtremes[1];
            xFixed = true;
        }
        for (int i = 0; i < delegate.numberOfYAxis(); i++) {
            boolean yFixed = false;
            yMax[i] = -Double.MAX_VALUE;
            yMin[i] = Double.MAX_VALUE;
            double[] fixedYExtremes = delegate.fixedYExtremes(i);
            if (fixedYExtremes != null) {
                yMin[i] = fixedYExtremes[0];
                yMax[i] = fixedYExtremes[1];
                yFixed = true;
            }
            if (xFixed && yFixed) {
                continue;
            }
            if (!curves[i].isEmpty() || !dots[i].isEmpty()) {
                double temp;
                for (Curve curve : curves[i]) {
                    if (!xFixed) {
                        temp = curve.getMax(Curve.X_INDEX);
                        if (temp > xMax) {
                            xMax = temp;
                        }
                        temp = curve.getMin(Curve.X_INDEX);
                        if (temp < xMin) {
                            xMin = temp;
                        }
                    }
                    if (!yFixed) {
                        temp = curve.getMax(Curve.Y_INDEX);
                        if (temp > yMax[i]) {
                            yMax[i] = temp;
                        }
                        temp = curve.getMin(Curve.Y_INDEX);
                        if (temp < yMin[i]) {
                            yMin[i] = temp;
                        }
                    }
                }
                for (Dot dot : dots[i]) {
                    if (!xFixed) {
                        temp = dot.x;
                        if (temp > xMax) {
                            xMax = temp;
                        }
                        if (temp < xMin) {
                            xMin = temp;
                        }
                    }
                    if (!yFixed) {
                        temp = dot.y;
                        if (temp > yMax[i]) {
                            yMax[i] = temp;
                        }
                        if (temp < yMin[i]) {
                            yMin[i] = temp;
                        }
                    }
                }
            }
        }
    }

    private void drawCurves(int yAxisIndex, Graphics2D g2d) {
        int xPixel;
        int yPixel;
        int nextXPixel;
        int nextYPixel;
        double[] xData;
        double[] yData;
        Color[] colors = null;
        ColorSource curvesColorSource = delegate.curvesColorSource(yAxisIndex);
        if (curvesColorSource == null) {
            g2d.setColor(Color.BLACK);
        } else {
            colors = curvesColorSource.getColors(curves[yAxisIndex].size());
        }
        for (int i = 0; i < curves[yAxisIndex].size(); i++) {
            Curve curve = curves[yAxisIndex].get(i);
            if (colors != null) {
                g2d.setColor(colors[i]);
            }
            xData = curve.data[Curve.X_INDEX];
            yData = curve.data[Curve.Y_INDEX];
            for (int j = 0; j < xData.length - 1; j++) {
                if (xData[j] < xMin
                        || xData[j] > xMax
                        || xData[j + 1] < xMin
                        || xData[j + 1] > xMax
                        || yData[j] < yMin[yAxisIndex]
                        || yData[j] > yMax[yAxisIndex]
                        || yData[j + 1] < yMin[yAxisIndex]
                        || yData[j + 1] > yMax[yAxisIndex]) {
                    continue;
                }
                if (Double.isNaN(xData[j])
                        || Double.isNaN(yData[j])
                        || Double.isNaN(xData[j + 1])
                        || Double.isNaN(yData[j + 1])) {
                    xPixel = 0;
                    nextXPixel = 0;
                    yPixel = 0;
                    nextYPixel = 0;
                } else {
                    xPixel = xValueToPixel(xData[j]);
                    nextXPixel = xValueToPixel(xData[j + 1]);
                    yPixel = yValueToPixel(yAxisIndex, yData[j]);
                    nextYPixel = yValueToPixel(yAxisIndex, yData[j + 1]);
                }
                g2d.setStroke(new BasicStroke(curve.curveWidth()));
                g2d.drawLine(xPixel, yPixel, nextXPixel, nextYPixel);
            }
        }
    }

    private void drawDots(int yAxisIndex, Graphics2D g2d) {
        int xPixel;
        int yPixel;
        int dotSize = (int) (DOT_SIZE_PROPORTION * size.width);
        Color[] colors = null;
        Color[] fillColors = null;
        ColorSource dotsColorSource = delegate.dotsColorSource(yAxisIndex);
        ColorSource dotsFillColorSource = delegate.dotsFillColorSource(yAxisIndex);
        if (dotsColorSource != null) {
            colors = dotsColorSource.getColors(dots[yAxisIndex].size());
        }
        if (dotsFillColorSource != null) {
            fillColors = dotsFillColorSource.getColors(dots[yAxisIndex].size());
        }
        for (int i = 0; i < dots[yAxisIndex].size(); i++) {
            Dot dot = dots[yAxisIndex].get(i);
            xPixel = xValueToPixel(dot.x);
            yPixel = yValueToPixel(yAxisIndex, dot.y);
            Color stroke = (colors != null) ? colors[i] : Color.BLACK;
            Color fill = (fillColors != null) ? fillColors[i] : Color.BLACK;
            switch (dot.shape) {
                case rectangle:
                    if (dot.selected) {
                        g2d.setColor(fill);
                        g2d.fillRect(xPixel - dotSize / 2, yPixel - dotSize / 2, dotSize, dotSize);
                    }
                    g2d.setColor(stroke);
                    g2d.setStroke(new BasicStroke(DOT_BORDER_WIDTH));
                    g2d.drawRect(xPixel - dotSize / 2, yPixel - dotSize / 2, dotSize, dotSize);
                    break;
                case circle:
                    if (dot.selected) {
                        g2d.setColor(fill);
                        g2d.fillOval(xPixel - dotSize / 2, yPixel - dotSize / 2, dotSize, dotSize);
                    }
                    g2d.setColor(stroke);
                    g2d.setStroke(new BasicStroke(DOT_BORDER_WIDTH));
                    g2d.drawOval(xPixel - dotSize / 2, yPixel - dotSize / 2, dotSize, dotSize);
                    break;
                case cross:
                    if (dot.selected) {
                        g2d.setColor(fill);
                        g2d.setStroke(new BasicStroke(DOT_THICK_BORDER_WIDTH));
                    } else {
                        g2d.setColor(stroke);
                        g2d.setStroke(new BasicStroke(DOT_BORDER_WIDTH));
                    }
                    g2d.drawLine(xPixel - dotSize / 2, yPixel, xPixel + dotSize / 2, yPixel);
                    g2d.drawLine(xPixel, yPixel - dotSize / 2, xPixel, yPixel + dotSize / 2);
                    break;
                case triangle:
                    java.awt.Polygon triangle = new java.awt.Polygon();
                    triangle.addPoint(xPixel, yPixel - dotSize / 2);
                    triangle.addPoint(xPixel - dotSize / 2, yPixel + dotSize / 2);
                    triangle.addPoint(xPixel + dotSize / 2, yPixel + dotSize / 2);
                    if (dot.selected) {
                        g2d.setColor(fill);
                        g2d.fillPolygon(triangle);
                    }
                    g2d.setColor(stroke);
                    g2d.setStroke(new BasicStroke(DOT_BORDER_WIDTH));
                    g2d.drawPolygon(triangle);
                    break;
            }
        }
    }

    private void drawPolarGrid(Graphics2D g2d) {
        int xCenter = xValueToPixel(0.0);
        int yCenter = yValueToPixel(0, 0.0);
        int xRadius1 = xValueToPixel(1.0) - xValueToPixel(0.0);
        int yRadius1 = yValueToPixel(0, -1.0) - yValueToPixel(0, 0.0);
        int xRadius2 = xValueToPixel(0.67) - xValueToPixel(0.0);
        int yRadius2 = yValueToPixel(0, -0.67) - yValueToPixel(0, 0.0);
        int xRadius3 = xValueToPixel(0.33) - xValueToPixel(0.0);
        int yRadius3 = yValueToPixel(0, -0.33) - yValueToPixel(0, 0.0);
        BasicStroke stroke = new BasicStroke(
                1.0f,
                BasicStroke.CAP_BUTT,
                BasicStroke.JOIN_BEVEL,
                0.0f,
                new float[]{8.0f, 5.0f},
                0.0f
        );
        g2d.setStroke(stroke);
        g2d.setColor(Color.GRAY);
        int tempX;
        int tempY;
        for (int i = 0; i < 8; i++) {
            tempX = xValueToPixel(Math.sin(i * 45 * Math.PI / 180));
            tempY = yValueToPixel(0, Math.cos(i * 45 * Math.PI / 180));
            g2d.drawLine(xCenter, yCenter, tempX, tempY);
        }
        g2d.drawOval(xCenter - xRadius1, yCenter - yRadius1, 2 * xRadius1, 2 * yRadius1);
        g2d.drawOval(xCenter - xRadius2, yCenter - yRadius2, 2 * xRadius2, 2 * yRadius2);
        g2d.drawOval(xCenter - xRadius3, yCenter - yRadius3, 2 * xRadius3, 2 * yRadius3);
    }

    private void drawZooomSquare(Graphics2D g2d) {
        g2d.setColor(Color.BLACK);
        BasicStroke stroke = new BasicStroke(
                2.0f,
                BasicStroke.CAP_BUTT,
                BasicStroke.JOIN_BEVEL,
                0.0f,
                new float[]{10.0f, 2.0f},
                0.0f
        );
        g2d.setStroke(stroke);
        int startX = Math.min(zoomXStart, zoomXEnd);
        int startY = Math.min(zoomYStart, zoomYEnd);
        int w = Math.abs(zoomXEnd - zoomXStart);
        int h = Math.abs(zoomYEnd - zoomYStart);
        g2d.drawRect(startX, startY, w, h);
    }

    private void drawVerticalLine(Graphics2D g2d) {
        int xPixel;
        for (int i = 0; i < verticalLines.length; i++) {
            xPixel = xValueToPixel(verticalLines[i]);
            g2d.setColor(Color.BLACK);
            BasicStroke stroke = new BasicStroke(
                    DASH_WIDTH,
                    BasicStroke.CAP_BUTT,
                    BasicStroke.JOIN_BEVEL,
                    0.0f,
                    DASH,
                    0.0f
            );
            g2d.setStroke(stroke);
            g2d.drawLine(xPixel, 0, xPixel, size.height);
        }
    }

    private double xPixelToValue(int xPixel) {
        double valueCoord;
        double xScale = (xMax - xMin) / size.width;
        valueCoord = xMin + (xPixel * xScale);
        return valueCoord;
    }

    private int xValueToPixel(double xValue) {
        int pixelCoord;
        double xScale = size.width / (xMax - xMin);
        pixelCoord = (int) ((xValue - xMin) * xScale);
        return pixelCoord;
    }

    private double yPixelToValue(int yAxisIndex, int yPixel) {
        double valueCoord;
        double yScale = (yMax[yAxisIndex] - yMin[yAxisIndex]) / size.height;
        valueCoord = yMin[yAxisIndex] + ((size.height - yPixel) * yScale);
        return valueCoord;
    }

    private int yValueToPixel(int yAxisIndex, double yValue) {
        int pixelCoord;
        double yScale = size.height / (yMax[yAxisIndex] - yMin[yAxisIndex]);
        pixelCoord = (int) (size.height - (yValue - yMin[yAxisIndex]) * yScale);
        return pixelCoord;
    }

    private class MouseHandler extends MouseAdapter {

        private int verticalLineToDrag = -1;
        private boolean isDragging = false;

        @Override
        public void mouseClicked(MouseEvent e) {
            if (!delegate.validData()) {
                return;
            }
            double xValue = xPixelToValue(e.getX());
            for (int yAxis = 0; yAxis < delegate.numberOfYAxis(); yAxis++) {
                double yValue = yPixelToValue(yAxis, e.getY());
                double width = SELECTION_TOLERANCE * (xMax - xMin);
                double height = SELECTION_TOLERANCE * (yMax[yAxis] - yMin[yAxis]);
                for (int i = 0; i < curves[yAxis].size(); i++) {
                    if (curves[yAxis].get(i).contains(xValue, yValue, width, height)) {
                        delegate.curveSelected(yAxis, i);
                        return;
                    }
                }
            }
            if (verticalLines != null) {
                double minDiff = Double.MAX_VALUE;
                int index = 0;
                for (int i = 0; i < verticalLines.length; i++) {
                    double diff = Math.abs(xValue - verticalLines[i]);
                    if (diff < minDiff) {
                        minDiff = diff;
                        index = i;
                    }
                }
                delegate.verticalLineMoved(index, xValue);
            }
        }

        @Override
        public void mousePressed(MouseEvent e) {
            if (delegate.canZoom()) {
                zoomXStart = e.getX();
                zoomYStart = e.getY();
            }
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            if (!delegate.validData()) {
                return;
            }
            verticalLineToDrag = -1;
            isDragging = false;
            if (isZooming) {
                double minX = xPixelToValue(Math.min(zoomXStart, zoomXEnd));
                double maxX = xPixelToValue(Math.max(zoomXStart, zoomXEnd));
                double minY = yPixelToValue(0, Math.min(zoomYStart, zoomYEnd));
                double maxY = yPixelToValue(0, Math.max(zoomYStart, zoomYEnd));
                double[] xExt = xExtremes();
                double[] yExt = yExtremes();
                double fullX = Math.abs(xExt[0] - xExt[1]);
                double fullY = Math.abs(yExt[0] - yExt[1]);
                boolean xSideOk = (Math.abs(maxX - minX)) > 0.05 * fullX;
                boolean ySideOk = (Math.abs(maxY - minY)) > 0.05 * fullY;
                if (xSideOk && ySideOk) {
                    delegate.zoomed(minX, maxX, minY, maxY);
                }
                isZooming = false;
                repaint();
            }
        }

        @Override
        public void mouseDragged(MouseEvent e) {
            if (!delegate.validData()) {
                return;
            }
            if (verticalLines != null) {
                double xValue = xPixelToValue(e.getX());
                if (!isDragging) {
                    isDragging = true;
                    double minDiff = Double.MAX_VALUE;
                    for (int i = 0; i < verticalLines.length; i++) {
                        double diff = Math.abs(xValue - verticalLines[i]);
                        if (diff < minDiff) {
                            minDiff = diff;
                            verticalLineToDrag = i;
                        }
                    }
                }
                if (xValue > xMax) {
                    xValue = xMax;
                }
                if (xValue < xMin) {
                    xValue = xMin;
                }
                if (verticalLineToDrag != -1) {
                    delegate.verticalLineMoved(verticalLineToDrag, xValue);
                }
            }
            if (delegate.canZoom()) {
                int xPixel = e.getX();
                int yPixel = e.getY();
                if (!isDragging) {
                    isDragging = true;
                }
                zoomXEnd = xPixel;
                zoomYEnd = yPixel;
                isZooming = true;
                repaint();
            }
        }

    }

}
