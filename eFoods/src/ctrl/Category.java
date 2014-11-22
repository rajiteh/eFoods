package ctrl;

import java.io.IOException;
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
	
	public static final int ROUTE_ALL = 0xff;
	public static final int ROUTE_BY_ID = 0x0a;

	protected void processRequest(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		System.out.println("Processing Category route");
		EFoods model = getModel(request);
		Route route = getRoute(request);
		List<CategoryBean> results = null;
		int routeType;
		if (route == null)
			routeType = -1;
		else
			routeType = route.getIdentifier();

		try {

			switch (routeType) {
			case ROUTE_BY_ID:
				int id = Integer.parseInt(route.getMatcher().group("catId"));
				results = model.categories(id);
				break;
			case ROUTE_ALL:
				results = model.categories(CategoryDAO.ID_ALL);
				break;
			default:
				throw new Exception("Unknown route. You done goofed up");
			}

		} catch (Exception e) {
			System.out.println("Database error");
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
		request.setAttribute("results", results);
		request.getRequestDispatcher("/Category.jspx").forward(
				request, response);

	}

}
