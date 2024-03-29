// PollTablePane.java
package jmri.jmrix.rps.swing.polling;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ResourceBundle;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.TableCellEditor;
import jmri.jmrix.rps.Engine;
import jmri.jmrix.rps.PollingFile;
import jmri.util.table.ButtonEditor;
import jmri.util.table.ButtonRenderer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Pane for user management of RPS polling.
 *
 * @author	Bob Jacobsen Copyright (C) 2008
 * @version	$Revision$
 */
public class PollTablePane extends javax.swing.JPanel {

    /**
     *
     */
    private static final long serialVersionUID = -1187242999349776714L;

    static final ResourceBundle rb = ResourceBundle.getBundle("jmri.jmrix.rps.swing.polling.PollingBundle");

    PollDataModel pollModel = null;
    jmri.ModifiedFlag modifiedFlag;

    /**
     * Constructor method
     */
    public PollTablePane(jmri.ModifiedFlag flag) {
        super();

        this.modifiedFlag = flag;

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        pollModel = new PollDataModel(modifiedFlag);

        JTable pollTable = jmri.util.JTableUtil.sortableDataModel(pollModel);

        // install a button renderer & editor
        ButtonRenderer buttonRenderer = new ButtonRenderer();
        pollTable.setDefaultRenderer(JButton.class, buttonRenderer);
        TableCellEditor buttonEditor = new ButtonEditor(new JButton());
        pollTable.setDefaultEditor(JButton.class, buttonEditor);
        pollTable.setDefaultRenderer(JComboBox.class, new jmri.jmrit.symbolicprog.ValueRenderer());
        pollTable.setDefaultEditor(JComboBox.class, new jmri.jmrit.symbolicprog.ValueEditor());

        try {
            jmri.util.com.sun.TableSorter tmodel = ((jmri.util.com.sun.TableSorter) pollTable.getModel());
            tmodel.setSortingStatus(PollDataModel.ADDRCOL, jmri.util.com.sun.TableSorter.ASCENDING);
        } catch (ClassCastException e3) {
        }  // if not a sortable table model
        pollTable.setRowSelectionAllowed(false);
        pollTable.setPreferredScrollableViewportSize(new java.awt.Dimension(580, 80));

        JScrollPane scrollPane = new JScrollPane(pollTable);
        add(scrollPane);

        // status info on bottom
        JPanel p = new JPanel() {
            /**
             *
             */
            private static final long serialVersionUID = 2303477665465877882L;

            public Dimension getMaximumSize() {
                int height = getPreferredSize().height;
                int width = super.getMaximumSize().width;
                return new Dimension(width, height);
            }
        };
        p.setLayout(new FlowLayout());

        polling = new JCheckBox(rb.getString("LabelPoll"));
        polling.setSelected(Engine.instance().getPolling());
        p.add(polling);
        polling.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                modifiedFlag.setModifiedFlag(true);
                checkPolling();
            }
        });

        JPanel m = new JPanel();
        m.setLayout(new BoxLayout(m, BoxLayout.Y_AXIS));
        ButtonGroup g = new ButtonGroup();
        bscMode = new JRadioButton(rb.getString("LabelBscMode"));
        bscMode.setSelected(Engine.instance().getBscPollMode());
        m.add(bscMode);
        g.add(bscMode);
        bscMode.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                modifiedFlag.setModifiedFlag(true);
                checkMode();
            }
        });
        directMode = new JRadioButton(rb.getString("LabelDirectMode"));
        directMode.setSelected(Engine.instance().getDirectPollMode());
        m.add(directMode);
        g.add(directMode);
        directMode.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                modifiedFlag.setModifiedFlag(true);
                checkMode();
            }
        });
        throttleMode = new JRadioButton(rb.getString("LabelThrottleMode"));
        throttleMode.setSelected(Engine.instance().getThrottlePollMode());
        m.add(throttleMode);
        g.add(throttleMode);
        throttleMode.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                modifiedFlag.setModifiedFlag(true);
                checkMode();
            }
        });
        p.add(m);

        p.add(Box.createHorizontalGlue());
        p.add(new JLabel(rb.getString("LabelDelay")));
        delay = new JTextField(5);
        delay.setText("" + Engine.instance().getPollingInterval());
        p.add(delay);
        delay.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                modifiedFlag.setModifiedFlag(true);
                updateInterval();
            }
        });

        JButton b = new JButton(rb.getString("LabelSetDefault"));
        b.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                modifiedFlag.setModifiedFlag(true);
                setDefaults();
            }
        });
        p.add(b);

        add(p);
    }

    JTextField delay;
    JCheckBox polling;
    JRadioButton bscMode;
    JRadioButton directMode;
    JRadioButton throttleMode;

    /**
     * Save the default value file
     */
    void setDefaults() {
        try {
            File file = new File(PollingFile.defaultFilename());
            if (log.isInfoEnabled()) {
                log.info("located file " + file + " for store");
            }
            // handle the file
            Engine.instance().storePollConfig(file);
            modifiedFlag.setModifiedFlag(false);
        } catch (Exception e) {
            log.error("exception during storeDefault: " + e);
        }
    }

    /**
     * Start or stop the polling
     */
    void checkPolling() {
        Engine.instance().setPolling(polling.isSelected());
    }

    /**
     * Change the polling mode
     */
    void checkMode() {
        if (bscMode.isSelected()) {
            Engine.instance().setBscPollMode();
        } else if (throttleMode.isSelected()) {
            Engine.instance().setThrottlePollMode();
        } else {
            Engine.instance().setDirectPollMode();
        }
    }

    /**
     * The requested interval has changed, update it
     */
    void updateInterval() {
        int interval = Integer.parseInt(delay.getText());
        log.debug("set interval to " + interval);
        Engine.instance().setPollingInterval(interval);
    }

    public void dispose() {
        pollModel.dispose();
    }

    private final static Logger log = LoggerFactory.getLogger(PollTablePane.class.getName());

}
