/**
 * 
 */
package Smartcard;

import java.io.IOException;
import java.util.HashMap;

import ActivityChoiceModel.DataManager;

/**
 * @author Antoine
 *
 */
@Deprecated
public class StationDataManager extends DataManager{

	HashMap<String, GTFSStop> myStations = new HashMap<String,GTFSStop>();
	
	public StationDataManager(){
		
	}
	
	public HashMap<String, GTFSStop> prepareStations(String pathToStationsData) throws IOException{
		initialize(pathToStationsData);
		createStations();
		return myStations;
	}

	private void createStations() {
		// TODO Auto-generated method stub
		for(int i = 0; i < myData.get(UtilsST.stationId).size(); i++){
			GTFSStop newS = new GTFSStop();
			newS.myId = myData.get(UtilsST.stationId).get(i);
			newS.lat = Double.parseDouble(myData.get(UtilsST.x).get(i));
			newS.lon = Double.parseDouble(myData.get(UtilsST.y).get(i));
			myStations.put(newS.myId, newS);
		}
	}
}
