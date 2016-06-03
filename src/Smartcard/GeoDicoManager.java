/**
 * 
 */
package Smartcard;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.geotools.data.DataStore;
import org.geotools.data.DataStoreFinder;
import org.geotools.data.FeatureSource;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureIterator;
import org.geotools.geometry.jts.JTS;
import org.geotools.geometry.jts.JTSFactoryFinder;
import org.geotools.referencing.CRS;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.opengis.feature.GeometryAttribute;
import org.opengis.feature.Property;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.filter.Filter;
import org.opengis.geometry.MismatchedDimensionException;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;

import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.MultiPolygon;

import ActivityChoiceModel.DataManager;
import ActivityChoiceModel.UtilsTS;

/**
 * @author Antoine
 *
 */
public class GeoDicoManager extends DataManager{
	
	public HashMap<String,ArrayList<String>> getDico(String pathGeoDico) throws IOException{
		HashMap<String,ArrayList<String>> myDico = new HashMap<String,ArrayList<String>>();
		initialize(pathGeoDico);
		for(int i = 0; i < myData.get(UtilsSM.zoneId).size(); i++){
			String nextRecordZone = myData.get(UtilsSM.zoneId).get(i);
			String newStation = myData.get(UtilsST.stationId).get(i);
			if(myDico.containsKey(nextRecordZone)){
				myDico.get(nextRecordZone).add(newStation);
			}
			else{
				myDico.put(nextRecordZone, new ArrayList<String>());
				myDico.get(nextRecordZone).add(newStation);
			}
		}
		return myDico;
	}
	
	public HashMap<String, ArrayList<String>> getDico(String pathGeographicZones, HashMap<String, GTFSStop> myStops, CoordinateReferenceSystem targetCRS) throws IOException, MismatchedDimensionException, TransformException, FactoryException{
		HashMap<String,ArrayList<String>> myDico = new HashMap<String,ArrayList<String>>();
		
		File file = new File(pathGeographicZones);
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("url", file.toURI().toURL());
		
		DataStore dataStore = DataStoreFinder.getDataStore(map);
	    String typeName = dataStore.getTypeNames()[0];
	    
	    FeatureSource<SimpleFeatureType, SimpleFeature> source = dataStore.getFeatureSource(typeName);
	    Filter filter = Filter.INCLUDE; // ECQL.toFilter("BBOX(THE_GEOM, 10,20,30,40)")
	    
	    

	    FeatureCollection<SimpleFeatureType, SimpleFeature> collection = source.getFeatures(filter);
	    try (FeatureIterator<SimpleFeature> features = collection.features()) {
	        while (features.hasNext()) {
	            SimpleFeature feature = features.next();
	            addCloseStops(feature, myStops, myDico, targetCRS);
	        }
	    }
	    
		return myDico;
		
	}

	private void addCloseStops(SimpleFeature feature, HashMap<String, GTFSStop> myStops, HashMap<String, ArrayList<String>> myDico, CoordinateReferenceSystem targetCRS) throws MismatchedDimensionException, TransformException, FactoryException {
		// TODO Auto-generated method stub

		String areaId =  Long.toString((long) feature.getAttribute(UtilsSM.areaId));
		
		GeometryFactory geometryFactory = JTSFactoryFinder.getGeometryFactory();
		
		MathTransform transform = CRS.findMathTransform(DefaultGeographicCRS.WGS84, targetCRS);
		
		for(GTFSStop curStop: myStops.values()){
			
			Coordinate coord = new Coordinate(curStop.lon, curStop.lat);
			Point curGeoStop = geometryFactory.createPoint(coord);
			MultiPolygon curArea = (MultiPolygon) feature.getDefaultGeometryProperty().getValue();

		    Geometry g3 = JTS.transform(curGeoStop, transform);
		    Geometry g4 = JTS.transform(curArea, transform);
		    
			if(g3.isWithinDistance(g4, UtilsSM.distanceThreshold)){
				if(myDico.containsKey(areaId)){
					myDico.get(areaId).add(curStop.myId);
				}
				else{
					myDico.put(areaId, new ArrayList<String>());
					myDico.get(areaId).add(curStop.myId);
				}
			}
		}
	}
	
}
