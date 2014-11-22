package util;

import java.util.regex.*;

import javax.servlet.http.HttpServletRequest;

public class Route {

	public static final String METHOD_GET = "GET";
	public static final String METHOD_POST = "POST";
	public static final String METHOD_ANY = "ANYTHING";

	private String urlPattern;
	private String destination;
	private String method;
	private int destinationType;
	private boolean requireAuthentication;
	private Matcher matcher;
	private int identifier;

	/**
	 * @return the matcher
	 */
	public Matcher getMatcher() {
		return matcher;
	}

	public Route(String urlPattern, String destination, String method, int identifier,
			boolean requireAuthentication) {
		super();
		this.urlPattern = urlPattern;
		this.destination = destination;
		this.method = method;
		this.identifier = identifier;
		this.requireAuthentication = requireAuthentication;
	}

	/**
	 * @return the identifier
	 */
	public int getIdentifier() {
		return identifier;
	}

	/**
	 * @param identifier the identifier to set
	 */
	public void setIdentifier(int identifier) {
		this.identifier = identifier;
	}

	/**
	 * @return the urlPattern
	 */
	public String getUrlPattern() {
		return urlPattern;
	}

	/**
	 * @return the destination
	 */
	public String getDestination() {
		return destination;
	}

	/**
	 * @return the destinationType
	 */
	public int getDestinationType() {
		return destinationType;
	}

	/**
	 * @return the method
	 */
	public String getMethod() {
		return method;
	}

	/**
	 * @return the requireAuthentication
	 */
	public boolean isRequireAuthentication() {
		return requireAuthentication;
	}

	public boolean match(HttpServletRequest request) {
		Matcher m = Pattern.compile(this.urlPattern).matcher(request.getPathInfo());
		if (m.matches() && verifyMethod(request.getMethod())) {
			this.matcher = m;
			return true;
		} else {
			return false;
		}
	}

	private boolean verifyMethod(String m) {
		if (m == null || this.method.equals(METHOD_ANY)
				|| this.method.equals(m))
			return true;
		else
			return false;

	}

}
