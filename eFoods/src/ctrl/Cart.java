package ctrl;

import java.io.IOException;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ctrl.BaseCtrl.PagingHelper;
import util.Route;
import util.SSOAuthenticator;
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
		
		switch(route.getIdentifier()) {
		case ROUTE_ALL:
			request.setAttribute("cartItems", cart.getCartItems());
			request.setAttribute("total", cart.getTotal());
			request.setAttribute("shipping", cart.getShipping());
			request.setAttribute("tax", cart.getTax());
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
				cart.manipulateCart(item, newQty);
			} catch (Exception e) {
				e.printStackTrace();
				throw new ServletException(e.getMessage());
			}
			break;
		case ROUTE_CHECKOUT:
			//Create PO Here
			break;
		case ROUTE_HISTORY:
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
