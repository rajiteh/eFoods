package ctrl;

import java.util.List;



import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import util.Route;
import util.SSOAuthenticator;
import model.*;


@WebServlet(name = "Item", displayName = "Item")
public class Item extends BaseCtrl {
	private static final long serialVersionUID = 1L;

	/*
	 * Route definitions for this controller
	 */
	public static final int ROUTE_ALL = 0xff;
	public static final int ROUTE_BY_NUMBER = 0x0b;
	public static final int ROUTE_BY_CATEGORY = 0x0c;
	public static final int ROUTE_BY_SEARCH = 0x0d;
	public static final int ROUTE_BY_NUMBER_FULL = 0x0e;

	/**
	 * Process the request and response. Uses the model, authenticator, route
	 * to manage the request appropriately.
	 */
	protected void processRequest(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		EFoods model = getModel(request);
		Route route = getRoute(request);
		PagingHelper paging = getPagination(request);
		SSOAuthenticator auth = (SSOAuthenticator) getAuthenticator(request);
		CartModel cart = CartModel.getInstance(request ,auth);
		List<ItemBean> results = null;
		int routeType;
		if (route == null)
			routeType = -1;
		else
			routeType = route.getIdentifier();


		switch (routeType) {
		case ROUTE_BY_CATEGORY:
			int catId = Integer.parseInt(route.getMatcher().group("catId"));
			CategoryBean cat = model.categories(catId).get(0);
			
			results = model.items(ItemDAO.NUMBER_ALL, catId, paging.getPage(), paging.getLimit(), ItemDAO.NO_FILTER);
			request.setAttribute("cartItems", cart.mappedCartItems());
			request.setAttribute("category", cat);
			request.setAttribute("results", results);
			request.getRequestDispatcher("/partials/_items.jspx").forward(request,
					response);
			break;
		case ROUTE_BY_NUMBER_FULL:
			String number = route.getMatcher().group("itemNumber");
			results = model.items(number, ItemDAO.CAT_ALL, ItemDAO.PAGE_ALL, ItemDAO.LIMIT_ALL, ItemDAO.NO_FILTER);
			request.setAttribute("cartItems", cart.mappedCartItems());
			request.setAttribute("category", results.get(0).getCategory());
			request.setAttribute("results", results);
			request.getRequestDispatcher("/partials/_items.jspx").forward(request,
					response);
			break;
		case ROUTE_BY_NUMBER:
			String number2 = route.getMatcher().group("itemNumber");
			ItemBean item = model.items(number2, ItemDAO.CAT_ALL, ItemDAO.PAGE_ALL, ItemDAO.LIMIT_ALL, ItemDAO.NO_FILTER).get(0);
			request.setAttribute("cartItem", cart.getCartItemFor(item));
			request.setAttribute("item", item);
			request.getRequestDispatcher("/partials/_item.jspx").forward(request,
					response);
			break;
		case ROUTE_ALL:
			results = model.items(ItemDAO.NUMBER_ALL, ItemDAO.CAT_ALL, paging.getPage(), paging.getLimit(), ItemDAO.NO_FILTER);
			request.setAttribute("results", results);
			request.getRequestDispatcher("/partials/_items.jspx").forward(request,
					response);
			break;
		case ROUTE_BY_SEARCH:
			String filter = request.getParameter("filter");
			System.out.println(filter);
			results = model.items(ItemDAO.NUMBER_ALL, ItemDAO.CAT_ALL, paging.getPage(), paging.getLimit(), filter);
			request.setAttribute("results", results);
			request.getRequestDispatcher("/partials/_items.jspx").forward(request,
					response);
			break;
		default:
			throw new Exception("routing error! not suppose to be here");
		}
	}



}
