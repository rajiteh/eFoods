package b2b;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

import beans.AggItemBean;
import beans.AggOrderBean;
import beans.ItemBean;
import beans.POBean;

// class that reads and consolidates purchase orders
public class Aggregator
{	
	public Aggregator()
   {
   }
	
	public AggOrderBean getAggregatedOrder(String[] fnames, int id) throws Exception
	{
		// get purchase orders
		List<POBean> orders = this.getOrders(fnames);
		
		// map that temporally stores item list
		Map<String, AggItemBean> mp = new TreeMap<String, AggItemBean>();
		
		// aggregate orders
		Iterator<POBean> itr = orders.iterator();
		while (itr.hasNext())
		{
			List<ItemBean> items = itr.next().getItems().getItemsList();
			
			// loop over items in a single order
			Iterator<ItemBean> itr2 = items.iterator();
			while (itr2.hasNext())
			{
				ItemBean item = itr2.next();
				String key = item.getNumber();
				
				if (mp.containsKey(key))
				{
					mp.get(key).addQuantity(item.getQuantity());
				}
				else
				{
					mp.put(key, new AggItemBean(item));
				}
			}
		} // end of aggregating orders into map
		
		// restore aggregated order into AggOrderBean
		List<AggItemBean> aggItems = new ArrayList<AggItemBean>();
		Iterator<AggItemBean> itr3 = mp.values().iterator();
		while (itr3.hasNext())
		{
			aggItems.add(itr3.next());
		}
		
		return new AggOrderBean(aggItems, id);
	}
	
	private List<POBean> getOrders(String[] fnames) throws Exception
	{
		if (fnames == null)
			throw new NullPointerException("File names are not valid.");
		
		// define unmarshaller
		JAXBContext jc;
		Unmarshaller um;
		try
		{
			jc = JAXBContext.newInstance(POBean.class);
			um = jc.createUnmarshaller();
		}
		catch (Exception e)
		{
			throw new Exception("Error occurs when defining unmarshaller.");
		}
		
		// ummarshal order files
		List<POBean> orders = new ArrayList<POBean>();
		for (int i = 0; i < fnames.length; i++)
		{
			try
			{
				orders.add((POBean) um.unmarshal(new File(fnames[i])));
			}
			catch (Exception e)
			{
				throw new Exception("Error occurs during unmarshalling.");
			}
		}
		
		return orders;
	}
}
