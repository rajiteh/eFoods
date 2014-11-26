package filter;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.DispatcherType;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;

import util.SSOAuthenticator;
import model.CartModel;
import model.CartModel.CartItem;
import model.EFoods;
import model.ItemBean;
import model.ItemDAO;

/**
 * Servlet Filter implementation class CrossSell
 */
@WebFilter(dispatcherTypes = { DispatcherType.REQUEST, DispatcherType.FORWARD, DispatcherType.INCLUDE }, urlPatterns = { "/backend/cart", "/partials/_item.jspx" })
@SuppressWarnings("unchecked")
public class CrossSell implements Filter {
	
	@SuppressWarnings("serial")
	private static Map<String, String> TO_CROSS_SELL = new HashMap<String, String>() {
		{
			put("0905A044", "0905A123"); //Add cross sell items <ItemAddedToCart,ItemToCrossSell>
			put("0905A112", "0905A127"); 
			put("0905A123", "0905A112"); 
			put("0905A127", "0905A044"); 
		}
	}; 
	
	public static String ACTIVE_CROSS_SELLS_MAP_KEY = "com.eFoods.filter.ACSMap";

	/**
	 * Default constructor.
	 */
	public CrossSell() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see Filter#destroy()
	 */
	public void destroy() {
		// TODO Auto-generated method stub
	}

	/**
	 * @see Filter#doFilter(ServletRequest, ServletResponse, FilterChain)
	 */
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		// TODO Auto-generated method stub
		// place your code here
		try {
			System.out.println("Filter init");
			HttpServletRequest httpRequest = (HttpServletRequest) request;
			HttpServletResponse httpResponse = (HttpServletResponse) response;
			if (!(httpRequest == null || httpResponse == null)) {
				if (httpRequest.getPathInfo() != null && httpRequest.getPathInfo().matches("^/cart(/)?"))
					filterCart(httpRequest, httpResponse, chain);
				else 
					filterItem(httpRequest, httpResponse, chain);
			} else {
				chain.doFilter(request, response);
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
			chain.doFilter(request, response);	
		}
	}

	/**
	 * @see Filter#init(FilterConfig)
	 */
	public void init(FilterConfig fConfig) throws ServletException {
		// TODO Auto-generated method stub
	}
	
	private void filterCart(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
		if (request.getMethod().equals("POST")) {
			System.out.println("Filter cart init moment");
			try {
				int quantity = Integer.parseInt(request.getParameter("qty"));
				String number = (String) request.getParameter("item");
				if (!number.equals("")) {
					Map<String, String> activeCrossSells = getCrossSellMap(request);
					if (quantity > 0) { // Added to cart
						System.out.println("Added to cart moment");
						if (TO_CROSS_SELL.get(number) != null)
							activeCrossSells.put(number, TO_CROSS_SELL.get(number));
					} else {
						System.out.println("Removed from cart moment");
						activeCrossSells.remove(number);
					}
					setCrossSellMap(request, activeCrossSells);
				} else {
					throw new Exception("Empty item number sent from client");
				}
			} catch (Exception e) {
				System.out.println(e.getMessage());
				e.printStackTrace();
			}
		}
		chain.doFilter(request, response);
	}
	
	private void filterItem(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
		if (request.getMethod().equals("GET")) {
			try {
				CartItem ci = (CartItem) request.getAttribute("cartItem");
				EFoods model = (EFoods) request.getServletContext().getAttribute(ctrl.Front.MODEL_KEY);
				
				Map<String, String> activeCrossSells = getCrossSellMap(request);
				
				if (ci != null && activeCrossSells != null) {	
					String crossSellThis = activeCrossSells.get(ci.getItem().getNumber());
					if (crossSellThis != null) {
						ItemBean crossSellItem = model.items(crossSellThis, ItemDAO.CAT_ALL, ItemDAO.PAGE_ALL, ItemDAO.LIMIT_ALL, ItemDAO.NO_FILTER).get(0);
						request.setAttribute("crossSellItem", crossSellItem);
						RequestDispatcher rd = request.getRequestDispatcher("/partials/_crossSellItem.jspx");
						if (request.getDispatcherType().equals(DispatcherType.FORWARD))
							rd.forward(request, response);
						else if (request.getDispatcherType().equals(DispatcherType.INCLUDE)) 
							rd.include(request, response);
						return;
					}					
				}
			} catch (Exception e) {
				System.out.println(e.getMessage());
				e.printStackTrace();
			}
		}
		chain.doFilter(request, response);
	}
	
	private Map<String,String> getCrossSellMap(HttpServletRequest request) {
		Map<String, String> csm = (HashMap<String,String>) request
				.getSession().getAttribute(ACTIVE_CROSS_SELLS_MAP_KEY);
		return csm == null ? new HashMap<String, String>() : csm;
	}
	
	private void setCrossSellMap(HttpServletRequest request, Map<String,String> csm) {
		request.getSession().setAttribute(ACTIVE_CROSS_SELLS_MAP_KEY, csm);
	}

}
