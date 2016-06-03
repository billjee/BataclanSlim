/**
 * 
 */
package Gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GradientPaint;
/**
 * @author Antoine
 *
 */
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.event.MouseListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.ActionMap;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JToolBar;

import java.io.File;
import org.geotools.data.DataUtilities;
import org.geotools.data.DefaultTransaction;
import org.geotools.data.FileDataStore;
import org.geotools.data.FileDataStoreFinder;
import org.geotools.data.Transaction;
import org.geotools.data.collection.ListFeatureCollection;
import org.geotools.data.shapefile.ShapefileDataStore;
import org.geotools.data.shapefile.ShapefileDataStoreFactory;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.data.simple.SimpleFeatureStore;
import org.geotools.feature.FeatureCollections;
import org.geotools.feature.SchemaException;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.geotools.geometry.DirectPosition2D;
import org.geotools.geometry.Envelope2D;
import org.geotools.geometry.jts.JTSFactoryFinder;
import org.geotools.map.FeatureLayer;
import org.geotools.map.Layer;
import org.geotools.map.MapContent;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.geotools.renderer.lite.StreamingRenderer;
import org.geotools.styling.SLD;
import org.geotools.styling.Style;
import org.geotools.swing.JMapPane;
import org.geotools.swing.action.InfoAction;
import org.geotools.swing.action.NoToolAction;
import org.geotools.swing.action.PanAction;
import org.geotools.swing.action.ResetAction;
import org.geotools.swing.action.ZoomInAction;
import org.geotools.swing.action.ZoomOutAction;
import org.geotools.swing.data.JFileDataStoreChooser;
import org.geotools.swing.event.MapMouseEvent;
import org.geotools.swing.event.MapMouseListener;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;



 
public class PaneMap extends JMapPane { 
	double clickToZoom = 0.01;
	
	public PaneMap() throws IOException, SchemaException{
		super();
		//File file = JFileDataStoreChooser.showOpenFile("shp", null);
		File file = new File("D:\\Recherche\\CharlieWorkspace\\BataclanSlim\\example\\association\\data\\disseminationArea.shp");
        if (file == null) {
            return;
        }

        FileDataStore store = FileDataStoreFinder.getDataStore(file);
        SimpleFeatureSource featureSource = store.getFeatureSource();
        Style style = SLD.createSimpleStyle(featureSource.getSchema());
        Layer layer = new FeatureLayer(featureSource, style);
        

        // Create a map content and add our shapefile to it
        MapContent map = new MapContent();
        map.setTitle("Quickstart");
        map.addLayer(layer);
        
        
        
        File file2 = new File("D:\\Recherche\\CharlieWorkspace\\BataclanSlim\\example\\association\\data\\stops.txt");
        if (file2 == null) {
            return;
        }
        
        final SimpleFeatureType TYPE = DataUtilities.createType("Location",
                "location:Point:srid=4326," + // <- the geometry attribute: Point type
                        "name:String," + // <- a String attribute
                        "number:String" // a number attribute
        );
        
        List<SimpleFeature> features = new ArrayList<SimpleFeature>();
        
        /*
         * GeometryFactory will be used to create the geometry attribute of each feature,
         * using a Point object for the location.
         */
        GeometryFactory geometryFactory = JTSFactoryFinder.getGeometryFactory();

        SimpleFeatureBuilder featureBuilder = new SimpleFeatureBuilder(TYPE);

        BufferedReader reader = new BufferedReader(new FileReader(file2));
        try {
            /* First line of the data file is the header */
            String line = reader.readLine();
            System.out.println("Header: " + line);

            for (line = reader.readLine(); line != null; line = reader.readLine()) {
                if (line.trim().length() > 0) { // skip blank lines
                    String tokens[] = line.split(Utils.Utils.COLUMN_DELIMETER);

                    double latitude = Double.parseDouble(tokens[4]);
                    double longitude = Double.parseDouble(tokens[5]);
                    String name = tokens[0].trim();
                    String number = tokens[2].trim();

                    /* Longitude (= x coord) first ! */
                    Point point = geometryFactory.createPoint(new Coordinate(longitude, latitude));

                    featureBuilder.add(point);
                    featureBuilder.add(name);
                    featureBuilder.add(number);
                    SimpleFeature feature = featureBuilder.buildFeature(null);
                    features.add(feature);
                }
            }
        } finally {
            reader.close();
        }
        
        SimpleFeatureCollection collection = new ListFeatureCollection(TYPE, features);
        
        
        Style style2 = SLD.createSimpleStyle(collection.getSchema());
        style2 = SLD.createPointStyle("Circle", Color.black, Color.black, (float) 0.5, 20);
        Layer layer2 = new FeatureLayer(collection, style2);
        
        
        map.addLayer(layer2);
        map.addLayer(layer);
        
        
        
        
        
        
        this.setDisplayArea(map.getMaxBounds());
        
        this.setMapContent(map);
        this.setRenderer(new StreamingRenderer());
        
        
        JToolBar toolBar = new JToolBar();
        toolBar.setOrientation(JToolBar.VERTICAL);
        toolBar.setFloatable(false);

        ButtonGroup cursorToolGrp = new ButtonGroup();

        JButton zoomInBtn = new JButton(new ZoomInAction(this));
        toolBar.add(zoomInBtn);
        cursorToolGrp.add(zoomInBtn);

        JButton zoomOutBtn = new JButton(new ZoomOutAction(this));
        toolBar.add(zoomOutBtn);
        cursorToolGrp.add(zoomOutBtn);
        
        JButton centerBtn = new JButton(new ResetAction(this));
        toolBar.add(centerBtn);
        cursorToolGrp.add(centerBtn);
        
        JButton infoBtn = new JButton(new InfoAction(this));
        toolBar.add(infoBtn);
        cursorToolGrp.add(infoBtn);
        
        JButton noBtn = new JButton(new NoToolAction(this));
        toolBar.add(noBtn);
        cursorToolGrp.add(noBtn);
        
        JButton panBtn = new JButton(new PanAction(this));
        toolBar.add(panBtn);
        cursorToolGrp.add(panBtn);
        
        
        //this.setActionMap(new WheelAction(this));
        
        
        this.add(toolBar);
        this.add(toolBar, BorderLayout.WEST);
        this.setLayout(new FlowLayout(FlowLayout.LEFT));
        //this.addMouseListener(new ZoomWithWheel());
       
	}
	
	
	class ZoomWithWheel implements  MapMouseListener{

		@Override
		public void onMouseClicked(MapMouseEvent ev) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onMouseDragged(MapMouseEvent ev) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onMouseEntered(MapMouseEvent ev) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onMouseExited(MapMouseEvent ev) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onMouseMoved(MapMouseEvent ev) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onMousePressed(MapMouseEvent ev) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onMouseReleased(MapMouseEvent ev) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onMouseWheelMoved(MapMouseEvent ev) {
			// TODO Auto-generated method stub
			// TODO Auto-generated method stub
			int clicks = ev.getWheelAmount();
			System.out.println(clicks);
		   // -ve means wheel moved up, +ve means down
		    int sign = (clicks < 0 ? -1 : 1);

		    Envelope env = getDisplayArea();
		    double width = env.getWidth();
		    double scale = clickToZoom * sign;
		   
		    Rectangle paneArea = getVisibleRect();
	        DirectPosition2D mapPos = ev.getWorldPos();


	         DirectPosition2D corner = new DirectPosition2D(
	                mapPos.getX() - 0.5d * paneArea.getWidth() * scale,
	                mapPos.getY() + 0.5d * paneArea.getHeight() * scale);
	        
	         Envelope2D newMapArea = new Envelope2D();
	         newMapArea.setFrameFromCenter(mapPos, corner);
	         setDisplayArea(newMapArea);

		}
		
	}
  
}