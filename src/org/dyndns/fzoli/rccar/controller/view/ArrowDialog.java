package org.dyndns.fzoli.rccar.controller.view;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.GridBagLayout;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.Window;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import org.dyndns.fzoli.rccar.controller.ControllerWindows;
import static org.dyndns.fzoli.rccar.controller.ControllerWindows.IC_ARROWS;
import org.dyndns.fzoli.rccar.controller.ControllerWindows.WindowType;
import org.dyndns.fzoli.ui.RepeatingReleasedEventsFixer;

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
        g.dispose();
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
        g.dispose();
    }
    
    public int getPercentX() {
        return createPercent(x);
    }

    public int getPercentY() {
        return createPercent(y);
    }
    
    private void setX(int x) {
        this.x = x;
        paint();
    }

    private void setY(int y) {
        this.y = y;
        paint();
    }
    
    public void setPercentX(int x) {
        setX(fromPercent(x));
    }
    
    public void setPercentY(int y) {
        setY(fromPercent(y));
    }
    
    public void setRelativeX(int x) {
        int s = x > getWidth() / 2 ? getWidth() / 20 - 1 : 1;
        x = x + (-1 * getMax() - s);
        if (!(x <= 0 ^ s != 1)) x = 0;
        setX(x);
    }
    
    public void setRelativeY(int y) {
        int s = y > getWidth() / 2 ? getWidth() / 20 - 1 : -1;
        y = getMax() - y + s;
        if (!(y <= 0 ^ s == -1)) y = 0;
        setY(y);
    }
    
    private int getMax() {
        return getWidth() / 2 - getWidth() / 40;
    }
    
    private int fromPercent(int i) {
        return (int)Math.round((getMax() + (i < 0 ? 1 : 0)) * (i / 100.0));
    }
    
    private int createPercent(int i) {
        int s = 100 * i / getMax();
        if (s > 100) s = 100;
        if (s < -100) s = -100;
        return s;
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
        if (x > 0) return new Rectangle(p[0] + 1     , p[1] , x      , p[2]); // jobb
        if (x < 0) return new Rectangle(p[3] + x - 1 , p[1] , -1 * x , p[2]); // bal
        return getDefaultRectangle(); // semerre
    }
    
    private Rectangle getRectangleY() {
        int[] p = getPoints();
        if (y < 0) return new Rectangle(p[1] , p[0] + 1     , p[2] , -1 * y); // le
        if (y > 0) return new Rectangle(p[1] , p[3] - y - 2 , p[2] , y + 1 ); // fel
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

abstract class ArrowPanel extends JPanel {

    private final ArrowLine al;
    
    private int tmpX = 0, tmpY = 0;
    private Integer tmpMX, tmpMY;
    private boolean btLeft = false;
    private Integer codeX, codeY;
    
    private void refresh(Integer x, Integer y) {
        if ((x != null && y != null && btLeft) || (x == null && y == null && !btLeft)) {
            if (codeX == null) {
                if (x != null) al.setRelativeX(x);
                else al.setPercentX(0);
            }
            if (codeY == null) {
                if (y != null) al.setRelativeY(y);
                else al.setPercentY(0);
            }
            repaint();
            fireChange();
        }
        tmpMX = x;
        tmpMY = y;
    }
    
    public ArrowPanel(int size) {
        super(new GridBagLayout());
        setBackground(Color.WHITE);
        setFocusable(true);

        JLayeredPane pane = new JLayeredPane();
        pane.setPreferredSize(new Dimension(size, size));
        pane.setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));

        JLabel lbBg = new JLabel(new ImageIcon(new Arrow(size)));
        pane.add(lbBg, JLayeredPane.POPUP_LAYER);
        lbBg.setBounds(0, 0, size, size);

        al = new ArrowLine(size);
        JLabel lbLn = new JLabel(new ImageIcon(al));
        pane.add(lbLn, JLayeredPane.DEFAULT_LAYER);
        lbLn.setBounds(0, 0, size, size);

        add(pane);

        pane.addMouseMotionListener(new MouseAdapter() {

            @Override
            public void mouseDragged(MouseEvent e) {
                refresh(e.getX(), e.getY());
            }

        });

        pane.addMouseListener(new MouseAdapter() {

            @Override
            public void mousePressed(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1) btLeft = true;
                refresh(e.getX(), e.getY());
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1) btLeft = false;
                refresh(null, null);
            }

        });

        addKeyListener(new KeyAdapter() {

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
                fireChange();
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
                fireChange();
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
                if (codeX != null && codeX.equals(e.getKeyCode())) {
                    if (tmpMX != null) al.setRelativeX(tmpMX);
                    else al.setPercentX(0);
                    codeX = null;
                }
            }

            private void resetY(KeyEvent e) {
                if (codeY != null && codeY.equals(e.getKeyCode())) {
                    if (tmpMY != null) al.setRelativeY(tmpMY);
                    else al.setPercentY(0);
                    codeY = null;
                }
            }
            
        });
    }
    
    public int getPercentX() {
        return al.getPercentX();
    }
    
    public int getPercentY() {
        return al.getPercentY();
    }
    
    public void setPercentX(int x) {
        al.setPercentX(x);
    }
    
    public void setPercentY(int y) {
        al.setPercentY(y);
    }
    
    private void fireChange() {
        int x = getPercentX();
        int y = getPercentY();
        if (tmpX != x || tmpY != y) {
            tmpX = x;
            tmpY = y;
            onChange(x, y);
        }
    }
    
    protected abstract void onChange(int x, int y);
    
}

public class ArrowDialog extends AbstractDialog {

    static {
        RepeatingReleasedEventsFixer.install(); // Linux bill. eseményjelzés javítása
    }
    
    public ArrowDialog(Window owner, ControllerWindows windows) {
        super(owner, "Vezérlő", windows);
        setIconImage(IC_ARROWS.getImage());
        setResizable(false);
        
        add(new ArrowPanel(200) {

            @Override
            protected void onChange(int x, int y) {
                System.out.println(x + " ; " + y);
            }

        });
        
        pack();
    }

    @Override
    public WindowType getWindowType() {
        return WindowType.CONTROLL;
    }
    
    public static void main(String[] args) {
        new ArrowDialog(null, null).setVisible(true);
    }
    
}
