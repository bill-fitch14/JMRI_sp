// WindowMenu.java
package jmri.util;

import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JSeparator;
import javax.swing.event.MenuEvent;
import jmri.util.swing.WindowInterface;

/**
 * Creates a menu showing all open windows and allows to bring one in front
 * <P>
 * @author	Giorgio Terdina Copyright 2008
 * @version $Revision$ 18-Nov-2008 GT Replaced blank menu lines, due to
 * untitled windows, with "Untitled" string
 */
public class WindowMenu extends JMenu implements javax.swing.event.MenuListener {

    /**
     *
     */
    private static final long serialVersionUID = 6064948065992866417L;
    private Frame parentFrame;	// Keep note of the window containing the menu
    private List<JmriJFrame> framesList;	// Keep the list of windows, in order to find out which window was selected

    public WindowMenu(WindowInterface wi) {
        super(Bundle.getMessage("MenuWindow"));
        parentFrame = wi.getFrame();
        addMenuListener(this);
    }

    public void menuSelected(MenuEvent e) {
        String windowName;
        framesList = JmriJFrame.getFrameList();
        removeAll();

        add(new AbstractAction(Bundle.getMessage("MenuItemMinimize")) {
            /**
             *
             */
            private static final long serialVersionUID = -4679325334427280261L;

            public void actionPerformed(ActionEvent e) {
                // the next line works on Java 2, but not 1.1.8
                if (parentFrame != null) {
                    parentFrame.setState(Frame.ICONIFIED);
                }
            }
        });
        add(new JSeparator());

        int framesNumber = framesList.size();
        for (int i = 0; i < framesNumber; i++) {
            JmriJFrame iFrame = framesList.get(i);
            windowName = iFrame.getTitle();
            if (windowName.equals("")) {
                windowName = "Untitled";
            }
            JCheckBoxMenuItem newItem = new JCheckBoxMenuItem(new AbstractAction(windowName) {
                /**
                 *
                 */
                private static final long serialVersionUID = 5368670948429697065L;

                public void actionPerformed(ActionEvent e) {
                    JMenuItem selectedItem = (JMenuItem) e.getSource();
                    // Since different windows can have the same name, look for the position of the selected menu item
                    int itemCount = getItemCount();
                    // Skip possible other items at the top of the menu (e.g. "Minimize")
                    int firstItem = itemCount - framesList.size();
                    for (int i = firstItem; i < itemCount; i++) {
                        if (selectedItem == getItem(i)) {
                            i -= firstItem;
                            // Retrieve the corresponding window
                            if (i < framesList.size()) {	// "i" should always be < framesList.size(), but it's better to make sure
                                framesList.get(i).setVisible(true);
                                framesList.get(i).setExtendedState(Frame.NORMAL);
                                return;
                            }
                        }
                    }
                }
            });
            if (iFrame == parentFrame) {
                newItem.setState(true);
            }
            add(newItem);
        }
    }

    public void menuDeselected(MenuEvent e) {
    }

    public void menuCanceled(MenuEvent e) {
    }

}
