/**
 * 
 */
package Controlers;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.util.ArrayList;

import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * @author Antoine
 *
 */
public class PromptComboBox extends JPanel{

	public ComboBox myComboBox;
	public PromptComboBox(String prompt, String hlp, ArrayList<Integer> threads){
		myComboBox = new ComboBox(hlp, threads);
		this.setLayout(new GridLayout(2,1));
		JLabel label = new JLabel(prompt);
	    this.add(label);
		
		this.add(myComboBox);
	}
}
