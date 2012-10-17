package org.dyndns.fzoli.rccar.test;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import javax.swing.JComponent;
import javax.swing.JFrame;

class Arrow extends JComponent {

    public Arrow() {
        setPreferredSize(new Dimension(250, 250));
    }
    
    private Polygon createPolygon(int size) {
        final int s2 = size / 2, s10 = size / 20, s20 = size / 40;
        final int[] xpoints = {0  , s10      , s10      , s2 - s20  , s2 - s20 , s2 - s10 , s2 , s2 + s10 , s2 + s20 , s2 + s20 , size - s10 , size - s10 , size , size - s10 , size - s10 , s2 + s20 , s2 + s20   , s2 + s10   , s2   , s2 - s10   , s2 - s20   , s2 - s20 , s10      , s10},
                    ypoints = {s2 , s2 - s10 , s2 - s20 , s2 - s20  , s10      , s10      , 0  , s10      , s10      , s2 - s20 , s2 - s20   , s2 - s10   , s2   , s2 + s10   , s2 + s20   , s2 + s20 , size - s10 , size - s10 , size , size - s10 , size - s10 , s2 + s20 , s2 + s20 , s2 + s10};
        return new Polygon(xpoints, ypoints, xpoints.length);
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        ((Graphics2D) g).draw(createPolygon(Math.min(getWidth(), getHeight()) - 1));
    }
    
}

public class ArrowTest {
    
    public static void main(String[] args) {
        new JFrame() {
            {
                setTitle("Nyilacska teszt");
                setDefaultCloseOperation(EXIT_ON_CLOSE);
                add(new Arrow());
                pack();
                setLocationRelativeTo(this);
                setVisible(true);
            }
        };
    }
    
}
