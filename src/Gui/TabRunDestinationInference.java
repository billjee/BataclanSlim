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

import org.geotools.temporal.object.Utils;

import Controlers.PromptButton;
import Controlers.PromptStringInformation;

/**
 * @author Antoine
 *
 */
public class TabRunDestinationInference extends JPanel {

	ArrayList<PromptStringInformation> myStringPrompts = new ArrayList<PromptStringInformation>();
	PromptStringInformation line1;
	PromptStringInformation line2;
	PromptStringInformation line3;
	PromptStringInformation line4;
	PromptStringInformation line5;
	PromptStringInformation line6;
	PromptStringInformation line7;
	PromptStringInformation line8;
	PromptButton line9;
	
	public TabRunDestinationInference(Dimension d){
		super();
		String GTFSadvice = "<html> public transit system can be described in a GTFS file (GTFS stand for Google Transit Feed Specification)."
				+ "They are open data and the data structure is checked and validated. We need the following file from the GTFS:"
				+ "<br/>routes.txt"
				+ "<br/>trips.txt"
				+ "<br/>stops.txt"
				+ "<br/>stop_times.txt";
			
		JLabel destinationInference = new JLabel("<html>Destination inference");
		
		line1 = new PromptStringInformation("<html>Path to the GTFS trips file",
				GTFSadvice,
				"destinationInference\\GTFS\\trips.txt");
		line2 = new PromptStringInformation("<html>Path to the GTFS stops file",
				GTFSadvice,
				"destinationInference\\GTFS\\stops.txt");
		line3 = new PromptStringInformation("<html>Path to the GTFS stop_times file",
				GTFSadvice,
				"destinationInference\\GTFS\\stop_times.txt");
		line4 = new PromptStringInformation("<html>Path to the GTFS routes file",
				GTFSadvice,
				"destinationInference\\GTFS\\routes.txt");
		line5 = new PromptStringInformation("<html>Path to the smartcard data",
				"<html>The smart card data should be related somehow to the GTFS file. Stop id, route id etc should be consistent with the GTFS description of the public transit system",
				"destinationInference\\smartcardSlim.csv");
		line6 = new PromptStringInformation("<html>Output path",
				"<html>CSV file -- path to save the smartcard file with the destinations",
				"outputs\\smartcards_with_destination.csv");
		line7 = new PromptStringInformation("<html>2 * Walkable distance threshold (in meters)",
				"<html> The walking distance threshold. Studies usually use a threshold between 2 * 500 meters and 2 * 1000 m");
		line8 = new PromptStringInformation("<html>Activity inference time threshold (in minutes)",
				"<html>If the interval between two boardings is more than the activity inference time threshold, than it is assumed that an activity took place between the two boardings ");
		line9 = new PromptButton("Infer alighting stop", 
				"<html> Infer destination for each transaction, whenever it is possible. This is done following Trépanier, Tranchant and Chaplean (2007), Individual Trip Destination Estimation in a Transit Smart Card Automated Fare Collection System.",
				d);
		
		JPanel myContent = new JPanel();
		myContent.add(destinationInference);
		myContent.add(line1);
		myContent.add(line2);
		myContent.add(line3);
		myContent.add(line4);
		myContent.add(line5);
		myContent.add(line6);
		myContent.add(line7);
		myContent.add(line8);
		myContent.add(line9);
		
		myContent.setLayout(new BoxLayout(myContent, BoxLayout.PAGE_AXIS));
		this.add(myContent, BorderLayout.SOUTH);
		
		myStringPrompts.add(line1);
		myStringPrompts.add(line2);
		myStringPrompts.add(line3);
		myStringPrompts.add(line4);
		myStringPrompts.add(line5);
		myStringPrompts.add(line6);
		myStringPrompts.add(line7);
		myStringPrompts.add(line8);
	}
	
}
