package com.alma.dao;

import com.alma.model.ManageSchool;
import com.alma.model.Student;
import com.alma.model.StudentHealth;
import com.alma.model.Users;
import com.alma.model.WellnessLookupAssociation;
import com.alma.util.CommonUtil;

import java.math.BigInteger;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;
import java.util.List;
import javax.servlet.ServletContext;
import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

public class StudentDAOImpl implements StudentDAO {
	private JdbcTemplate jdbcTemplate;
	private Logger log = Logger.getLogger(StudentDAOImpl.class);

	@Autowired
	private ManageSchoolDAO manageSchoolDAO;
	
	@Autowired
	private UsersDAO userDAO;

	@Autowired
	private WellnessLookupAssociationDAO wellnessLookupAssociationDAO;

	@Autowired
	ServletContext context;

	public StudentDAOImpl(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

	public Student getStudentsByRollIdSchoolId(String rollId, int schoolId) {
		return ((Student) this.jdbcTemplate.query(
				"SELECT studentId, student_roll_id, first_name, last_name, school_id, branch_id, class_name,section_name, profile_image, gender, DATE_FORMAT(date_of_birth,'%Y-%m-%d') date_of_birth , inserted_dt, updated_dt, isActive   from student WHERE student_roll_id=':studentRollId' AND school_id=:schoolId"
						.replace(":studentRollId", rollId).replace(":schoolId", String.valueOf(schoolId)),
				new ResultSetExtractor<Student>() {
					public Student extractData(ResultSet rs) throws SQLException, DataAccessException {
						if (rs.next()) {
							Student student = new Student();
							student.setStudentId(rs.getInt("studentId"));
							student.setStudentRollId(rs.getString("student_roll_id"));
							student.setFirstName(rs.getString("first_name"));
							student.setLastName(rs.getString("last_name"));
							student.setSchoolId(rs.getInt("school_id"));
							student.setBranchId(rs.getInt("branch_id"));
							student.setClassName(rs.getString("class_name"));
							if ((rs.getString("section_name") == null) || (rs.getString("section_name").isEmpty()))
								student.setSectionName(rs.getString("section_name"));
							else {
								student.setSectionName("'" + rs.getString("section_name") + "'");
							}
							student.setProfileImage(rs.getString("profile_image"));
							student.setGender(rs.getString("gender"));
							student.setDateOfBirth(rs.getDate("date_of_birth"));
							student.setCreatedDate(rs.getTimestamp("inserted_dt"));
							student.setUpdatedDate(rs.getTimestamp("updated_dt"));
							student.setActive(rs.getBoolean("isActive"));

							student.setSchoolLogo(
									StudentDAOImpl.this.manageSchoolDAO.getSchoolLogo(rs.getInt("school_id")));

							return student;
						}

						return null;
					}
				}));
	}

	public List<Student> getRecentAddedStudent(int schoolId, int branchId) {
		List<Student> students = this.jdbcTemplate.query(
				"SELECT * FROM student where school_id=? AND branch_id=? ORDER BY inserted_dt DESC LIMIT 4",
				new Object[] { Integer.valueOf(schoolId), Integer.valueOf(branchId) }, new RowMapper<Student>() {
					public Student mapRow(ResultSet rs, int rowNum) throws SQLException {
						Student student = new Student();
						student.setStudentId(rs.getInt("studentId"));
						student.setStudentRollId(rs.getString("student_roll_id"));
						student.setFirstName(rs.getString("first_name"));
						student.setLastName(rs.getString("last_name"));
						student.setSchoolId(rs.getInt("school_id"));
						student.setBranchId(rs.getInt("branch_id"));
						student.setClassName(rs.getString("class_name"));
						if ((rs.getString("section_name") == null) || (rs.getString("section_name").isEmpty()))
							student.setSectionName(rs.getString("section_name"));
						else {
							student.setSectionName("'" + rs.getString("section_name") + "'");
						}
						student.setProfileImage(rs.getString("profile_image"));
						student.setGender(rs.getString("gender"));
						student.setDateOfBirth(rs.getDate("date_of_birth"));
						student.setCreatedDate(rs.getTimestamp("inserted_dt"));
						student.setUpdatedDate(rs.getTimestamp("updated_dt"));
						student.setActive(rs.getBoolean("isActive"));

						return student;
					}
				});
		return students;
	}

	public List<Student> getStudentsBasedOnSchoolAdmin(int userId, String limitNumber) {
		List<Student> students = this.jdbcTemplate
				.query("SELECT * FROM student student, branch branch where student.branch_id = branch.branch_id  and branch.branch_id in (SELECT branch_id FROM admin_branch_association where user_id = ?) order by class_name asc, first_name asc limit :limitNo"
						.replace(":limitNo", limitNumber), new Object[] { Integer.valueOf(userId) }, new RowMapper<Student>() {
							public Student mapRow(ResultSet rs, int rowNum) throws SQLException {
								Student student = new Student();
								student.setStudentId(rs.getInt("studentId"));
								student.setStudentRollId(rs.getString("student_roll_id"));
								student.setFirstName(rs.getString("first_name"));
								student.setLastName(rs.getString("last_name"));
								student.setSchoolId(rs.getInt("school_id"));
								student.setBranchId(rs.getInt("branch_id"));
								student.setClassName(rs.getString("class_name"));

								if ((rs.getString("section_name") == null) || (rs.getString("section_name").isEmpty()))
									student.setSectionName(rs.getString("section_name"));
								else {
									student.setSectionName("'" + rs.getString("section_name") + "'");
								}
								student.setProfileImage(rs.getString("profile_image"));
								student.setGender(rs.getString("gender"));
								student.setDateOfBirth(rs.getDate("date_of_birth"));
								student.setCreatedDate(rs.getTimestamp("inserted_dt"));
								student.setUpdatedDate(rs.getTimestamp("updated_dt"));
								student.setActive(rs.getBoolean("isActive"));

								return student;
							}
						});
		return students;
	}

	public List<Student> getStudentsBasedOnWildCardSearch(String query) {
		List<Student> students = this.jdbcTemplate.query(query, new RowMapper<Student>() {
			public Student mapRow(ResultSet rs, int rowNum) throws SQLException {
				Student student = new Student();
				student.setStudentId(rs.getInt("studentId"));
				student.setStudentRollId(rs.getString("student_roll_id"));
				student.setFirstName(rs.getString("first_name"));
				student.setLastName(rs.getString("last_name"));
				student.setSchoolId(rs.getInt("school_id"));
				student.setBranchId(rs.getInt("branch_id"));
				student.setClassName(rs.getString("class_name"));
				if ((rs.getString("section_name") == null) || (rs.getString("section_name").isEmpty()))
					student.setSectionName(rs.getString("section_name"));
				else {
					student.setSectionName("'" + rs.getString("section_name") + "'");
				}
				student.setProfileImage(rs.getString("profile_image"));
				student.setGender(rs.getString("gender"));
				student.setDateOfBirth(rs.getDate("date_of_birth"));
				student.setCreatedDate(rs.getTimestamp("inserted_dt"));
				student.setUpdatedDate(rs.getTimestamp("updated_dt"));
				student.setActive(rs.getBoolean("isActive"));

				return student;
			}
		});
		return students;
	}

	public int totalStudentsCount(int userId) {
		String sql = "select count(*) from student student, branch branch where student.branch_id = branch.branch_id  and branch.branch_id in (SELECT branch_id FROM admin_branch_association where user_id = ?)";
		return this.jdbcTemplate.queryForObject(sql, new Object[] { Integer.valueOf(userId) },Integer.class);
	}

	public int totalStudentsCount() {
		String sql = "select count(*) from student where isActive = 1";
		return this.jdbcTemplate.queryForObject(sql,Integer.class);
	}

	public List<Student> getAllStudents(String limitNumber) {
		List<Student> students = this.jdbcTemplate.query("SELECT s.*,  u.contact, u.email,u.contact, u.first_name as p_first_name, u.last_name as p_last_name, u.user_id as p_user_id, sch.school_name, b.branch_name "
				+ "FROM student s, student_parent p, school sch, branch b, user_master u "
				+ "where s.school_id = sch.school_id and s.branch_id = b.branch_id and s.student_roll_id = p.student_roll_id "
				+ "and p.user_id = u.user_id order by inserted_dt desc",
				new RowMapper<Student>() {
					public Student mapRow(ResultSet rs, int rowNum) throws SQLException {
						Student student = new Student();
						student.setStudentId(rs.getInt("studentId"));
						student.setStudentRollId(rs.getString("student_roll_id"));
						student.setFirstName(rs.getString("first_name"));
						student.setLastName(rs.getString("last_name"));
						student.setSchoolId(rs.getInt("school_id"));
						student.setBranchId(rs.getInt("branch_id"));
						student.setClassName(rs.getString("class_name"));
						if ((rs.getString("section_name") == null) || (rs.getString("section_name").isEmpty()))
							student.setSectionName(rs.getString("section_name"));
						else {
							student.setSectionName("'" + rs.getString("section_name") + "'");
						}
						student.setProfileImage(rs.getString("profile_image"));
						student.setGender(rs.getString("gender"));
						student.setDateOfBirth(rs.getDate("date_of_birth"));
						student.setCreatedDate(rs.getTimestamp("inserted_dt"));
						student.setUpdatedDate(rs.getTimestamp("updated_dt"));
						student.setActive(rs.getBoolean("isActive"));
						
						student.setSchoolName(rs.getString("school_name"));
						student.setBranchName(rs.getString("branch_name"));
						
						Users p = new Users();
						p.setUserId(rs.getInt("p_user_id"));
						p.setContact(rs.getString("contact"));
						p.setFirstName(rs.getString("p_first_name"));
						p.setLastName(rs.getString("p_last_name"));
						p.setEmail(rs.getString("email"));
						
						student.setParent(p);

						return student;
					}
				});
		return students;
	}

	public List<Student> getGenderCount(int schoolId, int branchId) {
		String query = null;
		Object[] object = null;
		if (branchId != 0) {
			query = "SELECT gender, count(*) from student where school_id = ? and branch_id = ? group by gender";
			object = new Object[] { Integer.valueOf(schoolId), Integer.valueOf(branchId) };
		} else {
			query = "SELECT gender,count(*) FROM student WHERE school_id = ? group by gender";
			object = new Object[] { Integer.valueOf(schoolId) };
		}

		List<Student> students = this.jdbcTemplate.query(query, object, new RowMapper<Student>() {
			public Student mapRow(ResultSet rs, int rowNum) throws SQLException {
				Student student = new Student();

				student.setGender(rs.getString("gender"));
				
				student.setCount(rs.getInt("count(*)"));
				return student;
			}
		});
		return students;
	}
	
	public StudentHealth getStudentsHealthByRollIdSchoolId(String rollId, int schoolId) {
		return ((StudentHealth) this.jdbcTemplate.query(
				"SELECT * FROM student_health WHERE student_roll_id=':studentRollId' AND school_id=:schoolId"
						.replace(":studentRollId", rollId).replace(":schoolId", String.valueOf(schoolId)),
				new ResultSetExtractor<StudentHealth>() {
					public StudentHealth extractData(ResultSet rs) throws SQLException, DataAccessException {
						if (rs.next()) {
							StudentHealth studentHealth = new StudentHealth();
							studentHealth.setStudentRollId(rs.getString("student_roll_id"));
							studentHealth.setSchollId(rs.getInt("school_id"));
							studentHealth.setClassName(rs.getString("class_name"));
							studentHealth.setAge(rs.getDouble("age"));
							studentHealth.setHeight(rs.getDouble("height"));
							studentHealth.setHeightUnit(rs.getString("height_unit"));
							studentHealth.setWeight(rs.getDouble("weight"));
							studentHealth.setBmi(rs.getDouble("bmi"));
							studentHealth.setBmiPercentile(rs.getDouble("bmi_percentile"));
							studentHealth.setIbw(rs.getString("ibw"));
							studentHealth.setWeightUnit(rs.getString("weight_unit"));
							studentHealth.setDateOfMeasurement(rs.getDate("date_of_measurement"));
							studentHealth.setCategory(rs.getString("category"));
							return studentHealth;
						}
						return null;
					}
				}));
	}

	@Override
	public BigInteger createStudent(Student studentDetails) {

		BigInteger studentId = null;
		try {
			this.log.debug("Populating student table .....");
			String sql = "INSERT INTO student (student_roll_id, first_name, last_name, school_id, branch_id, class_name, section_name, gender, date_of_birth,inserted_dt,updated_dt,isActive,profile_image) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?)";

			KeyHolder keyHolder = new GeneratedKeyHolder();
			this.jdbcTemplate.update(new PreparedStatementCreator() {
			        public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
			            PreparedStatement ps =
			                connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
			            ps.setString(1, studentDetails.getStudentRollId());
			            ps.setString(2, studentDetails.getFirstName());
			            ps.setString(3, studentDetails.getLastName());
			            Date utilDate = new Date();
			            ps.setInt(4,  studentDetails.getSchoolId());
			            ps.setInt(5, Integer.valueOf(studentDetails.getBranchId()));
			            ps.setString(6, studentDetails.getClassName());
			            ps.setString(7, studentDetails.getSectionName());
			            ps.setString(8, studentDetails.getGender());
			            ps.setDate(9, new java.sql.Date(studentDetails.getDOB().getTime()));
			            ps.setDate(10, new java.sql.Date(utilDate.getTime()));
			            ps.setDate(11, new java.sql.Date(utilDate.getTime()));
			            ps.setBoolean(12, Boolean.valueOf(studentDetails.isActive()));
			            ps.setString(13, studentDetails.getProfileImage());
			            return ps;
			        }
			    },
			    keyHolder);
			studentId = (BigInteger) keyHolder.getKey();

			this.log.debug("Populating student Health table table .....");
			this.log.debug("-->" + studentDetails.getStudentsHealth().getAge() + studentDetails.getStudentsHealth().getBmi());

			String sqlHealth = "insert into student_health (student_roll_id, school_id, class_name, age, height, height_unit, weight, weight_unit,bmi, bmi_percentile, ibw, bmi_type,date_of_measurement, category) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

			WellnessLookupAssociation wellness = new WellnessLookupAssociation();
			if(studentDetails.getStudentsHealth().getBmi()>0)
			 wellness = this.wellnessLookupAssociationDAO.getWellnessReportCategory(
					studentDetails.getGender(), new Double(studentDetails.getStudentsHealth().getAge()).intValue(),
					new Double(studentDetails.getStudentsHealth().getBmiPercentile()).intValue());

			this.jdbcTemplate.update(sqlHealth,
					new Object[] { studentDetails.getStudentRollId(), studentDetails.getSchoolId(),
							studentDetails.getClassName(), Double.valueOf(studentDetails.getStudentsHealth().getAge()),
							Double.valueOf(studentDetails.getStudentsHealth().getHeight()), studentDetails.getStudentsHealth().getHeightUnit(),
							Double.valueOf(studentDetails.getStudentsHealth().getWeight()), studentDetails.getStudentsHealth().getWeightUnit(),
							Double.valueOf(studentDetails.getStudentsHealth().getBmi()), Double.valueOf(studentDetails.getStudentsHealth().getBmiPercentile()),
							studentDetails.getStudentsHealth().getIbw(), studentDetails.getStudentsHealth().getBmiType(), new Date(),
							(wellness.getCategory() == null) ? "" : wellness.getCategory() });

			this.log.debug("Populating student Health Histoiry table table table .....");

			String sqlHealthHistory = "insert into student_health_history (student_roll_id, school_id, class_name, age, height, height_unit, weight, weight_unit,bmi, bmi_percentile, ibw, bmi_type,date_of_measurement,category) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

			this.jdbcTemplate.update(sqlHealthHistory,
					new Object[] { studentDetails.getStudentRollId(), studentDetails.getSchoolId(),
							studentDetails.getClassName(), Double.valueOf(studentDetails.getStudentsHealth().getAge()),
							Double.valueOf(studentDetails.getStudentsHealth().getHeight()), studentDetails.getStudentsHealth().getHeightUnit(),
							Double.valueOf(studentDetails.getStudentsHealth().getWeight()), studentDetails.getStudentsHealth().getWeightUnit(),
							Double.valueOf(studentDetails.getStudentsHealth().getBmi()), Double.valueOf(studentDetails.getStudentsHealth().getBmiPercentile()),
							studentDetails.getStudentsHealth().getIbw(), studentDetails.getStudentsHealth().getBmiType(), new Date(),
							(wellness.getCategory() == null) ? "" : wellness.getCategory()});
			
			if(studentDetails.getStudentsHealth().getBmi()>0){
				this.log.debug("Populating report 1 data .....");
				// get report object
				WellnessLookupAssociation wla = this.wellnessLookupAssociationDAO.getWellnessReportbyCategory(studentDetails.getGender(), studentDetails.getStudentsHealth().getAge(), (wellness.getCategory() == null) ? "" : wellness.getCategory());
				if(wla.getCategory()!=null)
					wla = this.wellnessLookupAssociationDAO.getWellnessRecipe(wla);
				
				String sqlInsertRep1 = "INSERT INTO rep1_report(student_id, guidelines, recipeName, recipeContent, point_to_rem, create_date) VALUES (?,?,?,?,?,?)"; 
				this.jdbcTemplate.update(sqlInsertRep1,
						new Object[] { studentId, wla.getGuidelines(), wla.getRecipeName(), wla.getRecipeContent(), wla.getPntsToRem(), new Date() });

			}
			
			this.log.debug("Populating student_parent table .....");

			String sqlStudentParent = "INSERT INTO student_parent (student_roll_id, school_id, user_id, inserted_dt, updated_dt, isActive) VALUES(?,?,?,?,?,?)";

			if (!(studentDetails.getParent().getEmail().isEmpty() && studentDetails.getParent().getContact().isEmpty()) ) {
				String email = studentDetails.getParent().getEmail();
				if(email.isEmpty())
					email=studentDetails.getParent().getContact();
				Users user = this.userDAO.getIDByUserEmail(email);

				if(user==null){
					//create new user
					studentDetails.getParent().setRoleName("Parent");
					this.userDAO.addUser(studentDetails.getParent());
					user = this.userDAO.getIDByUserEmail(email);
				}
				this.jdbcTemplate.update(sqlStudentParent,
						new Object[] { studentDetails.getStudentRollId(), studentDetails.getSchoolId(),
								Integer.valueOf(user.getUserId()), new Date(), new Date(),
								Boolean.valueOf(studentDetails.isActive()) });
			}
			else{
				this.log.error("parent email and contact empty ");
			}

			this.log.debug("Student created with Basic details and Health details ");
		} catch (DataAccessException exp) {
			exp.printStackTrace();
			this.log.error("$$$ DataAccessException while loading data ", exp);
			throw exp;
		}
		return studentId;
	}
	
	@Override
	public int updateStudent(Student studentDetails){
		
		// get current date of birth and gender
		
		Student s = this.jdbcTemplate.query(
				"SELECT s.date_of_birth, s.gender FROM student s where s.studentId = :student_id"
				.replace(":student_id", String.valueOf(studentDetails.getStudentId())),
				new ResultSetExtractor<Student>() {
					public Student extractData(ResultSet rs) throws SQLException, DataAccessException {
						Student s = new Student();
						if (rs.next()) {
							s.setDateOfBirth(rs.getDate("date_of_birth"));
							s.setGender(rs.getString("gender"));
						}
						return s;
					}
				});
		
		String sql = "UPDATE student SET student_roll_id =?, first_name = ?, last_name = ?, class_name = ?, section_name = ?, "
				+ "gender = ?, date_of_birth = ?, updated_dt = ?,isActive = ?, profile_image = ? WHERE studentId=?";

		this.jdbcTemplate.update(sql, new Object[] { studentDetails.getStudentRollId(), studentDetails.getFirstName(), studentDetails.getLastName(), studentDetails.getClassName(),
				studentDetails.getSectionName(), studentDetails.getGender(), studentDetails.getDOB(), new Date(), studentDetails.isActive(), studentDetails.getProfileImage(), studentDetails.getStudentId()});
		
		if(!studentDetails.getDOB().equals(s.getDOB()) || !studentDetails.getGender().equals(s.getGender())){
			updateStudentHealthRecord(studentDetails);
		}
		
		return studentDetails.getStudentId();
	}
	
	@Override
	public void deleteStudent(int studentId){
		this.jdbcTemplate.update("call delete_student (?)", studentId);
	}

	@Override
	public List<Student> getChildBasedOnParentId(String parentId) {

		List<Student> students = this.jdbcTemplate.query("SELECT s.*,  u.contact, u.email, u.first_name as p_first_name, u.last_name as p_last_name, u.user_id as p_user_id, sch.school_name, sch.school_logo, b.branch_name "
				+ "FROM student s, student_parent p, school sch, branch b, user_master u "
				+ "where s.school_id = sch.school_id and s.branch_id = b.branch_id and s.student_roll_id = p.student_roll_id "
				+ "and p.user_id = u.user_id and p.user_id = :parent_id and s.isActive=1 order by inserted_dt desc".replace(":parent_id", parentId),
				new RowMapper<Student>() {
					public Student mapRow(ResultSet rs, int rowNum) throws SQLException {
						Student student = new Student();
						student.setStudentId(rs.getInt("studentId"));
						student.setStudentRollId(rs.getString("student_roll_id"));
						student.setFirstName(rs.getString("first_name"));
						student.setLastName(rs.getString("last_name"));
						student.setSchoolId(rs.getInt("school_id"));
						student.setBranchId(rs.getInt("branch_id"));
						student.setClassName(rs.getString("class_name"));
						if ((rs.getString("section_name") == null) || (rs.getString("section_name").isEmpty()))
							student.setSectionName(rs.getString("section_name"));
						else {
							student.setSectionName("'" + rs.getString("section_name") + "'");
						}
						student.setProfileImage(rs.getString("profile_image"));
						student.setGender(rs.getString("gender"));
						student.setDateOfBirth(rs.getDate("date_of_birth"));
						student.setCreatedDate(rs.getTimestamp("inserted_dt"));
						student.setUpdatedDate(rs.getTimestamp("updated_dt"));
						student.setActive(rs.getBoolean("isActive"));
						
						student.setSchoolName(rs.getString("school_name"));
						student.setBranchName(rs.getString("branch_name"));
						student.setSchoolLogo(rs.getString("school_logo"));
						Users p = new Users();
						p.setUserId(rs.getInt("p_user_id"));
						p.setContact(rs.getString("contact"));
						p.setFirstName(rs.getString("p_first_name"));
						p.setLastName(rs.getString("p_last_name"));
						p.setEmail(rs.getString("email"));
						
						student.setParent(p);

						return student;
					}
				});
		return students;
	
	}

	@Override
	public int[] getSchoolBranchId(String schoolName, String branchName) {
		int[] ids = (int[]) this.jdbcTemplate.query(
				"SELECT s.school_id, b.branch_id FROM school s, branch b where b.school_id = s.school_id and upper(b.branch_name) = ':branch_name' and upper(s.school_name) = ':schoolName'"
				.replace(":branch_name", branchName.toUpperCase()).replace(":schoolName", schoolName.toUpperCase()),
				new ResultSetExtractor<int[]>() {
					public int[] extractData(ResultSet rs) throws SQLException, DataAccessException {
						if (rs.next()) {
							int[] id = new int[2];
							id[0] = rs.getInt("school_id");
							id[1]= rs.getInt("branch_id");
							return id;
						}

						return null;
					}
				});
		if (ids == null) {
			return new int[]{0,0};
		}

		return ids;
	}

	@Override
	public int getStudentIdFromRollId(Student studentDetails) {
		return (int) this.jdbcTemplate.query(
				"SELECT s.studentId FROM student s, student_parent sp, user_master u where s.student_roll_id = sp.student_roll_id and sp.user_id = u.user_id and s.student_roll_id = ':studentRollId' and (u.email = ':email' or u.contact = ':contact')"
				.replace(":studentRollId", studentDetails.getStudentRollId()).replace(":email", studentDetails.getParent().getEmail()).replace(":contact", studentDetails.getParent().getContact()),
				new ResultSetExtractor<Integer>() {
					public Integer extractData(ResultSet rs) throws SQLException, DataAccessException {
						if (rs.next()) {
							return rs.getInt("studentId");
						}

						return 0;
					}
				});
	}

	@Override
	public void updateStudentHealthRecord(Student studentDetails) {
		WellnessLookupAssociation wellness = new WellnessLookupAssociation();
		if(studentDetails.getStudentsHealth().getBmi()>0)
		 wellness = this.wellnessLookupAssociationDAO.getWellnessReportCategory(
				studentDetails.getGender(), new Double(studentDetails.getStudentsHealth().getAge()).intValue(),
				new Double(studentDetails.getStudentsHealth().getBmiPercentile()).intValue());
		
		String sqlHealth = "UPDATE student_health SET age=?,height=?, height_unit=?, weight=?, weight_unit=?,bmi=?, bmi_percentile=?, ibw=?, bmi_type=?,date_of_measurement=?, category=? WHERE student_roll_id=? AND school_id=?";

		this.jdbcTemplate.update(sqlHealth,
				new Object[] {Double.valueOf(studentDetails.getStudentsHealth().getAge()),
						Double.valueOf(studentDetails.getStudentsHealth().getHeight()), studentDetails.getStudentsHealth().getHeightUnit(),
						Double.valueOf(studentDetails.getStudentsHealth().getWeight()), studentDetails.getStudentsHealth().getWeightUnit(),
						Double.valueOf(studentDetails.getStudentsHealth().getBmi()),
						Double.valueOf(studentDetails.getStudentsHealth().getBmiPercentile()), studentDetails.getStudentsHealth().getIbw(),
						studentDetails.getStudentsHealth().getBmiType(), new Date(),(wellness.getCategory() == null) ? "" : wellness.getCategory(),
						studentDetails.getStudentRollId(), studentDetails.getSchoolId()});
	
			this.log.debug("Populating student Health Histoiry table table table .....");
		
			String sqlHealthHistory = "insert into student_health_history (student_roll_id, school_id, class_name, age, height, height_unit, weight, weight_unit,bmi, bmi_percentile, ibw, bmi_type, date_of_measurement, category) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
		
			this.jdbcTemplate.update(sqlHealthHistory,
					new Object[] { studentDetails.getStudentRollId(), studentDetails.getSchoolId(),
							studentDetails.getClassName(), Double.valueOf(studentDetails.getStudentsHealth().getAge()),
							Double.valueOf(studentDetails.getStudentsHealth().getHeight()), studentDetails.getStudentsHealth().getHeightUnit(),
							Double.valueOf(studentDetails.getStudentsHealth().getWeight()), studentDetails.getStudentsHealth().getWeightUnit(),
							Double.valueOf(studentDetails.getStudentsHealth().getBmi()), Double.valueOf(studentDetails.getStudentsHealth().getBmiPercentile()),
							studentDetails.getStudentsHealth().getIbw(), studentDetails.getStudentsHealth().getBmiType(), new Date(),
							(wellness.getCategory() == null) ? "" : wellness.getCategory()});
			
			if(studentDetails.getStudentsHealth().getBmi()>0){
				this.log.debug("Populating report 1 data .....");
				// get report object
				// move current report to history
				String sql1 = "INSERT INTO rep1_report_history(report_id, student_id, guidelines, recipeName, recipeContent, point_to_rem, create_date, mvd_to_hist_date) SELECT report_id, student_id, guidelines, recipeName, recipeContent, point_to_rem, create_date, now() FROM rep1_report rep WHERE rep.student_id = ?";
				this.jdbcTemplate.update(sql1,new Object[] { studentDetails.getStudentId()});
				
				// delete current report
				String sql2 = "DELETE FROM rep1_report WHERE student_id =?";
				this.jdbcTemplate.update(sql2,new Object[] { studentDetails.getStudentId()});
				//add new report
				WellnessLookupAssociation wla = this.wellnessLookupAssociationDAO.getWellnessReportbyCategory(studentDetails.getGender(), studentDetails.getStudentsHealth().getAge(), (wellness.getCategory() == null) ? "" : wellness.getCategory());
				if(wla.getCategory()!=null)
					wla = this.wellnessLookupAssociationDAO.getWellnessRecipe(wla);
				
				String sqlInsertRep1 = "INSERT INTO rep1_report(student_id, guidelines, recipeName, recipeContent, point_to_rem, create_date) VALUES (?,?,?,?,?,?)"; 
				this.jdbcTemplate.update(sqlInsertRep1,
						new Object[] { studentDetails.getStudentId(), wla.getGuidelines(), wla.getRecipeName(), wla.getRecipeContent(), wla.getPntsToRem(), new Date() });

			}
	}

	@Override
	public StudentHealth getStudentsHealthByStudentId(int studentId) {

		return ((StudentHealth) this.jdbcTemplate.query(
				"SELECT * FROM student_health h, student s WHERE h.student_roll_id= s.student_roll_id AND h.school_id=s.school_id and s.studentId = :studentId"
					.replace(":studentId", String.valueOf(studentId)),
				new ResultSetExtractor<StudentHealth>() {
					public StudentHealth extractData(ResultSet rs) throws SQLException, DataAccessException {
						if (rs.next()) {
							StudentHealth studentHealth = new StudentHealth();
							studentHealth.setStudentRollId(rs.getString("student_roll_id"));
							studentHealth.setSchollId(rs.getInt("school_id"));
							studentHealth.setClassName(rs.getString("class_name"));
							studentHealth.setAge(rs.getDouble("age"));
							studentHealth.setHeight(rs.getDouble("height"));
							studentHealth.setHeightUnit(rs.getString("height_unit"));
							studentHealth.setWeight(rs.getDouble("weight"));
							studentHealth.setBmi(rs.getDouble("bmi"));
							studentHealth.setBmiPercentile(rs.getDouble("bmi_percentile"));
							studentHealth.setIbw(rs.getString("ibw"));
							studentHealth.setWeightUnit(rs.getString("weight_unit"));
							studentHealth.setDateOfMeasurement(rs.getDate("date_of_measurement"));
							studentHealth.setCategory(rs.getString("category"));
							return studentHealth;
						}
						return null;
					}
				}));
	
	}

	@Override
	public List<Student> getStudentsByBranchId(int branchId) {
				
				List<Student> students = this.jdbcTemplate.query("SELECT s.studentId, s.student_roll_id, s.first_name, s.last_name, s.school_id, s.branch_id, s.class_name, s.section_name, s.profile_image, s.gender, DATE_FORMAT(s.date_of_birth,'%Y-%m-%d') date_of_birth , s.inserted_dt, s.updated_dt, s.isActive, h.school_name, h.school_logo, b.branch_name from student s, school h, branch b WHERE h.school_id= s.school_id and b.branch_id = s.branch_id and b.branch_id=:branchId"
						.replace(":branchId", String.valueOf(branchId)),
						new RowMapper<Student>() {
							public Student mapRow(ResultSet rs, int rowNum) throws SQLException {
								Student student = new Student();
								student.setStudentId(rs.getInt("studentId"));
								student.setStudentRollId(rs.getString("student_roll_id"));
								student.setFirstName(rs.getString("first_name"));
								student.setLastName(rs.getString("last_name"));
								student.setSchoolId(rs.getInt("school_id"));
								student.setSchoolName(rs.getString("school_name"));
								student.setSchoolLogo(rs.getString("school_logo"));
								student.setBranchId(rs.getInt("branch_id"));
								student.setBranchName(rs.getString("branch_name"));
								student.setClassName(rs.getString("class_name"));
								if ((rs.getString("section_name") == null) || (rs.getString("section_name").isEmpty()))
									student.setSectionName(rs.getString("section_name"));
								else {
									student.setSectionName("'" + rs.getString("section_name") + "'");
								}
								student.setProfileImage(rs.getString("profile_image"));
								student.setGender(rs.getString("gender"));
								student.setDateOfBirth(rs.getDate("date_of_birth"));
								student.setCreatedDate(rs.getTimestamp("inserted_dt"));
								student.setUpdatedDate(rs.getTimestamp("updated_dt"));
								student.setActive(rs.getBoolean("isActive"));

								return student;
							}
						});
				return students;
	
	}
}
