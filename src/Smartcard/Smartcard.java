/**
 * 
 */
package Smartcard;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;

import org.joda.time.DateTime;

import ActivityChoiceModel.BiogemeChoice;
import ActivityChoiceModel.BiogemeControlFileGenerator;
import ActivityChoiceModel.UtilsTS;

/**
 * @author Antoine
 *
 */
public class Smartcard extends BiogemeChoice {

	double cardId;
	GTFSStop stationId;
	// public int choiceId;
	protected HashMap<String, ArrayList<String>> myData = new HashMap<String, ArrayList<String>>();
	protected ArrayList<SmartcardTrip> myTrips = new ArrayList<SmartcardTrip>();
	public int columnId;
	public boolean isDistributed = false;
	public int fare;

	public Smartcard() {
		// nest = UtilsTS.stoUser;
	}

	public Smartcard(BiogemeChoice toConvert) {
		biogeme_id = toConvert.biogeme_id;
		biogeme_case_id = toConvert.biogeme_case_id;
		choiceCombination = toConvert.choiceCombination;
		probability = toConvert.probability;
		utility = toConvert.utility;
		nest = UtilsTS.stoUser;
	}

	public void setId(double id) {
		// TODO Auto-generated method stub
		this.cardId = id;
	}

	
	public void processTripChainChoiceId(){
		ArrayList<DateTime> myDates = getDates();
		int nDaysTravelled = 0;
		int firstDepartureTime = 0;
		int lastDepartureTime = 0;
		int nObservedActivities = 0;
		int nUnlinkedTripLegs = 0;
		int nObservedTripLegs = 0;
		int ptFidelity = 0;
		HashMap<String,Integer> stationFrequenciesFirstBoarding = new HashMap<String,Integer>();
		HashMap<String,Integer> stationFrequenciesLastBoarding = new HashMap<String,Integer>();
		
		for (DateTime curDate : myDates) {
			if(curDate.getDayOfWeek() != 6 && curDate.getDayOfWeek() != 7){
				ArrayList<SmartcardTrip> dailyData = getOrderedDailyTransaction(curDate);
				nDaysTravelled++;
				firstDepartureTime += dailyData.get(0).boardingDate.getMinuteOfDay();
				lastDepartureTime += dailyData.get(dailyData.size()-1).boardingDate.getMinuteOfDay();
				incrementCounter(stationFrequenciesFirstBoarding,dailyData.get(0).boardingStop.myId);
				incrementCounter(stationFrequenciesLastBoarding,dailyData.get(dailyData.size()-1).boardingStop.myId);
				
				if(dailyData.size()==1){
					nObservedActivities++;
				}
				else{
					for(int i = 0; i < dailyData.size()-1; i++){
						SmartcardTrip smTp = dailyData.get(i);
						SmartcardTrip nextSmTp = dailyData.get(i+1);
						int deltaT = nextSmTp.boardingDate.getMinuteOfDay() - smTp.boardingDate.getMinuteOfDay();
						if(deltaT >= UtilsSM.timeThreshold){
							nObservedActivities++;
						}
					}
				}
				
				for(SmartcardTrip smTp: dailyData){
					if(smTp.alightingInferrenceCase==UtilsSM.UNLINKED){
						nUnlinkedTripLegs++;
					}
					else if(UtilsSM.inconsistentData.contains(smTp.alightingInferrenceCase)){}
					else{
						nObservedTripLegs++;
					}
				}
			}
		}
		
		stationId = getLivingStation(stationFrequenciesFirstBoarding, stationFrequenciesLastBoarding, firstDepartureTime/nDaysTravelled);
		
		firstDepartureTime = categorizeFirstDepartureTime(firstDepartureTime/nDaysTravelled);
		lastDepartureTime = categorizeLastDepartureTime(lastDepartureTime/nDaysTravelled);
		ptFidelity = categorizePtFidelity(nObservedTripLegs/(nObservedTripLegs+nUnlinkedTripLegs));
		nObservedActivities = categorizeNumberOfActivities(Math.round(nObservedActivities/nDaysTravelled));
		
		HashMap<String,Integer> myCombination = new HashMap<String,Integer>();
		myCombination.put(UtilsTS.firstDep + "Short", firstDepartureTime);
		myCombination.put(UtilsTS.lastDep + "Short", lastDepartureTime);
		myCombination.put(UtilsTS.nAct, nObservedActivities);
		myCombination.put(UtilsTS.fidelPtRange, ptFidelity);
		myCombination.put(UtilsTS.nest, 2);
		biogeme_case_id = BiogemeControlFileGenerator.returnChoiceId(myCombination);
		choiceCombination = myCombination;
	}

	private GTFSStop getLivingStation(HashMap<String, Integer> stationFrequenciesFirstBoarding,
			HashMap<String, Integer> stationFrequenciesLastBoarding, int avgFirstDepartureTime) {
		// TODO Auto-generated method stub
		//if first departure is after 2pm, then we consider that the last departure time to be more reliable to find the living location
		if(avgFirstDepartureTime > 840){
			String livingStopId = getMostFrequent(stationFrequenciesFirstBoarding);
			return PublicTransitSystem.myStops.get(livingStopId);
		}
		else{
			String livingStopId = getMostFrequent(stationFrequenciesLastBoarding);
			return PublicTransitSystem.myStops.get(livingStopId);
		}
	}

	private <T> T getMostFrequent(HashMap<T, Integer> counter) {
		// TODO Auto-generated method stub
		T mostFrequentId =null;
		int freq = 0;
		for(T s: counter.keySet()){
			int tempFreq = counter.get(s);
			if(tempFreq>freq){
				freq = tempFreq;
				mostFrequentId = s;
			}
		}
		return mostFrequentId;
	}

	private <T>void incrementCounter(HashMap<T, Integer> counter, T newOccurence) {
		// TODO Auto-generated method stub
		if(counter.containsKey(newOccurence)){
			counter.put(newOccurence, counter.get(newOccurence)+1);
		}
		else{
			counter.put(newOccurence, 1);
		}
	}

	private int categorizeNumberOfActivities(int n) {
		// TODO Auto-generated method stub
		if (n < 3) {
			return n;
		} else {
			return 3;
		}
	}

	private int categorizePtFidelity(double ptFidelity) {
		// TODO Auto-generated method stub
		if (ptFidelity < 0.05) {
			return 0;
		} else if (ptFidelity < 0.95) {
			return 1;
		} else {
			return 2;
		}
	}

	private int categorizeLastDepartureTime(int lastDepartureTime) {
		// TODO Auto-generated method stub
		if (lastDepartureTime <= UtilsSM.eveningPeakHourStart) {
			return 0;
		} else if (lastDepartureTime < UtilsSM.eveningPeakHourEnd) {
			return 1;
		} else if (lastDepartureTime >= UtilsSM.eveningPeakHourEnd) {
			return 2;
		} else {
			System.out.println("--error affecting departure hours");
			return 10;
		}
	}

	private int categorizeFirstDepartureTime(int firstDepartureTime) {
		// TODO Auto-generated method stub
		if (firstDepartureTime <= UtilsSM.morningPeakHourStart) {
			return 0;
		} else if (firstDepartureTime < UtilsSM.morningPeakHourEnd) {
			return 1;
		} else if (firstDepartureTime >= UtilsSM.morningPeakHourEnd) {
			return 2;
		} else {
			System.out.println("--error affecting departure hours");
			return 10;
		}
	}


	/**
	 * For each transaction record of the smart card, it applies the destination inference methodology as described in
	 * Trépanier, Tranchant, Chapleau (2007) Individual Trip Destination Estimation in a Transit Smart Card Automated Fare Collection System.
	 * The function attach, when possible, an alighting stop to the transaction. 
	 * In all cases it attach a destination inferrence code:
	 * <br/>11: destination was found using the next transaction of the day
	 * <br/>12: transaction is the last of the day and the destination was found using the first transaction of the day
	 * <br/>13: transaction is the last of the day and the destination was found using the first transaction of the next day
	 * <br/>14: destination was found using historical data
	 * <br/>30: despite going through all our hypothesis, no destination could be inferred
	 * <br/>41: data inconsistency, the stop id recorded in the transaction does not exists
	 * <br/>42: data inconsistency, the route id recorded in the transaction does not exists
	 * <br/>43: data inconsistency, the stop recorded in the transaction does not belong to the route
	 * <br/>44: data inconsistency, the boarding stop was actually the last of the route
	 * @throws ParseException
	 */

	public void inferDestinations() throws ParseException {
		ArrayList<DateTime> myDates = getDates();
		for (DateTime curDate : myDates) {
			ArrayList<SmartcardTrip> dailyData = getOrderedDailyTransaction(curDate);
			inferRegularTripDestinations(dailyData);
			inferLastTripDestinations(dailyData);
		}
		inferSingleTripDestination();
	}

	/**
	 * Check for the following data inconsistencies, for each transaction record:
	 * <br/>  a) stop Id exists in the GTFS dataset
	 * <br/>  b) route Id exists in the GTFS dataset
	 * <br/>  c) the boarding stop belongs to the boarding route in the GTFS dataset
	 * <br/>  d) the boarding stop is not the last one of its route
	 * <br/>The inconsistency is recorded in the SmartcardTrip.alightingInferrenceCase following the dictionary available in UtilsSM
	 */
	public void checkDataConsistency() {
		// TODO Auto-generated method stub
		for(SmartcardTrip smTp: myTrips){
			ArrayList<GTFSStop> vanishingRoute = smTp.boardingRoute.getVanishingRoute(smTp.boardingStop, smTp.boardingDirection);		
			
			if (!PublicTransitSystem.myStops.containsKey(smTp.boardingStop.myId)) {
				smTp.alightingInferrenceCase = UtilsSM.STOP_DONT_EXISTS;
			}
			else if(!PublicTransitSystem.myRoutes.containsKey(smTp.boardingRoute.myId)){
				smTp.alightingInferrenceCase = UtilsSM.ROUTE_DONT_EXISTS;
			}
			else if(!smTp.boardingRoute.contains(smTp.boardingStop)){
				smTp.alightingInferrenceCase = UtilsSM.BOARDING_STOP_DONT_BELONG_TO_ROUTE;
			}
			else if(vanishingRoute.size() == 0){
				smTp.alightingInferrenceCase = UtilsSM.BOARDED_ON_LAST_STATION;
			}
		}
	}

	private void inferSingleTripDestination() throws ParseException {
		// TODO Auto-generated method stub
		for (SmartcardTrip smTp : myTrips) {
			if (!UtilsSM.inconsistentData.contains(smTp.alightingInferrenceCase) && smTp.alightingStop == null) {
				SmartcardTrip similarTrip = getMostSimilarTrip(smTp);
				if (similarTrip == null) {
					smTp.alightingInferrenceCase = UtilsSM.UNLINKED;
				} else {
					smTp.alightingStop = similarTrip.alightingStop;
					smTp.alightingInferrenceCase = UtilsSM.HISTORY;
				}
			}
		}
	}

	/**
	 * Find the record available in the current month that took place on the
	 * same route, in the same direction, within the shortest time window and
	 * for which a destination could be inferred.
	 * 
	 * @param curIndex
	 *            indicates the index of the data we are processing.
	 * @return the index of the smartcard transaction which is the most similar
	 *         to the smart card transaction being processed, and which has an
	 *         inferred destination.
	 * @throws ParseException
	 * 
	 */

	private SmartcardTrip getMostSimilarTrip(SmartcardTrip trip) throws ParseException {
		// TODO Auto-generated method stub

		double deltaTime = Double.POSITIVE_INFINITY;
		SmartcardTrip mostSimilarTrip = null;

		for (SmartcardTrip smTp: myTrips) {
			if (smTp.myId != trip.myId && 
					smTp.boardingDate.getMonthOfYear() == trip.boardingDate.getMonthOfYear() && 
					smTp.boardingRoute.myId.equals(trip.boardingRoute.myId) && 
					smTp.boardingDirection.equals(trip.boardingDirection) && 
					smTp.alightingStop!=null) {
				if (Math.abs(smTp.boardingDate.getMinuteOfDay() - trip.boardingDate.getMinuteOfDay()) < deltaTime) {
					deltaTime = Math.abs(smTp.boardingDate.getMinuteOfDay() - trip.boardingDate.getMinuteOfDay());
					mostSimilarTrip = smTp;
				}
			}
		}
		return mostSimilarTrip;
	}

	private void inferRegularTripDestinations(ArrayList<SmartcardTrip> dailyData) {
		// TODO Auto-generated method stub

		for (int i = 0; i < dailyData.size()-1; i++) {
			SmartcardTrip curSmartcardTrip = dailyData.get(i);
			SmartcardTrip nextSmartcardTrip = dailyData.get(i + 1);
			GTFSStop alightingStop;
			
			if (!UtilsSM.inconsistentData.contains(curSmartcardTrip.alightingInferrenceCase)
					&& !UtilsSM.inconsistentData.contains(nextSmartcardTrip.alightingInferrenceCase)) {
				ArrayList<GTFSStop> curVanishingRoute = curSmartcardTrip.boardingRoute.getVanishingRoute(curSmartcardTrip.boardingStop, curSmartcardTrip.boardingDirection);
				alightingStop = getClosestStop(nextSmartcardTrip.boardingStop, curVanishingRoute);
				double distMin = alightingStop.getDistance(nextSmartcardTrip.boardingStop);
				if (distMin <= UtilsSM.WALKING_DISTANCE_THRESHOLD) {
					curSmartcardTrip.alightingInferrenceCase = UtilsSM.REGULAR;
					curSmartcardTrip.alightingStop = alightingStop;
				}
			}
		}
	}

	private void inferLastTripDestinations(ArrayList<SmartcardTrip> dailyData) throws ParseException {
		// TODO Auto-generated method stub
		SmartcardTrip lastSmartcardTrip = dailyData.get(dailyData.size() - 1);
		SmartcardTrip firstSmartcardTripOfDay = dailyData.get(0);
		SmartcardTrip firstSmartcardTripOfNextDay = getNextDayFirstTapIn(lastSmartcardTrip);
		ArrayList<GTFSStop> lastVanishingRoute = lastSmartcardTrip.boardingRoute.getVanishingRoute(lastSmartcardTrip.boardingStop, lastSmartcardTrip.boardingDirection);		
		
		if (!UtilsSM.inconsistentData.contains(lastSmartcardTrip.alightingInferrenceCase)) {
			if(dailyData.size() > 1 && !UtilsSM.inconsistentData.contains(firstSmartcardTripOfDay.alightingInferrenceCase)){
				GTFSStop potentialAlighting = getClosestStop(firstSmartcardTripOfDay.boardingStop, lastVanishingRoute);
				double distMin = potentialAlighting.getDistance(firstSmartcardTripOfDay.boardingStop);
				if (distMin <= UtilsSM.WALKING_DISTANCE_THRESHOLD) {
					lastSmartcardTrip.alightingInferrenceCase = UtilsSM.LAST_DAY_BUCKLED;
					lastSmartcardTrip.alightingStop = potentialAlighting;
				}
				else if(firstSmartcardTripOfNextDay == null){}
				else if(!UtilsSM.inconsistentData.contains(firstSmartcardTripOfNextDay.alightingInferrenceCase)){
					potentialAlighting = getClosestStop(firstSmartcardTripOfNextDay.boardingStop, lastVanishingRoute);
					distMin = potentialAlighting.getDistance(firstSmartcardTripOfNextDay.boardingStop);
					if (distMin <= UtilsSM.WALKING_DISTANCE_THRESHOLD) {
						lastSmartcardTrip.alightingInferrenceCase = UtilsSM.LAST_NEXT_DAY_BUCKLED;
						lastSmartcardTrip.alightingStop = potentialAlighting;
					}
				}
			}
		}
		else if(firstSmartcardTripOfNextDay == null){}
		else if(dailyData.size()==1 && !UtilsSM.inconsistentData.contains(firstSmartcardTripOfNextDay.alightingInferrenceCase)){
			GTFSStop potentialAlighting = getClosestStop(firstSmartcardTripOfNextDay.boardingStop, lastVanishingRoute);
			double distMin = potentialAlighting.getDistance(firstSmartcardTripOfNextDay.boardingStop);
			if (distMin <= UtilsSM.WALKING_DISTANCE_THRESHOLD) {
				lastSmartcardTrip.alightingInferrenceCase = UtilsSM.LAST_NEXT_DAY_BUCKLED;
				lastSmartcardTrip.alightingStop = potentialAlighting;
			}
		}
	}

	private SmartcardTrip getNextDayFirstTapIn(SmartcardTrip smtp) throws ParseException {
		// TODO Auto-generated method stub
		ArrayList<SmartcardTrip> nextDayData = getOrderedDailyTransaction(smtp.boardingDate.plusDays(1));
		if (nextDayData.size() != 0) {
			return nextDayData.get(0);
		}
		return null;
	}
	


	/**
	 * Compute the minimal distance between the bus route inputed and the bus stop inputed and
	 * return the stop which realizes this minimal distance.
	 * 
	 * @param stop stop which doesn't belong to the route
	 * @param route the ArrayList<GTFSStop> represents a bus route
	 * @return the stop in the inputed route which realizes the minimal distance to the inputed stop.
	 */
	private GTFSStop getClosestStop(GTFSStop stop, ArrayList<GTFSStop> route) {
		// TODO Auto-generated method stub
		double distMin = Double.POSITIVE_INFINITY;
		GTFSStop closestStop = null;
		for (GTFSStop s : route) {
			double dist = s.getDistance(stop);
			if (dist < distMin) {
				distMin = dist;
				closestStop = s;
			}
		}
		return closestStop;
	}

	private ArrayList<SmartcardTrip> getOrderedDailyTransaction(DateTime curDate) {
		// TODO Auto-generated method stub
		ArrayList<SmartcardTrip> dailyTrips = new ArrayList<SmartcardTrip>();
		
		for(SmartcardTrip smtp: myTrips){
			if(isSameDay(curDate, smtp.boardingDate)){
				dailyTrips.add(smtp);
			}
			dailyTrips.sort(null);
		}
		return dailyTrips;
	}


	private ArrayList<DateTime> getDates() {
		// TODO Auto-generated method stub
		ArrayList<DateTime> dates = new ArrayList<DateTime>();
		for (int i = 0; i < myTrips.size(); i++) {
			DateTime curDate = myTrips.get(i).boardingDate;
			if(dates.isEmpty()){
				dates.add(curDate);
			}
			else{
				boolean toAdd = true;
				for(int j = 0; j < dates.size(); j++){
					DateTime d = dates.get(j);
					if(isSameDay(d,curDate)){
						toAdd = false;
					}
				}
				if(toAdd){
					dates.add(curDate);
				}
			}
		}
		return dates;
	}

	private boolean isSameDay(DateTime d1, DateTime d2){
		if((d2.getYear() == d2.getYear()) && (d1.getDayOfYear() == d2.getDayOfYear())){
			return true;
		}
		return false;
	}

	public void assignFaretype() {
		// TODO Auto-generated method stub
		HashMap<Integer,Integer> fareFrequency = new HashMap<Integer,Integer>();
		for(SmartcardTrip smTp: myTrips){
			incrementCounter(fareFrequency,smTp.fare);
		}
		fare = getMostFrequent(fareFrequency);
	}
}
