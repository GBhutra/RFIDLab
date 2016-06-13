/**
 * 
 */
package graphics;

import java.util.HashMap;
import java.util.List;

import de.fhpotsdam.unfolding.data.Feature;
import de.fhpotsdam.unfolding.marker.Marker;

/**
 * @author Ghanshyam
 *
 */
public class FileHandler {

	/**
	 * This is the File Handler class. This class reads from the file  and writes back 
	 */
	
	private String tagsEPCRead;
    private static HashMap<String, Integer> epcCount = new HashMap<String, Integer>();
    private static HashMap<String, String> assetEPC = new HashMap<String, String>();
    HashMap<String, RFIDObject> lifeExpMap;
    List<Feature> countries;
    List<Marker> countryMarkers;
    private String GPSLat, GPSLong;
    
    
	public FileHandler() {
		// TODO Auto-generated constructor stub
	}
	
	public void readRFIDObjFromFile()	{

}
