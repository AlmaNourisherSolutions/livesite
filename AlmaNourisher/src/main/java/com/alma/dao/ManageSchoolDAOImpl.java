package com.alma.dao;

import com.alma.model.ManageSchool;
import com.alma.model.School;
import com.alma.model.Users;
import com.alma.util.CommonUtil;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletContext;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.web.multipart.MultipartFile;

public class ManageSchoolDAOImpl implements ManageSchoolDAO {

	@Autowired
	ServletContext context;
	private JdbcTemplate jdbcTemplate;
	KeyHolder holder = new GeneratedKeyHolder();
	
	@Autowired
	private UsersDAO userDAO;
	
	public ManageSchoolDAOImpl(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

	public List<ManageSchool> getSchoolDetails() {
		List<ManageSchool> schoollist = this.jdbcTemplate.query(
				"select s.*,u.user_id as admin_id, u.email as admin_email, u.first_name as admin_fname, u.last_name as admin_lname, u.contact as admin_phone "
				+ "from school s, admin_branch_association a, user_master u "
				+ "where s.school_id = a.school_id and a.level = 'school' and a.user_id = u.user_id order by inserted_dt desc",
				new RowMapper<ManageSchool>() {
					public ManageSchool mapRow(ResultSet rs, int rowNum) throws SQLException {
						ManageSchool schooldetails = new ManageSchool();
						schooldetails.setSchoolId(rs.getInt("school_id")); 
						schooldetails.setSchoolName(rs.getString("school_name"));
						schooldetails.setSchoolLogo(rs.getString("school_logo"));
						schooldetails.setSchoolBanner(rs.getString("school_banner"));
						schooldetails.setLandlineNumber(rs.getString("landline"));
						schooldetails.setMobileNumber(rs.getString("mobile_number"));
						schooldetails.setSchoolAddress(rs.getString("address"));
						schooldetails.setSchoolCity(rs.getString("city"));
						schooldetails.setSchoolState(rs.getString("state"));
						schooldetails.setSchoolCountry(rs.getString("country"));
						schooldetails.setSchoolPostalCode(rs.getString("postal_code"));
						schooldetails.setSchoolEmail(rs.getString("email"));
						schooldetails.setSchoolContactPerson(rs.getString("contactperson"));
						schooldetails.setSchoolActive((rs.getInt("isActive")==1)?true:false);
						schooldetails.setUrl(rs.getString("school_url"));
						
						Users schoolAdmin = new Users();
						schoolAdmin.setUserId(rs.getInt("admin_id"));
						schoolAdmin.setFirstName(rs.getString("admin_fname"));
						schoolAdmin.setLastName(rs.getString("admin_lname"));
						schoolAdmin.setEmail(rs.getString("admin_email"));
						schoolAdmin.setContact(rs.getString("admin_phone"));
						
						schooldetails.setSchoolAdmin(schoolAdmin);

						return schooldetails;
					}
				});
		return schoollist;
	}

	public List<ManageSchool> getSchoolList() {
		List<ManageSchool> schoollist = this.jdbcTemplate.query(
				"select s.* "
				+ "from school s "
				+ "where s.isActive = 1 order by s.inserted_dt desc",
				new RowMapper<ManageSchool>() {
					public ManageSchool mapRow(ResultSet rs, int rowNum) throws SQLException {
						ManageSchool schooldetails = new ManageSchool();
						schooldetails.setSchoolId(rs.getInt("school_id")); 
						schooldetails.setSchoolName(rs.getString("school_name"));
						schooldetails.setUrl(rs.getString("school_url"));
						schooldetails.setSchoolLogo(rs.getString("school_logo"));
						schooldetails.setSchoolBanner(rs.getString("school_banner"));
						schooldetails.setLandlineNumber(rs.getString("landline"));
						schooldetails.setMobileNumber(rs.getString("mobile_number"));
						schooldetails.setSchoolAddress(rs.getString("address"));
						schooldetails.setSchoolCity(rs.getString("city"));
						schooldetails.setSchoolState(rs.getString("state"));
						schooldetails.setSchoolCountry(rs.getString("country"));
						schooldetails.setSchoolPostalCode(rs.getString("postal_code"));
						schooldetails.setSchoolEmail(rs.getString("email"));
						schooldetails.setSchoolContactPerson(rs.getString("contactperson"));
						schooldetails.setSchoolActive((rs.getInt("isActive")==1)?true:false);

						return schooldetails;
					}
				});
		return schoollist;
	}

	public Number saveOrUpdate(ManageSchool manageSchool, MultipartFile schoolImage) {
		Number generatedFooId = null;
		
		if (manageSchool.getSchoolId() > 0) {
			System.out.println("Updated school id ----------->" + manageSchool.getSchoolId());
			System.out.println("school Active ----------->" + manageSchool.isSchoolActive());
			String sql = "UPDATE school SET school_name=?,school_url=?, address=?, mobile_number=?, email=?, contactperson=?, "
					+ "isActive=?, updated_dt = ?, school_logo=? WHERE school_id=?";

			this.jdbcTemplate.update(sql, new Object[] { manageSchool.getSchoolName(), manageSchool.getUrl(), manageSchool.getSchoolAddress(),
					manageSchool.getMobileNumber(), manageSchool.getSchoolEmail(), manageSchool.getSchoolContactPerson(), 
					Boolean.valueOf(manageSchool.isSchoolActive()),new Date(), manageSchool.getSchoolLogo(), manageSchool.getSchoolId() });
			
			return manageSchool.getSchoolId();
			
			
		} else {
			System.out.println("Added school ----------->" + manageSchool.getSchoolName());
			System.out.println("school Active ----------->" + manageSchool.isSchoolActive());
			SimpleJdbcInsert insertContactList = new SimpleJdbcInsert(this.jdbcTemplate).withTableName("school")
					.usingColumns(new String[] { "school_name", "school_url","address", "mobile_number", "email", "contactperson",
							"isActive", "inserted_dt", "updated_dt", "school_logo","school_banner" })
					.usingGeneratedKeyColumns(new String[] { "school_id" });

			Map<String,Object> insertParameters = new HashMap<String,Object> ();
			insertParameters.put("school_name", manageSchool.getSchoolName());
			insertParameters.put("school_url", manageSchool.getUrl());
			insertParameters.put("address", manageSchool.getSchoolAddress());
			insertParameters.put("mobile_number", manageSchool.getMobileNumber());
			insertParameters.put("email", manageSchool.getSchoolEmail());
			insertParameters.put("contactperson", manageSchool.getSchoolContactPerson());
			insertParameters.put("isActive", Boolean.valueOf(manageSchool.isSchoolActive()));
			insertParameters.put("inserted_dt", new Date());
			insertParameters.put("updated_dt", new Date());
			insertParameters.put("school_logo", manageSchool.getSchoolLogo());
			insertParameters.put("school_banner", manageSchool.getSchoolBanner());

			generatedFooId = insertContactList.executeAndReturnKey(insertParameters);
			
			Users user = null;
			if(manageSchool.getSchoolAdmin().getEmail()!=null){
				 user = this.userDAO.getIDByUserEmail(manageSchool.getSchoolAdmin().getEmail());
			}
			
			if(user!=null){
				this.jdbcTemplate.update("INSERT INTO `admin_branch_association`(`user_id`, `school_id`, `level`) VALUES (?,?,?)",
					new Object[] { user.getUserId(), generatedFooId,"school"});
			}
			else{
				//create new user
				manageSchool.getSchoolAdmin().setRoleName("SchoolAdmin");
				manageSchool.getSchoolAdmin().setActive(true);
				int userid = this.userDAO.addUser(manageSchool.getSchoolAdmin());
				this.jdbcTemplate.update("INSERT INTO `admin_branch_association`(`user_id`, `school_id`, `level`) VALUES (?,?,?)",
						new Object[] { userid, generatedFooId,"school"});
			}
		}

		System.out.println("Inserted school id ============------> " + generatedFooId);
		return generatedFooId;
	}

	@Override
	public void deleteSchool(int schoolId){
		this.jdbcTemplate.update("call delete_school (?)", schoolId);
	}
	
	public ManageSchool getSchoolById(String schoolId) throws NullPointerException {
		ManageSchool manageSchool = (ManageSchool) this.jdbcTemplate.query(
				"SELECT s.*, u.user_id as admin_id, u.email as admin_email, u.first_name as admin_fname, u.last_name as admin_lname, u.contact as admin_phone "
				+ "from school s, admin_branch_association a, user_master u "
				+ "where s.school_id = :schoolId and s.school_id = a.school_id and a.level = 'school' and a.user_id = u.user_id"
				.replace(":schoolId", schoolId),
				new ResultSetExtractor<ManageSchool>() {
					public ManageSchool extractData(ResultSet rs) throws SQLException, DataAccessException {
						if (rs.next()) {
							ManageSchool schooldetails = new ManageSchool();
							schooldetails.setSchoolAddress(rs.getString("address"));
							schooldetails.setMobileNumber(rs.getString("mobile_number"));
							schooldetails.setSchoolName(rs.getString("school_name"));
							schooldetails.setLandlineNumber(rs.getString("landline"));
							schooldetails.setSchoolId(rs.getInt("school_id"));
							schooldetails.setSchoolContactPerson(rs.getString("contactperson"));
							schooldetails.setSchoolEmail(rs.getString("email"));
							schooldetails.setSchoolActive(rs.getBoolean("isActive"));
							schooldetails.setSchoolLogo(rs.getString("school_logo"));
							schooldetails.setUrl(rs.getString("school_url"));
							
							Users schoolAdmin = new Users();
							schoolAdmin.setUserId(rs.getInt("admin_id"));
							schoolAdmin.setFirstName(rs.getString("admin_fname"));
							schoolAdmin.setLastName(rs.getString("admin_lname"));
							schoolAdmin.setEmail(rs.getString("admin_email"));
							schoolAdmin.setContact(rs.getString("admin_phone"));
							
							schooldetails.setSchoolAdmin(schoolAdmin);

							return schooldetails;
						}

						return null;
					}
				});
		if (manageSchool == null) {
			throw new NullPointerException();
		}

		return manageSchool;
	}

	public List<ManageSchool> getSchoolBasedOnWildCardColumnSearch(String sqlQuery) {
		List schoollist = this.jdbcTemplate.query(sqlQuery, new RowMapper<ManageSchool>() {
			public ManageSchool mapRow(ResultSet rs, int rowNum) throws SQLException {
				ManageSchool schooldetails = new ManageSchool();
				schooldetails.setSchoolAddress(rs.getString("address"));
				schooldetails.setSchoolLogo(
						((rs.getString("school_logo") == null) || (rs.getString("school_logo").isEmpty()))
								? ManageSchoolDAOImpl.this.context.getContextPath()
										+ "/resources/images/default-school.jpg"
								: CommonUtil.appendBase64StringInImageUrl(rs.getString("school_logo")));

				schooldetails.setMobileNumber(rs.getString("mobile_number"));
				schooldetails.setSchoolName(rs.getString("school_name"));
				schooldetails
						.setLandlineNumber(((rs.getString("landline") == null) || (rs.getString("landline").isEmpty()))
								? "" : rs.getString("landline"));
				schooldetails.setSchoolId(rs.getInt("school_id"));
				schooldetails.setSchoolContactPerson(rs.getString("contactperson"));
				schooldetails.setSchoolEmail(rs.getString("email"));

				return schooldetails;
			}
		});
		return schoollist;
	}

	public void editSchool(ManageSchool manageSchool) {
		System.out.println("Edited school id ----------->" + manageSchool.getSchoolId());

		this.jdbcTemplate.update(
				"UPDATE school SET school_name=?, mobile_number=?, address=?, updated_dt=?, isActive=?, email=?, contactperson=?, school_logo=? WHERE school_id=?",
				new Object[] { manageSchool.getSchoolName(), manageSchool.getMobileNumber(),
						manageSchool.getSchoolAddress(), new Date(), Boolean.valueOf(manageSchool.isSchoolActive()),
						manageSchool.getSchoolEmail(), manageSchool.getSchoolContactPerson(),
						Integer.valueOf(manageSchool.getSchoolId()) });
	}

	public void editSchool(ManageSchool manageSchool, MultipartFile schoolImage) {
		System.out.println("Edited school id ----------->" + manageSchool.getSchoolId());

		String schoolLogo = null;
		if (!(schoolImage.isEmpty())) {
			schoolLogo = CommonUtil.convertFileToBase64(schoolImage);
		}

		this.jdbcTemplate.update(
				"UPDATE school SET school_name=?, mobile_number=?, address=?, updated_dt=?, isActive=?, email=?, contactperson=?, school_logo=? WHERE school_id=?",
				new Object[] { manageSchool.getSchoolName(), manageSchool.getMobileNumber(),
						manageSchool.getSchoolAddress(), new Date(), Boolean.valueOf(manageSchool.isSchoolActive()),
						manageSchool.getSchoolEmail(), manageSchool.getSchoolContactPerson(),
						(schoolLogo == null) ? "" : schoolLogo, Integer.valueOf(manageSchool.getSchoolId()) });
	}

	public String getSchoolLogo(int schoolId) {
		String schoolLogo = (String) this.jdbcTemplate.queryForObject(
				"SELECT school_logo FROM school WHERE school_id= ? ", new Object[] { Integer.valueOf(schoolId) },
				String.class);

		if ((schoolLogo == null) || (schoolLogo.isEmpty()))
			schoolLogo = "/resources/images/default-school.jpg";
		else {
			schoolLogo = CommonUtil.appendBase64StringInImageUrl(schoolLogo);
		}
		return schoolLogo;
	}

	public int totalSchoolsCount() {
		String sql = "select count(*) as count from school where isActive=1";
		return this.jdbcTemplate.queryForObject(sql,Integer.class);
	}

	@Override
	public String getSchoolName(String schoolName) {
		String sql = "select school_name from school where school_url = ':schoolName'".replace(":schoolName", schoolName);
		return (String)this.jdbcTemplate.queryForList(sql).get(0).get("school_name");
	}

	@Override
	public ManageSchool getSchoolFromUrl(String schoolUrl) {
		String sql = "select * from school where school_url = ':schoolUrl'".replace(":schoolUrl", schoolUrl);
		Map<String, Object> record = this.jdbcTemplate.queryForList(sql).get(0);
		
		ManageSchool m = new ManageSchool();
		m.setSchoolId((Integer)record.get("school_id"));
		m.setSchoolName((String)record.get("school_name"));
		m.setSchoolBanner((String)record.get("school_banner"));
		m.setSchoolLogo((String)record.get("school_logo"));
		m.setUrl((String)record.get("school_url"));
		
		
		return m;
	}
}
