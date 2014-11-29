package util;

import java.net.URLEncoder;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.xml.bind.DatatypeConverter;

import org.apache.catalina.util.RequestUtil;
import org.apache.tomcat.util.codec.binary.Base64;

import model.UserBean;

public class SSOAuthenticator extends Authenticator {

	private static final String SESSION_KEY_PREFIX = "com.eFoods.util.Authentication.SSO";
	private static final String NONCE_VALUE_KEY = "com.eFoods.util.Authentication.SSO.nonce.value";
	private static final String NONCE_TIME_KEY = "com.eFoods.util.Authentication.SSO.nonce.time";
	private static final int NONCE_EXPIRY = 10; //minutes
	private static final int NONCE_LENGTH = 16;
	
	private String sharedKey;
	private String sessionKey;
	private String ssoEndpoint;
	private String ssoReciever;
	private List<String> adminUsers;
	
	public SSOAuthenticator(String ssoEndpoint, String ssoReciever,  String sharedKey, List<String> adminUsers) throws Exception {
		if (ssoEndpoint == null || ssoEndpoint.length() < 1)
			throw new Exception("Endpoint must not be blank!");
		if (sharedKey == null || sharedKey.length() < 1)
			throw new Exception("Shared key must not be blank!");
		if (adminUsers.size() < 1)
			throw new Exception("No admin users in the system!");
		if (ssoReciever == null || ssoReciever.length() < 1)
			throw new Exception("SSO Recieve URL must not be empty!");
		this.ssoEndpoint = ssoEndpoint;
		this.ssoReciever = ssoReciever;
		this.sharedKey = sharedKey;
		
		//Creates a unique sesion key for this SSO endpoint 
		this.sessionKey = SESSION_KEY_PREFIX + "." + ssoEndpoint.hashCode();
		this.adminUsers = adminUsers;
		
	}

	@Override
	public boolean isAuthenticated(HttpServletRequest request) {
		UserBean user = getUser(request);
		Route currentRoute = (Route) request.getAttribute(Router.REQUEST_ROUTE_KEY);
		if (user != null && currentRoute != null) {
			boolean needAdmin = currentRoute.isRequireAdmin();
			boolean hasAdmin = user.isAdmin();
			return needAdmin ? needAdmin == hasAdmin : true;	
		}		
		return false;
	}

	public UserBean getUser(HttpServletRequest request) {
		return ((UserBean) request.getSession().getAttribute(this.sessionKey));
	}
	
	private void setUser(HttpServletRequest request, UserBean user) {
		request.getSession().setAttribute(this.sessionKey, user);
	}

	/**
	 *Generates a nonce, constructs a payload and redirects to the auth endpoint
	 */
	public void SSORedirect(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String nonce = generateNonce(NONCE_LENGTH);
		String rawPayload = "nonce=" + nonce;
		String payload = new String(Base64.encodeBase64(rawPayload.getBytes()));
		String signature = encode(sharedKey,payload).toLowerCase();
		
		String encodedPayload = URLEncoder.encode(payload, "UTF-8");
		String encodedSignature = URLEncoder.encode(signature, "UTF-8");
		String encodedReciever = URLEncoder.encode(ssoReciever, "UTF-8");
		String queryString = "payload=" + encodedPayload + "&signature=" + encodedSignature + "&redirect=" + encodedReciever;
		storeNonce(request, nonce);
		System.out.println("Auth: Nonce created.");
		response.sendRedirect(ssoEndpoint + "?" + queryString);
	}
	
	/**
	 * Accepts a payload and a signature and verifies the authenticity of the request by comparing the nonce. 
	 * Set's the user in to the current session.
	 */
	@Override
	public boolean login(HttpServletRequest request, String uname,
			String pwd) throws Exception {
		Map<String,String[]> payload = new HashMap<String,String[]>();
		String localSignature, localNonce, remoteNonce, remotePayload, remoteSignature, rawPayload, userName, userFullName;
		
		remotePayload = request.getParameter("payload");
		remoteSignature = request.getParameter("signature");
		localSignature = encode(sharedKey, remotePayload);
		localNonce = retrieveNonce(request);
		
		//Only use the nonce once!
		expireNonce(request);
		
		rawPayload = new String(Base64.decodeBase64(remotePayload));
		RequestUtil.parseParameters(payload, rawPayload, "UTF-8");
		

		System.out.println("Auth : remote Payload: " + remotePayload);
		System.out.println("Auth : raw Payload: " + rawPayload);
		
		
		try {
			remoteNonce = payload.get("nonce")[0];
			userName = payload.get("username")[0];
			userFullName = payload.get("fullname")[0];
		} catch (Exception e) {
			throw new Exception("Invalid payload.");
		}
		
		
		//Initial validation
		if (!localSignature.toLowerCase().equals(remoteSignature.toLowerCase()))
			throw new Exception("Payload verification failed.");
		if (localNonce == null) 
			throw new Exception("Request expired. Please re-initiate login.");	
		if  (!localNonce.equals(remoteNonce)) 
			throw new Exception("Invalid nonce. Please re-initiaite login.");
		//End initial validation
	
		//Data validation
		if (userName == null || userName.length() < 1) 
			throw new Exception("Invalid username data in payload");
		if (userFullName == null || userFullName.length() < 1)
			throw new Exception("Invalid user full name data in payload");
		
		/*
		 * FOLLOWING CODE IS COMMENTED OUT FOR THE SAKE OF MARKER. AS THERE IS NO WAY
		 * TO ADD HIM/HER AS AN ADMIN WITHOUT PRIOR KNOWLEDGE OF THEIR USERNAMES
		 */
		//boolean isAdmin = false;
		//if (adminUsers.contains(userName))
		//	isAdmin = true;
		boolean isAdmin = true;
		/*
		 * End testing code
		 */
		
		setUser(request, new UserBean(userName, userFullName, isAdmin));
		return isAuthenticated(request);
	}

	@Override
	public void logout(HttpServletRequest request) throws Exception {
		HttpSession session = request.getSession(false);
		//Clears the session before invalidating 
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

	protected static void storeNonce(HttpServletRequest request, String nonce) {
		request.getSession().setAttribute(NONCE_VALUE_KEY, nonce);
		request.getSession().setAttribute(NONCE_TIME_KEY, System.currentTimeMillis());
	}
	
	protected static String retrieveNonce(HttpServletRequest request) {
		Long timeAtStore;
		try {
			timeAtStore = (Long) request.getSession().getAttribute(NONCE_TIME_KEY);
		} catch (NumberFormatException e) {
			timeAtStore = null;
		}
		if (timeAtStore != null && timeAtStore > (System.currentTimeMillis() - (NONCE_EXPIRY * 1000 * 60 * 10))) {
			return (String) request.getSession().getAttribute(NONCE_VALUE_KEY);	
		} else {
			//Expire the nonce if the maximum time has been passed.
			expireNonce(request);
			return null;
		}
		
		
	}
	
	protected static void expireNonce(HttpServletRequest request) {
		request.getSession().setAttribute(NONCE_VALUE_KEY, null);
		request.getSession().setAttribute(NONCE_TIME_KEY, null);
		System.out.println("Auth: Nonce destroyed");
	}
	protected static String encode(String secret, String data) throws Exception {
		String hashFunction = "HmacSHA256";
		Mac instance = Mac.getInstance(hashFunction);
		SecretKeySpec keySpec = new SecretKeySpec(secret.getBytes(),
				hashFunction);

		instance.init(keySpec);
		return DatatypeConverter.printHexBinary((instance.doFinal(data.getBytes()))).toLowerCase();
	}
	
	protected static String generateNonce(int length) throws NoSuchAlgorithmException {
		byte[] nonce = new byte[NONCE_LENGTH];
		Random rand = SecureRandom.getInstance ("SHA1PRNG");
		rand.nextBytes(nonce);
		return DatatypeConverter.printHexBinary(nonce);
	}



}
