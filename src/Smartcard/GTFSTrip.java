/**
 * 
 */
package Smartcard;

import java.util.HashMap;

/**
 * @author Antoine
 *
 */
public class GTFSTrip {

	String myId;
	String myRouteId;
	String myDirectionId;
	HashMap<Integer,GTFSStop> myDirection;
	
	public GTFSTrip(){
		
	}
	
	public GTFSTrip(String tripId, String routeId, String directionId){
		myId = tripId;
		myRouteId = routeId;
		myDirectionId = directionId;
		myDirection = new HashMap<Integer, GTFSStop>();
	}
}
