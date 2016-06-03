/**
 * 
 */
package Gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.io.PrintStream;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;

import SystemOutputs.CapturePane;
import SystemOutputs.StreamCapturer;

/**
 * @author Antoine
 *
 */
public class InformationPane extends JPanel {

	JPanel helpPane = new JPanel();
	JLabel helpText = new JLabel(("<html><br><br><br><br><br><br><br><br><br><br><br><br><br><br><br><br><br><br><br><br>"
			+ "<br><br><br><br><br><br><br><br><br><br><br><br></html>"));
	
	CapturePane capturePane = new CapturePane();
	
	public InformationPane(){
		
		System.setOut(new PrintStream(new StreamCapturer("STDOUT", capturePane, System.out)));
		System.out.println("Output test");
		
		helpPane.setBackground(Color.decode("#FFFFCC"));
		helpPane.add(helpText);
		helpPane.setLayout(new BoxLayout(helpPane, BoxLayout.PAGE_AXIS));
	    
	    JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT, helpPane, capturePane);
	    this.add(split);
	    this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
	}
}
