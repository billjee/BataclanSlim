/**
 * 
 */
package Controlers;

/**
 * @author Antoine
 *
 */
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.IOException; 
import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JLabel;


public class Button extends JButton{
  public StringBuilder help;
  public int id;

	public Button(String str, String hlp){
	    super(str);
	    help = new StringBuilder(64);
	    help = help.append(hlp);
    }
	
	/*public Button(String str, String hlp, Dimension d){
	    super(str);
	    
	    this.setMinimumSize(d);
	    help = new StringBuilder(64);
	    help = help.append(hlp);
    }*/
	public Button(String str, String hlp, int myNewId){
	    super(str);
	    help = new StringBuilder(64);
	    help = help.append(hlp);
	    id = myNewId;
    }
}