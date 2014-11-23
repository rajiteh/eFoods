package model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Iterator;
import java.util.List;
import java.util.Queue;





public class BaseDAO {
	
	
	public final static int ID_ALL = -1;
	public final static int PAGE_ALL = 1;
	public final static int LIMIT_ALL = -1;
	public BaseDAO() {
		// TODO Auto-generated constructor stub
	}

	protected class PrepareInstruction {
		static final int TYPE_STRING = 0x0a;
		static final int TYPE_INT = 0x0b;
		int type;
		Object value;
		public PrepareInstruction(int type, Object value) {
			super();
			this.type = type;
			this.value = value;
		}
	}
	
	protected String createWhereString(List<String> wheres, String operator) {
		String whereString = "";
		Iterator<String> iter = wheres.iterator();
        whereString += iter.hasNext() ? iter.next() : "";
        while (iter.hasNext()) whereString += " "+ operator + " " + iter.next();
        return "(" + whereString + ")";
	}
	
	protected PreparedStatement instaPrepareStatement(String query, Queue<PrepareInstruction> piQ, Connection connection) throws Exception {
		PreparedStatement ps = connection.prepareStatement(query);
		int idx=0;
		while (piQ.size() > 0) {
			PrepareInstruction pi = piQ.poll();
			idx++; //Start with 1
			switch (pi.type) {
			case PrepareInstruction.TYPE_STRING:
				ps.setString(idx, (String) pi.value);
				break;
			case PrepareInstruction.TYPE_INT:
				ps.setInt(idx, (int) pi.value);
				break;
			default:
				throw new Exception("Instructions not clear statement stuck in toaster");
			}
		}
		return ps;
	}
	
	protected String pagingString(int page, int limit) {
		String ret = "";
		
		if (page > 0) {
			ret += " OFFSET " + (page - 1) * limit + " ROWS ";
			if (limit != LIMIT_ALL) {
				ret += " FETCH NEXT " + limit + " ROWS ONLY";
			}
		}
		
		return ret;
	}
}
