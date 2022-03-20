package dao;

import java.sql.Statement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import exceptions.SystemException;

import transferobjects.ManagerPojo;
import transferobjects.ReimbursementPojo;


public class ManagerDaoJDBCImpl implements ManagerDao {

	@Override
	public List<ReimbursementPojo> fetchAllPending() throws SystemException {

		List<ReimbursementPojo> allPending = new ArrayList<ReimbursementPojo>();
		Connection conn = DBUtil.getConnected();
		try {
			Statement stmt = conn.createStatement();
			String query = "SELECT * FROM pending_details";
			ResultSet rs = stmt.executeQuery(query);
			while (rs.next()) {
				ReimbursementPojo reimbursementPojo = new ReimbursementPojo(rs.getInt(1), rs.getInt(2), rs.getFloat(3),
						rs.getString(4), rs.getString(5), rs.getString(6),rs.getString(7));
				allPending.add(reimbursementPojo);
			}
		} catch (SQLException e) {
			throw new SystemException();
		}
		return allPending;
	}

	
	@Override
	public List<ReimbursementPojo> fetchAllDenied() throws SystemException {

		List<ReimbursementPojo> allDenied = new ArrayList<ReimbursementPojo>();
		Connection conn = DBUtil.getConnected();
		try {
			Statement stmt = conn.createStatement();
			String query = "SELECT * FROM reimbursement_details WHERE status='deny' OR status='Denied'";
			ResultSet rs = stmt.executeQuery(query);
			while (rs.next()) {
				ReimbursementPojo denied = new ReimbursementPojo(rs.getInt(1), rs.getInt(2), rs.getFloat(3),
						rs.getString(4), rs.getString(5), rs.getString(6), rs.getString(7));
				allDenied.add(denied);
			}
		} catch (SQLException e) {
			throw new SystemException();
		}
		return allDenied;
	}
		

	@Override
	public List<ReimbursementPojo> fetchAllResolved() throws SystemException {

		List<ReimbursementPojo> allResolved = new ArrayList<ReimbursementPojo>();
		Connection conn = DBUtil.getConnected();
		try {
			Statement stmt = conn.createStatement();
			String query = "SELECT * FROM reimbursement_details WHERE status='approved' OR status='Approved'";
			ResultSet rs = stmt.executeQuery(query);
			while (rs.next()) {
				ReimbursementPojo resolvedPojo = new ReimbursementPojo(rs.getInt(1), rs.getInt(2), rs.getFloat(3),
						rs.getString(4), rs.getString(5), rs.getString(6), rs.getString(7));
				allResolved.add(resolvedPojo);
			}
		} catch (SQLException e) {
			 throw new SystemException();
			
		} 
		return allResolved;
	}

	@Override
	public List<ReimbursementPojo> fetchEmpReimbursement(int employeeId) throws SystemException {
		List<ReimbursementPojo> allRequests = new ArrayList<ReimbursementPojo>();
		
		Connection conn = DBUtil.getConnected();
		try {
			Statement stmt = conn.createStatement();
			String query = "SELECT * FROM pending_details WHERE employee_id= " + employeeId + "UNION ALL SELECT * FROM reimbursement_details WHERE employee_id=" + employeeId;
			ResultSet rs = stmt.executeQuery(query);
			while (rs.next()) {
				ReimbursementPojo reimbursementPojo = new ReimbursementPojo(rs.getInt(1), rs.getInt(2), rs.getFloat(3), rs.getString(4),
						rs.getString(5), rs.getString(6), rs.getString(7));
						allRequests.add(reimbursementPojo);
			} 
		} catch (SQLException e) {
			throw new SystemException();
		}
		return allRequests;
	}
	@Override
	public ReimbursementPojo fetchByReimId(int reimId) throws SystemException {
		ReimbursementPojo reimbursementPojo = null;
		Connection conn = DBUtil.getConnected();
		try {
			Statement stmt = conn.createStatement();
			String query = "SELECT * FROM pending_details WHERE pending_id=" + reimId;
			ResultSet rs = stmt.executeQuery(query);
			while (rs.next()) {
				reimbursementPojo = new ReimbursementPojo(rs.getInt(1), rs.getInt(2), rs.getFloat(3), rs.getString(4),
						rs.getString(5), rs.getString(6), rs.getString(7));
			} 
		} catch (SQLException e) {
			throw new SystemException();
		}
		return reimbursementPojo;
	}

	@Override
	public ReimbursementPojo approve(int pendingId) throws SystemException {
		ReimbursementPojo reimbursementPojo = null;
		Connection conn = DBUtil.getConnected();
		try {
			Statement stmt = conn.createStatement();
			
			reimbursementPojo = fetchByReimId(pendingId);
				if(reimbursementPojo != null) {
					reimbursementPojo.setStatus("Approved");
					System.out.println("fetched recording with pending id");
					String query1 = "INSERT INTO reimbursement_details(employee_id, reimbursement_amount, reimbursement_reason, status)" +
									"VALUES("+reimbursementPojo.getEmployeeId()+","+reimbursementPojo.getReimbursementAmount()+",'"+reimbursementPojo.getReimbursementReason()+"','Approved')";
				
				int rows1 = stmt.executeUpdate(query1);
				
				String query3 = "DELETE FROM pending_details WHERE pending_id=" + pendingId;
				int rows3 = stmt.executeUpdate(query3);
				
			} else {
				System.out.println("Sorry no account under that ID");
			}
		} catch (SQLException e) {
			throw new SystemException();
			
		}
		return reimbursementPojo;
	}
	
	
	@Override
	public ManagerPojo managerLogin(ManagerPojo manager) throws SystemException {
		Connection conn;
		
		 conn = DBUtil.getConnected();
		

	Statement stmt;
	try {
	
		stmt = conn.createStatement();
	 
	String query = "SELECT * FROM manager_details WHERE username= '" + manager.getUserName()+ "'AND password='" + manager.getPassword() + "'";
	ResultSet rs = stmt.executeQuery(query);
	if(rs.next()) {
		manager = new ManagerPojo(rs.getInt(1), rs.getString(2),
				rs.getString(3), rs.getString(4), rs.getString(5), rs.getString(6),
				rs.getString(7), rs.getString(8), rs.getString(8));
	}
	}catch (SQLException e) {
		
		throw new SystemException();
	
	}
		
return manager;
		
	
	}
	

	

	@Override
	public ReimbursementPojo deny(int pendingId) throws SystemException {
		ReimbursementPojo reimbursementPojo = null;
		Connection conn = DBUtil.getConnected();
		try {
			Statement stmt = conn.createStatement();
			
			reimbursementPojo = fetchByReimId(pendingId);
				if(reimbursementPojo != null) {
					
					System.out.println("fetched recording with pending id");
					String query1 = "INSERT INTO reimbursement_details(employee_id, reimbursement_amount, reimbursement_reason, status)" +
									"VALUES("+reimbursementPojo.getEmployeeId()+","+reimbursementPojo.getReimbursementAmount()+",'"+reimbursementPojo.getReimbursementReason()+"','Denied')";
				
				int rows1 = stmt.executeUpdate(query1);
				
				String query3 = "DELETE FROM pending_details WHERE pending_id=" + pendingId;
				int rows3 = stmt.executeUpdate(query3);
				
			} else {
				System.out.println("Sorry no account under that ID");
			}
		} catch (SQLException e) {
			throw new SystemException();
			
		}
		return reimbursementPojo;
	}
	

}
