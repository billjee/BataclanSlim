/**
 * 
 */
package Smartcard;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.geotools.geometry.jts.JTS;

import ActivityChoiceModel.DataManager;
import Utils.InputDataReader;

/**
 * @author Antoine
 *
 */
public class GTFSLoader {

	
	
	public GTFSLoader(){
		
	}
	
	public HashMap<String,GTFSTrip> getTrips(String pathToGTFSTripFile) throws IOException{
		
		HashMap<String,GTFSTrip> trips = new HashMap<String,GTFSTrip>();
		
		DataManager tempReader = new DataManager();
		tempReader.initialize(pathToGTFSTripFile);
		
		for(int i = 0; i < tempReader.getMyData().get(UtilsSM.tripId).size(); i++){
			String curTripId = tempReader.getMyData().get(UtilsSM.tripId).get(i);
			String curRouteId = tempReader.getMyData().get(UtilsSM.routeId).get(i);
			String curDirectionId = tempReader.getMyData().get(UtilsSM.directionId).get(i);
			GTFSTrip curTrip = new GTFSTrip(curTripId, curRouteId, curDirectionId);
			trips.put(curTripId,curTrip);
		}
		return trips;
	}
	
	public HashMap<String,GTFSStop> getStops(String pathToGTFSStopsFile) throws IOException{
		HashMap<String,GTFSStop> stops = new HashMap<String,GTFSStop>();
		
		DataManager tempReader = new DataManager();
		tempReader.initialize(pathToGTFSStopsFile);
		
		for(int i = 0; i < tempReader.getMyData().get(UtilsSM.stopId).size();i++){
			GTFSStop curStop = new GTFSStop();
			curStop.myId = tempReader.getMyData().get(UtilsSM.stopId).get(i);
			curStop.lat = Double.parseDouble(tempReader.getMyData().get(UtilsSM.lat).get(i));
			curStop.lon = Double.parseDouble(tempReader.getMyData().get(UtilsSM.lon).get(i));
			
			stops.put(curStop.myId, curStop);
		}
		return stops;
	}
	
	public HashMap<String, GTFSRoute> getRoutes(String pathToGTFSRoutesFile) throws IOException{
		HashMap<String,GTFSRoute> routes = new HashMap<String,GTFSRoute>();
		
		DataManager tempReader = new DataManager();
		tempReader.initialize(pathToGTFSRoutesFile);
		
		for(int i = 0; i < tempReader.getMyData().get(UtilsSM.routeId).size();i++){
			GTFSRoute curRoute = new GTFSRoute();
			curRoute.myId = tempReader.getMyData().get(UtilsSM.routeId).get(i);
			routes.put(curRoute.myId, curRoute);
		}
		return routes;
	}
	
	public HashMap<String, GTFSRoute> constructRouteItinerary(HashMap<String,GTFSTrip> myTrips,
			HashMap<String, GTFSRoute> myRoutes,
			HashMap<String, GTFSStop> myStops,
			String pathGTFSStopTimes) throws IOException{
		
		DataManager tempReader = new DataManager();
		tempReader.initialize(pathGTFSStopTimes);
		int count = 0;
		
		for(int i = 0; i < tempReader.getMyData().get(UtilsSM.tripId).size();i++){
			String curTripId = tempReader.getMyData().get(UtilsSM.tripId).get(i);
			String curStopId = tempReader.getMyData().get(UtilsSM.stopId).get(i);
			int curStopSequence = Integer.parseInt(tempReader.getMyData().get(UtilsSM.stopSequence).get(i));
			GTFSStop curStop = myStops.get(curStopId);
			GTFSTrip curTrip = myTrips.get(curTripId);
			
			//Each trip is constituted.
			curTrip.myDirection.put(curStopSequence, curStop);
			
		}
		
		//For each route, we choose the trip with the most stops attached as the base. We assume that trips with fewer stops are only pieces of the biggest trip.
		for(GTFSTrip curTrip: myTrips.values()){
			GTFSRoute curRoute = myRoutes.get(curTrip.myRouteId);
			
			if(!curRoute.myDirections.containsKey(curTrip.myDirectionId)){
				curRoute.myDirections.put(curTrip.myDirectionId, curTrip.myDirection);
			}
			else{
				int nOld = curRoute.myDirections.get(curTrip.myDirectionId).size();
				int nNew = curTrip.myDirection.size();
				if(nNew > nOld){
					curRoute.myDirections.put(curTrip.myDirectionId, curTrip.myDirection);
				}
			}
		}
		
		return myRoutes;
	}

	
}
