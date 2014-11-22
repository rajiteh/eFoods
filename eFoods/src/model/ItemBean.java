package model;

public class ItemBean {

	String number;
	String name;
	float price;
	int qty;
	int onOrder;
	int reOrder;
	CategoryBean category;
	int supplierId;
	int costPrice;
	String unit;

	public ItemBean(String number, String name, float price, int qty, int onOrder,
			int reOrder, CategoryBean category, int supplierId, int costPrice,
			String unit) {
		super();
		this.number = number;
		this.name = name;
		this.price = price;
		this.qty = qty;
		this.onOrder = onOrder;
		this.reOrder = reOrder;
		this.category = category;
		this.supplierId = supplierId;
		this.costPrice = costPrice;
		this.unit = unit;
	}

	/**
	 * @return the number
	 */
	public String getNumber() {
		return number;
	}

	/**
	 * @param number the number to set
	 */
	public void setNumber(String number) {
		this.number = number;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the price
	 */
	public float getPrice() {
		return price;
	}

	/**
	 * @param price the price to set
	 */
	public void setPrice(float price) {
		this.price = price;
	}

	/**
	 * @return the qty
	 */
	public int getQty() {
		return qty;
	}

	/**
	 * @param qty the qty to set
	 */
	public void setQty(int qty) {
		this.qty = qty;
	}

	/**
	 * @return the onOrder
	 */
	public int getOnOrder() {
		return onOrder;
	}

	/**
	 * @param onOrder the onOrder to set
	 */
	public void setOnOrder(int onOrder) {
		this.onOrder = onOrder;
	}

	/**
	 * @return the reOrder
	 */
	public int getReOrder() {
		return reOrder;
	}

	/**
	 * @param reOrder the reOrder to set
	 */
	public void setReOrder(int reOrder) {
		this.reOrder = reOrder;
	}

	/**
	 * @return the category
	 */
	public CategoryBean getCategory() {
		return category;
	}

	/**
	 * @param category the category to set
	 */
	public void setCategory(CategoryBean category) {
		this.category = category;
	}

	/**
	 * @return the supplierId
	 */
	public int getSupplierId() {
		return supplierId;
	}

	/**
	 * @param supplierId the supplierId to set
	 */
	public void setSupplierId(int supplierId) {
		this.supplierId = supplierId;
	}

	/**
	 * @return the costPrice
	 */
	public int getCostPrice() {
		return costPrice;
	}

	/**
	 * @param costPrice the costPrice to set
	 */
	public void setCostPrice(int costPrice) {
		this.costPrice = costPrice;
	}

	/**
	 * @return the unit
	 */
	public String getUnit() {
		return unit;
	}

	/**
	 * @param unit the unit to set
	 */
	public void setUnit(String unit) {
		this.unit = unit;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((number == null) ? 0 : number.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof ItemBean)) {
			return false;
		}
		ItemBean other = (ItemBean) obj;
		if (number == null) {
			if (other.number != null) {
				return false;
			}
		} else if (!number.equals(other.number)) {
			return false;
		}
		return true;
	}

}
