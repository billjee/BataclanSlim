/**
 * 
 */
package Smartcard;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * This represents a transportation line. A different class instance should be created for each direction.
 * @author Antoine
 *
 */
public class GTFSRoute {
	public String myId;
	ArrayList<GTFSTrip> myTripsId = new ArrayList<GTFSTrip>();
	
	/**
	 * This HashMap is structured as follow: the key indicates the direction (usually 0 or 1). The value is another HashMap<Integer, GTFSStop>, 
	 * the integer key is the stop sequence of the stop while the GTFS stop points to the stop.
	 */
	public HashMap<String,HashMap<Integer,GTFSStop>> myDirections = new HashMap<String,HashMap<Integer,GTFSStop>>();
	
	
	public ArrayList<GTFSStop> getVanishingRoute(GTFSStop curStop, String curDirectionId){

		ArrayList<GTFSStop> vanishingRoute = new ArrayList<GTFSStop>();
		if(this.contains(curStop)){
			HashMap<Integer,GTFSStop> curDirection = myDirections.get(curDirectionId);
			ArrayList<Integer> sequenceId = new ArrayList<Integer>();
			for(int i : curDirection.keySet()){
				sequenceId.add(i);
			}
			sequenceId.sort(null);
			
			int curStopSeqId = getSequenceId(curStop);
			
			for(int i: sequenceId){
				if(i > curStopSeqId){
					vanishingRoute.add(curDirection.get(i));
				}
			}
		}
		else{
			System.out.println("--the transaction data is corrupted: stop " + curStop.myId + " doesn't exist on route " + this.myId);
		}
		
		return vanishingRoute;
		
	}
	
	/*
	public double getDistance(GTFSStop origin, GTFSStop nextOrigin){
			double distance = 1000000;
			double xA = nextOrigin.x;
			double yA = nextOrigin.y;
			String currDirection = getDirection(origin);
			
			for(GTFSStop gTFSStop: myDirections.get(currDirection)){
				double xB = gTFSStop.x;
				double yB = gTFSStop.y;
				double currDist = Math.pow(xA-xB,2)+Math.pow(yA-yB, 2);
				if(currDist < distance){
					distance = currDist;
				}
			}
			distance = Math.sqrt(distance);
		return distance;
	}
	
	public String getDirection(GTFSStop gTFSStop){
		if(contains(gTFSStop)){
			for(String i : myDirections.keySet()){
				if(myDirections.get(i).contains(gTFSStop)){
					return i;
				}
			}
		}
		return "NULL";
	}*/
	
	public boolean contains(GTFSStop curStop){
		for(String dir: myDirections.keySet()){
			for(int seq: myDirections.get(dir).keySet()){
				String tempId = myDirections.get(dir).get(seq).myId;
				if(tempId.equals(curStop.myId)){
					return true;
				}
			}
		}
		return false;
	}
	
	public int getSequenceId(GTFSStop curStop){
		for(String dir: myDirections.keySet()){
			for(int seq: myDirections.get(dir).keySet()){
				String tempId = myDirections.get(dir).get(seq).myId;
				if(tempId.equals(curStop.myId)){
					return seq;
				}
			}
		}
		System.out.println("--there was probably a problem in function Line.contains");
		return -1;
	}

}
