/**
 * 
 */
package RFIDLabReaderApplication;

import java.util.List;
import java.util.regex.Pattern;

import org.llrp.ltk.generated.enumerations.AISpecStopTriggerType;
import org.llrp.ltk.generated.enumerations.AirProtocols;
import org.llrp.ltk.generated.enumerations.ROReportTriggerType;
import org.llrp.ltk.generated.enumerations.ROSpecStartTriggerType;
import org.llrp.ltk.generated.enumerations.ROSpecState;
import org.llrp.ltk.generated.enumerations.ROSpecStopTriggerType;
import org.llrp.ltk.generated.enumerations.StatusCode;
import org.llrp.ltk.generated.messages.ADD_ROSPEC;
import org.llrp.ltk.generated.messages.ADD_ROSPEC_RESPONSE;
import org.llrp.ltk.generated.messages.DELETE_ROSPEC;
import org.llrp.ltk.generated.messages.DELETE_ROSPEC_RESPONSE;
import org.llrp.ltk.generated.messages.ENABLE_ROSPEC;
import org.llrp.ltk.generated.messages.ENABLE_ROSPEC_RESPONSE;
import org.llrp.ltk.generated.messages.RO_ACCESS_REPORT;
import org.llrp.ltk.generated.messages.START_ROSPEC;
import org.llrp.ltk.generated.messages.START_ROSPEC_RESPONSE;
import org.llrp.ltk.generated.parameters.AISpec;
import org.llrp.ltk.generated.parameters.AISpecStopTrigger;
import org.llrp.ltk.generated.parameters.InventoryParameterSpec;
import org.llrp.ltk.generated.parameters.ROBoundarySpec;
import org.llrp.ltk.generated.parameters.ROReportSpec;
import org.llrp.ltk.generated.parameters.ROSpec;
import org.llrp.ltk.generated.parameters.ROSpecStartTrigger;
import org.llrp.ltk.generated.parameters.ROSpecStopTrigger;
import org.llrp.ltk.generated.parameters.TagReportContentSelector;
import org.llrp.ltk.generated.parameters.TagReportData;
import org.llrp.ltk.net.LLRPConnection;
import org.llrp.ltk.net.LLRPConnectionAttemptFailedException;
import org.llrp.ltk.net.LLRPConnector;
import org.llrp.ltk.net.LLRPEndpoint;
import org.llrp.ltk.types.Bit;
import org.llrp.ltk.types.LLRPMessage;
import org.llrp.ltk.types.UnsignedByte;
import org.llrp.ltk.types.UnsignedInteger;
import org.llrp.ltk.types.UnsignedShort;
import org.llrp.ltk.types.UnsignedShortArray;

/**
 * @author Ghanshyam
 *
 */


public class RFIDReader	implements LLRPEndpoint	{
	private enum ENUM_STATUS	{
		RUNNING, STOP
	}
	
	private String IpAddress;
	private LLRPConnection reader;
	private ENUM_STATUS status;
	private static final int TIMEOUT_MS = 10000;
	private static final int ROSPEC_ID = 123;
	private java.util.Vector<Observer> observers = new java.util.Vector<Observer>();

	public RFIDReader(String ip)	{
		IpAddress = ip;
		reader = new LLRPConnector(this, IpAddress);
		status = ENUM_STATUS.STOP;
	}
	
	public void register( Observer obs )	{ 
		observers.addElement( obs ); 
	}

	String getIPAddress() {
		return IpAddress;
	}
	
	public void startReader()	{
		try {	
			Thread.sleep(100);
			System.out.println("Starting reader.");
			connect();
			deleteROSpecs();
			addROSpec();
			enableROSpec();
			startROSpec();
				       
		} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();			
		}
		status = ENUM_STATUS.RUNNING;
		System.out.println("Reader successfully started");
	}
	
	public void stopReader()	{
		if	(status==ENUM_STATUS.RUNNING)	{
			deleteROSpecs();
			disconnect();
			status = ENUM_STATUS.STOP;
		}
	}
	
	private void connect()	{
        try	{
            System.out.println("Connecting to the reader.");
            ((LLRPConnector) reader).connect();
        }
        catch (LLRPConnectionAttemptFailedException e1)	{
            e1.printStackTrace();
            System.exit(1);
        }
    }
      
    // Disconnect from the reader
    private void disconnect()	{
        ((LLRPConnector) reader).disconnect();
    }
    
	@Override
	public void errorOccured(String arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void messageReceived(LLRPMessage msg) {
		// TODO Auto-generated method stub
		/*
		 * This function is invoked whenever a tag is read by the reader
		 */
		
		System.out.println("messageReceived function start..");
   
        if (msg.getTypeNum() == RO_ACCESS_REPORT.TYPENUM)	{
            // The message received is an Access Report.
            RO_ACCESS_REPORT report = (RO_ACCESS_REPORT) msg;
            
            // Get a list of the tags read.
            List <TagReportData> tags =	report.getTagReportDataList();
            
            // Loop through the list and get the EPC of each tag and update the Observers.
            for (TagReportData tag : tags)	{
            	for (java.util.Enumeration e=observers.elements(); e.hasMoreElements(); )	{
        			if(((Observer)e.nextElement()).update(tag) == ENUMS.STATUS.SUCCESS)	{
        				System.out.println(" Update successful!!");
        			}
        			else	{
        				System.out.println("ERROR: Update Failed!!");
        			}
        		}
            }
        }
	}
	
	 //RFID Functions
	public ROSpec buildROSpec()	{ 
		// System.out.println("Building the ROSpec.");
	    // Create a Reader Operation Spec (ROSpec).
	    ROSpec roSpec = new ROSpec();
	          
	        roSpec.setPriority(new UnsignedByte(0));
	        roSpec.setCurrentState(new ROSpecState(ROSpecState.Disabled));
	        roSpec.setROSpecID(new UnsignedInteger(ROSPEC_ID));
	        
	          
	        // Set up the ROBoundarySpec
	        // This defines the start and stop triggers.
	        ROBoundarySpec roBoundarySpec = new ROBoundarySpec();
	          
	        // Set the start trigger to null.
	        // This means the ROSpec will start as soon as it is enabled.
	        ROSpecStartTrigger startTrig = new ROSpecStartTrigger();
	        startTrig.setROSpecStartTriggerType
	            (new ROSpecStartTriggerType(ROSpecStartTriggerType.Null));
	        roBoundarySpec.setROSpecStartTrigger(startTrig);
	          
	        // Set the stop trigger is null. This means the ROSpec
	        // will keep running until an STOP_ROSPEC message is sent.
	        ROSpecStopTrigger stopTrig = new ROSpecStopTrigger();
	        stopTrig.setDurationTriggerValue(new UnsignedInteger(0));
	        stopTrig.setROSpecStopTriggerType
	            (new ROSpecStopTriggerType(ROSpecStopTriggerType.Null));
	        roBoundarySpec.setROSpecStopTrigger(stopTrig);
	          
	        roSpec.setROBoundarySpec(roBoundarySpec);
	          
	        // Add an Antenna Inventory Spec (AISpec).
	        AISpec aispec = new AISpec();
	          
	        // Set the AI stop trigger to null. This means that
	        // the AI spec will run until the ROSpec stops.
	        AISpecStopTrigger aiStopTrigger = new AISpecStopTrigger();
	        aiStopTrigger.setAISpecStopTriggerType
	            (new AISpecStopTriggerType(AISpecStopTriggerType.Null));
	        aiStopTrigger.setDurationTrigger(new UnsignedInteger(0));
	        aispec.setAISpecStopTrigger(aiStopTrigger);
	          
	        // Select which antenna ports we want to use.
	        // Setting this property to zero means all antenna ports.
	        UnsignedShortArray antennaIDs = new UnsignedShortArray();
	        antennaIDs.add(new UnsignedShort(0));
	        aispec.setAntennaIDs(antennaIDs);
	          
	        // Tell the reader that we're reading Gen2 tags.
	        InventoryParameterSpec inventoryParam = new InventoryParameterSpec();
	        inventoryParam.setProtocolID
	            (new AirProtocols(AirProtocols.EPCGlobalClass1Gen2));
	        inventoryParam.setInventoryParameterSpecID(new UnsignedShort(1));
	        aispec.addToInventoryParameterSpecList(inventoryParam);
	          
	        roSpec.addToSpecParameterList(aispec);
	          
	        // Specify what type of tag reports we want
	        // to receive and when we want to receive them.
	        ROReportSpec roReportSpec = new ROReportSpec();
	        // Receive a report every time a tag is read.
	        roReportSpec.setROReportTrigger(new ROReportTriggerType
	            (ROReportTriggerType.Upon_N_Tags_Or_End_Of_ROSpec));
	        roReportSpec.setN(new UnsignedShort(1));
	        TagReportContentSelector reportContent =
	            new TagReportContentSelector();
	        // Select which fields we want in the report.
	        reportContent.setEnableAccessSpecID(new Bit(0));
	        reportContent.setEnableAntennaID(new Bit(0));
	        reportContent.setEnableChannelIndex(new Bit(0));
	        reportContent.setEnableFirstSeenTimestamp(new Bit(0));
	        reportContent.setEnableInventoryParameterSpecID(new Bit(0));
	        reportContent.setEnableLastSeenTimestamp(new Bit(1));
	        reportContent.setEnablePeakRSSI(new Bit(0));
	        reportContent.setEnableROSpecID(new Bit(0));
	        reportContent.setEnableSpecIndex(new Bit(0));
	        reportContent.setEnableTagSeenCount(new Bit(0));
	        roReportSpec.setTagReportContentSelector(reportContent);
	        roSpec.setROReportSpec(roReportSpec);
	          
	        return roSpec;
	    }
	      
	    // Add the ROSpec to the reader.
	    public void addROSpec()
	    {
	        ADD_ROSPEC_RESPONSE response;
	          
	        ROSpec roSpec = buildROSpec();
	       // System.out.println("Adding the ROSpec.");
	        try
	        {
	            ADD_ROSPEC roSpecMsg = new ADD_ROSPEC();
	            roSpecMsg.setROSpec(roSpec);
	            response = (ADD_ROSPEC_RESPONSE)
	                reader.transact(roSpecMsg, TIMEOUT_MS);
	           // System.out.println(response.toXMLString());
	              
	            // Check if the we successfully added the ROSpec.
	            StatusCode status = response.getLLRPStatus().getStatusCode();
	            if (status.equals(new StatusCode("M_Success")))
	            {
	              //  System.out.println
	                //    ("Successfully added ROSpec.");
	            }
	            else
	            {
	                System.out.println("Error adding ROSpec.");
	                System.exit(1);
	            }
	        }
	        catch (Exception e)
	        {
	            System.out.println("Error adding ROSpec.");
	            e.printStackTrace();
	        }
	    }
	      
	    // Enable the ROSpec.
	    public void enableROSpec()
	    {
	        ENABLE_ROSPEC_RESPONSE response;
	          
	       // System.out.println("Enabling the ROSpec.");
	        ENABLE_ROSPEC enable = new ENABLE_ROSPEC();
	        enable.setROSpecID(new UnsignedInteger(ROSPEC_ID));
	        try
	        {
	            response = (ENABLE_ROSPEC_RESPONSE)
	                reader.transact(enable, TIMEOUT_MS);
	           // System.out.println(response.toXMLString());
	        }
	        catch (Exception e)
	        {
	            System.out.println("Error enabling ROSpec.");
	            e.printStackTrace();
	        }
	    }
	      
	    // Start the ROSpec.
	    public void startROSpec()
	    {
	        START_ROSPEC_RESPONSE response;
	       // System.out.println("Starting the ROSpec.");
	        START_ROSPEC start = new START_ROSPEC();
	        start.setROSpecID(new UnsignedInteger(ROSPEC_ID));
	        try
	        {
	            response = (START_ROSPEC_RESPONSE)
	                reader.transact(start, TIMEOUT_MS);
	          //  System.out.println(response.toXMLString());
	        }
	        catch (Exception e)
	        {
	            System.out.println("Error deleting ROSpec.");
	            e.printStackTrace();
	        }
	    }
	      
	    // Delete all ROSpecs from the reader.
	    public void deleteROSpecs()
	    {
	        DELETE_ROSPEC_RESPONSE response;
	          
	       // System.out.println("Deleting all ROSpecs.");
	        DELETE_ROSPEC del = new DELETE_ROSPEC();
	        // Use zero as the ROSpec ID.
	        // This means delete all ROSpecs.
	        del.setROSpecID(new UnsignedInteger(0));
	        try
	        {
	            response = (DELETE_ROSPEC_RESPONSE)
	                reader.transact(del, TIMEOUT_MS);
	           // System.out.println(response.toXMLString());
	        }
	        catch (Exception e)
	        {
	            System.out.println("Error deleting ROSpec.");
	            e.printStackTrace();
	        }
	    }
	
}

