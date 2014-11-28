package model;
import java.util.Date;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;

import model.CartModel;

@XmlRootElement(name="order")
public class PurchaseOrderWrapper {
	
	@XmlAttribute
	private int id;
	
	@XmlAttribute
	@XmlSchemaType(name="date")
	Date submitted;
	
	@XmlElement
	UserBean customer;
	
	@XmlElement(name="items")
	CartItemsWrapper items;

	@XmlElement
	String total;
	
	@XmlElement
	String shipping;
	
	@XmlElement
	String HST;
	
	@XmlElement
	String grandTotal;
	
	public PurchaseOrderWrapper() {};
	
	public PurchaseOrderWrapper(CartModel cart, int orderId) {
		this.id = orderId;
		this.submitted = new Date();
		this.customer = cart.getAccount();
		this.total = cart.getTotal().toPlainString();
		this.shipping = cart.getShipping().toPlainString();
		this.HST = cart.getTax().toPlainString();
		this.grandTotal = cart.getGrandTotal().toPlainString();
		this.items = new CartItemsWrapper(cart.getCartItems());
	}
	/**
	 * @return the grandTotal
	 */
	public String getGrandTotal() {
		return grandTotal;
	}

	/**
	 * @return the total
	 */
	public String getTotal() {
		return total;
	}

	/**
	 * @return the shipping
	 */
	public String getShipping() {
		return shipping;
	}

	/**
	 * @return the hST
	 */
	public String getHST() {
		return HST;
	}




}
 
