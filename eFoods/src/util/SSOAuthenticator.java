package util;

import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import model.UserBean;

public class SSOAuthenticator extends Authenticator {

	private static final String SESSION_KEY_PREFIX = "com.eFoods.util.Authentication.SSO";
	private String sessionKey;
	private String ssoEndpoint;

	public SSOAuthenticator(String ssoEndpoint) throws Exception {
		if (ssoEndpoint == null || ssoEndpoint.length() < 0)
			throw new Exception("Endpoint must not be blank!");
		this.ssoEndpoint = ssoEndpoint;
		this.sessionKey = SESSION_KEY_PREFIX + "." + ssoEndpoint.hashCode();
	}

	@Override
	public boolean isAuthenticated(HttpServletRequest request) {
		return (getUser(request) != null);
	}

	public UserBean getUser(HttpServletRequest request) {
		return ((UserBean) request.getSession().getAttribute(this.sessionKey));
	}

	@Override
	public boolean login(HttpServletRequest request, String username,
			String password) {

		request.getSession().setAttribute(this.sessionKey,
				new UserBean(username));

		return isAuthenticated(request);
	}

	@Override
	public void logout(HttpServletRequest request) throws Exception {
		HttpSession session = request.getSession(false);
		if (session != null) {
			Enumeration<String> e = session.getAttributeNames();
			while (e.hasMoreElements()) {
				String key = (String) e.nextElement();
				session.setAttribute(key, null);
			}
			session.invalidate();
		}
		if (isAuthenticated(request))
			throw new Exception("Could not log out successfully!");
	}

}
