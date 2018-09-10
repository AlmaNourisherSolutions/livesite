package com.alma.dao;

import com.alma.model.UserRoleAssociation;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import javax.sql.DataSource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

public class UserRoleAssociationDAOImpl implements UserRoleAssociationDAO {
	private JdbcTemplate jdbcTemplate;

	public UserRoleAssociationDAOImpl(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

	public List<UserRoleAssociation> getRolesByUserID(int userId) {
		List roles = this.jdbcTemplate.query("SELECT role_name FROM user_role_association WHERE user_id=?",
				new Object[] { Integer.valueOf(userId) }, new RowMapper() {
					public UserRoleAssociation mapRow(ResultSet rs, int rowNum) throws SQLException {
						UserRoleAssociation role = new UserRoleAssociation();
						role.setRoleName(rs.getString("role_name"));
						return role;
					}
				});
		return roles;
	}

	public boolean isSchoolAdmin(List<UserRoleAssociation> roles) {
		boolean flag = false;
		for (UserRoleAssociation role : roles) {
			if (role.getRoleName().equals("SchoolAdmin")) {
				flag = true;
				break;
			}
		}
		return flag;
	}

	public boolean isAlmaAdmin(List<UserRoleAssociation> roles) {
		boolean flag = false;
		for (UserRoleAssociation role : roles) {
			if (role.getRoleName().equals("Alma")) {
				flag = true;
				break;
			}
		}
		return flag;
	}

	public boolean isParent(List<UserRoleAssociation> roles) {
		boolean flag = false;
		for (UserRoleAssociation role : roles) {
			if (role.getRoleName().equals("Parent")) {
				flag = true;
				break;
			}
		}
		return flag;
	}
}
