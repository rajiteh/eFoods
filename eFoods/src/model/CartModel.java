package model;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import util.SSOAuthenticator;
import util.eFoodsDataSource;

public class CartModel {

	public class CartItem {
		ItemBean item;
		int quantity;

		/**
		 * @return the item
		 */
		public ItemBean getItem() {
			return item;
		}

		/**
		 * @return the quantity
		 */
		public int getQuantity() {
			return quantity;
		}

		public CartItem(ItemBean item, int quantity) {
			super();
			this.item = item;
			this.quantity = quantity;
		}

	}

	private static final BigDecimal TAX_MULTIPLIER = new BigDecimal("0.13");
	private static final boolean TAX_WITH_SHIPPING = false;
	private static final String CART_SESSION_KEY = "com.eFoods.model.Cart";

	private List<CartItem> cartItems;
	private UserBean account;
	private BigDecimal total;
	private BigDecimal shipping;
	private BigDecimal tax;

	public CartModel(UserBean account) {
		super();
		this.cartItems = new ArrayList<CartItem>();
		this.account = account;
		this.total = BigDecimal.ZERO;
		this.shipping = BigDecimal.ZERO;
		this.tax = BigDecimal.ZERO;
	}

	public synchronized void manipulateCart(ItemBean item, int qty)
			throws Exception {
		try {
			for (Iterator<CartItem> i = cartItems.listIterator(); i.hasNext();) {
				CartItem ci = i.next();
				if (ci.item.equals(item)) {
					if (qty < 1) {
						i.remove();
						System.out.println("Cart\t: Item removed. "
								+ item.number);
					} else {
						ci.quantity = qty;
						System.out.println("Cart\t: Item qty updated to " + qty
								+ " for " + item.number);
					}
					return;
				}
			}
			if (qty > 0) {
				cartItems.add(new CartItem(item, qty));

				System.out.println("Cart\t: Item added. " + qty + " of "
						+ item.number);
			} else {
				throw new Exception(
						"Invalid operation. Cannot set qty to 0 when it doesn't exist, NOOB");
			}
		} finally {
			computeCart();
		}

	}

	private void computeCart() {
		this.total = calculateTotal();
		this.shipping = calculateShipping();
		this.tax = calculateTax();

		System.out.println("Cart : Refreshed. Total:" + total + " Shipping:"
				+ shipping + " Tax:" + tax);
	}

	private BigDecimal calculateShipping() {
		return BigDecimal.ZERO;
	}

	private BigDecimal calculateTotal() {
		BigDecimal t = BigDecimal.ZERO;
		for (CartItem ci : cartItems) {
			t = t.add(new BigDecimal(ci.item.getPrice())).multiply(
					new BigDecimal(ci.quantity));
		}
		return t;
	}

	private BigDecimal calculateTax() {
		return (TAX_WITH_SHIPPING ? total.add(shipping)
				.multiply(TAX_MULTIPLIER) : total.multiply(TAX_MULTIPLIER));
	}

	/**
	 * @param cartItems
	 *            the cartItems to set
	 */
	public List<CartItem> getCartItems() {
		return cartItems;
	}

	/**
	 * Gets the CartItem object for the given item
	 * 
	 * @param item
	 * @return corresponding CartItem if found, null if doesnt exist in cart.
	 */
	public CartItem getCartItemFor(ItemBean item) {
		for (CartItem ci : cartItems)
			if (ci.item.equals(item))
				return ci;
		return null;
	}

	/**
	 * Creates a map of cart itesm so that it could be accessed directly from item number
	 * @return map containing item number as key and cartitem as value
	 */
	public Map<String, CartItem> mappedCartItems() {
		Map<String, CartItem> map = new LinkedHashMap<String, CartItem>();
		for (CartItem i : this.getCartItems())
			map.put(i.getItem().getName(), i);
		return map;
	}

	/**
	 * @param wot
	 */
	public void setCartItems(List<CartItem> cartItems) {
		this.cartItems = cartItems;
	}

	/**
	 * @return the account
	 */
	public UserBean getAccount() {
		return account;
	}

	/**
	 * @param account
	 *            the account to set
	 */
	public void setAccount(UserBean account) {
		if (account != null)
			System.out.println("CartModel: Ownership changed to "
					+ account.name);
		this.account = account;
	}

	/**
	 * @return the total
	 */
	public BigDecimal getTotal() {
		return total;
	}

	/**
	 * @param total
	 *            the total to set
	 */
	public void setTotal(BigDecimal total) {
		this.total = total;
	}

	/**
	 * @return the shipping
	 */
	public BigDecimal getShipping() {
		return shipping;
	}

	/**
	 * @param shipping
	 *            the shipping to set
	 */
	public void setShipping(BigDecimal shipping) {
		this.shipping = shipping;
	}

	/**
	 * @return the tax
	 */
	public BigDecimal getTax() {
		return tax;
	}

	/**
	 * @param tax
	 *            the tax to set
	 */
	public void setTax(BigDecimal tax) {
		this.tax = tax;
	}

	public static CartModel getInstance(HttpServletRequest request,
			SSOAuthenticator auth) {
		CartModel dis;
		if ((dis = (CartModel) request.getSession().getAttribute(
				CART_SESSION_KEY)) == null) {
			synchronized (CartModel.class) {
				if ((dis = (CartModel) request.getSession().getAttribute(
						CART_SESSION_KEY)) == null) {
					dis = new CartModel(null);
					request.getSession().setAttribute(CART_SESSION_KEY, dis);
					System.out.println("Cart: Initialized");
				}
			}
		}

		dis.setAccount(auth.getUser(request));
		return dis;
	}

}
