package util;

import java.io.IOException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Enumeration;
import java.util.HashMap;
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
	
	private static final Random RANDOM = new SecureRandom();
	
	private static final int NONCE_LENGTH = 16;
	
	private String sharedKey;
	private String sessionKey;
	private String ssoEndpoint;

	public SSOAuthenticator(String ssoEndpoint, String sharedKey) throws Exception {
		if (ssoEndpoint == null || ssoEndpoint.length() < 0)
			throw new Exception("Endpoint must not be blank!");
		if (sharedKey == null || sharedKey.length() < 0)
			throw new Exception("Shared key must not be blank!");
		this.sharedKey = sharedKey;
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
	
	private void setUser(HttpServletRequest request, UserBean user) {
		request.getSession().setAttribute(this.sessionKey, user);
	}

	public void SSORedirect(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String nonce = generateNonce(NONCE_LENGTH);
		String payload = URLEncoder.encode(new String(Base64.encodeBase64(("nonce=" + nonce).getBytes())), "UTF-8");
		System.out.println("Calculating the hash from  :" + payload);
		String signature = encode(sharedKey,payload).toLowerCase();
		String queryString = "payload=" + payload + "&signature=" + signature;
		storeNonce(request, nonce);
		response.sendRedirect(ssoEndpoint + "?" + queryString);
	}
	@Override
	public boolean login(HttpServletRequest request, String uname,
			String pwd) throws Exception {
		Map<String,String[]> payload = new HashMap<String,String[]>();
		String signature, nonce, userName, remoteNonce, remotePayload, remoteSignature, payloadQueryString;
		
		remotePayload = request.getParameter("payload");
		remoteSignature = request.getParameter("signature");
		signature = encode(sharedKey,URLEncoder.encode(remotePayload, "UTF-8"));
		
		if (!signature.equals(remoteSignature)) {
			throw new Exception("Payload verification failed.");
		}

		nonce = retrieveNonce(request);
		
		expireNonce(request); //A nonce may only used once and ONCE only
		
		if (nonce == null) {
			throw new Exception("Request expired. Please re-initiate login.");
		}
		
		payloadQueryString = new String(Base64.decodeBase64(remotePayload));
		RequestUtil.parseParameters(payload, payloadQueryString, "UTF-8");
		if (payload.get("nonce").length == 1 && payload.get("username").length == 1) {
			remoteNonce = payload.get("nonce")[0];
			userName = payload.get("username")[0];	
		} else {
			throw new Exception("Invalid payload");
		}
				
		if  (!nonce.equals(remoteNonce)) {
			throw new Exception("Invalid nonce. Please re-initiaite login.");
		}
		
		if (userName == null) {
			throw new Exception("Invalid username data in payload");
		}
		
		setUser(request, new UserBean(userName));
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
			expireNonce(request);
			return null;
		}
		
		
	}
	
	protected static void expireNonce(HttpServletRequest request) {
		request.getSession().setAttribute(NONCE_VALUE_KEY, null);
		request.getSession().setAttribute(NONCE_TIME_KEY, null);
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
