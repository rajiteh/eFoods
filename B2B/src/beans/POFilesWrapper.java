package beans;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="PurchaseOrderFiles")
public class POFilesWrapper
{
	@XmlElement(name="PurchaseOrderFile")
	private List<POFileBean> lsPof;

	public POFilesWrapper()
   {
	   super();
   }

	public List<POFileBean> getPOFileBeans()
	{
		return lsPof;
	}
}
