/**
 * 
 */
package Gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.ArrayList;

import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import Controlers.PromptButton;
import Controlers.PromptComboBox;
import Controlers.PromptStringInformation;

/**
 * @author Antoine
 *
 */
public class TabRunModelValidation extends JPanel {
	
	ArrayList<PromptStringInformation> myStringPrompts = new ArrayList<PromptStringInformation>();
	PromptStringInformation line1;
	PromptStringInformation line2;
	PromptStringInformation line3;
	PromptStringInformation line4;
	PromptComboBox line5;
	PromptStringInformation line6;
	PromptButton line7;

	public TabRunModelValidation(Dimension d){
		super();
		JLabel header1 = new JLabel("Apply the model on the data used for its calibration");
		line1 = new PromptStringInformation("Path to the travel survey",
				"<html>.csv file -- path to the travel survey used for the model calibration. It is recommanded to calibrate the model over 80% of the data available and to apply to model on the 20% remaining.</html>",
				"\\modelCalibration\\data\\travelSurvey_prepared.csv");
		
		line2 = new PromptStringInformation("Path to the choice description file", 
				"<html> .txt file -- this file describes choices attribute following this format: <br>"
				+ "choice, number of discrete alternatives<br>"
				+ "--<br>"
				+ "For example: <br>"
				+ "FIDEL_PT_RANGE, 3<br>" + 
				"FIRST_DEPShort, 3<br>" + 
				"LAST_DEPShort, 3<br>" + 
				"N_ACT, 4<br>" + 
				"NEST, 5</html>",
				"\\modelCalibration\\ctrl\\choiceDescription.txt");
		
		line3 = new PromptStringInformation("Path to hypothesis description file",
				"<html> .txt file -- the file describes hypothesis following this format:<br>"
				+ "DOWNTOWN, accessIndicator = 1, NEST = 2 - 3, dummy <br>"
				+ "where DOWNTOWN: is the hypothesis name, <br>"
				+ "accesIndicator: is the affecting attribute name (this name should be spelled the same way than in the travel survey <br>"
				+ "= 1-2 :are the affecting categories(1 and 2). For a continuous affeting category, just leave =<br>"
				+ "NEST: is the affected choice<br>"
				+ "=2-3: are the affected categories (2 and 3)<br>"
				+ "dummy: is the type of variable (choose: dummy, agent, alternative",
				"\\modelCalibration\\ctrl\\hypothesis.txt");
		
		line4 = new PromptStringInformation("Path to calibrated model file",
				"<html>.F12 file -- the calibrated model should be formatted as a .F12 file (such as those produced by BisonBiogeme. The format is as follows:<br>"
				+ "END<br>"
				+ "0	ACCESS_TO_STO	F	-1.54576	0.146511<br>" + 
				"1	BACK_FROM_SCHOOL	F	0.134444	0.0187833<br>" + 
				"2	BIG_FAMILY_1ST_DEP_PEAK	F	0.065637	0.0140151 <br>"
				+ "-1<br>"
				+ "--<br>"
				+ "first column is an identifier, it is not used<br>"
				+ "second column is the parameter name, it should be consistent with hypothesis description<br>"
				+ "third column is not used<br>"
				+ "fourth column is the parameter value used for model application<br>"
				+ "fifth column is the standard deviation of the parameter value, it is not used <br>"
				+ "the description start with the lavel END and finishes with the lavel -1<br>"
				+ "WARNING: if you are using the .F12 file produced by BisonBiogeme, be aware that it is not reporting fixed parameter, you have to add them manualy",
				"modelCalibration\\ctrl\\myModel.F12");
		
		ArrayList<Integer> alt = new ArrayList<Integer>();
		for(int i = 1; i <= 3; i++){alt.add(i);}
		line5 = new PromptComboBox("Choice set type", 
				"<html>Option 1: use the choice set universe<br>"
				+ "Option 2: use all alternative computed earlier when prepareing the data. Careful: with this methods, a single choice can be present more than once, which you probably don't want."
				+ "Option 3: use unique occurancies if choices amongst alternative computed earlier. It is a combination of the two other options. It reduces the choice set.",
				alt);
		
		line6 = new PromptStringInformation("Output path",
				"<html>.csv file -- the ouput will have simulated choice and observed choice so the user can draw confusion matrix. It also includes socio-demographic attributes to perform cross-sectionnal analysis of the model.</html>",
				"outputs\\travelSurveySimulatedMobility.csv");
		
		line7 = new PromptButton("Apply the model on the observed data",
				"<html> It is recommended to run the model on observed data to analyse whether the output is correctly predicted or not. </html>",
				d);
		
		JPanel myContent = new JPanel();
	    myContent.add(header1);
	    myContent.add(line1);
	    myContent.add(line2);
	    myContent.add(line3);
	    myContent.add(line4);
	    myContent.add(line5);
	    myContent.add(line6);
	    myContent.add(line7);
	    
	    myContent.setLayout(new BoxLayout(myContent, BoxLayout.PAGE_AXIS));
	    this.add(myContent,BorderLayout.SOUTH);
	    
	    myStringPrompts.add(line1);
		myStringPrompts.add(line2);
		myStringPrompts.add(line3);
		myStringPrompts.add(line4);
		myStringPrompts.add(line6);
	}
}
