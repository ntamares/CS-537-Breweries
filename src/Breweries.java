/*

Nestor Tamares
masc2566
CS 537 - Programming for GIS
Prof. Eckberg

*/

import javax.swing.*;
import java.io.IOException;
import java.awt.event.*;
import java.awt.*;
import com.esri.mo2.ui.bean.*;
import com.esri.mo2.ui.tb.ZoomPanToolBar;
import com.esri.mo2.ui.tb.SelectionToolBar;
import com.esri.mo2.ui.ren.LayerProperties;
import com.esri.mo2.cs.geom.Envelope;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumn;
import com.esri.mo2.data.feat.*; 
import com.esri.mo2.map.dpy.FeatureLayer;
import com.esri.mo2.file.shp.*;
import com.esri.mo2.map.dpy.Layerset;
import com.esri.mo2.map.draw.*;
import com.esri.mo2.ui.bean.Tool;
import java.util.ArrayList;

public class Breweries extends JFrame{
	
	//Map and layers
	static Map     map            = new Map();
	static boolean fullMap        = true; // map not zoomed
	Legend         legend;
	Layer          sanDiegoCounty = new Layer();
	String         sanDiegoPath   = "D:\\Data\\Supervisor_Districts.shp";
	String         dataPathName   = "";
	String         legendName     = "";
	

	
	// Menu Bar
	JMenuBar mbar         = new JMenuBar();
	JMenu    file         = new JMenu("File");
	JMenu    layerControl = new JMenu("Layer Control");
	
	// Toolbars
	ZoomPanToolBar          zptp    = new ZoomPanToolBar();
	static SelectionToolBar stb     = new SelectionToolBar();
	JToolbar                jtb     = new JToolBar();
	Arrow                   arrow   = new Arrow(); // to reset cursor after tool
	JPanel                  sdPanel = new JPanel();
	
	// Listeners + Adapters
	ComponentListener compLis;
	ActionListener lis, layerLis, layerControlLis;
	TocAdapter tocAdapter;
	static Envelope env; // wtf?
	
	public Breweries(){
		super ("San Diego Breweries");
		this.setSize(700,450);
		zptb.setMap(map);
		stb.setMap(map);
		setJMenuBar()
		
		
	}
	
}

// Adds shapefile to the map
private void addShapefileToMap(Layer layer,String s) {
	String datapath = s; 
	layer.setDataset("0;"+datapath);
	map.add(layer);
}

public static void main(String[] args){
	return 0;
}