/**
 * 
 */
package Controlers;

import java.util.ArrayList;

import javax.swing.JComboBox;

/**
 * @author Antoine
 *
 */
public class ComboBox extends JComboBox{

	public StringBuilder help;
	
	public ComboBox(String hlp, ArrayList<Integer> threads){
		super(threads.toArray());
		help = new StringBuilder(64);
		help = help.append(hlp);		
	}
}
