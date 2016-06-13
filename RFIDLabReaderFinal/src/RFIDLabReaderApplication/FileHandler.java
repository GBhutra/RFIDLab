/**
 * 
 */
package RFIDLabReaderApplication;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.llrp.ltk.generated.parameters.TagReportData;

import java.util.Map.Entry;
import java.util.regex.Pattern;

import processing.core.PApplet.*;



/**
 * @author Ghanshyam
 *
 */
public class FileHandler implements Observer{
	/**
	 * This class should handle all the thing related to the files. 
	 * Read the tags on start, update the hash when a tag is read
	 * and save the file into a printable format when save is invoked at the GUI
	 */
	
	protected static HashMap<String, Integer> epcCount = new HashMap<String, Integer>();
    protected static HashMap<String, String> assetEPC = new HashMap<String, String>();
    protected HashMap<String, RFIDObject> lifeExpMap = new HashMap<String, RFIDObject>();
    
	
	public FileHandler(String fileName) {
		// TODO Auto-generated constructor stub
		if(ENUMS.STATUS.SUCCESS==readFromCSVFile(fileName))	{ System.out.println(fileName+" file read successful");	}
		else {	System.out.println("ERROR: couldn't read "+fileName); }
	}
	
	public Integer getEPCCount(String RFIDTagId)	{
		return epcCount.get(RFIDTagId);
	}
	public String getAssetID(String RFIDTagId)	{
		return assetEPC.get(RFIDTagId);
	}
	public RFIDObject getRFIDObjectFromLifeExpMap(String RFIDTagId)	{
		return lifeExpMap.get(RFIDTagId);
	}
	
	public ENUMS.STATUS saveLog()	{
		PrintWriter writer = null;
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());
		try {
			writer = new PrintWriter("RFID"+timeStamp+".txt", "UTF-8");
		} catch (FileNotFoundException | UnsupportedEncodingException e) {
			e.printStackTrace();
			return ENUMS.STATUS.FAILURE;
		}
		
		for (Entry<String, Integer> entry : epcCount.entrySet())	{
			String key = entry.getKey();
	  	    int value = entry.getValue();
	  	    // System.out.println("Key is "+key+" and Value is "+value);
	  	    writer.println("RFID File created on "+timeStamp);
	  	    //lifeExpMap Global database, assetEPC (EPCTag, assetID)
	  	    // RFIDObj tempFileObject = new RFIDObj();
	  	    writer.println("Key:"+key+" #Value:"+value+" #Sign:"); //key is the RFID tag and value is the number of tags read 
	  	    writer.close();
      	}
        //saving the log of the data
        String fileName = "temp.txt";

        try {
            // Assume default encoding.
            FileWriter fileWriter = new FileWriter(fileName);

            // Always wrap FileWriter in BufferedWriter.
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

            // Note that write() does not automatically
            // append a newline character.
            Set<String> keys = epcCount.keySet(); // the read tags
            
            bufferedWriter.write("Hello there,");
            bufferedWriter.write(" here is some text.");
            bufferedWriter.newLine();
            bufferedWriter.write("We are writing");
            bufferedWriter.write(" the text to the file.");
            bufferedWriter.newLine();
            String key;
            Iterator<Map.Entry<String, RFIDObject>> i = lifeExpMap.entrySet().iterator(); 
            
            while(i.hasNext()){
                key = i.next().getKey();
                bufferedWriter.write( padRight(key.toString(), 5) +padRight((String) lifeExpMap.get(key).getEpcTag() ,45)+padRight((String) lifeExpMap.get(key).getSign() ,55));
                bufferedWriter.newLine();
            }

            // Always close files.
            bufferedWriter.close();
        }
        catch(IOException ex) {
        	ex.printStackTrace();
            return ENUMS.STATUS.FAILURE;
        }
		return ENUMS.STATUS.SUCCESS;
	}
	
	// Helper method to load life expectancy data from file
    private ENUMS.STATUS readFromCSVFile(String fileName) {
        BufferedReader br = null;
        String line="";
        try	{
        	br = new BufferedReader(new FileReader(fileName));
        	while ((line = br.readLine()) != null) {
        		String[] columns = line.split(",");
        		if (columns.length == 7 && !columns[6].equals("GPSLocation")) {
        			String[] doubleRiversideCoordinates = columns[6].split(":");
                	RFIDObject tmpObj = new RFIDObject(Double.parseDouble(doubleRiversideCoordinates[0]), Double.parseDouble(doubleRiversideCoordinates[1]), columns[5], columns[2]+" ** "+columns[3], Integer.parseInt(columns[1]));
                    lifeExpMap.put(columns[1],tmpObj); //asset no
                    assetEPC.put(tmpObj.getEpcTag(), columns[1]);
        		}
    		}
	    } catch (FileNotFoundException e) {
			e.printStackTrace();
			return ENUMS.STATUS.FAILURE;
		} catch (IOException e) {
			e.printStackTrace();
			return ENUMS.STATUS.FAILURE;
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
					System.out.println("Could not close the buffered reader!! not exitting");
					//return ENUMS.STATUS.FAILURE;
				}
			}
		}
        return ENUMS.STATUS.SUCCESS;
    }
    
    private String padRight(String s, int n) {
	     return String.format("%1$-" + n + "s", s);  
	}

	@Override
	public ENUMS.STATUS update(TagReportData tag) {
		// TODO Auto-generated method stub
		System.out.println("Updating the fileHandler");
    	String tagEPCRead = (String)tag.getEPCParameter().toString();
	   	String temp[] = tagEPCRead.split(Pattern.quote(":"));
    	
    	if(temp[2].equalsIgnoreCase(" e20021002000528314cb0272"))	{
	   	}
    	else if(epcCount.get(temp[2])==null){
	   		epcCount.put(temp[2], 1);
	   	}
	   	else	{
	   		int tmpEPCCount = epcCount.get(temp[2]);
		   	tmpEPCCount = tmpEPCCount + 1;
		   	epcCount.put(temp[2],tmpEPCCount);	
	   	}
		return ENUMS.STATUS.SUCCESS;
	}
}


/*
 * if(RUNNING_STATUS == 1){
			                  stop();
			                  System.out.println(" stop button clicked.");
			                  PrintWriter writer = null;
			                  String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());
							try {
								writer = new PrintWriter("RFID"+timeStamp+".txt", "UTF-8");
							} catch (FileNotFoundException | UnsupportedEncodingException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
								System.out.println("akash : didnot write ");
							}
			                	 
			                  for (Entry<String, Integer> entry : epcCount.entrySet()) {
			                	  String key = entry.getKey();
			                	  int value = entry.getValue();
			                	 // System.out.println("Key is "+key+" and Value is "+value);
			                	  writer.println("RFID File created on "+timeStamp);
			                	  //lifeExpMap Global database, assetEPC (EPCTag, assetID)
			                	 // RFIDObj tempFileObject = new RFIDObj();
			                	  writer.println("Key:"+key+" #Value:"+value+" #Sign:"); //key is the RFID tag and value is the number of tags read 
			                	  writer.close();
			                	  
			                	  // do stuff
			                	}
			                  
			                  //saving the log of the data
			                  String fileName = "temp.txt";

			                  try {
			                      // Assume default encoding.
			                      FileWriter fileWriter =
			                          new FileWriter(fileName);

			                      // Always wrap FileWriter in BufferedWriter.
			                      BufferedWriter bufferedWriter =
			                          new BufferedWriter(fileWriter);

			                      // Note that write() does not automatically
			                      // append a newline character.
			                      Set<String> keys = epcCount.keySet(); // the read tags
			                      
			                      bufferedWriter.write("Hello there,");
			                      bufferedWriter.write(" here is some text.");
			                      bufferedWriter.newLine();
			                      bufferedWriter.write("We are writing");
			                      bufferedWriter.write(" the text to the file.");
			                      String key;
			                      Iterator<Map.Entry<String, RFIDObj>> i = lifeExpMap.entrySet().iterator(); 
			                      
			                      while(i.hasNext()){
			                          key = i.next().getKey();
			                         
			                          
//			                          for ( String key : epcCount.keySet() ) {
//			                     		 String tmpKey = key.toString().trim();
//			                     		// tmpKey = tmpKey.substring(1, tmpKey.length());
//			                     		 tmpKey = "0x"+tmpKey;
//			                     		 String tmp = assetEPC.get(tmpKey);
//			                     		 System.out.println("Akash assetEPC"+tmp + " , Key = "+tmpKey);
//			                     		 RFIDObj rfidTmp = null;
//			                     		 if(tmp!=null)
//			                     			  rfidTmp = lifeExpMap.get(tmp);
//			                     		 System.out.println("Akash key "+tmp);
//			                          }
			                         // System.out.println("Asset:"+key+", loc: "+lifeExpMap.get(key).x+","+lifeExpMap.get(key).y+" ,EPC:"+(String) lifeExpMap.get(key).epcTag+" ,Sign:"+(String)lifeExpMap.get(key).sign);
			                          bufferedWriter.write( padRight(key.toString(), 5) +padRight((String) lifeExpMap.get(key).epcTag ,45)+padRight((String) lifeExpMap.get(key).sign ,55));
			                          bufferedWriter.newLine();
			                      }

			                      // Always close files.
			                      bufferedWriter.close();
			                  }
			                  catch(IOException ex) {
			                      System.out.println(
			                          "Error writing to file '"
			                          + fileName + "'");
			                      // Or we could just do this:
			                      // ex.printStackTrace();
			                  }
			                  
			                  
			                  
			                  
			                 
			                  }
			                  //0xe300833b2ddd9014035050000
			                   controlP5.remove(event.getController().getName());
			                }
			                
						

 */
