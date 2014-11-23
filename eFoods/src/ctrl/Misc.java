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
		
		switch(route.getIdentifier()) {
		case ERROR_PAGE:
			String encodedMsg = route.getMatcher().group("Base64EncodedMessage");
			String errorMessage;
			if (encodedMsg != null) {
				errorMessage= new String(Base64.decodeBase64(encodedMsg));	
			} else {
				errorMessage = "Something went wrong somewhere! :(";
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
