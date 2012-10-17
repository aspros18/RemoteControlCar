package org.dyndns.fzoli.rccar.test;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;


abstract class ArrowComponent extends BufferedImage {

    public ArrowComponent(int size) {
        super(size, size, TYPE_INT_ARGB);
        paint();
    }
    
    protected abstract void paint();
    
    protected Polygon createPolygon() {
        final int size = Math.min(getWidth(), getHeight()) - 1;
        final int s2 = size / 2, s10 = size / 20, s20 = size / 40;
        final int[] xpoints = {0  , s10      , s10      , s2 - s20  , s2 - s20 , s2 - s10 , s2 , s2 + s10 , s2 + s20 , s2 + s20 , size - s10 , size - s10 , size , size - s10 , size - s10 , s2 + s20 , s2 + s20   , s2 + s10   , s2   , s2 - s10   , s2 - s20   , s2 - s20 , s10      , s10},
                    ypoints = {s2 , s2 - s10 , s2 - s20 , s2 - s20  , s10      , s10      , 0  , s10      , s10      , s2 - s20 , s2 - s20   , s2 - s10   , s2   , s2 + s10   , s2 + s20   , s2 + s20 , size - s10 , size - s10 , size , size - s10 , size - s10 , s2 + s20 , s2 + s20 , s2 + s10};
        return new Polygon(xpoints, ypoints, xpoints.length);
    }
    
}

class Arrow extends ArrowComponent {

    public Arrow(int size) {
        super(size);
    }
    
    @Override
    protected void paint() {
        Graphics2D g = (Graphics2D) getGraphics();
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, getWidth(), getHeight());
        Polygon arrow = createPolygon();
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.CLEAR, 0.0f));
        g.fill(arrow);
        g.setPaintMode();
        g.setColor(Color.BLACK);
        g.draw(arrow);
    }
    
}





class ArrowLine extends ArrowComponent {
    
    private int x = 0, y = 0;
    
    public ArrowLine(int size) {
        super(size);
    }

    public void setX(int x) {
        this.x = x;
        paint();
    }

    public void setY(int y) {
        this.y = y;
        paint();
    }
    
    private Rectangle getDefaultRectangle() {
        int s2 = getWidth() / 2;
        int s20 = getWidth() / 20;
        int s40 = getWidth() / 40;
        return new Rectangle(s2 - s40, s2 - s40, s20 - 1, s20 - 1);
    }
    
    private Rectangle getRectangleX() {
        int s2 = getWidth() / 2;
        int s10 = getWidth() / 10;
        int s20 = getWidth() / 20;
        int s40 = getWidth() / 40;
        int a = s2 + s40 - 2, b = s2 - s20, c = s10 - 1, d = s2 - s40 + 1;
        if (x > 0) return new Rectangle(a, b, d, c); // jobb
        if (x < 0) return new Rectangle(0, b, d, c); // bal
        return getDefaultRectangle(); // semerre
    }
    
    private Rectangle getRectangleY() {
        int s2 = getWidth() / 2;
        int s10 = getWidth() / 10;
        int s20 = getWidth() / 20;
        int s40 = getWidth() / 40;
        int a = s2 + s40 - 2, b = s2 - s20, c = s10 - 1, d = s2 - s40 + 1;
        if (y < 0) return new Rectangle(b, a, c, d); // le
        if (y > 0) return new Rectangle(b, 0, c, d); // fel
        return getDefaultRectangle(); // semerre
    }
    
    @Override
    protected void paint() {
        Graphics2D g = (Graphics2D) getGraphics();
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, getWidth(), getHeight());
        g.setColor(Color.GREEN);
        fill(g, getDefaultRectangle());
        fill(g, getRectangleX());
        fill(g, getRectangleY());
    }
    
    private void fill(Graphics2D g, Rectangle r) {
        g.fillRect(r.x, r.y, r.width, r.height);
    }
    
}

public class ArrowTest {
    
    public static void main(String[] args) {
        final int size = 400;
        new JFrame() {
            {
                setTitle("Nyilacska teszt");
                setDefaultCloseOperation(EXIT_ON_CLOSE);
                JLayeredPane pane = new JLayeredPane();
                pane.setPreferredSize(new Dimension(size, size));
                
                JLabel lbBg = new JLabel(new ImageIcon(new Arrow(size)));
                pane.add(lbBg, JLayeredPane.POPUP_LAYER);
                lbBg.setBounds(0, 0, size, size);
                
                final ArrowLine al = new ArrowLine(size);
                JLabel lbLn = new JLabel(new ImageIcon(al));
                pane.add(lbLn, JLayeredPane.DEFAULT_LAYER);
                lbLn.setBounds(0, 0, size, size);
                
                add(pane);
                
                addKeyListener(new KeyAdapter() {

                    @Override
                    public void keyPressed(KeyEvent e) {
                        switch (e.getKeyCode()) {
                            case KeyEvent.VK_LEFT:
                                al.setX(-1);
                                break;
                            case KeyEvent.VK_RIGHT:
                                al.setX(1);
                                break;
                            case KeyEvent.VK_UP:
                                al.setY(1);
                                break;
                            case KeyEvent.VK_DOWN:
                                al.setY(-1);
                        }
                        repaint();
                    }

                    @Override
                    public void keyReleased(KeyEvent e) {
                        switch (e.getKeyCode()) {
                            case KeyEvent.VK_LEFT:
                                al.setX(0);
                                break;
                            case KeyEvent.VK_RIGHT:
                                al.setX(0);
                                break;
                            case KeyEvent.VK_UP:
                                al.setY(0);
                                break;
                            case KeyEvent.VK_DOWN:
                                al.setY(0);
                        }
                        repaint();
                    }
                    
                });
                
                pack();
                setResizable(false);
                setLocationRelativeTo(this);
                setVisible(true);
            }
        };
    }
    
}
