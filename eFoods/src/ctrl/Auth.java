package ctrl;

import java.io.IOException;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import model.UserBean;
import util.Authenticator;
import util.Route;
import util.SSOAuthenticator;

/**
 * Servlet implementation class Login
 */
public class Auth extends BaseCtrl implements Servlet {
	private static final long serialVersionUID = 1L;
	
	public static final int ROUTE_INITAL = 0x0a;
	public static final int ROUTE_AUTHENTICATE = 0x0b;
	public static final int ROUTE_LOGOUT = 0x0c;
	public static final int ROUTE_USER_BADGE = 0x0d;
      
    /**
     * @see BaseCtrl#BaseCtrl()
     */
    public Auth() {
        super();
        // TODO Auto-generated constructor stub
    }

	protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		SSOAuthenticator auth = (SSOAuthenticator) getAuthenticator(request);
		Route route = getRoute(request);
		switch(route.getIdentifier()) {
		case ROUTE_INITAL:
			try {
				request.getSession().setAttribute("LAST_STATE", request.getParameter("state"));
				auth.SSORedirect(request, response);
			} catch (Exception e2) {
				e2.printStackTrace();
				throw new ServletException(e2.getMessage());
			}
			break;
		case ROUTE_AUTHENTICATE:
			try {
				if (auth.login(request, null, null)) {
					UserBean usr = auth.getUser(request);
					System.out.println("Login: Successfully authenticated as " + usr.getName());
					response.sendRedirect(request.getContextPath() + "#!" + ((String) request.getSession().getAttribute("LAST_STATE")));
					request.getSession().setAttribute("LAST_STATE", null);
					return;
				} else {
					throw new Exception("Sorry! We could not authenticate you at this moment.");
				}
			} catch (Exception e1) {
				System.out.println(e1.getMessage());
				e1.printStackTrace();
				request.getSession().setAttribute(ctrl.Front.LAST_ERROR_KEY , e1.getMessage());
				response.sendRedirect(request.getContextPath());
				//throw new ServletException(e1.getMessage());
			}
			break;
		case ROUTE_LOGOUT:
			try {
				auth.logout(request);
				System.out.println("Login: Logged out");
			} catch (Exception e) {
				e.printStackTrace();
				throw new ServletException(e.getMessage());
				
			}
			break;
		case ROUTE_USER_BADGE:
			request.setAttribute("currentUser", auth.getUser(request));
			request.getRequestDispatcher("/partials/_userBadge.jspx").forward(
					request, response);
			break;
		default:
			throw new ServletException("Not sure whats happening here...");
		}
		
	}

}
