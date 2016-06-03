/**
 * 
 */
package Gui;

import java.awt.Color;
import java.awt.Dimension;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;

/**
 * @author Antoine
 *
 */
public class PaneModelCalibration extends JScrollPane {

	JTabbedPane myTabs = new JTabbedPane();
	JPanel myContent = new JPanel();
	TabPrepareBiogemeCalibration tabPrepareBiogemeCalibration = new TabPrepareBiogemeCalibration(new Dimension(30,50));
	TabRunModelCalibration tabRunModelCalibration = new TabRunModelCalibration();
	TabRunModelValidation tabRunModelValidation = new TabRunModelValidation(new Dimension(30,50));
	
	public PaneModelCalibration(){
		super();
		this.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
	    this.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
	    //this.setViewportView(myContent);
	    this.setViewportView(myTabs);
		this.setBackground(Color.WHITE);
		
		myTabs.add("Prepare model calibration", tabPrepareBiogemeCalibration);
		myTabs.add("Run model calibration", tabRunModelCalibration);
		myTabs.add("Validation on observed data", tabRunModelValidation);
	}
}
