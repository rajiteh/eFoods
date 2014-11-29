package ctrl;

import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import util.Route;
import model.*;

@WebServlet(name = "Category", displayName = "Category")
public class Category extends BaseCtrl {
	private static final long serialVersionUID = 1L;
	
	/*
	 * Route definitions for this controller
	 */
	public static final int ROUTE_ALL = 0xff;
	public static final int ROUTE_BY_ID = 0x0a;

	protected void processRequest(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		EFoods model = getModel(request);
		Route route = getRoute(request);
		List<CategoryBean> results = null;
		int routeType;
		if (route == null)
			routeType = -1;
		else
			routeType = route.getIdentifier();

		switch (routeType) {
		case ROUTE_BY_ID:
			int id = Integer.parseInt(route.getMatcher().group("catId"));
			results = model.categories(id);
			break;
		case ROUTE_ALL:
			results = model.categories(CategoryDAO.ID_ALL);
			break;
		default:
			throw new ServletException("Route configuration error.");
		}

		request.setAttribute("results", results);
		request.getRequestDispatcher("/partials/_category.jspx").forward(
				request, response);

	}

}
