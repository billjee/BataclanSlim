/**
 * 
 */
package Gui;

import java.awt.Cursor;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.geom.Point2D;

import javax.swing.ImageIcon;
import javax.swing.JComponent;

import org.geotools.geometry.DirectPosition2D;
import org.geotools.geometry.Envelope2D;
import org.geotools.swing.event.MapMouseEvent;
import org.geotools.swing.locale.LocaleUtils;
import org.geotools.swing.tool.AbstractZoomTool;
import org.geotools.swing.tool.CursorTool;
import org.geotools.swing.tool.ZoomInTool;

import com.vividsolutions.jts.geom.Envelope;

/**
 * @author Antoine
 *
 */
public class WheelTool extends AbstractZoomTool {

	/** Tool name */
    public static final String TOOL_NAME = LocaleUtils.getValue("CursorTool", "ZoomIn");
    
    /** Tool tip text */
    public static final String TOOL_TIP = LocaleUtils.getValue("CursorTool", "ZoomInTooltip");
    
    /** Cursor */
    public static final String CURSOR_IMAGE = "/org/geotools/swing/icons/mActionZoomIn.png";
    
    /** Cursor hotspot coordinates */
    public static final Point CURSOR_HOTSPOT = new Point(14, 9);
    
    /** Icon for the control */
    public static final String ICON_IMAGE = "/org/geotools/swing/icons/mActionZoomIn.png";
    
    private Cursor cursor;
    
    private final Point startPosDevice;
    private final Point2D startPosWorld;
    private boolean dragged;
    
	public WheelTool(){
		Toolkit tk = Toolkit.getDefaultToolkit();
        ImageIcon imgIcon = new ImageIcon(getClass().getResource(CURSOR_IMAGE));
        cursor = tk.createCustomCursor(imgIcon.getImage(), CURSOR_HOTSPOT, TOOL_NAME);
        
        startPosDevice = new Point();
        startPosWorld = new DirectPosition2D();
        dragged = false;
	}
	
	 /**
     * Get the mouse cursor for this tool
     */
    @Override
    public Cursor getCursor() {
        return cursor;
    }
    
    @Override
	public void onMouseWheelMoved(MapMouseEvent ev) {
		// TODO Auto-generated method stub
		// TODO Auto-generated method stub
		int clicks = ev.getWheelAmount();
		System.out.println(clicks);
	   // -ve means wheel moved up, +ve means down
	    int sign = (clicks < 0 ? -1 : 1);

	    Envelope env = getMapPane().getDisplayArea();
	    double width = env.getWidth();
	    double scale = width * zoom * sign;
	   
	    Rectangle paneArea = ((JComponent) getMapPane()).getVisibleRect();
        DirectPosition2D mapPos = ev.getWorldPos();


         DirectPosition2D corner = new DirectPosition2D(
                mapPos.getX() - 0.5d * paneArea.getWidth() / scale,
                mapPos.getY() + 0.5d * paneArea.getHeight() / scale);
        
         Envelope2D newMapArea = new Envelope2D();
         newMapArea.setFrameFromCenter(mapPos, corner);
         getMapPane().setDisplayArea(newMapArea);

	}
}
