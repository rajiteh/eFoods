package ctrl;

import java.io.IOException;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.tomcat.util.codec.binary.Base64;

import ctrl.BaseCtrl.PagingHelper;
import util.Route;
import util.SSOAuthenticator;
import model.CartModel;
import model.EFoods;
import model.ItemBean;
import model.ItemDAO;

/**
 * Servlet implementation class Cart
 */

public class Misc extends BaseCtrl implements Servlet {
	private static final long serialVersionUID = 1L;
	public static final int ERROR_PAGE = 0x0a;
    /**
     * @see BaseCtrl#BaseCtrl()
     */
    public Misc() {
        super();
        // TODO Auto-generated constructor stub
    }


	@Override
	protected void processRequest(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		Route route = getRoute(request);
		SSOAuthenticator auth = (SSOAuthenticator) getAuthenticator(request);
		int identifier;
		if (route == null) {
			identifier = ERROR_PAGE;
		} else {
			identifier = route.getIdentifier();
		}
		switch(identifier) {
		case ERROR_PAGE:
			String errorMessage;
			String encodedError = (String) request.getAttribute("encodedError");
			if (encodedError == null) {
				try {
					encodedError = route.getMatcher().group("Base64EncodedMessage");
					errorMessage= new String(Base64.decodeBase64(encodedError));
				} catch (Exception e) {
					errorMessage = "Something went wrong somewhere! :(";	
				}
			} else {
				errorMessage= new String(Base64.decodeBase64(encodedError));	
			}
			
			request.setAttribute("errorMessage", errorMessage);
			request.getRequestDispatcher("/partials/_serverError.jspx").forward(
					request, response);
			break;
		default:
			throw new ServletException("Uh oh! We shouldn't be here!");
		}
		
	}


	
	
	
	

}
