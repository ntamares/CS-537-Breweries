

// ===========================================================================
// PACKAGE DEFINITION
// ===========================================================================
//package Breweries ;


//===========================================================================
// IMPORTS
//===========================================================================
import com.esri.mo2.cs.geom.*;
import com.esri.mo2.data.feat.*;
import com.esri.mo2.file.shp.*;
import com.esri.mo2.map.dpy.FeatureLayer;
import com.esri.mo2.map.dpy.BaseFeatureLayer;
import com.esri.mo2.map.dpy.Layerset;
import com.esri.mo2.map.draw.* ;
import com.esri.mo2.ui.bean.*;
import com.esri.mo2.ui.dlg.*;
import com.esri.mo2.ui.ren.LayerProperties;
import com.esri.mo2.ui.tb.ZoomPanToolBar;
import com.esri.mo2.ui.tb.SelectionToolBar;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.io.*;
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.Vector;
import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumn;


//===========================================================================
// CLASS: Breweries
// DESCRIPTION: Main class for application.
//===========================================================================
public class Breweries extends JFrame {

	// Simple Variables
	static boolean fullMap = true;
	static boolean helpToolOn = true;
	double distance;
	int activeLayerIndex;
	String s1 = "C:/Breweries/data/sdcounty.shp";
	String s2 = "C:/Breweries/data/breweries.shp";
	String datapathname = "";
	String legendname = "";
	private ArrayList helpText = new ArrayList(3);
	static HelpTool helptool = new HelpTool() ;

	// Setup the Listeners here...
	ActionListener lis;
	ActionListener layerlis;
	ActionListener layercontrollis;
	ComponentListener complistener;

	// Object Variables
	static Map map = new Map();
	Legend legend;
	Legend legend2;
	Layer layer2 = new Layer();
	Layer layer = new Layer();
	Layer layer3 = new Layer();
	Layer newlayer = new Layer() ;
	static AcetateLayer acetLayer;
	static com.esri.mo2.map.dpy.Layer layer4;
	com.esri.mo2.map.dpy.Layer activeLayer;
	com.esri.mo2.cs.geom.Point initPoint, endPoint;
	Toc toc = new Toc();
	TocAdapter mytocadapter;
	static Envelope env;

	// Setup the Menu items here...
	JMenuBar mbar = new JMenuBar();
	JMenu file = new JMenu("File");
	JMenu theme = new JMenu("Theme");
	JMenu layercontrol = new JMenu("Layer Control");
	JMenu help = new JMenu("Help");
	JMenu help2 = new JMenu("Help Topics");

	// Setup ImageIcons for use later (mostly for code readabilty)
	ImageIcon clico = new ImageIcon("C:/Breweries/img/icons/clico.jpg");
	ImageIcon tvico = new ImageIcon("C:/Breweries/img/icons/tableview.jpg") ;
	ImageIcon dslico = new ImageIcon("C:/Breweriesimg/icons/demote.jpg") ;
	ImageIcon fmpico = new ImageIcon("C:/Breweries/img/icons/print.jpg") ;
	ImageIcon alico = new ImageIcon("C:/Breweries/img/icons/addtheme.gif") ;
	ImageIcon rlico = new ImageIcon("C:/Breweries/img/icons/delete.jpg") ;
	ImageIcon leico = new ImageIcon("C:/Breweries/img/icons/properties.jpg") ;
	ImageIcon helpico = new ImageIcon("C:/Breweries/img/icons/help2.png");
	static ImageIcon pslico = new ImageIcon("C:/Breweries/img/icons/promote.jpg") ;

	// Associate the above ImageIcons to JMenuIcons now...
	JMenuItem attribitem = new JMenuItem("Open Attribute Table", tvico );
	JMenuItem cpolylayer  = new JMenuItem("Create Polygon Layer From Selection",
										  clico );
	JMenuItem cpointlayer  = new JMenuItem("Create Point Layer From Selection",
										  clico );
	JMenuItem demoteitem = new JMenuItem("Demote Selected Layer", dslico );
	JMenuItem printitem = new JMenuItem("Print", fmpico );
	JMenuItem alitem = new JMenuItem("Add Layer", alico );
	JMenuItem remlyritem = new JMenuItem("Remove Layer", rlico );
	JMenuItem propsitem = new JMenuItem("Legend Editor", leico );
	static JMenuItem pitem = new JMenuItem("Promote Selected Layer", pslico );
	JMenuItem aboutitem = new JMenuItem("About", helpico );
	JMenuItem contactusitem = new JMenuItem("Contact Us", helpico );
	JMenuItem helptoolitem = new JMenuItem("Help Tool", helpico );
	JMenuItem h2toc = new JMenuItem("Table of Contents Help", helpico);
	JMenuItem h2le = new JMenuItem("Legend Editor Help", helpico);
	JMenuItem h2lc = new JMenuItem("Layer Control Help", helpico);

	// Setup the Toolbars next...
	ZoomPanToolBar zptb = new ZoomPanToolBar();
	static SelectionToolBar stb = new SelectionToolBar();
	JToolBar jtb = new JToolBar();

	// Setup the Statusbar next...
	JLabel statusLabel = new JLabel("status bar    LOC");
	static JLabel milesLabel = new JLabel("   DIST:  0 mi    ");
	static JLabel kmLabel = new JLabel("  0 km    ");
	java.text.DecimalFormat df = new java.text.DecimalFormat("0.000");

	// Setup the CustomToolbar here...
	JPanel myjp = new JPanel();
	JPanel myjp2 = new JPanel();
	JButton prtjb = new JButton(new ImageIcon("C:/Breweries/img/icons/print.jpg"));
	JButton addlyrjb = new JButton(new ImageIcon("C:/Breweries/img/icons/addtheme.gif"));
	JButton ptrjb = new JButton(new ImageIcon("C:/Breweries/img/icons/pointer.jpg"));
	JButton distjb = new JButton(new ImageIcon("C:/Breweries/img/icons/measure_1.jpg"));
	JButton XYjb = new JButton("XY");
	JButton hotjb = new JButton(new ImageIcon("C:/Breweries/img/icons/hotlink.gif"));
	JButton helptooljb = new JButton(new ImageIcon("C:/Breweries/img/icons/help2.png"));

	// Setup the hotlink tool here...
	Toolkit tk = Toolkit.getDefaultToolkit();
	Image bolt = tk.getImage("C:/Breweries/img/icons/hotlink_32x32-32.gif");
	java.awt.Cursor boltCursor = tk.createCustomCursor(bolt,
										new java.awt.Point(6,30),"bolt");

	// PickAdapter for the hotlink tool...
	MyPickAdapter picklis = new MyPickAdapter();
	Identify hotlink = new Identify();

	//===========================================================================
	// CLASS: MyPickAdapter
	// DESCRIPTION: hotlink tool listener implemented by Indentity class.
	//===========================================================================
	class MyPickAdapter implements PickListener {
		MouseEvent me ;
		int x ;
		int y ;

		//=========================================================================
		// FUNCTION: beginPick()
		// DESCRIPTION: This code runs when the user clicks the mouse when hotlink
		//              tool is enabled.
		//=========================================================================
		public void beginPick(PickEvent pe){
			System.out.println("begin pick");
			me = pe.getMouseEvent() ;
			x = me.getX() ;
			y = me.getY() ;
		}


		//=========================================================================
		// FUNCTION: foundData()
		// DESCRIPTION: This code runs when the user has clicked near a feature on
		//              the active layer.
		//=========================================================================
		public void foundData(PickEvent pe){

			String fname = "" ;
			String fsvalue ;
			int fivalue = 0;

			System.out.println("hola pick x,y = " + x + "," + y);
			com.esri.mo2.cs.geom.Point point = map.transformPixelToWorld(x,y);
			com.esri.mo2.data.feat.Cursor cursor = pe.getCursor();

			// Pull feature attribute information and pass to hotlink tool...
			Row row = null ;
			while (cursor.hasMore()) {
				row = (com.esri.mo2.data.feat.Row)cursor.next();
				Fields fields = row.getFields() ;
				Field field = fields.getField(1) ;
				fname = field.getName() ;
				fsvalue = row.getDisplayValue(1) ;
				fivalue = Integer.parseInt(fsvalue);
			}

			// Skip the sdcounty base layer which has GIS data...
			String actlayername = map.getLayer(activeLayerIndex).getName();
			if ( ! new String("sdcounty").equals(actlayername) ) {

				// Lookup this point in the array of points in our current layer...
				// If True, call function to invoke JPanel Window Create...
				try {
					HotPick hotpick = new HotPick(fivalue, row,
											      activeLayerIndex,
											      map);
					hotpick.setVisible(true);
				} catch(Exception e){
					System.err.println(e);
					e.printStackTrace();
				}
			}
		}


		//=========================================================================
		// FUNCTION: endPick()
		// DESCRIPTION: This code runs after the user has clicked.  It resets the
		//              hotlink tool to null and forces the user to click it again.
		//=========================================================================
		public void endPick(PickEvent pe){
			System.out.println("end pick");
			hotlink.setCursor(java.awt.Cursor.getPredefinedCursor(
													java.awt.Cursor.DEFAULT_CURSOR));
			map.setSelectedTool(null);
		}
	} ;


	//=========================================================================
	// FUNCTION: Breweries()
	// DESCRIPTION: Main Application Constructor
	//=========================================================================
	public Breweries() {

		// Top level variables...
		super("Breweries");
		helpToolOn = false ;
		this.setBounds(50,50,900,900);
		zptb.setMap(map);
		stb.setMap(map);
		setJMenuBar(mbar);

		// A special zoom listener to handle setting full map boolean to false...
		ActionListener lisZoom = new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				fullMap = false;
			}
		};

		// A special zoom listener to handle setting full map boolean to true...
		ActionListener lisFullExt = new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				fullMap = true;
			}
		};

		// Zoom tool bar button configuration (and add listeners from above) here...
		JButton zoomInButton = (JButton)zptb.getActionComponent("ZoomIn");
		JButton zoomFullExtentButton =
				(JButton)zptb.getActionComponent("ZoomToFullExtent");
		JButton zoomToSelectedLayerButton =
				(JButton)zptb.getActionComponent("ZoomToSelectedLayer");
		zoomInButton.addActionListener(lisZoom);
		zoomFullExtentButton.addActionListener(lisFullExt);
		zoomToSelectedLayerButton.addActionListener(lisZoom);

		// Create & add resize component listener...
		complistener = new ComponentAdapter () {
			public void componentResized(ComponentEvent ce) {
				if(fullMap) {
					map.setExtent(env);
					map.zoom(1.0);
					map.redraw();
				}
			}
		};
		addComponentListener(complistener);

	    // Create listeners for each button in the toolbar to handle right click when
	    // HelpTool is enabled...
	    // Print Button
	    MouseAdapter lishelptoolprt = new MouseAdapter() {
	    	public void mousePressed(MouseEvent me) {
	    		if (SwingUtilities.isRightMouseButton(me) && helpToolOn) {
	    			try {
	    				HelpDialog hd = new HelpDialog((String)helpText.get(9)) ;
	    				hd.setVisible(true);
	    			} catch(IOException e) {
	    				System.err.println(e);
						e.printStackTrace();
	    			}
	    		}
	    	}
	    };
	    prtjb.addMouseListener(lishelptoolprt);

	    // Add Layer Button
	    MouseAdapter lishelptooladdlyr = new MouseAdapter() {
	    	public void mousePressed(MouseEvent me) {
	    		if (SwingUtilities.isRightMouseButton(me) && helpToolOn) {
	    			try {
	    				HelpDialog hd = new HelpDialog((String)helpText.get(10)) ;
	    				hd.setVisible(true);
	    			} catch(IOException e) {
	    				System.err.println(e);
						e.printStackTrace();
	    			}
	    		}
	    	}
	    };
	    addlyrjb.addMouseListener(lishelptooladdlyr);

	    // Arrow Button
	    MouseAdapter lishelptoolptr = new MouseAdapter() {
	    	public void mousePressed(MouseEvent me) {
	    		if (SwingUtilities.isRightMouseButton(me) && helpToolOn) {
	    			try {
	    				HelpDialog hd = new HelpDialog((String)helpText.get(11)) ;
	    				hd.setVisible(true);
	    			} catch(IOException e) {
	    				System.err.println(e);
						e.printStackTrace();
	    			}
	    		}
	    	}
	    };
	    ptrjb.addMouseListener(lishelptoolptr);

	    // Distance Tool Button
	    MouseAdapter lishelptooldist = new MouseAdapter() {
	    	public void mousePressed(MouseEvent me) {
	    		if (SwingUtilities.isRightMouseButton(me) && helpToolOn) {
	    			try {
	    				HelpDialog hd = new HelpDialog((String)helpText.get(5)) ;
	    				hd.setVisible(true);
	    			} catch(IOException e) {
	    				System.err.println(e);
						e.printStackTrace();
	    			}
	    		}
	    	}
	    };
	    distjb.addMouseListener(lishelptooldist);

	    // XY Tool Button
	    MouseAdapter lishelptoolxy = new MouseAdapter() {
	    	public void mousePressed(MouseEvent me) {
	    		if (SwingUtilities.isRightMouseButton(me) && helpToolOn) {
	    			try {
	    				HelpDialog hd = new HelpDialog((String)helpText.get(7)) ;
	    				hd.setVisible(true);
	    			} catch(IOException e) {
	    				System.err.println(e);
						e.printStackTrace();
	    			}
	    		}
	    	}
	    };
	    XYjb.addMouseListener(lishelptoolxy);

	    // HotLink Tool Button
	    MouseAdapter lishelptoolhot = new MouseAdapter() {
	    	public void mousePressed(MouseEvent me) {
	    		if (SwingUtilities.isRightMouseButton(me) && helpToolOn) {
	    			try {
	    				HelpDialog hd = new HelpDialog((String)helpText.get(8)) ;
	    				hd.setVisible(true);
	    			} catch(IOException e) {
	    				System.err.println(e);
						e.printStackTrace();
	    			}
	    		}
	    	}
	    };
	    hotjb.addMouseListener(lishelptoolhot);

	    // HelpTool  Button
	    MouseAdapter lishelptoolhelp = new MouseAdapter() {
	    	public void mousePressed(MouseEvent me) {
	    		if (SwingUtilities.isRightMouseButton(me) && helpToolOn) {
	    			try {
	    				HelpDialog hd = new HelpDialog((String)helpText.get(3)) ;
	    				hd.setVisible(true);
	    			} catch(IOException e) {
	    				System.err.println(e);
						e.printStackTrace();
	    			}
	    		}
	    	}
	    };
	    helptooljb.addMouseListener(lishelptoolhelp);

		// Custom Tool Bar case statement next...
		lis = new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				Object source = ae.getSource();
				if (source == prtjb || source instanceof JMenuItem ) {
					com.esri.mo2.ui.bean.Print mapPrint =
							new com.esri.mo2.ui.bean.Print();
					mapPrint.setMap(map);
					mapPrint.doPrint();
				} else if (source == ptrjb) {
					Arrow arrow = new Arrow();
					map.setSelectedTool(arrow);
				} else if (source == distjb) {
					DistanceTool distanceTool = new DistanceTool();
					map.setSelectedTool(distanceTool);
				} else if (source == XYjb) {
					try {
						AddXYtheme addXYtheme = new AddXYtheme();
						addXYtheme.setMap(map);
						addXYtheme.setVisible(false);
						map.redraw();
					} catch (IOException e) { }
				} else if (source == hotjb) {
					hotlink.setCursor(boltCursor);
					map.setSelectedTool(hotlink);
				} else if (source == helptooljb){
					helpToolOn = true ;
					map.setSelectedTool(helptool);
				} else {
					try {
						AddLyrDialog aldlg = new AddLyrDialog();
						aldlg.setMap(map);
						aldlg.setVisible(true);
					} catch(IOException e) { }
				}
			}
		};
	    hotlink.setPickWidth(10);
		hotlink.addPickListener(picklis);
	    XYjb.addActionListener(lis);
	    hotjb.addActionListener(lis);
	    prtjb.addActionListener(lis);
	    ptrjb.addActionListener(lis);
	    distjb.addActionListener(lis);
	    addlyrjb.addActionListener(lis);
	    printitem.addActionListener(lis);
	    helptooljb.addActionListener(lis);

		// Create and configure the promote and demote listener now...
		layercontrollis = new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				String source = ae.getActionCommand();
				System.out.println(activeLayerIndex+" active index");
				if (source == "Promote Selected Layer") {
					map.getLayerset().moveLayer(activeLayerIndex,++activeLayerIndex);
				} else {
					map.getLayerset().moveLayer(activeLayerIndex,--activeLayerIndex);
					enableDisableButtons();
					map.redraw();
				}
			}
		};
	    pitem.addActionListener(layercontrollis);
	    demoteitem.addActionListener(layercontrollis);



		// Main program listener is created and attached to the
	    // buttons in the toolbar now...
		layerlis = new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				Object source = ae.getSource();
				if (source instanceof JMenuItem) {
					String arg = ae.getActionCommand();
					if(arg == "Add Layer") {
						try {
							AddLyrDialog aldlg = new AddLyrDialog();
							aldlg.setMap(map);
							aldlg.setVisible(true);
						} catch(IOException e){}
					} else if(arg == "About") {
						AboutBox ab = new AboutBox();
						ab.setProductName("Breweries");
						ab.setProductVersion("1.0");
						ab.setVisible(true) ;
					} else if (arg == "Contact Us"){
						try {
							String s = "\nPlease send any questions to: " ;
							s = s + "tamares@rohan.sdsu.edu." ;
							HelpDialog hd = new HelpDialog(s) ;
							hd.setVisible(true);
						} catch (IOException e) {}
					} else if (arg == "Help Tool"){
						try {
							HelpDialog hd = new HelpDialog((String)helpText.get(3)) ;
							hd.setVisible(true);
						} catch (IOException e) {}
					} else if (arg == "Table of Contents Help"){
						try {
							HelpDialog hd = new HelpDialog((String)helpText.get(0)) ;
							hd.setVisible(true);
						} catch (IOException e) {}
					} else if (arg == "Legend Editor Help"){
						try {
							HelpDialog hd = new HelpDialog((String)helpText.get(1)) ;
							hd.setVisible(true);
						} catch (IOException e) {}
					} else if (arg == "Layer Control Help"){
						try {
							HelpDialog hd = new HelpDialog((String)helpText.get(2)) ;
							hd.setVisible(true);
						} catch (IOException e) {}
					} else if(arg == "Remove Layer") {
						try {
							com.esri.mo2.map.dpy.Layer dpylayer = legend.getLayer();
							map.getLayerset().removeLayer(dpylayer);
							map.redraw();
							remlyritem.setEnabled(false);
							propsitem.setEnabled(false);
							attribitem.setEnabled(false);
							pitem.setEnabled(false);
							demoteitem.setEnabled(false);
							stb.setSelectedLayer(null);
							stb.setSelectedLayers(null);
							zptb.setSelectedLayer(null);
						} catch(Exception e) {}
					} else if(arg == "Legend Editor") {
						LayerProperties lp = new LayerProperties();
						lp.setLegend(legend);
						lp.setSelectedTabIndex(0);
						lp.setVisible(true);
					} else if (arg == "Open Attribute Table") {
						try {
							layer4 = legend.getLayer();
							AttrTab attrtab = new AttrTab();
							attrtab.setVisible(true);
						} catch(IOException ioe){}
					} else if (arg=="Create Polygon Layer From Selection" ||
						arg=="Create Point Layer From Selection" ) {

						int layertype ;
						com.esri.mo2.map.draw.BaseSimpleRenderer sbr = new
								com.esri.mo2.map.draw.BaseSimpleRenderer();

						// For Polygons
					    com.esri.mo2.map.draw.SimpleFillSymbol simplepolysymbol = new
				            	com.esri.mo2.map.draw.SimpleFillSymbol();
						simplepolysymbol.setSymbolColor(new Color(255,255,0));
			            simplepolysymbol.setType(
			            	com.esri.mo2.map.draw.SimpleFillSymbol.FILLTYPE_SOLID);
			            simplepolysymbol.setBoundary(true);

						// For Points
			            com.esri.mo2.map.draw.SimpleMarkerSymbol simplepointsymbol =
			            		new com.esri.mo2.map.draw.SimpleMarkerSymbol();
			            simplepointsymbol.setType( SimpleMarkerSymbol.STAR_MARKER );
						simplepointsymbol.setSymbolColor(new Color(255,255,0));
			            simplepointsymbol.setWidth(12);

						if ( arg =="Create Polygon Layer From Selection") {
							layertype = 2;
						} else {
							layertype = 0;
						}

						layer4 = legend.getLayer();
						FeatureLayer flayer2 = (FeatureLayer)layer4;
						System.out.println("has selected " + flayer2.hasSelection());
						if (flayer2.hasSelection()) {
							SelectionSet selectset = flayer2.getSelectionSet();
							FeatureLayer selectedlayer =
									flayer2.createSelectionLayer(selectset);
							sbr.setLayer(selectedlayer);
							if ( layertype == 2 ) {
								sbr.setSymbol(simplepolysymbol);
							} else {
								sbr.setSymbol(simplepointsymbol);
							}
							selectedlayer.setRenderer(sbr);
							Layerset layerset = map.getLayerset();

							layerset.addLayer(selectedlayer);

							if(stb.getSelectedLayers() != null) {
								pitem.setEnabled(true);
			            	}
							try {
								legend2 = toc.findLegend(selectedlayer);
			            	} catch (Exception e) {}

							CreateShapeDialog csd =
									new CreateShapeDialog(selectedlayer,layertype);
							csd.setVisible(true);
							Flash flash = new Flash(legend2);
							flash.start();
							map.redraw();
						}
					}
				}
			}
		};
	    alitem.addActionListener(layerlis);
	    propsitem.addActionListener(layerlis);
	    attribitem.addActionListener(layerlis);
	    cpolylayer.addActionListener(layerlis);
	    remlyritem.addActionListener(layerlis);
	    cpointlayer.addActionListener(layerlis);
	    aboutitem.addActionListener(layerlis);
	    contactusitem.addActionListener(layerlis);
	    helptoolitem.addActionListener(layerlis);
	    h2toc.addActionListener(layerlis);
	    h2le.addActionListener(layerlis);
	    h2lc.addActionListener(layerlis);

		// TOC Listener to set active layer here...
		toc.setMap(map);
		mytocadapter = new TocAdapter() {
			public void click(TocEvent e) {
				System.out.println(activeLayerIndex + " active layer index");
				legend = e.getLegend();
				activeLayer = legend.getLayer();
				stb.setSelectedLayer(activeLayer);
				zptb.setSelectedLayer(activeLayer);
				activeLayerIndex = map.getLayerset().indexOf(activeLayer);
				com.esri.mo2.map.dpy.Layer[] layers = {activeLayer};
				hotlink.setSelectedLayers(layers);

				System.out.println(activeLayerIndex + " active index");
				remlyritem.setEnabled(true);
				propsitem.setEnabled(true);
				attribitem.setEnabled(true);
				enableDisableButtons();
			}
		};
		toc.addTocListener(mytocadapter);

		map.addMouseMotionListener(
			new MouseMotionAdapter() {
				public void mouseMoved(MouseEvent me) {
					com.esri.mo2.cs.geom.Point worldPoint = null;
					if (map.getLayerCount() > 0) {
						worldPoint = map.transformPixelToWorld(me.getX(),me.getY());
						String s = "X:"+df.format(worldPoint.getX())+" "+
							"Y:"+df.format(worldPoint.getY());
						statusLabel.setText(s);
					} else {
						statusLabel.setText("X:0.000 Y:0.000");
					}
				}
			}
		);

		// Default application properties for default application behaviors...
		remlyritem.setEnabled(false);
		propsitem.setEnabled(false);
		attribitem.setEnabled(false);
		pitem.setEnabled(false);
	    demoteitem.setEnabled(false);

	    // Build the File menu now...
	    file.add(alitem);
	    file.add(printitem);
	    file.add(remlyritem);
	    file.add(propsitem);

	    // Build the Theme menu now...
	    theme.add(attribitem);
	    theme.add(cpolylayer);
	    theme.add(cpointlayer);

	    // Build the Layer Control menu now...
	    layercontrol.add(pitem);
	    layercontrol.add(demoteitem);

	    // Build the Help menu now...
	    setuphelpText();
	    help2.add(h2toc);
	    help2.add(h2le);
	    help2.add(h2lc);
	    help.add(help2);
	    help.add(helptoolitem);
	    help.add(contactusitem);
	    help.add(aboutitem);

	    // Now add those menus to the menubar itself...
	    mbar.add(file);
	    mbar.add(theme);
	    mbar.add(layercontrol);
	    mbar.add(help);

	    // Add some helpful tool tips next...
	    prtjb.setToolTipText("print map");
	    addlyrjb.setToolTipText("add layer");
	    ptrjb.setToolTipText("arrow tool");
	    distjb.setToolTipText("press-drag-release to measure a distance");
	    XYjb.setToolTipText("add a layer of points from a file");
	    hotjb.setToolTipText("hotlink tool--click somthing to maybe see a picture");
	    helptooljb.setToolTipText("click this button, "
	    		+ "then right click any toolbar item for help");

	    // Attach those buttons to the tool bar now...
	    jtb.add(prtjb);
	    jtb.add(addlyrjb);
	    jtb.add(ptrjb);
	    jtb.add(distjb);
	    jtb.add(XYjb);
	    jtb.add(hotjb);
	    jtb.add(helptooljb);

	    // Add these various objects to the jpanels now...
	    myjp.add(jtb);
	    myjp.add(zptb);
	    myjp.add(stb);
	    myjp2.add(statusLabel);
	    myjp2.add(milesLabel);
	    myjp2.add(kmLabel);

	    // Add a window splitter for usability...
	    JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,toc,map) ;
	    splitPane.setOneTouchExpandable(true) ;

	    // Configure the layout for the jpanel and then add the maps...
	    getContentPane().add(map, BorderLayout.CENTER);
	    getContentPane().add(myjp,BorderLayout.NORTH);
	    getContentPane().add(myjp2,BorderLayout.SOUTH);
	    addShapefileToMap(layer,s1);
	    addShapefileToMap(layer2,s2);

	    // Set Breweries reviewed to red...
	    java.util.List list = toc.getAllLegends();
	    com.esri.mo2.map.dpy.Layer lay1 = ((Legend)list.get(0)).getLayer();
	    FeatureLayer flayer1 = (FeatureLayer)lay1;
	    BaseSimpleRenderer bsr1 = (BaseSimpleRenderer)flayer1.getRenderer();
		/*TrueTypeMarkerSymbol ttm = new TrueTypeMarkerSymbol() ;
		ttm.setFont(new Font("ESRI Crime Analysis", Font.PLAIN, 24));
		ttm.setColor(new Color(255,0,0));
		ttm.setCharacter("107") ;
		bsr1.setSymbol(ttm) ;
		*/
		com.esri.mo2.map.draw.RasterMarkerSymbol sms = null; // for points
		sms = new com.esri.mo2.map.draw.RasterMarkerSymbol();
		sms.setSizeX(30);
		sms.setSizeY(30);
		sms.setImageString("C:/Breweries/img/icons/beer1.png");
		bsr1.setSymbol(sms);

	    // Set base polygon for sdcounty to grey...
	    com.esri.mo2.map.dpy.Layer lay2 = ((Legend)list.get(1)).getLayer();
	    FeatureLayer flayer2 = (FeatureLayer)lay2;
	    BaseSimpleRenderer bsr2 = (BaseSimpleRenderer)flayer2.getRenderer();
	    SimplePolygonSymbol sym2 = (SimplePolygonSymbol)bsr2.getSymbol();
	    sym2.setPaint(
	    		AoFillStyle.getPaint(com.esri.mo2.map.draw.AoFillStyle.SOLID_FILL,
	    		                     new java.awt.Color(90,120,127)));
	    bsr2.setSymbol(sym2);

	    // Display the rest now...
	    getContentPane().add(splitPane, BorderLayout.WEST);
	    map.setExtent(env);
		map.zoom(1.0);
		map.redraw();
	}


	//=========================================================================
	// FUNCTION: addShapefileToMap()
	// DESCRIPTION: Adds a shp file to the map
	//=========================================================================
	private void addShapefileToMap(Layer layer, String s) {
		String datapath = s;
		layer.setDataset("0;"+datapath);
		map.add(layer);
	}

	//=========================================================================
	// FUNCTION: enableDisableButtons()
	// DESCRIPTION: Depending on what's happening in the application these
	//              global variables need to be set/reset accordingly.
	//=========================================================================
	private void enableDisableButtons() {
		int layerCount = map.getLayerset().getSize();
		if (layerCount < 2) {
			pitem.setEnabled(false);
			demoteitem.setEnabled(false);
		} else if (activeLayerIndex == 0) {
			demoteitem.setEnabled(false);
			pitem.setEnabled(true);
		} else if (activeLayerIndex == layerCount - 1) {
			pitem.setEnabled(false);
			demoteitem.setEnabled(true);
		} else {
			pitem.setEnabled(true);
			demoteitem.setEnabled(true);
		}
	}


	//=========================================================================
	// FUNCTION: setuphelpText()
	// DESCRIPTION: Just a helper function to handle strings and help for the
	//              user
	//=========================================================================
	private void setuphelpText() {
		String s0 = "The toc, or table of contents, is to the left of the map.\n"
	    + "Each entry is called a 'legend' and represents a map 'layer' or\n"
	    + "'theme'.  If you click on a legend, that layer is called the\n"
	    + "active layer, or selected layer.  Its display (rendering) properties\n"
	    + "can be controlled using the Legend Editor, and the legends can be\n"
	    + "reordered using Layer Control.  Both Legend Editor and Layer Control\n"
	    + "are separate Help Topics.  This line is e...x...t...e...n...t...e...d"
	    + "to test the scrollpane.";
		helpText.add(s0);
		String s1 = "The Legend Editor is a menu item found under the File menu.\n"
	    + "Given that a layer is selected by clicking on its legend in the table"
	    + " of contents, clicking on Legend Editor will open a window giving you"
	    + " choices about how to display that layer.  For example you can control"
	    + " the color used to display the layer on the map, or whether to use"
	    + " multiple colors";
		helpText.add(s1);
		String s2 = "Layer Control is a Menu on the menu bar.  If you have selected"
		+ " a layer by clicking on a legend in the toc (table of contents) to the"
		+ " left of the map, then the promote and demote tools will become"
		+ " usable.  Clicking on promote will raise the selected legend one"
		+ " position higher in the toc, and clicking on demote will lower that"
		+ " legend one position in the toc.";
		helpText.add(s2);
		String s3 = "This tool will allow you to learn about certain other tools.\n"
	    + "You begin with a standard left mouse button click on the Help Tool"
	    + " itself.\nRIGHT click on another tool and a window may give you"
	    + "information about the intended usage of the tool.\nClick on the"
	    + " arrow tool to stop using the help tool.";
		helpText.add(s3);
		String s4 = "If you click on the Zoom In tool, and then click on the map,"
		+ "you\nwill see a part of the map in greater detail.  You can zoom in"
		+ " multiple times. You can also sketch a rectangular part of the map,"
		+ " and zoom to that.  You can\nundo a Zoom In with a Zoom Out or"
		+ " with a Zoom to Full Extent";
		helpText.add(s4);
		String s5 = "You must have a selected layer to use the Zoom to Active"
		+ "Layer tool.\n"
	    + "If you then click on Zoom to Active Layer, you will be shown enough of\n"
	    + "the full map to see all of the features in the layer you select.  If"
	    + " you\nselect a layer that shows where glaciers are, then you do not"
	    + " need to\nsee Hawaii, or any southern states, so you will see"
	    + "Alaska, and northern\nmainland states.";
		helpText.add(s5);
		String s6 = "If you click on the MOJO MeasureTool Measuring Tool and then.\n"
		+ "you click anywhere on the map, and drag and release.\n"
		+ "This will give the distance between the mouse click and release.";
	    helpText.add(s6);
	    String s7 = "If you click on the XY icon tool it will allow us to add a CSV"
	    + "file.\n If you click on the XY, it will let us to move to the "
	    + "path where csv file is stored.  Once we add the csv file, the "
	    + "points considered in that file will be displayed on the map";
		helpText.add(s7);
		String s8 = "If you click on Bolt also called Hotlink Tool\n "
		+ "is a pointer tool, if you click on any points on \n map you can see\n"
		+ "information about that point" ;
		helpText.add(s8);
		String s9 = "If you click on the printer button the native dialog box "
		+ "for print will open and you can print your map." ;
		helpText.add(s9);
		String s10 = "If you click on the add layer button a dialog box will appear"
		+ "which you are expected to browse to a shp file on your hard drive"
		+ " and load it into the application." ;
		helpText.add(s10);
		String s11 = "If you click on the Arrow Tool it will reset the mouse cursor"
		+ " and handlers to default values, clearing any user selections." ;
		helpText.add(s11);
	}


	//=========================================================================
	// FUNCTION: main()
	// DESCRIPTION: main function
	//=========================================================================
	public static void main(String[] args) {
		System.out.println("Showing Breweries!");
		Breweries qstart = new Breweries();
		qstart.addWindowListener(
			new WindowAdapter() {
				public void windowClosing(WindowEvent e) {
					System.exit(0);
				}
			}
		);
		qstart.setVisible(true);
		env = map.getExtent();
	}
}


//===========================================================================
// CLASS: AddLyrDialog
// DESCRIPTION: This class handles when the user selects the AddLayer option
//              to add a layer to the map.
//===========================================================================
class AddLyrDialog extends JDialog {
	Map map;
	ActionListener lis;
	JButton ok = new JButton("OK");
	JButton cancel = new JButton("Cancel");
	JPanel panel1 = new JPanel();
	com.esri.mo2.ui.bean.CustomDatasetEditor cus =
			new com.esri.mo2.ui.bean.CustomDatasetEditor();

	AddLyrDialog() throws IOException {

		setBounds(50,50,520,430);
		setTitle("Select a theme/layer");
		addWindowListener(
			new WindowAdapter() {
				public void windowClosing(WindowEvent e) {
					setVisible(false);
				}
			}
		);

		lis = new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				Object source = ae.getSource();
				if (source == cancel) {
					setVisible(false);
				} else {
					try {
						setVisible(false);
						map.getLayerset().addLayer(cus.getLayer());
						map.redraw();
						if (Breweries.stb.getSelectedLayers() != null) {
							Breweries.pitem.setEnabled(true);
						}
					} catch(IOException e){}
				}
			}
		};

		ok.addActionListener(lis);
		cancel.addActionListener(lis);
		getContentPane().add(cus,BorderLayout.CENTER);
		panel1.add(ok);
		panel1.add(cancel);
		getContentPane().add(panel1,BorderLayout.SOUTH);
	}

	//=========================================================================
	// FUNCTION: setMap()
	// DESCRIPTION: This function will set the map to the map passed in.
	//=========================================================================
	public void setMap(com.esri.mo2.ui.bean.Map map1){
		map = map1;
	}
}


//===========================================================================
// CLASS: AddXYtheme
// DESCRIPTION: This function parses a CSV file and creates a feature layer.
//===========================================================================
class AddXYtheme extends JDialog {
	Map map;
	Vector s2 = new Vector();
	Vector s3 = new Vector();
	Vector s4 = new Vector();
	Vector s5 = new Vector();
	Vector s6 = new Vector();
	Vector s7 = new Vector();
	Vector s8 = new Vector();
	Vector s9 = new Vector();
	Vector s10 = new Vector();
	JFileChooser jfc = new JFileChooser();
	BasePointsArray bpa = new BasePointsArray();
	AddXYtheme() throws IOException {
		setBounds(50,50,520,430);
		jfc.showOpenDialog(this);
		try {
			File file  = jfc.getSelectedFile();
			FileReader fred = new FileReader(file);
			BufferedReader in = new BufferedReader(fred);
			String s;
			double x,y;
			int n = 0;
			while ((s = in.readLine()) != null) {
				StringTokenizer st = new StringTokenizer(s,",");
				x = Double.parseDouble(st.nextToken());
				y = Double.parseDouble(st.nextToken());
				bpa.insertPoint(n,new com.esri.mo2.cs.geom.Point(x,y));
				s2.addElement(st.nextToken());
				s3.addElement(st.nextToken());
				s4.addElement(st.nextToken());
				s5.addElement(st.nextToken());
				s6.addElement(st.nextToken());
				s7.addElement(st.nextToken());
				s8.addElement(st.nextToken());
				s9.addElement(st.nextToken());
				s10.addElement(st.nextToken());
				n++ ;
			}
		} catch (IOException e){}
		XYfeatureLayer xyfl =
				new XYfeatureLayer(bpa,map,s2,s3,s4,s5,s6,s7,s8,s9,s10);
		xyfl.setVisible(true);
		map = Breweries.map;
		map.getLayerset().addLayer(xyfl);
		map.redraw();
	}

	//=========================================================================
	// FUNCTION: setMap()
	// DESCRIPTION: This function will set the map to the map passed in.
	//=========================================================================
	public void setMap(com.esri.mo2.ui.bean.Map map1){
		map = map1;
	}
}


//===========================================================================
// CLASS: XYfeatureLayer
// DESCRIPTION: This is the nuts and bolts class for the create feature layer
//              function from CSV (XYTool).
//===========================================================================
class XYfeatureLayer extends BaseFeatureLayer {
	BaseFields fields;
	private java.util.Vector featureVector;

	//=========================================================================
	// FUNCTION: XYFeatureLayer()
	// DESCRIPTION: Class constructor
	//=========================================================================
	public XYfeatureLayer(BasePointsArray bpa, Map map, Vector s2, Vector s3,
			Vector s4, Vector s5, Vector s6, Vector s7, Vector s8, Vector s9,
			Vector s10) {

		createFeaturesAndFields(bpa,map,s2,s3,s4,s5,s6,s7,s8,s9,s10);
		BaseFeatureClass bfc = getFeatureClass("MyPoints",bpa);
		setFeatureClass(bfc);
		BaseSimpleRenderer srd = new BaseSimpleRenderer();
		com.esri.mo2.map.draw.RasterMarkerSymbol sms = null; // for points
		sms = new com.esri.mo2.map.draw.RasterMarkerSymbol();
		sms.setSizeX(30);
		sms.setSizeY(30);
		sms.setImageString("C:/Breweries/img/icons/beer1.png");
		srd.setSymbol(sms);
		setRenderer(srd);
		XYLayerCapabilities lc = new XYLayerCapabilities();
		setCapabilities(lc);
	}

	//com.esri.mo2.map.draw.RasterMarkerSymbol sms = null; // for points
	/*			sms = new com.esri.mo2.map.draw.RasterMarkerSymbol();
				sms.setSizeX(15);
				sms.setSizeY(15);
				sms.setImageString("C:/ESRI/MOJ20/examples/BeatsLogo.png");
				sbr.setSymbol(sms);
			TrueTypeMarkerSymbol ttm = new TrueTypeMarkerSymbol() ;
			ttm.setFont(new Font("ESRI Crime Analysis", Font.PLAIN, 24));
		    ttm.setColor(new Color(0,0,0));
		    ttm.setCharacter("107") ;
		    srd.setSymbol(ttm) ;
	*/

	//=========================================================================
	// FUNCTION: createFeaturesAndFields()
	// DESCRIPTION: This creates the main data structure used by the XYTool to
	//              create the features in the map and attribute table.
	//=========================================================================
	private void createFeaturesAndFields(BasePointsArray bpa,Map map,Vector s2,
			Vector s3, Vector s4, Vector s5, Vector s6, Vector s7, Vector s8,
			Vector s9, Vector s10) {
		featureVector = new java.util.Vector();
		fields = new BaseFields();
		createDbfFields();
		for(int i=0;i<bpa.size();i++) {
			BaseFeature feature = new BaseFeature();
			feature.setFields(fields);
			com.esri.mo2.cs.geom.Point p =
					new com.esri.mo2.cs.geom.Point(bpa.getPoint(i));
			feature.setValue(0,p);
			feature.setValue(1,new Integer(i));
			feature.setValue(2,(String)s2.elementAt(i));
			feature.setValue(3,(String)s3.elementAt(i));
			feature.setValue(4,(String)s4.elementAt(i));
			feature.setValue(5,(String)s5.elementAt(i));
			feature.setValue(6,(String)s6.elementAt(i));
			feature.setValue(7,(String)s7.elementAt(i));
			feature.setValue(8,(String)s8.elementAt(i));
			feature.setValue(9,(String)s9.elementAt(i));
			feature.setValue(10,(String)s10.elementAt(i));
			feature.setDataID(new BaseDataID("MyPoints",i));
			featureVector.addElement(feature);
		}
	}

	//=========================================================================
	// FUNCTION: createDbfFields()
	// DESCRIPTION: This setups the attribute table, names the columns and
	//              defines the type of each.
	//=========================================================================
	private void createDbfFields() {
		fields.addField(new BaseField("#SHAPE#",Field.ESRI_SHAPE,0,0));
		fields.addField(new BaseField("ID",java.sql.Types.INTEGER,9,0));
		fields.addField(new BaseField("Name",java.sql.Types.VARCHAR,64,0));
		fields.addField(new BaseField("Address",java.sql.Types.VARCHAR,64,0));
		fields.addField(new BaseField("Phone Number",java.sql.Types.VARCHAR,64,0));
		fields.addField(new BaseField("Directions",java.sql.Types.VARCHAR,64,0));
		fields.addField(new BaseField("Hotel",java.sql.Types.VARCHAR,64,0));
		fields.addField(new BaseField("Rating",java.sql.Types.VARCHAR,64,0));
		fields.addField(new BaseField("URL",java.sql.Types.VARCHAR,64,0));
		fields.addField(new BaseField("Fun Fact",java.sql.Types.VARCHAR,64,0));
		fields.addField(new BaseField("Youtube",java.sql.Types.VARCHAR,64,0));
	}

	//=========================================================================
	// FUNCTION: BaseFeatureClass()
	// DESCRIPTION: This function returns the BaseFeature class.
	//=========================================================================
	public BaseFeatureClass getFeatureClass(String name,BasePointsArray bpa){
		com.esri.mo2.map.mem.MemoryFeatureClass featClass = null;
		try {
			featClass =
					new com.esri.mo2.map.mem.MemoryFeatureClass(
							MapDataset.POINT,fields);
		} catch (IllegalArgumentException iae) {}
		featClass.setName(name);
		for (int i=0;i<bpa.size();i++) {
			featClass.addFeature((Feature) featureVector.elementAt(i));
		}
		return featClass;
	}

	//=========================================================================
	// FUNCTION: XYLayerCapabilities()
	// DESCRIPTION: This function sets some properties of the new feature layer
	//=========================================================================
	private final class XYLayerCapabilities
		extends com.esri.mo2.map.dpy.LayerCapabilities {

		XYLayerCapabilities() {
			for (int i=0;i<this.size(); i++) {
				setAvailable(this.getCapabilityName(i),true);
				setEnablingAllowed(this.getCapabilityName(i),true);
				getCapability(i).setEnabled(true);
			}
		}
	}
}


//===========================================================================
// CLASS: AttrTab
// DESCRIPTION: This class displays the attribute table for the active layer
//===========================================================================
class AttrTab extends JDialog {
	JPanel panel1 = new JPanel();
	com.esri.mo2.map.dpy.Layer layer = Breweries.layer4;
	JTable jtable = new JTable(new MyTableModel());
	JScrollPane scroll = new JScrollPane(jtable);

	//=========================================================================
	// FUNCTION: AttrTab()
	// DESCRIPTION: Class constructor
	//=========================================================================
	public AttrTab() throws IOException {
		setBounds(70,70,450,350);
		setTitle("Attribute Table");
		addWindowListener(
			new WindowAdapter() {
				public void windowClosing(WindowEvent e) {
					setVisible(false);
				}
			}
		);

		scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		jtable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

		TableColumn tc = null;
		int numCols = jtable.getColumnCount();
		for (int j=0;j<numCols;j++) {
			tc = jtable.getColumnModel().getColumn(j);
			tc.setMinWidth(50);
		}
		getContentPane().add(scroll,BorderLayout.CENTER);
	}
}


//===========================================================================
//CLASS: MyTableModel
//DESCRIPTION: This class creates the attribute table
//===========================================================================
class MyTableModel extends AbstractTableModel {
	com.esri.mo2.map.dpy.Layer layer = Breweries.layer4;
	MyTableModel() {
		qfilter.setSubFields(fields);
		com.esri.mo2.data.feat.Cursor cursor = flayer.search(qfilter);
		while (cursor.hasMore()) {
			ArrayList inner = new ArrayList();
			Feature f = (com.esri.mo2.data.feat.Feature)cursor.next();
			inner.add(0,String.valueOf(row));
			for (int j=1;j<fields.getNumFields();j++) {
				inner.add(f.getValue(j).toString());
			}
			data.add(inner);
			row++;
		}
	}
	FeatureLayer flayer = (FeatureLayer) layer;
	FeatureClass fclass = flayer.getFeatureClass();
	String columnNames [] = fclass.getFields().getNames();
	ArrayList data = new ArrayList();
	int row = 0;
	int col = 0;
	BaseQueryFilter qfilter = new BaseQueryFilter();
	Fields fields = fclass.getFields();

	//=========================================================================
	// FUNCTION: getColumnCount()
	// DESCRIPTION: Returns the # of columns
	//=========================================================================
	public int getColumnCount() {
		return fclass.getFields().getNumFields();
	}

	//=========================================================================
	// FUNCTION: getRowCount()
	// DESCRIPTION: Returns the # of rows
	//=========================================================================
	public int getRowCount() {
		return data.size();
	}

	//=========================================================================
	// FUNCTION: getColumnName()
	// DESCRIPTION: Returns the name of column
	//=========================================================================
	public String getColumnName(int colIndx) {
		return columnNames[colIndx];
	}

	//=========================================================================
	// FUNCTION: getValueAt()
	// DESCRIPTION: Returns the value in the cell in the table
	//=========================================================================
	public Object getValueAt(int row, int col) {
		ArrayList temp = new ArrayList();
		temp =(ArrayList) data.get(row);
		return temp.get(col);
	}
}


//===========================================================================
//CLASS: CreateShapeDialog
//DESCRIPTION: This class writes the new shapefile to disk
//===========================================================================
class CreateShapeDialog extends JDialog {
	String name = "";
	String path = "";
	int ltype  ;
	JButton ok = new JButton("OK");
	JButton cancel = new JButton("Cancel");
	JTextField nameField =
			new JTextField("enter layer name here, then hit ENTER",25);
	com.esri.mo2.map.dpy.FeatureLayer selectedlayer;
	JPanel panel1 = new JPanel();
	JLabel centerlabel = new JLabel();

	ActionListener lis = new ActionListener() {
		public void actionPerformed(ActionEvent ae) {
			Object o = ae.getSource();
			if (o == nameField) {
				name = nameField.getText().trim();
				try {
					//path = ((ShapefileFolder)(Breweries.layer4.getLayerSource())).getPath();
					path = "C:\\Breweries\\data\\CreatedSHPFiles";
				} catch ( Exception e ) {
					path = "C:/Temp" ;
				}
				System.out.println(path+"    " + name);
			} else if (o == cancel) {
				setVisible(false);
			} else {
				try {
					ShapefileWriter.writeFeatureLayer(selectedlayer,path,name,ltype);
				} catch(Exception e) {
				      System.err.println("1");
				      System.err.println(e);
				      System.err.println("\n2");
				      System.err.println(e.getMessage());
				      System.err.println("\n3");
				      System.err.println(e.getLocalizedMessage());
				      System.err.println("\n4");
				      System.err.println(e.getCause());
				      e.printStackTrace();
				}
				setVisible(false);
			}
		}
	};

	//=========================================================================
	// FUNCTION: CreateShapeDialog()
	// DESCRIPTION: Constructor
	//=========================================================================
	public CreateShapeDialog (com.esri.mo2.map.dpy.FeatureLayer layer5,
					          int layertype) {
		selectedlayer = layer5;
		ltype = layertype;
		setBounds(40,350,450,150);
	    setTitle("Create new shapefile?");
	    addWindowListener(
	    	new WindowAdapter() {
	    		public void windowClosing(WindowEvent e) {
	    			setVisible(false);
	    		}
	    	}
	    );
	    nameField.addActionListener(lis);
	    ok.addActionListener(lis);
	    cancel.addActionListener(lis);
	    String s = "<HTML> To make a new shapefile from the new layer, enter<BR>" +
	      "the new name you want for the layer and click OK.<BR>" +
	      "You can then add it to the map in the usual way.<BR>"+
	      "Click ENTER after replacing the text with your layer name";
	    centerlabel.setHorizontalAlignment(JLabel.CENTER);
	    centerlabel.setText(s);
	    getContentPane().add(centerlabel,BorderLayout.CENTER);
	    panel1.add(nameField);
	    panel1.add(ok);
	    panel1.add(cancel);
	    getContentPane().add(panel1,BorderLayout.SOUTH);
	}
}


//===========================================================================
//CLASS: Arrow
//DESCRIPTION: This class handles the mouse cursor behavior.
//===========================================================================
class Arrow extends Tool {

	//=========================================================================
	// FUNCTION: Arrow()
	// DESCRIPTION: Class constructor
	//=========================================================================
	public Arrow() {
		Breweries.milesLabel.setText("DIST   0 mi   ");
		Breweries.kmLabel.setText("   0 km    ");
		Breweries.map.repaint();
		Breweries.helpToolOn = false ;
	}
}


//===========================================================================
//CLASS: Flash
//DESCRIPTION: When a new layer is added this class will flash the new layer
//             in a new thread to help show the user what they have done.
//             It works on polygon layers only.
//===========================================================================
class Flash extends Thread {
	Legend legend;
	Flash(Legend legendin) {
		legend = legendin;
	}

	//=========================================================================
	// FUNCTION: run()
	// DESCRIPTION: This is the function that will be executed when the child
	//              thread is invoked.
	//=========================================================================
	public void run() {
		for (int i=0;i<12;i++) {
			try {
				Thread.sleep(500);
				legend.toggleSelected();
			} catch (Exception e) {}
		}
	}
}


//===========================================================================
//CLASS: DistanceTool
//DESCRIPTION: A class to help calculate as the crow flies distance
//             interactively with the user.
//===========================================================================
class DistanceTool extends DragTool  {
	int startx,starty,endx,endy,currx,curry;
	com.esri.mo2.cs.geom.Point initPoint, endPoint, currPoint;
	double distance;

	//=========================================================================
	// FUNCTION: mousePressed()
	// DESCRIPTION: Collects the starting point for the user.
	//=========================================================================
	public void mousePressed(MouseEvent me) {
		startx = me.getX(); starty = me.getY();
		initPoint = Breweries.map.transformPixelToWorld(me.getX(),me.getY());
	}

	//=========================================================================
	// FUNCTION: mouseReleased()
	// DESCRIPTION: Collects the end point and calculates as the crow flies
	//              distance.
	//=========================================================================
	public void mouseReleased(MouseEvent me) {
		endx = me.getX(); endy = me.getY();
		endPoint = Breweries.map.transformPixelToWorld(me.getX(),me.getY());
		distance = (69.44 / (2*Math.PI)) * 360 * Math.acos(
				 Math.sin(initPoint.y * 2 * Math.PI / 360)
				 * Math.sin(endPoint.y * 2 * Math.PI / 360)
				 + Math.cos(initPoint.y * 2 * Math.PI / 360)
				 * Math.cos(endPoint.y * 2 * Math.PI / 360)
				 * (Math.abs(initPoint.x - endPoint.x) < 180 ?
                    Math.cos((initPoint.x - endPoint.x)*2*Math.PI/360):
                    Math.cos((360 -
                    		Math.abs(initPoint.x - endPoint.x))*2*Math.PI/360)));
		System.out.println( distance );
		Breweries.milesLabel.setText("DIST: " +
									 new Float((float)distance).toString()
									 + " mi  ");
		Breweries.kmLabel.setText(new Float((float)(distance*1.6093)).toString()
								  + " km");

		if (Breweries.acetLayer != null) {
			Breweries.map.remove(Breweries.acetLayer);
		}

		Breweries.acetLayer = new AcetateLayer() {
			public void paintComponent(java.awt.Graphics g) {
				java.awt.Graphics2D g2d = (java.awt.Graphics2D) g;
				Line2D.Double line = new Line2D.Double(startx,
							starty,
							endx,
							endy);
				g2d.setColor(new Color(0,0,250));
				g2d.draw(line);
			}
		};

		Graphics g = super.getGraphics();
		Breweries.map.add(Breweries.acetLayer);
		Breweries.map.redraw();
	}

	//=========================================================================
	// FUNCTION: cancel()
	// DESCRIPTION: Null function to handle cancel event
	//=========================================================================
	public void cancel() {};
}


//===========================================================================
//CLASS: HotPick
//DESCRIPTION: Displays a dialog window with attribute data information as
//             well as a button to click to take the user to the website.
//===========================================================================
class HotPick extends JDialog {


	 // String[][] breweryPics = new String[][] {
		//} ;

		


	//=========================================================================
	// FUNCTION: HotPick()
	// DESCRIPTION: Class constructor
	//=========================================================================
	public HotPick(int id, Row row, int activeLayerIndex, Map map)
		throws IOException {

		// Main variables...
		ActionListener jblis ;
		final String os = System.getProperty("os.name");
		final JButton jb = new JButton("Click Here To Open Website",
				new ImageIcon("C:/Breweries/img/icons/globe.png"));
		final JButton jbv = new JButton("Click Here To Open A Youtube Video",
				new ImageIcon("C:/Breweries/img/icons/globe.png"));
		// Get the main container for all panels here...
		Container container = getContentPane();

		// Create new top layer panel for all subsequent panels...
		JPanel top = new JPanel(new BorderLayout());
		// Title information:
		setTitle(row.getDisplayValue(2));

		// Create a grid layout for Brewery Information...
		// Then each column is it's own FlowLayout to control size/height of
		// the JLabel text fields...
		JPanel info = new JPanel(new GridLayout(1,2));

		// First Column for Brewery Information (title):
		JPanel infotitlePanel = new JPanel() ;
		infotitlePanel.setLayout( new FlowLayout( FlowLayout.LEADING ) );
		JLabel infotitle = new JLabel(row.getDisplayValue(2)) ;
		infotitlePanel.add(infotitle);
		info.add(infotitlePanel) ;

		// Second Column for Brewery Information (details):
		String breweryinfo = "<HTML>";
		JPanel infodetailsPanel = new JPanel() ;
		infodetailsPanel.setLayout( new FlowLayout( FlowLayout.LEADING ) );

		// Store off the url field for use later...
		//final String url = row.getDisplayValue(8) ;

		// Loop through the features and add each item to the text display...
		int numfieldsinrow = row.getFields().size() ;
		System.out.println(numfieldsinrow);
		System.out.println(row.getDisplayValue(10));
		String[] fieldnames = row.getFields().getNames() ;
		for ( int i=3; i<numfieldsinrow; ++i ) {
			// Skip URL field since we have a JButton to handle that
			// in the South...
			if ( i != 8 && i != 10) {
				breweryinfo += fieldnames[i] + ": " +
						row.getDisplayValue(i) + "<BR>" ;
			}
		}
		JLabel infodetails = new JLabel(breweryinfo) ;
		infodetailsPanel.add(infodetails) ;
		info.add(infodetailsPanel);
		top.add(info, BorderLayout.NORTH);

		// Create a grid layout for pictures...
		JPanel pics = new JPanel(new GridLayout(1,10,10,10));
		String curLayerName = map.getLayer(activeLayerIndex).getName();
		System.out.println("LayerName = [" + curLayerName + "]" );
		if ( new String("MyPoints").equals(curLayerName) ) {

			// We have pictures so make the window size larger than normal...
			setBounds(20,20,1000,600);

			for ( int i=0 ; i<b[id].length; ++i) {
				JLabel label = new JLabel(new ImageIcon(b[id][i]));
				pics.add(label) ;
			}
			//The pictures will likely need a horizontal scroll bar...
			JScrollPane jp = new JScrollPane( pics,
		            				JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
		            				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
			top.add(jp, BorderLayout.CENTER);
		} else {
			// We do not have pictures so make the window size smaller...
			setBounds(50,50,500,275);
		}


		jblis = new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				Object source = ae.getSource();
				String url = "";
				if(source == jb){
					url = row.getDisplayValue(8) ;
				}
				else{
					url = row.getDisplayValue(10) ;
				}

				// Default to windows...
				String b = "C:\\Program Files\\Internet Explorer\\IEXPLORE.EXE";
				b = b + " " + url ;

				if (source == jb || source == jbv) {
					try {
						if (os.indexOf("Windows") != -1) {
							Runtime.getRuntime().exec(b);
						} else if (os.indexOf("Mac") != -1) {
							b = "open" + "-a" + "Safari" + url ;
							Runtime.getRuntime().exec(b);
							}
					} catch (Exception ex) {
						System.out.println("cannot execute command. " + ex);
						ex.printStackTrace();
					}
				}
			}
		} ;


		jb.addActionListener(jblis);
		jbv.addActionListener(jblis);
		top.add(jb, BorderLayout.SOUTH);
		top.add(jbv, BorderLayout.EAST);

		container.add(top) ;

		// Close window handler...
		addWindowListener(
			new WindowAdapter() {
				public void windowClosing(WindowEvent e) {
					setVisible(false);
				}
			}
		);
	}
}


//===========================================================================
//CLASS: HelpDialog
//DESCRIPTION: A Help Menu function to display a new window and display
//             helpful text to the user to help show them how to use the app.
//===========================================================================
class HelpDialog extends JDialog {
	JTextArea ha ;

	//=========================================================================
	// FUNCTION: HelpDialog()
	// DESCRIPTION: Class constructor
	//=========================================================================
	public HelpDialog(String input) throws IOException {
		setBounds(70,70,450,250) ;
		setTitle("Help");
		addWindowListener(
			new WindowAdapter() {
				public void windowClosing(WindowEvent e) {
					setVisible(false);
				}
			}
		) ;
		ha = new JTextArea(input,7,40);
		JScrollPane sp = new JScrollPane(ha);
		ha.setEditable(false);
		getContentPane().add(sp,"Center");
	}
}


class HelpTool extends Tool {
}