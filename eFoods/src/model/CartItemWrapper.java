package model;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

import model.CartModel.CartItem;

public class CartItemWrapper {
	
	@XmlAttribute
	String number;
	
	@XmlElement
	String name;
	
	@XmlElement
	float price;
	
	@XmlElement
	int quantity;
	
	@XmlElement
	String extended;

	public CartItemWrapper() {
		// TODO Auto-generated constructor stub
	}
	public CartItemWrapper(CartItem cartItem) {
		this.number = cartItem.getItem().getNumber();
		this.name = cartItem.getItem().getName();
		this.price = cartItem.getItem().getPrice();
		this.quantity = cartItem.getQuantity();
		this.extended = cartItem.getItemTotal();
	}

}
