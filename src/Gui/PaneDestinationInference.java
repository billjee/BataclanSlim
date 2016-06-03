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
public class PaneDestinationInference extends JScrollPane {

	JTabbedPane myTabs = new JTabbedPane();
	JPanel myContent = new JPanel();
	
	TabRunDestinationInference tabRunDestinationInference = new TabRunDestinationInference(new Dimension(30,50));
	TabAnalyzeDestinationInference tabAnalyzeDestinationInference = new TabAnalyzeDestinationInference();
	
	public PaneDestinationInference(){
		super();
		this.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
	    this.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
	    //this.setViewportView(myContent);
	    this.setViewportView(myTabs);
		this.setBackground(Color.WHITE);
		
		myTabs.add("Infer alighting stops", tabRunDestinationInference);
		myTabs.add("Analyze inferred alighting stops ", tabAnalyzeDestinationInference);
	}
}
