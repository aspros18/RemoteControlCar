package chrriis.dj.nativeswing.swtimpl.components;

public class MenuItemActionEvent<T extends JMenuItem> extends TrayActionEvent<T> {
   
   public MenuItemActionEvent(T component) {
       super(component);
   }
   
}
