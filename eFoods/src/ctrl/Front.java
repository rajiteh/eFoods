package ctrl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.tomcat.util.codec.binary.Base64;

import model.*;
import util.*;

/**
 * Servlet implementation class Front
 */
@WebServlet("/backend/*")
@SuppressWarnings("serial")
public class Front extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	//Context objects
	public static final String MODEL_KEY = "com.eFoods.ctrl.Front.EFOODS";
	private static final String ROUTER_KEY = "com.eFoods.ctrl.Front.ROUTER";
	public static final String SSO_AUTHENTICATOR_KEY = "com.eFoods.ctrl.Front.HTTP_AUTHENTICATOR";
	
	
	//SSO Configuration
	//private static final String SSO_AUTHENTICATOR_URL = "http://localhost:9000/";
	private static final String SSO_AUTHENTICATOR_URL = "https://www.cse.yorku.ca/~cse11011/4413/auth/";
	private static final String SSO_RECIEVER_URL = "backend/login/authenticate";
	private static final String SSO_SHARED_KEY = "7fcbc0e27e7bb31e4ad7a39677eebdaf6b94c9db7dcdb2ebf25791298609a4a4";
	private static final List<String> SSO_ADMINS = new ArrayList<String>() {{
		add("cse11011"); //List of admin users
		add("cse13195");
	}};
	
	
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
			auth = new SSOAuthenticator(SSO_AUTHENTICATOR_URL, SSO_RECIEVER_URL, SSO_SHARED_KEY, SSO_ADMINS);
		} catch (Exception e) {
			throw new ServletException(e.getMessage());
		}
		
		//Setting up routes
		Router appRouter = new Router();
		
		
		appRouter.addRoute(new Route("^/category(/)?$","Category", Route.METHOD_GET, Category.ROUTE_ALL, false));
		//appRouter.addRoute(new Route("^/category/(?<catId>[0-9]+)(/)?$","Category", Route.METHOD_GET, Category.ROUTE_BY_ID, false));
		appRouter.addRoute(new Route("^/category/(?<catId>[0-9]+)(/)?$","Item", Route.METHOD_GET, Item.ROUTE_BY_CATEGORY, false));
		
		appRouter.addRoute(new Route("^/item(/)?$","Item", Route.METHOD_GET, Item.ROUTE_ALL,false));
		appRouter.addRoute(new Route("^/item/(?<itemNumber>[0-9a-zA-Z]+)/partial(/)?$","Item", Route.METHOD_GET, Item.ROUTE_BY_NUMBER,false));
		appRouter.addRoute(new Route("^/item/(?<itemNumber>[0-9a-zA-Z]+)(/)?$","Item", Route.METHOD_GET, Item.ROUTE_BY_NUMBER_FULL,false));
		
		appRouter.addRoute(new Route("^/search(/)?$","Item", Route.METHOD_GET, Item.ROUTE_BY_SEARCH,false));

		
		appRouter.addRoute(new Route("^/login(/)?$","Auth", Route.METHOD_GET, Auth.ROUTE_INITAL, false));
		appRouter.addRoute(new Route("^/login/authenticate(/)?$","Auth", Route.METHOD_GET, Auth.ROUTE_AUTHENTICATE, false));
		appRouter.addRoute(new Route("^/logout(/)?$","Auth", Route.METHOD_GET, Auth.ROUTE_LOGOUT, true));
		appRouter.addRoute(new Route("^/user(/)?$","Auth", Route.METHOD_GET, Auth.ROUTE_USER_BADGE, false));
		
		appRouter.addRoute(new Route("^/cart(/)?$","Cart", Route.METHOD_GET, Cart.ROUTE_ALL, false));
		appRouter.addRoute(new Route("^/cart(/)?$","Cart", Route.METHOD_POST, Cart.ROUTE_MANIPULATE, false));
		appRouter.addRoute(new Route("^/cart/checkout(/)?$","Cart", Route.METHOD_POST, Cart.ROUTE_CHECKOUT, true));
		appRouter.addRoute(new Route("^/cart/history(/)?$","Cart", Route.METHOD_GET, Cart.ROUTE_HISTORY, true));
		appRouter.addRoute(new Route("^/cart/success(/)?$","Cart", Route.METHOD_GET, Cart.ROUTE_SUCCESS, true));
		appRouter.addRoute(new Route("^/cart/badge(/)?$","Cart", Route.METHOD_GET, Cart.ROUTE_BADGE, false));
		
		
		appRouter.addRoute(new Route("^/error/(?<Base64EncodedMessage>.*)?","Misc", Route.METHOD_GET, Misc.ERROR_PAGE, false));
		
		appRouter.addRoute(new Route("^/analytics(/)?$","AnalysisCtrl", Route.METHOD_ANY, AnalysisCtrl.ANALYTICS_PAGE, true, true));
		
		appRouter.addRoute(new Route("^/orders(/)?$","Orders", Route.METHOD_GET, Orders.ROUTE_ALL_NEW, false));
		
		//POST request to /orders/<order-id> url with parameter "state" being one of "new, pending, purchased" will change the status of it.
		appRouter.addRoute(new Route("^/orders/(?<orderId>[0-9]+)(/)?$","Orders", Route.METHOD_POST, Orders.ROUTE_UPDATE_STATUS, false));
		//Poking the context
		config.getServletContext().setAttribute(MODEL_KEY, model);
		config.getServletContext().setAttribute(ROUTER_KEY, appRouter);
		config.getServletContext().setAttribute(SSO_AUTHENTICATOR_KEY, auth);
		
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		// ensure to create a session whenever a fresh request comes
		request.getSession();
		
		Router router =  (Router) request.getServletContext().getAttribute(ROUTER_KEY);
		Authenticator httpAuth = (SSOAuthenticator) request.getServletContext().getAttribute(SSO_AUTHENTICATOR_KEY);
		
		Route route;
		try {
			if ((route = router.getRoute(request)) != null) {
				if (!route.isRequireAuthentication() || httpAuth.isAuthenticated(request)) {
					System.out.println("Serving Route: " + route.getMethod() + " " + request.getPathInfo() + " (" + route.getUrlPattern() + ") " +
							(route.isRequireAuthentication() ? " (Authenticated as " + ((SSOAuthenticator) httpAuth).getUser(request).getName() + ")": ""));
					request.getServletContext().getNamedDispatcher(route.getDestination()).forward(request, response);
				} else {
					throw new Exception("You are not authorized to access this page. Please login.");
				}
			} else {
				System.out.println("Route not found!: " + request.getPathInfo());
				throw new Exception("Requested path is not found.");
			}
		} catch (Exception e) {
			System.out.println("Front controller exception! : " + e.getMessage());
			String encodedError = Base64.encodeBase64String(e.getMessage().getBytes());
			if(request.getParameter("main") != null) {
				System.out.println("Routing via Misc.");
				request.setAttribute("encodedError", encodedError);
				request.getServletContext().getNamedDispatcher("Misc").forward(request, response);
			} else if (request.getParameter("ajax") != null) {
				 response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				 response.setContentType("application/json");
				 response.getOutputStream().print("{\"error\": \"" + e.getMessage().replaceAll("\"", "\\\"") + "\"}");
			} else {
				String rdr = (request.getContextPath().length() == 0 ? "/" : request.getContextPath()) + "#!backend/error/" + encodedError;;
				System.out.println("Redirecting to: " + rdr);
				response.sendRedirect(rdr);
			}
			//throw new ServletException(e);
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}
