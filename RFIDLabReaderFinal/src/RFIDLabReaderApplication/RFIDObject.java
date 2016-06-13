/**
 * 
 */
package RFIDLabReaderApplication;

/**
 * @author Ghanshyam
 *
 */
public class RFIDObject {

	/**
	 * 
	 */
	
	private double x,y;
	private String epcTag;
	private String sign;
	private int assetID;
	private boolean readStatus;
	
	
	public RFIDObject() {
		// TODO Auto-generated constructor stub
		x=0;y=0;
		epcTag = null;
		sign = null;
		assetID = 0;
		readStatus = false;
	}
	
	public RFIDObject(double x, double y) {
		// TODO Auto-generated constructor stub
		this.x = x;
		this.y =y;
	}
	
	public RFIDObject(double x, double y, String epc, String sgn, int asset) {
		this.x = x;
		this.y =y;
		this.epcTag = epc;
		this.sign = sgn;
		this.assetID = asset;
		this.readStatus = false;
	}
	
	public double getX() {
		return x;
	}
	public void setX(double x) {
		this.x = x;
	}
	public double getY() {
		return y;
	}
	public void setY(double y) {
		this.y = y;
	}
	public String getEpcTag() {
		return epcTag;
	}
	public void setEpcTag(String epcTag) {
		this.epcTag = epcTag;
	}
	public String getSign() {
		return sign;
	}
	public void setSign(String sign) {
		this.sign = sign;
	}
	public int getAssetID() {
		return assetID;
	}
	public void setAssetID(int assetID) {
		this.assetID = assetID;
	}
	public boolean isReadStatus() {
		return readStatus;
	}
	public void setReadStatus(boolean readStatus) {
		this.readStatus = readStatus;
	}

}

