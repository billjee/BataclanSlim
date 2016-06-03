/**
 * 
 */
package Gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.util.ArrayList;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import Controlers.Button;
import Controlers.PromptButton;
import Controlers.PromptStringInformation;

/**
 * @author Antoine
 *
 */
public class TabPreparePopulationSynthesis extends JPanel {

	//JScrollPane myScroll = new JScrollPane();
	ArrayList<PromptStringInformation> myStringPrompts = new ArrayList<PromptStringInformation>();
	Button act1;
	Button act2;
	PromptStringInformation line1;
	PromptStringInformation line2;
	PromptStringInformation line3;
	PromptButton line4;
	PromptStringInformation line5;
	PromptButton line6;
	
	
	public TabPreparePopulationSynthesis(Dimension d){
		
		super();
		
		
		JLabel header1 = new JLabel("Prepare global conditional distributions");
		line1 = new PromptStringInformation("<html>Path to disaggregated data", 
				"<html>.csv file -- Provide the path to the disaggregated data source (travel survey, public use micro sample etc)"
				+ "<br> WARNING: for this specific case, we used the SEMI-COLON ( ;) to separate the columns instead of the more traditional COMA for specific reason related to our original case study",
				"populationSynthesis\\data\\pumfSlim.csv");
		line2 = new PromptStringInformation("<html>Path to data description", 
				"<html>.txt file -- You have to create a text file which describes each agent attributes and the number of categories for each attributes, starting from 0. For instance: <br>"
				+ "<br>age,0,1,2,3,4,5,6" + 
				"  <br>sex,0,1,,,,," + 
				"  <br>mStat,0,1,2,,,," + 
				"  <br>nPers,0,1,2,3,4,,",
				"populationSynthesis\\ctrl\\descFileSlim.txt");
		line3 = new PromptStringInformation("Path to output directory", 
				"<html>folder -- provide the path to the output directory, if you have multiple disaggregated datasource, it is recommended to provide a different folder for each of them to avoid overwriting information. <br>"
				+ "If you have if have various data sets, you will have to manually select the source for each conditional distribution by browsing the folders and copy pasting the file you want to use.",
				"populationSynthesis\\distributions\\");
		line4 = new PromptButton("Draw conditional distributions",
				"<html>Input: disaggregated data. <br>"
	    		+ "Output: conditional distributions for each attribute described in the data description file.<br>"
	    		+ "--<br>"
	    		+ "The function will create conditional distributions from disaggregated data. The output as the following format (describing for each category and each existing attribute combination): <br>"
	    		+ "p(age|gender-occupation-income-cars)<br>"
	    		+ "0|1-2-0-0, 12<br>"
	    		+ "<br>"
	    		+ "It is recommended to create a folder for each data source to avoid consecutive run of the function to erase previous results.",
	    		d);
	    
	    JLabel header2 = new JLabel("Prepare local conditional distributions");
	    line5 = new PromptStringInformation("Path to census data",
	    		"<html>.csv file -- Provide the path to aggregated data (census)<br>"
	    		+ "please note that the column delimiter used is the semi colon - ; - because in the Canadian census, the coma - , - is being used in the headers",
	    		"populationSynthesis\\data\\censusSlim.csv");
	
	    line6 = new PromptButton("Draw local conditional distributions",
	    		"<html>Input: aggregated data (census counts over age and sex). <br>"
	    	    		+ "Output: conditional distribution for each attributes available at local level (age and gender)<br>"
	    	    		+ "--<br>"
	    	    		+ "This function will create as many subfolders as there is local areas. Therefore it is recommended to dedicate a folder to this output.<br>"
	    	    		+ "--<br>"
	    	    		+ "This function was coded to create local distribution of age * gender from Canadian census data. The user should code his own census preparation method"
	    	    		+ "based on our example."
	    	    		, d);
	    

	    JPanel myContent = new JPanel();
	    myContent.add(header1);
	    myContent.add(line1);
	    myContent.add(line2);
	    myContent.add(line3);
	    myContent.add(line4);
	    myContent.add(new JPanel());
	    myContent.add(new JPanel());
	    myContent.add(header2);
	    myContent.add(line5);
	    myContent.add(line6);
	    
	    myContent.setLayout(new BoxLayout(myContent, BoxLayout.PAGE_AXIS));	    
	    this.add(myContent,BorderLayout.SOUTH);
	    
	    myStringPrompts.add(line1);
		myStringPrompts.add(line2);
		myStringPrompts.add(line3);
		myStringPrompts.add(line5);
	}
}
