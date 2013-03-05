package chrriis.dj.nativeswing.swtimpl.components;

class TrayItemData {

    public final int KEY;

    public String tooltip;
    public byte[] imageData;
    public boolean visible;

    public TrayItemData(int key, String tooltip, byte[] imageData) {
        this.KEY = key;
        this.tooltip = tooltip;
        this.imageData = imageData;
        this.visible = imageData != null;
    }

}
