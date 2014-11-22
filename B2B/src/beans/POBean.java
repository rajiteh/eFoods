package beans;

import java.util.Calendar;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;

// Bean class for single purchase order
@XmlRootElement(name="order")
public class POBean
{
	// @XmlElement
	private CustomerBean customer;
	// @XmlElement(name="item")
	private ItemsWrapper items;
	// @XmlElement
	private double total;
	// @XmlElement
	private double shipping;
	// @XmlElement(name="HST")
	private double hst;
	// @XmlElement
	private double grandTotal;
	// @XmlAttribute
	private int id;
	// @XmlAttribute
	// @XmlSchemaType(name = "date")
	private Calendar submitted;
	
	public POBean()
   {
	   super();
   }

	public POBean(CustomerBean customer, ItemsWrapper items, double total,
         double shipping, double hst, double grandTotal, int id,
         Calendar submitted)
   {
	   this.customer = customer;
	   this.items = items;
	   this.total = total;
	   this.shipping = shipping;
	   this.hst = hst;
	   this.grandTotal = grandTotal;
	   this.id = id;
	   this.submitted = submitted;
   }

	// ==============   getters and setters  ====================
	@XmlElement
	public CustomerBean getCustomer()
	{
		return customer;
	}

	public void setCustomer(CustomerBean customer)
	{
		this.customer = customer;
	}

	@XmlElement(name="items")
	public ItemsWrapper getItems()
	{
		return items;
	}

	public void setItems(ItemsWrapper items)
	{
		this.items = items;
	}

	@XmlElement
	public double getTotal()
	{
		return total;
	}

	public void setTotal(double total)
	{
		this.total = total;
	}

	@XmlElement
	public double getShipping()
	{
		return shipping;
	}

	public void setShipping(double shipping)
	{
		this.shipping = shipping;
	}

	@XmlElement
	public double getHst()
	{
		return hst;
	}

	public void setHst(double hst)
	{
		this.hst = hst;
	}

	@XmlElement
	public double getGrandTotal()
	{
		return grandTotal;
	}

	public void setGrandTotal(double grandTotal)
	{
		this.grandTotal = grandTotal;
	}

	@XmlAttribute
	public int getId()
	{
		return id;
	}

	public void setId(int id)
	{
		this.id = id;
	}

	@XmlAttribute
	@XmlSchemaType(name = "date")
	public Calendar getSubmitted()
	{
		return submitted;
	}

	public void setSubmitted(Calendar submitted)
	{
		this.submitted = submitted;
	}
}
