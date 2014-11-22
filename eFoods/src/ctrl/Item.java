package ctrl;

import java.io.IOException;
import java.util.List;


import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import util.Route;
import model.*;

@WebServlet(name = "Item", displayName = "Item")
public class Item extends BaseCtrl {
	private static final long serialVersionUID = 1L;

	public static final int ROUTE_ALL = 0xff;
	public static final int ROUTE_BY_NUMBER = 0x0b;
	public static final int ROUTE_BY_CATEGORY = 0x0c;


	protected void processRequest(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		EFoods model = getModel(request);
		Route route = getRoute(request);
		PagingHelper paging = getPagination(request);
		List<ItemBean> results = null;
		int routeType;
		if (route == null)
			routeType = -1;
		else
			routeType = route.getIdentifier();

		try {

			switch (routeType) {
			case ROUTE_BY_CATEGORY:
				int catId = Integer.parseInt(route.getMatcher().group("catId"));
				CategoryBean cat = model.categories(catId).get(0);
				request.setAttribute("category", cat);
				results = model.items(ItemDAO.NUMBER_ALL, catId, paging.getPage(), paging.getLimit());
				break;
			case ROUTE_BY_NUMBER:
				String number = route.getMatcher().group("itemNumber");
				results = model.items(number, ItemDAO.CAT_ALL, paging.getPage(), paging.getLimit());
				break;
			case ROUTE_ALL:
			default:
				results = model.items(ItemDAO.NUMBER_ALL, ItemDAO.CAT_ALL, paging.getPage(), paging.getLimit());
			}

				

		} catch (Exception e) {
			System.out.println("Database error");
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
		request.setAttribute("results", results);
		request.getRequestDispatcher("/partials/_item.jspx").forward(request,
				response);
	}



}
