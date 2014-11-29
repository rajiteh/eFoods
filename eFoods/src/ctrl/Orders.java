package ctrl;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.transform.stream.StreamResult;

import model.POFileWrapper;
import model.POFilesWrapper;
import util.PurchaseOrderFiles;
import util.PurchaseOrderFiles.PurchaseOrderFile;
import util.Route;

public class Orders extends BaseCtrl {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public static final int ROUTE_ALL_NEW = 0x0a;
	public static final int ROUTE_UPDATE_STATUS = 0x0b;
	public static final int ROUTE_NUKE = 0x0c;
	public static final String BASE_PATH = "/PurchaseOrders/";
	
	

	@Override
	protected synchronized void processRequest(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		Route route = getRoute(request);		
		String basePath = request.getServletContext().getRealPath(BASE_PATH);
		String baseUrl = request.getContextPath() + BASE_PATH;		
		PurchaseOrderFiles pofs; 
		
		switch(route.getIdentifier()) {
		case ROUTE_ALL_NEW:
			pofs = new PurchaseOrderFiles(basePath);
			List<PurchaseOrderFile> pofList = pofs.getOrdersByStatus(PurchaseOrderFile.STATUS_NEW);
			List<POFileWrapper> pofWrapperList = new ArrayList<POFileWrapper>();
			for(PurchaseOrderFile pof: pofList) {
				pofWrapperList.add(new POFileWrapper(baseUrl + pof.getFileNameOnly(),
						pof.getOrderId()));
			}
			POFilesWrapper pofsWrapper = new POFilesWrapper(pofWrapperList);
			String xmlOut = getXML(pofsWrapper);
			response.getWriter().write(xmlOut);
		
			break;
		case ROUTE_UPDATE_STATUS:
			int orderId = Integer.parseInt(route.getMatcher().group("orderId"));
			String status = request.getParameter("status");
			pofs = new PurchaseOrderFiles(basePath);
			pofs.updateStatus(orderId, status);
			break;
		case ROUTE_NUKE:
				pofs = new PurchaseOrderFiles(basePath);
				pofs.nuke();
			break;
		default:
			throw new ServletException("Routing configuration error.");
		}

	}
	
	private String getXML(Object wrapper) throws Exception {
		StringWriter sWriter = null;
		try {
			JAXBContext jaxbCtx = JAXBContext.newInstance(wrapper
					.getClass());
			Marshaller marshaller = jaxbCtx.createMarshaller();
			
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT,
					Boolean.TRUE);
			sWriter = new StringWriter();
			
			marshaller.marshal(wrapper, new StreamResult(sWriter));
			
			// add the stylesheet
			StringBuffer sb = new StringBuffer(sWriter.toString());
			
			
			return sb.toString();
		} catch (Exception e) {
			throw e;
		} finally {
			if (sWriter != null)
				sWriter.close();
		}
		

	}

}
