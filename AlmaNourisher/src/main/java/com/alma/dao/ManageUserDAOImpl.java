package com.alma.dao;

import com.alma.exception.CustomGenericException;
import com.alma.model.ManageUser;
import com.alma.model.Users;
import com.alma.util.CommonUtil;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.sql.DataSource;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.NumberToTextConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

public class ManageUserDAOImpl implements ManageUserDAO {

	@Autowired
	private PasswordEncoder passwordEncoder;
	private JdbcTemplate jdbcTemplate;

	public ManageUserDAOImpl(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

	@Transactional
	public void CreateUser(ManageUser manageUser) {
		try {
			Number generatedUserId = null;

			System.out.println("populating to user master table +++++++++++++++++++++++++++++");

			SimpleJdbcInsert insertContactList = new SimpleJdbcInsert(this.jdbcTemplate)
					.withTableName("user_master").usingColumns(new String[] { "email", "passwd", "first_name",
							"last_name", "inserted_dt", "updated_dt", "isActive", "contact" })
					.usingGeneratedKeyColumns(new String[] { "user_id" });

			Map insertParameters = new HashMap();
			insertParameters.put("email", manageUser.getEmail());

			insertParameters.put("passwd", this.passwordEncoder.encode(manageUser.getPassword()));

			insertParameters.put("first_name", manageUser.getFirstName());
			insertParameters.put("last_name", manageUser.getLastName());
			insertParameters.put("inserted_dt", new Date());
			insertParameters.put("updated_dt", new Date());
			insertParameters.put("isActive", Boolean.valueOf(manageUser.isUserisActive()));
			insertParameters.put("contact", manageUser.getContact());

			generatedUserId = insertContactList.executeAndReturnKey(insertParameters);

			System.out.println("User created with id ===>" + generatedUserId);

			if (generatedUserId != null) {
				String sqlUserRoleAssociation = "INSERT INTO user_role_association(role_name,user_id) VALUES (?,?)";

				this.jdbcTemplate.update(sqlUserRoleAssociation,
						new Object[] { manageUser.getRole_name(), generatedUserId });

				System.out.println("---- user_role_association created for role ---- " + manageUser.getRole_name());

				if (manageUser.getRole_name().equals("SchoolAdmin")) {
					String sqlschoolupdate = "INSERT INTO admin_branch_association(branch_id, user_id) values(?,?)";
					this.jdbcTemplate.update(sqlschoolupdate,
							new Object[] { Integer.valueOf(manageUser.getBranchId()), generatedUserId });

					System.out.println("Branch associated to the user =======----===> " + manageUser.getBranchId());
				}
			}

		} catch (DuplicateKeyException dke) {
			throw new DuplicateKeyException("Duplicate key <br/>" + manageUser.toString());
		}
	}

	public void editUser(ManageUser manageUser) {
		System.out.println("updating user table");

		String sql = "UPDATE user_master SET passwd=?,first_name=?,last_name=?,inserted_dt=?,updated_dt=?,isActive=?,contact=? WHERE  user_id=? ";

		this.jdbcTemplate.update(sql,
				new Object[] { this.passwordEncoder.encode(manageUser.getPassword()), manageUser.getFirstName(),
						manageUser.getLastName(), new Date(), new Date(), Boolean.valueOf(manageUser.isUserisActive()),
						manageUser.getContact(), Integer.valueOf(manageUser.getUserid()) });

		System.out.println("edited user table" + manageUser.isUserisActive());

		String sqlUserAssociation = "UPDATE user_role_association SET user_id=?, role_name=? where user_id=?";

		this.jdbcTemplate.update(sqlUserAssociation, new Object[] { Integer.valueOf(manageUser.getUserid()),
				manageUser.getRole_name(), Integer.valueOf(manageUser.getUserid()) });
	}

	public ManageUser getUserDetailsByID(int userId) throws NullPointerException {
		ManageUser manageUser = (ManageUser) this.jdbcTemplate.query(
				" select um.email, um.first_name, um.last_name, ur.role_name, um.contact, um.passwd from user_master um, user_role_association ur where um.user_id = ur.user_id and um.user_id=?",
				new Object[] { Integer.valueOf(userId) }, new ResultSetExtractor() {
					public ManageUser extractData(ResultSet rs) throws SQLException, DataAccessException {
						if (rs.next()) {
							ManageUser user = new ManageUser();
							user.setEmail(rs.getString("email"));
							user.setFirstName(rs.getString("first_name"));
							user.setLastName(rs.getString("last_name"));
							user.setPassword(rs.getString("passwd"));
							user.setRole_name(rs.getString("role_name"));
							user.setContact(rs.getString("contact"));
							return user;
						}
						return null;
					}
				});
		if (manageUser == null) {
			throw new NullPointerException();
		}
		return manageUser;
	}

	public int encryptUserPassword() throws Exception {
		String sql = "SELECT * FROM user_master";

		int count = 0;

		List<ManageUser> users = this.jdbcTemplate.query(sql, new RowMapper() {
			public ManageUser mapRow(ResultSet rs, int rowNum) throws SQLException {
				ManageUser user = new ManageUser();
				user.setUserid(rs.getInt("user_id"));
				user.setPassword(rs.getString("passwd"));

				return user;
			}
		});
		if (users.size() > 0) {
			sql = "UPDATE user_master SET passwd=? WHERE user_id = ?";
			for (ManageUser user : users) {
				if (user.getPassword().length() != 60) {
					count = this.jdbcTemplate.update(sql, new Object[] {
							this.passwordEncoder.encode(user.getPassword()), Integer.valueOf(user.getUserid()) });
				}

			}

		}

		return count;
	}

	public void addImportedParent(Row row) throws DuplicateKeyException, CustomGenericException {
		ManageUser manageUser = new ManageUser();
		try {
			String email = row.getCell(0, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
			String contact = NumberToTextConverter.toText(row.getCell(1).getNumericCellValue());
			if (email.isEmpty()) {
				email = null;
			}
			if (contact.isEmpty()) {
				contact = null;
			}
			manageUser.setEmail(email);
			manageUser.setContact(contact);
			manageUser.setFirstName(row.getCell(2, Row.CREATE_NULL_AS_BLANK).getStringCellValue());
			manageUser.setLastName(row.getCell(3, Row.RETURN_BLANK_AS_NULL).getStringCellValue());
			manageUser.setUserisActive(true);

			System.out.println("===========================");
			System.out.println(manageUser.toString());
		} catch (NullPointerException npe) {
			npe.printStackTrace();
			throw new RuntimeException("Please check the contact number or email from xls/xlsx file: <br/>"
					+ manageUser.toString() + "======>" + npe.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Problem in fetching the values from xls/xlsx file: <br/>"
					+ manageUser.toString() + "======>" + e.getMessage());
		}

		addParentDetailsFromImportFile(manageUser);
	}

	@Transactional
	private void addParentDetailsFromImportFile(ManageUser manageUser)
			throws DuplicateKeyException, CustomGenericException {
		try {
			if ((manageUser.getEmail() == null) && (manageUser.getContact() == null)) {
				throw new CustomGenericException(
						"Both user email or contact shouldn't be empty. <br/>" + manageUser.toString());
			}

			if ((manageUser.getEmail() != null) && (manageUser.getContact() == null)) {
				throw new CustomGenericException(
						"Contact number should be there to create default password. <br/>" + manageUser.toString());
			}

			if ((manageUser.getEmail() != null) && (!(CommonUtil.isValidEmailAddress(manageUser.getEmail())))) {
				throw new CustomGenericException("Email provided is not valid. <br/>" + manageUser.toString());
			}

			if ((manageUser.getContact() != null) && (!(CommonUtil.isValidMobileNumber(manageUser.getContact())))) {
				throw new CustomGenericException(
						"Mobile number provided is not valid. <br/> Enter 10 digit mobile number. <br/>"
								+ manageUser.toString());
			}

			if (!(isUserExists(manageUser.getEmail(), manageUser.getContact()))) {
				manageUser.setPassword(manageUser.getContact());
				manageUser.setRole_name("Parent");
				CreateUser(manageUser);
			} else {
				throw new CustomGenericException(
						"User already exits with this email or contact :<br/>" + manageUser.toString());
			}
		} catch (DuplicateKeyException dke) {
			throw new DuplicateKeyException("Duplicate key " + manageUser.toString());
		}
	}

	private boolean isUserExists(String email, String contactNo) {
		boolean flag = false;
		Integer cnt = Integer.valueOf(0);

		if (email != null) {
			cnt = (Integer) this.jdbcTemplate.queryForObject("SELECT count(*) FROM user_master WHERE email=?",
					new Object[] { email }, Integer.class);
		} else {
			cnt = (Integer) this.jdbcTemplate.queryForObject("SELECT count(*) FROM user_master WHERE contact=?",
					new Object[] { contactNo }, Integer.class);
		}

		if (cnt.intValue() != 0) {
			flag = true;
		}
		return flag;
	}

	@Override
	public void changePassword(Users u) {
		String password = this.passwordEncoder.encode(u.getPassword());
		
		String sqlHealth = "UPDATE user_master SET passwd=? WHERE user_id=?";

		this.jdbcTemplate.update(sqlHealth,
				new Object[] {password,u.getUserId()});
		
	}
}
