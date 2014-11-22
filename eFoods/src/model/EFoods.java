package model;

import java.util.List;

public class EFoods {
	private CategoryDAO categoryDAO;
	private ItemDAO itemDAO;
	
	public EFoods() {
			categoryDAO = new CategoryDAO();
			itemDAO = new ItemDAO();
	}

	public List<CategoryBean> categories(int id) throws Exception {
		return categoryDAO.retrieve(id);
	}
	
	public List<ItemBean> items(String number, int catId, int page, int limit) throws Exception {
		return itemDAO.retrieve(number, catId, page, limit);
	}

	 

}
