package org.dyndns.fzoli.rccar.test;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.GridBagLayout;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;

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

    private void fill(Graphics2D g, Rectangle r) {
        g.fillRect(r.x, r.y, r.width, r.height);
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

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
    
    public int getPercentX() {
        return createPercent(x);
    }

    public int getPercentY() {
        return createPercent(y);
    }
    
    public void setX(int x) {
        this.x = x;
        paint();
    }

    public void setY(int y) {
        this.y = y;
        paint();
    }
    
    public void setPercentX(int x) {
        setX(getPercent(x, x < 0));
    }
    
    public void setPercentY(int y) {
        setY(getPercent(y, y > 0));
    }
    
    public void setRelativeX(int x) {
        int s = x > getWidth() / 2 ? getWidth() / 20 - 1 : 1;
        x = x + (-1 * getMax(false) - s);
        if (x >= 0 && s == 1) x = 0;
        if (x <= 0 && s != 1) x = 0;
        setX(x);
    }
    
    public void setRelativeY(int y) {
        int s = y > getWidth() / 2 ? getWidth() / 20 - 1 : 0;
        y = getMax(false) - y + s;
        if (y <= 0 && s == 0) y = 0;
        if (y >= 0 && s != 0) y = 0;
        setY(y);
    }
    
    private int getMax(boolean dec) {
        return getWidth() / 2 - getWidth() / 40 - (dec ? 1 : 0);
    }
    
    private int getPercent(int i, boolean dec) {
        return (int)(getMax(dec) * (i / 100.0));
    }
    
    private int createPercent(int i) {
        return 100 * i / getMax(false);
    }
    
    private Rectangle getDefaultRectangle() {
        int s2 = getWidth() / 2;
        int s20 = getWidth() / 20;
        int s40 = getWidth() / 40;
        int a = s2 - s40, b = s20 - 1;
        return new Rectangle(a, a, b, b);
    }
    
    private Rectangle getRectangleX() {
        int[] p = getPoints();
        if (x > 0) return new Rectangle(p[0]         , p[1] , x + 1      , p[2]); // jobb
        if (x < 0) return new Rectangle(p[3] + x - 1 , p[1] , -1 * x + 1 , p[2]); // bal
        return getDefaultRectangle(); // semerre
    }
    
    private Rectangle getRectangleY() {
        int[] p = getPoints();
        if (y < 0) return new Rectangle(p[1] , p[0]         , p[2] , -1 * y + 1); // le
        if (y > 0) return new Rectangle(p[1] , p[3] - y - 1 , p[2] , y + 1     ); // fel
        return getDefaultRectangle(); // semerre
    }
    
    private int[] getPoints() {
        int[] points = new int[4];
        int s2 = getWidth() / 2;
        int s10 = getWidth() / 10;
        int s20 = getWidth() / 20;
        int s40 = getWidth() / 40;
        points[0] = s2 + s40 - 2;
        points[1] = s2 - s20;
        points[2]= s10 - 1;
        points[3] = s2 - s40 + 1;
        return points;
    }
    
}

public class ArrowTest {
    
    public static void main(String[] args) {
        final int size = 600;
        new JFrame() {
            {
                setTitle("Nyilacska teszt");
                setDefaultCloseOperation(EXIT_ON_CLOSE);
                
                JPanel p = new JPanel(new GridBagLayout());
                p.setBackground(Color.WHITE);
                
                JLayeredPane pane = new JLayeredPane();
                pane.setPreferredSize(new Dimension(size, size));
                pane.setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
                
                JLabel lbBg = new JLabel(new ImageIcon(new Arrow(size)));
                pane.add(lbBg, JLayeredPane.POPUP_LAYER);
                lbBg.setBounds(0, 0, size, size);
                
                final ArrowLine al = new ArrowLine(size);
                JLabel lbLn = new JLabel(new ImageIcon(al));
                pane.add(lbLn, JLayeredPane.DEFAULT_LAYER);
                lbLn.setBounds(0, 0, size, size);
                
                p.add(pane);
                add(p);
                
                pane.addMouseMotionListener(new MouseAdapter() {

                    @Override
                    public void mouseDragged(MouseEvent e) {
                        al.setRelativeX(e.getX());
                        al.setRelativeY(e.getY());
                        repaint();
                    }
                    
                });
                
                pane.addMouseListener(new MouseAdapter() {

                    @Override
                    public void mousePressed(MouseEvent e) {
                        al.setRelativeX(e.getX());
                        al.setRelativeY(e.getY());
                        repaint();
                    }

                    @Override
                    public void mouseReleased(MouseEvent e) {
                        al.setX(0);
                        al.setY(0);
                        repaint();
                    }
                    
                });
                
                addKeyListener(new KeyAdapter() {
                    
                    private Integer codeX, codeY;
                    
                    @Override
                    public void keyPressed(KeyEvent e) {
                        switch (e.getKeyCode()) {
                            case KeyEvent.VK_LEFT:
                                setX(e, true);
                                break;
                            case KeyEvent.VK_RIGHT:
                                setX(e, false);
                                break;
                            case KeyEvent.VK_UP:
                                setY(e, true);
                                break;
                            case KeyEvent.VK_DOWN:
                                setY(e, false);
                        }
                        repaint();
                    }

                    @Override
                    public void keyReleased(KeyEvent e) {
                        switch (e.getKeyCode()) {
                            case KeyEvent.VK_LEFT:
                                resetX(e);
                                break;
                            case KeyEvent.VK_RIGHT:
                                resetX(e);
                                break;
                            case KeyEvent.VK_UP:
                                resetY(e);
                                break;
                            case KeyEvent.VK_DOWN:
                                resetY(e);
                        }
                        repaint();
                    }
                    
                    private void setX(KeyEvent e, boolean left) {
                        codeX = e.getKeyCode();
                        al.setPercentX(left ? -100 : 100);
                    }
                    
                    private void setY(KeyEvent e, boolean up) {
                        codeY = e.getKeyCode();
                        al.setPercentY(up ? 100 : -100);
                    }
                    
                    private void resetX(KeyEvent e) {
                        if (codeX.equals(e.getKeyCode())) al.setX(0);
                    }
                    
                    private void resetY(KeyEvent e) {
                        if (codeY.equals(e.getKeyCode())) al.setY(0);
                    }
                    
                });
                
                pack();
                setLocationRelativeTo(this);
                setVisible(true);
            }
        };
    }
    
}
