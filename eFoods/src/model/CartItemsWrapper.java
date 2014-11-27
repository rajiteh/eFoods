package model;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import model.CartModel.CartItem;

@XmlRootElement
public class CartItemsWrapper
{
	@XmlElement(name="item")
	private List<CartItemBean> itemsList;

	public CartItemsWrapper()
   {
	   super();
	   // TODO Auto-generated constructor stub
   }
	
	public CartItemsWrapper(List<CartItem> cartItems)
	{
		List<CartItemBean> cartItemsList = new ArrayList<CartItemBean>();
		for(CartItem ci: cartItems)
			cartItemsList.add(new CartItemBean(ci));
		
		this.itemsList = cartItemsList;
	}
}
