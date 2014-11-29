package util;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

public class Router {

	public static final String REQUEST_ROUTE_KEY = "request-route";
	
	List<Route> routes;

	public Router() {
		this.routes = new ArrayList<Route>();
	}

	public void addRoute(Route route) {
		routes.add(route);
	}

	/**
	 * 
	 * @param request
	 * @return the route with respect to the request
	 */
	public Route getRoute(HttpServletRequest request) {
		for (Route r : routes)
			if (r.match(request)){
				request.setAttribute(REQUEST_ROUTE_KEY, r);
				return r;
			}
				
		return null;
	}

	/**
	 * @return the routes
	 */
	public List<Route> getRoutes() {
		return routes;
	}

}
