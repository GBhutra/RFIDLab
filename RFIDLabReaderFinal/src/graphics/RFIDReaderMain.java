/**
 * 
 */
package graphics;

import processing.core.PApplet;

/**
 * @author Ghanshyam
 *
 */
public class RFIDReaderMain {

	/**
	 * 
	 */
	public RFIDReaderMain() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		//
		RFIDReader reader = new RFIDReader("192.168.1.50");
		views view = new views(reader);
		PApplet.main(new String[] { "--present", "graphics.views" });
		System.out.println("This is the main file!!");	
		try{
		}
		catch(Exception e)
		{
			
		}
	}

}
