package ActivityChoiceModel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.LineNumberReader;
import java.util.logging.Level;
import java.util.logging.Logger;


import SimulationObjects.Person;
import Utils.*;

public class TravelSurveyPreparator {
	BiogemeControlFileGenerator biogemeGenerator = new BiogemeControlFileGenerator();
	
	InputDataReader myInputDataReader = new InputDataReader();
	OutputFileWritter myOutputFileWritter = new OutputFileWritter();
	//public HashMap<String, Object> myTravelSurvey;
	HashMap<String, ArrayList<Object>> myData = new HashMap<String, ArrayList<Object>>();
	protected RandomNumberGen randGen = new RandomNumberGen();
	
	public TravelSurveyPreparator(){
		
	}
	
	public void initialize(String path){
		myInputDataReader.OpenFile(path);
		String[] temp;
		temp=path.split(".csv");
		myOutputFileWritter.OpenFile(temp[0] + "_prepared.csv");
	}
	
	public void initialize(String path, String outputPath){
		myInputDataReader.OpenFile(path);
		myOutputFileWritter.OpenFile(outputPath);
	}
	
	public TravelSurveyPreparator(String path){
		initialize(path);	
	}
	
	public void processData(int choiceSetSize) throws IOException{
		storeData();
		processSocioDemographics();
		processNumberOfActivities();
		System.out.println("--mobility processed");
		processTourTypes();
		System.out.println("--tour types processed");
		processModalClass();
		System.out.println("--modal class and fidelity to public transit processed");
		processLastDepartureHour();
		System.out.println("--last departure hours processed ");
		processAverageTourLength();
		System.out.println("--average tour length processed");
		processNumberOfKids();
		System.out.println("--number of kids in household processed");
		processActivityDuration();
		System.out.println("--max activity duration computed");
		processMinMaxTourLength();
		System.out.println("-- min and max distance for a trip processed");
		processAlternatives(choiceSetSize);// maybe some work should be done to avoid to get alternatives irrelevant (like not taking PT, or for a different kind of occupation)
		System.out.println("--alternative processed");
		toBoolean(UtilsTS.pStart, UtilsTS.trueValue);
		//addOnes("choice");
		ArrayList temp = myData.remove(UtilsTS.groupHour);
		myData.put(UtilsTS.firstDep, temp);
		selectAndPrint(choiceSetSize);
		myData.put("N_ACTIVITIES", new ArrayList());
		myData.put("N_FREQUENT_ACTIVITIES", new ArrayList());
		myData.put("MODAL_CLASS", new ArrayList());
	}
	
	private void processSocioDemographics() {
		// TODO Auto-generated method stub
		for(int i = 0; i < myData.size(); i++){
			
		}
	}

	public void processActivityDuration(){
		myData.put("MAX_ACT_TIME", new ArrayList<Object>());
		for(int i = 0; i < myData.get(UtilsTS.id).size();i++){
			if(myData.get(UtilsTS.pStart).get(i).equals(UtilsTS.trueValue)){
				double maxDuration = 0;
				String hhId = (String)myData.get(UtilsTS.hhId).get(i);
				String persId = (String)myData.get(UtilsTS.persId).get(i);
				for(int j = i; j < myData.get(UtilsTS.id).size()-1; j++){
					
					if(hhId.equals((String)myData.get(UtilsTS.hhId).get(j)) && persId.equals((String)myData.get(UtilsTS.persId).get(j))){
						double startTime = hourStrToDouble((String)myData.get(UtilsTS.groupHour).get(j));
						double endTime = hourStrToDouble((String)myData.get(UtilsTS.groupHour).get(j+1));
						double duration = endTime - startTime;
						if(duration>maxDuration){
							maxDuration = duration;
						}
					}
					else{
						j+=400000;
					}
				}
				myData.get("MAX_ACT_TIME").add(Double.toString(maxDuration));
			}
			else{
				myData.get("MAX_ACT_TIME").add(0);
			}
		}
	}
	
	private double hourStrToDouble(String time) {
		// TODO Auto-generated method stub
		//in OD survey of Montréal, TIME is formated 2800
		time.trim();
		double hour = 0;
		if(time.length()==4){
			hour = 60 * Double.parseDouble(time.substring(0, 1)) + Double.parseDouble(time.substring(2,3));
		}
		else if(time.length() == 3){
			hour = 60 * Double.parseDouble(Character.toString(time.charAt(0))) + Double.parseDouble(time.substring(1,2));
		}
		return hour; //here, hour is in minutes
	}
	
	public void processMotorRate(){
		myData.put(UtilsTS.motor, new ArrayList<Object>());
		for(int i = 0; i < myData.get(UtilsTS.id).size();i++){
			double motor = Double.parseDouble((String)myData.get(UtilsTS.cars).get(i)) / 
					(Double.parseDouble((String)(myData.get(UtilsTS.pers).get(i))) -
					(int)myData.get(UtilsTS.kids).get(i));
			myData.get(UtilsTS.motor).add(motor);
		}
	}

	public void processMinMaxTourLength(){
		myData.put(UtilsTS.minDist, new ArrayList<Object>());
		myData.put(UtilsTS.maxDist, new ArrayList<Object>());
		for(int i = 0; i < myData.get(UtilsTS.id).size();i++){
			if(myData.get(UtilsTS.pStart).get(i).equals(UtilsTS.trueValue)){
				double minDist = 1000;
				double maxDist = 0;
				String hhId = (String)myData.get(UtilsTS.hhId).get(i);
				String persId = (String)myData.get(UtilsTS.persId).get(i);
				for(int j = i; j < myData.get(UtilsTS.id).size(); j++){
					if(hhId.equals((String)myData.get(UtilsTS.hhId).get(j)) && persId.equals((String)myData.get(UtilsTS.persId).get(j))){
						if(
								!myData.get(UtilsTS.xOrigin).get(j).equals("")&&
								!myData.get(UtilsTS.xDest).get(j).equals("")&&
								!myData.get(UtilsTS.yOrigin).get(j).equals("")&&
								!myData.get(UtilsTS.yDest).get(j).equals("")){
							double currDist =  Math.sqrt(
									Math.pow(Double.parseDouble((String)myData.get(UtilsTS.xOrigin).get(j)) -
											Double.parseDouble((String)myData.get(UtilsTS.xDest).get(j)), 2) + 
									Math.pow(Double.parseDouble((String)myData.get(UtilsTS.yOrigin).get(j)) - 
											Double.parseDouble((String)myData.get(UtilsTS.yDest).get(j)), 2));
							if(currDist<minDist){
								minDist = currDist;
							}
							if(currDist>maxDist){
								maxDist = currDist;
							}
						}
						else{
							
						}
					}
					else{
						j+=400000;
					}
				}
				minDist = minDist / 1000;
				maxDist = maxDist / 1000;
				myData.get(UtilsTS.minDist).add(minDist);
				myData.get(UtilsTS.maxDist).add(maxDist);
			}
			else{
				myData.get(UtilsTS.minDist).add(0);
				myData.get(UtilsTS.maxDist).add(0);
			}
		}
	}
	
	//this function should be ran after the
	public void processNumberOfKids(){
		//here, kids are 5 to 15,
		myData.put("KIDS", new ArrayList<Object>());
		for(int i = 0; i < myData.get(UtilsTS.id).size();i++){
			
			if(myData.get(UtilsTS.pStart).get(i).equals(UtilsTS.trueValue)){
				int nKids = 0;
				String hhId = (String)myData.get(UtilsTS.hhId).get(i);
				for(int j = Math.max(0,i-150); j < Math.min(myData.get(UtilsTS.id).size(),i + 150); j++){
					if(myData.get(UtilsTS.pStart).get(j).equals(UtilsTS.trueValue)){
						if(myData.get(UtilsTS.hhId).get(j).equals(hhId)){
							if(Integer.parseInt((String)myData.get(UtilsTS.age).get(j)) < 15 ){
								nKids++;
							}
						}
					}
				}
				myData.get("KIDS").add(nKids);
			}
			else{
				myData.get("KIDS").add(0);
			}
			if(i%1000==0){
				System.out.println(i);
			}
		}
	}
	
	public void processAlternatives(int n){
		for(int i = 0; i < n; i++){
			myData.put(UtilsTS.chainLength+Integer.toString(i), new ArrayList<Object>());
			myData.put(UtilsTS.firstDep+Integer.toString(i), new ArrayList<Object>());
			myData.put(UtilsTS.lastDep+Integer.toString(i), new ArrayList<Object>());
			myData.put(UtilsTS.minDist+Integer.toString(i), new ArrayList<Object>());
			myData.put(UtilsTS.maxDist+Integer.toString(i), new ArrayList<Object>());
			myData.put("MAX_ACT_TIME"+Integer.toString(i), new ArrayList<Object>());
			myData.put(UtilsTS.fidelPtRange+Integer.toString(i), new ArrayList<Object>());
			myData.put(UtilsTS.nAct+Integer.toString(i), new ArrayList<Object>());
			myData.put(UtilsTS.tourType+Integer.toString(i), new ArrayList<Object>());
			myData.put("P_STATUT"+Integer.toString(i), new ArrayList<Object>());
		}
		
		for(int i = 0; i < myData.get(UtilsTS.id).size();i++){
			if(myData.get(UtilsTS.pStart).get(i).equals(UtilsTS.trueValue) ){
				String hhId = (String)myData.get(UtilsTS.hhId).get(i);
				String persId = (String)myData.get(UtilsTS.persId).get(i);
				//String pStatut = (String)myData.get("P_STATUT").get(i);
				Set<Integer> idClosests = findClosestAlternatives(
						n, 
						Integer.parseInt((String)myData.get("M_DOMXCOOR").get(i)),
						Integer.parseInt((String)myData.get("M_DOMYCOOR").get(i)), 
						hhId,
						persId);
				
				addAlternatives(n, idClosests);	
			}
			else{
				addAlternatives(n);
			}
			if(i%1000==0){
				System.out.println(i);
			}
		}
	}

	private void addAlternatives(int n) {
		// TODO Auto-generated method stub
		for(int i = 0; i < n; i++){
			myData.get(UtilsTS.chainLength+Integer.toString(i)).add("0");
			myData.get(UtilsTS.firstDep+Integer.toString(i)).add("0");
			myData.get(UtilsTS.lastDep+Integer.toString(i)).add("0");
			myData.get("MAX_ACT_TIME"+Integer.toString(i)).add("0");
			myData.get(UtilsTS.minDist+Integer.toString(i)).add("0");
			myData.get(UtilsTS.maxDist+Integer.toString(i)).add("0");
			myData.get(UtilsTS.fidelPtRange+Integer.toString(i)).add("0");
			myData.get(UtilsTS.nAct+Integer.toString(i)).add("0");
			myData.get(UtilsTS.tourType+Integer.toString(i)).add(0);
			myData.get("P_STATUT"+Integer.toString(i)).add("0");
		}
	}

	private void addAlternatives(int n, Set<Integer> idClosests) {
		// TODO Auto-generated method stub
		Iterator<Integer> it = idClosests.iterator();
		for(int i = 0; i < n; i++){
			int currIpere = it.next();
			int j = 0;
			while(currIpere != Integer.parseInt((String)myData.get(UtilsTS.id).get(j))){//IPERE is a unique identifier for each record in the travel survey of Montreal
				j++;
			}
			String altDist = (String)myData.get(UtilsTS.chainLength).get(j);
			String altFirstDep = (String)myData.get(UtilsTS.groupHour).get(j);
			String altLastDep = (String)myData.get(UtilsTS.lastDep).get(j);
			String maxActTime = (String)myData.get("MAX_ACT_TIME").get(j);
			String minDist = (String)myData.get(UtilsTS.minDist).get(j);
			String maxDist = (String)myData.get(UtilsTS.maxDist).get(j);
			String fidelPT = (String)myData.get(UtilsTS.fidelPtRange).get(j);
			Integer nActivities = (Integer)myData.get(UtilsTS.nAct).get(j);
			Integer tourType = (Integer)myData.get(UtilsTS.tourType).get(j);
			String pStatut = (String)myData.get("P_STATUT").get(j);
			
			
			myData.get(UtilsTS.firstDep+Integer.toString(i)).add(altFirstDep);
			myData.get(UtilsTS.lastDep+Integer.toString(i)).add(altLastDep);
			myData.get(UtilsTS.chainLength+Integer.toString(i)).add(altDist);
			myData.get("MAX_ACT_TIME"+Integer.toString(i)).add(maxActTime);
			myData.get(UtilsTS.minDist+Integer.toString(i)).add(minDist);
			myData.get(UtilsTS.maxDist+Integer.toString(i)).add(maxDist);
			myData.get(UtilsTS.fidelPtRange+Integer.toString(i)).add(fidelPT);
			myData.get(UtilsTS.nAct+Integer.toString(i)).add(nActivities);
			myData.get(UtilsTS.tourType+Integer.toString(i)).add(tourType);
			myData.get("P_STATUT"+Integer.toString(i)).add(pStatut);
		}
	}

	private Set<Integer> findClosestAlternatives(int n, int x, int y, String hhId, String persId) {
		// TODO Auto-generated method stub
		HashMap<Integer,Integer> closeAlternatives = new HashMap<Integer, Integer>(); // structure : the key is formed by IPERE (from OD survey), the value is the distance
		
		for(int j = 0; j < myData.get(UtilsTS.id).size(); j++){
			if(
					myData.get(UtilsTS.pStart).get(j).equals(UtilsTS.trueValue) && 
					!(((String)myData.get(UtilsTS.hhId).get(j)).equals(hhId) && 
					((String)myData.get(UtilsTS.persId).get(j)).equals(persId))
			){
				int curX = Integer.parseInt((String)myData.get("M_DOMXCOOR").get(j));
				int curY = Integer.parseInt((String)myData.get("M_DOMYCOOR").get(j));
				int curIpere = Integer.parseInt((String)myData.get(UtilsTS.id).get(j));
				int curDist = (int)(Math.pow((x-curX), 2) + Math.pow((y-curY), 2)); 
				
				if(closeAlternatives.size() < n){
					closeAlternatives.put(curIpere, curDist);
				}
				else{
					int[] removeId = getFarthestAltenative(closeAlternatives);
					if(curDist< removeId[1]){
						closeAlternatives.remove(removeId[0]);
						closeAlternatives.put(curIpere, curDist);
					}
				}				
			}
			else{	
			}	
		}
		return closeAlternatives.keySet();
	}
	
	private int[] getFarthestAltenative(HashMap<Integer, Integer> closeAlternatives) {
		// TODO Auto-generated method stub
		int curDist = 0;
		int curId = 0;
		for(java.util.Map.Entry<Integer, Integer> e:closeAlternatives.entrySet()){
			if(e.getValue() > curDist){
				curId = e.getKey();
				curDist = e.getValue();
			}
		}
		int answer[] = new int[2];
		answer[0] = curId;
		answer[1] = curDist;
		return answer;
	}

	public void processAverageTourLength() {
		// TODO Auto-generated method stub
		myData.put(UtilsTS.chainLength, new ArrayList<Object>());
		for(int i = 0; i < myData.get(UtilsTS.id).size();i++){
			if(myData.get(UtilsTS.pStart).get(i).equals(UtilsTS.trueValue)){
				double distance = 0;
				String hhId = (String)myData.get(UtilsTS.hhId).get(i);
				String persId = (String)myData.get(UtilsTS.persId).get(i);
				for(int j = i; j < myData.get(UtilsTS.id).size(); j++){
					if(hhId.equals((String)myData.get(UtilsTS.hhId).get(j)) && persId.equals((String)myData.get(UtilsTS.persId).get(j))){
						
						if(
								!myData.get(UtilsTS.xOrigin).get(j).equals("")&&
								!myData.get(UtilsTS.xDest).get(j).equals("")&&
								!myData.get(UtilsTS.yOrigin).get(j).equals("")&&
								!myData.get(UtilsTS.yDest).get(j).equals("")){
							distance+=  Math.sqrt(
									Math.pow(Double.parseDouble((String)myData.get(UtilsTS.xOrigin).get(j)) -
											Double.parseDouble((String)myData.get(UtilsTS.xDest).get(j)), 2) + 
									Math.pow(Double.parseDouble((String)myData.get(UtilsTS.yOrigin).get(j)) - 
											Double.parseDouble((String)myData.get(UtilsTS.yDest).get(j)), 2));
						}
						else{
							distance+=0;
						}
						
					}
					else{
						j+=400000;
					}
				}
				distance = distance / 1000;
				myData.get(UtilsTS.chainLength).add(distance);
			}
			else{
				myData.get(UtilsTS.chainLength).add((double)0);
			}
		}
	}

	public void processLastDepartureHour() {
		// TODO Auto-generated method stub
		myData.put(UtilsTS.lastDep, new ArrayList<Object>());

		for(int i = 0; i < myData.get(UtilsTS.id).size();i++){
			if(myData.get(UtilsTS.pStart).get(i).equals(UtilsTS.trueValue)){
				ArrayList<String> temp = new ArrayList<String>();
				String hhId = (String)myData.get(UtilsTS.hhId).get(i);
				String persId = (String)myData.get(UtilsTS.persId).get(i);
				for(int j = i; j < myData.get(UtilsTS.id).size(); j++){
					if(hhId.equals((String)myData.get(UtilsTS.hhId).get(j)) && persId.equals((String)myData.get(UtilsTS.persId).get(j))){
						temp.add((String)myData.get(UtilsTS.hour).get(j));
					}
					else{
						j+=400000;
					}
				}
				myData.get(UtilsTS.lastDep).add(temp.get(temp.size()-1));
			}
			else{
				myData.get(UtilsTS.lastDep).add("10");
			}
		}	
	}

	public void processModalClass() {
		// TODO Auto-generated method stub
		//myData.put("MODAL_CLASS", new ArrayList());
		myData.put(UtilsTS.fidelPt, new ArrayList());
		myData.put(UtilsTS.fidelPtRange, new ArrayList());
		for(int i = 0; i < myData.get(UtilsTS.id).size();i++){
			if(myData.get(UtilsTS.pStart).get(i).equals(UtilsTS.trueValue)){
				HashMap<String, Integer> counters = new HashMap<String, Integer>();
				counters.put("nPT", 0);
				counters.put("nPrivate", 0);
				counters.put("nActive", 0);
				String hhId = (String)myData.get(UtilsTS.hhId).get(i);
				String persId = (String)myData.get(UtilsTS.persId).get(i);
				for(int j = i; j < myData.get(UtilsTS.id).size(); j++){
					if(hhId.equals((String)myData.get(UtilsTS.hhId).get(j)) && persId.equals((String)myData.get(UtilsTS.persId).get(j))){
						setModalClass(counters,j);
					}
					else{
						j+=400000;
					}
				}
				
				double ratio;
				if(((double)(counters.get("nPT")+counters.get("nPrivate")+counters.get("nActive")))!=0){
					ratio = (double)(counters.get("nPT"))/ ((double)(counters.get("nPT")+counters.get("nPrivate")+counters.get("nActive")));

				}
				else{
					ratio = 0;
				}
				
				if(Double.isNaN(ratio)){
					myData.get(UtilsTS.fidelPt).add("0");
				}
				else{
					myData.get(UtilsTS.fidelPt).add(Double.toString(ratio));
				}
				
				if(Double.isNaN(ratio)){
					myData.get(UtilsTS.fidelPtRange).add("0");
				}
				else if(ratio<=0.05){
					myData.get(UtilsTS.fidelPtRange).add("0");
				}
				else if(ratio<=0.95){
					myData.get(UtilsTS.fidelPtRange).add("1");
				}
				else if(ratio>=0.95){
					myData.get(UtilsTS.fidelPtRange).add("2");
				}
				else{
					myData.get(UtilsTS.fidelPtRange).add("10");//this is the people who did not move
				}
			}
			else{
				myData.get(UtilsTS.fidelPt).add("0");
				myData.get(UtilsTS.fidelPtRange).add("10");
			}
		}
	}
			
	private void setModalClass(HashMap<String, Integer> counters, int i) {
		// TODO Auto-generated method stub
		HashMap<String, Object> modalClass = new HashMap<String, Object>();
		modalClass.put("isPT", false);
		modalClass.put("isPrivate", false);
		modalClass.put("isActive", false);

		processMode(modalClass, counters,"MODE1",i);
		processMode(modalClass, counters,"MODE2",i);
		processMode(modalClass, counters,"MODE3",i);
		processMode(modalClass, counters,"MODE4",i);
		processMode(modalClass, counters,"MODE5",i);
	}

	private void processMode(HashMap<String, Object> modalClass, HashMap<String, Integer> counters, String mode, int i) {
		// TODO Auto-generated method stub
		
		if(myData.get(mode).get(i).equals("1") || 
				myData.get(mode).get(i).equals("2") || 
				myData.get(mode).get(i).equals("9") || //taxi
				myData.get(mode).get(i).equals("10") ||// transport for disabbled
				myData.get(mode).get(i).equals("15") ||
				myData.get(mode).get(i).equals("16") ||
				myData.get(mode).get(i).equals("17")
				){
			modalClass.put("isPrivate", true);
			int nTemp = counters.get("nPrivate") + 1;
			counters.put("nPrivate", nTemp);
			}
		else if(
				myData.get(mode).get(i).equals("4") 
				){
			modalClass.put("isPT", true);
			int nTemp = counters.get("nPT") + 1;
			counters.put("nPT", nTemp);
			}
		else if (myData.get(mode).get(i).equals("11") || 
				myData.get(mode).get(i).equals("12")	||
				myData.get(mode).get(i).equals("5") ||
				myData.get(mode).get(i).equals("6") ||
				myData.get(mode).get(i).equals("7") || 
				myData.get(mode).get(i).equals("3") || 
				myData.get(mode).get(i).equals("8")
				){
			modalClass.put("isActive", true);
			int nTemp = counters.get("nActive") + 1;
			counters.put("nActive", nTemp);
		}
	}

	public void processNumberOfActivities(){
		myData.put(UtilsTS.nAct, new ArrayList());
		for(int i = 0; i < myData.get(UtilsTS.id).size();i++){
			if(myData.get(UtilsTS.pStart).get(i).equals(UtilsTS.trueValue)){
				int nActivities =0; 
				
				String hhId = (String)myData.get(UtilsTS.hhId).get(i);
				String persId = (String)myData.get(UtilsTS.persId).get(i);
				for(int j = i; j < myData.get(UtilsTS.id).size()-1; j++){
					if(((String)myData.get(UtilsTS.pStart).get(j+1)).equals(UtilsTS.trueValue)){
					}
					else if(hhId.equals((String)myData.get(UtilsTS.hhId).get(j)) && persId.equals((String)myData.get(UtilsTS.persId).get(j))){
						if(myData.get(UtilsTS.backHomeTrip).get(j).equals(UtilsTS.trueValue)){
						}
						else{
							nActivities = nActivities + 1;
						}
					}
					else{
						j+= 400000;
					}
				}
				if(nActivities > 3){
					nActivities = 3;
				}
				myData.get(UtilsTS.nAct).add(nActivities);
			}
			else{
				myData.get(UtilsTS.nAct).add(0);
			}
		}
	}
	
	public void processTourTypes(){
		myData.put(UtilsTS.tourType, new ArrayList());
		for(int i = 0; i < myData.get(UtilsTS.id).size();i++){
			if(myData.get(UtilsTS.pStart).get(i).equals(UtilsTS.trueValue)){
				String hhId = (String)myData.get(UtilsTS.hhId).get(i);
				String persId = (String)myData.get(UtilsTS.persId).get(i);
				int nH = 1;
				int nC = 0;
				int nU = 0;
				int nW = 0;
				int nS = 0;
				int tourType;
				
				if((int)myData.get(UtilsTS.nAct).get(i) == 0){	
				}
				else{
					for(int j = i; j < myData.get(UtilsTS.id).size(); j++){
						
						if(hhId.equals((String)myData.get(UtilsTS.hhId).get(j)) && persId.equals((String)myData.get(UtilsTS.persId).get(j))){
							if(getActivityType((String)myData.get(UtilsTS.backHomeTrip).get(j)).equals("H")){
								nH++;
							}
							else if(getActivityType((String)myData.get(UtilsTS.backHomeTrip).get(j)).equals("C")){
								nC++;
							}
							else if(getActivityType((String)myData.get(UtilsTS.backHomeTrip).get(j)).equals("U")){
								nU++;
							}
							else if(getActivityType((String)myData.get(UtilsTS.backHomeTrip).get(j)).equals("W")){
								nW++;
							}
							else if(getActivityType((String)myData.get(UtilsTS.backHomeTrip).get(j)).equals("S")){
								nS++;
							}
							//tourType = tourType + getActivityType((String)myData.get(UtilsTS.dMotif).get(j));
						}
						else{
							j+= 400000;
						}
					}
				}
				tourType = getTourType(nH, nW, nS, nC,  nU);
				myData.get(UtilsTS.tourType).add(tourType);
			}
			else{
				myData.get(UtilsTS.tourType).add(0); // index for "NULL"
			}
		}
	}
	
	
	private int getTourType(int nH, int nW, int nS, int nC, int nU) {
		// TODO Auto-generated method stub
		if(nH == 1 && nC == 0 && nU == 0 && nW == 0 && nS == 0)
			return 1; // index for : "H"
		else if(nW>=1)
			return 2; //index for W
		else if(nW==0 && nS >= 1)
			return 3;// index for "HSH";
		else if(nW==0 && nS == 0 && nC >=1)
			return 4;//index for "HCH"		
		else if(nW == 0 && nS==0 && nC==0 && nU >=1)
			return 5;//index for "HUH"
		else
			return 3; // index for "other";
	}

	public void toBoolean(String columnName, String trueValue){
		for(int i = 0; i < myData.get(UtilsTS.id).size();i++){
			if(myData.get(columnName).get(i).equals(trueValue)){
				myData.get(columnName).set(i, "1");
			}
			else{
				myData.get(columnName).set(i, "0");
			}
		}
	}
	
	public String getActivityType(String act){
		
		if(act.equals("1") || act.equals("2")|| act.equals("3")){
			return "W";// work activity
		}
		else if(act.equals("4")){
			return "S"; // study activity
		}
		else if(act.equals("12")||act.equals("13")){
			return "C";// other possibly constraint activity
		}
		else if(act.equals("14")){
			return "H";
		}
		else{
			return "U"; // other activity are assumed to be unconstrained
		}
	}
	
	public void storeData() throws IOException
    {
    	 ArrayList<ArrayList> data = new ArrayList<ArrayList>();
    	 data = getData();
    	 
    	 ArrayList<String> headers = data.get(0);
    	 for(int i =0; i < headers.size(); i++){
    		 myData.put(headers.get(i), new ArrayList());
    	 }

    	 for (int i=1; i<data.size(); i++)
    	 {
    			for (int j=0; j<data.get(i).size();j++)
    			{
    				try{
    					myData.get(headers.get(j)).add(data.get(i).get(j));
    				}catch(IndexOutOfBoundsException e){
    					System.out.println("column : " + j + " " + headers.get(j));
        				System.out.println("column : " + j + " " + data.get(i).get(j));
    				}
    				
    				
    			}
    	 }
    }
	
	public ArrayList<ArrayList> getData() throws IOException
    {
    	String line=null;
    	Scanner scanner = null;
    	ArrayList<ArrayList> data = new ArrayList<ArrayList>();

    		int i=0;
    		while((line=myInputDataReader.myFileReader.readLine())!= null)
    		{
    			data.add(new ArrayList());
    			scanner = new Scanner(line);
    			scanner.useDelimiter(Utils.COLUMN_DELIMETER);

    				while (scanner.hasNext())
    				{
    					String dat = scanner.next();
    					data.get(i).add(dat);
    				}
    				i++;
    		}
    	return data;
    }
	
	public void printData() throws IOException {

		String headers= new String();
		for (String key : myData.keySet()) {
			headers+= key + Utils.COLUMN_DELIMETER;
		}
		headers = headers.substring(0, headers.length()-1);
		myOutputFileWritter.WriteToFile(headers);
		
		for(int i=0; i < myData.get(UtilsTS.id).size(); i++){
			String line = new String();
			for(String key: myData.keySet()){
				line += myData.get(key).get(i) + Utils.COLUMN_DELIMETER;
			}
			line = line.substring(0, line.length()-1);
			myOutputFileWritter.WriteToFile(line);
		}
		myOutputFileWritter.CloseFile();
	}
	
	public void printColumns(ArrayList<String> toPrint) throws IOException {

		String headers= new String();
		Iterator<String> it = toPrint.iterator();
		while(it.hasNext()){
			String head = it.next();
			headers+= head + Utils.COLUMN_DELIMETER;
		}
		headers = headers.substring(0, headers.length()-1);
		myOutputFileWritter.WriteToFile(headers);
		
		for(int i=0; i < myData.get(UtilsTS.id).size(); i++){
			if(myData.get(UtilsTS.pStart).get(i).equals("1") &&
					!myData.get(UtilsTS.alternative).get(i).equals("-1")){
				String line = new String();
				Iterator<String> it2 = toPrint.iterator();
				while(it2.hasNext()){
					String head = it2.next();
					try{
						line += myData.get(head).get(i) + Utils.COLUMN_DELIMETER;
					}
					catch(NullPointerException e){
						line += Utils.COLUMN_DELIMETER;
					}
				}
				line = line.substring(0, line.length()-1);
				myOutputFileWritter.WriteToFile(line);
			}
			else{
			}
		}
		myOutputFileWritter.CloseFile();
	}
	
	public void selectAndPrint(int choiceSetSize){
		ArrayList<String> headers = new ArrayList<String>();
		headers.add(UtilsTS.id);
		headers.add("X");
		headers.add("Y");
		headers.add(UtilsTS.hhId);
		headers.add(UtilsTS.pStart);
		headers.add(UtilsTS.persId);
		
		headers.add(UtilsTS.cars);
		headers.add(UtilsTS.pers);
		headers.add(UtilsTS.kids);
		
		headers.add(UtilsTS.sex);
		headers.add(UtilsTS.ageGroup);
		headers.add(UtilsTS.weigth);
		
		headers.add(UtilsTS.pStatut);
		headers.add(UtilsTS.firstDep + "Short");//FIRST departure
		headers.add(UtilsTS.lastDep + "Short");
		headers.add(UtilsTS.fidelPtRange);
		headers.add(UtilsTS.nAct);
		headers.add(UtilsTS.nest);
		headers.add(UtilsTS.startWithSto);
		headers.add(UtilsTS.accessToPt);
		headers.add(UtilsTS.end);
		
		headers.add(UtilsTS.alternative);
		headers.add(UtilsTS.choice);
		headers.add(UtilsTS.stoUser);
		for(int i = 0; i < choiceSetSize; i++){
			headers.add(UtilsTS.alternative+Integer.toString(i));
		}

		try {
			printColumns(headers);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public ArrayList<Object> createSubSamples(int numberOfCores){

	    //creates sub group of zone that can be processed using multithreading to accelerate computation
	    int subSampleSize = myData.get(UtilsTS.id).size()/(numberOfCores);
	    ArrayList<Object> subSamples = new ArrayList<Object>();
	    int idxCore = 0;
	    
	    for (int j = 0; j<numberOfCores; j++){
	    	HashMap<String, Object> currSubSample = new HashMap<String, Object>();
	    	for (String key: myData.keySet()){
	    		currSubSample.put(key, new ArrayList<Object>());
	    	}
	    	subSamples.add(currSubSample);
	    }
	    
	    System.out.println(myData.get(UtilsTS.id).size());
	    for(int k = 0; k < myData.get(UtilsTS.id).size(); k++){
	    	if((k>=idxCore*subSampleSize && k < (idxCore+1)*subSampleSize)|| (k>=idxCore*subSampleSize && idxCore == numberOfCores-1)){// || (k>=i*subSampleSize && i == numberOfCores-1)
	    		for (String key: myData.keySet()){
	    			//System.out.println(key + "  "+ idxCore + "  " + k);
	    			try{

		    			((HashMap<String, ArrayList<Object>>) subSamples.get(idxCore)).get(key).add(myData.get(key).get(k));
	    			}
	    			catch(IndexOutOfBoundsException e){
	    				((HashMap<String, ArrayList<Object>>) subSamples.get(idxCore)).get(key).add("1000");
	    				System.out.println(key + "  "+ idxCore + "  " + k);
	    				System.out.println(e);
	    			}
		    	}
	    	}
	    	else{
	    		idxCore++;
	    	}
	    }
	    for (int j = 0; j<numberOfCores; j++){
	    	int size = ((HashMap<String, ArrayList<Object>>) subSamples.get(j)).get(UtilsTS.id).size();
	    	System.out.println("sample " + j + ": " + size);	
	    }
	    return subSamples;
	}
	
	public void processDataMultiThreads(int numberOfCores, int nAlternatives, BiogemeControlFileGenerator ctrlGen/*String pathControleFile, String pathOutput, String pathHypothesis*/) throws IOException
    {
		String workingDir = System.getProperty("user.dir");
		biogemeGenerator = ctrlGen;

		storeData();
		System.out.println("--data stored");		
		//All prepraration methods should be designed for your specific data set.

		processNumberOfActivities();
		System.out.println("--mobility processed");
		processModalClass();
		System.out.println("--modal class and fidelity to public transit processed");
		processLastDepartureHour();
		System.out.println("--last departure hours processed ");

		processSTOuser();
		processNest();
		processStartWithSTO();
		processLastWithSTO();
		
		//The travel survey data and the other data sets should be prepared such as attributes categories matches with each other.
		processSocioDemographic();
		System.out.println("--socio demographic category were processed");
		
		ArrayList temp = myData.remove(UtilsTS.groupHour);
		myData.put(UtilsTS.firstDep, temp);
		
		departureHour3categories();
		lastDepartureHour3categories();
		System.out.println("--categories for departure hours were processed");
		
		processChoiceIndex();
		System.out.println("--choice index processed");

	    ArrayList<Object> subSamples = createSubSamples(numberOfCores);
	    System.out.println("--data batched and ready for multithreading");
	    //create and run multiple threads
	    ExecutorService cores = Executors.newFixedThreadPool(numberOfCores);
    	Set<Future<HashMap<String,ArrayList<Object>>>> set = new HashSet<Future<HashMap<String,ArrayList<Object>>>>();
    	
    	
    	for(int i = 0; i< numberOfCores; i++){
    		Callable<HashMap<String,ArrayList<Object>>> callable = new processChoiceSet(subSamples.get(i), nAlternatives);
        	Future<HashMap<String,ArrayList<Object>>> future = cores.submit(callable);
            set.add(future);
    	}
    	
    	myData.clear();
    	boolean firstAnswer = true;
    	try {
            for (Future<HashMap<String,ArrayList<Object>>> future : set) {
            	HashMap<String,ArrayList<Object>> answer = future.get();
            	if(firstAnswer){
            		myData = answer;
            		firstAnswer = false;
            	}
            	else{
            		for (String key: myData.keySet()){
        	    		myData.get(key).addAll(answer.get(key));
        	    	}
            	}
            }
    	} 
    	catch (InterruptedException | ExecutionException ex) {
    		ex.printStackTrace(); 
    	}
    	
    	System.out.println("--alternatives processed");
		//toBoolean(UtilsTS.pStart, UtilsTS.trueValue);
		selectAndPrint(nAlternatives);
		cores.shutdown();
    }
	
	private void processLastWithSTO() {
		// TODO Auto-generated method stub
		ArrayList<Object> ends = new ArrayList<Object>();
		for(int i = 0; i < myData.get(UtilsTS.id).size();i++){
			String end = new String();
			if(myData.get(UtilsTS.pStart).get(i).equals(UtilsTS.trueValue)){
				String hhId = (String)myData.get(UtilsTS.hhId).get(i);
				String persId = (String)myData.get(UtilsTS.persId).get(i);
				for(int j = i; j < myData.get(UtilsTS.id).size(); j++){
					if(hhId.equals((String)myData.get(UtilsTS.hhId).get(j)) && persId.equals((String)myData.get(UtilsTS.persId).get(j))){
						String mode1 = (String)myData.get(UtilsTS.mode1).get(j);
						String mode2 = (String)myData.get(UtilsTS.mode2).get(j);
						String mode3 = (String)myData.get(UtilsTS.mode3).get(j);
						String mode4 = (String)myData.get(UtilsTS.mode4).get(j);
						String mode5 = (String)myData.get(UtilsTS.mode5).get(j);
						if(!mode5.equals("")){end = mode5;}
						else if(!mode4.equals("")){end = mode4;}
						else if(!mode3.equals("")){end = mode3;}
						else if(!mode2.equals("")){end = mode2;}
						else if(!mode1.equals("")){end = mode1;}
						
					}
					else{
						j+=400000;
					}
				}
			}
			else{
				end = "nothing";
			}
			ends.add(end);
		}
		myData.put(UtilsTS.end, ends);
	}

	private void processStartWithSTO() {
		// TODO Auto-generated method stub
		ArrayList<Object> startWithSto = new ArrayList<Object>();
		for(int i = 0; i < myData.get(UtilsTS.id).size(); i++){
			String start;
			if(myData.get(UtilsTS.pStart).get(i).equals(UtilsTS.trueValue)){
				start = (String) myData.get(UtilsTS.mode1).get(i);
				if(start.equals("4")){
					start = "1";
				}
				else{
					start = "0";
				}
			}
			else{
				start = "0";
			}
			startWithSto.add(start);
		}
		myData.put(UtilsTS.startWithSto, startWithSto);
	}

	private void processSTOuser() {
		// TODO Auto-generated method stub
		ArrayList<Object> stoUser = new ArrayList<Object>();
		for(int i = 0; i < myData.get(UtilsTS.id).size(); i++){
			boolean isSTOuser = false;
			if(myData.get(UtilsTS.pStart).get(i).equals(UtilsTS.trueValue)){
				String hhId = (String)myData.get(UtilsTS.hhId).get(i);
				String persId = (String)myData.get(UtilsTS.persId).get(i);
				for(int j = i; j < myData.get(UtilsTS.id).size(); j++){
					if(hhId.equals((String)myData.get(UtilsTS.hhId).get(j)) && persId.equals((String)myData.get(UtilsTS.persId).get(j))){
						String mode1 = ((String) myData.get(UtilsTS.mode1).get(j)).trim();
						String mode2 = ((String) myData.get(UtilsTS.mode2).get(j)).trim();
						String mode3 = ((String) myData.get(UtilsTS.mode3).get(j)).trim();
						String mode4 = ((String) myData.get(UtilsTS.mode4).get(j)).trim();
						String mode5 = ((String) myData.get(UtilsTS.mode5).get(j)).trim();
						if(mode1.equals("4")||
								mode2.equals("4")||
								mode3.equals("4")||
								mode4.equals("4")||
								mode5.equals("4")){
							isSTOuser = true;
						}
						
					}
					else{
						j+= 400000;
					}
				}
			}
			if(isSTOuser){
				stoUser.add("1");
			}
			else{
				stoUser.add("0");
			}
		}
		myData.put(UtilsTS.stoUser, stoUser);
	}
	
	private void processNest() {
		// TODO Auto-generated method stub
		ArrayList<Object> nests = new ArrayList<Object>();
		for(int i = 0; i < myData.get(UtilsTS.id).size(); i++){
			String nest;
			if(myData.get(UtilsTS.pStart).get(i).equals(UtilsTS.trueValue)){
				int stoCount = 0;
				int ptCount = 0;
				int carDriverCount = 0;
				int carPassCount = 0;
				int activeModeCount = 0;
				String hhId = (String)myData.get(UtilsTS.hhId).get(i);
				String persId = (String)myData.get(UtilsTS.persId).get(i);
				for(int j = i; j < myData.get(UtilsTS.id).size(); j++){
					if(hhId.equals((String)myData.get(UtilsTS.hhId).get(j)) && persId.equals((String)myData.get(UtilsTS.persId).get(j))){
						String mode1 = ((String) myData.get(UtilsTS.mode1).get(j)).trim();
						String mode2 = ((String) myData.get(UtilsTS.mode2).get(j)).trim();
						String mode3 = ((String) myData.get(UtilsTS.mode3).get(j)).trim();
						String mode4 = ((String) myData.get(UtilsTS.mode4).get(j)).trim();
						String mode5 = ((String) myData.get(UtilsTS.mode5).get(j)).trim();
						if(mode1.equals("4")||
								mode2.equals("4")||
								mode3.equals("4")||
								mode4.equals("4")||
								mode5.equals("4")){
							stoCount++;
						}
						else if(mode1.equals("1")|| //car driver and moto drivers
								mode2.equals("1")||
								mode3.equals("1")||
								mode4.equals("1")||
								mode5.equals("1")||
								mode1.equals("15")||
								mode2.equals("15")||
								mode3.equals("15")||
								mode4.equals("15")||
								mode5.equals("15")){
							carDriverCount++;
						}
						else if(mode1.equals("2")|| // car passengers
								mode2.equals("2")||
								mode3.equals("2")||
								mode4.equals("2")||
								mode5.equals("2")){
							carPassCount++;
						}
						else if(mode1.equals("11")|| // active modes (bike and walk
								mode2.equals("11")||
								mode3.equals("11")||
								mode4.equals("11")||
								mode5.equals("11")||
								mode1.equals("12")||
								mode2.equals("12")||
								mode3.equals("12")||
								mode4.equals("12")||
								mode5.equals("12")){
							activeModeCount++;
						}
						else{
							ptCount++;
						}
					}
					else{
						j+= 400000;
					}
				}
				if(stoCount > 0){
					nest = "2";
				}
				else if( ptCount == Utils.max(carDriverCount,carPassCount,activeModeCount,ptCount)){
					nest = "3";
				}
				else if( carDriverCount == Utils.max(carDriverCount,carPassCount,activeModeCount,ptCount)){
					nest = "0";
				}
				else if( carPassCount == Utils.max(carDriverCount,carPassCount,activeModeCount,ptCount)){
					nest = "1";
				}
				else{
					nest = "4";
				}
			}
			else{
				nest = "1000";
			}
			nests.add(nest);
		}
		myData.put(UtilsTS.nest, nests);
	}

	private void processSocioDemographic() {
		// TODO Auto-generated method stub
		ArrayList<Object> myAges = new ArrayList<Object>();
		ArrayList<Object> myOccs = new ArrayList<Object>();
		ArrayList<Object> mySexs = new ArrayList<Object>();
		ArrayList<Object> myNpers = new ArrayList<Object>();
		ArrayList<Object> myCars = new ArrayList<Object>();
		
		for(int i = 0; i < myData.get(UtilsTS.id).size(); i++){
			int ageCat = Integer.parseInt((String) myData.get(UtilsTS.ageGroup).get(i));
			int myAgeCat = -1;
			if(ageCat <=2){myAgeCat = -1;}
			else if(ageCat <= 4){myAgeCat = 0;}
			else if(ageCat ==5){myAgeCat = 1;}
			else if(ageCat ==6){myAgeCat = 2;}
			else if(ageCat ==7){myAgeCat = 3;}
			else if(ageCat ==8){myAgeCat = 4;}
			else if(ageCat ==9){myAgeCat = 4;}
			else if(ageCat ==10){myAgeCat = 5;}
			else if(ageCat ==11){myAgeCat = 6;}
			else if(ageCat >=12){myAgeCat = 6;}
			myAges.add(myAgeCat);
			
			int occ = Integer.parseInt((String) myData.get(UtilsTS.occupation).get(i));
			int myOcc = -1;
			if(occ == 1){myOcc = 0;}
			else if(occ == 2){myOcc = 0;}
			else if(occ == 3){myOcc = 1;}
			else if(occ == 4){myOcc = 2;}
			else if(occ == 5){myOcc = 3;}
			else if(occ == 6){myOcc = -1;}
			else if(occ == 7){myOcc = -1;}
			else if(occ == 8){myOcc = -1;}
			myOccs.add(Integer.toString(myOcc));
			
			int sex = Integer.parseInt((String) myData.get(UtilsTS.sex).get(i));
			int mySex = -1;
			if(sex == 1){mySex = 0;}
			else if(sex == 2){mySex = 1;}
			mySexs.add(mySex);
			
			int nPer = Integer.parseInt((String) myData.get(UtilsTS.pers).get(i));
			int myNPer = -1;
			if(nPer == 1){myNPer = 0;}
			else if(nPer== 2){myNPer = 1;}
			else if(nPer == 3){myNPer= 2;}
			else if(nPer == 4){myNPer = 3;}
			else if(nPer >= 5){myNPer = 4;}
			myNpers.add(myNPer);
			
			int ncar = Integer.parseInt((String) myData.get(UtilsTS.cars).get(i));
			int myNcar = -1;
			if(ncar == 0){myNcar = 0;}
			else if(ncar == 1){myNcar= 1;}
			else if(ncar== 2){myNcar = 2;}
			else if(ncar >= 3){myNcar= 3;}
			myCars.add(myNcar);
		}
		myData.put(UtilsTS.ageGroup,myAges);
		myData.put(UtilsTS.sex, mySexs);
		myData.put(UtilsTS.occupation,myOccs);
		myData.put(UtilsTS.pers, myNpers);
		myData.put(UtilsTS.cars, myCars);
	}

	private void processChoiceIndex() {
		// TODO Auto-generated method stub
		myData.put(UtilsTS.alternative, new ArrayList<Object>());
		myData.put(UtilsTS.choice, new ArrayList<Object>());
		
		for(int i = 0; i < myData.get(UtilsTS.id).size(); i++){
			if(myData.get(UtilsTS.pStart).get(i).equals(UtilsTS.trueValue)){
				String altFirstDep = (String)myData.get(UtilsTS.firstDep + "Short").get(i);
				String altLastDep = (String)myData.get(UtilsTS.lastDep + "Short").get(i);
				String fidelPTRange = (String)myData.get(UtilsTS.fidelPtRange).get(i);
				Integer nActivities = (Integer)myData.get(UtilsTS.nAct).get(i);
				String nest = (String) myData.get(UtilsTS.nest).get(i);
				
				HashMap<String,Integer> currCombinationChoice = new HashMap<String,Integer>();
				currCombinationChoice.put(UtilsTS.nAct, nActivities);
				currCombinationChoice.put(UtilsTS.firstDep+"Short", Integer.parseInt(altFirstDep));
				currCombinationChoice.put(UtilsTS.lastDep+"Short", Integer.parseInt(altLastDep));
				currCombinationChoice.put(UtilsTS.fidelPtRange, Integer.parseInt(fidelPTRange));
				currCombinationChoice.put(UtilsTS.nest, Integer.parseInt(nest));
				int choice = getChoiceIndex(currCombinationChoice);
				myData.get(UtilsTS.alternative).add(Integer.toString(choice));
				String myChoice = BiogemeChoice.getConstantName(currCombinationChoice);
				myData.get(UtilsTS.choice).add(myChoice);
			}
			else{
				int choice = 10000;
				myData.get(UtilsTS.alternative).add(Integer.toString(choice));
				String myChoice = "NOT A DATA POINT";
				myData.get(UtilsTS.choice).add(myChoice);
			}
			
		}
	}
	
	
	private int getChoiceIndex(HashMap<String, Integer> myCombinationChoice) {
		// TODO Auto-generated method stub
		for(BiogemeChoice currChoice: BiogemeControlFileGenerator.choiceIndex){
			if(areEquals(currChoice.choiceCombination,myCombinationChoice)){
				return currChoice.biogeme_case_id;
			}
		}
		System.out.println("--error: combination index was not found for code: " + myCombinationChoice.toString());
		return 1000;
	}
	
	public boolean areEquals(HashMap<String,Integer> m1,HashMap<String,Integer> m2){
		for(String key: m1.keySet()){
			if(!m2.containsKey(key)){
				System.out.println("mapping problem");
				return false;
			}
			if(m1.get(key) != m2.get(key)){
				return false;
			}
		}
		return true;
	}

	private static class processChoiceSet
    implements Callable, Runnable {
    	HashMap<String, ArrayList<Object>> currData;
    	int n;
    	public processChoiceSet(Object subSample, int numberOfAlternatives){
    		currData = (HashMap)subSample;
    		n = numberOfAlternatives;
    	}
    	
        public HashMap<String, ArrayList<Object>> call()  throws Exception {
   		    processAlternatives();
        	return currData;
        }
        
        @Override
		public void run() {
			// TODO Auto-generated method stub
			//return statistics;
		}
	
	
		public void processAlternatives( ){
			for(int i = 0; i < n; i++){
				currData.put(UtilsTS.alternative+Integer.toString(i), new ArrayList<Object>());
			}
			
			for(int i = 0; i < currData.get(UtilsTS.id).size();i++){
				if(currData.get(UtilsTS.pStart).get(i).equals(UtilsTS.trueValue) ){
					String hhId = (String)currData.get(UtilsTS.hhId).get(i);
					String persId = (String)currData.get(UtilsTS.persId).get(i);
					//String pStatut = (String)currData.get(UtilsTS.pStatut).get(i);
					Set<Integer> idClosests = findClosestAlternatives(
							n, 
							Double.parseDouble((String)currData.get(UtilsTS.mtmX).get(i)),
							Double.parseDouble((String)currData.get(UtilsTS.mtmY).get(i)), 
							hhId,
							persId);
					
					addAlternatives(idClosests);	
				}
				else{
					addAlternatives();
				}
				if(i%1000==0){
					System.out.println(i);
				}
			}
		}
	
		private void addAlternatives() {
			// TODO Auto-generated method stub
			for(int i = 0; i < n; i++){
				currData.get(UtilsTS.alternative+Integer.toString(i)).add("0");
			}
		}
	
		private void addAlternatives(Set<Integer> idClosests) {
			// TODO Auto-generated method stub
			Iterator<Integer> it = idClosests.iterator();
			for(int i = 0; i < n; i++){
				int currIpere = it.next();
				int j = 0;
				while(currIpere != Integer.parseInt((String)currData.get(UtilsTS.id).get(j))){//IPERE is a unique identifier for each record in the travel survey of Montreal
					j++;
				}
			
				String alternative = (String)currData.get(UtilsTS.alternative).get(j);
				currData.get(UtilsTS.alternative+Integer.toString(i)).add(alternative);
			}
		}
		
		

		private Set<Integer> findClosestAlternatives(int n, double x, double y, String hhId, String persId) {
			// TODO Auto-generated method stub
			HashMap<Integer,Integer> closeAlternatives = new HashMap<Integer, Integer>(); // structure : the key is formed by IPERE (from OD survey), the value is the distance
			
			for(int j = 0; j < currData.get(UtilsTS.id).size(); j++){
				if(
						currData.get(UtilsTS.pStart).get(j).equals(UtilsTS.trueValue) && 
						!(((String)currData.get(UtilsTS.hhId).get(j)).equals(hhId) && 
						((String)currData.get(UtilsTS.persId).get(j)).equals(persId))
				){
					double curX = Double.parseDouble((String)currData.get(UtilsTS.mtmX).get(j));
					double curY = Double.parseDouble((String)currData.get(UtilsTS.mtmY).get(j));
					int curIpere = Integer.parseInt((String)currData.get(UtilsTS.id).get(j));
					int curDist = (int)(Math.pow((x-curX), 2) + Math.pow((y-curY), 2)); 
					
					if(closeAlternatives.size() < n){
						closeAlternatives.put(curIpere, curDist);
					}
					else{
						int[] removeId = getFarthestAltenative(closeAlternatives);
						if(curDist< removeId[1]){
							closeAlternatives.remove(removeId[0]);
							closeAlternatives.put(curIpere, curDist);
						}
					}				
				}
				else{	
				}	
			}
			return closeAlternatives.keySet();
		}
		
		private int[] getFarthestAltenative(HashMap<Integer, Integer> closeAlternatives) {
			// TODO Auto-generated method stub
			int curDist = 0;
			int curId = 0;
			for(java.util.Map.Entry<Integer, Integer> e:closeAlternatives.entrySet()){
				if(e.getValue() > curDist){
					curId = e.getKey();
					curDist = e.getValue();
				}
			}
			int answer[] = new int[2];
			answer[0] = curId;
			answer[1] = curDist;
			return answer;
		}
	}
	
	

	public void addOnes(String header) {
		// TODO Auto-generated method stub
		myData.put(header, new ArrayList<Object>());
		for(int i = 0; i < myData.get(UtilsTS.id).size();i++){
			myData.get(header).add("1");
		}
	}
	
	public void departureHour3categories(){
		myData.put(UtilsTS.firstDep + "Short", new ArrayList());
		for(int i = 0; i < myData.get(UtilsTS.id).size(); i++){
			String temp = (String) myData.get(UtilsTS.firstDep).get(i);
			if(temp.equals("1")){myData.get(UtilsTS.firstDep+ "Short").add("0");} //living earlier then peak hour (before 7am)
			else if (temp.equals("2")){myData.get(UtilsTS.firstDep+ "Short").add("1");} // leaving during peak hour (6-9am)
			else if (temp.equals("10")){myData.get(UtilsTS.firstDep+ "Short").add("10");}
			else {myData.get(UtilsTS.firstDep+ "Short").add("2");} 
		}
	}
	
	public void lastDepartureHour3categories(){
		myData.put(UtilsTS.lastDep + "Short", new ArrayList());

		for(int i = 0; i < myData.get(UtilsTS.id).size(); i++){
			int hour = Integer.parseInt((String)myData.get(UtilsTS.lastDep).get(i));
			if(hour<1530){myData.get(UtilsTS.lastDep+ "Short").add("0");}
			else if(hour < 1830){myData.get(UtilsTS.lastDep+ "Short").add("1");}
			else if (hour >=1830){myData.get(UtilsTS.lastDep+ "Short").add("2");}
			else{myData.get(UtilsTS.lastDep + "Short").add("10");}
		}	
	}

	
	
	
	public int antitheticDrawMNL(ArrayList<Double> utilities){
		ArrayList<Double> exponentials = new ArrayList<Double>();
		double denominator = 0;
		for(Double u : utilities){
			double expU = Math.exp(u);
			exponentials.add(expU);
			denominator += expU;
		}
		ArrayList<Double> cumulativeProbabilities = new ArrayList<Double>();
		double cumul = 0;
		for(Double eU : exponentials){
			cumul += eU;
			cumulativeProbabilities.add(cumul/denominator);
		}
		
		double randVal = randGen.NextDoubleInRange(0, 1);
		int index =0;
		for(int i =0; i<cumulativeProbabilities.size(); i++){
			if(randVal>cumulativeProbabilities.get(i)){
			}
			else{
				index = i;
				i+=1000;
			}
		}
		return index;
	}
}
