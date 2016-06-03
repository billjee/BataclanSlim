/**
 * 
 */
package SystemOutputs;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.EventQueue;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;

/**
 * @author Antoine
 *
 */
public class CapturePane extends JScrollPane implements Consumer {

    private JLabel output;
    private JPanel outputPane = new JPanel();

    public CapturePane() {
    	super();
        //setLayout(new BoxLayout(this,BoxLayout.PAGE_AXIS));
        output = new JLabel("<html>");
        
        output.setForeground(Color.WHITE);
		outputPane.setBackground(Color.BLACK);
		outputPane.add(output);
		outputPane.setLayout(new BoxLayout(outputPane, BoxLayout.PAGE_AXIS));
		
		this.setViewportView(outputPane);

    }

    @Override
    public void appendText(final String text) {
        if (EventQueue.isDispatchThread()) {
            output.setText("<html>" + text + "<br>" + output.getText());
        } else {
            EventQueue.invokeLater(new Runnable() {
                @Override
                public void run() {
                    appendText(text);
                }
            });

        }
    }        
}
