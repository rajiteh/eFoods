package model;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import model.CartModel;
import model.CartModel.CartItem;

@XmlRootElement(name="order")
public class PurchaseOrderWrapper {
	
	@XmlAttribute
	private int id;
	
	@XmlAttribute
	Date submitted;
	
	@XmlElement
	UserBean customer;
	
	@XmlElement
	List<CartItemWrapper> items;

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
		this.grandTotal = cart.getTotal().toPlainString();
		
		List<CartItemWrapper> cartItemWrapperList = new ArrayList<CartItemWrapper>();
		for(CartItem ci: cart.getCartItems())
			cartItemWrapperList.add(new CartItemWrapper(ci));
		
		this.items = cartItemWrapperList;
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
 
