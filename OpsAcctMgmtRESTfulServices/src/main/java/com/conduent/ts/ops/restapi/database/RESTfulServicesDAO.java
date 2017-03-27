package com.conduent.ts.ops.restapi.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import org.apache.log4j.Logger;

import com.conduent.ts.ops.restapi.config.OSCodes;
import com.conduent.ts.ops.restapi.config.OSProcessParameters;
import com.conduent.ts.ops.restapi.dto.RevokedJWT;
import com.conduent.ts.ops.restapi.dto.UserRoles;

public class RESTfulServicesDAO {
	
	protected DataSource ds = null;
	private static Logger logger = Logger.getLogger(RESTfulServicesDAO.class.getName());
	
	public RESTfulServicesDAO() {
		try {
			Context context = new InitialContext();
			ds = (DataSource) context.lookup("java:comp/env/jdbc/OpsDataSource");
		} catch (NamingException e) {
			logger.error("Error initializing data source");
		}
	}
	
	public UserRoles getUserRoles(UserRoles userRoles) {		
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		if (userRoles == null) {
			return null;
		}
		
		String username = userRoles.getUsername();
		String firstName = userRoles.getFirstName();
		String lastName = userRoles.getLastName();
		String email = userRoles.getEmail();
		String contactId = userRoles.getContactId();
		List<String> siebelRoles = userRoles.getSiebelRoles();
		
		try {
			List<String> paramList = new ArrayList<String>();
			conn = ds.getConnection();
			String sql = "select * from opms.o_v_user_roles where user_status = 'Active' ";
			
			if (!(username == null || username.equals(""))) {
				sql += " and upper(employee_login) = ? ";
				paramList.add(username.toUpperCase());
			}
			
			if (!(firstName == null || firstName.equals(""))) {
				sql += " and upper(first_name) = ? ";
				paramList.add(firstName.toUpperCase());
			}
			
			if (!(lastName == null || lastName.equals(""))) {
				sql += " and upper(last_name) = ? ";
				paramList.add(lastName.toUpperCase());
			}
			
			if (!(email == null || email.equals(""))) {
				sql += " and upper(email) = ? ";
				paramList.add(email.toUpperCase());
			}
			
			if (!(contactId == null || contactId.equals(""))) {
				sql += " and contact_id = ? ";
				paramList.add(contactId);
			}
			
			if (!(siebelRoles == null || siebelRoles.isEmpty())) {
				sql += " and responsibility_title in (";
				for (int i = 0; i < siebelRoles.size(); i++) {
					if (i == 0) {
						sql += "?";
					} else {
						sql += ",?";
					}
					paramList.add(siebelRoles.get(i));
				}
				sql += ") ";
			}
			
			logger.debug(sql);
			stmt = conn.prepareStatement(sql);
			for (int i = 0; i < paramList.size(); i++) {
				stmt.setString(i+1, paramList.get(i));
			}
			rs = stmt.executeQuery();
			userRoles = null;
			
			
			for (int i = 0; rs.next(); i++) {
				if (i == 0) {
					userRoles = new UserRoles();
					
					List<String> roles = new ArrayList<String>();
					
					userRoles.setUsername(rs.getString("EMPLOYEE_LOGIN"));
					userRoles.setContactId(rs.getString("CONTACT_ID"));
					userRoles.setType(rs.getString("USER_TYPE"));
					userRoles.setFirstName(rs.getString("FIRST_NAME"));
					userRoles.setLastName(rs.getString("LAST_NAME"));
					userRoles.setEmail(rs.getString("EMAIL"));
					userRoles.setSecurityQ(rs.getString("SECURITY_Q"));
					userRoles.setSecurityA(rs.getString("SECURITY_A"));
					
					roles.add(rs.getString("RESPONSIBILITY_TITLE"));
					userRoles.setSiebelRoles(roles);
					
					String account_id = rs.getString("ACCOUNT_ID");
					if (account_id == null) {
						userRoles.setAccountId(null);
					} else {
						if (account_id.matches("^[a-zA-Z0-9]{1,2}\\-[a-zA-Z0-9]{3,8}$")) {
							userRoles.setAccountId(account_id);
						} else {
							userRoles.setAccountId(null);
						}
					}
				} else {
					userRoles.getSiebelRoles().add(rs.getString("RESPONSIBILITY_TITLE"));
				}
			}
		} catch (SQLException e) {
			logger.error(e.getMessage());
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
				if (stmt != null) {
					stmt.close();
				}
				if (conn != null) {
					conn.close();
				}
			} catch (SQLException e) {
				logger.error(e.getMessage());
			}
		}
		
		return userRoles;
	}
	
	public int insertRevokedToken(RevokedJWT revokedJWT) {
		Connection conn = null;
		PreparedStatement stmt = null;
		
		int result = 0;
		
		try {
			conn = ds.getConnection();
			String sql = "insert into opms.o_d_rest_revoked_tokens (jwi, username, effective_date, expiry_date) values (?, ?, ?, ?) ";
			stmt = conn.prepareStatement(sql);
			stmt.setString(1, revokedJWT.getJti());
			stmt.setString(2, revokedJWT.getUsername());
			stmt.setDate(3, new java.sql.Date(revokedJWT.getEffectiveDate().getTime()));
			stmt.setDate(4, new java.sql.Date(revokedJWT.getExpiryDate().getTime()));
			result = stmt.executeUpdate();
			
		} catch (SQLException e) {
			logger.error(e.getMessage());
		} finally {
			try {
				if (stmt != null) {
					stmt.close();
				}
				if (conn != null) {
					conn.close();
				}
			} catch (SQLException e) {
				logger.error(e.getMessage());
			}
		}
		return result;
	}
	
	public boolean checkRevokedTokens(String jti) {
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		try {
			conn = ds.getConnection();
			String sql = "select count(*) from opms.o_d_rest_revoked_tokens where jwi = ? ";
			stmt = conn.prepareStatement(sql);
			stmt.setString(1, jti);
			rs = stmt.executeQuery();
			
			while (rs.next()) {
				int count = rs.getInt(1);
				if (count > 0) {
					return true;
				}
			}
		} catch (SQLException e) {
			logger.error(e.getMessage());
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
				if (stmt != null) {
					stmt.close();
				}
				if (conn != null) {
					conn.close();
				}
			} catch (SQLException e) {
				logger.error(e.getMessage());
			}
		}
		return false;
	}
	
	public int insertLockedAccount(String username) {
		Connection conn = null;
		PreparedStatement stmt = null;
		
		int result = 0;
		
		try {
			conn = ds.getConnection();
			String sql = "insert into opms.o_d_rest_locked_accounts (username, locked_date) values (?, sysdate) ";
			stmt = conn.prepareStatement(sql);
			stmt.setString(1, username);
			result = stmt.executeUpdate();
			
		} catch (SQLException e) {
			logger.error(e.getMessage());
		} finally {
			try {
				if (stmt != null) {
					stmt.close();
				}
				if (conn != null) {
					conn.close();
				}
			} catch (SQLException e) {
				logger.error(e.getMessage());
			}
		}
		return result;
	}
	
	public int removeLockedAccount(String username) {
		Connection conn = null;
		PreparedStatement stmt = null;
		
		int result = 0;
		
		try {
			conn = ds.getConnection();
			String sql = "delete from opms.o_d_rest_locked_accounts where username = ? ";
			stmt = conn.prepareStatement(sql);
			stmt.setString(1, username);
			result = stmt.executeUpdate();
			
		} catch (SQLException e) {
			logger.error(e.getMessage());
		} finally {
			try {
				if (stmt != null) {
					stmt.close();
				}
				if (conn != null) {
					conn.close();
				}
			} catch (SQLException e) {
				logger.error(e.getMessage());
			}
		}
		return result;
	}
	
	public boolean checkLockedAccount(String username) {
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		try {
			conn = ds.getConnection();
			String sql = "select count(*) from opms.o_d_rest_locked_accounts where username = ? ";
			stmt = conn.prepareStatement(sql);
			stmt.setString(1, username);
			rs = stmt.executeQuery();
			
			while (rs.next()) {
				int count = rs.getInt(1);
				if (count > 0) {
					return true;
				}
			}
		} catch (SQLException e) {
			logger.error(e.getMessage());
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
				if (stmt != null) {
					stmt.close();
				}
				if (conn != null) {
					conn.close();
				}
			} catch (SQLException e) {
				logger.error(e.getMessage());
			}
		}
		return false;
	}
	
	public short getAgencyId() {
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		
		short agencyId = 0;
		
		try {
			conn = ds.getConnection();
			String sql = "select * from opms.o_v_agency ";
			stmt = conn.createStatement();
			rs = stmt.executeQuery(sql);
			
			while (rs.next()) {
				agencyId = rs.getShort("AGENCY_ID");
				break;
			}
		} catch (SQLException e) {
			logger.error(e.getMessage());
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
				if (stmt != null) {
					stmt.close();
				}
				if (conn != null) {
					conn.close();
				}
			} catch (SQLException e) {
				logger.error(e.getMessage());
			}
		}
		return agencyId;
	}
	
	public List<OSProcessParameters> getProcessParameters(short agencyId, String paramGroup) {
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		List<OSProcessParameters> parametersList = new ArrayList<OSProcessParameters>();
		
		try {
			conn = ds.getConnection();
			String sql = "select process_parameter_id, agency_id, param_name, param_group, param_code, param_value, update_date "
					+ " from opms.o_s_process_parameters where agency_id = ? and param_group = ? ";
			stmt = conn.prepareStatement(sql);
			stmt.setShort(1, agencyId);
			stmt.setString(2, paramGroup);
			rs = stmt.executeQuery();
			
			while (rs.next()) {
				OSProcessParameters parameter = new OSProcessParameters();
				parameter.setProcessParameterId(rs.getLong("PROCESS_PARAMETER_ID"));
				parameter.setAgencyId(rs.getShort("AGENCY_ID"));
				parameter.setParamName(rs.getString("PARAM_NAME"));
				parameter.setParamGroup(rs.getString("PARAM_GROUP"));
				parameter.setParamCode(rs.getString("PARAM_CODE"));
				parameter.setParamValue(rs.getString("PARAM_VALUE"));
				parameter.setUpdateDate(new Date(rs.getTimestamp("UPDATE_DATE").getTime()));
				
				parametersList.add(parameter);
			}
		} catch (SQLException e) {
			logger.error(e.getMessage());
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
				if (stmt != null) {
					stmt.close();
				}
				if (conn != null) {
					conn.close();
				}
			} catch (SQLException e) {
				logger.error(e.getMessage());
			}
		}
		
		return parametersList;
	}
	
	public List<OSCodes> getCodes(String codeType) {
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		List<OSCodes> codesList = new ArrayList<OSCodes>();
		
		try {
			conn = ds.getConnection();
			String sql = "select code_id, descrip_short, descrip_long, code_type, update_date "
					+ " from opms.o_s_codes where code_type = ? ";
			stmt = conn.prepareStatement(sql);
			stmt.setString(1, codeType);
			rs = stmt.executeQuery();
			
			while (rs.next()) {
				OSCodes code = new OSCodes();
				code.setCodeId(rs.getLong("CODE_ID"));
				code.setDescripShort(rs.getString("DESCRIP_SHORT"));
				code.setDescripLong(rs.getString("DESCRIP_LONG"));
				code.setCodeType(rs.getString("CODE_TYPE"));
				code.setUpdateDate(new Date(rs.getTimestamp("UPDATE_DATE").getTime()));
				
				codesList.add(code);
			}
		} catch (SQLException e) {
			logger.error(e.getMessage());
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
				if (stmt != null) {
					stmt.close();
				}
				if (conn != null) {
					conn.close();
				}
			} catch (SQLException e) {
				logger.error(e.getMessage());
			}
		}
		
		return codesList;
	}
}
