/**
 * 
 */
package Gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.ArrayList;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

import Controlers.PromptButton;
import Controlers.PromptStringInformation;
import Smartcard.UtilsSM;

/**
 * @author Antoine
 *
 */
public class TabRunSocioDemographicInference extends JPanel {

	ArrayList<PromptStringInformation> myStringPrompts = new ArrayList<PromptStringInformation>();
	PromptStringInformation line0;
	PromptStringInformation line1;
	PromptStringInformation line2;
	PromptStringInformation line3;
	PromptStringInformation line4;
	PromptStringInformation line5;
	PromptStringInformation line6;
	PromptStringInformation line7;
	PromptStringInformation line8;
	PromptStringInformation line9;
	PromptStringInformation line10;
	PromptButton line15;
	
	public TabRunSocioDemographicInference(Dimension d){
		super();
		String GTFSadvice = "<html> public transit system can be described in a GTFS file (GTFS stand for Google Transit Feed Specification)."
				+ "They are open data and the data structure is checked and validated. We need the following file from the GTFS:"
				+ "<br/>routes.txt"
				+ "<br/>trips.txt"
				+ "<br/>stops.txt"
				+ "<br/>stop_times.txt";
		
		JLabel header1 = new JLabel("Infering socio-demographic charatecteristics");
		
		line0 = new PromptStringInformation("Path to the population synthesis config file",
				"The population synthesis config file is giving information about the various attributes of the population",
				"association\\ctrl\\config.txt");
		
		line1 = new PromptStringInformation("Path to the choice description file", 
				"<html> .txt file -- this file describes choices attribute following this format: <br>"
				+ "choice, number of discrete alternatives<br>"
				+ "--<br>"
				+ "For example: <br>"
				+ "FIDEL_PT_RANGE, 3<br>" + 
				"FIRST_DEPShort, 3<br>" + 
				"LAST_DEPShort, 3<br>" + 
				"N_ACT, 4<br>" + 
				"NEST, 5</html>",
				"association\\ctrl\\choiceDescription.txt");
		line2 = new PromptStringInformation("Path to hypothesis description file",
				"<html> .txt file -- the file describes hypothesis following this format:<br>"
				+ "DOWNTOWN, accessIndicator = 1, NEST = 2 - 3, dummy <br>"
				+ "where DOWNTOWN: is the hypothesis name, <br>"
				+ "accesIndicator: is the affecting attribute name (this name should be spelled the same way than in the travel survey <br>"
				+ "= 1-2 :are the affecting categories(1 and 2). For a continuous affeting category, just leave =<br>"
				+ "NEST: is the affected choice<br>"
				+ "=2-3: are the affected categories (2 and 3)<br>"
				+ "dummy: is the type of variable (choose: dummy, agent, alternative",
				"association\\ctrl\\hypothesis.txt");
		line3 = new PromptStringInformation("Path to smart card data",
				"<html>.csv file -- path to the smart card data file<br>"
				+ "we recommend using at least one month of data so average values are meaningful<br>"
				+ "--<br>"
				+ "the program should be modified so it can connect to the transaction database, handle large data sets and process information on a daily basis</html>",
				"outputs\\smartcard_with_destinations.csv");
		
		line5 = new PromptStringInformation("<html>Path to the GTFS stops file",
				GTFSadvice,
				"destinationInference\\GTFS\\stops.txt");
		
		line6 = new PromptStringInformation("<html> Path to the zonal shapefile",
				"<html>The shapefile describing the local zones should be registered in lat long format. (ESPG:4036)",
				"association\\data\\disseminationArea.shp");
		
		line4 = new PromptStringInformation("Path to the synthetic population file", 
				".csv file -- provide here the path to the synthetic population you want to use.",
				"association\\data\\syntheticPopulation.csv");
		
		line7 = new PromptStringInformation("<html>Coordinate Reference System",
				"<html>It is recommended to provide a coordinate reference system. If you don't the will try to find a suitable one. However you cannot expect a high precision."
				+ "<br> The Coordinate Reference System should be given in the EPSG format. For example: EPSG:26918 is the CRS for our example (NAD83 / UTM zone 18N)."
				+ "<br> All spatial information (stops files, zonal information) should be provided in the lat long information."
				+ " The CRS is expected to be the projection in which the lat long information should be projected.");
		
		/*line5 = new PromptStringInformation("Path to the neighborhood file",
				"<html>.csv file -- this file should contain a list of all bus stops Id and the list of all dissemination areas that are close enough<br>"
				+ "--<br>"
				+ "The format should be:<br>"
				+ "stopId,zoneId<br>"
				+ "208, 300405<br>"
				+ "208, 300406<br>"
				+ "209, 299 889<br>"
				+ "etc</html>",
				"association\\ctrl\\geoDico500m.csv");*/
		
		
		
		line8 = new PromptStringInformation("Path to the model coefficients",
				"<html>.F12 file -- the BisonBiogeme software produces an output on the F12 format. We use this format to read parameters values<br>"
				+ "END <br>"
				+ "0 ParameterName	F	0.1	0.02<br>"
				+ "-1<br>"
				+ "here, 0.1 is the average value for the parameter, and that is what is being used. For information, 0.02 is the standard deviation, we don't use this information in our framework.</html>",
				"association\\ctrl\\myModel.F12");
		
		line9 = new PromptStringInformation("Number of batches",
				"The cost matrix used for smart card distributions to the synthetic population is essentially a scattered matrix. However the Hungarian algorithm is no coded to consider this efficiently."
				+ "Therefore there is a need to process by batch. The higher the number of batch is, the lower RAM requirement is, but the further away you are from the theoretical framework."
				+ " Try to stick with a low number of batches. For RAM consideration: if it happens that there is 10 000 people in one of your batche, you need at least 1Go of RAM. For batches with 20 000 people, you need more than 3 Go. "
				+ "In our case study, we had a population of 300 000 person, we would have required 670 Go of RAM to fully implements our methodology.");
		
		line10 = new PromptStringInformation("Output path for the smart cards with inferred attributes",
				"<html>.csv file -- this file will have a line for each smart card including the new attributes. </html>",
				"outputs\\smartcardOwners.csv");
		

		
		line15 = new PromptButton("Start infering socio-demographic attributes",
				"Be aware that this process may last for a few hours",
				d);
		
		JPanel myContent = new JPanel();
		myContent.add(header1);
		myContent.add(line0);
		myContent.add(line1);
		myContent.add(line2);
		myContent.add(line3);
		myContent.add(line4);
		myContent.add(line5);
		myContent.add(line6);
		myContent.add(line7);
		myContent.add(line8);
		myContent.add(line9);
		myContent.add(line10);
		myContent.add(line15);
		
		
		myContent.setLayout(new BoxLayout(myContent, BoxLayout.PAGE_AXIS));
		this.add(myContent, BorderLayout.SOUTH);
		
		myStringPrompts.add(line0);
		myStringPrompts.add(line1);
		myStringPrompts.add(line2);
		myStringPrompts.add(line3);
		myStringPrompts.add(line4);
		myStringPrompts.add(line5);
		myStringPrompts.add(line6);
		myStringPrompts.add(line7);
		myStringPrompts.add(line8);
		myStringPrompts.add(line9);
		myStringPrompts.add(line10);
	}
}
