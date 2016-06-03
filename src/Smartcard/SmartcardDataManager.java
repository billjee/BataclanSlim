/**
 * 
 */
package Smartcard;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.joda.time.DateTime;

import ActivityChoiceModel.BiogemeAgent;
import ActivityChoiceModel.BiogemeControlFileGenerator;
import ActivityChoiceModel.DataManager;
import ActivityChoiceModel.UtilsTS;
import Utils.OutputFileWritter;
import Utils.Utils;

/**
 * @author Antoine
 *
 */
public class SmartcardDataManager extends DataManager {

	BiogemeControlFileGenerator myCtrlFileGenerator = new BiogemeControlFileGenerator();
	ArrayList<Smartcard> mySmartcards = new ArrayList<Smartcard>();
	SimpleDateFormat sdf = new SimpleDateFormat(UtilsSM.DATE_FORMAT);

	public SmartcardDataManager() {

	}

	public SmartcardDataManager(BiogemeControlFileGenerator biogemeGenerator) {
		myCtrlFileGenerator = biogemeGenerator;
	}

	public void prepareSmartcards(String smartcardData) throws IOException, ParseException {
		initialize(smartcardData);
		System.out.println("--smartcard manager initialized");
		createSmartcards();
		System.out.println("--smartcard created");
		assignFareTypes();
		assignColumnIndex();
	}

	private void assignFareTypes() {
		// TODO Auto-generated method stub
		for(Smartcard sm: mySmartcards){
			sm.assignFaretype();
		}
	}

	// Note that we are working at the route level to infer destination.
	// HOwever, with a bit more work we could infer destination at bus trip
	// level, it would allow us to
	// condsider for scheduled arrival time at the station has seen in the GTFS.
	public void inferDestinations() throws ParseException {
		for (Smartcard sm : mySmartcards) {
			sm.checkDataConsistency();
			sm.inferDestinations();
		}
	}

	///// NEW WAY


	private void assignColumnIndex() {
		// TODO Auto-generated method stub
		int column = 0;
		for (Smartcard tempS : mySmartcards) {
			tempS.columnId = column;
			column++;
		}
	}


	/**
	 * Creates the smart cards object out of the smart card dataset and fill it
	 * with observed trips.
	 * 
	 * @throws ParseException
	 */
	private void createSmartcards() throws ParseException {
		System.out.println("-- number of records to process " + myData.get(UtilsSM.cardId).size());
		for (int i = 0; i < myData.get(UtilsSM.cardId).size(); i++) {
			Smartcard curSm = getSmartcard(i);
			SmartcardTrip newTrip = createTrip(i);
			curSm.myTrips.add(newTrip);
		}
		System.out.println("--number of smart cards " + mySmartcards.size());
	}

	/**
	 * This function create a trip instance for the record i from the smart card
	 * dataset. It link the smart card trip to the boarding stop. It also create
	 * read the timestamp out of a "date" column and a "time" colum, concatenate
	 * thise information and attach the timestamp to the trip.
	 * 
	 * @param i
	 *            the rank of the smart card dataset being processed
	 * @return the a well formatted ready to use smart card trip that just need
	 *         to be enriched. The whole data information is available in the
	 *         HashMap<String,String> myData.
	 * @throws ParseException
	 */
	private SmartcardTrip createTrip(int i) throws ParseException {
		// TODO Auto-generated method stub
		SmartcardTrip trip = new SmartcardTrip();
		for (String s : myData.keySet()) {
			trip.myData.put(s, myData.get(s).get(i));
		}
		String curBoardingStopId = myData.get(UtilsSM.boardingStopId).get(i);
		String curBoardingRouteId = myData.get(UtilsSM.boardingRouteId).get(i);
		String curDirectionId = myData.get(UtilsSM.boardingDirectionId).get(i);
		int curId = Integer.parseInt(myData.get(UtilsSM.smartcardTripId).get(i));
		int curFare = Integer.parseInt(myData.get(UtilsSM.fare).get(i));
		
		GTFSStop curBoardingStop =  PublicTransitSystem.myStops.get(curBoardingStopId);
		GTFSRoute curBoardingRoute = PublicTransitSystem.myRoutes.get(curBoardingRouteId);
		
		//First we check if the data was not corrupted (the smartcard may not correspond to the GTFS data)
		trip.myId = curId;
		trip.boardingStop = curBoardingStop;
		trip.boardingRoute = curBoardingRoute;
		trip.boardingDirection = curDirectionId;
		trip.fare = curFare;
		

		String curDate = myData.get(UtilsSM.boardingDate).get(i);
		String curTime = myData.get(UtilsSM.boardingTime).get(i);
		String curDateTime = curDate + " at " + curTime;
		Date d = sdf.parse(curDateTime);
		DateTime d2 = new DateTime(d);
		trip.boardingDate = d2;
		return trip;
	}

	/**
	 * This method check if the smart card corresponding to line i is already
	 * created and stored in mySmartcards. If it is an existing smart card, then
	 * it returns it. Otherwise, it create a new one, add it to the smartcards
	 * and return it.
	 * 
	 * @param i
	 * @return
	 */
	public Smartcard getSmartcard(int i) {
		double id = Double.parseDouble(myData.get(UtilsSM.cardId).get(i));
		for (Smartcard currS : mySmartcards) {
			if (currS.cardId == id) {
				return currS;
			}
		}
		Smartcard newSm = new Smartcard();
		newSm.setId(Double.parseDouble(myData.get(UtilsSM.cardId).get(i)));
		mySmartcards.add(newSm);
		return newSm;
	}


	public void printSmartcards(String path) throws IOException {
		// TODO Auto-generated method stub
		OutputFileWritter write = new OutputFileWritter();
		write.OpenFile(path);
		SmartcardTrip tempSmTp = mySmartcards.get(0).myTrips.get(0);
		String header = new String();
		for(String key: tempSmTp.myData.keySet()){
			header+= key + Utils.COLUMN_DELIMETER ;
		}
		header += UtilsSM.boardingLat
				+Utils.COLUMN_DELIMETER + UtilsSM.boardingLong
				+Utils.COLUMN_DELIMETER + UtilsSM.alightingStop
				+Utils.COLUMN_DELIMETER + UtilsSM.destinationInferenceCase
				+Utils.COLUMN_DELIMETER + UtilsSM.alightingLat
				+Utils.COLUMN_DELIMETER + UtilsSM.alightingLong;
		write.WriteToFile(header);
		
		for(Smartcard sm: mySmartcards){
			for(SmartcardTrip smTp: sm.myTrips){
				String newLine = new String();
				for(String key: smTp.myData.keySet()){
					newLine+= smTp.myData.get(key) + Utils.COLUMN_DELIMETER;
				}
				if(smTp.alightingStop == null){
					newLine += smTp.boardingStop.lat
							+ Utils.COLUMN_DELIMETER + smTp.boardingStop.lon
							+ Utils.COLUMN_DELIMETER
							+ Utils.COLUMN_DELIMETER + smTp.alightingInferrenceCase	
							+ Utils.COLUMN_DELIMETER
							+ Utils.COLUMN_DELIMETER ;
				}
				else{
					newLine += smTp.boardingStop.lat
							+ Utils.COLUMN_DELIMETER + smTp.boardingStop.lon
							+ Utils.COLUMN_DELIMETER + smTp.alightingStop.myId
							+ Utils.COLUMN_DELIMETER + smTp.alightingInferrenceCase
							+ Utils.COLUMN_DELIMETER + smTp.alightingStop.lat
							+ Utils.COLUMN_DELIMETER + smTp.alightingStop.lon;
				}
				write.WriteToFile(newLine);
			}
		}
		write.CloseFile();
	}

	public ArrayList<Smartcard> processTripChainChoiceIds() {
		// TODO Auto-generated method stub
		for(Smartcard sm: mySmartcards){
			sm.processTripChainChoiceId();
		}
		return null;
	}
}
