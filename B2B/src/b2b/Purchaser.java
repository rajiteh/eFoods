package b2b;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;

import beans.AggItemBean;
import beans.AggOrderBean;

public class Purchaser
{	
	private List<Wholesaler> wholesalers;
	private String key;
	
	public Purchaser()
	{
		this.wholesalers = new ArrayList<Wholesaler>();
	}
	
	public void purchase(AggOrderBean order, String outfile) throws Exception
	{	
		Iterator<AggItemBean> itr = order.getItems().iterator();
		while (itr.hasNext())
		{
			AggItemBean item = itr.next();
			
			// find best offer
			Wholesaler best = null;
			double bestOffer = Double.POSITIVE_INFINITY;
			Iterator<Wholesaler> itr2 = this.wholesalers.iterator();
			while (itr2.hasNext())
			{
				Wholesaler cur = itr2.next();
				double offer = cur.quote(item.getNumber());
				if (offer >= 0 && offer < bestOffer)
				{
					best = cur;
					bestOffer = offer;
				}
			} // end of iterating wholesalers
			
			// item out of stock
			if (best == null)
			{
				item.setPrice(-1.0);
				item.setWholesaler("No wholesaler");
				item.setCode("Out of stock");
			}
			else // take the best offer
			{
				item.setCode(best.order(item.getNumber(), item.getQuantity(), this.key));
				item.setPrice(bestOffer);
				item.setWholesaler(best.getName());
				item.updateExtended();
			}
		} // end of iterating items
		
		// generate report
		order.updateTotal();
		this.generateReport(order, outfile);
	}
	
	public boolean addWholesaler(Wholesaler ws)
	{
		return this.wholesalers.add(ws);
	}
	
	public void setKey(String key)
	{
		this.key = key;
	}
	
	private void generateReport(AggOrderBean order, String filename) throws Exception
	{
		// marshaling
		JAXBContext jc = JAXBContext.newInstance(order.getClass());
		Marshaller mar = jc.createMarshaller();
		mar.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
		mar.setProperty(Marshaller.JAXB_FRAGMENT, Boolean.TRUE);
		mar.marshal(order, new File(filename));
	}
}
