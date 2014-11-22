package ctrl;

import java.net.URL;

import b2b.Aggregator;
import b2b.Purchaser;
import b2b.Wholesaler;
import beans.AggOrderBean;

public class Manager
{

	public static void main(String[] args) throws Exception
	{
		// create aggregator and purchaser
		Aggregator agg = new Aggregator();
		Purchaser pch = new Purchaser();
		
		// add wholesalers
		pch.addWholesaler(new Wholesaler("Toronto", new URL("http://roumani.eecs.yorku.ca:4413/axis/YYZ.jws")));
		pch.addWholesaler(new Wholesaler("Vancouver", new URL("http://roumani.eecs.yorku.ca:4413/axis/YVR.jws")));
		pch.addWholesaler(new Wholesaler("Halifax", new URL("http://roumani.eecs.yorku.ca:4413/axis/YHZ.jws")));
		
		// set purchase key
		pch.setKey("4413Secret");
		
		// test
		String[] fns = {"/home/user/Downloads/PO120.xml",
							 "/home/user/Downloads/PO121.xml",
							 "/home/user/Downloads/PO122.xml",
							 "/home/user/Downloads/PO124.xml"};
		
		AggOrderBean prcr = agg.getAggregatedOrder(fns, 103);
		pch.purchase(prcr, "/home/user/Downloads/rpt103.xml");
	}

}
