package org.dyndns.fzoli.mobilerc;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;

/**
 *
 * @author zoli
 */
public class RadarArrow extends BufferedImage {

    private final int size;
    private final Graphics2D g;
    private final Point[] points;

    private Shape shape;
    private Double rotation;
    
    public RadarArrow(int size) {
        this(size, null);
    }
    
    public RadarArrow(int size, Double rotation) {
        super(size, size, BufferedImage.TYPE_INT_ARGB);
        
        this.size = size;
        this.points = new Point[] {
            new Point((int)(size * 0.2), (int)(size - size * 0.2)),
            new Point((int)(size / 2.0), (int)(size * 0.2)),
            new Point((int)(size - size * 0.2), (int)(size - size * 0.2)),
            new Point((int)(size / 2.0), (int)(size * 0.65))
        };
        
        this.g = (Graphics2D) getGraphics();
        this.g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        setRotation(rotation);
    }
    
    public Double getRotation() {
        return rotation;
    }
    
    public RadarArrow setRotation(Double rotation) {
        this.rotation = rotation;
        if (rotation == null) {
            double s = size * 0.3;
            double p = (size / 2.0) - (s / 2.0);
            shape = new Ellipse2D.Double(p, p, s, s);
        }
        else {
            int s = (int) (size / 2.0);
            shape = new RotateablePolygon(points).rotate(rotation, new Point(s, s));
        }
        draw();
        return this;
    }
    
    private void draw() {
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.CLEAR, 0.0f));
        g.fillRect(0, 0, size, size);
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
        g.setColor(new Color(50, 170, 200));
        g.fill(shape);
        g.setColor(new Color(0, 0, 170));
        g.setStroke(new BasicStroke((int)(size / 20.0)));
        g.draw(shape);
    }
    
}