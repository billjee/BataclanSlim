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
public class PaneSocioDemographicInference extends JScrollPane{

	JTabbedPane myTabs = new JTabbedPane();
	JPanel myContent = new JPanel();
	
	TabRunSocioDemographicInference tabRunSocioDemographicInference = new TabRunSocioDemographicInference(new Dimension(30,50));
	
	public PaneSocioDemographicInference(){
		super();
		this.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
	    this.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
	    //this.setViewportView(myContent);
	    this.setViewportView(myTabs);
		this.setBackground(Color.WHITE);
		
		myTabs.add("Infer socio demographic attributes", tabRunSocioDemographicInference);
	}
}
