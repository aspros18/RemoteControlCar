package org.dyndns.fzoli.mobilerc;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

/**
 *
 * @author zoli
 */
public class RadarArrow extends BufferedImage {

    private final int size;
    private final Graphics2D g;
    private final Point[] points;

    private RotateablePolygon pol;

    public RadarArrow(int size) {
        super(size, size, BufferedImage.TYPE_INT_ARGB);
        this.points = new Point[] {
            new Point((int)(size * 0.2), (int)(size - size * 0.2)),
            new Point((int)(size / 2.0), (int)(size * 0.2)),
            new Point((int)(size - size * 0.2), (int)(size - size * 0.2)),
            new Point((int)(size / 2.0), (int)(size * 0.65))
        };
        this.size = size;
        this.pol = new RotateablePolygon(points);
        this.g = (Graphics2D) getGraphics();
        this.g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        draw();
    }

    public RadarArrow(int size, double angle) {
        this(size);
        rotate(angle);
    }
    
    public RadarArrow rotate(double angle) {
        pol = new RotateablePolygon(points);
        pol.rotate(angle, new Point((int) (size / 2.0), (int)(size / 2.0)));
        draw();
        return this;
    }
    
    private void draw() {
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.CLEAR, 0.0f));
        g.fillRect(0, 0, size, size);
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
        g.setColor(new Color(50, 170, 200));
        g.fill(pol);
        g.setColor(new Color(0, 0, 170));
        g.setStroke(new BasicStroke(2));
        g.draw(pol);
    }
    
}