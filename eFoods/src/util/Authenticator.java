package util;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public abstract class Authenticator {

	protected String sessionKey;

	public abstract boolean isAuthenticated(HttpServletRequest request);

	public abstract boolean login(HttpServletRequest request, String username,
			String password) throws Exception;

	public abstract void logout(HttpServletRequest request) throws Exception;


}
