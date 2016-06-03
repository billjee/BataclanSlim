/**
 * 
 */
package Smartcard;

import java.util.ArrayList;
import java.util.HashMap;

import org.geotools.referencing.CRS;

import ActivityChoiceModel.UtilsTS;

/**
 * This class is a dictionary which contains all references required from the data sets.
 * All times are given in minutes, with 0min being equivalent to midnight.
 * All distances are given in meters.
 * @author Antoine
 *
 */

public class UtilsSM {
	public static final double INFINIT = 10000;
	public static final double WALKING_DISTANCE_THRESHOLD = 1000; //in meters
	
	public static final String DATE_FORMAT = "dd/MM/yyyy 'at' HHmm";
	public static String CRS = "EPSG:26918";
	
	
	
	
	
	
	
	public static String alightingStop = "alighting_stop";
	public static String index = "index";
	public static String cardId = "card_id";
	public static String date = "date";
	public static String firstTrans = "dailyFirstTransaction";
	public static String lastTrans = "dailyLastTransaction";
	public static String time = "validation_time";
	public static String isNotFirst = "F";
	public static String isNotLast = "F";
	public static String isFirst = "T";
	public static String isLast = "T";
	public static String zoneId = "DAUID";
	public static String lineId = "NM_LI";
	public static String stationId = "NUM_ARRET";
	public static String destStationId = "DER_Num_Arret_Dest";
	public static String nextStationId = "DER_Num_Arret_Dest";
	
	public static double morningPeakHourStart = 420; 
	public static double morningPeakHourEnd = 540;
	public static double eveningPeakHourStart = 930;
	public static double eveningPeakHourEnd = 1080;

	public static double timeThreshold = 30;
	public static double distanceThreshold = 1000;
	public static int choiceSetSize = 20;
	public static String noPt = "C_NOPT";
	
	public static HashMap<String, String> dictionnary = new HashMap<String, String>();
	public static String agentId = "agentId";
	
	
	
	/*#######################################GTFS dictionnary######################################*/
	public static String tripId = "trip_id";
	public static String routeId = "route_id";
	public static String stopId = "stop_id";
	public static String lat = "stop_lat";
	public static String lon = "stop_lon";
	public static String directionId = "direction_id";
	public static String stopSequence = "stop_sequence";
	
	
	/*#######################################Dest inference dictionnary######################################*/
	public static int REGULAR = 11;// estimated using the next trip of the day
	public static int LAST_DAY_BUCKLED = 12; // last trip of the day estimated using a buckle on the first trip of the day
	public static int LAST_NEXT_DAY_BUCKLED = 13;// last trip of the day, estimated by buckling the trip chain on the next day
	public static int HISTORY = 14;//was estimated using historical data
	public static int UNLINKED = 30; //could not be estimated because it is not linked and no similar trips were made
	public static int STOP_DONT_EXISTS = 41;
	public static int ROUTE_DONT_EXISTS = 42;
	public static int BOARDING_STOP_DONT_BELONG_TO_ROUTE = 43;
	public static int BOARDED_ON_LAST_STATION = 44;
	public static ArrayList<Integer> inconsistentData = new ArrayList<Integer>();
	
	
	/*#######################################Smartcard data dictionnary######################################*/
	public static String fare = "paid_fare";
	public static String boardingStopId = "stop_id";
	public static String boardingRouteId = "route_id";
	public static String boardingDirectionId = "direction_id";
	public static String boardingDate = "date";
	public static String boardingTime = "transaction_time";
	public static String destinationInferenceCase = "InferenceCase";
	public static String smartcardTripId = "id";
	public static String alightingLat = "alighting_lat";
	public static String alightingLong = "alighting_long";
	public static String boardingLat = "boarding_lat";
	public static String boardingLong = "boarding_long";
	
	/*#######################################geographic area SHP dico######################################*/
	public static String areaId = "id";
	
	
	public UtilsSM(){
		inconsistentData.add(STOP_DONT_EXISTS);
		inconsistentData.add(ROUTE_DONT_EXISTS);
		inconsistentData.add(BOARDING_STOP_DONT_BELONG_TO_ROUTE);
		inconsistentData.add(BOARDED_ON_LAST_STATION);
		
		/*dictionnary.put(UtilsTS.ageGroup, "age");
		dictionnary.put(UtilsTS.sex, "sex");
		dictionnary.put(UtilsTS.mStat, "mStat");
		dictionnary.put(UtilsTS.pers, "nPers");
		dictionnary.put(UtilsTS.inc, "inc");
		dictionnary.put(UtilsTS.cars, "car");
		dictionnary.put(UtilsTS.occupation, "occ");
		dictionnary.put(UtilsTS.edu, "edu");
		dictionnary.put(UtilsTS.motor, "car");*/
		
		dictionnary.put(UtilsTS.motor, "car");
		dictionnary.put(UtilsTS.edu, "edu");
		dictionnary.put(UtilsTS.ageGroup, UtilsTS.ageGroup);
		dictionnary.put(UtilsTS.sex, UtilsTS.sex);
		dictionnary.put(UtilsTS.mStat, UtilsTS.mStat);
		dictionnary.put(UtilsTS.pers, UtilsTS.pers);
		dictionnary.put(UtilsTS.inc, UtilsTS.inc);
		dictionnary.put(UtilsTS.cars, UtilsTS.cars);
		dictionnary.put(UtilsTS.occupation, UtilsTS.occupation);
		dictionnary.put(UtilsTS.edu, UtilsTS.edu);
		dictionnary.put(UtilsTS.motor, UtilsTS.motor);
		
	}
	
	
	
}