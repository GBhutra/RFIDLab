package RFIDLabReaderApplication;

import java.util.regex.Pattern;

import org.llrp.ltk.generated.parameters.TagReportData;

import controlP5.Button;
import controlP5.ControlEvent;
import controlP5.ControlFont;
import controlP5.ControlP5;
import processing.core.PApplet;
import processing.core.PFont;
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
    PImage asset[];
    protected String image[] = {"StopSign.png","YieldSign","streetSign","tick.png","cross.png","map.png"};
    
    //
    
    public static int numButtons = 4;
	public String[]  Button= {"Start", "Advanced", "Save", "Exit"};
	public static Button[] control_button;
	
	private static ENUMS.SIGN currSign = ENUMS.SIGN.STOP;
	private static ENUMS.SIGN prevSign = ENUMS.SIGN.STOP;
	private int currAssetID;
	private int prevAssetID;
	private static int MAX_ASSET_ID = 51;
	
	
	
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
   	       	 		
   	       	 		if(printTagRFIDObj.getAssetID()<MAX_ASSET_ID)
   	       	 			currAssetID = printTagRFIDObj.getAssetID();
   	       	 	}
   	       	 	else	{
   	       		 
   	       	 	}
    		}
    	}
		return ENUMS.STATUS.SUCCESS;
	}
	
	public void setup()	{
		//Initializing with defaults
		currAssetID = 1;
		prevAssetID = 1;
		layout = ENUMS.LAYOUT.SIMPLIFIED;
		reader = new RFIDReader("192.168.1.50");
		fileHandler = new FileHandler("Riverside3.csv");
		reader.register(this);
		reader.register(fileHandler);
		
		loadImages();
		calculateDimensions();
		size(WIDTH,HEIGHT);
		
		
		cp5 = new ControlP5(this);
		PFont p = createFont("Arial",20,true);
		cp5.setFont(p);
		
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
		
		stroke(0);
		rect(79,89,642,402);
		rect(79,509,252,72);
		rect(469,511,252,72);
	
		int width = asset[currAssetID].width;
		int height = asset[currAssetID].height;
		
		if(width>height)	{	asset[currAssetID].resize(SIGN_WIDTH, Math.round(height*(SIGN_WIDTH/width)));	}
		else	{	asset[currAssetID].resize(Math.round(width*(SIGN_HEIGHT/height)),SIGN_HEIGHT);	}
		
		imageMode(CENTER);
		image(asset[currAssetID],400,290);
		imageMode(CORNER);
	}
	
	public void draw() {
		if(prevAssetID!=currAssetID)	{
			background(200);
			int width = asset[currAssetID].width;
			int height = asset[currAssetID].height;
		
			// Calculation to maintain Aspect ratio
			if(width>height)	{	asset[currAssetID].resize(SIGN_WIDTH, Math.round(height*(SIGN_WIDTH/width)));	}
			else	{	asset[currAssetID].resize(Math.round(width*(SIGN_HEIGHT/width)),SIGN_HEIGHT);	}
			
			imageMode(CENTER);
			try {	image(asset[currAssetID],400,290);	}
			catch (Exception e)	{
				System.out.println("ERROR loading image");
				//image(NoimageFound)
				e.printStackTrace();
			}
			
			imageMode(CORNER);
			prevAssetID = currAssetID;
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
		asset = new PImage[51];
		for (int i=0;i<51;i++)	{
			String img = i+".png";
			try	{
				asset[i] = loadImage(img);
			}
			catch (Exception e)	{
				System.out.println("Error Loading asset "+i);
				e.printStackTrace();
			}
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
