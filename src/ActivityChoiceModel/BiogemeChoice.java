/**
 * 
 */
package ActivityChoiceModel;

import java.util.ArrayList;
import java.util.HashMap;

import Smartcard.UtilsSM;
import Utils.Utils;

/**
 * @author Antoine
 *
 */
public class BiogemeChoice {
	
	public int biogeme_id;
	public int biogeme_case_id;
	public HashMap<String,Integer> choiceCombination = new HashMap<String,Integer> ();
	public double probability;
	public double utility;
	public HashMap<String,String> myAttributes = new HashMap<String, String>();
	public String nest = new String();
	
	public BiogemeChoice(){
		
	}

	public boolean isAffecting(BiogemeHypothesis currH, BiogemeAgent currAgent) {
		// TODO Auto-generated method stub
		
		for(int i = 0; i < currH.affectingCategories.size();i++){
			if(choiceCombination.containsKey(currH.affectingDimensionName)){
				if(choiceCombination.get(currH.affectingDimensionName) == currH.affectingCategories.get(i)){
					return true;
				}
			}
			else if(currAgent.myAttributes.containsKey(currH.affectingDimensionName)){
				if(currH.affectingCategories.get(i) == Integer.parseInt(currAgent.myAttributes.get(currH.affectingDimensionName))){
					return true;
				}
			}
			else{
				//System.out.println("oups " + currH.affectingDimensionName);	
			}
		}
		return false;
	}
	
	
	public boolean isAffected(BiogemeHypothesis currH){
		// TODO Auto-generated method stub
		
		for(int i = 0; i < currH.affectedCategories.size();i++){
			if(choiceCombination.get(currH.affectedDimensionName) == currH.affectedCategories.get(i)){
				return true;
			}
		}
		//System.out.println("oups " + currH.affectingDimensionName);
		return false;
	}
	
	/*public boolean isCst(BiogemeHypothesis currH){
		// TODO Auto-generated method stub
		if(currH.coefName.equals(getConstantName())){
			return true;
		}
		
		return false;
	}*/

	public double getAffectingValue(BiogemeHypothesis currH, BiogemeAgent currAgent) {
		// TODO Auto-generated method stub
		for(int i = 0; i < currH.affectingCategories.size();i++){
			if(choiceCombination.get(currH.affectingDimensionName) == currH.affectingCategories.get(i)){
				return currH.affectingCategories.get(i);
			}
			if(choiceCombination.get(currH.affectingDimensionName) == Integer.parseInt(currAgent.myAttributes.get(currH.affectingDimensionName))){
				return Double.parseDouble(currAgent.myAttributes.get(currH.affectingDimensionName));
			}
		}
		return (Double) null;
	}
	
	public String getConstantName(){
		return getConstantName(choiceCombination);
	}
	
	public static String getConstantName(HashMap<String, Integer> combination){
		String constantName = new String();
		
		String nestDimensionName = BiogemeControlFileGenerator.getNestDimension();
		ArrayList<Integer> aggregatedNests = BiogemeControlFileGenerator.choiceDimensions.get(nestDimensionName).aggregated;
		if(aggregatedNests.contains(combination.get(nestDimensionName))){
			constantName = "C_"+combination.get(nestDimensionName);
		}
		
		else{
			 constantName = "C";
			for(String s: BiogemeControlFileGenerator.dimensionOrder){
				constantName += "_" + combination.get(s);
			}
		}
		return constantName;
	}
	
	public  String getNestName(){
		return getNestName(choiceCombination);
	}
	
	public static String getNestName(HashMap<String, Integer> combination){
		String constantName = new String();
		String nestDimensionName = BiogemeControlFileGenerator.getNestDimension();
		constantName = "nest_" + Integer.toString(combination.get(nestDimensionName));
		/*if(combination.get(UtilsTS.nest) == 0){
			constantName = UtilsTS.carDriver;
		}
		else if(combination.get(UtilsTS.nest) == 1){
			constantName = UtilsTS.carPassenger;
		}
		else if(combination.get(UtilsTS.nest) == 2 &&
				combination.get(UtilsTS.nAct)!=0 && combination.get(UtilsTS.fidelPtRange)!=0){
			constantName = UtilsTS.stoUser;
		}
		else if(combination.get(UtilsTS.nest) == 3){
			constantName = UtilsTS.ptUserNoSto;
		}
		else{ //last case is active mode and we added all 'bad record of the other cases
			constantName = UtilsTS.activeMode;
		}*/
		return constantName;
	}
	
	public String toString(){
		String answer = Integer.toString(biogeme_id);
		for(String key: choiceCombination.keySet()){
			answer+= Utils.COLUMN_DELIMETER + key + Utils.COLUMN_DELIMETER + choiceCombination.get(key);
		}
		return answer;
	}
	
}
