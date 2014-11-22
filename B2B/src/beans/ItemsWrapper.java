package beans;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class ItemsWrapper
{
	@XmlElement(name="item")
	private List<ItemBean> list;

	public ItemsWrapper()
   {
	   super();
   }

	public List<ItemBean> getItemsList()
	{
		return list;
	}

	public void setItemsList(List<ItemBean> list)
	{
		this.list = list;
	}
}
