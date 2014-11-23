package ctrl;

import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import model.*;
import util.*;

/**
 * Servlet implementation class Front
 */
@WebServlet("/backend/*")
public class Front extends HttpServlet {
	private static final long serialVersionUID = 1L;
	public static final String MODEL_KEY = "com.eFoods.ctrl.Front.EFOODS";
	private static final String ROUTER_KEY = "com.eFoods.ctrl.Front.ROUTER";
	public static final String SSO_AUTHENTICATOR_KEY = "com.eFoods.ctrl.Front.HTTP_AUTHENTICATOR";
	private static final String SSO_AUTHENTICATOR_URL = "https://www.cse.yorku.ca/~cse11011/4413/sso_endpoint";
	
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Front() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see Servlet#init(ServletConfig)
	 */
	public void init(ServletConfig config) throws ServletException {
		super.init();
		
		//Initializing Model
		EFoods model = new EFoods();
		
		//Initializing authentication module
		SSOAuthenticator auth;
		try {
			auth = new SSOAuthenticator(SSO_AUTHENTICATOR_URL);
		} catch (Exception e) {
			throw new ServletException("Authenticator init failure.");
		}
		
		//Setting up routes
		Router appRouter = new Router();
		
		
		appRouter.addRoute(new Route("^/category(/)?$","Category", Route.METHOD_GET, Category.ROUTE_ALL, false));
		appRouter.addRoute(new Route("^/category/(?<catId>[0-9]+)(/)?$","Category", Route.METHOD_GET, Category.ROUTE_BY_ID, false));
		appRouter.addRoute(new Route("^/category/(?<catId>[0-9]+)/items(/)?$","Item", Route.METHOD_GET, Item.ROUTE_BY_CATEGORY, false));
		
		appRouter.addRoute(new Route("^/item(/)?$","Item", Route.METHOD_GET, Item.ROUTE_ALL,false));
		appRouter.addRoute(new Route("^/item/(?<itemNumber>[0-9a-zA-Z]+)(/)?$","Item", Route.METHOD_GET, Item.ROUTE_BY_NUMBER,false));
		
		appRouter.addRoute(new Route("^/login(/)?","Auth", Route.METHOD_GET, Auth.ROUTE_INITAL, false));
		appRouter.addRoute(new Route("^/login/authorize(/)?","Auth", Route.METHOD_GET, Auth.ROUTE_AUTHORIZE, false));
		appRouter.addRoute(new Route("^/logout(/)?","Auth", Route.METHOD_GET, Auth.ROUTE_LOGOUT, true));
		appRouter.addRoute(new Route("^/user(/)?","Auth", Route.METHOD_GET, Auth.ROUTE_USER_BADGE, false));
		
		appRouter.addRoute(new Route("^/cart(/)?","Cart", Route.METHOD_GET, Cart.ROUTE_ALL, false));
		appRouter.addRoute(new Route("^/cart(/)?","Cart", Route.METHOD_POST, Cart.ROUTE_MANIPULATE, false));
		appRouter.addRoute(new Route("^/cart/checkout(/)?","Cart", Route.METHOD_POST, Cart.ROUTE_CHECKOUT, true));
		appRouter.addRoute(new Route("^/cart/history(/)?","Cart", Route.METHOD_GET, Cart.ROUTE_HISTORY, true));
		appRouter.addRoute(new Route("^/cart/badge(/)?","Cart", Route.METHOD_GET, Cart.ROUTE_BADGE, false));
		
		//Poking the context
		config.getServletContext().setAttribute(MODEL_KEY, model);
		config.getServletContext().setAttribute(ROUTER_KEY, appRouter);
		config.getServletContext().setAttribute(SSO_AUTHENTICATOR_KEY, auth);
		
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Router router =  (Router) request.getServletContext().getAttribute(ROUTER_KEY);
		Authenticator httpAuth = (SSOAuthenticator) request.getServletContext().getAttribute(SSO_AUTHENTICATOR_KEY);
		
		Route route;
		try {			
			if ((route = router.getRoute(request)) != null) {
				if (!route.isRequireAuthentication() || httpAuth.isAuthenticated(request)) {
					System.out.println("Serving Route: " + request.getPathInfo() + 
							(route.isRequireAuthentication() ? " (Authenticated as " + ((SSOAuthenticator) httpAuth).getUser(request).getName() + ")": ""));
					request.getServletContext().getNamedDispatcher(route.getDestination()).forward(request, response);
				} else {
					System.out.println("Permission Denied");
					request.getRequestDispatcher("/partials/_permissionDenied.jspx").forward(
							request, response);
				}
			} else {
				
				System.out.println("Route not found!: " + request.getPathInfo());
				request.getRequestDispatcher("/partials/_notFound.jspx").forward(
						request, response);
			}
		} catch (Exception e) {
			
			throw e;
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}
