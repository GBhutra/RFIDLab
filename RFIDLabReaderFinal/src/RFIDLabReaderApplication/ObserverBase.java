/**
 * 
 */
package RFIDLabReaderApplication;
import org.llrp.ltk.generated.parameters.TagReportData;
/**
 * @author Ghanshyam
 *
 */
public interface ObserverBase {
	ENUMS.STATUS update(TagReportData Tag);
}