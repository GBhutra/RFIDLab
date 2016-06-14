package RFIDLabReaderApplication;

import java.util.Iterator;
import java.util.Map;
import java.util.regex.Pattern;

import org.llrp.ltk.generated.parameters.TagReportData;

import controlP5.Button;
import controlP5.ControlEvent;
import controlP5.ControlP5;
import de.fhpotsdam.unfolding.UnfoldingMap;
import de.fhpotsdam.unfolding.geo.Location;
import de.fhpotsdam.unfolding.marker.Marker;
import de.fhpotsdam.unfolding.marker.MarkerManager;
import de.fhpotsdam.unfolding.marker.SimplePointMarker;
import de.fhpotsdam.unfolding.providers.AbstractMapProvider;
import de.fhpotsdam.unfolding.providers.Google;
import de.fhpotsdam.unfolding.providers.MBTilesMapProvider;
import de.fhpotsdam.unfolding.utils.MapUtils;
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
    private int SIGN_CENTER_X;
    private int SIGN_CENTER_Y;

    private int BUTTON2_WIDTH; 
    private int BUTTON2_HEIGHT; 

    private int MAP_WIDTH; 
    private int MAP_HEIGHT; 
    
    public static ENUMS.LAYOUT layout = ENUMS.LAYOUT.SIMPLIFIED;
    //End of Graphics constants
    
    private RFIDObject newRFIDObjectRead = null;
    
    //the graphics index
    protected PImage asset[]=null;
    protected PImage asset_map_bg;
    protected PImage asset_text_bg;
    protected PImage asset_check;
    protected PImage asset_cross;
    protected PImage asset_logo;
    protected PImage asset_error;
    
    
    public static int numButtons = 4;
	public String[]  Button= {"Start", "Advanced", "Save", "Exit"};
	public static Button[] control_button;
	
	private static ENUMS.SIGN currSign = ENUMS.SIGN.STOP;
	private static ENUMS.SIGN prevSign = ENUMS.SIGN.STOP;
	private int currAssetID;
	private int prevAssetID;
	private static int MAX_ASSET_ID = 51;
	
	//Map object
	protected UnfoldingMap map;
	protected MarkerManager<Marker> markerManager;
	
	
	//RFID Reader object
	protected RFIDReader reader;
	
	//File Handler object
	protected FileHandler fileHandler;
	
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
   	       	 		newRFIDObjectRead = printTagRFIDObj;
   	       	 		if("street"==tmpSign.substring(0,6))	{
   	       	 			prevSign = currSign;
   	       	 			currSign = ENUMS.SIGN.STREETSIGN;
   	       	 		}
   	       	 		else if("stop"==tmpSign.substring(0,6))	{
   	       	 			prevSign = currSign;
		       	 		currSign = ENUMS.SIGN.STOP;
		       	 	}
   	       	 		else if("yield"==tmpSign.substring(0,6))	{
   	       	 			prevSign = currSign;
	   	       	 		currSign = ENUMS.SIGN.YIELD;
	   	       	 	}
   	       	 		if(printTagRFIDObj.getAssetID()<MAX_ASSET_ID)
   	       	 			currAssetID = printTagRFIDObj.getAssetID();
   	       	 	}
   	       	 	else	{
   	       	 		System.out.println("Error No Asset Available for the TagID:"+readRFIDTag);
   	       	 		return ENUMS.STATUS.FAILURE;
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
		
		layoutTransitionTo(layout);
		size(WIDTH,HEIGHT,P3D);
		
		// Top button bar
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
		// End of Button setup
		
		System.out.println("Top Buttons Loaded");
		
		//Map setup
		
		AbstractMapProvider provider = new Google.GoogleMapProvider();
		int zoomLevel = 15;
		boolean offline = false;
		String mbTilesString = "blankLight-1-3.mbtiles";
		
		if (offline) {
			// If you are working offline, you need to use this provider 
			// to work with the maps that are local on your computer.  
			provider = new MBTilesMapProvider(mbTilesString);
			// 3 is the maximum zoom level for working offline 
		}
		map = new UnfoldingMap(this, 410, 90, MAP_WIDTH, MAP_HEIGHT, provider);
		MapUtils.createDefaultEventDispatcher(this, map);
		Location loc; 
        SimplePointMarker pointMarker;
        String key;
        Iterator<Map.Entry<String, RFIDObject>> i = fileHandler.lifeExpMap.entrySet().iterator();
        
        while(i.hasNext()){
            key = i.next().getKey();
            System.out.println("Asset:"+key+", loc: "+fileHandler.lifeExpMap.get(key).getX()+","+fileHandler.lifeExpMap.get(key).getY()+" ,EPC:"+(String) fileHandler.lifeExpMap.get(key).getEpcTag()+" ,Sign:"+(String)fileHandler.lifeExpMap.get(key).getSign());
            
            //List Box
            //if(layout==ENUMS.LAYOUT.SIMPLIFIED)
            //	lbCSV.addItem( padRight(key.toString(), 5) +padRight((String) lifeExpMap.get(key).epcTag ,45)+padRight((String) lifeExpMap.get(key).sign ,55), Integer.parseInt(key));
         
           loc = new Location(fileHandler.lifeExpMap.get(key).getX(), fileHandler.lifeExpMap.get(key).getY());
           pointMarker = new SimplePointMarker(loc);
           String tmpSign = (String)fileHandler.lifeExpMap.get(key).getSign();
            tmpSign = tmpSign.substring(0,4);
          
            //marker colors
            if(tmpSign.equalsIgnoreCase("stop"))	{
            	pointMarker.setColor(color(255, 50, 50, 100));
            	map.addMarker(pointMarker);
            }
            else if (tmpSign.equalsIgnoreCase("yield"))	{
            	pointMarker.setColor(color(50, 255, 50, 100));
            	map.addMarker(pointMarker);
            }
            else if (tmpSign.equalsIgnoreCase("streetsign"))
        	{
            	pointMarker.setColor(color(50, 50, 255, 100));
            	map.addMarker(pointMarker);
        	}
    	    map.zoomAndPanTo(zoomLevel, new Location(30.641602 , -96.4739)); //reiverside
            map.zoomAndPanTo(zoomLevel, new Location(30.6235,-96.347619)); //college

    	    markerManager = map.getDefaultMarkerManager();         	
        }
        System.out.println("End of Map Setup");
		//End of Map Setup
	}
	
	public void draw() {
		if(prevAssetID!=currAssetID)	{
			background(200);
			imageMode(CENTER);
			try {	image(asset[currAssetID],SIGN_CENTER_X,SIGN_CENTER_Y);	}
			catch (Exception e)	{
				System.out.println("ERROR loading image");
				image(asset_error,SIGN_CENTER_X,SIGN_CENTER_Y);
				e.printStackTrace();
			}
			imageMode(CORNER);
			
    		if (null!=newRFIDObjectRead)	{ 
    		    Location loc = new Location(newRFIDObjectRead.getX(), newRFIDObjectRead.getY());
    			SimplePointMarker pM = new SimplePointMarker(loc);
	    		pM.setColor(color(0, 255, 0, 100));
	             //pointMarker.setStrokeColor(color(255, 0, 0));
	             //pointMarker.setStrokeWeight(4);
	            map.addMarker(pM);
	            newRFIDObjectRead = null;
    		}
            prevAssetID = currAssetID;
		}
		if(layout==ENUMS.LAYOUT.ADVANCED)	{
			map.draw();
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
					  layoutTransitionTo(ENUMS.LAYOUT.ADVANCED);
				}	else	{
					  System.out.println("Changing the layout from advanced -> simplified");
					  theEvent.getController().setLabel("Advanced");
					  layoutTransitionTo(ENUMS.LAYOUT.SIMPLIFIED);
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
	
	private void layoutTransitionTo(ENUMS.LAYOUT lay)	{
		background(220);
		calculateDimensions(lay);
		noStroke();
		fill(180);
		rect(0,0,800,89);
		fill(255);
		
		imageMode(CENTER);
		if(lay == ENUMS.LAYOUT.SIMPLIFIED)	{
			frameRate(10);
			rect(79,89,642,402);
			rect(79,509,252,72);
			rect(469,511,252,72);
			image(asset_check,205,545);
			image(asset_cross,595,545);
			
		}
		else	{
			frameRate(30);
			rect(19,89,372,402);
			rect(19,509,172,72);
			rect(219,509,172,72);
			rect(409,89,372,237);
			rect(409,344,372,237);
			
			image(asset_check,105,545);
			image(asset_cross,305,545);
			image(asset_map_bg,595,208);

		}
		
		image(asset[currAssetID],SIGN_CENTER_X,SIGN_CENTER_Y);
		System.out.println("\n\n Asset "+currAssetID+" before setting :width="+asset[currAssetID].width+" height="+asset[currAssetID].height);
		imageMode(CORNER);
	}
	
	public void loadImages()	{
		//if(null==asset)	{
			asset = new PImage[MAX_ASSET_ID];
			asset_map_bg = new PImage();
			asset_text_bg = new PImage();
			asset_check = new PImage();
			asset_cross = new PImage();
			asset_logo = new PImage();
			asset_error = new PImage();
		//}
	
		for (int i=0;i<MAX_ASSET_ID;i++)	{
			String img = i+".png";
			try	{
				asset[i] = loadImage(img);
			}
			catch (Exception e)	{
				System.out.println("Error Loading asset "+i);
				asset[i] = loadImage("error.png");
				e.printStackTrace();
			}
		}
		try	{
			asset_map_bg 	=loadImage("bg_map.png");
			//asset_text_bg	=loadImage();
			asset_check		=loadImage("check.png");
			asset_cross		=loadImage("cross.jpg");
			//asset_logo	=loadImage("");
			asset_error		=loadImage("error.png");
		}
		catch(Exception e)	{
			System.out.println("Error loading default images ");
			e.printStackTrace();
		}
	}
	
	//This method calculates the size of the objects to be created. 
	public void calculateDimensions(ENUMS.LAYOUT lay)	{
		System.out.println("CatculateDimensions invoked for "+lay+" loyout");
		float ratio = 0.0f;
		BUTTON1_WIDTH = Math.round(150*(WIDTH/800));
		BUTTON1_HEIGHT = Math.round(50*(HEIGHT/600));
		
		MAP_WIDTH = Math.round(370*(WIDTH/800));
		MAP_HEIGHT = Math.round(235*(HEIGHT/600));
		
		SIGN_HEIGHT = Math.round(400*(HEIGHT/600));
		
		if(lay == ENUMS.LAYOUT.SIMPLIFIED)	{
			SIGN_WIDTH = Math.round(640*(WIDTH/800));
		
			BUTTON2_WIDTH = Math.round(250*(WIDTH/800));
			BUTTON2_HEIGHT = Math.round(70*(HEIGHT/600));
			
			SIGN_CENTER_X = 400;
		    SIGN_CENTER_Y = 290;
		}
		else	{	
			SIGN_WIDTH = Math.round(370*(WIDTH/800));
	
			BUTTON2_WIDTH = Math.round(170*(WIDTH/800));
			BUTTON2_HEIGHT = Math.round(70*(HEIGHT/600));
			
			SIGN_CENTER_X = 205;
			SIGN_CENTER_Y = 290;
		}
		System.out.println("\n\n Asset :width="+SIGN_WIDTH+" height="+SIGN_HEIGHT);
		loadImages();
		for (int i=0;i<MAX_ASSET_ID;i++)	{
				asset[i] = resizeToFit(asset[i], SIGN_WIDTH, SIGN_HEIGHT);
		}
		
		asset_error = resizeToFit(asset_error, SIGN_WIDTH, SIGN_WIDTH);
		asset_check =  resizeToFit(asset_check,BUTTON2_WIDTH,BUTTON2_HEIGHT);
		asset_cross =  resizeToFit(asset_cross,BUTTON2_WIDTH,BUTTON2_HEIGHT);
		asset_map_bg = resizeToFit(asset_map_bg,MAP_WIDTH,MAP_HEIGHT);
		
	}
	
	private PImage resizeToFit(PImage img, int polygon_width, int polygon_height)	{
		if (null!=img)	{
			float ratio = (float)img.width/(float)img.height;
			if(polygon_width>polygon_height)	{
				if	(polygon_width>=Math.round(polygon_height*ratio))	
					img.resize(Math.round(ratio*polygon_height),polygon_height);
				else
					img.resize(polygon_width, Math.round(polygon_width/ratio));
			}
			else	{
				if	(polygon_height>=Math.round(polygon_width/ratio))	
					img.resize(polygon_width,Math.round(polygon_width/ratio));
				else
					img.resize(Math.round(ratio*polygon_height),polygon_height);
			}
		}
		else	{
			System.out.println("ERROR: asset not available for resize");
		}
		return img;
	}
	
	private String padRight(String s, int n) {
	     return String.format("%1$-" + n + "s", s);  
	}
}
