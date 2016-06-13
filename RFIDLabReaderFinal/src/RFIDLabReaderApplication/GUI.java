package RFIDLabReaderApplication;

import java.util.regex.Pattern;

import org.llrp.ltk.generated.parameters.TagReportData;

import controlP5.Button;
import controlP5.ControlEvent;
import controlP5.ControlP5;
import processing.core.PApplet;
import processing.core.PImage;

/**
 * @author Ghanshyam
 *
 */

public class GUI extends PApplet implements Observer{	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	ControlP5 cp5;
	//The following values are graphics related
    public static int WIDTH 	= 800;
    public static int HEIGHT 	= 600;
     
    private int BUTTON1_WIDTH;
    private int BUTTON1_HEIGHT;
     
    private int SIGN_WIDTH; 
    private int SIGN_HEIGHT;

    private int BUTTON2_WIDTH; 
    private int BUTTON2_HEIGHT; 

    private int MAP_WIDTH; 
    private int MAP_HEIGHT; 
    
    public static ENUMS.LAYOUT layout = ENUMS.LAYOUT.SIMPLIFIED;
    //End of Graphics constants
    
    //the graphics index
    PImage img[];
    protected String image[] = {"StopSign.png","YieldSign","streetSign","tick.png","cross.png","map.png"};
    
    //
    
    public static int numButtons = 4;
	public String[]  Button= {"Start", "Advanced", "Save", "Exit"};
	public static Button[] control_button;
	
	private static ENUMS.SIGN currSign = ENUMS.SIGN.STOP;
	private static ENUMS.SIGN prevSign = ENUMS.SIGN.STOP;
	private int currAssetID;
	private int prevAssetID;
	
	
	
	//RFID Reader object
	RFIDReader reader;
	
	//File Handler object
	FileHandler fileHandler;
	
	// From here onwards all the methods are defined.
	public GUI() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public ENUMS.STATUS update(TagReportData tag) {
		// TODO Auto-generated method stub
		System.out.println("Updating the GUI");
		String tagEPCRead = (String)tag.getEPCParameter().toString();
	   	String temp[] = tagEPCRead.split(Pattern.quote(":"));
    	
    	if(temp[2].equalsIgnoreCase(" e20021002000528314cb0272"))	{
    		System.out.println("Play sound!!");
	   	}
    	else	{
    		if(null!=fileHandler.getEPCCount(temp[2]))	{
    			String readRFIDTag =  "0x"+temp[2].trim();
    			RFIDObject printTagRFIDObj = new RFIDObject();
   	       	 	printTagRFIDObj = fileHandler.getRFIDObjectFromLifeExpMap(fileHandler.getAssetID(readRFIDTag));
   	       	 	
   	       	 	if(printTagRFIDObj != null)	{
   	       	 		System.out.println(printTagRFIDObj.getAssetID()+" # "+printTagRFIDObj.getSign()+" # "+temp[2]);
   	       	 		String tmpSign = printTagRFIDObj.getSign();
   	       	 		if("street"==tmpSign.substring(0,6))	{
   	       	 			currSign = ENUMS.SIGN.STREETSIGN;
   	       	 		}
   	       	 		else if("stop"==tmpSign.substring(0,6))	{
		       	 			currSign = ENUMS.SIGN.STOP;
		       	 	}
   	       	 		else if("yield"==tmpSign.substring(0,6))	{
	   	       	 		currSign = ENUMS.SIGN.YIELD;
	   	       	 	}
   	       	 		
   	       	 		if(printTagRFIDObj.getAssetID()!=51)
   	       	 			currAssetID = printTagRFIDObj.getAssetID();
   	       	 	}
   	       	 	else	{
   	       		 
   	       	 	}
    		}
    	}
		return ENUMS.STATUS.SUCCESS;
	}
	
	public void setup()	{
		layout = ENUMS.LAYOUT.SIMPLIFIED;
		calculateDimensions();
		size(WIDTH,HEIGHT);
		
		cp5 = new ControlP5(this);
		reader = new RFIDReader("192.168.1.50");
		fileHandler = new FileHandler("Riverside3.csv");
		reader.register(this);
		reader.register(fileHandler);
		
		
		//loadImages();
		noStroke();
		
		control_button = new Button[4];
		for (int i=1;i<=numButtons;i++)	{
			int pad = Math.round((WIDTH-numButtons*BUTTON1_WIDTH)/5);
			int x = (i*pad)+(BUTTON1_WIDTH*(i-1));
			System.out.println("Button "+i+" placed @ x="+x);
			control_button[i-1]  =	cp5.addButton(Button[i-1])
									   .setId(i)
									   .setPosition(x,20)
									   .setSize(BUTTON1_WIDTH,BUTTON1_HEIGHT);
		}
		control_button[0].setSwitch(true);
		
		stroke(255);
		rect(80,90,640,400);
		rect(80,510,250,70);
		rect(470,510,250,70);
		PImage img = loadImage("1.png");
		imageMode(CENTER);
			image(img,400,290);
		imageMode(CORNER);
	}
	
	public void draw() {
		if(layout==ENUMS.LAYOUT.SIMPLIFIED)	{
			
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
		  	case "Exit":
		  		System.out.println("Exit button clicked");
		  		System.out.println("Buh bye......");
		  		System.exit(0);
		  	break;
		  	case "Save":
		  		System.out.println("Save button clikled");
		  		if(ENUMS.STATUS.SUCCESS == fileHandler.saveLog())	{
		  			System.out.println("File saved successfully");
		  		}
		  		else	{
		  			System.out.println("ERROR Couldn't Save file");
		  		}
		  	break;
		  }
	}
	
	public static void main(String args[])	{
		System.out.println("This is the main function!! :D");
		PApplet.main(new String[] { "--present", "RFIDLabReaderApplicaiton.GUI" });
		System.out.println("RFID Program");		
	}
	
	private void layoutTransitionTo(ENUMS.LAYOUT Layout)	{
		if(Layout==ENUMS.LAYOUT.SIMPLIFIED)	{
			if(currSign==ENUMS.SIGN.STOP)	{
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
	
	public void calculateDimensions()	{
		BUTTON1_WIDTH = Math.round(150*(WIDTH/800));
		BUTTON1_HEIGHT = Math.round(50*(HEIGHT/600));
		
		if(layout == ENUMS.LAYOUT.SIMPLIFIED)	{
			SIGN_WIDTH = Math.round(640*(WIDTH/800));
			SIGN_HEIGHT = Math.round(400*(HEIGHT/600));
	
			BUTTON2_WIDTH = Math.round(250*(WIDTH/800));
			BUTTON2_HEIGHT = Math.round(70*(HEIGHT/800));
	
			MAP_WIDTH = Math.round(0);
			MAP_HEIGHT = Math.round(0);
		}
		else	{	
			SIGN_WIDTH = Math.round(370*(WIDTH/800));
			SIGN_HEIGHT = Math.round(400*(HEIGHT/600));
	
			BUTTON2_WIDTH = Math.round(170*(WIDTH/800));
			BUTTON2_HEIGHT = Math.round(70*(HEIGHT/800));
	
			MAP_WIDTH = Math.round(370*(WIDTH/800));
			MAP_HEIGHT = Math.round(225*(HEIGHT/800));
		}
	}
}
