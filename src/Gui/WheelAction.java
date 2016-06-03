/**
 * 
 */
package Gui;

import java.awt.event.ActionEvent;

import javax.swing.ActionMap;

import org.geotools.swing.MapPane;
import org.geotools.swing.action.ZoomInAction;
import org.geotools.swing.tool.ZoomInTool;

/**
 * @author Antoine
 *
 */
public class WheelAction extends ZoomInAction {

	public WheelAction(MapPane mapPane){
		super(mapPane);
	}
	
	public void actionPerformed(ActionEvent ev) {
        getMapPane().setCursorTool(new WheelTool());
    }
}
