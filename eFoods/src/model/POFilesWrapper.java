package model;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="PurchaseOrderFiles")
public class POFilesWrapper {
	
	@XmlElement(name="PurchaseOrderFile")
	List<POFileWrapper> pofList;
	public POFilesWrapper() {
		// TODO Auto-generated constructor stub
	}
	
	public POFilesWrapper(List<POFileWrapper> pofList) {
		this.pofList = pofList;
	}

}
