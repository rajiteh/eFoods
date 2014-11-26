package ctrl;

import java.io.IOException;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.tomcat.util.codec.binary.Base64;

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
					String state = ((String) request.getSession().getAttribute("LAST_STATE"));
					if (state == null || state.startsWith("backend/error")) { state = ""; }
					String rdr = (request.getContextPath().length() == 0 ? "/" : request.getContextPath()) + "#!" + state;
					System.out.println("Redirecting to: " + rdr);
					response.sendRedirect(rdr);
					return;
				} else {
					throw new Exception("Sorry! We could not authenticate you at this moment.");
				}
			} catch (Exception e1) {
				System.out.println(e1.getMessage());
				e1.printStackTrace();
				String encodedError = Base64.encodeBase64String(e1.getMessage().getBytes());
				//Debug
				String rdr = (request.getContextPath().length() == 0 ? "/" : request.getContextPath()) + "#!backend/error/" + encodedError;;
				System.out.println("Redirecting to: " + rdr);
				response.sendRedirect(rdr);
				//throw new ServletException(e1.getMessage());
			}
			break;
		case ROUTE_LOGOUT:
			try {
				auth.logout(request);
				if (auth.getUser(request) == null) {
					System.out.println("Login: Logged out");	
				} else {
					throw new Exception("Could not log out");
				};
				
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
