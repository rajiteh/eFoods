package beans;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

// item bean for aggregated order
@XmlRootElement
public class AggItemBean
{
	// @XmlAttribute
	private String number;
	// @XmlElement
	private String name;
	// @XmlElement
	private int quantity;
	
	// the following attributes may not be defined at creation
	// @XmlElement
	private double price;
	// @XmlElement
	private double extended;
	// @XmlElement
	private String wholesaler;
	// @XmlElement
	private String code;
	
	public AggItemBean()
   {
	   super();
   }

	public AggItemBean(ItemBean item)
	{
		this.number = item.getNumber();
		this.name = item.getName();
		this.quantity = item.getQuantity();
	}
	
	public AggItemBean(String number, String name, double price, int quantity,
         String wholesaler, String code)
   {
	   this.number = number;
	   this.name = name;
	   this.price = price;
	   this.quantity = quantity;
	   this.updateExtended();
	   this.wholesaler = wholesaler;
	   this.code = code;
   }
	
	public void addQuantity(int amount)
	{
		this.quantity = this.quantity + amount;
		this.updateExtended();
	}

	// ==============   getters and setters  ====================
	@XmlAttribute
	public String getNumber()
	{
		return number;
	}

	public void setNumber(String number)
	{
		this.number = number;
	}

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
		this.updateExtended();
	}

	@XmlElement
	public int getQuantity()
	{
		return quantity;
	}

	public void setQuantity(int quantity)
	{
		this.quantity = quantity;
		this.updateExtended();
	}

	@XmlElement
	public double getExtended()
	{
		return extended;
	}

	@XmlElement
	public String getWholesaler()
	{
		return wholesaler;
	}

	public void setWholesaler(String wholesaler)
	{
		this.wholesaler = wholesaler;
	}

	@XmlElement
	public String getCode()
	{
		return code;
	}

	public void setCode(String code)
	{
		this.code = code;
	}
	
	public void updateExtended()
	{
		this.extended = this.price * this.quantity;
	}
}
