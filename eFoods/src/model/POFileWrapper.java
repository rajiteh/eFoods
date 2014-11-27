package model;

import javax.xml.bind.annotation.XmlElement;

public class POFileWrapper {
	@XmlElement
	String url;
	
	@XmlElement
	int id;

	/**
	 * @return the url
	 */
	public String getUrl() {
		return url;
	}
	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}
	
	public POFileWrapper() {
		// TODO Auto-generated constructor stub
	}

	public POFileWrapper(String url, int i) {
		super();
		this.url = url;
		this.id = i;
	
	}
}
