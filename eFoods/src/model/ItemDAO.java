package model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import util.eFoodsDataSource;

public class ItemDAO extends BaseDAO {

	public final static String NUMBER_ALL = "-1";
	public final static int CAT_ALL = -1;
	public final static String NO_FILTER = "__!!__";
	
	public static final String BASE_QUERY = "SELECT  I.number, "
			+ "I.name,  I.price,  I.qty,  I.onorder, "
			+ "I.reorder,  I.catid,  I.supid,  I.costprice, "
			+ "I.unit,  C.id as \"CAT_ID\",  C.name as \"CAT_NAME\", "
			+ "C.description as \"CAT_DESCRIPTION\" FROM ITEM I "
			+ "INNER JOIN CATEGORY C  ON I.catId = C.id";
	
	public ItemDAO() {}

	/**
	 * Retrieves all the items from the database.
	 * @return A List containing all the items.
	 * @throws Exception
	 */
	public List<ItemBean> retrieve() throws Exception {
		return retrieve(NUMBER_ALL);
	}


	/**
	 * Retrieves a single item from the database.
	 * @param number
	 * @return A List containing the single item.
	 * @throws Exception
	 */
	public List<ItemBean> retrieve(String number) throws Exception {
		return retrieve(number, CAT_ALL, PAGE_ALL, LIMIT_ALL, NO_FILTER);
	}
	
	/**
	 * Creates a query for the DB based on the passed parameters.
	 * 
	 * @param number
	 * @param catId
	 * @param page
	 * @param limit
	 * @param filter
	 * @return The list of the items passed on the generated query.
	 * @throws Exception
	 */
	public List<ItemBean> retrieve(String number, int catId, int page, int limit, String filter) throws Exception {
		Connection connection = null;
		PreparedStatement preparedStatement;
		ResultSet rs;
		List<ItemBean> retval = new ArrayList<ItemBean>();

		try {
			connection = eFoodsDataSource.getConnection();

			
				Queue<PrepareInstruction> instructions=new LinkedList<PrepareInstruction>();	
				List<String> wheres = new ArrayList<String>();
				
				if (!(number.equals(NUMBER_ALL))) {
					wheres.add("I.number = ?");
					instructions.add(new PrepareInstruction(PrepareInstruction.TYPE_STRING, number));
				}
				if (catId != ID_ALL) {
					wheres.add("C.id = ?");
					instructions.add(new PrepareInstruction(PrepareInstruction.TYPE_INT, catId));
				}
				
				if (!filter.equals(NO_FILTER)) {
					wheres.add("LOWER(I.name) LIKE LOWER( ? )");
					instructions.add(new PrepareInstruction(PrepareInstruction.TYPE_STRING, "%" + filter + "%"));
				}
				String query = BASE_QUERY;
				if (wheres.size() > 0) { query += " WHERE " + createWhereString(wheres, "AND"); }
				
		        query += pagingString(page, limit);
		        System.out.println("DB Query: " + query.replaceFirst(BASE_QUERY, ""));
				preparedStatement = instaPrepareStatement(query, instructions, connection);
				
			

			rs = preparedStatement.executeQuery();

			while (rs.next()) {
				CategoryBean cat = new CategoryBean(rs.getInt("cat_id"),
						rs.getString("cat_name"),
						rs.getString("cat_description"), null);
				ItemBean item = new ItemBean(rs.getString("number"),
						rs.getString("name"), rs.getFloat("price"),
						rs.getInt("qty"), rs.getInt("onorder"),
						rs.getInt("reorder"), cat, rs.getInt("supid"),
						rs.getInt("costprice"), rs.getString("unit"));
				retval.add(item);
			}
		} catch (Exception e) {
			throw e;
		} finally {
			if (connection != null)
				connection.close();
		}

		return retval;
	}
	

	/*
	 * SET SCHEMA roumani; SELECT I.number, I.name, I.price, I.qty, I.onorder,
	 * I.reorder, I.catid, I.supid, I.costprice, I.unit, C.id as "cat_id",
	 * C.name as "cat_name", C.description as "cat_description" FROM ITEM I
	 * INNER JOIN CATEGORY C ON I.catId = C.id;
	 */
}
