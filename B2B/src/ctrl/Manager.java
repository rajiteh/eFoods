package ctrl;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

import b2b.Aggregator;
import b2b.Purchaser;
import b2b.Wholesaler;
import beans.AggOrderBean;

public class Manager
{
	public static void main(String[] args)
	{	
		// create aggregator and purchaser
		Aggregator agg = new Aggregator();
		Purchaser pch = new Purchaser();
		
		// add wholesalers
		try
		{
			pch.addWholesaler(new Wholesaler("Toronto", new URL("http://roumani.eecs.yorku.ca:4413/axis/YYZ.jws")));
			pch.addWholesaler(new Wholesaler("Vancouver", new URL("http://roumani.eecs.yorku.ca:4413/axis/YVR.jws")));
			pch.addWholesaler(new Wholesaler("Halifax", new URL("http://roumani.eecs.yorku.ca:4413/axis/YHZ.jws")));
		}
		catch (MalformedURLException m)
		{
			System.out.println("Invalid URL for wholesalers. Program aborted.");
			System.exit(1);
		}
		
		// set purchase key
		pch.setKey("4413secret");
		
		// download PO files from eFood server
		PODownloader dl = new PODownloader("http://localhost:4413/eFoods/backend/orders", "http://localhost:4413");
		Date now = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
		String dir = "./" + sdf.format(now) + "/";
		new File(dir).mkdir();
		
		try
		{
			String[] fns = dl.downloadPOs(dir);

			int id = Math.abs(now.hashCode());
			dl.setStatus("pending");
			AggOrderBean prcrm = agg.getAggregatedOrder(fns, id);
			pch.purchase(prcrm, dir + "rpt" + id + ".xml");
			dl.setStatus("purchased");
			//dl.setStatus("new"); //restore for testing
		}
		catch (Exception e)
		{
			System.out.println(e.getMessage() + " Program aborted.");
			System.exit(1);
		}
	}

}
