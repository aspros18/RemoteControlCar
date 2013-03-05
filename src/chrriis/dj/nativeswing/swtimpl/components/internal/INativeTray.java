package chrriis.dj.nativeswing.swtimpl.components.internal;

public interface INativeTray {

    public int createTrayItem(byte[] imageData, String tooltip);

    public void setTooltip(int key, String text);
    
    public void setImage(int key, byte[] imageData);
    
    public void setVisible(int key, boolean visible);
    
    public void dispose();
    
}
