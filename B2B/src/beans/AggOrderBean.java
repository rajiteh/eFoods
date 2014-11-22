package beans;

import java.util.Iterator;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

// aggregated order bean
@XmlRootElement(name="procurement")
public class AggOrderBean
{
	// @XmlElement(name="item")
	private List<AggItemBean> items;
	// @XmlElement
	private double total;
	// @XmlAttribute
	private int id;
	
	public AggOrderBean()
   {
	   super();
   }

	public AggOrderBean(List<AggItemBean> items, int id)
   {
	   this.items = items;
	   this.updateTotal();
	   this.id = id;
   }
	
	// ==============   getters and setters  ====================
	@XmlElement(name="item")
	public List<AggItemBean> getItems()
	{
		return items;
	}

	public void setItems(List<AggItemBean> items)
	{
		this.items = items;
	}

	@XmlElement
	public double getTotal()
	{
		this.updateTotal();
		return total;
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
	
	private void updateTotal()
	{
		this.total = 0;
		Iterator<AggItemBean> itr = this.items.iterator();
		while (itr.hasNext())
		{
			this.total = this.total + itr.next().getExtended();
		}
	}
}
