package ctrl;

import java.io.IOException;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import util.Route;

public class Analytics extends BaseCtrl implements Servlet
{
	private static final long serialVersionUID = 1L;
	public static final int ANALYTICS_PAGE = 0x0a;
	
	@Override
	protected void processRequest(HttpServletRequest request,
	      HttpServletResponse response) throws ServletException, IOException
	{
		
		Route route = getRoute(request);
		switch(route.getIdentifier()) {
		case ANALYTICS_PAGE:
			try {
			// get analytic information from application scope
			Long avgAddItemTime = (Long) this.getServletContext().getAttribute("avgAddItemTime");
			Long avgCheckOutTime = (Long) this.getServletContext().getAttribute("avgCheckOutTime");
			if (avgAddItemTime == null) avgAddItemTime = new Long(0);
			if (avgCheckOutTime == null) avgCheckOutTime = new Long(0);
			// poke information into request scope
			request.setAttribute("avgAddItemTime", avgAddItemTime);
			request.setAttribute("avgCheckOutTime", avgCheckOutTime);
		
			// dispatch to JSPX
			request.getRequestDispatcher("/partials/_analytics.jspx").forward(request, response);
			} catch (Exception e) {
				e.printStackTrace();
				throw new ServletException(e.getMessage());
			}
			break;
		default:
			throw new ServletException("Uh oh! We shouldn't be here!");
		}
	}

}
