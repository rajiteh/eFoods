package model;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

public class UserBean {
	
	@XmlAttribute(name="account")
	String name;
	
	
	String fullName;


	boolean admin;
	
	public UserBean() {

	}

	public UserBean(String name, String fullName, boolean admin) {
		super();
		this.name = name;
		this.fullName = fullName;
		this.admin = admin;
	}
	
	
	
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * @return the admin
	 */
	public boolean isAdmin() {
		return admin;
	}


	/**
	 * @return the fullName
	 */
	@XmlElement(name="name")
	public String getFullName() {
		return fullName;
	}


}
