/**
 * 
 */
package Gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;

import Controlers.Button;
import Controlers.PromptButton;

/**
 * @author Antoine
 *
 */
public class PaneButtons extends JPanel {
	Dimension d;
	JPanel myContent = new JPanel();
	PromptButton buttonGeneral;
	PromptButton buttonPopulationSynthesis;
	PromptButton buttonModelCalibration;
	PromptButton buttonDestinationInference;
	PromptButton buttonSocioDemoInference;
    PromptButton buttonShowMap ;
    
    
	public PaneButtons(){
		d = new Dimension(200, 50);
		buttonGeneral = new PromptButton("General",
				"<html> A few words of introduction and setting up the project",
				d,
				0);
		buttonPopulationSynthesis = new PromptButton("Population Synthesis", 
				"<html>Methods to prepare datasets for the population synthesis (creating both local and global conditionals) and methods to perform a MCMC based population synthesis, according to Gibbs Sampling method described in Farooq et al. (2013)</html>",
				d,
				1);
		buttonModelCalibration = new PromptButton("Model calibration", 
				"<html> Methods to generate a control file for nested joint models for BisonBiogeme, to prepare the travel survey for the calibration/ </html> ",
				d,
				2);
		buttonDestinationInference = new PromptButton("Destination inference",
				"<html> Methods to infer alighting bus stops and analyse the goodness of this methods applied to your data </html>",
				d,
				3);
		buttonSocioDemoInference = new PromptButton("Socio-demographic inference",
				"<html> Methods to attach socio demographic attributes to smart cards based on the three following hypotheses:<br>"
				+ "(A) people live near their daily most frequent bus stops<br>"
				+ "(B) people socio-demographic attributes have a string influence on their mobility choices<br>"
				+ "(C) the applied fare allow us to infer some socio-demographic information</html>",
				d,
				4);
		buttonShowMap = new PromptButton("Show map",
	    		"this is to show the map",
	    		d,
	    		5);
		
	    myContent.setLayout(new BoxLayout(myContent, BoxLayout.PAGE_AXIS));
	    myContent.add(buttonGeneral);
	    myContent.add(buttonPopulationSynthesis);
	    myContent.add(buttonModelCalibration);
	    myContent.add(buttonDestinationInference);
	    myContent.add(buttonSocioDemoInference);
	    myContent.add(buttonShowMap);
	    this.add(myContent,BorderLayout.SOUTH);
	}
}
