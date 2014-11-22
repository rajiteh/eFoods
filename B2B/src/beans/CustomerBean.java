package beans;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class CustomerBean
{
	// @XmlElement
	private String name;
	// @XmlAttribute
	private String account;
	
	public CustomerBean()
   {
	   super();
   }

	public CustomerBean(String name, String account)
   {
	   this.name = name;
	   this.account = account;
   }

	// ==============   getters and setters  ====================
	@XmlElement
	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	@XmlAttribute
	public String getAccount()
	{
		return account;
	}

	public void setAccount(String account)
	{
		this.account = account;
	}
}
