package ctrl;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import util.Authenticator;
import util.Route;
import util.Router;
import model.*;


public abstract class BaseCtrl extends HttpServlet
{
	private static final long serialVersionUID = 1L;
	
    /**
     * @see HttpServlet#HttpServlet()
     */
    public BaseCtrl() {
        super();
        
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		processRequest(request, response);
	}

		
	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}
	
	protected abstract void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException; 
	
	//Static helpers
	
	protected static EFoods getModel(HttpServletRequest request) {
		return (EFoods) request.getServletContext().getAttribute(ctrl.Front.MODEL_KEY);
	}
	
	protected static Route getRoute(HttpServletRequest request) {
		return (Route) request.getAttribute(Router.REQUEST_ROUTE_KEY);
	}
	
	protected static Authenticator getAuthenticator(HttpServletRequest request) {
		return (Authenticator) request.getServletContext().getAttribute(ctrl.Front.SSO_AUTHENTICATOR_KEY);
	}

}
