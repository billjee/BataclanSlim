/**
 * 
 */
package Gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;

/**
 * @author Antoine
 *
 */
public class PanePopulationSynthesis extends JScrollPane{

	JTabbedPane myTabs = new JTabbedPane();
	JPanel myContent = new JPanel();
	TabPreparePopulationSynthesis tabPreparePopulationSynthesis = new TabPreparePopulationSynthesis(new Dimension(30,50));
    TabRunPopulationSynthesis tabRunPopulationSynthesis = new TabRunPopulationSynthesis();
	public PanePopulationSynthesis(){
		super();
	    this.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
	    this.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
	    //this.setViewportView(myContent);
	    this.setViewportView(myTabs);
		this.setBackground(Color.WHITE);
	    
	    myTabs.add("Data preparation", tabPreparePopulationSynthesis);
	    myTabs.add("Population synthesizer", tabRunPopulationSynthesis);
	    

	}
}
