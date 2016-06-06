/**
 * 
 */
package ActivityChoiceModel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;

import Smartcard.PublicTransitSystem;
import Smartcard.Smartcard;
import Smartcard.GTFSStop;
import Smartcard.UtilsSM;
import Utils.RandomNumberGen;
import Utils.Utils;

/**
 * The Agent is the one making decision in our simulation.
 * @author Antoine
 *
 */
public class BiogemeAgent {
	

	public HashMap<String, String> myAttributes;
	protected static RandomNumberGen randGen = new RandomNumberGen();
	ArrayList<? extends BiogemeChoice> myChoices = new ArrayList<BiogemeChoice>();
	public boolean isDistributed = false;
	public double smartcard = 0.0;
	
	public BiogemeAgent(){
		myAttributes = new HashMap<String, String>();
	}

	public void setAttributes(ArrayList<String> attributeNames, ArrayList<String> attributeValues) {
		// TODO Auto-generated method stub
		for(int i = 0; i < attributeNames.size(); i++){
			myAttributes.put(attributeNames.get(i), attributeValues.get(i));
		}
	}
	
	/**
	 * The agent is given a set of choices (ArrayList<BiogemeChoice>). He choose one amongst the choices he is presented.
	 * The selected choice is stored in the agent {@link BiogemeAgent#myAttributes} under the label {@link BiogemeChoice#biogeme_case_id}.
	 * @param choiceSet
	 */
	public void applyModel(ArrayList<BiogemeChoice> choiceSet) {
		// TODO Auto-generated method stub
		computeUtilities(choiceSet);
		ArrayList<Double> choiceCumProb = getChoicesCumulativeProbabilities(choiceSet);
		int choiceIndex = antitheticDraw(choiceCumProb);
		BiogemeChoice choice = choiceSet.get(choiceIndex);
		myAttributes.put(UtilsTS.sim, Integer.toString(choice.biogeme_case_id));
	}

	/**
	 * Transform the {@code utilities} from utility values into cumulative probability values ranging between 0 and 1.
	 * @param utilities
	 * @return sortier ArrayList of cumulative probabilities from 0 to 1.
	 */

	private ArrayList<Double> getProbabilities(ArrayList<Double> utilities) {
		// TODO Auto-generated method stub
		ArrayList<Double> cumProbabilities = new ArrayList<Double>();
		Double logsum = 0.0;
		for(int i = 0; i < utilities.size(); i++){
			logsum += Math.exp(utilities.get(i));
		}
		double currProbability = 0;
		for(int i = 0; i < utilities.size(); i++){
			currProbability += Math.exp(utilities.get(i)) / logsum;
			cumProbabilities.add(currProbability);
		}
		return cumProbabilities;
	}

	
	/**
	 * If the travel survey was processed to attach alternatives to the realized choice, the function read the data
	 * from the input dataset (travel survey) and identify columns including {@link UtilsTS#alternative} in the header as reachable alternatives.
	 * @return
	 */
	@Deprecated
	public ArrayList<BiogemeChoice> generateChoiceSetFromTravelSurveyAlternatives() {
		// TODO Auto-generated method stub
		ArrayList<BiogemeChoice> choiceSet = new ArrayList<BiogemeChoice>();
		ArrayList<String> alreadySampled = new ArrayList<String>();
		
		for(String header: myAttributes.keySet()){
			if(header.contains(UtilsTS.alternative)){
				int choiceIndex = Integer.parseInt(myAttributes.get(header));
				if(! (choiceIndex == -1)){
					BiogemeChoice curChoice = BiogemeSimulator.getChoice(choiceIndex);
					String cstName = curChoice.getConstantName();
					if(!alreadySampled.contains(cstName)){
						choiceSet.add(curChoice);
						alreadySampled.add(cstName);
					}
				}
			}
		}
		addNestChoices(choiceSet, alreadySampled);
		return choiceSet;
	}
	
	@Deprecated
	public ArrayList<BiogemeChoice> generateChoiceSetFromTravelSurvey() {
		// TODO Auto-generated method stub
		ArrayList<BiogemeChoice> choiceSet = new ArrayList<BiogemeChoice>();
		for(String header: myAttributes.keySet()){
			if(header.contains(UtilsTS.alternative)){
				int choiceIndex = Integer.parseInt(myAttributes.get(header));
				BiogemeChoice curChoice = BiogemeSimulator.getChoice(choiceIndex);
				choiceSet.add(curChoice);
			}
		}
		return choiceSet;
	}
	


	@Deprecated
	private void addNestChoices(ArrayList<BiogemeChoice> choiceSet, ArrayList<String> alreadySampled) {
		// TODO Auto-generated method stub
		for(BiogemeChoice curChoice: BiogemeSimulator.modelChoiceUniverse){
			String cstName = curChoice.nest;
			if(cstName.equals(UtilsTS.carDriver) ||
					cstName.equals(UtilsTS.carPassenger) ||
					cstName.equals(UtilsTS.activeMode) ||
					cstName.equals(UtilsTS.ptUserNoSto)){
				if(!alreadySampled.contains(cstName)){
					choiceSet.add(curChoice);
					alreadySampled.add(cstName);
				}
			}
		}
	}

	
	/**
	 * Operate an antithetic draw on the cumulative probability description. Generates a realized choice which respect observed utility functions.
	 * @param cumulativeProbabilities contains the cumulative probabilities ordered from 0 to 1
	 * @return the rank of the realized choice
	 */
	public int antitheticDraw(ArrayList<Double> cumulativeProbabilities){

		double randVal = randGen.NextDoubleInRange(0, 1);
		int index =0;
		for(int i =0; i<cumulativeProbabilities.size(); i++){
			if(randVal>cumulativeProbabilities.get(i)){
			}
			else{
				index = i;
				i+=cumulativeProbabilities.size()+1;
			}
		}
		return index;
	}

	/*@Deprecated
	private ArrayList<Smartcard> generateChoiceSet(int choiceSetSize, 
			HashMap<Double,ArrayList<Smartcard>> closeSmartcards){
		// TODO Auto-generated method stub
		
		double myZone = Double.parseDouble(myAttributes.get(UtilsSM.zoneId));
		ArrayList<Smartcard> agentChoiceSet = new ArrayList<Smartcard>();
		//ArrayList<Integer> myStations = PublicTransitSystem.geoDico.get(myZone);
		if(isDistributed){
			ArrayList<Smartcard> stayHome = PublicTransitSystem.myCtrlGen.getNestsChoice();
			agentChoiceSet.addAll(stayHome);
		}
		else{
			ArrayList<Smartcard> potentialSmartcard = closeSmartcards.get(myZone);
			
			if(potentialSmartcard.size() > choiceSetSize){
				Random random = new Random();
				for(int i = 0; i < choiceSetSize; i++){
					if(potentialSmartcard.size()!=0){
						int nextChoice = random.nextInt(potentialSmartcard.size());
						Smartcard currChoice = potentialSmartcard.get(nextChoice);
						if(!currChoice.isDistributed){
							if(Utils.occupationCriterion){
								if((Integer.parseInt(myAttributes.get("occ")) == currChoice.fare) ||
										(myAttributes.get("occ").equals("3") && 
												currChoice.fare ==0) ){
									agentChoiceSet.add(currChoice);
								}
								else{
									i= i - 1;
								}
							}
							else{
								agentChoiceSet.add(currChoice);
							}
						}
						else{
							i = i-1;
						}
					}
				}
			}
			
			
			else{
				for(Smartcard currChoice: potentialSmartcard){
					if(!currChoice.isDistributed){
						agentChoiceSet.add(currChoice);
					}
				}
			}
			ArrayList<Smartcard> stayHome = PublicTransitSystem.myCtrlGen.getNestsChoice();
			agentChoiceSet.addAll(stayHome);
		}
		
		return agentChoiceSet;
	}*/
	
	public ArrayList<Smartcard> generateChoiceSet(HashMap<String, ArrayList<Smartcard>> zonalSmartcardIndex){
		// TODO Auto-generated method stub
		
		String myZone = myAttributes.get(UtilsSM.zoneId).trim();
		ArrayList<Smartcard> agentChoiceSet = new ArrayList<Smartcard>();
		//ArrayList<Integer> myStations = PublicTransitSystem.geoDico.get(myZone);
		if(isDistributed){
			ArrayList<Smartcard> nestChoices = PublicTransitSystem.myCtrlGen.getNestsChoice();
			agentChoiceSet.addAll(nestChoices);
		}
		else{
			ArrayList<Smartcard> potentialSmartcard = zonalSmartcardIndex.get(myZone);
			
			if(potentialSmartcard.size()!=0){
				for(int i = 0; i < potentialSmartcard.size();i++){
					Smartcard currChoice = potentialSmartcard.get(i);
					if(!currChoice.isDistributed){
						if(Utils.occupationCriterion){
							if((Integer.parseInt(myAttributes.get("occ")) == currChoice.fare) ||
									(myAttributes.get("occ").equals("3") && 
											currChoice.fare ==0) ){
								agentChoiceSet.add(currChoice);
							}
						}
						else{
							agentChoiceSet.add(currChoice);
						}
					}
				}
			}
		}
		myChoices = agentChoiceSet;
		return agentChoiceSet;
	}
	
	

	public double[] writeCosts(int size, int smartcardCount) {
		// TODO Auto-generated method stub
		
		double[] newRow = new double[size];
		for(int i = 0; i < size; i++){
			newRow[i] = Double.MAX_VALUE;
		}
		for(BiogemeChoice currChoice: myChoices){
			double cost = 10000 - 1000* currChoice.utility;
			if(currChoice.getConstantName().equals(UtilsSM.noPt)){
				for(int i = smartcardCount; i < size; i++){
					newRow[i] = cost;
				}
			}
			else{
				newRow[((Smartcard)currChoice).columnId] = cost;
			}
		}
		return newRow;
	}
	
	/*
	@Deprecated
	public void processSmartcardChoiceSet(ArrayList<Smartcard> choiceSet){
		ArrayList<Double> utilities	 = new ArrayList<Double>();
		for(int i = 0; i < choiceSet.size(); i++){
			
			double utility = 0;
			//int choiceId = choiceSet.get(i).biogeme_group_id;
			BiogemeChoice currChoice = choiceSet.get(i);
			
			for(BiogemeHypothesis currH: BiogemeSimulator.modelHypothesis){
				if(currH.isCst()){
					utility += currH.getCoefficientValue();
				}
				else if(currChoice.isAffected(currH)  && currH.isDummy){
					if( currChoice.isAffecting(currH, this)){
						utility += currH.getCoefficientValue();
					}
				}
				else if(currChoice.isAffected(currH) && !currH.isDummy){
					if(currH.isAgentSpecificVariable){
						String att = UtilsSM.dictionnary.get(currH.affectingDimensionName) ;
						utility += currH.getCoefficientValue() * Double.parseDouble(myAttributes.get(att));
					}
					else if(currH.isAlternativeSpecificVariable){
						String att = UtilsSM.dictionnary.get(currH.affectingDimensionName);
						utility += currH.getCoefficientValue() * Double.parseDouble(currChoice.myAttributes.get(att));
					}
					else{
						System.out.println(currH.coefName + " was not considered");
					}
					//utility += currH.getCoefficientValue() * currChoice.getAffectingValue(currH, this);
				}
			}

			currChoice.utility = utility;
			utilities.add(utility);
		}
				// TODO Auto-generated method stub
			
		Double logsum = 0.0;
		for(int i = 0; i < choiceSet.size(); i++){
			logsum += Math.exp(choiceSet.get(i).utility);
		}
		double currProbability = 0;
		for(BiogemeChoice currChoice: choiceSet){
			currProbability = Math.exp(currChoice.utility) / logsum;
			currChoice.probability = currProbability;
		}
		myChoices = choiceSet;
	}*/
	
	public boolean isStoRider() {
		// TODO Auto-generated method stub
		ArrayList<BiogemeChoice> choiceSet = BiogemeSimulator.modelChoiceUniverse;
		choiceSet = restrainChoiceSet(choiceSet);
		HashMap<String, Double> realizedNests = getRealizedNests(choiceSet);
		
		computeUtilities(choiceSet);
		ArrayList<Double> nestCumProb = getNestCumulativeProbabilities(choiceSet, realizedNests);
		int choice = antitheticDraw(nestCumProb);
		
		
		//apply a post treatement to balance when needed
		if(myAttributes.get(UtilsSM.dictionnary.get(UtilsTS.ageGroup)).equals("0") && choice == 2){
			Random r = new Random();
			int rInt = r.nextInt(15);
			if(rInt <= 5){
				choice = 3;
			}
		}
		if((myAttributes.get(UtilsSM.dictionnary.get(UtilsTS.ageGroup)).equals("5") ||
				myAttributes.get(UtilsSM.dictionnary.get(UtilsTS.ageGroup)).equals("6"))
				&& choice == 2){
			Random r = new Random();
			int rInt = r.nextInt(10);
			if(rInt < 5){
				choice = 1;
			}
		}
		
		
		if(choice == 2){
			return true;
		}
		else{
			return false;
		}
	}


	private ArrayList<Double> getNestCumulativeProbabilities(ArrayList<BiogemeChoice> choices, HashMap<String, Double> nests) {
		// TODO Auto-generated method stub
		ArrayList<Double> cumProb = new ArrayList<Double>();
		
		HashMap<String,Double> cumProbs = new HashMap<String,Double>();
		for(String nest: BiogemeSimulator.modelNests.keySet()){
			cumProbs.put(nest, 0.0);
		}
		
		HashMap<String, Double> logsums = new HashMap<String, Double>();
		for(String nest: BiogemeSimulator.modelNests.keySet()){
			logsums.put(nest, 0.0);
		}

		for(BiogemeChoice curChoice: choices){
			String nestName = curChoice.getNestName();
			double scale = BiogemeSimulator.modelNests.get(nestName);
			double logsum = logsums.get(nestName);
			
			logsum += Math.exp(scale * curChoice.utility);
			logsums.put(nestName, logsum);
		}
		
		//take the logarithm of the sum of exp
		for(String nest: nests.keySet()){
			double logsum = logsums.get(nest);
			logsum = Math.log(logsum);
			logsums.put(nest,logsum);
		}
		
		//compute the common enominator
		double denom = 0;
		for(String nest: nests.keySet()){
			double logsum = logsums.get(nest);
			double scale = BiogemeSimulator.modelNests.get(nest);
			denom+= Math.exp(logsum/scale);
		}
		
		//compute the probabilities
		for(String nest: nests.keySet()){
			double logsum = logsums.get(nest);
			double scale = BiogemeSimulator.modelNests.get(nest);
			double prob = Math.exp(logsum/scale)/denom;
			cumProbs.put(nest, prob);
		}
		
		
		//create cumulative probabilities
		cumProb.add(cumProbs.get(UtilsTS.carDriver));
		cumProb.add(cumProb.get(cumProb.size()-1)+cumProbs.get(UtilsTS.carPassenger));
		cumProb.add(cumProb.get(cumProb.size()-1)+cumProbs.get(UtilsTS.stoUser));
		cumProb.add(cumProb.get(cumProb.size()-1)+cumProbs.get(UtilsTS.ptUserNoSto));
		cumProb.add(1.0);
		
		
		return cumProb;
	}
	
	private ArrayList<Double> getChoicesCumulativeProbabilities(ArrayList<BiogemeChoice> myChoices) {
		// TODO Auto-generated method stub
		ArrayList<Double> cumProb = new ArrayList<Double>();
		HashMap<String, Double> logsums = new HashMap<String, Double>();
		logsums = initLogsum(myChoices);
		
		HashMap<String, Double> subNests = new HashMap<String,Double>();
		subNests = getRealizedNests(myChoices);
		
		for(BiogemeChoice curChoice: myChoices){
			String nestName = curChoice.getNestName();
			double scale = subNests.get(nestName);
			double logsum = logsums.get(nestName);
			
			logsum += Math.exp(scale * curChoice.utility);
			logsums.put(nestName, logsum);
		}
		for(String nest: subNests.keySet()){
			double logsum = logsums.get(nest);
			logsum = Math.log(logsum);
			logsums.put(nest,logsum);
		}
		double sumLog = 0;
		for(String nest: subNests.keySet()){
			double logsum = logsums.get(nest);
			double scale = subNests.get(nest);
			sumLog+= Math.exp(logsum/scale);
		}
		
		double cumP = 0;
		for(int i = 0; i < myChoices.size(); i++){
			BiogemeChoice curChoice = myChoices.get(i);
			double thisProb = 0;
			
			double nestLog = logsums.get(curChoice.getNestName());
			double nestScale = subNests.get(curChoice.getNestName());
			
			thisProb = (Math.exp(nestScale * curChoice.utility) / Math.exp(nestLog)) *
					(Math.exp(nestLog/nestScale) / sumLog);
			
			
			cumP+=thisProb;
			cumProb.add(cumP);
		}
		cumProb.remove(cumProb.size()-1);
		cumProb.add(1.0);
		return cumProb;
	}

	private HashMap<String, Double> getRealizedNests(ArrayList<BiogemeChoice> myChoices) {
		// TODO Auto-generated method stub
		HashMap<String, Double> realizedNests = new HashMap<String, Double>();
		for(BiogemeChoice choice: myChoices){
			String name = choice.getNestName();
			Double scale = BiogemeSimulator.modelNests.get(name);
			realizedNests.put(name, scale);
		}
		
		return realizedNests;
	}
	
	private HashMap<String, Double> initLogsum(ArrayList<BiogemeChoice> myChoices) {
		// TODO Auto-generated method stub
		HashMap<String, Double> realizedNests = new HashMap<String, Double>();
		for(BiogemeChoice choice: myChoices){
			String name = choice.getNestName();
			realizedNests.put(name, 0.0);
		}
		return realizedNests;
	}

	public void computeUtilities(ArrayList<? extends BiogemeChoice> choiceSet){
		
		for(int i = 0; i < choiceSet.size(); i++){
			
			double utility = 0;
			//int choiceId = choiceSet.get(i).biogeme_group_id;
			BiogemeChoice currChoice = choiceSet.get(i);
			currChoice.utility = 0;
			HashMap<String, Integer> currCombination = currChoice.choiceCombination;
			
			for(BiogemeHypothesis currH: BiogemeSimulator.modelHypothesis){
				if(currChoice.getConstantName().equals(currH.coefName)){
					utility += currH.getCoefficientValue();
				}
				else if(currCombination.get(UtilsTS.nest).equals(UtilsTS.carDriver) &&
						currH.affectedDimensionName.equals(UtilsTS.nest) &&
						currH.affectedCategories.contains(0)
						){
					utility = updateUtility(currH,currChoice,utility);
				}
				else if(currCombination.get(UtilsTS.nest).equals(UtilsTS.carPassenger) &&
						currH.affectedDimensionName.equals(UtilsTS.nest) &&
						currH.affectedCategories.contains(1)
						){
					utility = updateUtility(currH,currChoice,utility);
				}
				else if(currCombination.get(UtilsTS.nest).equals(UtilsTS.ptUserNoSto) &&
						currH.affectedDimensionName.equals(UtilsTS.nest) &&
						currH.affectedCategories.contains(3)
						){
					utility = updateUtility(currH,currChoice,utility);
				}
				else if(currCombination.get(UtilsTS.nest).equals(UtilsTS.activeMode) &&
						currH.affectedDimensionName.equals(UtilsTS.nest) &&
						currH.affectedCategories.contains(4)
						){
					utility = updateUtility(currH,currChoice,utility);
				}
				else if(currCombination.get(UtilsTS.nest).equals(UtilsTS.stoUser)){
					utility = updateUtility(currH,currChoice,utility);
				}			
			}
			currChoice.utility = utility;
		}	
		myChoices = choiceSet;
	}

	private double updateUtility(BiogemeHypothesis currH, BiogemeChoice currChoice, double utility) {
		// TODO Auto-generated method stub
		if(currChoice.isAffected(currH)  && currH.isDummy){
			if( currChoice.isAffecting(currH, this)){
				utility += currH.getCoefficientValue();
			}
		}
		else if(currChoice.isAffected(currH) && !currH.isDummy){
			if(currH.isAgentSpecificVariable){
				String att = UtilsSM.dictionnary.get(currH.affectingDimensionName) ;
				utility += currH.getCoefficientValue() * Double.parseDouble(myAttributes.get(att));
			}
			else if(currH.isAlternativeSpecificVariable){
				String att = UtilsSM.dictionnary.get(currH.affectingDimensionName);
				utility += currH.getCoefficientValue() * Double.parseDouble(currChoice.myAttributes.get(att));
			}
			else{
				System.out.println(currH.coefName + " was not considered");
			}
			//utility += currH.getCoefficientValue() * currChoice.getAffectingValue(currH, this);
		}
		return utility;
	}

	
	// TODO Auto-generated method stub
	public ArrayList<BiogemeChoice> restrainChoiceSet(ArrayList<BiogemeChoice> choiceSet) {
		// TODO Auto-generated method stub
		ArrayList<BiogemeChoice> temp = new ArrayList<BiogemeChoice>();
		
		for(int i = 0; i < choiceSet.size(); i++){
			boolean t = true;
			BiogemeChoice curChoice = choiceSet.get(i);
			if(curChoice.getNestName().equals(UtilsTS.carDriver)){
				if(myAttributes.get(UtilsSM.dictionnary.get(UtilsTS.ageGroup)).equals("0")){
					Random r = new Random();
					int rInt = r.nextInt(8);
					if(!(rInt == 1)){
						t = false;
					}
				}
				else if(myAttributes.get(UtilsSM.dictionnary.get(UtilsTS.cars)).equals("0")){
					t = false;
				}
			}
			if(t){
				temp.add(curChoice);
			}
		}
		return temp;
	}

}
