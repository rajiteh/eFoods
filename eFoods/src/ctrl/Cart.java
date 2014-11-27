package ctrl;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import util.PurchaseOrderFiles;
import util.Route;
import util.SSOAuthenticator;
import util.PurchaseOrderFiles.PurchaseOrderFile;
import model.CartModel;
import model.EFoods;
import model.ItemBean;
import model.ItemDAO;

/**
 * Servlet implementation class Cart
 */

public class Cart extends BaseCtrl implements Servlet {
	private static final long serialVersionUID = 1L;
	public static final int ROUTE_ALL = 0x0a;
	public static final int ROUTE_MANIPULATE = 0x0b;
	public static final int ROUTE_HISTORY = 0x0c;
	public static final int ROUTE_CHECKOUT = 0x0d;
	public static final int ROUTE_BADGE = 0x0e;
	public static final int ROUTE_SUCCESS = 0x0f;
       
    /**
     * @see BaseCtrl#BaseCtrl()
     */
    public Cart() {
        super();
        // TODO Auto-generated constructor stub
    }


	@Override
	protected void processRequest(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		Route route = getRoute(request);
		SSOAuthenticator auth = (SSOAuthenticator) getAuthenticator(request);
		CartModel cart = CartModel.getInstance(request ,auth);
		EFoods model = getModel(request);
		PagingHelper paging = getPagination(request);
		String filePath = request.getServletContext().getRealPath(Orders.BASE_PATH);
		switch(route.getIdentifier()) {
		case ROUTE_ALL:
			request.setAttribute("cartItems", cart.getCartItems());
			request.setAttribute("total", cart.getTotal());
			request.setAttribute("shipping", cart.getShipping());
			request.setAttribute("tax", cart.getTax());
			request.setAttribute("user", auth.getUser(request));
			request.getRequestDispatcher("/partials/_cart.jspx").forward(
					request, response);
			break;
		case ROUTE_MANIPULATE:
			String itemNumber;
			ItemBean item;
			int newQty;
			
			try {
				itemNumber = request.getParameter("item");
				newQty =  Integer.parseInt(request.getParameter("qty"));
				item = model.items(itemNumber, ItemDAO.CAT_ALL, paging.getPage(), paging.getLimit(), ItemDAO.NO_FILTER).get(0);
				if (item.getQty() <= newQty) {
					cart.manipulateCart(item, newQty);	
				} else {
					throw new Exception("There are not enough quantities to satisfy your order.");
				}
				
				// poke number of cart items into session scope, for listener
				request.getSession().setAttribute("cartItemsCount", cart.getCartItems().size());
			} catch (Exception e) {
				e.printStackTrace();
				throw new ServletException(e.getMessage());
			}
			break;
		case ROUTE_CHECKOUT:
			System.out.println("Checking out");
			// add code here
			try {
				
				if (cart.getCartItems().size() < 1) {
					throw new Exception("Can't checkout empty cart!");
				}
				int orderId = model.createPurchaseOrder(cart, filePath);
				// notify listener by poking attribute into session scope after client had checked out
				cart.clear();
				request.getSession().setAttribute("checkedOut", true);
				String rdr = (request.getContextPath().length() == 0 ? "/" : request.getContextPath()) + "#!backend/cart/success?order=" + orderId;
				response.sendRedirect(rdr);
			} catch (Exception e) {
				e.printStackTrace();
				throw new ServletException(e.getMessage());
			}
			
			break;
		case ROUTE_HISTORY:
			try {
				PurchaseOrderFiles pofs = new PurchaseOrderFiles(filePath);
				List<PurchaseOrderFile> pofList = pofs.getOrdersByUser(auth.getUser(request).getName());
				
				List<PurchaseOrderFile> pendingOrders = new ArrayList<PurchaseOrderFile>();
				List<PurchaseOrderFile> newOrders = new ArrayList<PurchaseOrderFile>();
				List<PurchaseOrderFile> purchasedOrders = new ArrayList<PurchaseOrderFile>();
				for(PurchaseOrderFile pof : pofList)
					if (pof.getStatus().equals(PurchaseOrderFile.STATUS_NEW))
						newOrders.add(pof);
					else if (pof.getStatus().equals(PurchaseOrderFile.STATUS_PENDING))
						pendingOrders.add(pof);
					else if (pof.getStatus().equals(PurchaseOrderFile.STATUS_PURCHASED))
						purchasedOrders.add(pof);
				
				request.setAttribute("pendingOrders", pendingOrders);
				request.setAttribute("newOrders", newOrders);
				request.setAttribute("purchasedOrders", purchasedOrders);
				request.getRequestDispatcher("/partials/_cartHistory.jspx").forward(
						request, response);	
			} catch (Exception e) {
				e.printStackTrace();
				throw new ServletException(e.getMessage());
			}
			
			break;
		case ROUTE_SUCCESS:
			request.getRequestDispatcher("/partials/_cartSuccess.jspx").forward(
					request, response);
			break;
		case ROUTE_BADGE:
			request.setAttribute("cartItemCount", cart.getCartItems().size());
			request.getRequestDispatcher("/partials/_cartBadge.jspx").forward(
					request, response);
			break;
		default:
			throw new ServletException("Uh oh! We shouldn't be here!");
		}
		
	}


	
	
	
	

}
