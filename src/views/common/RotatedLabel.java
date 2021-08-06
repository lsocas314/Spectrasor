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
package views.common;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import javax.swing.JLabel;

/**
 * A label view that is rotated.
 *
 * @author L.B.P.Socas
 */
public final class RotatedLabel extends JLabel {

    // ######### Public API ##########
    
    /**
     * Enumerator that defines the label direction
     */
    public enum Direction {

        /**
         * Vertical-up direction.
         */
        verticalUp,

        /**
         * Vertical-down direction.
         */
        verticalDown
    }

    /**
     * Create a new label with the specified rotation.
     *
     * @param direction the direction of the label's rotation.
     */
    public RotatedLabel(Direction direction) {
        super();
        this.direction = direction;
    }

    @Override
    public int getHeight() {
        return getSize().height;
    }

    @Override
    public Dimension getPreferredSize() {
        Dimension preferredSize = super.getPreferredSize();
        return new Dimension(preferredSize.height, preferredSize.width);
    }

    @Override
    public Dimension getSize() {
        if (!needsRotate) {
            return super.getSize();
        }
        Dimension size = super.getSize();
        return new Dimension(size.height, size.width);
    }

    @Override
    public int getWidth() {
        return getSize().width;
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D gr = (Graphics2D) g.create();
        switch (direction) {
            case verticalUp:
                gr.translate(0, getSize().getHeight());
                gr.transform(AffineTransform.getQuadrantRotateInstance(-1));
                break;
            case verticalDown:
                gr.transform(AffineTransform.getQuadrantRotateInstance(1));
                gr.translate(0, -getSize().getWidth());
                break;
        }
        needsRotate = true;
        super.paintComponent(gr);
        needsRotate = false;
    }

    // ######### Private implementation ##########

    private final Direction direction;
    private boolean needsRotate;
    
}
