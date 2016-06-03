/**
 * 
 */
package Smartcard;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;

import org.geotools.referencing.CRS;
import org.geotools.referencing.GeodeticCalculator;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.opengis.geometry.MismatchedDimensionException;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.TransformException;

import ActivityChoiceModel.BiogemeAgent;
import ActivityChoiceModel.BiogemeChoice;
import ActivityChoiceModel.BiogemeControlFileGenerator;
import ActivityChoiceModel.BiogemeHypothesis;
import ActivityChoiceModel.BiogemeSimulator;
import ActivityChoiceModel.UtilsTS;
import Associations.HungarianAlgorithm;
import Utils.OutputFileWritter;
import Utils.Utils;

/**
 * @author Antoine
 *
 */
public class PublicTransitSystem {
	
	static DefaultGeographicCRS myCRS = DefaultGeographicCRS.WGS84;
	public static GeodeticCalculator gc = new GeodeticCalculator(myCRS);
	
	static HashMap<String, GTFSStop> myStops = new HashMap<String, GTFSStop>();
	public static HashMap<String, GTFSTrip> myTrips = new HashMap<String, GTFSTrip>();
	public static HashMap<String, GTFSRoute> myRoutes = new HashMap<String, GTFSRoute>();
	
	
	public static ArrayList<Smartcard> mySmartcards = new ArrayList<Smartcard>();
	
	public static HashMap<String,ArrayList<BiogemeAgent>> zonalPopulation = new HashMap<String,ArrayList<BiogemeAgent>>();
	public static HashMap<String, ArrayList<Smartcard>> zonalChoiceSets = new HashMap<String, ArrayList<Smartcard>>();
	
	public static ArrayList<BiogemeAgent> myPopulation = new ArrayList<BiogemeAgent>();
	PopulationWriter myPopWriter = new PopulationWriter();
	double[][] costMatrix;
	
	/**
	 * The geoDico has zonal identifiers as keys and an ArrayList of close station identifiers.
	 */
	public static HashMap<String, ArrayList<String>> geoDico = new HashMap<String,ArrayList<String>>();
	public static BiogemeSimulator mySimulator = new BiogemeSimulator();

	public static BiogemeControlFileGenerator myCtrlGen;
	
	public PublicTransitSystem(){
		
	}
	/**
	 * Load useful GTFS components such as the trips table, the stops table, the route table and the stop_times table.
	 * Be aware that all GTFS text file should be encoded in UTF-8.
	 * @param pathGTFSTrips
	 * @param pathGTFSStops
	 * @param pathGTFSStopTimes
	 * @param pathGTFSRoutes
	 * @throws IOException
	 */
	public void initializePTsystemForDestinationInference(
			String pathGTFSTrips,
			String pathGTFSStops,
			String pathGTFSStopTimes,
			String pathGTFSRoutes) throws IOException{
		GTFSLoader myGTFSLoader = new GTFSLoader();
		myStops = myGTFSLoader.getStops(pathGTFSStops);
		myTrips = myGTFSLoader.getTrips(pathGTFSTrips);
		myRoutes = myGTFSLoader.getRoutes(pathGTFSRoutes);
		myGTFSLoader.constructRouteItinerary(myTrips, myRoutes, myStops, pathGTFSStopTimes);
	}
	
	
	

	public void initializeForSocioInference(BiogemeControlFileGenerator ctrlGenerator, 
			String pathSmartcard, 
			String pathPop,
			String pathModel,
			String pathGtfsStops,
			String pathLocalZonesShp) throws IOException, ParseException, MismatchedDimensionException, TransformException, FactoryException{
		
		GTFSLoader myLoader = new GTFSLoader();
		myStops = myLoader.getStops(pathGtfsStops);
		
		CoordinateReferenceSystem targetCRS;
		
		try{
			targetCRS = CRS.decode(UtilsSM.CRS);
		}
		catch(NoSuchAuthorityCodeException e){
			targetCRS = CRS.decode("AUTO2:42001,"+myStops.get(0).lat+","+myStops.get(0).lon);
		}
		
		
		myCtrlGen = ctrlGenerator;
		
		mySimulator.extractChoiceUniverse();
		mySimulator.setHypothesis();
		mySimulator.importBiogemeModel(pathModel);
		mySimulator.importNest(pathModel);
		
		SmartcardDataManager mySmartcardManager = new SmartcardDataManager(myCtrlGen);
		mySmartcardManager.prepareSmartcards(pathSmartcard);
		mySmartcards = mySmartcardManager.processTripChainChoiceIds();
		
		
		PopulationDataManager myPopGenerator = new PopulationDataManager();
		myPopulation = myPopGenerator.getAgents(pathPop);
		
		GeoDicoManager myGeoDico = new GeoDicoManager();
		geoDico = myGeoDico.getDico(pathLocalZonesShp, myStops, targetCRS);
		
		
	}
	
	public void initializeForSocioInference(BiogemeControlFileGenerator ctrlGenerator, 
			String pathSmartcard, 
			String pathGeoDico, 
			String pathPop,
			String pathModel) throws IOException, ParseException{
		myCtrlGen = ctrlGenerator;
		SmartcardDataManager mySmartcardManager = new SmartcardDataManager(myCtrlGen);
		GeoDicoManager myGeoDico = new GeoDicoManager();
		PopulationDataManager myPopGenerator = new PopulationDataManager();
		
		mySimulator.extractChoiceUniverse();
		mySimulator.setHypothesis();
		mySimulator.importBiogemeModel(pathModel);
		mySimulator.importNest(pathModel);
		
		mySmartcardManager.prepareSmartcards(pathSmartcard);
		mySmartcards = mySmartcardManager.processTripChainChoiceIds();
		geoDico = myGeoDico.getDico(pathGeoDico);
		System.out.println("--geodico assigned");
		myPopulation = myPopGenerator.getAgents(pathPop);
		
	}
	
	
	
	public void createZonalSmartcardIndex(){
		for(String currZone : geoDico.keySet()){
			ArrayList<String> closeStations = geoDico.get(currZone);
			ArrayList<Smartcard> zonalChoiceSet = new ArrayList<Smartcard>();
			Iterator<Smartcard> universalChoiceSet = mySmartcards.iterator();
			while(universalChoiceSet.hasNext()){
				Smartcard currCard = universalChoiceSet.next();
				if(closeStations.contains(currCard.stationId.myId.trim())){
					zonalChoiceSet.add(currCard);
				}
			}
			//System.out.println(zonalChoiceSet.size());
			zonalChoiceSets.put(currZone,zonalChoiceSet);
		}
	}
	
	private HashMap<String, ArrayList<Smartcard>> createZonalSmartcardIndex(ArrayList<Smartcard> mySmartcards){
		
		HashMap<String, ArrayList<Smartcard>> localZonalChoiceSets = new HashMap<String, ArrayList<Smartcard>>();
		for(String currZone : geoDico.keySet()){
			ArrayList<String> closeStations = geoDico.get(currZone);
			ArrayList<Smartcard> zonalChoiceSet = new ArrayList<Smartcard>();
			Iterator<Smartcard> universalChoiceSet = mySmartcards.iterator();
			while(universalChoiceSet.hasNext()){
				Smartcard currCard = universalChoiceSet.next();
				if(closeStations.contains(currCard.stationId.myId.trim())){
					zonalChoiceSet.add(currCard);
				}
			}
			//System.out.println(zonalChoiceSet.size());
			localZonalChoiceSets.put(currZone,zonalChoiceSet);
		}
		return localZonalChoiceSets;
	}
	
	public void createZonalPopulationIndex() {
		// TODO Auto-generated method stub
		for(String currZone : geoDico.keySet()){
			Iterator<BiogemeAgent> it = myPopulation.iterator();
			zonalPopulation.put(currZone, new ArrayList<BiogemeAgent>());
			while(it.hasNext()){
				BiogemeAgent currAgent = it.next();
				if(currAgent.myAttributes.get(UtilsSM.zoneId).trim().equals(currZone.trim())){
					zonalPopulation.get(currZone).add(currAgent);
				}
			}
		}
	}
	
	
	private double[][] createLocalCostMatrix(
			ArrayList<BiogemeAgent> myPopulation, 
			ArrayList<Smartcard> mySmartcards, 
			HashMap<String, ArrayList<Smartcard>> zonalSmartcardIndex
			) throws IOException{
		
		int n = 0;
		int N = myPopulation.size();
		int M = mySmartcards.size();
		int rowIndex = 0;
		double[][] costMatrix = new double[N][N];
		int count = 0;
		for(BiogemeAgent person: myPopulation){
			double zoneId = Double.parseDouble(person.myAttributes.get(UtilsSM.zoneId));
			if(zonalSmartcardIndex.containsKey(zoneId)){
				//ArrayList<Smartcard> choiceSet = person.generateChoiceSet(UtilsSM.choiceSetSize, zonalSmartcardIndex);
				ArrayList<Smartcard> choiceSet = person.generateChoiceSet(zonalSmartcardIndex);
				person.computeUtilities(choiceSet);
				//person.processSmartcardChoiceSet(choiceSet);
				//person.createAndWeighChoiceSet(UtilsSM.choiceSetSize, zonalSmartcardIndex );
				costMatrix[rowIndex] = person.writeCosts(N, M);
				rowIndex++;
			}
			else{
				double[] newRow = new double[myPopulation.size()];
				for(int i = 0; i < myPopulation.size(); i++){newRow[i] = Double.MAX_VALUE;}
				costMatrix[rowIndex] = newRow;
				rowIndex++;
				System.out.println("--this guy shouldn't be there...");
			}
			if(count%1000 ==0){System.out.println("processed agent: " + count);}
			count++;
		}
		return costMatrix;
	}
	
	
	

	private void assignColumnIndex(ArrayList<Smartcard> mySmartcards) {
		// TODO Auto-generated method stub
		int column = 0;
		for(Smartcard tempS : mySmartcards){
			tempS.columnId = column;
			column++;
		}
	}
	
	public void processMatchingOnPtRiders() throws IOException {
		// TODO Auto-generated method stub
		HashMap<String, ArrayList<Smartcard>> zonalSmartcardIndex = zonalChoiceSets;// createZonalSmartcardIndex(mySmartcards);
		System.out.println("--prepare to get pt riders");
		ArrayList<GTFSStop> gTFSStops = new ArrayList<GTFSStop>();
		for(GTFSStop st: myStops.values()){
			gTFSStops.add(st);
		}
		ArrayList<BiogemeAgent> ptRiders = getPtRiders(gTFSStops);
		System.out.println("--pt riders generated");
		
		
		ArrayList<Smartcard> consistentSmartcards = sortSmartcard(mySmartcards);
		assignColumnIndex(consistentSmartcards);
		
		double[][] costMatrix = createLocalCostMatrix(ptRiders, consistentSmartcards, zonalSmartcardIndex);
	
					
		int[] result;
		HungarianAlgorithm hu =new HungarianAlgorithm(costMatrix);
		result=hu.execute();

		for(int j=0;j<result.length;j++){
			if(consistentSmartcards.size()>result[j]){
				consistentSmartcards.get(result[j]).isDistributed = true;
				ptRiders.get(j).isDistributed = true;
				ptRiders.get(j).smartcard = consistentSmartcards.get(result[j]).cardId;
			}
			else{
			}
		} 
	}
	
	public void processMatchingOnPtRidersByBatch(int n) throws IOException {
		// TODO Auto-generated method stub
		HashMap<String, ArrayList<Smartcard>> zonalSmartcardIndex;
		System.out.println("--prepare to get pt riders");
		
		ArrayList<ArrayList<GTFSStop>> batches = new ArrayList<ArrayList<GTFSStop>>();
		for(int i =0; i < n; i++){ batches.add(new ArrayList<GTFSStop>());}
		int batchCount = 0;
		for(GTFSStop st: myStops.values()){
			if(st.getSmartcards().size()!= 0){
				batches.get(batchCount).add(st);
				batchCount++;
				if(batchCount==n){batchCount = 0;}
			}
		}
		
		for(ArrayList<GTFSStop> batch: batches){
			ArrayList<BiogemeAgent> ptRidersBatch = getPtRiders(batch);
			ArrayList<Smartcard> smartcardsBatch = getSmartcard(batch);
			resetDistributionIndicator(ptRidersBatch);
			smartcardsBatch = sortSmartcard(smartcardsBatch);
			assignColumnIndex(smartcardsBatch);
			zonalSmartcardIndex = createZonalSmartcardIndex(smartcardsBatch);
			System.out.println("--pt riders generated: " + ptRidersBatch.size());
			
			double[][] costMatrix = createLocalCostMatrix(ptRidersBatch, smartcardsBatch, zonalSmartcardIndex);
			int[] result;
			HungarianAlgorithm hu =new HungarianAlgorithm(costMatrix);
			result=hu.execute();

			for(int j=0;j<result.length;j++){
				if(smartcardsBatch.size()>result[j]){
					smartcardsBatch.get(result[j]).isDistributed = true;
					ptRidersBatch.get(j).isDistributed = true;
					ptRidersBatch.get(j).smartcard = smartcardsBatch.get(result[j]).cardId;
				}
				else{
				}
			} 
		}
	}
	
	private void resetDistributionIndicator(ArrayList<BiogemeAgent> ptRidersBatch) {
		// TODO Auto-generated method stub
		for(BiogemeAgent ag: ptRidersBatch){
			ag.isDistributed = false;
		}
	}

	private ArrayList<Smartcard> getSmartcard(ArrayList<GTFSStop> batch) {
		// TODO Auto-generated method stub
		ArrayList<Smartcard> temp = new ArrayList<Smartcard>();
		for(GTFSStop st: batch){
			
			temp.addAll(st.getSmartcards());
		}
		return temp;
	}

	private ArrayList<Smartcard> sortSmartcard(ArrayList<Smartcard> mySmartcards) {
		// TODO Auto-generated method stub
		ArrayList<Smartcard> sorted = new ArrayList<Smartcard>();
		for(Smartcard sm: mySmartcards){
			sorted.add(sm);
		}
		return sorted;
	}

	public void processMatchingStationByStation() throws IOException {
		// TODO Auto-generated method stub
		int count = 0;
		for(String key : myStops.keySet()){
			count++;
			ArrayList<Smartcard> currLocalSmartcards = new ArrayList<Smartcard>();
			ArrayList<BiogemeAgent> currLocalPopulation = new ArrayList<BiogemeAgent>();
			GTFSStop currStation = myStops.get(key);
			currLocalSmartcards.addAll(currStation.getSmartcards());
			if(currLocalSmartcards.size() != 0){
				currLocalPopulation.addAll(currStation.getLocalPopulation());
				
				assignColumnIndex(currLocalSmartcards);
				HashMap<String, ArrayList<Smartcard>> zonalSmartcardIndex = createZonalSmartcardIndex(currLocalSmartcards);
				
				
				double[][] costMatrix = createLocalCostMatrix(currLocalPopulation, currLocalSmartcards, zonalSmartcardIndex);
				//double[][] costMatrix = createLocalCostMatrix(currLocalPopulation, currLocalSmartcards, zonalSmartcardIndex);
				
				System.out.println("count : " + count + 
						" station " + key + 
						" with " + currLocalSmartcards.size() +" local smart cards " +
						"costMatrix size " + costMatrix.length);
				int[] result;
				HungarianAlgorithm hu =new HungarianAlgorithm(costMatrix);
				result=hu.execute();

				for(int j=0;j<result.length;j++){
					if(currLocalSmartcards.size()>result[j]){
						currLocalSmartcards.get(result[j]).isDistributed = true;
						currLocalPopulation.get(j).isDistributed = true;
						currLocalPopulation.get(j).smartcard = currLocalSmartcards.get(result[j]).cardId;
					}
					else{
					}
				} 
			}
		}
	}
	


	private ArrayList<BiogemeAgent> getPtRiders(ArrayList<GTFSStop> batch) {
		// TODO Auto-generated method stub
		int multPool =1;
		
		ArrayList<BiogemeAgent> ptRiders = new ArrayList<BiogemeAgent>();
		Random r = new Random();
		
		for(GTFSStop st: batch){

			ArrayList<Smartcard> localSm = st.getSmartcards();
			if(localSm.size()>0){
				ArrayList<BiogemeAgent> localPop = st.getLocalPopulation();
				
				if(!Utils.occupationCriterion){
					int i = 0;
					while(i<multPool * localSm.size()){
						int n = r.nextInt(localPop.size());
						BiogemeAgent curAgent = localPop.get(n);
						if(curAgent.isStoRider()&& !curAgent.isDistributed){
							curAgent.isDistributed = true;
							ptRiders.add(curAgent);
							i++;
						}
					}
				}
				else{
					ArrayList<Smartcard> regularCards = getRegularCard(localSm);
					ArrayList<Smartcard>  studentCards = getStudentCard(localSm);
					ArrayList<Smartcard>  retireeCards = getRetireeCard(localSm);
					ArrayList<BiogemeAgent> regular = getRegularPersons(localPop);
					ArrayList<BiogemeAgent> student = getStudentPersons(localPop);
					ArrayList<BiogemeAgent> retiree = getRetireePersons(localPop);
					int regCount = 0;
					int stdtCount = 0;
					int retCount = 0;
					while(regCount<multPool *regularCards.size()){
						int n = r.nextInt(regular.size());
						BiogemeAgent curAgent = regular.get(n);
						int occupation = Integer.parseInt(curAgent.myAttributes.get(UtilsSM.dictionnary.get(UtilsTS.occupation)));
						if(curAgent.isStoRider()&& !curAgent.isDistributed && (occupation == 0 || occupation == 3)){
							curAgent.isDistributed = true;
							ptRiders.add(curAgent);
							regCount++;
						}
					}
					while(stdtCount<multPool *studentCards.size()){
						int n = r.nextInt(student.size());
						BiogemeAgent curAgent = student.get(n);
						int occupation = Integer.parseInt(curAgent.myAttributes.get(UtilsSM.dictionnary.get(UtilsTS.occupation)));
						if(curAgent.isStoRider()&& !curAgent.isDistributed && occupation == 1 ){
							curAgent.isDistributed = true;
							ptRiders.add(curAgent);
							stdtCount++;
						}
					}
					while(retCount<multPool *retireeCards.size()){
						int n = r.nextInt(retiree.size());
						BiogemeAgent curAgent = retiree.get(n);
						int occupation = Integer.parseInt(curAgent.myAttributes.get(UtilsSM.dictionnary.get(UtilsTS.occupation)));
						if(curAgent.isStoRider()&& !curAgent.isDistributed && occupation == 2){
							curAgent.isDistributed = true;
							ptRiders.add(curAgent);
							retCount++;
						}
					}
				}
			}
		}		
		return ptRiders;
	}
	

	private ArrayList<BiogemeAgent> getRetireePersons(ArrayList<BiogemeAgent> localPop) {
		// TODO Auto-generated method stub
		ArrayList<BiogemeAgent> temp = new ArrayList<BiogemeAgent>();
		for(BiogemeAgent ag: localPop){
			int occupation = Integer.parseInt(ag.myAttributes.get(UtilsSM.dictionnary.get(UtilsTS.occupation)));
			if(occupation == 2){
				temp.add(ag);
			}
		}
		return temp;
	}

	private ArrayList<BiogemeAgent> getStudentPersons(ArrayList<BiogemeAgent> localPop) {
		// TODO Auto-generated method stub
		ArrayList<BiogemeAgent> temp = new ArrayList<BiogemeAgent>();
		for(BiogemeAgent ag: localPop){
			int occupation = Integer.parseInt(ag.myAttributes.get(UtilsSM.dictionnary.get(UtilsTS.occupation)));
			if(occupation == 1){
				temp.add(ag);
			}
		}
		return temp;
	}

	private ArrayList<BiogemeAgent> getRegularPersons(ArrayList<BiogemeAgent> localPop) {
		// TODO Auto-generated method stub
		ArrayList<BiogemeAgent> temp = new ArrayList<BiogemeAgent>();
		for(BiogemeAgent ag: localPop){
			int occupation = Integer.parseInt(ag.myAttributes.get(UtilsSM.dictionnary.get(UtilsTS.occupation)));
			if(occupation == 0 || occupation == 3){
				temp.add(ag);
			}
		}
		return temp;
	}

	private ArrayList<Smartcard> getRetireeCard(ArrayList<Smartcard> localSm) {
		// TODO Auto-generated method stub
		ArrayList<Smartcard> temp = new ArrayList<Smartcard>();
		for(Smartcard sm:localSm){
			if(sm.fare == 2){
				temp.add(sm);
			}
		}
		return temp;
	}

	private ArrayList<Smartcard> getStudentCard(ArrayList<Smartcard> localSm) {
		// TODO Auto-generated method stub
		ArrayList<Smartcard> temp = new ArrayList<Smartcard>();
		for(Smartcard sm:localSm){
			if(sm.fare == 1){
				temp.add(sm);
			}
		}
		return temp;
	}

	private ArrayList<Smartcard> getRegularCard(ArrayList<Smartcard> localSm) {
		// TODO Auto-generated method stub
		ArrayList<Smartcard> temp = new ArrayList<Smartcard>();
		for(Smartcard sm:localSm){
			if(sm.fare == 0){
				temp.add(sm);
			}
		}
		return temp;
	}

	public void localRandomMatch() {
		// TODO Auto-generated method stub
		
		//TO BE CONTINUED
		int count = 0;
		Random random = new Random();
		for(String key : myStops.keySet()){
			count++;
			ArrayList<Smartcard> currLocalSmartcards = new ArrayList<Smartcard>();
			ArrayList<BiogemeAgent> currLocalPopulation = new ArrayList<BiogemeAgent>();
			GTFSStop currStation = myStops.get(key);

	
			currLocalSmartcards.addAll(currStation.getSmartcards());
			if(currLocalSmartcards.size() != 0){
				currLocalPopulation.addAll(currStation.getLocalPopulation());
				
				Iterator<Smartcard> it = currLocalSmartcards.iterator();
				while(it.hasNext()){
					Smartcard sm = it.next();
					int r = random.nextInt(currLocalPopulation.size());
					currLocalPopulation.get(r).smartcard = sm.cardId;
					sm.isDistributed = true;
					currLocalPopulation.get(r).isDistributed = true;
				}
			}
			
		}
	}
	
	public void globalRandomMatch(){
		Random random = new Random();
		for(int i = 0; i < mySmartcards.size(); i++){
			int r = random.nextInt(myPopulation.size());
			BiogemeAgent ag = myPopulation.get(r);
			Smartcard sm = mySmartcards.get(i);
			if(ag.isDistributed){
				i=i-1;
			}
			else{
				sm.isDistributed = true;
				ag.isDistributed = true;
				ag.smartcard = sm.cardId;
			}
		}
	}

	

	public void printSmartcards(String path) throws IOException {
		// TODO Auto-generated method stub
		OutputFileWritter write = new OutputFileWritter();
		write.OpenFile(path);
		Smartcard tempSm = mySmartcards.get(0);
		BiogemeAgent tempAgent = myPopulation.get(0);
		String header = new String();
		for(String key : tempAgent.myAttributes.keySet()){
			header+= key + Utils.COLUMN_DELIMETER;
		}
		header+= UtilsSM.cardId + Utils.COLUMN_DELIMETER
				+UtilsSM.stationId + Utils.COLUMN_DELIMETER
				+ UtilsSM.fare + Utils.COLUMN_DELIMETER
				+ UtilsTS.choice;
		for(String key: mySmartcards.get(0).myAttributes.keySet()){
			header+= Utils.COLUMN_DELIMETER + key;
		}
		write.WriteToFile(header);
		
		for(BiogemeAgent currAgent: myPopulation){
			if(currAgent.smartcard != 0){
				String newLine = new String();
				Smartcard sm = getSmartcard(currAgent);
				for(String key: currAgent.myAttributes.keySet()){
					newLine+= currAgent.myAttributes.get(key) + Utils.COLUMN_DELIMETER;
				}
				newLine+= sm.cardId + Utils.COLUMN_DELIMETER
						+ sm.stationId.myId + Utils.COLUMN_DELIMETER
						+ sm.fare + Utils.COLUMN_DELIMETER +
						sm.getConstantName();
				for(String key : sm.myAttributes.keySet()){
					newLine+= Utils.COLUMN_DELIMETER +sm.myAttributes.get(key) ;
				}
				write.WriteToFile(newLine);
			}
		}
		write.CloseFile();
		
	}

	private Smartcard getSmartcard(BiogemeAgent currAgent) {
		// TODO Auto-generated method stub
		if(currAgent.smartcard != 0){
			for(Smartcard sm: mySmartcards){
				if(sm.cardId == currAgent.smartcard){
					return sm;
				}
			}
		}
		return null;
	}

	public void printStation(String string) throws IOException {
		// TODO Auto-generated method stub
		OutputFileWritter writer = new OutputFileWritter();
		writer.OpenFile(string);
		writer.WriteToFile("stationId, smartcard count, pop count");
		
		for(String key : myStops.keySet()){
			ArrayList<Smartcard> currLocalSmartcards = new ArrayList<Smartcard>();
			ArrayList<BiogemeAgent> currLocalPopulation = new ArrayList<BiogemeAgent>();
			GTFSStop currStation = myStops.get(key);
			currLocalSmartcards.addAll(currStation.getSmartcards());
			currLocalPopulation.addAll(currStation.getLocalPopulation());
			writer.WriteToFile(currStation.myId + Utils.COLUMN_DELIMETER + 
					currLocalSmartcards.size() + Utils.COLUMN_DELIMETER +
					currLocalPopulation.size());
		}
		writer.CloseFile();
	}

	public void getValues() {
		// TODO Auto-generated method stub
		for(BiogemeHypothesis currH: BiogemeSimulator.modelHypothesis){
			System.out.println(currH.coefName + " " + currH.coefValue);
		}
	}

	
	
}
