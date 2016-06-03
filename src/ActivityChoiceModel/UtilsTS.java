package ActivityChoiceModel;

import java.util.ArrayList;

public class UtilsTS {
	
	public static String end = "endWith";
	public static String startWithSto = "startWithSto";
	public static String accessToPt = "accessIndicator";
	public static String city;
	//Constants
    public static String id;
    public static String hhId;
    public static String persId;
    public static String domSdr;
    public static String domAd;
    public static String cars;
    public static String pers;
    public static String kids;
    public static String pStart;
    public static String noDep;
    public static String sex;
    public static String ageGroup;
    public static String age;
    public static String occupation;
    public static String licence;
    public static String weigth;
    public static String groupHour;
    public static String hour;
    public static String backHomeTrip;
    public static String mtmX;
    public static String mtmY;
    


    
    //new
    public static String chainLength;
    public static String firstDep;
    public static String lastDep;
    public static String minDist;
    public static String maxDist;
    public static String maxActTime;
    public static String fidelPtRange;
    public static String fidelPt;
    public static String nAct;
    public static String tourType;
    public static String pStatut;
    public static String motor;
    
    public static String xOrigin;
    public static String yOrigin;
    public static String xDest;
    public static String yDest;
    
    
    public static String sim;
	public static String choice;
	public static String stayedHome;
	public static String noPT;
	
	public static String mode1;
	public static String mode2;
	public static String mode3;
	public static String mode4;
	public static String mode5;
	
	
	
	
	//###########DICO pour le recensement
	public static String dauid = "dauid";
	public static String alternative = "alternative";
	public static String var = "_var";
	public static String edu = "edu";
	public static String inc = "inc";
	public static String mStat = "mStat";
	
	
	// ###Dico mode
	public static String carDriver = "carDriver";
	public static String carPassenger = "carPassenger";
	public static String stoUser = "stoUser";
	public static String ptUserNoSto = "ptUserNoSto";
	public static String activeMode = "activeMode";
	public static String nest = "NEST";
	public static String trueValue = "1";
	
	
	public UtilsTS(String area){
		
			city = "Gatineau";
			
			//Constants
		     id = "ID";
		     hhId = "HH_ID";
		     persId = "PERS_ID";
		     pStart = "PERS_START";
		     backHomeTrip = "BACK_HOME_TRIP";
		     nAct = "N_ACT";
		     ageGroup = "AGE_GROUP";
		     occupation = "OCCUPATION";
		     cars = "N_CARS";
		     pers = "HH_SIZE";
		     firstDep = "FIRST_DEP";
		     lastDep = "LAST_DEP";
		     weigth = "WEIGHT";
		     sex = "SEXE";
		     pStatut = "OCCUPATION";
		     groupHour = "GROUP_HOUR";
		     mtmX = "X";
		     mtmY = "Y";
		     domSdr = "sdrlogis05";
		     domAd = "sdrlogis05";
		     kids = "KIDS";
		     noDep = "NDEP";
		     age = "AGE";
		     licence = "PERMIS";
		     hour = "HOUR";
		     xOrigin ="XMTM83Z9OR";
		     yOrigin="YMTM83Z9OR";
		     xDest="XMTM83Z9D";
		     yDest="YMTM83Z9LG";

		    
		    //new
		     chainLength = "DIST_2";
		     
		     minDist = "MIN_DIST";
		     maxDist = "MAX_DIST";
		     maxActTime = "MAX_ACT_TIME";
		     fidelPtRange = "FIDEL_PT_RANGE";
		     fidelPt = "FIDEL_PT";
		     tourType = "TOUR_TYPE";
		     motor = "MOTOR";
		     sim = "sim";
			 choice = "CHOICE";
			 stayedHome = "HOME";
			 noPT = "NO_PT";
			 
			 mode1 = "MODE1";
			 mode2 = "MODE2";
			 mode3 = "MODE3";
			 mode4 = "MODE4";
			 mode5 = "MODE5";
	}    
}
