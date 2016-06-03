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
public class TabPrepareBiogemeCalibration extends JPanel {
	
	ArrayList<PromptStringInformation> myStringPrompts = new ArrayList<PromptStringInformation>();
	PromptStringInformation line1;
	PromptStringInformation line2;
	PromptStringInformation line3;
	PromptButton line4;
	PromptStringInformation line5;
	PromptStringInformation line6;
	PromptStringInformation line7;
	PromptComboBox line8;
	PromptStringInformation line9;
	PromptStringInformation line10;
	PromptButton line11;

	public TabPrepareBiogemeCalibration(Dimension d){
		super();
		
		JLabel prepareBiogemeControlFile = new JLabel("Prepare a control file for BisonBiogeme");
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
				"modelCalibration\\ctrl\\choiceDescription.txt");
		line2 = new PromptStringInformation("Path to hypothesis description file",
				"<html> .txt file -- the file describes hypothesis following this format:<br>"
				+ "DOWNTOWN, accessIndicator = 1, NEST = 2 - 3, dummy <br>"
				+ "where DOWNTOWN: is the hypothesis name, <br>"
				+ "accesIndicator: is the affecting attribute name (this name should be spelled the same way than in the travel survey <br>"
				+ "= 1-2 :are the affecting categories(1 and 2). For a continuous affeting category, just leave =<br>"
				+ "NEST: is the affected choice<br>"
				+ "=2-3: are the affected categories (2 and 3)<br>"
				+ "dummy: is the type of variable (choose: dummy, agent, alternative",
				"modelCalibration\\ctrl\\hypothesis.txt");
		line3 = new PromptStringInformation("Output path to save the BisonBiogeme control file", 
				"<html>Path to save a BisonBiogeme control file matching choice description and hypothesis. <br>"
				+ "Beware that if you make hand changes in this control file, those changes won't be considered in later use in the Bataclan framework.</html>",
				"outputs\\BiogemeCtrlFile.mod");
		line4 = new PromptButton("Generate BisonBiogeme control file",
				"<html>Generate a BisonBiogeme control file matching choice description and hypothesis. <br>"
				+ "Beware that if you make hand changes in this control file, those changes won't be considered in later use in the Bataclan framework.</html>",
				d);
		
		JLabel prepareTravelSurvey = new JLabel("Format travel survey so choice index match the software dictionnary");
		line5 = new PromptStringInformation("Path to the choice description file", 
				"<html> .txt file -- this file describes choice attributes following this format: <br>"
				+ "choice, number of discrete alternatives<br>"
				+ "--<br>"
				+ "For example: <br>"
				+ "FIDEL_PT_RANGE, 3<br>" + 
				"FIRST_DEPShort, 3<br>" + 
				"LAST_DEPShort, 3<br>" + 
				"N_ACT, 4<br>" + 
				"NEST, 5</html>",
				"modelCalibration\\ctrl\\choiceDescription.txt");
		line6 = new PromptStringInformation("Path to hypothesis description file",
				"<html> .txt file -- the file describes hypothesis following this format:<br>"
				+ "DOWNTOWN, accessIndicator = 1, NEST = 2 - 3, dummy <br>"
				+ "where DOWNTOWN: is the hypothesis name, <br>"
				+ "accesIndicator: is the affecting attribute name (this name should be spelled the same way than in the travel survey <br>"
				+ "= 1-2 :are the affecting categories(1 and 2). For a continuous affeting category, just leave =<br>"
				+ "NEST: is the affected choice<br>"
				+ "=2-3: are the affected categories (2 and 3)<br>"
				+ "dummy: is the type of variable (choose: dummy, agent, alternative",
				"modelCalibration\\ctrl\\hypothesis.txt");
		
		line7 = new PromptStringInformation("Path to the travel survey", 
				"<html>.csv file -- path to the travel survey to prepare: each sample will be labeled with the corresponding choice.<br>"
				+ "It is recommended that the user write down his own function to prepare the travel survey, because each travel survey has its own specificities. </html>",
				"modelCalibration\\data\\travelSurvey.csv");
		
		ArrayList<Integer> threads =  new ArrayList<Integer>();
		for(int i = 1; i <= Runtime.getRuntime().availableProcessors()-1; i++){threads.add(i);}
		JComboBox threadChoice = new JComboBox(threads.toArray());
		line8 = new PromptComboBox("Number of threads", 
				"<html>Number -- it allows the software to work using multithreading. This is interesting to reduce computation time.",
				threads);
		
		line9 = new PromptStringInformation("Number of close alternatives to consider",
				"<html>number -- the N trip chain spatially based the closest to the living location of the agent are sampled as the choice set<br>"
				+ "when simulating the trip chain choice, you will have the choice between accessing the whole choice universe or only accessing trip chain types that occured in the neighborhood");
		
		line10 = new PromptStringInformation("Output path",
				"<html>path -- the path to save the prepared travel survey",
				"outputs\\travelSurvey_prepared.csv");
		
		line11 = new PromptButton("Prepare the travel survey for BisonBiogeme",
				"<html>Generate a BisonBiogeme control file matching choice description and hypothesis. <br>"
				+ "Beware that if you make hand changes in this control file, those changes won't be considered in later use in the Bataclan framework.</html>",
				d);
		
		JPanel myContent = new JPanel();
		myContent.add(prepareBiogemeControlFile);
		myContent.add(line1);
		myContent.add(line2);
		myContent.add(line3);
		myContent.add(line4);
		myContent.add(prepareTravelSurvey);
		myContent.add(line5);
		myContent.add(line6);
		myContent.add(line7);
		myContent.add(line8);
		myContent.add(line9);
		myContent.add(line10);
		myContent.add(line11);
		
		myContent.setLayout(new BoxLayout(myContent, BoxLayout.PAGE_AXIS));	    
		this.add(myContent, BorderLayout.SOUTH);
		
		myStringPrompts.add(line1);
		myStringPrompts.add(line2);
		myStringPrompts.add(line3);
		myStringPrompts.add(line5);
		myStringPrompts.add(line6);
		myStringPrompts.add(line7);
		myStringPrompts.add(line9);
		myStringPrompts.add(line10);
	}
}
