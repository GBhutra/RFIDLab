package graphics;
import processing.core.*;
import controlP5.*;

public class View extends PApplet{
	
	/*
	 * The following section has the declarations used in the following functions
	 */
	// Graphics Layout options 
    private enum ENUM_LAYOUT {
        SIMPLIFIED, ADVANCED 
    }
    private enum ENUM_SIGN {
        STOP, YIELD, STREETSIGN;
    }
	
	ControlP5 cp5;
	//The following values are graphics related
    public static int WIDTH 	= 800;
    public static int HEIGHT 	= 600;
    
    public static int BUTTON1_WIDTH = Math.round(150*(WIDTH/800));
    public static int BUTTON1_HEIGHT = Math.round(50*(HEIGHT/600));
    
    public static int SIGN_WIDTH_SIMPLIFIED = Math.round(640*(WIDTH/800));
    public static int SIGN_HEIGHT_SIMPLIFIED = Math.round(400*(HEIGHT/600));
    
    public static int SIGN_WIDTH_ADV = Math.round(370*(WIDTH/800));
    public static int SIGN_HEIGHT_ADV = Math.round(400*(HEIGHT/600));
    
    public static int BUTTON2_WIDTH = Math.round(250*(WIDTH/800));
    public static int BUTTON2_HEIGHT = Math.round(70*(HEIGHT/800));
    
    public static int MAP_WIDTH = Math.round(370*(WIDTH/800));
    public static int MAP_HEIGHT = Math.round(225*(HEIGHT/800));
    
    public static ENUM_LAYOUT LAYOUT = ENUM_LAYOUT.SIMPLIFIED;
    //End of Graphics constants
    
    //the graphics index
    PImage img[];
    protected String image[] = {"StopSign.png","YieldSign","streetSign","tick.png","cross.png","map.png"};
    
    //
    
    public static int numButtons = 4;
	public String[]  Button= {"Start", "Advanced", "Save", "Exit"};
	public static Button[] control_button;
	
	private static ENUM_SIGN currSign = ENUM_SIGN.STOP;
	private static ENUM_SIGN prevSign = ENUM_SIGN.STOP;
	
	
	//Declarations related to the RFID Reader
	RFIDReader reader;
	String IpAddress = "192.168.1.50";
	//End of Declarations for RFID reader
	
	// From here onwards all the methods are defined.
	public views(RFIDReader read)	{
		System.out.println("This is the constructor of the views");
		reader = read;
	}
	public void setup()	{
		size(WIDTH,HEIGHT);
		cp5 = new ControlP5(this);
		//reader = new RFIDReader(IpAddress);
		
		//loadImages();
		noStroke();
		
		control_button = new Button[4];
		for (int i=1;i<=numButtons;i++)	{
			int pad = Math.round((WIDTH-numButtons*BUTTON1_WIDTH)/5);
			int x = (i*pad)+(BUTTON1_WIDTH*(i-1));
			System.out.println("Button "+i+" placed @ x="+x);
			control_button[i-1]  =	cp5.addButton(Button[i-1])
									   .setPosition(x,20)
									   .setSize(BUTTON1_WIDTH,BUTTON1_HEIGHT);
		}
		control_button[0].setSwitch(true);
		
		stroke(255);
		
			
	}
	
	public void draw() {
		if(LAYOUT==ENUM_LAYOUT.SIMPLIFIED)	{
			
		}
		else {
		}
	}
	
	public void controlEvent(ControlEvent theEvent) {
		  System.out.println("Button clicked");
		  switch(theEvent.getController().getName())	{
		  	case "Start":
		  		if("Start"==theEvent.getController().getLabel())	{
					  theEvent.getController().setLabel("Stop");
					  System.out.println("Starting the reader");
					  reader.startReader();
				}	else	{
					  System.out.println("Stopping the reader");
					  theEvent.getController().setLabel("Start");
					  reader.stopReader();
				}
		  	break;
		  	case "Advanced":
		  		if("Advanced"==theEvent.getController().getLabel())	{
		  			  System.out.println("Changing the layout from simplified -> advanced");
					  theEvent.getController().setLabel("Simplified");
				}	else	{
					  System.out.println("Changing the layout from advanced -> simplified");
					  theEvent.getController().setLabel("Advanced");
				}
		  	break;
		  }
	}
	
	public static void main(String args[])	{
		System.out.println("This is the main function!! :D");
		PApplet.main(new String[] { "--present", "tryakash.RFIDHighWayProgram" });
		System.out.println("RFID Program");		
	}
	
	private void layoutTransitionTo(ENUM_LAYOUT layout)	{
		if(layout==ENUM_LAYOUT.SIMPLIFIED)	{
			if(currSign==ENUM_SIGN.STOP)	{
				//image(img[0],80,90,
			}
		}
		
	}
	
	public void loadImages()	{
		img = new PImage[5];
		for (int i=0;i<5;i++)	{
			img[i] = loadImage(image[i]);
		}
		
	}
}


/*
 * 
//The following values are graphics related
public static int WIDTH 	= 800;
public static int HEIGHT 	= 600;

public static int BUTTON1_WIDTH = Math.round(150*(WIDTH/800));
public static int BUTTON1_HEIGHT = Math.round(50*(HEIGHT/600));

public static int SIGN_WIDTH_SIMPLIFIED = Math.round(640*(WIDTH/800));
public static int SIGN_HEIGHT_SIMPLIFIED = Math.round(400*(HEIGHT/600));

public static int SIGN_WIDTH_ADV = Math.round(370*(WIDTH/800));
public static int SIGN_HEIGHT_ADV = Math.round(400*(HEIGHT/600));


public static int BUTTON2_WIDTH = Math.round(250*(WIDTH/800));
public static int BUTTON2_HEIGHT = Math.round(70*(HEIGHT/800));

public static int MAP_WIDTH = Math.round(370*(WIDTH/800));
public static int MAP_HEIGHT = Math.round(225*(HEIGHT/800));

//End of Graphics constants

// Graphics Layout options 
public enum ENUM_LAYOUT {
    SIMPLIFIED, ADVANCED 
}



imageMode(CENTER);
if(signValue.equalsIgnoreCase("street"))	{
	if (LAYOUT==ENUM_LAYOUT.SIMPLIFIED)	{
		if (signCurrent.width > signCurrent.height)	{
			image(signCurrent, 400,290, SIGN_WIDTH_SIMPLIFIED, signCurrent.height*(SIGN_WIDTH_SIMPLIFIED/signCurrent.width) );
		}
		else {
			image(signCurrent, 400,290, signCurrent.width*(SIGN_HEIGHT_SIMPLIFIED/signCurrent.height),SIGN_HEIGHT_SIMPLIFIED );
		}
	}	else {
		if (signCurrent.width > signCurrent.height)	{
			image(signCurrent, 185,290, SIGN_WIDTH_ADV, signCurrent.height*(SIGN_WIDTH_SIMPLIFIED/signCurrent.width) );
		}
		else {
			image(signCurrent, 400,290, signCurrent.width*(SIGN_HEIGHT_SIMPLIFIED/signCurrent.height),SIGN_HEIGHT_SIMPLIFIED );
		}
	}
}	else{
	image(signCurrent, 850,100, 170, 170 );
	
}
imageMode(CORNER); 
*/
