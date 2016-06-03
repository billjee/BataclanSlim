/**
 * 
 */
package Controlers;

import javax.swing.JTextField;

/**
 * @author Antoine
 *
 */
public class TextField extends JTextField {

	public StringBuilder help;
	
	public TextField(String str, String hlp){
		super(str);
		help = new StringBuilder(64);
		help = help.append(hlp);
	}
}
