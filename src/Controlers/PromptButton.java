/**
 * 
 */
package Controlers;

import java.awt.Dimension;
import java.awt.GridLayout;

import javax.swing.JPanel;

/**
 * @author Antoine
 *
 */
public class PromptButton extends JPanel {

	public Button myButton;
	
	public PromptButton(String str, String hlp, Dimension d){
		super();
		this.setLayout(new GridLayout(1,1));
		myButton = new Button(str,hlp);
		myButton.setPreferredSize(d);
		this.add(myButton);
	}
	
	public PromptButton(String str, String hlp, Dimension d, int myNewId){
		super();
		this.setLayout(new GridLayout(1,1));
		myButton = new Button(str,hlp,myNewId);
		myButton.setPreferredSize(d);
		this.add(myButton);
	}
}
