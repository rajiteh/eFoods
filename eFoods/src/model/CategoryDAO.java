package model;

import java.math.BigInteger;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;


import util.eFoodsDataSource;
public class CategoryDAO extends BaseDAO {

	
	
	public CategoryDAO() {
		// TODO Auto-generated constructor stub
	}

	public List<CategoryBean> retrieve() throws Exception {
		return retrieve(ID_ALL);
	}
	

	
	
	public List<CategoryBean> retrieve(int id) throws Exception {
		Connection connection = null;
		PreparedStatement preparedStatement;
		ResultSet resultSet;
		List<CategoryBean> retval = new ArrayList<CategoryBean>();
		
		

		try {
			connection = eFoodsDataSource.getConnection();
			
			if(id==ID_ALL) {
				String query = "SELECT id, name, description, picture FROM category";
				preparedStatement = connection.prepareStatement(query);
			} else {
				String query = "SELECT id, name, description, picture FROM category WHERE id = ?";
				preparedStatement = connection.prepareStatement(query);
				preparedStatement.setInt(1,  id);
			}
			
			resultSet = preparedStatement.executeQuery();

			while (resultSet.next()) {
				retval.add(new CategoryBean(resultSet.getInt("id"),
						resultSet.getString("name"), 
						resultSet.getString("description"),
						new BigInteger(resultSet.getString("picture"), 16).toByteArray()));
			}
		} catch (Exception e) {
			throw e;
		} finally {
			if (connection != null)
				connection.close();
		}

		return retval;
			}

}
