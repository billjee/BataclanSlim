/**
 * 
 */
package Gui;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * @author Antoine
 *
 */
public class TabRunModelCalibration extends JPanel {

	public TabRunModelCalibration(){
		super();
		JLabel warning = new JLabel("<html>This part was not coded in our framework. We estimated the model using BisonBiogeme Software.<br>"
				+ "Further development should include model calibration based on Python Biogeme and/or R software.");
		
		JPanel myContent = new JPanel();
		myContent.add(warning);
		myContent.setLayout(new BoxLayout(myContent, BoxLayout.PAGE_AXIS));
		this.add(myContent);
	}
}
