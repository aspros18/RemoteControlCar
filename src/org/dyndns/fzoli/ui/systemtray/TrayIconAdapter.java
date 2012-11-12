package org.dyndns.fzoli.ui.systemtray;

import java.awt.Image;
import java.awt.TrayIcon.MessageType;
import java.awt.event.ActionListener;
import java.awt.event.MouseListener;

/**
 *
 * @author zoli
 */
public interface TrayIconAdapter {
    
    public void addActionListener(ActionListener listener);
    
    public void removeActionListener(ActionListener listener);
    
    public void addMouseListener(MouseListener listener);
    
    public void removeMouseListener(MouseListener listener);
    
    public ActionListener[] getActionListeners();
    
    public MouseListener[] getMouseListeners();
    
    public Image getImage();
    
    public void setImage(Image image);
    
    public String getToolTip();
    
    public void setToolTip(String toolTip);
    
    public PopupMenuAdapter getPopupMenu();
    
    public PopupMenuAdapter createPopupMenu();
    
    public void displayMessage(String title, String message, MessageType type);
    
}
