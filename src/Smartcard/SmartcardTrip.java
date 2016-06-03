/**
 * 
 */
package Smartcard;

import java.util.HashMap;

import org.joda.time.Chronology;
import org.joda.time.DateTime;
import org.joda.time.DateTimeFieldType;
import org.joda.time.DateTimeZone;
import org.joda.time.Instant;
import org.joda.time.ReadableInstant;

/**
 * The passenger trip class represent the trip as observed through smart card
 * data. It has an observed boarding and it should be processed to determine the
 * alighting.
 * 
 * @author Antoine
 *
 */
public class SmartcardTrip implements Comparable<SmartcardTrip>{

	int myId;
	int fare;
	GTFSStop boardingStop;
	GTFSRoute boardingRoute;
	String boardingDirection;
	DateTime boardingDate;
	GTFSStop alightingStop = null;
	int alightingInferrenceCase;

	HashMap<String, String> myData = new HashMap<String, String>();

	public SmartcardTrip() {

	}

	@Override
	public int compareTo(SmartcardTrip o) {
		// TODO Auto-generated method stub
		return boardingDate.compareTo(o.boardingDate);
	}

}
