package beans;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class ItemBean
{
	// @XmlElement
	private String name;
	// @XmlElement
	private double price;
	// @XmlElement
	private int quantity;
	// @XmlElement
	private double extended;	// = price * quantity
	// @XmlAttribute
	private String number;	// item number
	
	public ItemBean()
   {
	   super();
   }

	public ItemBean(String name, double price, int quantity, double extended,
         String number)
   {
	   this.name = name;
	   this.price = price;
	   this.quantity = quantity;
	   this.extended = extended;
	   this.number = number;
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

	@XmlElement
	public double getPrice()
	{
		return price;
	}

	public void setPrice(double price)
	{
		this.price = price;
	}

	@XmlElement
	public int getQuantity()
	{
		return quantity;
	}

	public void setQuantity(int quantity)
	{
		this.quantity = quantity;
	}

	@XmlElement
	public double getExtended()
	{
		return extended;
	}

	public void setExtended(double extended)
	{
		this.extended = extended;
	}

	@XmlAttribute
	public String getNumber()
	{
		return number;
	}

	public void setNumber(String number)
	{
		this.number = number;
	}
}
