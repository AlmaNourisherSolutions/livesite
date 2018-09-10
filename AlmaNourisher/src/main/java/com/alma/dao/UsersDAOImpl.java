package com.alma.dao;

import com.alma.model.ContactUs;
import com.alma.model.Users;

import java.math.BigInteger;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.security.crypto.password.PasswordEncoder;

public class UsersDAOImpl implements UsersDAO {

	@Autowired
	private PasswordEncoder passwordEncoder;
	private JdbcTemplate jdbcTemplate;

	public UsersDAOImpl(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

	public int addUser(Users user) {
		String password = user.getPassword();
		user.setPassword(this.passwordEncoder.encode(password));
		SimpleJdbcInsert  adduser = new SimpleJdbcInsert(this.jdbcTemplate).withTableName("user_master")
		.usingColumns(new String[] { "email", "passwd","first_name", "last_name", "inserted_dt", "updated_dt",
				"isActive","contact" })
		.usingGeneratedKeyColumns(new String[] { "user_id" });
		
		Map<String,Object> insertParameters = new HashMap<String,Object> ();
		insertParameters.put("email", user.getEmail());
		insertParameters.put("passwd", user.getPassword());
		insertParameters.put("first_name", user.getFirstName());
		insertParameters.put("last_name", user.getLastName());
		insertParameters.put("inserted_dt", new Date());
		insertParameters.put("updated_dt", new Date());
		insertParameters.put("isActive", (user.isActive()?1:0));
		insertParameters.put("contact", user.getContact());
		
		int userId =  adduser.executeAndReturnKey(insertParameters).intValue();
		
		
		this.jdbcTemplate.update("INSERT INTO user_role_association(role_name, user_id) VALUES (?,?)",
				new Object[] { user.getRoleName(), userId});
		
		return userId;
		
	}

	public List<Users> getUsers() {
		List<Users> users = this.jdbcTemplate.query("SELECT * FROM user_master", new RowMapper<Users>() {
			public Users mapRow(ResultSet rs, int rowNum) throws SQLException {
				Users user = new Users();

				user.setUserId(rs.getInt("user_id"));
				user.setEmail(rs.getString("email"));
				user.setContact(rs.getString("contact"));
				user.setPassword(rs.getString("passwd"));
				user.setFirstName(rs.getString("first_name"));
				user.setLastName(rs.getString("last_name"));
				user.setCreatedDate(rs.getTimestamp("inserted_dt"));
				user.setUpdatedDate(rs.getTimestamp("updated_dt"));
				user.setActive(rs.getBoolean("isActive"));

				return user;
			}
		});
		return users;
	}

	public void updateUser(Users user) {
		this.jdbcTemplate.update(
				"UPDATE user_master SET email=?, first_name=?, last_name=?, updated_dt=?, isActive=?",
				new Object[] { user.getEmail(), user.getPassword(), user.getFirstName(), user.getLastName(),
						new Date(), Boolean.valueOf(user.isActive()) });
	}

	public void deleteUser(int userId) {
		this.jdbcTemplate.update("DELETE FROM user_master WHERE user_id =?", new Object[] { Integer.valueOf(userId) });
	}

	public Users getUser(String email) {
		String query = "SELECT * FROM user_master WHERE email=':email' or contact=':email'".replaceAll(":email", email);

		return ((Users) this.jdbcTemplate.query(query, new ResultSetExtractor<Users>() {
			public Users extractData(ResultSet rs) throws SQLException, DataAccessException {
				if (rs.next()) {
					Users user = new Users();
					user.setUserId(rs.getInt("user_id"));
					user.setEmail(rs.getString("email"));
					user.setPassword(rs.getString("passwd"));
					user.setFirstName(rs.getString("first_name"));
					user.setLastName(rs.getString("last_name"));
					user.setCreatedDate(rs.getDate("inserted_dt"));
					user.setUpdatedDate(rs.getDate("updated_dt"));
					user.setActive(rs.getBoolean("isActive"));

					return user;
				}
				return null;
			}
		}));
	}

	public List<Users> getUsersBasedOnJoinOfRoleAssociation(String columnName, String queryParam, int userId,
			boolean isAlmaAdmin) {
		List<Users> users = new ArrayList<Users>();
		try {
			if (columnName.equals("role_name"))
				columnName = "ura." + columnName;
			else {
				columnName = "um." + columnName;
			}

			if (isAlmaAdmin) {
				users = this.jdbcTemplate.query(
						"select um.user_id, um.email, um.contact, um.first_name, um.last_name, ura.role_name from user_master um inner join user_role_association ura on um.user_id = ura.user_id where :columnName like ':queryParam%'"
								.replace(":columnName", columnName).replace(":queryParam", queryParam),
						new RowMapper<Users>() {
							public Users mapRow(ResultSet rs, int rowNum) throws SQLException {
								Users user = new Users();
								user.setUserId(rs.getInt("user_id"));
								user.setEmail(rs.getString("email"));
								user.setContact(rs.getString("contact"));
								user.setFirstName(rs.getString("first_name"));
								user.setLastName(rs.getString("last_name"));

								user.setRoleName(rs.getString("role_name"));
								return user;
							}

						});
			} else {
				users = this.jdbcTemplate.query(
						"SELECT um.user_id, um.email, um.contact, um.first_name, um.last_name, ura.role_name FROM user_master um INNER JOIN user_role_association ura ON um.user_id = ura.user_id WHERE um.user_id in (SELECT sp.user_id FROM student_parent sp INNER JOIN branch b ON sp.school_id = b.school_id INNER JOIN admin_branch_association adm ON b.branch_id = adm.branch_id WHERE adm.user_id = ?) and :columnName like ':queryParam%'"
								.replace(":columnName", columnName).replace(":queryParam", queryParam),
						new Object[] { Integer.valueOf(userId) }, new RowMapper<Users>() {
							public Users mapRow(ResultSet rs, int rowNum) throws SQLException {
								Users user = new Users();
								user.setUserId(rs.getInt("user_id"));
								user.setEmail(rs.getString("email"));
								user.setContact(rs.getString("contact"));
								user.setFirstName(rs.getString("first_name"));
								user.setLastName(rs.getString("last_name"));

								user.setRoleName(rs.getString("role_name"));
								return user;
							}

						});
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return users;
	}

	public Users getUserRoleByID(int userId) {
		return ((Users) this.jdbcTemplate.query(
				"select um.email, um.first_name, um.last_name, ur.role_name from user_master um, user_role_association ur where um.user_id = ur.user_id and ur.user_id= ?",
				new Object[] { Integer.valueOf(userId) }, new ResultSetExtractor<Users>() {
					public Users extractData(ResultSet rs) throws SQLException, DataAccessException {
						if (rs.next()) {
							Users user = new Users();
							user.setEmail(rs.getString("email"));
							user.setFirstName(rs.getString("first_name"));
							user.setLastName(rs.getString("last_name"));
							user.setRoleName(rs.getString("role_name"));

							return user;
						}
						return null;
					}
				}));
	}

	public Users getIDByUserEmail(String parentEmail) {
		return ((Users) this.jdbcTemplate.query("select user_id from user_master where email=? or contact=?",
				new Object[] { parentEmail, parentEmail }, new ResultSetExtractor<Users>() {
					public Users extractData(ResultSet rs) throws SQLException, DataAccessException {
						if (rs.next()) {
							Users user = new Users();
							user.setUserId(rs.getInt("user_id"));
							return user;
						}
						return null;
					}
				}));
	}

	public List<Users> getUsersBasedOnSchoolAdmin(int userId) {
		List<Users> users = this.jdbcTemplate.query(
				"SELECT um.user_id,um.email,um.first_name,um.last_name,\tum.contact FROM user_master um WHERE um.user_id in (\tSELECT sp.user_id FROM student_parent sp INNER JOIN    branch b ON sp.school_id = b.school_id INNER JOIN    admin_branch_association adm ON b.branch_id = adm.branch_id    WHERE adm.user_id = ?)",
				new Object[] { Integer.valueOf(userId) }, new RowMapper<Users>() {
					public Users mapRow(ResultSet rs, int rowNum) throws SQLException {
						Users user = new Users();
						user.setUserId(rs.getInt("user_id"));
						user.setEmail(rs.getString("email"));
						user.setFirstName(rs.getString("first_name"));
						user.setLastName(rs.getString("last_name"));
						user.setContact(rs.getString("contact"));

						return user;
					}
				});
		return users;
	}

	public Users getEmail(String email) {
		String query = "SELECT * FROM user_master WHERE email=':email'".replaceAll(":email", email);
		return ((Users) this.jdbcTemplate.query(query, new ResultSetExtractor<Users>() {
			public Users extractData(ResultSet rs) throws SQLException, DataAccessException {
				if (rs.next()) {
					Users user = new Users();
					user.setUserId(rs.getInt("user_id"));
					user.setEmail(rs.getString("email"));
					user.setPassword(rs.getString("passwd"));
					user.setFirstName(rs.getString("first_name"));
					user.setLastName(rs.getString("last_name"));
					user.setCreatedDate(rs.getDate("inserted_dt"));
					user.setUpdatedDate(rs.getDate("updated_dt"));
					user.setActive(rs.getBoolean("isActive"));

					return user;
				}
				return null;
			}
		}));
	}

	public void updateToken(String pw, String email) {
		Users user = getIDByUserEmail(email);

		this.jdbcTemplate.update(" UPDATE user_master SET token=? where user_id=?",
				new Object[] { pw, Integer.valueOf(user.getUserId()) });
	}

	public int getUserIdByToken(String token) {
		int userId = ((Integer) this.jdbcTemplate.queryForObject("SELECT user_id FROM user_master WHERE token=? ",
				new Object[] { token }, Integer.class)).intValue();

		return userId;
	}

	public int updatePasswordForUser(int userId, String password) {
		int updatedRow = this.jdbcTemplate.update("UPDATE user_master SET passwd = ?, updated_dt = ? WHERE user_id = ?",
				new Object[] { this.passwordEncoder.encode(password), new Date(), Integer.valueOf(userId) });

		return updatedRow;
	}

	public Users getUserPasswdByUserid(int UserId) {
		return ((Users) this.jdbcTemplate.query("SELECT passwd FROM user_master WHERE user_id =?",
				new Object[] { Integer.valueOf(UserId) }, new ResultSetExtractor<Users>() {
					public Users extractData(ResultSet rs) throws SQLException, DataAccessException {
						if (rs.next()) {
							Users user = new Users();
							user.setPassword(rs.getString("passwd"));

							return user;
						}
						return null;
					}
				}));
	}

	public Users getUserByUserid(int user) {
		return ((Users) this.jdbcTemplate.query("SELECT * FROM user_master WHERE user_id=?",
				new Object[] { Integer.valueOf(user) }, new ResultSetExtractor<Users>() {
					public Users extractData(ResultSet rs) throws SQLException, DataAccessException {
						if (rs.next()) {
							Users user = new Users();
							user.setUserId(rs.getInt("user_id"));
							user.setEmail(rs.getString("email"));
							user.setPassword(rs.getString("passwd"));
							user.setFirstName(rs.getString("first_name"));
							user.setLastName(rs.getString("last_name"));
							user.setContact(rs.getString("contact"));
							user.setToken(rs.getString("token"));

							return user;
						}
						return null;
					}
				}));
	}

	public int updateUserProfile(String newPassword, String firstName, String lastName, String email, String contact,
			int userId) {
		int row = 0;

		if ((newPassword != null) && (!(newPassword.isEmpty()))) {
			row = this.jdbcTemplate.update(
					"UPDATE user_master SET passwd = ?, first_name=?,last_name=? ,email=?, contact=?, updated_dt=? where user_id = ?",
					new Object[] { this.passwordEncoder.encode(newPassword), firstName, lastName, email, contact,
							new Date(), Integer.valueOf(userId) });
		} else {
			row = this.jdbcTemplate.update(
					"UPDATE user_master SET first_name=?,last_name=? ,email=?, contact=?, updated_dt=? where user_id = ?",
					new Object[] { firstName, lastName, email, contact, new Date(), Integer.valueOf(userId) });
		}

		return row;
	}

	public void delete(String token) {
		this.jdbcTemplate.update("UPDATE user_master SET token = null where token=?", new Object[] { token });
	}

	public List<Users> getUserByRole(String role) {
		List<Users> users = this.jdbcTemplate.query(
				"SELECT email,contact FROM user_master WHERE user_id in ( SELECT user_id FROM user_role_association WHERE role_name = ? )",
				new Object[] { role }, new RowMapper<Users>() {
					public Users mapRow(ResultSet rs, int rowNum) throws SQLException {
						Users user = new Users();
						user.setEmail(rs.getString("email"));
						user.setContact(rs.getString("contact"));

						return user;
					}
				});
		return users;
	}

	@Override
	public int validateParentSchoolAssociation(String schoolUrl, Users u) {
		return (Integer) this.jdbcTemplate.query("select count(*) as count from school s, student_parent p WHERE s.school_url = ? and s.school_id = p.school_id and p.user_id = ?",
				new Object[] { schoolUrl, Integer.valueOf(u.getUserId()) }, new ResultSetExtractor<Integer>() {
					public Integer extractData(ResultSet rs) throws SQLException, DataAccessException {
						if (rs.next()) {
							return rs.getInt("count");
						}
						return 0;
					}
				});
	}

	@Override
	public int validateAdminSchoolAssociation(String schoolUrl, Users u) {
		return (Integer) this.jdbcTemplate.query("select count(*) as count from school s, admin_branch_association a WHERE s.school_url = ? and s.school_id = a.school_id and a.user_id = ?",
				new Object[] { schoolUrl, Integer.valueOf(u.getUserId()) }, new ResultSetExtractor<Integer>() {
					public Integer extractData(ResultSet rs) throws SQLException, DataAccessException {
						if (rs.next()) {
							return rs.getInt("count");
						}
						return 0;
					}
				});
	}

	@Override
	public String userAssociatedVerification(String role, String url, Users loginUser) {
		String assocation = "";
		if("Parent".equalsIgnoreCase(role)){
			assocation = this.jdbcTemplate.query("select s.school_url from school s, student_parent p WHERE s.school_id = p.school_id and p.user_id = ? and s.school_url=? LIMIT 1",
					new Object[] { Integer.valueOf(loginUser.getUserId()), url }, new ResultSetExtractor<String>() {
						public String extractData(ResultSet rs) throws SQLException, DataAccessException {
							if (rs.next()) {
								return rs.getString("school_url");
							}
							return "";
						}
					});
		}
		else if("SchoolAdmin".equalsIgnoreCase(role)){
			assocation = this.jdbcTemplate.query("select s.school_url from school s, admin_branch_association a WHERE s.school_id = a.school_id and a.user_id = ? and s.school_url=? LIMIT 1",
					new Object[] {Integer.valueOf(loginUser.getUserId()),url }, new ResultSetExtractor<String>() {
						public String extractData(ResultSet rs) throws SQLException, DataAccessException {
							if (rs.next()) {
								return rs.getString("school_url");
							}
							return "";
						}
					});
		}
		else if("Admin".equalsIgnoreCase(role)){
			assocation = this.jdbcTemplate.query("select a.role_name from user_role_association a WHERE a.user_id = ? and a.role_name='Alma'",
					new Object[] {Integer.valueOf(loginUser.getUserId()) }, new ResultSetExtractor<String>() {
						public String extractData(ResultSet rs) throws SQLException, DataAccessException {
							while (rs.next()) {
									return "AlmaAdmin";
							}
							return "";
						}
					});
		}
		return assocation;
	}

	@Override
	public int addContactUsRecord(ContactUs contactUs) {
		return this.jdbcTemplate.update(
				"INSERT INTO contact_us(name, email, phone, subject, message, insert_Date) VALUES (?, ?, ?, ?, ?,?)",
				new Object[] { contactUs.getName(),contactUs.getEmail(), contactUs.getPhone(), contactUs.getSubject(), contactUs.getMessage(),new Date()});
	
	}

	@Override
	public void encryptPassword() {
		List<Users> users = this.jdbcTemplate.query(
				"SELECT user_id, passwd FROM user_master WHERE isActive = 0",
				new Object[] { }, new RowMapper<Users>() {
					public Users mapRow(ResultSet rs, int rowNum) throws SQLException {
						Users user = new Users();
						user.setUserId(rs.getInt("user_id"));
						user.setPassword(rs.getString("passwd"));

						return user;
					}
				});
		
		for(Users u: users){
		this.jdbcTemplate.update(
				"UPDATE user_master SET passwd = ?, isActive = 1 where user_id = ?",
				new Object[] { this.passwordEncoder.encode(u.getPassword()), Integer.valueOf(u.getUserId()) });
		}
		
	}
}