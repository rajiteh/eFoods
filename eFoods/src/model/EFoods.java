package model;

import java.io.File;
import java.io.FileWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.transform.stream.StreamResult;

import util.PurchaseOrderFiles;

public class EFoods {
	private CategoryDAO categoryDAO;
	private ItemDAO itemDAO;
	public EFoods() {
			categoryDAO = new CategoryDAO();
			itemDAO = new ItemDAO();
	}

	public List<CategoryBean> categories(int id) throws Exception {
		return categoryDAO.retrieve(id);
	}
	
	public List<ItemBean> items(String number, int catId, int page, int limit, String filter) throws Exception {
		return itemDAO.retrieve(number, catId, page, limit, filter);
	}
	
	/**
	 * Creates a purchase order based on the current items in the cart.
	 * 
	 * @param cart
	 * @param filepath
	 * @return the purchase order id
	 * @throws Exception
	 */
	public synchronized int createPurchaseOrder(CartModel cart, String filepath) throws Exception {
		PurchaseOrderFiles pofs = new PurchaseOrderFiles(filepath);
		int orderId = pofs.getNextOrderId();
		PurchaseOrderWrapper poWrapper = new PurchaseOrderWrapper(cart, orderId);
		StringWriter sWriter = null;
		FileWriter fWriter = null;
		try {
			JAXBContext jaxbCtx = JAXBContext.newInstance(poWrapper
					.getClass());
			Marshaller marshaller = jaxbCtx.createMarshaller();
			
			//marshaller.setProperty(Marshaller.JAXB_SCHEMA_LOCATION,
			//		"SIS.xsd");
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT,
					Boolean.TRUE);
			marshaller.setProperty(Marshaller.JAXB_FRAGMENT, Boolean.TRUE);

			sWriter = new StringWriter();
			sWriter.write("<?xml version='1.0'?>");
			sWriter.write("\n");
			sWriter.write("<?xml-stylesheet type='text/xsl' href='PurchaseOrders.xsl' ?>");
			sWriter.write("\n");
			marshaller.marshal(poWrapper, new StreamResult(sWriter));

			pofs.storeNewOrder(orderId, cart.getAccount().getName(), sWriter.toString());
			return orderId;
		} catch (Exception e) {
			throw e;
		} finally {
			if (fWriter != null)
				fWriter.close();
			if (sWriter != null)
				sWriter.close();
		}

	}



	 

}
