package util;

import java.sql.Connection;
import java.sql.SQLException;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

public class eFoodsDataSource {

	private static final String DATASOURCE_CONTEXT = "java:/comp/env/jdbc/EECS";
	private static final String SCHEMA = "roumani";
	private static DataSource dataSource;
	
	
	/**
	 * @return the dataSource
	 * @throws NamingException 
	 */
	public static DataSource getDataSource() throws NamingException {
		if (dataSource == null) {
			synchronized(eFoodsDataSource.class) {
				if (dataSource == null) {
					Context initialContext = new InitialContext();
					dataSource = (DataSource) initialContext.lookup(DATASOURCE_CONTEXT);
				}
			}
		}
		return dataSource;
	}

	public static Connection getConnection() throws SQLException, NamingException {
		Connection c = getDataSource().getConnection();
		c.createStatement().executeUpdate("set schema " + SCHEMA);
		return c;
	}
	protected eFoodsDataSource() {}
	

}
