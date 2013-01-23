package org.dyndns.fzoli.rccar.controller.view;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.GridBagLayout;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.Timer;
import static org.dyndns.fzoli.rccar.controller.ControllerModels.getData;
import org.dyndns.fzoli.rccar.controller.ControllerWindows;
import static org.dyndns.fzoli.rccar.controller.ControllerWindows.IC_ARROWS;
import org.dyndns.fzoli.rccar.controller.ControllerWindows.WindowType;
import org.dyndns.fzoli.rccar.model.Control;
import org.dyndns.fzoli.ui.RepeatingReleasedEventsFixer;

/**
 * A vezérlőpanel rétege.
 */
abstract class ArrowComponent extends BufferedImage {

    public ArrowComponent(int size) {
        super(size, size, TYPE_INT_ARGB);
        paint();
    }
    
    /**
     * Kirajzolás.
     */
    protected abstract void paint();
    
    protected int getMax() {
        return getWidth() / 2 - getWidth() / 40;
    }
    
    protected int getRelativeX(int x) {
        int s = x > getWidth() / 2 ? getWidth() / 20 - 1 : 1;
        x = x + (-1 * getMax() - s);
        if (!(x <= 0 ^ s != 1)) x = 0;
        return x;
    }
    
    protected int getRelativeY(int y) {
        int s = y > getWidth() / 2 ? getWidth() / 20 - 1 : -1;
        y = getMax() - y + s;
        if (!(y <= 0 ^ s == -1)) y = 0;
        return y;
    }
    
    /**
     * Polygon elkészítése.
     */
    protected Polygon createPolygon() {
        final int size = Math.min(getWidth(), getHeight()) - 1;
        final int s2 = size / 2, s10 = size / 20, s20 = size / 40;
        final int[] xpoints = {0  , s10      , s10      , s2 - s20  , s2 - s20 , s2 - s10 , s2 , s2 + s10 , s2 + s20 , s2 + s20 , size - s10 , size - s10 , size , size - s10 , size - s10 , s2 + s20 , s2 + s20   , s2 + s10   , s2   , s2 - s10   , s2 - s20   , s2 - s20 , s10      , s10},
                    ypoints = {s2 , s2 - s10 , s2 - s20 , s2 - s20  , s10      , s10      , 0  , s10      , s10      , s2 - s20 , s2 - s20   , s2 - s10   , s2   , s2 + s10   , s2 + s20   , s2 + s20 , size - s10 , size - s10 , size , size - s10 , size - s10 , s2 + s20 , s2 + s20 , s2 + s10};
        return new Polygon(xpoints, ypoints, xpoints.length);
    }
    
}

/**
 * A felső rétegben megjelenő nyíl.
 */
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

/**
 * A középső rétegben megjelenő sebességkorlátot jelző vonal.
 */
class ArrowLimit extends ArrowComponent {

    private Integer maxY = null;
    private boolean fullX = false, fullY = false;
    
    public ArrowLimit(int size) {
        super(size);
    }

    @Override
    protected void paint() {
        Graphics2D g = (Graphics2D) getGraphics();
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.CLEAR, 0.0f));
        g.fillRect(0, 0, getWidth(), getHeight());
        if (!fullY && maxY != null) {
            g.setColor(Color.RED);
            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
            int start = getWidth() / 2 - getWidth() / 20;
            int stop = getWidth() / 10;
            g.fillRect(start, getRelativeMaxY(true), stop, 2);
            g.fillRect(start, getRelativeMaxY(false), stop, 2);
        }
    }

    boolean isFullX() {
        return fullX;
    }

    boolean isFullY() {
        return fullY;
    }
    
    Integer getRelativeMaxY(boolean up) {
        return getRelativeMaxY(maxY, up);
    }
    
    Integer getRelativeMaxY(int y, boolean up) {
        if (y > maxY) y = maxY;
        return getRelativeY(y, up);
    }
    
    Integer getRelativeY(int y, boolean up) {
        return getRelativeY((up ? 1 : -1) * y) + (up ? 0 : (getWidth() / 20));
    }
    
    Integer getMaxY() {
        return maxY;
    }
    
    void setMaxY(Integer maxY) {
        this.maxY = maxY;
        paint();
    }
    
}

/**
 * Az alsó rétegben megjelenő, nyilat kitöltő vonal.
 */
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
    
    void setXCo(int x) {
        this.x = x;
        paint();
    }

    void setYCo(int y) {
        this.y = y;
        paint();
    }
    
    public void setPercentX(int x) {
        setXCo(fromPercent(x));
    }
    
    public void setPercentY(int y) {
        setYCo(fromPercent(y));
    }
    
    public void setRelativeX(int x) {
        setXCo(getRelativeX(x));
    }
    
    public void setRelativeY(int y) {
        setYCo(getRelativeY(y));
    }
    
    int fromPercent(int i) {
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


/**
 * A nyíl rétegeit egyesítő panel.
 */
abstract class ArrowPanel extends JPanel {

    private final ArrowLimit aLim;
    private final ArrowLine aLin;
    
    private boolean controlling = true, increase = false;
    
    private int tmpX = 0, tmpY = 0;
    private boolean btLeft = false, btRight = false, fixLimit = false;
    private Integer tmpMX, tmpMY;
    private Integer codeX, codeY;
    
    private final MouseAdapter LISTENER_MOUSE = new MouseAdapter() {

        @Override
        public void mousePressed(MouseEvent e) {
            if (e.getButton() == MouseEvent.BUTTON1) btLeft = true;
            if (e.getButton() == MouseEvent.BUTTON3) btRight = true;
            refresh(e.getX(), e.getY());
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            removeRestore();
            if (e.getButton() == MouseEvent.BUTTON1) btLeft = false;
            if (e.getButton() == MouseEvent.BUTTON3) btRight = false;
            refresh(null, null);
        }

        @Override
        public void mouseDragged(MouseEvent e) {
            refresh(e.getX(), e.getY());
        }
        
        private void refresh(Integer x, Integer y) {
            if (!controlling) return;
            if (btRight && y != null && !fixLimit) {
                setMaxY(y);
            }
            if ((x != null && y != null && btLeft) || (x == null && y == null && !btLeft)) {
                if (codeX == null) {
                    if (x != null) {
                        if (aLim.isFullX()) {
                            int rx = aLin.getRelativeX(x);
                            aLin.setPercentX(rx > 0 ? 100 : rx == 0 ? 0 : -100);
                        }
                        else {
                            aLin.setRelativeX(x);
                        }
                    }
                    else {
                        aLin.setPercentX(0);
                    }
                }
                if (codeY == null) {
                    if (y != null) {
                        int ry = aLin.getRelativeY(y);
                        if (aLim.isFullY()) {
                            aLin.setPercentY(ry > 0 ? 100 : ry == 0 ? 0 : -100);
                        }
                        else {
                            int cy = ry;
                            if (aLim.getMaxY() != null) cy = ry >= 0 ? ry > aLim.getMaxY() ? aLim.getMaxY() : ry : ry < -1 * aLim.getMaxY() ? -1 * aLim.getMaxY() : ry;
                            aLin.setYCo(cy);
                        }
                    }
                    else {
                        aLin.setPercentY(0);
                    }
                }
                repaint();
                fireChange();
                tmpMX = x;
                tmpMY = y;
            }
        }
        
    };
    
    private Timer timerIncrease;
    
    final KeyListener LISTENER_KEY = new KeyAdapter() {

        @Override
        public void keyPressed(KeyEvent e) {
            if (!controlling) return;
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
                    break;
                case KeyEvent.VK_A:
                    setX(e, true);
                    break;
                case KeyEvent.VK_D:
                    setX(e, false);
                    break;
                case KeyEvent.VK_W:
                    setY(e, true);
                    break;
                case KeyEvent.VK_S:
                    setY(e, false);
            }
        }

        @Override
        public void keyReleased(KeyEvent e) {
            removeRestore();
            if (!controlling) return;
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
                    break;
                case KeyEvent.VK_A:
                    resetX(e);
                    break;
                case KeyEvent.VK_D:
                    resetX(e);
                    break;
                case KeyEvent.VK_W:
                    resetY(e);
                    break;
                case KeyEvent.VK_S:
                    resetY(e);
            }
        }
        
        private void setX(KeyEvent e, boolean left) {
            codeX = e.getKeyCode();
            aLin.setPercentX(left ? -100 : 100);
            apply();
        }
        
        private void setY(KeyEvent e, final boolean up) {
            codeY = e.getKeyCode();
            if (!increase) {
                if (isMaxYLimit()) aLin.setRelativeY(aLim.getRelativeMaxY(up));
                else aLin.setPercentY(up ? 100 : -100);
                apply();
            }
            else if (timerIncrease == null) {
                fixLimit = true;
                timerIncrease = new Timer(0, new ActionListener() {

                    private int i = 0;

                    private final int max = 110;
                    
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        i += 5;
                        if (i >= max) timerIncrease.stop();
                        if (isMaxYLimit()) aLin.setRelativeY(aLim.getRelativeMaxY(i, up));
                        else aLin.setRelativeY(aLim.getRelativeY(i, up));
                        apply();
                    }

                });
                timerIncrease.setDelay(150);
                timerIncrease.start();
            }
        }

        private void resetX(KeyEvent e) {
            if (codeX != null && codeX.equals(e.getKeyCode())) {
                if (tmpMX != null) aLin.setRelativeX(tmpMX);
                else aLin.setPercentX(0);
                codeX = null;
            }
            apply();
        }

        private void resetY(KeyEvent e) {
            stopIncrease();
            if (codeY != null && codeY.equals(e.getKeyCode())) {
                if (tmpMY != null) aLin.setRelativeY(tmpMY);
                else aLin.setPercentY(0);
                codeY = null;
            }
            apply();
        }

        private boolean isMaxYLimit() {
            return !aLim.isFullY() && aLim.getMaxY() != null;
        }
        
        private void apply() {
            repaint();
            fireChange();
        }
        
    };
    
    public ArrowPanel(int size) {
        super(new GridBagLayout());
        setBackground(Color.WHITE);
        setFocusable(true);

        JLayeredPane pane = new JLayeredPane();
        pane.setPreferredSize(new Dimension(size, size));
        pane.setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
        
        JLabel lbBg = new JLabel(new ImageIcon(new Arrow(size)));
        pane.add(lbBg, JLayeredPane.DRAG_LAYER);
        lbBg.setBounds(0, 0, size, size);
        
        aLim = new ArrowLimit(size);
        JLabel lbLm = new JLabel(new ImageIcon(aLim));
        pane.add(lbLm, JLayeredPane.POPUP_LAYER);
        lbLm.setBounds(0, 0, size, size);
        
        aLin = new ArrowLine(size);
        JLabel lbLn = new JLabel(new ImageIcon(aLin));
        pane.add(lbLn, JLayeredPane.DEFAULT_LAYER);
        lbLn.setBounds(0, 0, size, size);

        add(pane);

        pane.addMouseMotionListener(LISTENER_MOUSE);
        pane.addMouseListener(LISTENER_MOUSE);
        addKeyListener(LISTENER_KEY);
    }

    public void setMaxY(int y) {
        int ry = Math.abs(aLin.getRelativeY(y));
        aLim.setMaxY(ry == 0 ? null : ry);
        repaint();
    }
    
    private void stopIncrease() {
        if (timerIncrease != null) {
            timerIncrease.stop();
            timerIncrease = null;
            fixLimit = false;
        }
    }
    
    private Integer oldX, oldY, oldLimit;
    
    public void setControlling(boolean controlling, boolean restoring) {
        this.controlling = controlling;
        if (controlling && restoring) {
            if (oldX != null) setPercentX(oldX);
            if (oldY != null) setPercentY(oldY);
            if (oldLimit != null) aLim.setMaxY(oldLimit);
            fireChange();
            oldX = oldY = oldLimit = null;
        }
        if (!controlling) {
            stopIncrease();
            if (restoring) {
                oldX = getPercentX();
                oldY = getPercentY();
                oldLimit = aLim.getMaxY();
            }
            setPercentX(0);
            setPercentY(0);
        }
    }

    private void removeRestore() {
        if (oldX != null || oldY != null || oldLimit != null) {
            setPercentX(0);
            setPercentY(0);
            oldX = oldY = oldLimit = null;
        }
    }
    
    public void setIncrease(boolean increase) {
        this.increase = increase;
        stopIncrease();
    }
    
    public int getPercentX() {
        return aLin.getPercentX();
    }
    
    public int getPercentY() {
        return aLin.getPercentY();
    }
    
    public void setPercentX(int x) {
        aLin.setPercentX(x);
    }
    
    public void setPercentY(int y) {
        aLin.setPercentY(y);
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

/**
 * Vezérlő ablak.
 * @author zoli
 */
public class ArrowDialog extends AbstractDialog {
    
    static {
        RepeatingReleasedEventsFixer.install(); // Linux bill. eseményjelzés javítása
    }
    
    private final ArrowPanel ARROW_PANEL = new ArrowPanel(200) {

        @Override
        protected void onChange(int x, int y) {
            System.out.println("Control x: " + x + " y: " + y);
            getData().getSender().setControl(new Control(x, y));
        }

    };
    
    private final ItemListener LISTENER_INCREASE = new ItemListener() {

        @Override
        public void itemStateChanged(ItemEvent e) {
            ARROW_PANEL.setIncrease(((JToggleButton) e.getItem()).isSelected());
        }
        
    };
    
    public ArrowDialog(ControllerFrame owner, ControllerWindows windows) {
        super(owner, "Vezérlő", windows);
        getData().setArrowDialog(this);
        
        // ikon beállítása és panel hozzáadása
        setIconImage(IC_ARROWS.getImage());
        add(ARROW_PANEL);
        
        // átméretezés tiltása, ablak méretének minimalizálása
        setResizable(false);
        pack();
        
        if (owner != null) {
            // a főablak is képes irányítani nyilakkal a járművet
            owner.addKeyListener(ARROW_PANEL.LISTENER_KEY);
            // folyamatos sebességadás átállításának figyelése
            owner.getIncreaseButton().addItemListener(LISTENER_INCREASE);
        }
    }
    
    public void setControlling(boolean b, boolean restoring) {
        ControllerFrame owner = getControllerFrame();
        ARROW_PANEL.setControlling(b, restoring);
        if (b) {
            if (owner != null) {
                owner.getIncreaseButton().setEnabled(true);
            }
        }
        else {
            ARROW_PANEL.setMaxY(0);
            if (owner != null) {
                owner.getIncreaseButton().setSelected(false);
                owner.getIncreaseButton().setEnabled(false);
            }
        }
    }
    
    /**
     * Az ablak típusa: vezérlő ablak.
     */
    @Override
    public WindowType getWindowType() {
        return WindowType.CONTROLL;
    }
    
    /**
     * Frissíti a felületet az adatmodel alapján.
     */
    public void refresh() {
        refreshControlling();
        refreshControl();
    }
    
    /**
     * Engedélyezi vagy tiltja a vezérlést az adatmodel alapján.
     * Ha a vezérlés azért szűnik meg, mert a jármű nem érhető el, amint elérhetővé válik, a vezérlőjel visszaáll.
     */
    public void refreshControlling() {
        boolean controlling = getData().isControlling() != null && getData().isControlling();
        setControlling(controlling && getData().isVehicleAvailable(), controlling);
    }
    
    /**
     * A vezérlőjel alapján frissíti a felületet.
     */
    public void refreshControl() {
        ARROW_PANEL.setPercentX(getData().getControl() == null ? 0 : getData().getControl().getX());
        ARROW_PANEL.setPercentY(getData().getControl() == null ? 0 : getData().getControl().getY());
    }
    
    /**
     * Teszt.
     */
    public static void main(String[] args) {
        new ArrowDialog(null, null).setVisible(true);
    }
    
}
