package ctrl;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import model.UserBean;
import util.Route;
import util.SSOAuthenticator;

/**
 * Servlet implementation class Login
 */
public class Auth extends BaseCtrl implements Servlet {
	private static final long serialVersionUID = 1L;
	
	/*
	 * Route definitions for this controller
	 */
	public static final int ROUTE_INITAL = 0x0a;
	public static final int ROUTE_AUTHENTICATE = 0x0b;
	public static final int ROUTE_LOGOUT = 0x0c;
	public static final int ROUTE_USER_BADGE = 0x0d;
      

	protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
		SSOAuthenticator auth = (SSOAuthenticator) getAuthenticator(request);
		Route route = getRoute(request);
		switch(route.getIdentifier()) {
		case ROUTE_INITAL:
			request.getSession().setAttribute("LAST_STATE", request.getParameter("state"));
			auth.SSORedirect(request, response);
			break;
		case ROUTE_AUTHENTICATE:
			if (auth.login(request, null, null)) {
				UserBean usr = auth.getUser(request);
				System.out.println("Login: Successfully authenticated as " + usr.getName());
				String state = ((String) request.getSession().getAttribute("LAST_STATE"));
				if (state == null || state.startsWith("backend/error")) { state = ""; }
				String rdr = (request.getContextPath().length() == 0 ? "/" : request.getContextPath()) + "#!" + state;
				System.out.println("Redirecting to: " + rdr);
				response.sendRedirect(rdr);
			} else {
				throw new Exception("Sorry! We could not authenticate you at this moment.");
			}
			break;
		case ROUTE_LOGOUT:
			auth.logout(request);
			System.out.println("Login: Logged out");
			break;
		case ROUTE_USER_BADGE:
			request.setAttribute("currentUser", auth.getUser(request));
			request.getRequestDispatcher("/partials/_userBadge.jspx").forward(
					request, response);
			break;
		default:
			throw new ServletException("Route configuration error.");
		}
		
	}

}
