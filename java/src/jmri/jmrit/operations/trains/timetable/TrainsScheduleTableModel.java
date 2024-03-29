// TrainsScheduleTableModel.java
package jmri.jmrit.operations.trains.timetable;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;
import javax.swing.JTable;
import javax.swing.table.TableColumnModel;
import jmri.jmrit.beantable.EnablingCheckboxRenderer;
import jmri.jmrit.operations.setup.Control;
import jmri.jmrit.operations.trains.Train;
import jmri.jmrit.operations.trains.TrainManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Table Model for edit of train schedules (Timetable) used by operations
 *
 * @author Daniel Boudreau Copyright (C) 2010, 2012
 * @version $Revision$
 */
public class TrainsScheduleTableModel extends javax.swing.table.AbstractTableModel implements PropertyChangeListener {

    TrainManager trainManager = TrainManager.instance();
    TrainScheduleManager scheduleManager = TrainScheduleManager.instance();

    // Defines the columns
    private static final int IDCOLUMN = 0;
    private static final int NAMECOLUMN = IDCOLUMN + 1;
    private static final int DESCRIPTIONCOLUMN = NAMECOLUMN + 1;

    private static final int FIXEDCOLUMN = DESCRIPTIONCOLUMN + 1;

    public TrainsScheduleTableModel() {
        super();
        trainManager.addPropertyChangeListener(this);
        scheduleManager.addPropertyChangeListener(this);
        updateList();
        addPropertyChangeTrainSchedules();
    }

    public final int SORTBYNAME = 1;
    public final int SORTBYTIME = 2;
    public final int SORTBYDEPARTS = 3;
    public final int SORTBYTERMINATES = 4;
    public final int SORTBYROUTE = 5;
    public final int SORTBYID = 6;

    private int _sort = SORTBYTIME;

    public void setSort(int sort) {
        synchronized (this) {
            _sort = sort;
        }
        updateList();
        fireTableStructureChanged();
        initTable();
    }

    private synchronized void updateList() {
        // first, remove listeners from the individual objects
        removePropertyChangeTrains();

        if (_sort == SORTBYID) {
            sysList = trainManager.getTrainsByIdList();
        } else if (_sort == SORTBYNAME) {
            sysList = trainManager.getTrainsByNameList();
        } else if (_sort == SORTBYTIME) {
            sysList = trainManager.getTrainsByTimeList();
        } else if (_sort == SORTBYDEPARTS) {
            sysList = trainManager.getTrainsByDepartureList();
        } else if (_sort == SORTBYTERMINATES) {
            sysList = trainManager.getTrainsByTerminatesList();
        } else if (_sort == SORTBYROUTE) {
            sysList = trainManager.getTrainsByRouteList();
        }

        // and add listeners back in
        addPropertyChangeTrains();
    }

    public synchronized List<Train> getSelectedTrainList() {
        return sysList;
    }

    List<Train> sysList = null;
    JTable _table = null;
    TrainsScheduleTableFrame _frame = null;

    void initTable(JTable table, TrainsScheduleTableFrame frame) {
        _table = table;
        _frame = frame;
        initTable();
    }

    void initTable() {
        if (_table == null) {
            return;
        }
        // Install the button handlers
        TableColumnModel tcm = _table.getColumnModel();
        _table.setDefaultRenderer(Boolean.class, new EnablingCheckboxRenderer());

        if (!_frame.loadTableDetails(_table)) {
            // set column preferred widths, note that columns can be deleted
            int[] widths = trainManager.getTrainScheduleFrameTableColumnWidths();
            int numCol = widths.length;
            if (widths.length > getColumnCount()) {
                numCol = getColumnCount();
            }
            for (int i = 0; i < numCol; i++) {
                tcm.getColumn(i).setPreferredWidth(widths[i]);
            }
        }
        // have to shut off autoResizeMode to get horizontal scroll to work (JavaSwing p 541)
        _table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
    }

    public synchronized int getRowCount() {
        return sysList.size();
    }

    public int getFixedColumn() {
        return FIXEDCOLUMN;
    }

    public int getColumnCount() {
        return getFixedColumn() + scheduleManager.numEntries();
    }

    public String getColumnName(int col) {
        switch (col) {
            case IDCOLUMN:
                synchronized (this) {
                    if (_sort == SORTBYID) {
                        return Bundle.getMessage("Id");
                    }
                    return Bundle.getMessage("Time");
                }
            case NAMECOLUMN:
                return Bundle.getMessage("Name");
            case DESCRIPTIONCOLUMN:
                return Bundle.getMessage("Description");
        }
        TrainSchedule ts = getSchedule(col);
        if (ts != null) {
            return ts.getName();
        }
        return "unknown"; // NOI18N
    }

    public Class<?> getColumnClass(int col) {
        switch (col) {
            case IDCOLUMN:
                return String.class;
            case NAMECOLUMN:
                return String.class;
            case DESCRIPTIONCOLUMN:
                return String.class;
        }
        if (col >= getFixedColumn() && col < getColumnCount()) {
            return Boolean.class;
        }
        return null;
    }

    public boolean isCellEditable(int row, int col) {
        switch (col) {
            case IDCOLUMN:
            case NAMECOLUMN:
            case DESCRIPTIONCOLUMN:
                return false;
            default:
                return true;
        }
    }

    public synchronized Object getValueAt(int row, int col) {
        if (row >= sysList.size()) {
            return "ERROR row " + row; // NOI18N
        }
        Train train = sysList.get(row);
        if (train == null) {
            return "ERROR train unknown " + row; // NOI18N
        }
        switch (col) {
            case IDCOLUMN: {
                if (_sort == SORTBYID) {
                    return train.getId();
                }
                return train.getDepartureTime();
            }
            case NAMECOLUMN:
                return train.getIconName();
            case DESCRIPTIONCOLUMN:
                return train.getDescription();
        }
        TrainSchedule ts = getSchedule(col);
        if (ts != null) {
            return ts.containsTrainId(train.getId());
        }
        return "unknown " + col; // NOI18N
    }

    public synchronized void setValueAt(Object value, int row, int col) {
        TrainSchedule ts = getSchedule(col);
        if (ts != null) {
            Train train = sysList.get(row);
            if (train == null) {
                log.error("train not found");
                return;
            }
            if (((Boolean) value).booleanValue()) {
                ts.addTrainId(train.getId());
            } else {
                ts.removeTrainId(train.getId());
            }
        }
    }

    public void propertyChange(PropertyChangeEvent e) {
        if (Control.showProperty) {
            log.debug("Property change: ({}) old: ({}) new: ({})", e.getPropertyName(), e.getOldValue(), e
                    .getNewValue());
        }
        if (e.getPropertyName().equals(TrainManager.LISTLENGTH_CHANGED_PROPERTY)
                || e.getPropertyName().equals(TrainManager.TRAIN_ACTION_CHANGED_PROPERTY)) {
            updateList();
            fireTableDataChanged();
        } else if (e.getPropertyName().equals(TrainScheduleManager.LISTLENGTH_CHANGED_PROPERTY)
                || e.getPropertyName().equals(TrainSchedule.NAME_CHANGED_PROPERTY)) {
            // update property change
            removePropertyChangeTrainSchedules();
            addPropertyChangeTrainSchedules();
            fireTableStructureChanged();
            initTable();
        } else if (e.getPropertyName().equals(TrainSchedule.SCHEDULE_CHANGED_PROPERTY)) {
            fireTableDataChanged();
        } else if (e.getSource().getClass().equals(Train.class)) {
            Train train = (Train) e.getSource();
            synchronized (this) {
                int row = sysList.indexOf(train);
                if (Control.showProperty) {
                    log.debug("Update train table row: " + row + " name: " + train.getName());
                }
                if (row >= 0) {
                    fireTableRowsUpdated(row, row);
                }
            }
        }
    }

    public TrainSchedule getSchedule(int col) {
        if (col >= getFixedColumn() && col < getColumnCount()) {
            List<TrainSchedule> trainSchedules = scheduleManager.getSchedulesByIdList();
            TrainSchedule ts = trainSchedules.get(col - getFixedColumn());
            return ts;
        }
        return null;
    }

    private void removePropertyChangeTrainSchedules() {
        List<TrainSchedule> trainSchedules = scheduleManager.getSchedulesByIdList();
        for (TrainSchedule ts : trainSchedules) {
            ts.removePropertyChangeListener(this);
        }
    }

    private void addPropertyChangeTrainSchedules() {
        List<TrainSchedule> trainSchedules = scheduleManager.getSchedulesByIdList();
        for (TrainSchedule ts : trainSchedules) {
            ts.addPropertyChangeListener(this);
        }
    }

    private synchronized void removePropertyChangeTrains() {
        if (sysList != null) {
            for (Train train : sysList) {
                train.removePropertyChangeListener(this);
            }
        }
    }

    private synchronized void addPropertyChangeTrains() {
        if (sysList != null) {
            for (Train train : sysList) {
                train.addPropertyChangeListener(this);
            }
        }
    }

    public void dispose() {
        if (log.isDebugEnabled()) {
            log.debug("dispose");
        }
        trainManager.removePropertyChangeListener(this);
        scheduleManager.removePropertyChangeListener(this);
        removePropertyChangeTrains();
        removePropertyChangeTrainSchedules();
    }

    private final static Logger log = LoggerFactory.getLogger(TrainsScheduleTableModel.class.getName());
}
