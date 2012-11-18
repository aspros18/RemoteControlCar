package org.dyndns.fzoli.rccar.controller.view.map;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import org.dyndns.fzoli.ui.RotateablePolygon;

/**
 * A térképen megjelenő nyíl.
 * @author zoli
 */
class MapArrow extends BufferedImage {

    /**
     * A nyíl mérete.
     */
    private final int size;
    
    /**
     * Referencia rajzoláshoz.
     */
    private final Graphics2D g;
    
    /**
     * A nyíl mérete alapján az északra mutató nyíl pontjai.
     */
    private final Point[] points;

    /**
     * A nyíl alakja.
     */
    private Shape shape;
    
    /**
     * Az irány.
     */
    private Double rotation;
    
    public MapArrow(int size) {
        this(size, null);
    }
    
    public MapArrow(int size, Double rotation) {
        super(size, size, BufferedImage.TYPE_INT_ARGB);
        
        this.size = size;
        this.points = new Point[] { // a nyíl formát eredményező pontok
            new Point((int)(size * 0.2), (int)(size - size * 0.2)),
            new Point((int)(size / 2.0), (int)(size * 0.2)),
            new Point((int)(size - size * 0.2), (int)(size - size * 0.2)),
            new Point((int)(size / 2.0), (int)(size * 0.65))
        };
        
        this.g = (Graphics2D) getGraphics();
        this.g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON); // élsimítás bekapcsolása
        
        setRotation(rotation);
    }
    
    /**
     * A nyíl iránya.
     * Ha nincs irány megadva, null.
     */
    public Double getRotation() {
        return rotation;
    }
    
    /**
     * Az irány megadása.
     * @param rotation 0 - 359 között értelmezhető az óramutató járásával megegyezően. Ha null, akkor nyíl helyett kör rajzolódik.
     * @return az objektum önmaga, hogy lehessen további metódusokat hívni rajta
     */
    public MapArrow setRotation(Double rotation) {
        this.rotation = rotation;
        if (rotation == null) { // nincs irány megadva, tehát kör alakzat jön létre
            double s = size * 0.3;
            double p = (size / 2.0) - (s / 2.0);
            shape = new Ellipse2D.Double(p, p, s, s);
        }
        else { // nyíl alakzat jön létre az irány alapján
            int s = (int) (size / 2.0);
            shape = new RotateablePolygon(points).rotate(rotation, new Point(s, s));
        }
        draw(); // újrarajzolás
        return this;
    }
    
    /**
     * Újrafesti az alakzatot.
     */
    private void draw() {
        // üresre törlés
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.CLEAR, 0.0f));
        g.fillRect(0, 0, size, size);
        
        // alakzat belsejének kirajzolása
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
        g.setColor(new Color(50, 170, 200));
        g.fill(shape);
        
        // alakzat keretének kirajzolása
        g.setColor(new Color(0, 0, 170));
        g.setStroke(new BasicStroke((int)(size / 20.0)));
        g.draw(shape);
    }
    
}