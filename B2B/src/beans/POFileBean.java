package beans;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class POFileBean
{
	@XmlElement
	private String url;
	@XmlElement
	private int id;
	
	public POFileBean()
   {
	   super();
   }

	public String getUrl()
	{
		return url;
	}

	public int getId()
	{
		return id;
	}
}
