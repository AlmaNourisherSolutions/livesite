package com.alma.dao;

import com.alma.model.Branch;
import com.alma.model.Users;

import java.sql.ResultSet;
import java.sql.SQLException;
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
import org.springframework.transaction.annotation.Transactional;

public class BranchDAOImpl implements BranchDAO {
	private JdbcTemplate jdbcTemplate;
	
	@Autowired
	private UsersDAO userDAO;

	public BranchDAOImpl(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

	public List<Branch> getBranchBasedOnSchoolId(String schoolId) {
		List<Branch> branches = this.jdbcTemplate.query(
				"select b.*, s.school_name, u.user_id as admin_id, u.email as admin_email, u.first_name as admin_fname, u.last_name as admin_lname, u.contact as admin_phone from branch b, school s, admin_branch_association a, user_master u where "
				+ "b.school_id = s.school_id and a.level = 'branch' and a.branch_id = b.branch_id and a.user_id = u.user_id and b.school_id = :schoolId ".replace(":schoolId", schoolId), new RowMapper<Branch>() {
					public Branch mapRow(ResultSet rs, int rowNum) throws SQLException {
						Branch branch = new Branch();
						branch.setBranchId(rs.getInt("branch_id"));
						branch.setBranchName(rs.getString("branch_name"));
						branch.setBranchDescription(rs.getString("branch_description"));
						branch.setSchoolId(rs.getInt("school_id"));
						branch.setBranchEmail(rs.getString("email"));
						branch.setBranchLandlineNumber(rs.getString("landline"));
						branch.setBranchMobileNumber(rs.getString("mobile_number"));
						branch.setBranchAddress(rs.getString("address"));
						branch.setBranchCity(rs.getString("city"));
						branch.setBranchState(rs.getString("state"));
						branch.setBranchCountry(rs.getString("country"));
						branch.setBranchPostalCode(rs.getString("postal_code"));
						branch.setBranchIsActive(rs.getBoolean("isActive"));
						branch.setSchoolName(rs.getString("school_name"));
						
						Users schoolAdmin = new Users();
						schoolAdmin.setUserId(rs.getInt("admin_id"));
						schoolAdmin.setFirstName(rs.getString("admin_fname"));
						schoolAdmin.setLastName(rs.getString("admin_lname"));
						schoolAdmin.setEmail(rs.getString("admin_email"));
						schoolAdmin.setContact(rs.getString("admin_phone"));
						
						branch.setSchoolAdmin(schoolAdmin);
						return branch;
					}
				});
		return branches;
	}

	@Override
	public List<String> getClassBasedOnBranchId(String branchId) {
		List<String> classes = this.jdbcTemplate.query(
				"select * from class where branch_id = :branchId".replace(":branchId", branchId), new RowMapper<String>() {
					public String mapRow(ResultSet rs, int rowNum) throws SQLException {
						String className = rs.getString("class_name");
						return className;
					}
				});
		return classes;
	}
	
	public Branch getSchoolAndBranchBasedOnSchoolAdmin(int userId) {
		return ((Branch) this.jdbcTemplate.query(
				"select school.school_id, school.school_name, branch.branch_id,branch.branch_name from branch branch, school school where school.school_id = branch.school_id and branch_id = (select branch_id from  admin_branch_association where user_id = ?)",
				new Object[] { Integer.valueOf(userId) }, new ResultSetExtractor<Branch>() {
					public Branch extractData(ResultSet rs) throws SQLException, DataAccessException {
						if (rs.next()) {
							Branch branch = new Branch();
							branch.setSchoolId(rs.getInt("school_id"));
							branch.setSchoolName(rs.getString("school_name"));
							branch.setBranchId(rs.getInt("branch_id"));
							branch.setBranchName(rs.getString("branch_name"));
							return branch;
						}
						return null;
					}
				}));
	}

	public Branch getSchoolAndBranchBasedOnIds(String schoolId, String branchId) {
		return ((Branch) this.jdbcTemplate
				.query("select school.school_id, school.school_name, branch.branch_id, branch.branch_name from branch branch, school school where school.school_id = :schoolId and branch.branch_id = :branchId"
						.replace(":schoolId", schoolId).replace(":branchId", branchId), new ResultSetExtractor<Branch>() {
							public Branch extractData(ResultSet rs) throws SQLException, DataAccessException {
								if (rs.next()) {
									Branch branch = new Branch();
									branch.setSchoolId(rs.getInt("school_id"));
									branch.setSchoolName(rs.getString("school_name"));
									branch.setBranchId(rs.getInt("branch_id"));
									branch.setBranchName(rs.getString("branch_name"));
									return branch;
								}
								return null;
							}
						}));
	}

	@Transactional
	public Number saveOrUpdate(Branch branch) {
		Number generatedBranchId = null;

		System.out.println("School id for branch " + branch.getSchoolId());
		
		if (branch.getBranchId() > 0) {
			System.out.println("Updated branch id ----------->" + branch.getBranchId());
			System.out.println("branch Active ----------->" + branch.isBranchIsActive());
			String sql = "UPDATE branch SET branch_name=?,branch_description=?, address=?, mobile_number=?, email=?, "
					+ "isActive=?, updated_dt = ? WHERE branch_id=?";

			this.jdbcTemplate.update(sql, new Object[] { branch.getBranchName(), branch.getBranchName(), branch.getBranchAddress(),
					branch.getBranchMobileNumber(), branch.getBranchEmail(), 
					Boolean.valueOf(branch.isBranchIsActive()),new Date(), branch.getBranchId() });
			
			this.jdbcTemplate.update("DELETE FROM class where branch_id = ?", new Object[] {branch.getBranchId()});
			for(String cls: branch.getClasses()){
				this.jdbcTemplate.update("INSERT INTO class(class_name, branch_id, school_id, inserted_dt, updated_dt, isActive) VALUES (?,?,?,?,?,?)",
						new Object[] { cls, branch.getBranchId(),Integer.valueOf(branch.getSchoolId()), new Date(), new Date(), 1});
			}
			
			return branch.getBranchId();
			
			
		} else {

			SimpleJdbcInsert insertContactList = new SimpleJdbcInsert(this.jdbcTemplate).withTableName("branch")
					.usingColumns(new String[] { "branch_name", "branch_description", "school_id", "email", "mobile_number",
							"address", "inserted_dt", "updated_dt", "isActive" })
					.usingGeneratedKeyColumns(new String[] { "branch_id" });
	
			Map<String,Object> insertParameters = new HashMap<String,Object>();
			insertParameters.put("branch_name", branch.getBranchName());
			insertParameters.put("branch_description", branch.getBranchName());
			insertParameters.put("school_id", Integer.valueOf(branch.getSchoolId()));
			insertParameters.put("email", branch.getBranchEmail());
			insertParameters.put("mobile_number", branch.getBranchMobileNumber());
			insertParameters.put("address", branch.getBranchAddress());
			insertParameters.put("inserted_dt", new Date());
			insertParameters.put("updated_dt", new Date());
			insertParameters.put("isActive", Boolean.valueOf(branch.isBranchIsActive()));
	
			generatedBranchId = insertContactList.executeAndReturnKey(insertParameters);
			
			for(String cls: branch.getClasses()){
				this.jdbcTemplate.update("INSERT INTO class(class_name, branch_id, school_id, inserted_dt, updated_dt, isActive) VALUES (?,?,?,?,?,?)",
						new Object[] { cls, generatedBranchId,Integer.valueOf(branch.getSchoolId()), new Date(), new Date(), 1});
			}
			
			Users user = null;
			if(branch.getSchoolAdmin().getEmail()!=null){
				 user = this.userDAO.getIDByUserEmail(branch.getSchoolAdmin().getEmail());
			}
			
			if(user!=null){
				this.jdbcTemplate.update("INSERT INTO admin_branch_association(user_id, branch_id, school_id, level) VALUES (?,?,?,?)",
					new Object[] { user.getUserId(), generatedBranchId,Integer.valueOf(branch.getSchoolId()), "branch"});
			}
			else{
				//create new user
				branch.getSchoolAdmin().setRoleName("SchoolAdmin");
				branch.getSchoolAdmin().setActive(true);
				int userid = this.userDAO.addUser(branch.getSchoolAdmin());
				this.jdbcTemplate.update("INSERT INTO admin_branch_association(user_id, branch_id, school_id, level) VALUES (?,?,?,?)",
						new Object[] { userid, generatedBranchId, Integer.valueOf(branch.getSchoolId()),"branch"});
			}
		}
		return generatedBranchId;

	}

	@Override
	public List<String> getClassLookup() {
		List<String> classes = this.jdbcTemplate.query(
				"select name from class_lookup", new RowMapper<String>() {
					public String mapRow(ResultSet rs, int rowNum) throws SQLException {
						return rs.getString("name");
					}
				});
		return classes;
	}
	
	@Override
	public void deleteSchoolBranch(int branchId){
		this.jdbcTemplate.update("call delete_branch (?)", branchId);
	}
	
}

