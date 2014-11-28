package util;

import java.io.File;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import util.PurchaseOrderFiles.PurchaseOrderFile;

public class PurchaseOrderFiles {
	public class PurchaseOrderFile {
		public static final String STATUS_NEW = "new";
		public static final String STATUS_PENDING = "pending";
		public static final String STATUS_PURCHASED = "purchased";
		
		String fileName;
		int orderId;
		String userName;
		String status;
		String modifiedDate;
		/**
		 * @return the modifiedDate
		 */
		public String getModifiedDate() {
			return modifiedDate;
		}

		/**
		 * @return the fileName
		 */
		public String getFileName() {
			return fileName;
		}

		/**
		 * @return the oderId
		 */
		public int getOrderId() {
			return orderId;
		}

		/**
		 * @return the userName
		 */
		public String getUserName() {
			return userName;
		}

		/**
		 * @return the status
		 */
		public String getStatus() {
			return status;
		}

		public String getFileNameOnly() {
			return new File(fileName).getName();
		}
		public PurchaseOrderFile(File basePath, int orderId, String userName,
				String status) throws Exception {
			String filename = orderId + "_" + userName + "_" + status + ".xml";
			if (!filename.matches(MATCHER_REGEX))
				throw new Exception("Order filename invalid. Check parameters. " + filename);
			File leFile = new File(basePath, filename);
			this.fileName = leFile.getAbsolutePath();
			this.orderId = orderId;
			this.userName = userName;
			this.status = status;
			this.modifiedDate = new Date(leFile.lastModified()).toString();
		}
	}
	protected static final String MATCHER_REGEX = "^(?<poId>[1-9][0-9]*)_(?<accountId>[A-Za-z0-9]+)_(?<statusId>new|pending|purchased)\\.xml$";

	
	
	private File basePath;
	private List<PurchaseOrderFile> purchaseOrderFiles;
	
	public PurchaseOrderFiles(String basePath) throws Exception {
		this.basePath = new File(basePath);
		this.purchaseOrderFiles = allOrderFiles();
	}

	public List<PurchaseOrderFile> allOrderFiles() throws Exception {
		File[] files = basePath.listFiles();
		List<PurchaseOrderFile> results = new ArrayList<PurchaseOrderFile>();
		List<Integer> idArray = new ArrayList<Integer>();
		for (File file : files) {
			if (file.isFile()) {
				if (!file.getName().endsWith(".xml")) continue; //Avoid stupid mac osx .DS_store files
				Matcher matcher = Pattern.compile(MATCHER_REGEX).matcher(file.getName());
				if (matcher.matches()) {
					int currId = Integer.parseInt(matcher.group("poId"));
					String account = matcher.group("accountId");
					String status = matcher.group("statusId");
					if (idArray.contains(currId))
						throw new Exception("Non unique id " + currId  );
					else
						idArray.add(currId);
					results.add(new PurchaseOrderFile(basePath,currId,account,status));
				} else {
					throw new Exception("Malformed xml name " + file.getName());
				}
			}
		}			
		return results;
	}
	
	public int getNextOrderId() throws Exception {
		int[] ids = new int[purchaseOrderFiles.size()];
		int i = 0;
		for(PurchaseOrderFile pof: purchaseOrderFiles)
			ids[i] = pof.getOrderId();
			i++;
		Arrays.sort(ids);
		if (ids.length > 0) {
			return ids[ids.length - 1] + 1;
		} else {
			return 1;
		}
	}
	
	public void storeNewOrder(int orderId, String accountName, String order) throws Exception {
		int nextId = getNextOrderId();
		if (nextId != orderId)
			throw new Exception("Order ID mismatch. ");
		PurchaseOrderFile pof = new PurchaseOrderFile(basePath, orderId, accountName, PurchaseOrderFile.STATUS_NEW);
		FileWriter fWriter = null;
		try {
			fWriter = new FileWriter(pof.getFileName());
			fWriter.write(order);
			purchaseOrderFiles.add(pof);
			System.out.println("PO: Stored purchase order " + pof.getFileName());
		} catch (Exception e) {
			throw e;
		} finally {
			if (fWriter != null)
				fWriter.close();
		}
		
	}

	public List<PurchaseOrderFile> getOrdersByUser(String username) {
		List<PurchaseOrderFile> result = new ArrayList<PurchaseOrderFile>();
		for(PurchaseOrderFile pof : purchaseOrderFiles) {
			if (pof.getUserName().equals(username))
				result.add(pof);
		}
		return result;
	}
	
	public List<PurchaseOrderFile> getOrdersByStatus(String status) {
		List<PurchaseOrderFile> result = new ArrayList<PurchaseOrderFile>();
		for(PurchaseOrderFile pof : purchaseOrderFiles) {
			if (pof.getStatus().equals(status))
				result.add(pof);
		}
		return result;
	}
	
	public PurchaseOrderFile getOrderById(int orderId) {
		for(PurchaseOrderFile pof : purchaseOrderFiles) {
			if (pof.getOrderId() == orderId)
				return pof;
		}
		return null;
	}

	public void updateStatus(int orderId, String status) throws Exception {
		PurchaseOrderFile pof = getOrderById(orderId);
		if (pof != null) {
			String fname = pof.getFileName();
			PurchaseOrderFile newPof = new PurchaseOrderFile(basePath, orderId, pof.getUserName(), status);
			String newFname = newPof.getFileName();
			File oldFile = new File(fname);
			File newFile = new File(newFname);
			if (oldFile.renameTo(newFile)) {
				this.purchaseOrderFiles = allOrderFiles();
			} else {
				throw new Exception("Could not rename file");
			}
		} else {
			throw new Exception("Order ID does not exist");
		}
		
	}

	public void nuke() throws Exception {
		for(PurchaseOrderFile pof : purchaseOrderFiles) {
			new File(pof.getFileName()).delete();
		}
		this.purchaseOrderFiles = allOrderFiles();
	}
	

}
