package RFIDLabReaderApplication;
import org.llrp.ltk.generated.parameters.TagReportData;

public interface Observer {	
	ENUMS.STATUS update(TagReportData Tag);
}
