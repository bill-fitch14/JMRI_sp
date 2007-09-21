// jmri.jmrit.display.LayoutEditor.java

package jmri.jmrit.display;

import jmri.InstanceManager;
import jmri.Sensor;
import jmri.Turnout;
import jmri.Memory;
import jmri.SignalHead;
import jmri.jmrit.catalog.NamedIcon;
import jmri.util.JmriJFrame;

import java.awt.*;

import java.awt.geom.*;
import java.awt.event.*;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

import java.util.ArrayList;

import java.util.ResourceBundle;

import java.text.MessageFormat;

/**
 * Provides a scrollable Layout Panel and editor toolbars (that can be
 *		hidden)
 * <P>
 * This module serves as a manager for the LayoutTurnout, Layout Block, 
 *		PositionablePoint, Track Segment, and LevelXing objects which are
 *      integral subparts of the LayoutEditor class.
 * <P> 
 * All created objects are put on specific levels depending on their
 *		type (higher levels are in front):
 * Note that higher numbers appear behind lower numbers.
 * <P>
 * The "contents" List keeps track of all text and icon label objects 
 *		added to the targetframe for later manipulation.  Other Lists
 *      keep track of drawn items.
 * <P>
 * Based in part on PanelEditor.java (Bob Jacobsen (c) 2002, 2003). In
 *		particular, text and icon label items are copied from Panel
 *		editor, as well as some of the control design.
 *
 * @author Dave Duchamp  Copyright: (c) 2004-2007
 * @version $Revision: 1.4 $
 */

public class LayoutEditor extends JmriJFrame {

	// Defined text resource
	static final ResourceBundle rb = ResourceBundle.getBundle("jmri.jmrit.display.LayoutEditorBundle");
    static final ResourceBundle rbx = ResourceBundle.getBundle("jmri.jmrit.display.DisplayBundle");

    // Defined constants
    final public static Integer BKG       = new Integer(1);  // must be lower than all others below
    final public static Integer ICONS     = new Integer(3);
    final public static Integer LABELS    = new Integer(5);  // also used for Memories
    final public static Integer SECURITY  = new Integer(6);
    final public static Integer TURNOUTS  = new Integer(7);
	final public static Integer POINTS    = new Integer(8);
    final public static Integer SIGNALS   = new Integer(9);
    final public static Integer SENSORS   = new Integer(10);
    final public static Integer CLOCK     = new Integer(10);
    // size of point boxes
	private static final double SIZE = 3.0;
	private static final double SIZE2 = 6.0;  // must be twice SIZE
	// connection types
	final public static  int NONE = 0;
	final public static  int POS_POINT = 1;
	final public static  int TURNOUT_A = 2;  // throat for RH, LH, and WYE turnouts
	final public static  int TURNOUT_B = 3;  // continuing route for RH or LH turnouts
	final public static  int TURNOUT_C = 4;  // diverging route for RH or LH turnouts
	final public static  int TURNOUT_D = 5;  // double-crossover only
	final public static  int LEVEL_XING_A = 6;  
	final public static  int LEVEL_XING_B = 7;
	final public static  int LEVEL_XING_C = 8;
	final public static  int LEVEL_XING_D = 9;
	final public static  int TRACK = 10;
	final public static  int TURNOUT_CENTER = 11; // non-connection points should be last
	final public static  int LEVEL_XING_CENTER = 12;
	// dashed line parameters
	private static int minNumDashes = 3;
	private static double maxDashLength = 10;
	
    // Operational instance variables - not saved to disk
    private jmri.TurnoutManager tm = null;
	private LayoutEditor thisPanel = null;
	private LayoutPane targetPanel = null;
	private JPanel topEditBar = null;
	private JPanel helpBar = null;
	private LayoutPositionableLabel backgroundImage = null;
        
    private ButtonGroup itemGroup = null;
    private JTextField blockIDField = new JTextField(8);
	private JTextField blockSensor = new JTextField(5);
    
    private JCheckBox turnoutRHBox = new JCheckBox(rb.getString("RightHandAbbreviation"));
    private JCheckBox turnoutLHBox = new JCheckBox(rb.getString("LeftHandAbbreviation"));
    private JCheckBox turnoutWYEBox = new JCheckBox(rb.getString("WYEAbbreviation"));
    private JCheckBox doubleXoverBox = new JCheckBox(rb.getString("DoubleCrossOver"));
	private JTextField rotationField = new JTextField(3);
    private JTextField nextTurnout = new JTextField(5);
    
    private JCheckBox levelXingBox = new JCheckBox(rb.getString("LevelCrossing"));
    private JCheckBox endBumperBox = new JCheckBox(rb.getString("EndBumper"));
    private JCheckBox anchorBox = new JCheckBox(rb.getString("Anchor"));
    private JCheckBox trackBox = new JCheckBox(rb.getString("TrackSegment"));

	private JCheckBox dashedLine = new JCheckBox(rb.getString("Dashed"));
	private JCheckBox mainlineTrack = new JCheckBox(rb.getString("MainlineBox"));

    private JCheckBox sensorBox = new JCheckBox(rb.getString("SensorIcon"));
    private JTextField nextSensor = new JTextField(5);
    private MultiIconEditor sensorIconEditor = null;
    private JFrame sensorFrame;
    
    private JCheckBox signalBox = new JCheckBox(rb.getString("SignalIcon"));
    private JTextField nextSignalHead = new JTextField(5);
    public MultiIconEditor signalIconEditor = null;
    public JFrame signalFrame;
    
    private JCheckBox textLabelBox = new JCheckBox(rb.getString("TextLabel"));
    private JTextField textLabel = new JTextField(8);
	
	private JCheckBox memoryBox = new JCheckBox(rb.getString("Memory"));
    private JTextField textMemory = new JTextField(8);
    
    private JCheckBox iconLabelBox = new JCheckBox(rb.getString("IconLabel"));
    private MultiIconEditor iconEditor = null;
    private JFrame iconFrame = null;

    private JCheckBox multiSensorBox = new JCheckBox(rb.getString("MultiSensor")+"...");
    private MultiSensorIconFrame multiSensorFrame = null;
    
    private JLabel xLabel = new JLabel("00");
    private JLabel yLabel = new JLabel("00");
    private int xLoc = 0;
    private int yLoc = 0;
	private Point2D currentPoint = new Point2D.Double(100.0,100.0);
    private int height = 100;
    private int width = 100;
    private int numTurnouts = 0;
	private TrackSegment newTrack = null;
	private boolean panelChanged = false;

	// selection variables
	private boolean selectionActive = false;
	private double selectionX = 0.0;
	private double selectionY = 0.0;
	private double selectionWidth = 0.0;
	private double selectionHeight = 0.0;
   
	// Option menu items 
    private JCheckBoxMenuItem editModeItem = null;
    private JCheckBoxMenuItem positionableItem = null;
    private JCheckBoxMenuItem controlItem = null;
    private JCheckBoxMenuItem showHelpItem = null;
    private ButtonGroup bkColorButtonGroup = null;
	private ButtonGroup trackColorButtonGroup = null;
	private Color[] trackColors = new Color[13];
	private JRadioButtonMenuItem[] trackColorMenuItems = new JRadioButtonMenuItem[13];
	private int trackColorCount = 0;
	
	// Selected point information
    private final static int TURNOUT = 1;      // possible object types
    private final static int LEVEL_XING = 2;
    private final static int POINT = 3;
	private Point2D startPoint = new Point2D.Double(0.0,0.0); // starting point
	private Point2D startLocation = new Point2D.Double(0.0,0.0); // starting location for undo
	private Object selectedObject = null; // selected object, null if nothing selected
	private Object prevSelectedObject = null; // previous selected object, for undo
	private int selectedPointType = 0;   // connection type within the selected object
	private boolean selectedNeedsConnect = false; // true if selected object is unconnected
	private Object foundObject = null; // found object, null if nothing found
	private Point2D foundLocation = new Point2D.Double(0.0,0.0);  // location of found object
	private int foundPointType = 0;   // connection type within the found object
	private boolean foundNeedsConnect = false; // true if found point needs a connection
	private Object beginObject = null; // begin track segment connection object, null if none
	private Point2D beginLocation = new Point2D.Double(0.0,0.0);  // location of begin object
	private int beginPointType = 0;   // connection type within begin connection object
	private Point2D currentLocation = new Point2D.Double(0.0,0.0); // current location
	
	// Lists of items that describe the Layout, and allow it to be drawn
	//		Each of the items must be saved to disk over sessions
    public ArrayList contents = new ArrayList();  // icons and labels
	public ArrayList turnoutList = new ArrayList();  // LayoutTurnouts
	public ArrayList trackList = new ArrayList();  // TrackSegment list
	public ArrayList pointList = new ArrayList();  // PositionablePoint list
	public ArrayList xingList = new ArrayList();  // LevelXing list
	// counts used to determine unique internal names
	private int numAnchors = 0;
	private int numEndBumpers = 0;
	private int numTrackSegments = 0;
	private int numLevelXings = 0;
	private int numLayoutTurnouts = 0;
	// Lists of items that facilitate tools
	public ArrayList signalList = new ArrayList();  // Signal Head Icons
        
    // persistent instance variables - saved to disk with Save Panel
	private int panelWidth = 0;
	private int panelHeight = 0;
	private int upperLeftX = 0;
	private int upperLeftY = 0;
    private static float mainlineTrackWidth = 4.0F;
    private static float sideTrackWidth = 2.0F;
	private Color defaultTrackColor = Color.black;
    private String layoutName = "";
	private double xScale = 1.0;
	private double yScale = 1.0;
	private boolean editMode = true;
    private boolean positionable = true;
    private boolean controlLayout = true;
	private boolean showHelpBar = true;
	
	// saved state of options when panel was loaded or created
	private boolean savedEditMode = true;
    private boolean savedPositionable = true;
    private boolean savedControlLayout = true;
	private boolean savedShowHelpBar = false;
    
    public LayoutEditor() { this("My Layout");}

    public LayoutEditor(String name) {
        super(name);
        layoutName = name;
        // set to full screen
        Dimension screenDim = Toolkit.getDefaultToolkit().getScreenSize();
        height = screenDim.height-120;
        width = screenDim.width-20;
        setSize(screenDim.width, screenDim.height);
        // initialize frame
        Container contentPane = getContentPane();
        contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.Y_AXIS));
        // initialize menu bar
        JMenuBar menuBar = new JMenuBar();
		// set up File menu
        JMenu fileMenu = new JMenu(rb.getString("MenuFile"));
        menuBar.add(fileMenu);
        fileMenu.add(new jmri.configurexml.StoreXmlUserAction(rbx.getString("MenuItemStore")));
        fileMenu.addSeparator();
        JMenuItem deleteItem = new JMenuItem(rbx.getString("DeletePanel"));
        fileMenu.add(deleteItem);
        deleteItem.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent event) {
					deletePanel();
                }
            });
        setJMenuBar(menuBar);
        // setup Options menu
		setupOptionMenu(menuBar);
		// setup Tools menu
		setupToolsMenu(menuBar);
		// setup Help menu
        addHelpMenu("package.jmri.jmrit.display.LayoutEditor", true);
		
        // setup group for radio buttons selecting items to add and line style
        itemGroup = new ButtonGroup();
        itemGroup.add(turnoutRHBox);
        itemGroup.add(turnoutLHBox);
        itemGroup.add(turnoutWYEBox);
        itemGroup.add(doubleXoverBox);
        itemGroup.add(levelXingBox);
        itemGroup.add(endBumperBox);
        itemGroup.add(anchorBox);
        itemGroup.add(trackBox);
		itemGroup.add(multiSensorBox);
        itemGroup.add(sensorBox);
        itemGroup.add(signalBox);
        itemGroup.add(textLabelBox);
        itemGroup.add(memoryBox);
        itemGroup.add(iconLabelBox);
		turnoutRHBox.setSelected(true);
		dashedLine.setSelected(false);
		mainlineTrack.setSelected(false);
        // setup top edit bar
        topEditBar = new JPanel();
        topEditBar.setLayout(new BoxLayout(topEditBar, BoxLayout.Y_AXIS));
		// add first row of edit tool bar items
        JPanel top1 = new JPanel();
        top1.add(new JLabel(rb.getString("Location")+" - x:"));
        top1.add(xLabel);
        top1.add(new JLabel(" y:"));
        top1.add(yLabel);
		// add turnout items
        top1.add (new JLabel("    "+rb.getString("Turnout")+": "));
        top1.add (new JLabel(rb.getString("Name")));
        top1.add (nextTurnout);
		nextTurnout.setToolTipText(rb.getString("TurnoutNameToolTip"));
		top1.add (new JLabel(rb.getString("Type")));
        top1.add (turnoutRHBox);
		turnoutRHBox.setToolTipText(rb.getString("RHToolTip"));
        top1.add (turnoutLHBox);
		turnoutLHBox.setToolTipText(rb.getString("LHToolTip"));
        top1.add (turnoutWYEBox);
		turnoutWYEBox.setToolTipText(rb.getString("WYEToolTip"));
        top1.add (doubleXoverBox);
		doubleXoverBox.setToolTipText(rb.getString("DoubleCrossOverToolTip"));
		top1.add (new JLabel("    "+rb.getString("Rotation")));
		top1.add (rotationField);
		rotationField.setToolTipText(rb.getString("RotationToolTip"));
        topEditBar.add(top1);
		// add second row of edit tool bar items
        JPanel top2 = new JPanel();
        top2.add(new JLabel(rb.getString("BlockID")));
        top2.add(blockIDField);
		blockIDField.setToolTipText(rb.getString("BlockIDToolTip"));
        top2.add(new JLabel(rb.getString("OccupancySensor")));
        top2.add(blockSensor);
		blockSensor.setText("");
		blockSensor.setToolTipText(rb.getString("OccupancySensorToolTip"));
		top2.add (new JLabel("  "+rb.getString("Track")+":  "));
        top2.add (levelXingBox);
		levelXingBox.setToolTipText(rb.getString("LevelCrossingToolTip"));
        top2.add (trackBox);
		trackBox.setToolTipText(rb.getString("TrackSegmentToolTip"));
		top2.add (dashedLine);
		dashedLine.setToolTipText(rb.getString("DashedCheckBoxTip"));
		top2.add (mainlineTrack);
		mainlineTrack.setToolTipText(rb.getString("MainlineCheckBoxTip"));
        topEditBar.add(top2);
		// add third row of edit tool bar items
        JPanel top3 = new JPanel();
        top3.add(new JLabel("  "+rb.getString("Nodes")+": "));
        top3.add (endBumperBox);
		endBumperBox.setToolTipText(rb.getString("EndBumperToolTip"));
        top3.add (anchorBox);
		anchorBox.setToolTipText(rb.getString("AnchorToolTip"));
        top3.add(new JLabel("   "+rb.getString("Labels")+": "));
		top3.add (textLabelBox);
		textLabelBox.setToolTipText(rb.getString("TextLabelToolTip"));
        top3.add (textLabel);
		textLabel.setToolTipText(rb.getString("TextToolTip"));		
		top3.add (memoryBox);
		memoryBox.setToolTipText(rb.getString("MemoryBoxToolTip"));
        top3.add (textMemory);
		textMemory.setToolTipText(rb.getString("MemoryToolTip"));		
        topEditBar.add(top3);
		// add fourth row of edit tool bar items
        JPanel top4 = new JPanel();
		// multi sensor 
		top4.add (multiSensorBox);
		multiSensorBox.setToolTipText(rb.getString("MultiSensorToolTip"));		
		// change icon
		top4.add (new JLabel("    "));
        JButton changeIcon = new JButton(rb.getString("ChangeIcons")+"...");
        changeIcon.addActionListener( new ActionListener() {
                public void actionPerformed(ActionEvent a) {
					if (sensorBox.isSelected())
						sensorFrame.show();
					else if (signalBox.isSelected())
						signalFrame.show();
					else if (iconLabelBox.isSelected())
						iconFrame.show();
                }
            } );
        top4.add(changeIcon);
		changeIcon.setToolTipText(rb.getString("ChangeIconToolTip"));
		// sensor icon
		top4.add (new JLabel("    "));
        top4.add (sensorBox);
		sensorBox.setToolTipText(rb.getString("SensorBoxToolTip"));
        top4.add (nextSensor);
		nextSensor.setToolTipText(rb.getString("SensorIconToolTip"));
        sensorIconEditor = new MultiIconEditor(4);
        sensorIconEditor.setIcon(0, "Active:","resources/icons/smallschematics/tracksegments/circuit-occupied.gif");
        sensorIconEditor.setIcon(1, "Inactive", "resources/icons/smallschematics/tracksegments/circuit-empty.gif");
        sensorIconEditor.setIcon(2, "Inconsistent:", "resources/icons/smallschematics/tracksegments/circuit-error.gif");
        sensorIconEditor.setIcon(3, "Unknown:","resources/icons/smallschematics/tracksegments/circuit-error.gif");
        sensorIconEditor.complete();
        sensorFrame = new JFrame(rb.getString("EditSensorIcons"));
		sensorFrame.getContentPane().add(new JLabel("  "+rb.getString("IconChangeInfo")+"  "),BorderLayout.NORTH);
        sensorFrame.getContentPane().add(sensorIconEditor);
        sensorFrame.pack();
		// signal icon
		top4.add (new JLabel("    "));
        top4.add (signalBox);
		signalBox.setToolTipText(rb.getString("SignalBoxToolTip"));
        top4.add (nextSignalHead);
		nextSignalHead.setToolTipText(rb.getString("SignalIconToolTip"));		
        signalIconEditor = new MultiIconEditor(8);
		signalIconEditor.setIcon(0, "Red:","resources/icons/smallschematics/searchlights/left-red-short.gif");
		signalIconEditor.setIcon(1, "Flash red:", "resources/icons/smallschematics/searchlights/left-flashred-short.gif");
		signalIconEditor.setIcon(2, "Yellow:", "resources/icons/smallschematics/searchlights/left-yellow-short.gif");
		signalIconEditor.setIcon(3, "Flash yellow:", "resources/icons/smallschematics/searchlights/left-flashyellow-short.gif");
		signalIconEditor.setIcon(4, "Green:","resources/icons/smallschematics/searchlights/left-green-short.gif");
		signalIconEditor.setIcon(5, "Flash green:","resources/icons/smallschematics/searchlights/left-flashgreen-short.gif");
		signalIconEditor.setIcon(6, "Dark:","resources/icons/smallschematics/searchlights/left-dark-short.gif");
		signalIconEditor.setIcon(7, "Held:","resources/icons/smallschematics/searchlights/left-held-short.gif");
        signalIconEditor.complete();
        signalFrame = new JFrame(rb.getString("EditSignalIcons"));
		signalFrame.getContentPane().add(new JLabel("  "+rb.getString("IconChangeInfo")+"  "),BorderLayout.NORTH);
        signalFrame.getContentPane().add(signalIconEditor);
        signalFrame.pack();
		// icon label
		top4.add (new JLabel("    "));
        top4.add (iconLabelBox);
		iconLabelBox.setToolTipText(rb.getString("IconLabelToolTip"));
        iconEditor = new MultiIconEditor(1);
        iconEditor.setIcon(0, "","resources/icons/smallschematics/tracksegments/block.gif");
        iconEditor.complete();
        iconFrame = new JFrame(rb.getString("EditIcon"));
        iconFrame.getContentPane().add(iconEditor);
        iconFrame.pack();

		topEditBar.add(top4);
        contentPane.add(topEditBar);
        topEditBar.setVisible(false);

        // setup main layout display
        targetPanel = new LayoutPane(){
            // provide size services, even though a null layout manager is used
            public void setSize(int h, int w) {
                this.h = h;
                this.w = w;
                super.setSize(h, w);
            }
            int h = 100;
            int w = 100;
            public Dimension getPreferredSize() {
                return new Dimension(h,w);
            }
            public Dimension getMinimumSize() {
                return getPreferredSize();
            }
            public Dimension getMaximumSize() {
                return getPreferredSize();
            }
        };
        targetPanel.setLayout(null);
        targetPanel.addMouseListener(new MouseAdapter() 
            {
                public void mousePressed(MouseEvent event) 
                {
                    handleMousePressed(event);
                }
                public void mouseReleased(MouseEvent event)
                {
                    handleMouseReleased(event);
                }
            });
        targetPanel.addMouseMotionListener (new MouseMotionAdapter()
            {   
                public void mouseDragged(MouseEvent event)
                {
                    handleMouseDragged(event);
                }
                public void mouseMoved(MouseEvent event)
                {
                    handleMouseMoved(event);
                }
            });

        JScrollPane js = new JScrollPane(targetPanel);
        js.setHorizontalScrollBarPolicy(js.HORIZONTAL_SCROLLBAR_ALWAYS);
        js.setVerticalScrollBarPolicy(js.VERTICAL_SCROLLBAR_ALWAYS);

        JPanel p1 = new JPanel();
        p1.setLayout(new BoxLayout(p1, BoxLayout.X_AXIS));
        p1.add(js);
        contentPane.add(p1);

        contentPane.setLayout(new BoxLayout(contentPane,BoxLayout.Y_AXIS));

        targetPanel.setSize(width, height);
        targetPanel.revalidate();
        
		// setup help bar
		helpBar = new JPanel();
        helpBar.setLayout(new BoxLayout(helpBar, BoxLayout.Y_AXIS));
        JPanel help1 = new JPanel();
		help1.add(new JLabel(rb.getString("Help1")));
		helpBar.add(help1);
        JPanel help2 = new JPanel();
		help2.add(new JLabel(rb.getString("Help2")));
		helpBar.add(help2);
        JPanel help3 = new JPanel();
		int system = jmri.util.SystemType.getType();
		if (system==jmri.util.SystemType.MACOSX) {
			help3.add(new JLabel(rb.getString("Help3Mac")));
		}
		else if (system==jmri.util.SystemType.WINDOWS) {
			help3.add(new JLabel(rb.getString("Help3Win")));
		}
		else {
			help3.add(new JLabel(rb.getString("Help3")));
		}
		helpBar.add(help3);		

        contentPane.add(helpBar);
        helpBar.setVisible(false);
		
        // register the resulting panel for later configuration
        InstanceManager.configureManagerInstance().registerUser(this);
		jmri.jmrit.display.PanelMenu.instance().addLayoutEditorPanel(this);
		thisPanel = this;
		resetDirty();

        // when this window closes, set contents of targetPanel uneditable
        addWindowListener(new java.awt.event.WindowAdapter() {
                public void windowClosing(java.awt.event.WindowEvent e) {
					// If the panel has been changed, prompt to save
					if (panelChanged || (savedEditMode!=editMode) ||
							(savedPositionable!=positionable) ||
							(savedControlLayout!=controlLayout) ||					
							(savedShowHelpBar!=showHelpBar) ) {
						// remind to save panel		
						javax.swing.JOptionPane.showMessageDialog(null,
								rb.getString("Reminder1")+" "+rb.getString("Reminder2")+
								"\n"+rb.getString("Reminder3"),
								rb.getString("ReminderTitle"),
								javax.swing.JOptionPane.INFORMATION_MESSAGE);
					}
                    setAllPositionable(false);
					jmri.jmrit.display.PanelMenu.instance().updateLayoutEditorPanel(thisPanel);
                }
            });
    } 
	
	LayoutEditorTools tools = null;
	void setupToolsMenu(JMenuBar menuBar) {
		JMenu toolsMenu = new JMenu(rb.getString("MenuTools"));
		menuBar.add(toolsMenu);
		// scale track diagram 
        JMenuItem scaleItem = new JMenuItem(rb.getString("ScaleTrackDiagram")+"...");
        toolsMenu.add(scaleItem);
        scaleItem.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent event) {
					// bring up scale track diagram dialog
					scaleTrackDiagram();
                }
            });
		// translate selection 
        JMenuItem moveItem = new JMenuItem(rb.getString("TranslateSelection")+"...");
        toolsMenu.add(moveItem);
        moveItem.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent event) {
					// bring up translate selection dialog
					moveSelection();
                }
            });
		// undo translate selection 
        JMenuItem undoMoveItem = new JMenuItem(rb.getString("UndoTranslateSelection"));
        toolsMenu.add(undoMoveItem);
        undoMoveItem.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent event) {
					// undo previous move selection 
					undoMoveSelection();
                }
            });
		// set signals at turnout
		JMenuItem turnoutItem = new JMenuItem(rb.getString("SignalsAtTurnout")+"...");
        toolsMenu.add(turnoutItem);
        turnoutItem.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent event) {
					if (tools == null) {
						tools = new LayoutEditorTools(thisPanel);
					}
					// bring up signals at turnout tool dialog
					tools.setSignalsAtTurnout(signalIconEditor,signalFrame);
                }
            });
		// set signals at block boundary
		JMenuItem boundaryItem = new JMenuItem(rb.getString("SignalsAtBoundary")+"...");
        toolsMenu.add(boundaryItem);
        boundaryItem.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent event) {
					if (tools == null) {
						tools = new LayoutEditorTools(thisPanel);
					}
					// bring up signals at block boundary tool dialog
					tools.setSignalsAtBlockBoundary(signalIconEditor,signalFrame);
                }
            });
		// set signals at crossover turnout
		JMenuItem xoverItem = new JMenuItem(rb.getString("SignalsAtXoverTurnout")+"...");
        toolsMenu.add(xoverItem);
        xoverItem.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent event) {
					if (tools == null) {
						tools = new LayoutEditorTools(thisPanel);
					}
					// bring up signals at double crossover tool dialog
					tools.setSignalsAtXoverTurnout(signalIconEditor,signalFrame);
                }
            });
		// set signals at level crossing
		JMenuItem xingItem = new JMenuItem(rb.getString("SignalsAtLevelXing")+"...");
        toolsMenu.add(xingItem);
        xingItem.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent event) {
					if (tools == null) {
						tools = new LayoutEditorTools(thisPanel);
					}
					// bring up signals at level crossing tool dialog
					tools.setSignalsAtLevelXing(signalIconEditor,signalFrame);
                }
            });
	}

	void setupOptionMenu(JMenuBar menuBar) {
        JMenu optionMenu = new JMenu(rb.getString("Options"));
        menuBar.add(optionMenu);
		// edit mode item
        editModeItem = new JCheckBoxMenuItem(rb.getString("EditMode"));
        optionMenu.add(editModeItem);
        editModeItem.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent event) {
                    editMode = editModeItem.isSelected();
					if (editMode) {
						helpBar.setVisible(showHelpBar);
					}
                    setEditMode(editMode);
                }
            });
        editModeItem.setSelected(editMode);
		// positionable item
        positionableItem = new JCheckBoxMenuItem(rb.getString("AllowRepositioning"));
        optionMenu.add(positionableItem);
        positionableItem.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent event) {
                    boolean mode = positionableItem.isSelected();
                    setAllPositionable(mode);
                }
            });                    
        positionableItem.setSelected(true);
		// controlable item
		controlItem = new JCheckBoxMenuItem(rb.getString("AllowLayoutControl"));
        optionMenu.add(controlItem);
        controlItem.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent event) {
                    boolean mode = controlItem.isSelected();
                    setAllControlling(mode);
                }
            });                    
        controlItem.setSelected(true);
		// show help item
		showHelpItem = new JCheckBoxMenuItem(rb.getString("ShowEditHelp"));
        optionMenu.add(showHelpItem);
        showHelpItem.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent event) {
                    showHelpBar = showHelpItem.isSelected();
					if (editMode) {
						helpBar.setVisible(showHelpBar);
					}
                }
            });                    
        showHelpItem.setSelected(showHelpBar);
		// title item
        optionMenu.addSeparator();
        JMenuItem titleItem = new JMenuItem(rb.getString("NewTitle")+"...");
        optionMenu.add(titleItem);
        titleItem.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent event) {
                    // prompt for name
                    String newName = JOptionPane.showInputDialog(targetPanel, 
											rb.getString("EnterTitle")+":");
                    if (newName==null) return;  // cancelled
                    setTitle(newName);
                    layoutName = newName;
					jmri.jmrit.display.PanelMenu.instance().renameLayoutEditorPanel(thisPanel);
					panelChanged = true;
                }
            });
		// add background image
        JMenuItem backgroundItem = new JMenuItem(rb.getString("AddBackground")+"...");
        optionMenu.add(backgroundItem);
        backgroundItem.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent event) {
					addBackground();
					panelChanged = true;
					repaint();
                }
            });
		// add fast clock
        JMenuItem clockItem = new JMenuItem(rb.getString("AddFastClock"));
        optionMenu.add(clockItem);
        clockItem.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent event) {
					addClock();
					panelChanged = true;
					repaint();
                }
            });
		// set location and size
        JMenuItem locationItem = new JMenuItem(rb.getString("SetLocation"));
        optionMenu.add(locationItem);
        locationItem.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent event) {
					setCurrentPositionAndSize();
					log.debug("Bounds:"+upperLeftX+", "+upperLeftY+", "+panelWidth+", "+panelHeight);
                }
            });
		// set track width 
        JMenuItem widthItem = new JMenuItem(rb.getString("SetTrackWidth")+"...");
        optionMenu.add(widthItem);
        widthItem.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent event) {
					// bring up enter track width dialog
					enterTrackWidth();
                }
            });
		// track color item
		JMenu trackColorMenu = new JMenu(rb.getString("DefaultTrackColor"));
		trackColorButtonGroup = new ButtonGroup();
		addTrackColorMenuEntry(trackColorMenu, rb.getString("Black"), Color.black);
		addTrackColorMenuEntry(trackColorMenu, rb.getString("DarkGray"),Color.darkGray);
		addTrackColorMenuEntry(trackColorMenu, rb.getString("Gray"),Color.gray);
		addTrackColorMenuEntry(trackColorMenu, rb.getString("LightGray"),Color.lightGray);
		addTrackColorMenuEntry(trackColorMenu, rb.getString("White"),Color.white);
		addTrackColorMenuEntry(trackColorMenu, rb.getString("Red"),Color.red);
		addTrackColorMenuEntry(trackColorMenu, rb.getString("Pink"),Color.pink);
		addTrackColorMenuEntry(trackColorMenu, rb.getString("Orange"),Color.orange);
		addTrackColorMenuEntry(trackColorMenu, rb.getString("Yellow"),Color.yellow);
		addTrackColorMenuEntry(trackColorMenu, rb.getString("Green"),Color.green);
		addTrackColorMenuEntry(trackColorMenu, rb.getString("Blue"),Color.blue);
		addTrackColorMenuEntry(trackColorMenu, rb.getString("Magenta"),Color.magenta);
		addTrackColorMenuEntry(trackColorMenu, rb.getString("Cyan"),Color.cyan);
        optionMenu.add(trackColorMenu);
	}
	
	// operational variables for enter track width pane
	private JmriJFrame enterTrackWidthFrame = null;
	private boolean enterWidthOpen = false;
	private boolean trackWidthChange = false;
	private JTextField sideWidthField = new JTextField(6);
	private JTextField mainlineWidthField = new JTextField(6);
	private JButton trackWidthDone;
	private JButton trackWidthCancel;

	// display dialog for entering track widths
	protected void enterTrackWidth() {
		if (enterWidthOpen) {
			enterTrackWidthFrame.setVisible(true);
			return;
		}
		// Initialize if needed
		if (enterTrackWidthFrame == null) {
            enterTrackWidthFrame = new JmriJFrame( rb.getString("SetTrackWidth") );
            enterTrackWidthFrame.addHelpMenu("package.jmri.jmrit.display.EnterTrackWidth", true);
            enterTrackWidthFrame.setLocation(70,30);
            Container theContentPane = enterTrackWidthFrame.getContentPane();        
            theContentPane.setLayout(new BoxLayout(theContentPane, BoxLayout.Y_AXIS));
			// setup side track width
            JPanel panel2 = new JPanel(); 
            panel2.setLayout(new FlowLayout());
			JLabel sideWidthLabel = new JLabel( rb.getString("SideTrackWidth"));
            panel2.add(sideWidthLabel);
            panel2.add(sideWidthField);
            sideWidthField.setToolTipText( rb.getString("SideTrackWidthHint") );
            theContentPane.add(panel2);
			// setup mainline track width
            JPanel panel3 = new JPanel(); 
            panel3.setLayout(new FlowLayout());
			JLabel mainlineWidthLabel = new JLabel( rb.getString("MainlineTrackWidth"));
            panel3.add(mainlineWidthLabel);
            panel3.add(mainlineWidthField);
            mainlineWidthField.setToolTipText( rb.getString("MainlineTrackWidthHint") );
            theContentPane.add(panel3);
			// set up Done and Cancel buttons
            JPanel panel5 = new JPanel();
            panel5.setLayout(new FlowLayout());
            panel5.add(trackWidthDone = new JButton(rb.getString("Done")));
            trackWidthDone.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    trackWidthDonePressed(e);
                }
            });
            trackWidthDone.setToolTipText( rb.getString("DoneHint") );
			// Cancel
            panel5.add(trackWidthCancel = new JButton(rb.getString("Cancel")));
            trackWidthCancel.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    trackWidthCancelPressed(e);
                }
            });
            trackWidthCancel.setToolTipText( rb.getString("CancelHint") );
            theContentPane.add(panel5);
		}
		// Set up for Entry of Track Widths
		mainlineWidthField.setText(""+getMainlineTrackWidth());
		sideWidthField.setText(""+getSideTrackWidth());
		enterTrackWidthFrame.addWindowListener(new java.awt.event.WindowAdapter() {
				public void windowClosing(java.awt.event.WindowEvent e) {
					trackWidthCancelPressed(null);
				}
			});
        enterTrackWidthFrame.pack();
        enterTrackWidthFrame.setVisible(true);	
		trackWidthChange = false;	
		enterWidthOpen = true;
	}	
	void trackWidthDonePressed(ActionEvent a) {
		String newWidth = "";
		float wid = 0.0F;
		// get side track width
		newWidth = sideWidthField.getText().trim();
		try {
			wid = Float.parseFloat(newWidth);
		}
		catch (Exception e) {
			JOptionPane.showMessageDialog(enterTrackWidthFrame,rb.getString("EntryError")+": "+
					e+" "+rb.getString("TryAgain"),rb.getString("Error"),
					JOptionPane.ERROR_MESSAGE);
            return;
		}
		if ( (wid<=0.99) || (wid>10.0) ) {
			JOptionPane.showMessageDialog(enterTrackWidthFrame,
					java.text.MessageFormat.format(rb.getString("Error2"),
					new String[]{" "+wid+" "}),rb.getString("Error"),
					JOptionPane.ERROR_MESSAGE);
			return;
		}
		if (sideTrackWidth!=wid) {
			sideTrackWidth = wid;
			trackWidthChange = true;
		}
		// get mainline track width
		newWidth = mainlineWidthField.getText().trim();
		try {
			wid = Float.parseFloat(newWidth);
		}
		catch (Exception e) {
			JOptionPane.showMessageDialog(enterTrackWidthFrame,rb.getString("EntryError")+": "+
					e+rb.getString("TryAgain"),rb.getString("Error"),
					JOptionPane.ERROR_MESSAGE);
            return;
		}
		if ( (wid<=0.99) || (wid>10.0) ) {
			JOptionPane.showMessageDialog(enterTrackWidthFrame,
					java.text.MessageFormat.format(rb.getString("Error2"),
					new String[]{" "+wid+" "}),rb.getString("Error"),
					JOptionPane.ERROR_MESSAGE);
			return;
		}
		if (mainlineTrackWidth!=wid) {
			mainlineTrackWidth = wid;
			trackWidthChange = true;
		}
		// success - hide dialog and repaint if needed
		enterWidthOpen = false;
		enterTrackWidthFrame.setVisible(false);
		if (trackWidthChange) {
			repaint();
			panelChanged = true;
		}
	}
	void trackWidthCancelPressed(ActionEvent a) {
		enterWidthOpen = false;
		enterTrackWidthFrame.setVisible(false);
		if (trackWidthChange) {
			repaint();
			panelChanged = true;
		}
	}

	// operational variables for scale/translate track diagram pane
	private JmriJFrame scaleTrackDiagramFrame = null;
	private boolean scaleTrackDiagramOpen = false;
	private JTextField xFactorField = new JTextField(6);
	private JTextField yFactorField = new JTextField(6);
	private JTextField xTranslateField = new JTextField(6);
	private JTextField yTranslateField = new JTextField(6);
	private JButton scaleTrackDiagramDone;
	private JButton scaleTrackDiagramCancel;

	// display dialog for scaling the track diagram
	protected void scaleTrackDiagram() {
		if (scaleTrackDiagramOpen) {
			scaleTrackDiagramFrame.setVisible(true);
			return;
		}
		// Initialize if needed
		if (scaleTrackDiagramFrame == null) {
            scaleTrackDiagramFrame = new JmriJFrame( rb.getString("ScaleTrackDiagram") );
            scaleTrackDiagramFrame.addHelpMenu("package.jmri.jmrit.display.ScaleTrackDiagram", true);
            scaleTrackDiagramFrame.setLocation(70,30);
            Container theContentPane = scaleTrackDiagramFrame.getContentPane();        
            theContentPane.setLayout(new BoxLayout(theContentPane, BoxLayout.Y_AXIS));
			// setup x translate
            JPanel panel31 = new JPanel(); 
            panel31.setLayout(new FlowLayout());
			JLabel xTranslateLabel = new JLabel( rb.getString("XTranslateLabel"));
            panel31.add(xTranslateLabel);
            panel31.add(xTranslateField);
            xTranslateField.setToolTipText( rb.getString("XTranslateHint") );
            theContentPane.add(panel31);
			// setup y translate
            JPanel panel32 = new JPanel(); 
            panel32.setLayout(new FlowLayout());
			JLabel yTranslateLabel = new JLabel( rb.getString("YTranslateLabel"));
            panel32.add(yTranslateLabel);
            panel32.add(yTranslateField);
            yTranslateField.setToolTipText( rb.getString("YTranslateHint") );
            theContentPane.add(panel32);
			// setup information message 1
            JPanel panel33 = new JPanel(); 
            panel33.setLayout(new FlowLayout());
			JLabel message1Label = new JLabel( rb.getString("Message1Label"));
            panel33.add(message1Label);
            theContentPane.add(panel33);			
			// setup x factor
            JPanel panel21 = new JPanel(); 
            panel21.setLayout(new FlowLayout());
			JLabel xFactorLabel = new JLabel( rb.getString("XFactorLabel"));
            panel21.add(xFactorLabel);
            panel21.add(xFactorField);
            xFactorField.setToolTipText( rb.getString("FactorHint") );
            theContentPane.add(panel21);
			// setup y factor
            JPanel panel22 = new JPanel(); 
            panel22.setLayout(new FlowLayout());
			JLabel yFactorLabel = new JLabel( rb.getString("YFactorLabel"));
            panel22.add(yFactorLabel);
            panel22.add(yFactorField);
            yFactorField.setToolTipText( rb.getString("FactorHint") );
            theContentPane.add(panel22);
			// setup information message 2
            JPanel panel23 = new JPanel(); 
            panel23.setLayout(new FlowLayout());
			JLabel message2Label = new JLabel( rb.getString("Message2Label"));
            panel23.add(message2Label);
            theContentPane.add(panel23);			
			// set up Done and Cancel buttons
            JPanel panel5 = new JPanel();
            panel5.setLayout(new FlowLayout());
            panel5.add(scaleTrackDiagramDone = new JButton(rb.getString("ScaleTranslate")));
            scaleTrackDiagramDone.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    scaleTrackDiagramDonePressed(e);
                }
            });
            scaleTrackDiagramDone.setToolTipText( rb.getString("ScaleTranslateHint") );
            panel5.add(scaleTrackDiagramCancel = new JButton(rb.getString("Cancel")));
            scaleTrackDiagramCancel.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    scaleTrackDiagramCancelPressed(e);
                }
            });
            scaleTrackDiagramCancel.setToolTipText( rb.getString("CancelHint") );
            theContentPane.add(panel5);
		}
		// Set up for Entry of Scale and Translation
		xFactorField.setText("1.0");
		yFactorField.setText("1.0");
		xTranslateField.setText("0");
		yTranslateField.setText("0");
		scaleTrackDiagramFrame.addWindowListener(new java.awt.event.WindowAdapter() {
				public void windowClosing(java.awt.event.WindowEvent e) {
					scaleTrackDiagramCancelPressed(null);
				}
			});
        scaleTrackDiagramFrame.pack();
        scaleTrackDiagramFrame.setVisible(true);	
		scaleTrackDiagramOpen = true;
	}	
	void scaleTrackDiagramDonePressed(ActionEvent a) {
		String newText = "";
		boolean scaleChange = false;
		boolean translateError = false;
		float xTranslation = 0.0F;
		float yTranslation = 0.0F;
		float xFactor = 1.0F;
		float yFactor = 1.0F;
		// get x translation
		newText = xTranslateField.getText().trim();
		try {
			xTranslation = Float.parseFloat(newText);
		}
		catch (Exception e) {
			JOptionPane.showMessageDialog(scaleTrackDiagramFrame,rb.getString("EntryError")+": "+
					e+" "+rb.getString("TryAgain"),rb.getString("Error"),
					JOptionPane.ERROR_MESSAGE);
            return;
		}
		// get y translation
		newText = yTranslateField.getText().trim();
		try {
			yTranslation = Float.parseFloat(newText);
		}
		catch (Exception e) {
			JOptionPane.showMessageDialog(scaleTrackDiagramFrame,rb.getString("EntryError")+": "+
					e+" "+rb.getString("TryAgain"),rb.getString("Error"),
					JOptionPane.ERROR_MESSAGE);
            return;
		}
		// get x factor
		newText = xFactorField.getText().trim();
		try {
			xFactor = Float.parseFloat(newText);
		}
		catch (Exception e) {
			JOptionPane.showMessageDialog(scaleTrackDiagramFrame,rb.getString("EntryError")+": "+
					e+" "+rb.getString("TryAgain"),rb.getString("Error"),
					JOptionPane.ERROR_MESSAGE);
            return;
		}
		// get y factor
		newText = yFactorField.getText().trim();
		try {
			yFactor = Float.parseFloat(newText);
		}
		catch (Exception e) {
			JOptionPane.showMessageDialog(scaleTrackDiagramFrame,rb.getString("EntryError")+": "+
					e+" "+rb.getString("TryAgain"),rb.getString("Error"),
					JOptionPane.ERROR_MESSAGE);
            return;
		}
		// here when all numbers read in successfully - check for translation
		if ( (xTranslation!=0.0F) || (yTranslation!=0.0F) ) {
			// apply translation
			if ( translateTrack(xTranslation,yTranslation) )
				scaleChange = true;
			else {
				log.error("Error translating track diagram");
				translateError = true;
			}
		}
		if ( !translateError && ( (xFactor!=1.0) || (yFactor!=1.0) ) ) {
			// apply scale change
			if ( scaleTrack(xFactor,yFactor) )
				scaleChange = true;
			else
				log.error("Error scaling track diagram");
		}		
		// success - hide dialog and repaint if needed
		scaleTrackDiagramOpen = false;
		scaleTrackDiagramFrame.setVisible(false);
		if (scaleChange) {
			repaint();
			panelChanged = true;
		}
	}
	void scaleTrackDiagramCancelPressed(ActionEvent a) {
		scaleTrackDiagramOpen = false;
		scaleTrackDiagramFrame.setVisible(false);
	}
	boolean translateTrack (float xDel, float yDel) {
		// loop over all defined turnouts
		for (int i = 0; i<turnoutList.size();i++) {
			LayoutTurnout t = (LayoutTurnout)turnoutList.get(i);
			Point2D center = t.getCoordsCenter();
			t.setCoordsCenter(new Point2D.Double(center.getX()+xDel,center.getY()+yDel));
		}
		// loop over all defined level crossings
		for (int i = 0; i<xingList.size();i++) {
			LevelXing x = (LevelXing)xingList.get(i);
			Point2D center = x.getCoordsCenter();
			x.setCoordsCenter(new Point2D.Double(center.getX()+xDel,center.getY()+yDel));
		}
		// loop over all defined Anchor Points and End Bumpers
		for (int i = 0; i<pointList.size();i++) {
			PositionablePoint p = (PositionablePoint)pointList.get(i);
			Point2D coord = p.getCoords();
			p.setCoords(new Point2D.Double(coord.getX()+xDel,coord.getY()+yDel));
		}
		return true;
	}
	boolean scaleTrack (float xFactor, float yFactor) {
		// loop over all defined turnouts
		for (int i = 0; i<turnoutList.size();i++) {
			LayoutTurnout t = (LayoutTurnout)turnoutList.get(i);
			t.scaleCoords(xFactor,yFactor);
		}
		// loop over all defined level crossings
		for (int i = 0; i<xingList.size();i++) {
			LevelXing x = (LevelXing)xingList.get(i);
			x.scaleCoords(xFactor,yFactor);
		}
		// loop over all defined Anchor Points and End Bumpers
		for (int i = 0; i<pointList.size();i++) {
			PositionablePoint p = (PositionablePoint)pointList.get(i);
			Point2D coord = p.getCoords();
			p.setCoords(new Point2D.Double(round(coord.getX()*xFactor),
										round(coord.getY()*yFactor)));
		}
		// update the overall scale factors
		xScale = xScale*xFactor;
		yScale = yScale*yFactor;
		return true;
	}
	double round (double x) {
		int i = (int)(x+0.5);
		return ((double)i);
	}

	// operational variables for move selection pane
	private JmriJFrame moveSelectionFrame = null;
	private boolean moveSelectionOpen = false;
	private JTextField xMoveField = new JTextField(6);
	private JTextField yMoveField = new JTextField(6);
	private JButton moveSelectionDone;
	private JButton moveSelectionCancel;
	private boolean canUndoMoveSelection = false;
	private double undoDeltaX = 0.0;
	private double undoDeltaY = 0.0;
	private Rectangle2D undoRect;

	// display dialog for translation a selection
	protected void moveSelection() {
		if (!selectionActive || (selectionWidth==0.0) || (selectionHeight==0.0) ) {
			// no selection has been made - nothing to move
			JOptionPane.showMessageDialog(this,rb.getString("Error12"),
					rb.getString("Error"),JOptionPane.ERROR_MESSAGE);
			return;
		}
		if (moveSelectionOpen) {
			moveSelectionFrame.setVisible(true);
			return;
		}
		// Initialize if needed
		if (moveSelectionFrame == null) {
            moveSelectionFrame = new JmriJFrame( rb.getString("TranslateSelection") );
            moveSelectionFrame.addHelpMenu("package.jmri.jmrit.display.TranslateSelection", true);
            moveSelectionFrame.setLocation(70,30);
            Container theContentPane = moveSelectionFrame.getContentPane();        
            theContentPane.setLayout(new BoxLayout(theContentPane, BoxLayout.Y_AXIS));
			// setup x translate
            JPanel panel31 = new JPanel(); 
            panel31.setLayout(new FlowLayout());
			JLabel xMoveLabel = new JLabel( rb.getString("XTranslateLabel"));
            panel31.add(xMoveLabel);
            panel31.add(xMoveField);
            xMoveField.setToolTipText( rb.getString("XTranslateHint") );
            theContentPane.add(panel31);
			// setup y translate
            JPanel panel32 = new JPanel(); 
            panel32.setLayout(new FlowLayout());
			JLabel yMoveLabel = new JLabel( rb.getString("YTranslateLabel"));
            panel32.add(yMoveLabel);
            panel32.add(yMoveField);
            yMoveField.setToolTipText( rb.getString("YTranslateHint") );
            theContentPane.add(panel32);
			// setup information message 
            JPanel panel33 = new JPanel(); 
            panel33.setLayout(new FlowLayout());
			JLabel message1Label = new JLabel( rb.getString("Message3Label"));
            panel33.add(message1Label);
            theContentPane.add(panel33);			
			// set up Done and Cancel buttons
            JPanel panel5 = new JPanel();
            panel5.setLayout(new FlowLayout());
            panel5.add(moveSelectionDone = new JButton(rb.getString("MoveSelection")));
            moveSelectionDone.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    moveSelectionDonePressed(e);
                }
            });
            moveSelectionDone.setToolTipText( rb.getString("MoveSelectionHint") );
            panel5.add(moveSelectionCancel = new JButton(rb.getString("Cancel")));
            moveSelectionCancel.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    moveSelectionCancelPressed(e);
                }
            });
            moveSelectionCancel.setToolTipText( rb.getString("CancelHint") );
            theContentPane.add(panel5);
		}
		// Set up for Entry of Translation
		xMoveField.setText("0");
		yMoveField.setText("0");
		moveSelectionFrame.addWindowListener(new java.awt.event.WindowAdapter() {
				public void windowClosing(java.awt.event.WindowEvent e) {
					moveSelectionCancelPressed(null);
				}
			});
        moveSelectionFrame.pack();
        moveSelectionFrame.setVisible(true);	
		moveSelectionOpen = true;
	}	
	void moveSelectionDonePressed(ActionEvent a) {
		String newText = "";
		float xTranslation = 0.0F;
		float yTranslation = 0.0F;
		// get x translation
		newText = xMoveField.getText().trim();
		try {
			xTranslation = Float.parseFloat(newText);
		}
		catch (Exception e) {
			JOptionPane.showMessageDialog(moveSelectionFrame,rb.getString("EntryError")+": "+
					e+" "+rb.getString("TryAgain"),rb.getString("Error"),
					JOptionPane.ERROR_MESSAGE);
            return;
		}
		// get y translation
		newText = yMoveField.getText().trim();
		try {
			yTranslation = Float.parseFloat(newText);
		}
		catch (Exception e) {
			JOptionPane.showMessageDialog(moveSelectionFrame,rb.getString("EntryError")+": "+
					e+" "+rb.getString("TryAgain"),rb.getString("Error"),
					JOptionPane.ERROR_MESSAGE);
            return;
		}
		// here when all numbers read in - translation if entered
		if ( (xTranslation!=0.0F) || (yTranslation!=0.0F) ) {
			// set up selection rectangle
			Rectangle2D selectRect = new Rectangle2D.Double (selectionX, selectionY, 
															selectionWidth, selectionHeight);
			// set up undo information
			undoRect = new Rectangle2D.Double (selectionX+xTranslation, selectionY+yTranslation, 
															selectionWidth, selectionHeight);
			undoDeltaX = -xTranslation;
			undoDeltaY = -yTranslation;
			canUndoMoveSelection = true;
			// apply translation to icon items within the selection
			for (int i = 0; i<contents.size(); i++) {
				Component c = (Component)contents.get(i);
				Point2D upperLeft = c.getLocation();
				if (selectRect.contains(upperLeft)) {
					int xNew = (int)(upperLeft.getX()+xTranslation);
					int yNew = (int)(upperLeft.getY()+yTranslation);
					c.setLocation(xNew,yNew);
				}
 			}
			// loop over all defined turnouts
			for (int i = 0; i<turnoutList.size();i++) {
				LayoutTurnout t = (LayoutTurnout)turnoutList.get(i);
				Point2D center = t.getCoordsCenter();
				if (selectRect.contains(center)) {
					t.setCoordsCenter(new Point2D.Double(center.getX()+xTranslation,
																center.getY()+yTranslation));
				}
			}
			// loop over all defined level crossings
			for (int i = 0; i<xingList.size();i++) {
				LevelXing x = (LevelXing)xingList.get(i);
				Point2D center = x.getCoordsCenter();
				if (selectRect.contains(center)) {
					x.setCoordsCenter(new Point2D.Double(center.getX()+xTranslation,
																center.getY()+yTranslation));
				}
			}
			// loop over all defined Anchor Points and End Bumpers
			for (int i = 0; i<pointList.size();i++) {
				PositionablePoint p = (PositionablePoint)pointList.get(i);
				Point2D coord = p.getCoords();
				if (selectRect.contains(coord)) {
					p.setCoords(new Point2D.Double(coord.getX()+xTranslation,
																coord.getY()+yTranslation));
				}
			}
			repaint();
			panelChanged = true;
		}
		// success - hide dialog 
		moveSelectionOpen = false;
		moveSelectionFrame.setVisible(false);
	}
	void moveSelectionCancelPressed(ActionEvent a) {
		moveSelectionOpen = false;
		moveSelectionFrame.setVisible(false);
	}
	void undoMoveSelection() {
		if (canUndoMoveSelection) {
			for (int i = 0; i<contents.size(); i++) {
				Component c = (Component)contents.get(i);
				Point2D upperLeft = c.getLocation();
				if (undoRect.contains(upperLeft)) {
					int xNew = (int)(upperLeft.getX()+undoDeltaX);
					int yNew = (int)(upperLeft.getY()+undoDeltaY);
					c.setLocation(xNew,yNew);
				}
 			}
			for (int i = 0; i<turnoutList.size();i++) {
				LayoutTurnout t = (LayoutTurnout)turnoutList.get(i);
				Point2D center = t.getCoordsCenter();
				if (undoRect.contains(center)) {
					t.setCoordsCenter(new Point2D.Double(center.getX()+undoDeltaX,
															center.getY()+undoDeltaY));
				}
			}
			for (int i = 0; i<xingList.size();i++) {
				LevelXing x = (LevelXing)xingList.get(i);
				Point2D center = x.getCoordsCenter();
				if (undoRect.contains(center)) {
					x.setCoordsCenter(new Point2D.Double(center.getX()+undoDeltaX,
																center.getY()+undoDeltaY));
				}
			}
			for (int i = 0; i<pointList.size();i++) {
				PositionablePoint p = (PositionablePoint)pointList.get(i);
				Point2D coord = p.getCoords();
				if (undoRect.contains(coord)) {
					p.setCoords(new Point2D.Double(coord.getX()+undoDeltaX,
																coord.getY()+undoDeltaY));
				}
			}
			repaint();
			canUndoMoveSelection = false;
		}
		return;
	}
	
	public void setCurrentPositionAndSize() {
		// save current panel location and size
		Dimension dim = getSize();
		panelHeight = dim.height;
		panelWidth = dim.width;
		Point pt = getLocationOnScreen();
		upperLeftX = pt.x;
		upperLeftY = pt.y;
		log.debug("Position - "+upperLeftX+","+upperLeftY+" Size - "+panelWidth+","+panelHeight);		
		panelChanged = true;
	}

    void addTrackColorMenuEntry(JMenu menu, final String name, final Color color) {
        ActionListener a = new ActionListener() {
				final String desiredName = name;
				final Color desiredColor = color;
				public void actionPerformed(ActionEvent e) { 
					if (defaultTrackColor!=desiredColor) {
						defaultTrackColor = desiredColor;
						panelChanged = true;
						repaint();
					}
				}
			};
        JRadioButtonMenuItem r = new JRadioButtonMenuItem(name);
        r.addActionListener(a);
        trackColorButtonGroup.add(r);
        if (defaultTrackColor == color) r.setSelected(true);
        else r.setSelected(false);
        menu.add(r);
		trackColorMenuItems[trackColorCount] = r;
		trackColors[trackColorCount] = color;
		trackColorCount ++;
    }
	
	protected void setOptionMenuTrackColor() {
		for (int i = 0;i<trackColorCount;i++) {
			if (trackColors[i] == defaultTrackColor) 
				trackColorMenuItems[i].setSelected(true);
			else 
				trackColorMenuItems[i].setSelected(false);
		}	
	}
    
    public void setEditMode(boolean visible) {
        editMode = visible;
        topEditBar.setVisible(visible);
        setAllEditable(visible);
		if (visible)
			helpBar.setVisible(showHelpBar);
		else
			helpBar.setVisible(false);
        repaint();
    }

    /**
     * Add a fast clock
     */
    public void addClock() {
        AnalogClock2Display l = new AnalogClock2Display(this);
        l.setOpaque(false);
        l.update();
        l.setDisplayLevel(new Integer(10));
        setNextLocation(l);
		panelChanged = true;
        putClock(l);
    }
    public void putClock(AnalogClock2Display c) {
        c.invalidate();
        targetPanel.add(c, c.getDisplayLevel());
        contents.add(c);
        // reshow the panel
        targetPanel.validate();
    }
	
	/**
	 * Allow external trigger of re-draw
	 */
	public void redrawPanel() { repaint(); }
	
	/**
	 * Allow external reset of dirty bit
	 */
	public void resetDirty() { 
		panelChanged = false;
		savedEditMode = editMode;
		savedPositionable = positionable;
		savedControlLayout = controlLayout;					
		savedShowHelpBar = showHelpBar;
	}

	/**
	 * Allow external set of dirty bit
	 */
	public void setDirty() { panelChanged = true; }	
	
	/**
	 * Handle a mouse pressed event
     */
    private void handleMousePressed(MouseEvent event)
    {
        if (editMode) {
            xLoc = event.getX();
            yLoc = event.getY();
			boolean prevSelectionActive = selectionActive;
			selectionActive = false;
            xLabel.setText(Integer.toString(xLoc));
            yLabel.setText(Integer.toString(yLoc));
			// if asking for menu, or moving a point, check if on known point
			if ( event.isPopupTrigger() || (event.isMetaDown() && positionable) ) {			
				Point loc = event.getPoint();
				selectedObject = null;
				if (checkSelect(loc, false)) {
					// match to a connection point
					selectedObject = foundObject;
					selectedPointType = foundPointType;
					selectedNeedsConnect = foundNeedsConnect;
					startLocation = foundLocation;
					startPoint = loc;
					foundObject = null;
					if (event.isPopupTrigger()) {
						// show popup menu
						switch (selectedPointType) {
							case POS_POINT:
								((PositionablePoint)selectedObject).showPopUp(event);
								break;
							case TURNOUT_CENTER:
								((LayoutTurnout)selectedObject).showPopUp(event);
								break;
							case LEVEL_XING_CENTER:
								((LevelXing)selectedObject).showPopUp(event);								
								break;
						}
					}
				}
				else if (event.isPopupTrigger()) {
					TrackSegment tr = checkTrackSegments(loc);
					if (tr!=null) {
						tr.showPopUp(event);
					}
					else if (backgroundImage != null) {
						// show background image popup menu
						backgroundImage.showPopUp(event);
					}
				}
			}
			else if (event.isShiftDown() && trackBox.isSelected()) {
				// starting a Track Segment, check for free connection point
				Point loc = event.getPoint();
				selectedObject = null;
				if (checkSelect(loc, true)) {
					// match to a free connection point
					beginObject = foundObject;
					beginPointType = foundPointType;
					beginLocation = foundLocation;
				}
				else {
					foundObject = null;
					beginObject = null;
				}
			}
			else if ( controlLayout && (!event.isMetaDown()) && (!event.isPopupTrigger()) && 
									(!event.isShiftDown()) && (!event.isControlDown()) ) {
				// check if mouse is on a turnout 
				selectedObject = null;
				Point loc = event.getPoint();
				for (int i = 0; i<turnoutList.size();i++) {
					LayoutTurnout t = (LayoutTurnout)turnoutList.get(i);
					// check the center point
					Point2D pt = t.getCoordsCenter();
					Rectangle2D r = new Rectangle2D.Double(
						pt.getX()-SIZE2,pt.getY()-SIZE2,2.0*SIZE2,2.0*SIZE2);
					if (r.contains(loc)) {
						// mouse was pressed on this turnout
						selectedObject = (Object)t;
						selectedPointType = TURNOUT_CENTER;
						break;
					}
				}
				// check if starting selection
				if (selectedObject == null) {
					selectionActive = true;
					selectionX = loc.getX();
					selectionY = loc.getY();
					selectionWidth = 0.0;
					selectionHeight = 0.0;
				}
			}
			if (prevSelectionActive) repaint();	
        }
		else if ( controlLayout && (!event.isMetaDown()) && (!event.isPopupTrigger()) && 
								(!event.isShiftDown()) && (!event.isControlDown()) ) {
			// checked if mouse is on a turnout (using wider search range)
			selectedObject = null;
			Point loc = event.getPoint();
			for (int i = 0; i<turnoutList.size();i++) {
				LayoutTurnout t = (LayoutTurnout)turnoutList.get(i);
				// check the center point
				Point2D pt = t.getCoordsCenter();
				Rectangle2D r = new Rectangle2D.Double(
						pt.getX()-(SIZE2*2.0),pt.getY()-(SIZE2*2.0),4.0*SIZE2,4.0*SIZE2);
				if (r.contains(loc)) {
					// mouse was pressed on this turnout
					selectedObject = (Object)t;
					selectedPointType = TURNOUT_CENTER;
					break;
				}
			}
		}
    }
	
	private boolean checkSelect(Point2D loc, boolean requireUnconnected) {
		// check positionable points, if any
		for (int i = 0; i<pointList.size();i++) {
			PositionablePoint p = (PositionablePoint)pointList.get(i);
			if ( ((Object)p!=selectedObject) && !requireUnconnected || 
					(p.getConnect1()==null) || 
					((p.getType()!=PositionablePoint.END_BUMPER) && 
												(p.getConnect2()==null)) ) {
				Point2D pt = p.getCoords();
				Rectangle2D r = new Rectangle2D.Double(
							pt.getX() - SIZE,pt.getY() - SIZE,SIZE2,SIZE2);
				if (r.contains(loc)) {
					// mouse was pressed on this connection point
					foundLocation = pt;
					foundObject = (Object)p;
					foundPointType = POS_POINT;
					foundNeedsConnect = ((p.getConnect1()==null)||(p.getConnect2()==null));
					return true;
				}
			}
		}
		// check turnouts, if any
		for (int i = 0; i<turnoutList.size();i++) {
			LayoutTurnout t = (LayoutTurnout)turnoutList.get(i);
			if ((Object)t!=selectedObject) {
				if (!requireUnconnected) {
					// check the center point
					Point2D pt = t.getCoordsCenter();
					Rectangle2D r = new Rectangle2D.Double(
							pt.getX() - SIZE2,pt.getY() - SIZE2,SIZE2+SIZE2,SIZE2+SIZE2);
					if (r.contains(loc)) {
						// mouse was pressed on this connection point
						foundLocation = pt;
						foundObject = (Object)t;
						foundPointType = TURNOUT_CENTER;
						foundNeedsConnect = false;
						return true;
					}
				}
				if (!requireUnconnected || (t.getConnectA()==null)) {
					// check the A connection point
					Point2D pt = t.getCoordsA();
					Rectangle2D r = new Rectangle2D.Double(
							pt.getX() - SIZE,pt.getY() - SIZE,SIZE2,SIZE2);
					if (r.contains(loc)) {
						// mouse was pressed on this connection point
						foundLocation = pt;
						foundObject = (Object)t;
						foundPointType = TURNOUT_A;
						foundNeedsConnect = (t.getConnectA()==null);
						return true;
					}
				}
				if (!requireUnconnected || (t.getConnectB()==null)) {
					// check the B connection point
					Point2D pt = t.getCoordsB();
					Rectangle2D r = new Rectangle2D.Double(
							pt.getX() - SIZE,pt.getY() - SIZE,SIZE2,SIZE2);
					if (r.contains(loc)) {
						// mouse was pressed on this connection point
						foundLocation = pt;
						foundObject = (Object)t;
						foundPointType = TURNOUT_B;
						foundNeedsConnect = (t.getConnectB()==null);
						return true;
					}
				}
				if (!requireUnconnected || (t.getConnectC()==null)) {
					// check the C connection point
					Point2D pt = t.getCoordsC();
					Rectangle2D r = new Rectangle2D.Double(
							pt.getX() - SIZE,pt.getY() - SIZE,SIZE2,SIZE2);
					if (r.contains(loc)) {
						// mouse was pressed on this connection point
						foundLocation = pt;
						foundObject = (Object)t;
						foundPointType = TURNOUT_C;
						foundNeedsConnect = (t.getConnectC()==null);
						return true;
					}
				}
				if ((t.getTurnoutType()==LayoutTurnout.DOUBLE_XOVER) && (
						!requireUnconnected || (t.getConnectD()==null))) {
					// check the D connection point, double crossover turnouts only
					Point2D pt = t.getCoordsD();
					Rectangle2D r = new Rectangle2D.Double(
							pt.getX() - SIZE,pt.getY() - SIZE,SIZE2,SIZE2);
					if (r.contains(loc)) {
						// mouse was pressed on this connection point
						foundLocation = pt;
						foundObject = (Object)t;
						foundPointType = TURNOUT_D;
						foundNeedsConnect = (t.getConnectD()==null);
						return true;
					}
				}
			}
		}
				
		// check level Xings, if any
		for (int i = 0; i<xingList.size();i++) {
			LevelXing x = (LevelXing)xingList.get(i);
			if ((Object)x!=selectedObject) {
				if (!requireUnconnected) {
					// check the center point
					Point2D pt = x.getCoordsCenter();
					Rectangle2D r = new Rectangle2D.Double(
							pt.getX() - SIZE2,pt.getY() - SIZE2,SIZE2+SIZE2,SIZE2+SIZE2);
					if (r.contains(loc)) {
						// mouse was pressed on this connection point
						foundLocation = pt;
						foundObject = (Object)x;
						foundPointType = LEVEL_XING_CENTER;
						foundNeedsConnect = false;
						return true;
					}
				}
				if (!requireUnconnected || (x.getConnectA()==null)) {
					// check the A connection point
					Point2D pt = x.getCoordsA();
					Rectangle2D r = new Rectangle2D.Double(
							pt.getX() - SIZE,pt.getY() - SIZE,SIZE2,SIZE2);
					if (r.contains(loc)) {
						// mouse was pressed on this connection point
						foundLocation = pt;
						foundObject = (Object)x;
						foundPointType = LEVEL_XING_A;
						foundNeedsConnect = (x.getConnectA()==null);
						return true;
					}
				}
				if (!requireUnconnected || (x.getConnectB()==null)) {
					// check the B connection point
					Point2D pt = x.getCoordsB();
					Rectangle2D r = new Rectangle2D.Double(
							pt.getX() - SIZE,pt.getY() - SIZE,SIZE2,SIZE2);
					if (r.contains(loc)) {
						// mouse was pressed on this connection point
						foundLocation = pt;
						foundObject = (Object)x;
						foundPointType = LEVEL_XING_B;
						foundNeedsConnect = (x.getConnectB()==null);
						return true;
					}
				}
				if (!requireUnconnected || (x.getConnectC()==null)) {
					// check the C connection point
					Point2D pt = x.getCoordsC();
					Rectangle2D r = new Rectangle2D.Double(
							pt.getX() - SIZE,pt.getY() - SIZE,SIZE2,SIZE2);
					if (r.contains(loc)) {
						// mouse was pressed on this connection point
						foundLocation = pt;
						foundObject = (Object)x;
						foundPointType = LEVEL_XING_C;
						foundNeedsConnect = (x.getConnectC()==null);
						return true;
					}
				}
				if (!requireUnconnected || (x.getConnectD()==null)) {
					// check the D connection point
					Point2D pt = x.getCoordsD();
					Rectangle2D r = new Rectangle2D.Double(
							pt.getX() - SIZE,pt.getY() - SIZE,SIZE2,SIZE2);
					if (r.contains(loc)) {
						// mouse was pressed on this connection point
						foundLocation = pt;
						foundObject = (Object)x;
						foundPointType = LEVEL_XING_D;
						foundNeedsConnect = (x.getConnectD()==null);
						return true;
					}
				}
			}
		}
		
		// no connection point found
		foundObject = null;
		return false;
	}
	
	private TrackSegment checkTrackSegments(Point2D loc) {
		// check Track Segments, if any
		for (int i = 0; i<trackList.size(); i++) {
			TrackSegment tr = (TrackSegment)trackList.get(i);
			Object o = tr.getConnect1();
			int type = tr.getType1();
			// get coordinates of first end point
			Point2D pt1 = getEndCoords(o,type);
			o = tr.getConnect2();
			type = tr.getType2();
			// get coordinates of second end point
			Point2D pt2 = getEndCoords(o,type);
			// construct a detection rectangle
			double cX = (pt1.getX() + pt2.getX())/2.0D;
			double cY = (pt1.getY() + pt2.getY())/2.0D;			
			Rectangle2D r = new Rectangle2D.Double(
						cX - SIZE2,cY - SIZE2,SIZE2+SIZE2,SIZE2+SIZE2);
			// Test this detection rectangle
			if (r.contains(loc)) {
				// mouse was pressed in detection rectangle
				return tr;
			}
		}
		return null;
	}
	
	private Point2D getEndCoords(Object o, int type) {
		switch (type) {
			case POS_POINT:
				return ((PositionablePoint)o).getCoords();
			case TURNOUT_A:
				return ((LayoutTurnout)o).getCoordsA();
			case TURNOUT_B:
				return ((LayoutTurnout)o).getCoordsB();
			case TURNOUT_C:
				return ((LayoutTurnout)o).getCoordsC();
			case TURNOUT_D:
				return ((LayoutTurnout)o).getCoordsD();
			case LEVEL_XING_A:
				return ((LevelXing)o).getCoordsA();
			case LEVEL_XING_B:
				return ((LevelXing)o).getCoordsB();
			case LEVEL_XING_C:
				return ((LevelXing)o).getCoordsC();
			case LEVEL_XING_D:
				return ((LevelXing)o).getCoordsD();
		}
		return (new Point2D.Double(0.0,0.0));
	}			

    private void handleMouseReleased(MouseEvent event)
    {
        if (editMode) {
            xLoc = event.getX();
            yLoc = event.getY();
            xLabel.setText(Integer.toString(xLoc));
            yLabel.setText(Integer.toString(yLoc));
			currentPoint = event.getPoint();
            if (!event.isPopupTrigger() && !event.isMetaDown() && event.isShiftDown()) {
                Point p = event.getPoint();
                if (turnoutRHBox.isSelected()) {
					addLayoutTurnout(LayoutTurnout.RH_TURNOUT);
                }
                else if (turnoutLHBox.isSelected()) {
					addLayoutTurnout(LayoutTurnout.LH_TURNOUT);
                }
                else if (turnoutWYEBox.isSelected()) {
					addLayoutTurnout(LayoutTurnout.WYE_TURNOUT);
                }
                else if (doubleXoverBox.isSelected()) {
					addLayoutTurnout(LayoutTurnout.DOUBLE_XOVER);
                }
                else if (levelXingBox.isSelected()) {
					addLevelXing();
                }
                else if (endBumperBox.isSelected()) {
					addEndBumper();
                }
                else if (anchorBox.isSelected()) {
					addAnchor();
                }
                else if (trackBox.isSelected()) {
					if ( (beginObject!=null) && (foundObject!=null) &&
							(beginObject!=foundObject) ) {
						addTrackSegment();
						setCursor(Cursor.getDefaultCursor());
					}
					beginObject = null;
					foundObject = null;
                }
                else if (multiSensorBox.isSelected()) {
                    startMultiSensor();
                }
                else if (sensorBox.isSelected()) {
                    addSensor();
                }
                else if (signalBox.isSelected()) {
                    addSignalHead();
                }
                else if (textLabelBox.isSelected()) {
                    addLabel();
                }
                else if (memoryBox.isSelected()) {
                    addMemory();
                }
                else if (iconLabelBox.isSelected()) {
                    addIcon();
                }
                else {
                    log.warn("No item selected in panel edit mode");
                }
                repaint();
            }
			// check if controlling turnouts
			else if ( ( selectedObject!=null) && (selectedPointType==TURNOUT_CENTER) && 
					controlLayout && (!event.isMetaDown()) && (!event.isPopupTrigger()) && 
						(!event.isShiftDown()) && (!event.isControlDown()) ) {
				// controlling layout, not in edit mode
				LayoutTurnout t = (LayoutTurnout)selectedObject;
				t.toggleTurnout();
			}
			// check for popup trigger 
			else if ( event.isPopupTrigger() ) {			
				Point loc = event.getPoint();
				selectedObject = null;
				if (checkSelect(loc, false)) {
					// match to a connection point
					selectedObject = foundObject;
					selectedPointType = foundPointType;
					selectedNeedsConnect = foundNeedsConnect;
					startLocation = foundLocation;
					startPoint = loc;
					foundObject = null;
					// show popup menu
					switch (selectedPointType) {
						case POS_POINT:
							((PositionablePoint)selectedObject).showPopUp(event);
							break;
						case TURNOUT_CENTER:
							((LayoutTurnout)selectedObject).showPopUp(event);
							break;
						case LEVEL_XING_CENTER:
							((LevelXing)selectedObject).showPopUp(event);								
							break;
					}
				}
				else {
					TrackSegment tr = checkTrackSegments(loc);
					if (tr!=null) {
						tr.showPopUp(event);
					}
				}
			}
			if ( (trackBox.isSelected()) && (beginObject!=null) && (foundObject!=null) ) {
				// user let up shift key before releasing the mouse when creating a track segment
				setCursor(Cursor.getDefaultCursor());
				beginObject = null;
				foundObject = null;
				repaint();
			}
        }
		// check if controlling turnouts out of edit mode
		else if ( ( selectedObject!=null) && (selectedPointType==TURNOUT_CENTER) && 
				controlLayout && (!event.isMetaDown()) && (!event.isPopupTrigger()) && 
					(!event.isShiftDown()) ) {
			// controlling layout, not in edit mode
			LayoutTurnout t = (LayoutTurnout)selectedObject;
			t.toggleTurnout();
		}
		if (selectedObject!=null) {
			// An object was selected, deselect it
			prevSelectedObject = selectedObject;
			selectedObject = null;
		}
    }

    private void handleMouseMoved(MouseEvent event)
    {
        if (editMode) {
            xLoc = event.getX();
            yLoc = event.getY();
            xLabel.setText(Integer.toString(xLoc));
            yLabel.setText(Integer.toString(yLoc));
        }
    }

    private void handleMouseDragged(MouseEvent event)
    {
        if (editMode) {
            xLoc = event.getX();
            yLoc = event.getY();
            xLabel.setText(Integer.toString(xLoc));
            yLabel.setText(Integer.toString(yLoc));
			if ( (selectedObject!=null) && event.isMetaDown() ) {
				// moving a point
				Point2D p = event.getPoint();
				Point2D newPos = new Point2D.Double(
						startLocation.getX() + p.getX() - startPoint.getX(),
						startLocation.getY() + p.getY() - startPoint.getY());
				switch (selectedPointType) {
					case POS_POINT:
						((PositionablePoint)selectedObject).setCoords(newPos);
						break;
					case TURNOUT_CENTER:
						((LayoutTurnout)selectedObject).setCoordsCenter(newPos);
						break;
					case TURNOUT_A:
						LayoutTurnout o = (LayoutTurnout)selectedObject;
						o.setCoordsA(newPos);
						break;
					case TURNOUT_B:
						o = (LayoutTurnout)selectedObject;
						o.setCoordsB(newPos);
						break;
					case TURNOUT_C:
						o = (LayoutTurnout)selectedObject;
						o.setCoordsC(newPos);
						break;
					case TURNOUT_D:
						o = (LayoutTurnout)selectedObject;
						o.setCoordsD(newPos);
						break;
					case LEVEL_XING_CENTER:
						((LevelXing)selectedObject).setCoordsCenter(newPos);
						break;
					case LEVEL_XING_A:
						LevelXing x = (LevelXing)selectedObject;
						x.setCoordsA(newPos);
						break;
					case LEVEL_XING_B:
						x = (LevelXing)selectedObject;
						x.setCoordsB(newPos);
						break;
					case LEVEL_XING_C:
						x = (LevelXing)selectedObject;
						x.setCoordsC(newPos);
						break;
					case LEVEL_XING_D:
						x = (LevelXing)selectedObject;
						x.setCoordsD(newPos);
						break;
				}
				// if point is unconnected, check proximity of an unconnected point
				if (selectedNeedsConnect) {
					boolean needResetCursor = (foundObject!=null);
					if (checkSelect(newPos, true)) {
						// have match to free connection point, change cursor
						setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
					}
					else if (needResetCursor) {
						setCursor(Cursor.getDefaultCursor());
					}
				}
				repaint();
			}
			else if ( (beginObject!=null) && event.isShiftDown() 
											&& trackBox.isSelected() ) {
				// dragging from first end of Track Segment
				currentLocation = event.getPoint();
				boolean needResetCursor = (foundObject!=null);
				if (checkSelect(currentLocation, true)) {
					// have match to free connection point, change cursor
					setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
				}
				else if (needResetCursor) {
					setCursor(Cursor.getDefaultCursor());
				}
				repaint();
			}
			else if ( selectionActive && (!event.isShiftDown()) && (!event.isMetaDown()) ) {
				selectionWidth = xLoc - selectionX;
				selectionHeight = yLoc - selectionY;
				repaint();
			}
        }
    }

	private void updateLocation(Object o,int pointType,Point2D newPos) {
		switch (pointType) {
			case TURNOUT_A:
				((LayoutTurnout)o).setCoordsA(newPos);
				break;
			case TURNOUT_B:
				((LayoutTurnout)o).setCoordsB(newPos);
				break;
			case TURNOUT_C:
				((LayoutTurnout)o).setCoordsC(newPos);
				break;
			case TURNOUT_D:
				((LayoutTurnout)o).setCoordsD(newPos);
				break;
			case LEVEL_XING_A:
				((LevelXing)o).setCoordsA(newPos);
				break;
			case LEVEL_XING_B:
				((LevelXing)o).setCoordsB(newPos);
				break;
			case LEVEL_XING_C:
				((LevelXing)o).setCoordsC(newPos);
				break;
			case LEVEL_XING_D:
				((LevelXing)o).setCoordsD(newPos);
				break;
		}
		panelChanged = true;
	}
	public void setLoc(int x, int y) {
		if (editMode) {
			xLoc = x;
			yLoc = y;
			xLabel.setText(Integer.toString(xLoc));
			yLabel.setText(Integer.toString(yLoc));
		}
	}
    
    /**
     * Add an Anchor point. 
     */
    public void addAnchor() {
		numAnchors ++;
		// get unique name
		String name = "";
		boolean duplicate = true;
		while (duplicate) {
			name = "A"+numAnchors;
			if (findPositionablePointByName(name)==null) duplicate = false;
			if (duplicate) numAnchors ++;
		}
		// create object
		PositionablePoint o = new PositionablePoint(name, 
							PositionablePoint.ANCHOR, currentPoint, this);
		if (o!=null) {
			pointList.add(o);
			panelChanged = true;
		}
	}

    /**
     * Add an End Bumper point. 
     */
    public void addEndBumper() {
		numEndBumpers ++;
		// get unique name
		String name = "";
		boolean duplicate = true;
		while (duplicate) {
			name = "EB"+numEndBumpers;
			if (findPositionablePointByName(name)==null) duplicate = false;
			if (duplicate) numEndBumpers ++;
		}
		// create object
		PositionablePoint o = new PositionablePoint(name, 
							PositionablePoint.END_BUMPER, currentPoint, this);
		if (o!=null) {
			pointList.add(o);
			panelChanged = true;
		}
	}

    /**
     * Add a Track Segment 
     */
    public void addTrackSegment() {
		numTrackSegments ++;
		// get unique name
		String name = "";
		boolean duplicate = true;
		while (duplicate) {
			name = "T"+numTrackSegments;
			if (findTrackSegmentByName(name)==null) duplicate = false;
			if (duplicate) numTrackSegments ++;
		}
		// create object
		newTrack = new TrackSegment(name,beginObject,beginPointType,
						foundObject,foundPointType,dashedLine.isSelected(),
						mainlineTrack.isSelected(),this);
		if (newTrack!=null) {
			trackList.add(newTrack);
			panelChanged = true;
			// link to connected objects
			setLink(newTrack,TRACK,beginObject,beginPointType);
			setLink(newTrack,TRACK,foundObject,foundPointType);
			// check on layout block
			LayoutBlock b = provideLayoutBlock(blockIDField.getText().trim());
			if (b!=null) {
				newTrack.setLayoutBlock(b);
				// check on occupancy sensor
				String sensorName = (blockSensor.getText().trim());
				if (sensorName.length()>0) {
					if (!validateSensor(sensorName,b,this)) {
						b.setOccupancySensorName("");
					}
					else {
						blockSensor.setText( b.getOccupancySensorName() );
					}
				}
			}
		}
		else {
			log.error("Failure to create a new Track Segment");
		}
	}

    /**
     * Add a Level Crossing 
     */
    public void addLevelXing() {
		numLevelXings ++;
		// get unique name
		String name = "";
		boolean duplicate = true;
		while (duplicate) {
			name = "X"+numLevelXings;
			if (findLevelXingByName(name)==null) duplicate = false;
			if (duplicate) numLevelXings ++;
		}
		// create object
		LevelXing o = new LevelXing(name,currentPoint,this);
		if (o!=null) {
			xingList.add(o);
			panelChanged = true;
			// check on layout block
			LayoutBlock b = provideLayoutBlock(blockIDField.getText().trim());
			if (b!=null) {
				o.setLayoutBlockAC(b);
				o.setLayoutBlockBD(b);
				// check on occupancy sensor
				String sensorName = (blockSensor.getText().trim());
				if (sensorName.length()>0) {
					if (!validateSensor(sensorName,b,this)) {
						b.setOccupancySensorName("");
					}
					else {
						blockSensor.setText( b.getOccupancySensorName() );
					}
				}
			}
		}
	}

    /**
     * Add a Layout Turnout 
     */
    public void addLayoutTurnout(int type) {
		// get the rotation entry
		double rot = 0.0;
		String s = rotationField.getText().trim();
		if (s.length()<1) {
			rot = 0.0;
		}
		else {
			try {
				rot = Double.parseDouble(s);
			}
			catch (Exception e) {
				JOptionPane.showMessageDialog(targetPanel,rb.getString("Error3")+" "+
						e,rb.getString("Error"),JOptionPane.ERROR_MESSAGE);
				return;
			}
		}
		numLayoutTurnouts ++;
		// get unique name
		String name = "";
		boolean duplicate = true;
		while (duplicate) {
			name = "TO"+numLayoutTurnouts;
			if (findLayoutTurnoutByName(name)==null) duplicate = false;
			if (duplicate) numLayoutTurnouts ++;
		}
		// create object
		LayoutTurnout o = new LayoutTurnout(name,type,
										currentPoint,rot,xScale,yScale,this);
		if (o!=null) {
			turnoutList.add(o);
			panelChanged = true;
			// check on layout block
			LayoutBlock b = provideLayoutBlock(blockIDField.getText().trim());
			if (b!=null) {
				o.setLayoutBlock(b);
				// check on occupancy sensor
				String sensorName = (blockSensor.getText().trim());
				if (sensorName.length()>0) {
					if (!validateSensor(sensorName,b,this)) {
						b.setOccupancySensorName("");
					}
					else {
						blockSensor.setText( b.getOccupancySensorName() );
					}
				}
			}
			// set default continuing route Turnout State
 			o.setContinuingSense(Turnout.CLOSED);
			// check on a physical turnout
			String turnoutName = nextTurnout.getText().trim();
			if ( validatePhysicalTurnout(turnoutName,targetPanel) ) {
				// turnout is valid and unique.
				o.setTurnout(turnoutName);
				if (o.getTurnout().getSystemName().equals(turnoutName.toUpperCase())) {
					nextTurnout.setText(turnoutName.toUpperCase());
				}
			}
			else {
				o.setTurnout("");
				nextTurnout.setText("");
			}
		}
	}
	
	/**
	 * Validates that a physical turnout exists and is unique among Layout Turnouts
	 *    Returns true if valid turnout was entered, false otherwise
	 */
	public boolean validatePhysicalTurnout(String turnoutName,Component openPane) {
		// check if turnout name was entered
		if (turnoutName.length() < 1) {
			// no turnout entered
			return false;
		}
		// ensure that this turnout is unique among Layout Turnouts
		LayoutTurnout t = null;
		for (int i=0;i<turnoutList.size();i++) {
			t = (LayoutTurnout)turnoutList.get(i);
			log.debug("LT '"+t.getName()+"', Turnout tested '"+t.getTurnoutName()+"' ");
			Turnout to = t.getTurnout();
			if (to!=null) {
				if ( (to.getSystemName().equals(turnoutName.toUpperCase())) ||
					((to.getUserName()!=null) && (to.getUserName().equals(turnoutName))) ) {
					JOptionPane.showMessageDialog(openPane,
							java.text.MessageFormat.format(rb.getString("Error4"),
							new String[]{turnoutName}),
							rb.getString("Error"),JOptionPane.ERROR_MESSAGE);
					return false;
				}
			}
		}
		// check that the unique turnout name corresponds to a defined physical turnout
		Turnout to = InstanceManager.turnoutManagerInstance().getTurnout(turnoutName);
		if (to == null) {
			// There is no turnout corresponding to this name
			JOptionPane.showMessageDialog(openPane,
					java.text.MessageFormat.format(rb.getString("Error8"),
					new String[]{turnoutName}),
					rb.getString("Error"),JOptionPane.ERROR_MESSAGE);
			return false;
		}
		return true;
	}

    /**
     * Adds a link in the 'to' object to the 'from' object
     */
    private void setLink(Object fromObject,int fromPointType,
											Object toObject,int toPointType) {
		switch (toPointType) {
			case POS_POINT:
				if (fromPointType==TRACK) {
					((PositionablePoint)toObject).setTrackConnection(
													(TrackSegment)fromObject);
				}
				else {
					log.error("Attempt to set a non-TRACK connection to a Positionable Point");
				}
				break;
			case TURNOUT_A:
				((LayoutTurnout)toObject).setConnectA(fromObject,fromPointType);
				break;
			case TURNOUT_B:
				((LayoutTurnout)toObject).setConnectB(fromObject,fromPointType);
				break;
			case TURNOUT_C:
				((LayoutTurnout)toObject).setConnectC(fromObject,fromPointType);
				break;
			case TURNOUT_D:
				((LayoutTurnout)toObject).setConnectD(fromObject,fromPointType);
				break;
			case LEVEL_XING_A:
				((LevelXing)toObject).setConnectA(fromObject,fromPointType);
				break;
			case LEVEL_XING_B:
				((LevelXing)toObject).setConnectB(fromObject,fromPointType);
				break;
			case LEVEL_XING_C:
				((LevelXing)toObject).setConnectC(fromObject,fromPointType);
				break;
			case LEVEL_XING_D:
				((LevelXing)toObject).setConnectD(fromObject,fromPointType);
				break;
			case TRACK:
				// should never happen, Track Segment links are set in ctor
				log.error("Illegal request to set a Track Segment link");
				break;
		}
	}
	
    /**
     * Return a layout block with the entered name, creating a new one if needed.
	 *   Note that the entered name becomes the user name of the LayoutBlock, and
	 *		a system name is automatically created by LayoutBlockManager if needed.
     */
    public LayoutBlock provideLayoutBlock(String s) {
		if (s.length() < 1) {
			// nothing entered
			return null;
		}
		// check if this Layout Block already exists
		LayoutBlock blk = InstanceManager.layoutBlockManagerInstance().getByUserName(s);
		if (blk == null) {
			blk = InstanceManager.layoutBlockManagerInstance().createNewLayoutBlock(null,s);
			if (blk == null) {
				log.error("Failure to create LayoutBlock '"+s+"'.");
				return null;
			}
			else {
				// initialize the new block
				blk.setBlockTrackColor(defaultTrackColor);
				blk.setBlockOccupiedColor(defaultTrackColor);
			}
		}
		// set both new and previously existing block
		blk.addLayoutEditor(this);
		panelChanged = true;
		blk.incrementUse();
		return blk;
	}

	/**
	 * Validates that the supplied occupancy sensor name corresponds to an existing sensor
	 *   and is unique among all blocks.  If valid, returns true and sets the block sensor
	 *   name in the block.  Else returns false, and does nothing to the block.
	 * This method also converts the sensor name to upper case if it is a system name.
	 */
	public boolean validateSensor(String sensorName, LayoutBlock blk, Component openFrame) {
		// check if anything entered	
		if (sensorName.length()<1) {
			// no sensor entered
			return false;
		}
		// get a validated sensor corresponding to this name and assigned to block
		Sensor s = blk.validateSensor(sensorName,openFrame);
		if (s==null) {
			// There is no sensor corresponding to this name
			return false;
		}
		else {
			// Have sensor, check if name should be upper case
			if (sensorName.toUpperCase().equals(s.getSystemName())) {
				sensorName = sensorName.toUpperCase();
			}
		}
		return true;
	}
	
    /**
     * Return a layout block with the given name if one exists.
	 * Registers this LayoutEditor with the layout block.
	 * This method is designed to be used when a panel is loaded. The calling
	 *		method must handle whether the use count should be incremented.
     */
    public LayoutBlock getLayoutBlock(String blockID) {
		// check if this Layout Block already exists
		LayoutBlock blk = InstanceManager.layoutBlockManagerInstance().getByUserName(blockID);
		if (blk==null) {
			log.error("LayoutBlock '"+blockID+"' not found when panel loaded");
			return null;
		}
		blk.addLayoutEditor(this);
		return blk;
	}
	
	boolean noWarnPositionablePoint = false;
	
    /**
     * Remove a PositionablePoint -- an Anchor or an End Bumper. 
     */
	protected boolean removePositionablePoint(PositionablePoint o) {
		// First verify with the user that this is really wanted
		if (!noWarnPositionablePoint) {
			int selectedValue = JOptionPane.showOptionDialog(targetPanel,
					rb.getString("Question2"),rb.getString("WarningTitle"),
					JOptionPane.YES_NO_CANCEL_OPTION,JOptionPane.QUESTION_MESSAGE,null,
					new Object[]{rb.getString("ButtonYes"),rb.getString("ButtonNo"),
					rb.getString("ButtonYesPlus")},rb.getString("ButtonNo"));
			if (selectedValue == 1) return(false);   // return without creating if "No" response
			if (selectedValue == 2) {
				// Suppress future warnings, and continue
				noWarnPositionablePoint = true;
			}
		}
		// remove from selection information
		if (selectedObject==(Object)o) selectedObject = null;
		if (prevSelectedObject==(Object)o) prevSelectedObject = null;
		// remove connections if any
		TrackSegment t = o.getConnect1();
		if (t!=null) removeTrackSegment(t);
		t = o.getConnect2();
		if (t!=null) removeTrackSegment(t);
		// delete from array
		for (int i = 0; i<pointList.size();i++) {
			PositionablePoint p = (PositionablePoint)pointList.get(i);
			if (p==o) {
				// found object
				pointList.remove(i);
				panelChanged = true;
				repaint();
				return(true);
			}
		}
		return (false);	
	}
	
	boolean noWarnLayoutTurnout = false;
	
    /**
     * Remove a LayoutTurnout
     */
	protected boolean removeLayoutTurnout(LayoutTurnout o) {
		// First verify with the user that this is really wanted
		if (!noWarnLayoutTurnout) {
			int selectedValue = JOptionPane.showOptionDialog(targetPanel,
					rb.getString("Question1"),rb.getString("WarningTitle"),
					JOptionPane.YES_NO_CANCEL_OPTION,JOptionPane.QUESTION_MESSAGE,null,
					new Object[]{rb.getString("ButtonYes"),rb.getString("ButtonNo"),
					rb.getString("ButtonYesPlus")},rb.getString("ButtonNo"));
			if (selectedValue == 1) return(false);   // return without removing if "No" response
			if (selectedValue == 2) {
				// Suppress future warnings, and continue
				noWarnLayoutTurnout = true;
			}
		}
		// remove from selection information
		if (selectedObject==(Object)o) selectedObject = null;
		if (prevSelectedObject==(Object)o) prevSelectedObject = null;
		// remove connections if any
		TrackSegment t = (TrackSegment)o.getConnectA();
		if (t!=null) removeTrackSegment(t);
		t = (TrackSegment)o.getConnectB();
		if (t!=null) removeTrackSegment(t);
		t = (TrackSegment)o.getConnectC();
		if (t!=null) removeTrackSegment(t);
		t = (TrackSegment)o.getConnectD();
		if (t!=null) removeTrackSegment(t);
		// decrement Block use count(s)
		LayoutBlock b = o.getLayoutBlock();
		if (b!=null) b.decrementUse();
		if (o.getTurnoutType()==LayoutTurnout.DOUBLE_XOVER) {
			LayoutBlock b2 = o.getLayoutBlockB();
			if ( (b2!=null) && (b2!=b) ) b2.decrementUse();
			LayoutBlock b3 = o.getLayoutBlockC();
			if ( (b3!=null) && (b3!=b) && (b3!=b2) ) b3.decrementUse();
			LayoutBlock b4 = o.getLayoutBlockD();
			if ( (b4!=null) && (b4!=b) &&
						(b4!=b2) && (b4!=b3) ) b4.decrementUse();
		}	
		// delete from array
		for (int i = 0; i<turnoutList.size();i++) {
			LayoutTurnout lt = (LayoutTurnout)turnoutList.get(i);
			if (lt==o) {
				// found object
				turnoutList.remove(i);
				panelChanged = true;
				repaint();
				return(true);
			}
		}
		return(false);	
	}
	
	boolean noWarnLevelXing = false;
	
    /**
     * Remove a Level Crossing
     */
	protected boolean removeLevelXing (LevelXing o) {
		// First verify with the user that this is really wanted
		if (!noWarnLevelXing) {
			int selectedValue = JOptionPane.showOptionDialog(targetPanel,
					rb.getString("Question3"),rb.getString("WarningTitle"),
					JOptionPane.YES_NO_CANCEL_OPTION,JOptionPane.QUESTION_MESSAGE,null,
					new Object[]{rb.getString("ButtonYes"),rb.getString("ButtonNo"),
					rb.getString("ButtonYesPlus")},rb.getString("ButtonNo"));
			if (selectedValue == 1) return(false);   // return without creating if "No" response
			if (selectedValue == 2) {
				// Suppress future warnings, and continue
				noWarnLevelXing = true;
			}
		}
		// remove from selection information
		if (selectedObject==(Object)o) selectedObject = null;
		if (prevSelectedObject==(Object)o) prevSelectedObject = null;
		// remove connections if any
		TrackSegment t = (TrackSegment)o.getConnectA();
		if (t!=null) removeTrackSegment(t);
		t = (TrackSegment)o.getConnectB();
		if (t!=null) removeTrackSegment(t);
		t = (TrackSegment)o.getConnectC();
		if (t!=null) removeTrackSegment(t);
		t = (TrackSegment)o.getConnectD();
		if (t!=null) removeTrackSegment(t);
		// decrement block use count if any blocks in use
		LayoutBlock lb = o.getLayoutBlockAC();
		if (lb != null) lb.decrementUse();
		LayoutBlock lbx = o.getLayoutBlockBD();
		if ( (lbx != null) && (lbx!=lb) ) lb.decrementUse();
		// delete from array
		for (int i = 0; i<xingList.size();i++) {
			LevelXing lx = (LevelXing)xingList.get(i);
			if (lx==o) {
				// found object
				xingList.remove(i);
				o.remove();
				panelChanged = true;
				repaint();
				return(true);
			}
		}
		return(false);	
	}

    /**
     * Remove a Track Segment 
     */
	protected void removeTrackSegment(TrackSegment o) {
		// remove any connections
		int type = o.getType1();
		if (type==POS_POINT) {
			PositionablePoint p = (PositionablePoint)(o.getConnect1());
			if (p!=null) p.removeTrackConnection(o);
		}
		else {
			disconnect(o.getConnect1(),type);
		}
		type = o.getType2();
		if (type==POS_POINT) {
			PositionablePoint p = (PositionablePoint)(o.getConnect2());
			if (p!=null) p.removeTrackConnection(o);
		}
		else {
			disconnect(o.getConnect2(),type);
		}
		// decrement Block use count
		LayoutBlock b = o.getLayoutBlock();
		if (b!=null) b.decrementUse();
		// delete from array
		for (int i = 0; i<trackList.size();i++) {
			TrackSegment t = (TrackSegment)trackList.get(i);
			if (t==o) {
				// found object
				trackList.remove(i);
				panelChanged = true;
				repaint();
				return;
			}
		}	
	}
	
	private void disconnect(Object o, int type) {
		if (o==null) return;
		switch (type) {
			case TURNOUT_A:
				((LayoutTurnout)o).setConnectA(null,NONE);
				break;
			case TURNOUT_B:
				((LayoutTurnout)o).setConnectB(null,NONE);
				break;
			case TURNOUT_C:
				((LayoutTurnout)o).setConnectC(null,NONE);
				break;
			case TURNOUT_D:
				((LayoutTurnout)o).setConnectD(null,NONE);
				break;
			case LEVEL_XING_A:
				((LevelXing)o).setConnectA(null,NONE);
				break;
			case LEVEL_XING_B:
				((LevelXing)o).setConnectB(null,NONE);
				break;
			case LEVEL_XING_C:
				((LevelXing)o).setConnectC(null,NONE);
				break;
			case LEVEL_XING_D:
				((LevelXing)o).setConnectD(null,NONE);
				break;
		}
	}
	
    /**
     * Add a sensor indicator to the Draw Panel
     */
    void addSensor() {
		if ((nextSensor.getText()).trim().length()<=0) {
			JOptionPane.showMessageDialog(targetPanel,rb.getString("Error10"),
						rb.getString("Error"),JOptionPane.ERROR_MESSAGE);
			return;
		}
        LayoutSensorIcon l = new LayoutSensorIcon();
        l.setActiveIcon(sensorIconEditor.getIcon(0));
        l.setInactiveIcon(sensorIconEditor.getIcon(1));
        l.setInconsistentIcon(sensorIconEditor.getIcon(2));
        l.setUnknownIcon(sensorIconEditor.getIcon(3));
		l.setSensor(nextSensor.getText().trim());
		Sensor xSensor = l.getSensor();
		if (xSensor != null) {
			if ( (xSensor.getUserName()==null) || 
					(!(xSensor.getUserName().equals(nextSensor.getText().trim()))) ) 
				nextSensor.setText(xSensor.getSystemName());
		}
        setNextLocation(l);
		panelChanged = true;
        putSensor(l);
    }
    public void putSensor(LayoutSensorIcon l) {
        l.invalidate();
        targetPanel.add(l, SENSORS);
		l.connect(this);
        contents.add(l);
        // reshow the panel
        targetPanel.validate();
    }

    /**
     * Add a signal head to the Panel
     */
    void addSignalHead() {
		// check for valid signal head entry
		String tName = nextSignalHead.getText().trim();
        SignalHead mHead = null;
		if ( (tName!=null) && (tName!="") ) {
			mHead = InstanceManager.signalHeadManagerInstance().getBySystemName(tName.toUpperCase());
			if (mHead == null) 
				mHead = InstanceManager.signalHeadManagerInstance().getByUserName(tName);
			else 
				nextSignalHead.setText(mHead.getSystemName());
		}
        if (mHead == null) {
			// There is no signal head corresponding to this name
			JOptionPane.showMessageDialog(thisPanel,
					java.text.MessageFormat.format(rb.getString("Error9"),
					new String[]{tName}),
					rb.getString("Error"),JOptionPane.ERROR_MESSAGE);
			return;
		}
		// create and set up signal icon	
        LayoutSignalHeadIcon l = new LayoutSignalHeadIcon();
        l.setRedIcon(signalIconEditor.getIcon(0));
        l.setFlashRedIcon(signalIconEditor.getIcon(1));
        l.setYellowIcon(signalIconEditor.getIcon(2));
        l.setFlashYellowIcon(signalIconEditor.getIcon(3));
        l.setGreenIcon(signalIconEditor.getIcon(4));
        l.setFlashGreenIcon(signalIconEditor.getIcon(5));
        l.setDarkIcon(signalIconEditor.getIcon(6));
        l.setHeldIcon(signalIconEditor.getIcon(7));
        l.setSignalHead(nextSignalHead.getText().trim());
		SignalHead xSignal = l.getSignalHead();
		if (xSignal != null) {
			if ( !(xSignal.getUserName().equals(nextSignalHead.getText().trim())) ) {
				nextSignalHead.setText(xSignal.getSystemName());
			}
		}
        setNextLocation(l);
		panelChanged = true;
        putSignal(l);
    }
    public void putSignal(LayoutSignalHeadIcon l) {
        l.invalidate();
        targetPanel.add(l, SIGNALS);
		l.connect(this);
        contents.add(l);
		signalList.add(l);
        // reshow the panel
        targetPanel.validate();
    }

    /**
     * Add a label to the Draw Panel
     */
    void addLabel() {
        LayoutPositionableLabel l = new LayoutPositionableLabel(
											textLabel.getText().trim());
        setNextLocation(l);
        l.setSize(l.getPreferredSize().width, l.getPreferredSize().height);
        l.setDisplayLevel(LABELS);
		panelChanged = true;
        putLabel(l);
    }
    public void putLabel(LayoutPositionableLabel l) {
        l.invalidate();
        targetPanel.add(l, l.getDisplayLevel());
		if ( ((l.getDisplayLevel()).intValue()) != (BKG.intValue()) ) {
			l.connect(this);
		}
		else {
			backgroundImage = l;
		}	
        contents.add(l);
        targetPanel.validate();
    }

    /**
     * Add a memory label to the Draw Panel
     */
    void addMemory() {
		if ((textMemory.getText()).trim().length()<=0) {
			JOptionPane.showMessageDialog(targetPanel,rb.getString("Error11"),
						rb.getString("Error"),JOptionPane.ERROR_MESSAGE);
			return;
		}
        LayoutMemoryIcon l = new LayoutMemoryIcon();
        l.setMemory(textMemory.getText().trim());
		Memory xMemory = l.getMemory();
		if (xMemory != null) {
			if ( (xMemory.getUserName() == null) || 
					(!(xMemory.getUserName().equals(textMemory.getText().trim())))  ) {
				// put the system name in the memory field
				textMemory.setText(xMemory.getSystemName());
			}
		}
        setNextLocation(l);
        l.setSize(l.getPreferredSize().width, l.getPreferredSize().height);
        l.setDisplayLevel(LABELS);
		panelChanged = true;
        putLabel(l);
    }

    /**
     * Add an icon to the target
     */
    void addIcon() {
        LayoutPositionableLabel l = new LayoutPositionableLabel(iconEditor.getIcon(0) );
        setNextLocation(l);
        l.setDisplayLevel(ICONS);
		panelChanged = true;
        putLabel(l);
    }
	
	/** 
	 * Add a background image
	 */
	public void addBackground() {
        JFileChooser inputFileChooser = new JFileChooser("Select Background Image");
        int retVal = inputFileChooser.showOpenDialog(this);
        if (retVal != JFileChooser.APPROVE_OPTION) return;  // give up if no file selected
        NamedIcon icon = new NamedIcon(inputFileChooser.getSelectedFile().getPath(),
                                       inputFileChooser.getSelectedFile().getPath());
        LayoutPositionableLabel l = new LayoutPositionableLabel(icon);
        l.setFixed(true);
        l.setShowTooltip(false);
        l.setSize(icon.getIconWidth(), icon.getIconHeight());
        l.setDisplayLevel(BKG);
        putLabel(l);
	}

    /**
     * Invoke a window to allow you to add a MultiSensor indicator to the target
     */
	private int multiLocX;
	private int multiLocY;
    void startMultiSensor() {
		multiLocX = xLoc;
		multiLocY = yLoc;
        if (multiSensorFrame == null) {
            // create a common edit frame
            multiSensorFrame = new MultiSensorIconFrame(this);
            multiSensorFrame.initComponents();
            multiSensorFrame.pack();
        }  
        multiSensorFrame.setVisible(true);
    }
    // Invoked when window has new multi-sensor ready
    public void addMultiSensor(MultiSensorIcon l) {
		l.setLocation(multiLocX,multiLocY);
		panelChanged = true;
        putMultiSensor(l);
    }
    // invoked to install the multi-sensor
    public void putMultiSensor(MultiSensorIcon l) {
        l.invalidate();
		l.setViewCoordinates(true);
        targetPanel.add(l, l.getDisplayLevel());
        contents.add(l);
        // reshow the panel
        targetPanel.validate();
    }

    /**
     * Set object location and size for icon and label object as it is created.
     * Size comes from the preferredSize; location comes
     * from the fields where the user can spec it.
     */
    void setNextLocation(JComponent obj) {
        obj.setLocation(xLoc,yLoc);
    }
	
	/**
	 * Called by JmriJFrame when window is closing.  
	 * Do not delete the Panel at this time.
	 */
	public void dispose() {
		jmri.jmrit.display.PanelMenu.instance().updateLayoutEditorPanel(this);
	}
	
	/** 
	 * Invoked by DeletePanel menu item
	 *     Validate user intent before deleting
	 */
	public void deletePanel() {
		// verify deletion
		int selectedValue = JOptionPane.showOptionDialog(targetPanel,
				rbx.getString("QuestionA")+"\n"+rbx.getString("QuestionB"),
				rbx.getString("DeleteVerifyTitle"),JOptionPane.YES_NO_OPTION,
				JOptionPane.QUESTION_MESSAGE,null,
				new Object[]{rbx.getString("ButtonYesDelete"),rbx.getString("ButtonNoCancel")},
				rbx.getString("ButtonNoCancel"));
		if (selectedValue == 1) return;   // return without deleting if "No" response
		
		// delete panel - deregister the panel for saving 
        InstanceManager.configureManagerInstance().deregister(this);
		jmri.jmrit.display.PanelMenu.instance().deletePanel((Object)this);
		setVisible(false);		
		// clean up local links
        contents.clear();
		turnoutList.clear();
		trackList.clear();
		pointList.clear();
		xingList.clear();
        targetPanel = null;

        // clean up GUI aspects
        this.removeAll();
        super.dispose();
    }

    /**
     *  Control whether panel items are positionable.
     * @param state true for positionable.
     */
    public void setAllPositionable(boolean state) {
        if (positionableItem.isSelected()!=state) positionableItem.setSelected(state);
        positionable = state;
        for (int i = 0; i<contents.size(); i++) {
            ((Positionable)contents.get(i)).setPositionable(state);
        }
    }

    /**
     *  Control whether target panel items are editable.
     *  Does this by invoke the {@link Positionable#setEditable} function of
     *  each item on the target panel. This also controls the relevant pop-up menu items
     *  (which are the primary way that items are edited).
     * @param state true for editable.
     */
    public void setAllEditable(boolean state) {
        for (int i = 0; i<contents.size(); i++) {
            ((Positionable)contents.get(i)).setEditable(state);
        }
    }

    /**
     *  Control whether target panel items are controlling layout items.
     *  Does this by invoke the {@link Positionable#setControlling} function of
     *  each item on the target panel. This also controls the relevant pop-up menu items.
     * @param state true for controlling.
     */
    public void setAllControlling(boolean state) {
        if (controlItem.isSelected()!=state) controlItem.setSelected(state);
        controlLayout = state;
        for (int i = 0; i<contents.size(); i++) {
            ((Positionable)contents.get(i)).setControlling(state);
        }
    }
	
	// accessor routines for persistent information
    public boolean isEditable() {
        return editMode;
    }
	public void setEditable(boolean state) {
		editMode = state;
		editModeItem.setSelected(editMode);
		setEditMode(editMode);
	}
    public boolean isPositionable() {
        return positionable;
    }
    public boolean isControlling() {
        return controlLayout;
    }
	public int getLayoutWidth() {return panelWidth;}
	public int getLayoutHeight() {return panelHeight;}
	public int getUpperLeftX() {return upperLeftX;}
	public int getUpperLeftY() {return upperLeftY;}
	public int getMainlineTrackWidth() {
		int wid = (int)mainlineTrackWidth;
		return wid;
	}
	public int getSideTrackWidth() {
		int wid = (int)sideTrackWidth;
		return wid;
	}
	public double getXScale() {return xScale;}
	public double getYScale() {return yScale;}
	public String getDefaultTrackColor() {return colorToString(defaultTrackColor);}
	public String getLayoutName() {return layoutName;}
	public boolean getShowHelpBar() {return showHelpBar;}
	public void setLayoutDimensions(int w, int h, int x, int y) {
		upperLeftX = x;
		upperLeftY = y;
		setLocation(x,y);
		panelWidth = w;
		panelHeight = h;
		targetPanel.setSize(w-18,h-60);
		log.debug("Position - "+upperLeftX+","+upperLeftY+" SetSize - "+panelWidth+","+panelHeight);		
	}
	public void setMainlineTrackWidth(int w) {mainlineTrackWidth = w;}
	public void setSideTrackWidth(int w) {sideTrackWidth = w;}
	public void setDefaultTrackColor(String color) {
		defaultTrackColor = stringToColor(color);
		setOptionMenuTrackColor();
	}
	public void setXScale(double xSc) {xScale = xSc;}
	public void setYScale(double ySc) {yScale = ySc;}
	public void setLayoutName(String name) {layoutName = name;}
	public void setShowHelpBar(boolean state) {
		if (showHelpBar!=state) {
			showHelpBar = state;
			showHelpItem.setSelected(showHelpBar);
			if (editMode) {
				helpBar.setVisible(showHelpBar);
			}
		}
	}
	// final initialization routine for loading a LayoutEditor
	public void setConnections() {
		// initialize TrackSegments if any
		if (trackList.size()>0) {
			for (int i = 0; i<trackList.size(); i++) {
				((TrackSegment)trackList.get(i)).setObjects(this);
			}
		}
		// initialize PositionablePoints if any
		if (pointList.size()>0) {
			for (int i = 0; i<pointList.size(); i++) {
				((PositionablePoint)pointList.get(i)).setObjects(this);
			}
		}
		// initialize LevelXings if any
		if (xingList.size()>0) {
			for (int i = 0; i<xingList.size(); i++) {
				((LevelXing)xingList.get(i)).setObjects(this);
			}
		}
		// initialize LayoutTurnouts if any
		if (turnoutList.size()>0) {
			for (int i = 0; i<turnoutList.size(); i++) {
				((LayoutTurnout)turnoutList.get(i)).setObjects(this);
			}
		}
		resetDirty();
	}
	// utility routines
	public static String colorToString(Color color) {
		if(color == Color.black) return "black";
		else if (color == Color.darkGray) return "darkGray";
		else if (color == Color.gray) return "gray";
		else if (color == Color.lightGray) return "lightGray";
		else if (color == Color.white) return "white";
		else if (color == Color.red) return "red";
		else if (color == Color.pink) return "pink";
		else if (color == Color.orange) return "orange";
		else if (color == Color.yellow) return "yellow";
		else if (color == Color.green) return "green";
		else if (color == Color.blue) return "blue";
		else if (color == Color.magenta) return "magenta";
		else if (color == Color.cyan) return "cyan";
		log.error ("unknown color sent to colorToString");
		return "black";
	}
	public static Color stringToColor(String string) {
		if(string.equals("black")) return Color.black;
		else if (string.equals("darkGray")) return Color.darkGray;	
		else if (string.equals("gray")) return Color.gray;	
		else if (string.equals("lightGray")) return Color.lightGray;	
		else if (string.equals("white")) return Color.white;	
		else if (string.equals("red")) return Color.red;	
		else if (string.equals("pink")) return Color.pink;	
		else if (string.equals("orange")) return Color.orange;	
		else if (string.equals("yellow")) return Color.yellow;	
		else if (string.equals("green")) return Color.green;
		else if (string.equals("blue")) return Color.blue;	
		else if (string.equals("magenta")) return Color.magenta;	
		else if (string.equals("cyan")) return Color.cyan;	
		log.error("unknown color text '"+string+"' sent to stringToColor");
		return Color.black;
	}
	public TrackSegment findTrackSegmentByName(String name) {
		if (name.length()<=0) return null;
		for (int i = 0; i<trackList.size(); i++) {
			TrackSegment t = (TrackSegment)trackList.get(i);
			if (t.getID().equals(name)) {
				return t;
			}
		}
		return null;
	}
	public  PositionablePoint findPositionablePointByName(String name) {
		if (name.length()<=0) return null;
		for (int i = 0; i<pointList.size(); i++) {
			PositionablePoint p = (PositionablePoint)pointList.get(i);
			if (p.getID().equals(name)) {
				return p;
			}
		}
		return null;
	}
	public  LayoutTurnout findLayoutTurnoutByName(String name) {
		if (name.length()<=0) return null;
		for (int i = 0; i<turnoutList.size(); i++) {
			LayoutTurnout t = (LayoutTurnout)turnoutList.get(i);
			if (t.getName().equals(name)) {
				return t;
			}
		}
		return null;
	}
	public  LevelXing findLevelXingByName(String name) {
		if (name.length()<=0) return null;
		for (int i = 0; i<xingList.size(); i++) {
			LevelXing x = (LevelXing)xingList.get(i);
			if (x.getID().equals(name)) {
				return x;
			}
		}
		return null;
	}
	public Object findObjectByTypeAndName(int type,String name) {
		if (name.length()<=0) return null;
		switch (type) {
			case NONE:
				return null;
			case POS_POINT:
				return (Object)findPositionablePointByName(name);
			case TURNOUT_A:
			case TURNOUT_B:
			case TURNOUT_C:
			case TURNOUT_D:
				return (Object)findLayoutTurnoutByName(name);
			case LEVEL_XING_A:
			case LEVEL_XING_B:
			case LEVEL_XING_C:
			case LEVEL_XING_D:
				return (Object)findLevelXingByName(name);
			case TRACK:
				return (Object)findTrackSegmentByName(name);
		}
		log.error("did not find Object '"+name+"' of type "+type);
		return null;
	}

    /**
     *  Special internal class to allow drawing of layout to a JLayeredPane
     *  This is the 'target' pane where the layout is displayed
     */
    class LayoutPane extends JLayeredPane 
    {
        public void paint(Graphics g) {
            super.paint(g);
            Graphics2D g2 = (Graphics2D)g;
            g2.setColor(defaultTrackColor);			
			main = false;
            g2.setStroke(new BasicStroke(sideTrackWidth,BasicStroke.CAP_ROUND,BasicStroke.JOIN_ROUND));
            drawHiddenTrack(g2);
			drawDashedTrack(g2,false);
			drawDashedTrack(g2,true);
			drawSolidTrack(g2,false);
			drawSolidTrack(g2,true);
            drawTurnouts(g2);
			drawXings(g2);
			drawTrackInProgress(g2);
            g2.setStroke(new BasicStroke(1.0F,BasicStroke.CAP_ROUND,BasicStroke.JOIN_ROUND));
			drawPoints(g2);
			if (editMode) {
				drawTurnoutRects(g2);
				drawXingRects(g2);
				drawTrackOvals(g2);
				drawSelectionRect(g2);
			}
        }
    }
	
	boolean main = true;
	float trackWidth = sideTrackWidth;
	
	protected void setTrackStrokeWidth(Graphics2D g2, boolean need) {
		if (main == need) return;
		main = need;
		// change track stroke width
		if ( main ) {
			trackWidth = mainlineTrackWidth;
			g2.setStroke(new BasicStroke(mainlineTrackWidth,BasicStroke.CAP_ROUND,
															BasicStroke.JOIN_ROUND));
		}
		else {
			trackWidth = sideTrackWidth;
			g2.setStroke(new BasicStroke(sideTrackWidth,BasicStroke.CAP_ROUND,
															BasicStroke.JOIN_ROUND));
		}
	}
		
	protected void drawTurnouts(Graphics2D g2)
	{
		float trackWidth = sideTrackWidth;
		// loop over all defined turnouts
		for (int i = 0; i<turnoutList.size();i++) {
			LayoutTurnout t = (LayoutTurnout)turnoutList.get(i);
			LayoutBlock b = t.getLayoutBlock();
			if (b!=null) {
				g2.setColor(b.getBlockColor());
			}
			else {
				g2.setColor(defaultTrackColor);
			}
			if (t.getTurnoutType()==LayoutTurnout.DOUBLE_XOVER) {
				// double crossover turnout
				Turnout t1 = t.getTurnout();
				if (t1==null) {
					// no physical turnout linked - draw A corner
					setTrackStrokeWidth(g2,t.isMainlineA());
					g2.draw(new Line2D.Double(t.getCoordsA(),
										midpoint(t.getCoordsA(),t.getCoordsB())));
					setTrackStrokeWidth(g2,false);
					g2.draw(new Line2D.Double(t.getCoordsA(),
										midpoint(t.getCoordsA(),t.getCoordsC())));
					// change block if needed
					b = t.getLayoutBlockB();
					if (b!=null) g2.setColor(b.getBlockColor());
					else g2.setColor(defaultTrackColor);
					// draw B corner
					setTrackStrokeWidth(g2,t.isMainlineB());
					g2.draw(new Line2D.Double(t.getCoordsB(),
										midpoint(t.getCoordsA(),t.getCoordsB())));
					setTrackStrokeWidth(g2,false);
					g2.draw(new Line2D.Double(t.getCoordsB(),
										midpoint(t.getCoordsB(),t.getCoordsD())));
					// change block if needed
					b = t.getLayoutBlockC();
					if (b!=null) g2.setColor(b.getBlockColor());
					else g2.setColor(defaultTrackColor);
					// draw C corner
					setTrackStrokeWidth(g2,t.isMainlineC());
					g2.draw(new Line2D.Double(t.getCoordsC(),
										midpoint(t.getCoordsC(),t.getCoordsD())));
					setTrackStrokeWidth(g2,false);
					g2.draw(new Line2D.Double(t.getCoordsC(),
										midpoint(t.getCoordsA(),t.getCoordsC())));
					// change block if needed
					b = t.getLayoutBlockD();
					if (b!=null) g2.setColor(b.getBlockColor());
					else g2.setColor(defaultTrackColor);
					// draw D corner
					setTrackStrokeWidth(g2,t.isMainlineD());
					g2.draw(new Line2D.Double(t.getCoordsD(),
										midpoint(t.getCoordsC(),t.getCoordsD())));
					setTrackStrokeWidth(g2,false);
					g2.draw(new Line2D.Double(t.getCoordsD(),
										midpoint(t.getCoordsB(),t.getCoordsD())));
				}
				else {
					int state = t1.getKnownState();
					if ( state == Turnout.CLOSED ) {
						// continuing path - not crossed over
						setTrackStrokeWidth(g2,t.isMainlineA());
						g2.draw(new Line2D.Double(t.getCoordsA(),
												midpoint(t.getCoordsA(),t.getCoordsB())));
						setTrackStrokeWidth(g2,false);
						g2.draw(new Line2D.Double(t.getCoordsA(),
												third(t.getCoordsA(),t.getCoordsC())));
						b = t.getLayoutBlockB();
						if (b!=null) g2.setColor(b.getBlockColor());
						else g2.setColor(defaultTrackColor);
						setTrackStrokeWidth(g2,t.isMainlineB());
						g2.draw(new Line2D.Double(t.getCoordsB(),
												midpoint(t.getCoordsA(),t.getCoordsB())));
						setTrackStrokeWidth(g2,false);
						g2.draw(new Line2D.Double(t.getCoordsB(),
												third(t.getCoordsB(),t.getCoordsD())));
						b = t.getLayoutBlockC();
						if (b!=null) g2.setColor(b.getBlockColor());
						else g2.setColor(defaultTrackColor);
						setTrackStrokeWidth(g2,t.isMainlineC());
						g2.draw(new Line2D.Double(t.getCoordsC(),
												midpoint(t.getCoordsC(),t.getCoordsD())));
						setTrackStrokeWidth(g2,false);
						g2.draw(new Line2D.Double(t.getCoordsC(),
												third(t.getCoordsC(),t.getCoordsA())));
						b = t.getLayoutBlockD();
						if (b!=null) g2.setColor(b.getBlockColor());
						else g2.setColor(defaultTrackColor);
						setTrackStrokeWidth(g2,t.isMainlineD());
						g2.draw(new Line2D.Double(t.getCoordsD(),
												midpoint(t.getCoordsC(),t.getCoordsD())));
						setTrackStrokeWidth(g2,false);
						g2.draw(new Line2D.Double(t.getCoordsD(),
												third(t.getCoordsD(),t.getCoordsB())));
					}
					else if (state == Turnout.THROWN) {
						// diverting (crossed) path 
						setTrackStrokeWidth(g2,t.isMainlineA());
						g2.draw(new Line2D.Double(t.getCoordsA(),
												third(t.getCoordsA(),t.getCoordsB())));
						setTrackStrokeWidth(g2,false);
						g2.draw(new Line2D.Double(t.getCoordsA(),t.getCoordsCenter()));
						b = t.getLayoutBlockB();
						if (b!=null) g2.setColor(b.getBlockColor());
						else g2.setColor(defaultTrackColor);
						setTrackStrokeWidth(g2,t.isMainlineB());
						g2.draw(new Line2D.Double(t.getCoordsB(),
												third(t.getCoordsB(),t.getCoordsA())));
						setTrackStrokeWidth(g2,false);
						g2.draw(new Line2D.Double(t.getCoordsB(),t.getCoordsCenter()));
						b = t.getLayoutBlockC();
						if (b!=null) g2.setColor(b.getBlockColor());
						else g2.setColor(defaultTrackColor);
						setTrackStrokeWidth(g2,t.isMainlineC());
						g2.draw(new Line2D.Double(t.getCoordsC(),
												third(t.getCoordsC(),t.getCoordsD())));
						setTrackStrokeWidth(g2,false);
						g2.draw(new Line2D.Double(t.getCoordsC(),t.getCoordsCenter()));
						b = t.getLayoutBlockD();
						if (b!=null) g2.setColor(b.getBlockColor());
						else g2.setColor(defaultTrackColor);
						setTrackStrokeWidth(g2,t.isMainlineD());
						g2.draw(new Line2D.Double(t.getCoordsD(),
												third(t.getCoordsD(),t.getCoordsC())));
						setTrackStrokeWidth(g2,false);
						g2.draw(new Line2D.Double(t.getCoordsD(),t.getCoordsCenter()));
					}
					else {
						// unknown or inconsistent
						setTrackStrokeWidth(g2,t.isMainlineA());
						g2.draw(new Line2D.Double(t.getCoordsA(),
												third(t.getCoordsA(),t.getCoordsB())));
						setTrackStrokeWidth(g2,false);
						g2.draw(new Line2D.Double(t.getCoordsA(),
												third(t.getCoordsA(),t.getCoordsC())));
						b = t.getLayoutBlockB();
						if (b!=null) g2.setColor(b.getBlockColor());
						else g2.setColor(defaultTrackColor);
						setTrackStrokeWidth(g2,t.isMainlineB());
						g2.draw(new Line2D.Double(t.getCoordsB(),
												third(t.getCoordsB(),t.getCoordsA())));
						setTrackStrokeWidth(g2,false);
						g2.draw(new Line2D.Double(t.getCoordsB(),
												third(t.getCoordsB(),t.getCoordsD())));
						b = t.getLayoutBlockC();
						if (b!=null) g2.setColor(b.getBlockColor());
						else g2.setColor(defaultTrackColor);
						setTrackStrokeWidth(g2,t.isMainlineC());
						g2.draw(new Line2D.Double(t.getCoordsC(),
												third(t.getCoordsC(),t.getCoordsD())));
						setTrackStrokeWidth(g2,false);
						g2.draw(new Line2D.Double(t.getCoordsC(),
												third(t.getCoordsC(),t.getCoordsA())));
						b = t.getLayoutBlockD();
						if (b!=null) g2.setColor(b.getBlockColor());
						else g2.setColor(defaultTrackColor);
						setTrackStrokeWidth(g2,t.isMainlineD());
						g2.draw(new Line2D.Double(t.getCoordsD(),
												third(t.getCoordsD(),t.getCoordsC())));
						setTrackStrokeWidth(g2,false);
						g2.draw(new Line2D.Double(t.getCoordsD(),
												third(t.getCoordsD(),t.getCoordsB())));
					}
				}
			}
			else {
				// LH, RH, or WYE Turnouts
				Turnout t2 = t.getTurnout();
				if (t2==null) {
					// no physical turnout linked - draw connected
					setTrackStrokeWidth(g2,t.isMainlineA());
					g2.draw(new Line2D.Double(t.getCoordsA(),t.getCoordsCenter()));
					setTrackStrokeWidth(g2,t.isMainlineB());
					g2.draw(new Line2D.Double(t.getCoordsB(),t.getCoordsCenter()));
					setTrackStrokeWidth(g2,t.isMainlineC());
					g2.draw(new Line2D.Double(t.getCoordsC(),t.getCoordsCenter()));
				}
				else {
					setTrackStrokeWidth(g2,t.isMainlineA());
					g2.draw(new Line2D.Double(t.getCoordsA(),t.getCoordsCenter()));
					switch (t2.getKnownState()) {
						case Turnout.CLOSED:
							if (t.getContinuingSense()==Turnout.CLOSED) {
								setTrackStrokeWidth(g2,t.isMainlineB());
								g2.draw(new Line2D.Double(t.getCoordsB(),t.getCoordsCenter()));
								setTrackStrokeWidth(g2,t.isMainlineC());
								g2.draw(new Line2D.Double(t.getCoordsC(),
											midpoint(t.getCoordsCenter(),t.getCoordsC())));
							}
							else { 
								setTrackStrokeWidth(g2,t.isMainlineC());
								g2.draw(new Line2D.Double(t.getCoordsC(),t.getCoordsCenter()));
								setTrackStrokeWidth(g2,t.isMainlineB());
								g2.draw(new Line2D.Double(t.getCoordsB(),
											midpoint(t.getCoordsCenter(),t.getCoordsB())));
							}
							break;
						case Turnout.THROWN:
							if (t.getContinuingSense()==Turnout.THROWN) {
								setTrackStrokeWidth(g2,t.isMainlineB());
								g2.draw(new Line2D.Double(t.getCoordsB(),t.getCoordsCenter()));
								setTrackStrokeWidth(g2,t.isMainlineC());
								g2.draw(new Line2D.Double(t.getCoordsC(),
											midpoint(t.getCoordsCenter(),t.getCoordsC())));
							}
							else { 
								setTrackStrokeWidth(g2,t.isMainlineC());
								g2.draw(new Line2D.Double(t.getCoordsC(),t.getCoordsCenter()));
								setTrackStrokeWidth(g2,t.isMainlineB());
								g2.draw(new Line2D.Double(t.getCoordsB(),
											midpoint(t.getCoordsCenter(),t.getCoordsB())));
							}
							break;
						default:
							// inconsistent or unknown
							setTrackStrokeWidth(g2,t.isMainlineC());
							g2.draw(new Line2D.Double(t.getCoordsC(),
											midpoint(t.getCoordsCenter(),t.getCoordsC())));							
							setTrackStrokeWidth(g2,t.isMainlineB());
							g2.draw(new Line2D.Double(t.getCoordsB(),
											midpoint(t.getCoordsCenter(),t.getCoordsB())));
					}
				}
			}
		}
	}
	
	private Point2D midpoint (Point2D p1,Point2D p2) {
		return new Point2D.Double((p1.getX()+p2.getX())/2.0,(p1.getY()+p2.getY())/2.0);
	}
	
	private Point2D third (Point2D p1,Point2D p2) {
		return new Point2D.Double( p1.getX()+((p2.getX()-p1.getX())/3.0),
						p1.getY()+((p2.getY()-p1.getY())/3.0) );
	}
	
	private void drawXings(Graphics2D g2)
	{
		// loop over all defined level crossings
		for (int i = 0; i<xingList.size();i++) {
			LevelXing x = (LevelXing)xingList.get(i);
			if ( x.isMainlineBD() && (!x.isMainlineAC()) ) {
				drawXingAC(g2,x);
				drawXingBD(g2,x);
			}
			else {
				drawXingBD(g2,x);
				drawXingAC(g2,x);
			}				
		}
	}
	private void drawXingAC(Graphics2D g2,LevelXing x) {
		// set color - check for an AC block
		LayoutBlock b = x.getLayoutBlockAC();
		if (b!=null) {
			g2.setColor(b.getBlockColor());
		}
		else {
			g2.setColor(defaultTrackColor);
		}
		// set track width for AC block
		setTrackStrokeWidth(g2,x.isMainlineAC());
		// draw AC segment	
		g2.draw(new Line2D.Double(x.getCoordsA(),x.getCoordsC()));
	}
	private void drawXingBD(Graphics2D g2,LevelXing x) {
		// set color - check for an BD block
		LayoutBlock b = x.getLayoutBlockBD();
		if (b!=null) {
			g2.setColor(b.getBlockColor());
		}
		else {
			g2.setColor(defaultTrackColor);
		}
		// set track width for BD block
		setTrackStrokeWidth(g2,x.isMainlineBD());
		// draw BD segment	
		g2.draw(new Line2D.Double(x.getCoordsB(),x.getCoordsD()));
	}
	
	private void drawTurnoutRects(Graphics2D g2)
	{
		// loop over all defined turnouts
		for (int i = 0; i<turnoutList.size();i++) {
			LayoutTurnout t = (LayoutTurnout)turnoutList.get(i);
			Point2D pt = t.getCoordsCenter();
			g2.setColor(defaultTrackColor);
			g2.draw(new Ellipse2D.Double (
							pt.getX()-SIZE2, pt.getY()-SIZE2, SIZE2+SIZE2, SIZE2+SIZE2));
			pt = t.getCoordsA();
 			if (t.getConnectA()==null) {
				g2.setColor(Color.red);
			}
			else {
				g2.setColor(Color.green);
			}
			g2.draw(new Rectangle2D.Double (
							pt.getX()-SIZE, pt.getY()-SIZE, SIZE2, SIZE2));
			pt = t.getCoordsB();
 			if (t.getConnectB()==null) {
				g2.setColor(Color.red);
			}
			else {
				g2.setColor(Color.green);
			}
			g2.draw(new Rectangle2D.Double (
							pt.getX()-SIZE, pt.getY()-SIZE, SIZE2, SIZE2));
			pt = t.getCoordsC();
 			if (t.getConnectC()==null) {
				g2.setColor(Color.red);
			}
			else {
				g2.setColor(Color.green);
			}
			g2.draw(new Rectangle2D.Double (
							pt.getX()-SIZE, pt.getY()-SIZE, SIZE2, SIZE2));
			if (t.getTurnoutType()==LayoutTurnout.DOUBLE_XOVER) {
				pt = t.getCoordsD();
				if (t.getConnectD()==null) {
					g2.setColor(Color.red);
				}
				else {
					g2.setColor(Color.green);
				}
				g2.draw(new Rectangle2D.Double (
							pt.getX()-SIZE, pt.getY()-SIZE, SIZE2, SIZE2));
			}
		}
	}
	
	private void drawXingRects(Graphics2D g2)
	{
		// loop over all defined level crossings
		for (int i = 0; i<xingList.size();i++) {
			LevelXing x = (LevelXing)xingList.get(i);
			Point2D pt = x.getCoordsCenter();
			g2.setColor(defaultTrackColor);
			g2.draw(new Ellipse2D.Double (
							pt.getX()-SIZE2, pt.getY()-SIZE2, SIZE2+SIZE2, SIZE2+SIZE2));
			pt = x.getCoordsA();
 			if (x.getConnectA()==null) {
				g2.setColor(Color.red);
			}
			else {
				g2.setColor(Color.green);
			}
			g2.draw(new Rectangle2D.Double (
							pt.getX()-SIZE, pt.getY()-SIZE, SIZE2, SIZE2));
			pt = x.getCoordsB();
 			if (x.getConnectB()==null) {
				g2.setColor(Color.red);
			}
			else {
				g2.setColor(Color.green);
			}
			g2.draw(new Rectangle2D.Double (
							pt.getX()-SIZE, pt.getY()-SIZE, SIZE2, SIZE2));
			pt = x.getCoordsC();
 			if (x.getConnectC()==null) {
				g2.setColor(Color.red);
			}
			else {
				g2.setColor(Color.green);
			}
			g2.draw(new Rectangle2D.Double (
							pt.getX()-SIZE, pt.getY()-SIZE, SIZE2, SIZE2));
			pt = x.getCoordsD();
 			if (x.getConnectD()==null) {
				g2.setColor(Color.red);
			}
			else {
				g2.setColor(Color.green);
			}
			g2.draw(new Rectangle2D.Double (
							pt.getX()-SIZE, pt.getY()-SIZE, SIZE2, SIZE2));
		}
	}
	
	private void drawHiddenTrack(Graphics2D g2)
	{
		for (int i = 0; i<trackList.size();i++) {
			TrackSegment t = (TrackSegment)trackList.get(i);
			if (editMode && t.getHidden()) {
				LayoutBlock b = t.getLayoutBlock();
				if (b!=null) g2.setColor(b.getBlockColor());
				else g2.setColor(defaultTrackColor);
				g2.setStroke(new BasicStroke(1.0F,BasicStroke.CAP_ROUND,BasicStroke.JOIN_ROUND));
				g2.draw(new Line2D.Double(getCoords(t.getConnect1(),t.getType1()),
										getCoords(t.getConnect2(),t.getType2())));
				setTrackStrokeWidth(g2,!main);
			}
		}
	}
	private void drawDashedTrack(Graphics2D g2, boolean mainline)
	{
		for (int i = 0; i<trackList.size();i++) {
			TrackSegment t = (TrackSegment)trackList.get(i);
			if ( (!t.getHidden()) && t.getDashed() && (mainline == t.getMainline()) ) {		
				LayoutBlock b = t.getLayoutBlock();
				if (b!=null) g2.setColor(b.getBlockColor());
				else g2.setColor(defaultTrackColor);
				setTrackStrokeWidth(g2,mainline);
				Point2D end1 = getCoords(t.getConnect1(),t.getType1());
				Point2D end2 = getCoords(t.getConnect2(),t.getType2());
				double delX = end1.getX() - end2.getX();
				double delY = end1.getY() - end2.getY();
				double cLength = Math.sqrt( (delX*delX) + (delY*delY) );
				// note: The preferred dimension of a dash (solid + blank space) is 
				//         5 * the track width - about 60% solid and 40% blank.
				int nDashes = (int)( cLength / (((double)trackWidth)*5.0) );
				if (nDashes < 3) nDashes = 3;
				double delXDash = -delX/( ((double)nDashes) - 0.5 );
				double delYDash = -delY/( ((double)nDashes) - 0.5 );
				double begX = end1.getX();
				double begY = end1.getY();
				for (int k = 0; k<nDashes; k++) {
					g2.draw(new Line2D.Double(new Point2D.Double(begX,begY),
						new Point2D.Double((begX+(delXDash*0.5)),(begY+(delYDash*0.5)))));
					begX += delXDash;
					begY += delYDash;
				}
			}
		}
	}
	private void drawSolidTrack(Graphics2D g2, boolean mainline)
	{
		for (int i = 0; i<trackList.size();i++) {
			TrackSegment t = (TrackSegment)trackList.get(i);
			if ( (!t.getHidden()) && (!t.getDashed()) && (mainline == t.getMainline()) ) {		
				LayoutBlock b = t.getLayoutBlock();
				if (b!=null) g2.setColor(b.getBlockColor());
				else g2.setColor(defaultTrackColor);
				setTrackStrokeWidth(g2,mainline);
				g2.draw(new Line2D.Double(getCoords(t.getConnect1(),t.getType1()),
										getCoords(t.getConnect2(),t.getType2())));
			}
		}
	}	
	private void drawTrackInProgress(Graphics2D g2)
	{
		// check for segment in progress
		if ( editMode && (beginObject!=null) && trackBox.isSelected() ) {
			g2.setColor(defaultTrackColor);
			setTrackStrokeWidth(g2,false);
			g2.draw(new Line2D.Double(beginLocation,currentLocation));
		}
	}

	private void drawTrackOvals(Graphics2D g2)
	{
		// loop over all defined track segments
		g2.setColor(defaultTrackColor);
		for (int i = 0; i<trackList.size();i++) {
			TrackSegment t = (TrackSegment)trackList.get(i);
			Point2D pt1 = getCoords(t.getConnect1(),t.getType1());
			Point2D pt2 = getCoords(t.getConnect2(),t.getType2());
			double cX = (pt1.getX() + pt2.getX())/2.0D;
			double cY = (pt1.getY() + pt2.getY())/2.0D;			
			g2.draw(new Ellipse2D.Double (cX-SIZE2, cY-SIZE2, SIZE2+SIZE2, SIZE2+SIZE2));
		}
	}

	private void drawPoints(Graphics2D g2)
	{
		for (int i = 0; i<pointList.size();i++) {
			PositionablePoint p = (PositionablePoint)pointList.get(i);
			switch (p.getType()) {
				case PositionablePoint.ANCHOR:
					// nothing to draw unless in edit mode
					if (editMode) {
						// in edit mode, draw locater rectangle
						Point2D pt = p.getCoords();
						if ((p.getConnect1()==null) || (p.getConnect2()==null)) {
							g2.setColor(Color.red);
						}
						else {
							g2.setColor(Color.green);
						}
						g2.draw(new Rectangle2D.Double (
									pt.getX()-SIZE, pt.getY()-SIZE, SIZE2, SIZE2));
					}
					break;
				case PositionablePoint.END_BUMPER:
					// nothing to draw unless in edit mode
					if (editMode) {
						// in edit mode, draw locater rectangle
						Point2D pt = p.getCoords();
						if (p.getConnect1()==null) {
							g2.setColor(Color.red);
						}
						else {
							g2.setColor(Color.green);
						}
						g2.draw(new Rectangle2D.Double (
									pt.getX()-SIZE, pt.getY()-SIZE, SIZE2, SIZE2));
					}
					break;
				default:
					log.error("Illegal type of Positionable Point");
			}
		}
	}

	private void drawSelectionRect(Graphics2D g2) {
		if ( selectionActive && (selectionWidth!=0.0) && (selectionHeight!=0.0) ){
			g2.setColor(defaultTrackColor);
			g2.setStroke(new BasicStroke(1.0F,BasicStroke.CAP_ROUND,BasicStroke.JOIN_ROUND));
			g2.draw(new Rectangle2D.Double (selectionX, selectionY, selectionWidth, selectionHeight));
		}
	}

	protected Point2D getCoords(Object o, int type) {
		if (o != null) {
			switch (type) {
				case POS_POINT:
					return ((PositionablePoint)o).getCoords();
				case TURNOUT_A:
					return ((LayoutTurnout)o).getCoordsA();
				case TURNOUT_B:
					return ((LayoutTurnout)o).getCoordsB();
				case TURNOUT_C:
					return ((LayoutTurnout)o).getCoordsC();
				case TURNOUT_D:
					return ((LayoutTurnout)o).getCoordsD();
				case LEVEL_XING_A:
					return ((LevelXing)o).getCoordsA();
				case LEVEL_XING_B:
					return ((LevelXing)o).getCoordsB();
				case LEVEL_XING_C:
					return ((LevelXing)o).getCoordsC();
				case LEVEL_XING_D:
					return ((LevelXing)o).getCoordsD();
			}
		}
		else {
			log.error("Null connection point of type "+type);
		}
		return (new Point2D.Double(0.0,0.0));
	}
	
    // initialize logging
    static org.apache.log4j.Category log = org.apache.log4j.Category.getInstance(LayoutEditor.class.getName());
}
