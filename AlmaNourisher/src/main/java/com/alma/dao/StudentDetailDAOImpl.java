package com.alma.dao;

import com.alma.exception.CustomGenericException;
import com.alma.model.StaticDataModel;
import com.alma.model.StudentDetails;
import com.alma.model.StudentFileDetails;
import com.alma.model.Users;
import com.alma.model.WellnessLookupAssociation;
import com.alma.util.AgeBMICalculator;
import com.alma.util.BMIForAge;
import com.alma.util.CommonUtil;
import com.alma.util.DataAssembler;
import java.io.PrintStream;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.sql.DataSource;
import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Transactional
public class StudentDetailDAOImpl implements StudentDetailDAO {
	private JdbcTemplate jdbcTemplate;
	private Logger log = Logger.getLogger(StudentDetailDAOImpl.class);

	@Autowired
	private UsersDAO userDAO;

	@Autowired
	private WellnessLookupAssociationDAO wellnessLookupAssociationDAO;

	public StudentDetailDAOImpl(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

	public int saveStudent(StudentDetails studentDetails) {
		int studentId = 0;
		try {
			this.log.debug("Populating student table .....");
			String sql = "INSERT INTO student (student_roll_id, first_name, last_name, school_id, branch_id, class_name, section_name, gender, date_of_birth,inserted_dt,updated_dt,isActive) VALUES(?,?,?,?,?,?,?,?,?,?,?,?)";

			studentId = this.jdbcTemplate.update(sql,
					new Object[] { studentDetails.getStudentRollId(), studentDetails.getFirstName(),
							studentDetails.getLastName(), studentDetails.getSchoolId(),
							Integer.valueOf(studentDetails.getBranchId()), studentDetails.getClassName(),
							studentDetails.getSectionName(), studentDetails.getGender(), studentDetails.getDOB(),
							new Date(), new Date(), Boolean.valueOf(studentDetails.isStudentActive()) });

			this.log.debug("Populating student Health table table .....");
			this.log.debug("-->" + studentDetails.getAge() + studentDetails.getBmi());

			String sqlHealth = "insert into student_health (student_roll_id, school_id, class_name, age, height, height_unit, weight, weight_unit,bmi, bmi_percentile, ibw, bmi_type,date_of_measurement, category) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

			WellnessLookupAssociation wellness = this.wellnessLookupAssociationDAO.getWellnessReportCategory(
					studentDetails.getGender(), new Double(studentDetails.getAge()).intValue(),
					new Double(studentDetails.getBmiPercentile()).intValue());

			this.jdbcTemplate.update(sqlHealth,
					new Object[] { studentDetails.getStudentRollId(), studentDetails.getSchoolId(),
							studentDetails.getClassName(), Double.valueOf(studentDetails.getAge()),
							Double.valueOf(studentDetails.getHeight()), studentDetails.getHeightUnit(),
							Double.valueOf(studentDetails.getWeight()), studentDetails.getWeightUnit(),
							Double.valueOf(studentDetails.getBmi()), Double.valueOf(studentDetails.getBmiPercentile()),
							studentDetails.getIbw(), studentDetails.getBmiType(), new Date(),
							(wellness.getCategory() == null) ? "" : wellness.getCategory() });

			this.log.debug("Populating student Health Histoiry table table table .....");

			String sqlHealthHistory = "insert into student_health_history (student_roll_id, school_id, class_name, age, height, height_unit, weight, weight_unit,bmi, bmi_percentile, ibw, bmi_type,date_of_measurement,category) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

			this.jdbcTemplate.update(sqlHealthHistory,
					new Object[] { studentDetails.getStudentRollId(), studentDetails.getSchoolId(),
							studentDetails.getClassName(), Double.valueOf(studentDetails.getAge()),
							Double.valueOf(studentDetails.getHeight()), studentDetails.getHeightUnit(),
							Double.valueOf(studentDetails.getWeight()), studentDetails.getWeightUnit(),
							Double.valueOf(studentDetails.getBmi()), Double.valueOf(studentDetails.getBmiPercentile()),
							studentDetails.getIbw(), studentDetails.getBmiType(), new Date(),
							(wellness.getCategory() == null) ? "" : wellness.getCategory()});

			this.log.debug("Populating student_parent table .....");

			String sqlStudentParent = "INSERT INTO student_parent (student_roll_id, school_id, user_id, inserted_dt, updated_dt, isActive) VALUES(?,?,?,?,?,?)";

			if (!(studentDetails.getParentEmail().isEmpty())) {
				Users user = this.userDAO.getIDByUserEmail(studentDetails.getParentEmail());

				this.jdbcTemplate.update(sqlStudentParent,
						new Object[] { studentDetails.getStudentRollId(), studentDetails.getSchoolId(),
								Integer.valueOf(user.getUserId()), new Date(), new Date(),
								Boolean.valueOf(studentDetails.isStudentActive()) });
			}

			this.log.debug("Student created with Basic details and Health details ");
		} catch (DataAccessException exp) {
			exp.printStackTrace();
			this.log.error("$$$ DataAccessException while loading data ", exp);
		}
		return studentId;
	}

	public void saveStudent(StudentDetails studentDetails, MultipartFile studentImage) {
		try {
			this.log.debug("Populating student table .....");
			String sql = "INSERT INTO student (student_roll_id, first_name, last_name, school_id, branch_id, class_name, section_name, profile_image, gender, date_of_birth,inserted_dt,updated_dt,isActive) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?)";

			String profileImage = null;
			if (!(studentImage.isEmpty())) {
				profileImage = CommonUtil.convertFileToBase64(studentImage);
			}

			this.jdbcTemplate.update(sql,
					new Object[] { studentDetails.getStudentRollId(), studentDetails.getFirstName(),
							studentDetails.getLastName(), studentDetails.getSchoolId(),
							Integer.valueOf(studentDetails.getBranchId()), studentDetails.getClassName(),
							studentDetails.getSectionName(),
							(profileImage == null) ? "/resources/images/unisex.png" : profileImage,
							studentDetails.getGender(), studentDetails.getDOB(), new Date(), new Date(),
							Boolean.valueOf(studentDetails.isStudentActive()) });

			this.log.debug("Populating student Health table table .....");
			this.log.debug("-->" + studentDetails.getAge() + studentDetails.getBmi());

			String sqlHealth = "insert into student_health (student_roll_id, school_id, class_name, age, height, height_unit, weight, weight_unit,bmi, bmi_percentile, ibw, bmi_type,date_of_measurement, category) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

			WellnessLookupAssociation wellness = this.wellnessLookupAssociationDAO.getWellnessReportCategory(
					studentDetails.getGender(), new Double(studentDetails.getAge()).intValue(),
					new Double(studentDetails.getBmiPercentile()).intValue());

			this.jdbcTemplate.update(sqlHealth,
					new Object[] { studentDetails.getStudentRollId(), studentDetails.getSchoolId(),
							studentDetails.getClassName(), Double.valueOf(studentDetails.getAge()),
							Double.valueOf(studentDetails.getHeight()), studentDetails.getHeightUnit(),
							Double.valueOf(studentDetails.getWeight()), studentDetails.getWeightUnit(),
							Double.valueOf(studentDetails.getBmi()), Double.valueOf(studentDetails.getBmiPercentile()),
							studentDetails.getIbw(), studentDetails.getBmiType(), new Date(),
							(wellness.getCategory() == null) ? "" : wellness.getCategory() });

			this.log.debug("Populating student Health Histoiry table table table .....");

			String sqlHealthHistory = "insert into student_health_history (student_roll_id, school_id, class_name, age, height, height_unit, weight, weight_unit,bmi, bmi_percentile, ibw, bmi_type,date_of_measurement,category) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

			this.jdbcTemplate.update(sqlHealthHistory,
					new Object[] { studentDetails.getStudentRollId(), studentDetails.getSchoolId(),
							studentDetails.getClassName(), Double.valueOf(studentDetails.getAge()),
							Double.valueOf(studentDetails.getHeight()), studentDetails.getHeightUnit(),
							Double.valueOf(studentDetails.getWeight()), studentDetails.getWeightUnit(),
							Double.valueOf(studentDetails.getBmi()), Double.valueOf(studentDetails.getBmiPercentile()),
							studentDetails.getIbw(), studentDetails.getBmiType(), new Date(),
							(wellness.getCategory() == null) ? "" : wellness.getCategory()});

			if (!(studentDetails.getParentEmail().isEmpty())) {
				this.log.debug("Populating student_parent table .....");

				String sqlStudentParent = "INSERT INTO student_parent (student_roll_id, school_id, parent_email, inserted_dt, updated_dt, isActive) VALUES(?,?,?,?,?,?)";

				this.jdbcTemplate.update(sqlStudentParent,
						new Object[] { studentDetails.getStudentRollId(), studentDetails.getSchoolId(),
								studentDetails.getParentEmail(), new Date(), new Date(),
								Boolean.valueOf(studentDetails.isStudentActive()) });
			}

			this.log.debug("Student created with Basic details, Health details and Health History");
		} catch (DataAccessException exp) {
			exp.printStackTrace();
			this.log.error("$$$ DataAccessException while loading data ", exp);
		}
	}

	public void updateStudent(StudentDetails studentDetails) {
		try {
			this.log.debug("Populating student table .....");

			String sql = "UPDATE  student SET first_name=?,last_name=?, class_name=? , section_name=?, gender=?, date_of_birth=?, updated_dt=?, isActive=? where student_roll_id = ? and school_id=?";

			this.jdbcTemplate.update(sql,
					new Object[] { studentDetails.getFirstName(), studentDetails.getLastName(),
							studentDetails.getClassName(), studentDetails.getSectionName(), studentDetails.getGender(),
							studentDetails.getDOB(), new Date(), Boolean.valueOf(studentDetails.isStudentActive()),
							studentDetails.getStudentRollId(), studentDetails.getSchoolId() });

			System.out.println(">>>>>>>>>>>>" + studentDetails.getStudentRollId());
			System.out.println("<<<<<<<<<<<<<<<< " + studentDetails.getSchoolId());

			this.log.debug("Populating student Health table table .....");
			this.log.debug("-->" + studentDetails.getAge() + studentDetails.getBmi());

			String sqlHealth = null;
			StudentDetails currentHealth = getStudentCurrentHealth(studentDetails.getSchoolId(),
					studentDetails.getStudentRollId());
			WellnessLookupAssociation wellness = this.wellnessLookupAssociationDAO.getWellnessReportCategory(
					studentDetails.getGender(), new Double(studentDetails.getAge()).intValue(),
					new Double(studentDetails.getBmiPercentile()).intValue());

			if (currentHealth != null) {
				sqlHealth = "UPDATE student_health SET class_name=?, age=?, height=?, height_unit=?, weight=?, weight_unit=?,bmi=?, bmi_percentile=?, ibw=?, bmi_type=?,date_of_measurement=?, category=? WHERE student_roll_id=? AND school_id=?";

				this.jdbcTemplate.update(sqlHealth,
						new Object[] { studentDetails.getClassName(), Double.valueOf(studentDetails.getAge()),
								Double.valueOf(studentDetails.getHeight()), studentDetails.getHeightUnit(),
								Double.valueOf(studentDetails.getWeight()), studentDetails.getWeightUnit(),
								Double.valueOf(studentDetails.getBmi()),
								Double.valueOf(studentDetails.getBmiPercentile()), studentDetails.getIbw(),
								studentDetails.getBmiType(), new Date(),
								(wellness.getCategory() == null) ? "" : wellness.getCategory(),
								studentDetails.getStudentRollId(), studentDetails.getSchoolId() });
			} else {
				sqlHealth = "insert into student_health (student_roll_id, school_id, class_name, age, height, height_unit, weight, weight_unit,bmi, bmi_percentile, ibw, bmi_type,date_of_measurement, category) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

				this.jdbcTemplate.update(sqlHealth,
						new Object[] { studentDetails.getStudentRollId(), studentDetails.getSchoolId(),
								studentDetails.getClassName(), Double.valueOf(studentDetails.getAge()),
								Double.valueOf(studentDetails.getHeight()), studentDetails.getHeightUnit(),
								Double.valueOf(studentDetails.getWeight()), studentDetails.getWeightUnit(),
								Double.valueOf(studentDetails.getBmi()),
								Double.valueOf(studentDetails.getBmiPercentile()), studentDetails.getIbw(),
								studentDetails.getBmiType(), new Date(),
								(wellness.getCategory() == null) ? "" : wellness.getCategory() });
			}

			this.log.debug("Populating student Health Histoiry table table table .....");

			String sqlHealthHistory = "insert into student_health_history (student_roll_id, school_id, class_name, age, height, height_unit, weight, weight_unit,bmi, bmi_percentile, ibw, bmi_type, date_of_measurement, category) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

			this.jdbcTemplate.update(sqlHealthHistory,
					new Object[] { studentDetails.getStudentRollId(), studentDetails.getSchoolId(),
							studentDetails.getClassName(), Double.valueOf(studentDetails.getAge()),
							Double.valueOf(studentDetails.getHeight()), studentDetails.getHeightUnit(),
							Double.valueOf(studentDetails.getWeight()), studentDetails.getWeightUnit(),
							Double.valueOf(studentDetails.getBmi()), Double.valueOf(studentDetails.getBmiPercentile()),
							studentDetails.getIbw(), studentDetails.getBmiType(), new Date(),
							(wellness.getCategory() == null) ? "" : wellness.getCategory()});

			this.log.debug("Student created with Basic details, Health details and Health History");

			String sqlParentStudent = null;
			Users user = this.userDAO.getIDByUserEmail(studentDetails.getParentEmail());
			if (isStudentParentRelationExists(studentDetails.getStudentRollId(), studentDetails.getSchoolId())) {
				sqlParentStudent = "UPDATE student_parent SET user_id=?, updated_dt=?, isActive=? WHERE student_roll_id=? AND school_id=?";

				this.jdbcTemplate.update(sqlParentStudent,
						new Object[] { Integer.valueOf(user.getUserId()), new Date(),
								Boolean.valueOf(studentDetails.isStudentActive()), studentDetails.getStudentRollId(),
								studentDetails.getSchoolId() });

				this.log.debug("Updated student_parent table .....");
			} else {
				sqlParentStudent = "INSERT INTO student_parent (student_roll_id, school_id, user_id, inserted_dt, isActive) VALUES (?,?,?,?,?)";

				this.jdbcTemplate.update(sqlParentStudent,
						new Object[] { studentDetails.getStudentRollId(), studentDetails.getSchoolId(),
								Integer.valueOf(user.getUserId()), new Date(),
								Boolean.valueOf(studentDetails.isStudentActive()) });

				this.log.debug("Inserted student_parent table with new record .....");
			}

		} catch (DataAccessException exp) {
			exp.printStackTrace();
			this.log.error("$$$ DataAccessException while loading data ", exp);
		}
	}

	private boolean isStudentParentRelationExists(String studentRollId, String schoolId) {
		boolean flag = false;
		Integer cnt = (Integer) this.jdbcTemplate.queryForObject(
				"SELECT count(*) FROM student_parent WHERE student_roll_id=? AND school_id=?",
				new Object[] { studentRollId, schoolId }, Integer.class);

		if ((cnt != null) && (cnt.intValue() > 0)) {
			flag = true;
		}
		return flag;
	}

	public void updateStudent(StudentDetails studentDetails, MultipartFile studentImage) {
		try {
			this.log.debug("Populating student table .....");

			String sql = "UPDATE  student SET first_name=?,last_name=?,branch_id=? , class_name=? , section_name=?, profile_image=?, gender=?, date_of_birth=?, updated_dt=?, isActive=? where student_roll_id = ? and school_id=?";

			String profileImage = null;
			if (!(studentImage.isEmpty())) {
				profileImage = CommonUtil.convertFileToBase64(studentImage);
			}

			this.jdbcTemplate.update(sql,
					new Object[] { studentDetails.getFirstName(), studentDetails.getLastName(),
							Integer.valueOf(studentDetails.getBranchId()), studentDetails.getClassName(),
							studentDetails.getSectionName(), (profileImage == null) ? "" : profileImage,
							studentDetails.getGender(), studentDetails.getDOB(), new Date(),
							Boolean.valueOf(studentDetails.isStudentActive()), studentDetails.getStudentRollId(),
							studentDetails.getSchoolId() });

			this.log.debug("Populating student Health table table .....");

			String sqlQuery = "SELECT count(*) FROM student_health WHERE student_roll_id=? AND school_id=?";
			int count = ((Integer) this.jdbcTemplate.queryForObject(sqlQuery,
					new Object[] { studentDetails.getStudentRollId(), studentDetails.getSchoolId() }, Integer.class))
							.intValue();

			WellnessLookupAssociation wellness = this.wellnessLookupAssociationDAO.getWellnessReportCategory(
					studentDetails.getGender(), new Double(studentDetails.getAge()).intValue(),
					new Double(studentDetails.getBmiPercentile()).intValue());
			if (count > 0) {
				String sqlHealth = "UPDATE student_health SET class_name=?, age=?, height=?, height_unit=?, weight=?, weight_unit=?,bmi =?, bmi_percentile=?, ibw=?, bmi_type=?, category=? WHERE student_roll_id=? AND school_id=?";

				this.jdbcTemplate.update(sqlHealth, new Object[] { studentDetails.getClassName(),
						Double.valueOf(studentDetails.getAge()), Double.valueOf(studentDetails.getHeight()),
						studentDetails.getHeightUnit(), Double.valueOf(studentDetails.getWeight()),
						studentDetails.getWeightUnit(), Double.valueOf(studentDetails.getBmi()),
						Double.valueOf(studentDetails.getBmiPercentile()), studentDetails.getIbw(),
						studentDetails.getBmiType(), (wellness.getCategory() == null) ? "" : wellness.getCategory(),
						studentDetails.getStudentRollId(), studentDetails.getSchoolId() });

				this.log.debug("Populating student Health Histoiry table table table .....");
			} else {
				
				String sqlHealth = "insert into student_health (student_roll_id, school_id, class_name, age, height, height_unit, weight, weight_unit,bmi, bmi_percentile, ibw, bmi_type,date_of_measurement, category) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

				this.jdbcTemplate.update(sqlHealth,
						new Object[] { studentDetails.getStudentRollId(), studentDetails.getSchoolId(),
								studentDetails.getClassName(), Double.valueOf(studentDetails.getAge()),
								Double.valueOf(studentDetails.getHeight()), studentDetails.getHeightUnit(),
								Double.valueOf(studentDetails.getWeight()), studentDetails.getWeightUnit(),
								Double.valueOf(studentDetails.getBmi()),
								Double.valueOf(studentDetails.getBmiPercentile()), studentDetails.getIbw(),
								studentDetails.getBmiType(), new Date(),
								(wellness.getCategory() == null) ? "" : wellness.getCategory() });

				this.log.debug("Populating student Health Histoiry table table table .....");
			}

			String sqlHealthHistory = "insert into student_health_history (student_roll_id, school_id, class_name, age, height, height_unit, weight, weight_unit,bmi, bmi_percentile, ibw, bmi_type, date_of_measurement,category) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

			this.jdbcTemplate.update(sqlHealthHistory,
					new Object[] { studentDetails.getStudentRollId(), studentDetails.getSchoolId(),
							studentDetails.getClassName(), Double.valueOf(studentDetails.getAge()),
							Double.valueOf(studentDetails.getHeight()), studentDetails.getHeightUnit(),
							Double.valueOf(studentDetails.getWeight()), studentDetails.getWeightUnit(),
							Double.valueOf(studentDetails.getBmi()), Double.valueOf(studentDetails.getBmiPercentile()),
							studentDetails.getIbw(), studentDetails.getBmiType(), new Date(),
							(wellness.getCategory() == null) ? "" : wellness.getCategory()});

			this.log.debug("Student created with Basic details, Health details and Health History");

			System.out.println("parentemail " + studentDetails.getParentEmail());
			if (!(studentDetails.getParentEmail().isEmpty())) {
				System.out.println("parent is not empty");
				sqlQuery = "SELECT count(*) FROM student_parent WHERE student_roll_id=? AND school_id=?";
				count = ((Integer) this.jdbcTemplate.queryForObject(sqlQuery,
						new Object[] { studentDetails.getStudentRollId(), studentDetails.getSchoolId() },
						Integer.class)).intValue();

				if (count > 0) {
					String sqlParentStudent = " UPDATE student_parent SET parent_email=?, updated_dt=?, isActive=? WHERE student_roll_id=? AND school_id=?";

					this.jdbcTemplate.update(sqlParentStudent,
							new Object[] { studentDetails.getParentEmail(), new Date(),
									Boolean.valueOf(studentDetails.isStudentActive()),
									studentDetails.getStudentRollId(), studentDetails.getSchoolId() });

					this.log.debug("Populating student_parent table .....");
				} else {
					this.log.debug("Populating student_parent table .....");

					String sqlStudentParent = "INSERT INTO student_parent (student_roll_id, school_id, parent_email, inserted_dt, updated_dt, isActive) VALUES(?,?,?,?,?,?)";

					this.jdbcTemplate.update(sqlStudentParent,
							new Object[] { studentDetails.getStudentRollId(), studentDetails.getSchoolId(),
									studentDetails.getParentEmail(), new Date(), new Date(),
									Boolean.valueOf(studentDetails.isStudentActive()) });

					this.log.debug("Student created with Basic details, Health details and Health History");
				}

			}

		} catch (DataAccessException exp) {
			exp.printStackTrace();
			this.log.error("$$$ DataAccessException while loading data ", exp);
		}
	}

	public void createStudentFromImportFile(List<StudentFileDetails> studentList) {
		Map<String,String> mapSchoolBranch = new LinkedHashMap<String,String>();

		List<StaticDataModel> branchlist = this.jdbcTemplate.query(
				" select school.school_name,branch.school_id,branch.branch_name, branch.branch_id  from school school, branch branch where school.school_id= branch.school_id; ",
				new RowMapper<StaticDataModel>() {
					String mapKey;
					String mapValue;

					public StaticDataModel mapRow(ResultSet rs, int rowNum) throws SQLException {
						StaticDataModel staticDataModel = new StaticDataModel();

						this.mapKey = rs.getString("school_name") + "#" + rs.getString("branch_name");
						this.mapValue = rs.getString("school_id") + "#" + rs.getString("branch_id");

						staticDataModel.setKey(this.mapKey);
						staticDataModel.setValue(this.mapValue);

						return staticDataModel;
					}
				});
		for (StaticDataModel staticDataModel : branchlist) {
			mapSchoolBranch.put("" + staticDataModel.getKey(), staticDataModel.getValue());
		}

		for (StudentFileDetails studentFileDetails : studentList) {
			StudentDetails studentDetails = DataAssembler.getFileImportStudentDetails(mapSchoolBranch,
					studentFileDetails);
			saveStudent(studentDetails);
		}
	}

	public void addImage(MultipartFile multipartFile, String studentRollId, String schoolId) {
		String profileImage = null;

		if (!(multipartFile.isEmpty())) {
			profileImage = CommonUtil.convertFileToBase64(multipartFile);
		}

		String sql = "UPDATE student SET profile_image=?, updated_dt=? WHERE student_roll_id=? and school_id=?";
		this.jdbcTemplate.update(sql,
				new Object[] { (profileImage == null) ? "" : profileImage, new Date(), studentRollId, schoolId });
	}

	public StudentDetails getStudentCurrentHealth(String school_id, String studentRollId) {
		String currentHealth = "SELECT * FROM student_health where school_id=:schoolId AND student_roll_id=':studentRollId'";
		try {
			return ((StudentDetails) this.jdbcTemplate.query(
					currentHealth.replace(":schoolId", school_id).replace(":studentRollId", studentRollId),
					new ResultSetExtractor<StudentDetails>() {
						public StudentDetails extractData(ResultSet rs) throws SQLException, DataAccessException {
							if (rs.next()) {
								StudentDetails studentDetails = new StudentDetails();
								studentDetails.setStudentRollId(rs.getString("student_roll_id"));
								studentDetails.setSchoolId(rs.getInt("school_id") + "");
								studentDetails.setClassName(rs.getString("class_name"));
								studentDetails.setAge(rs.getDouble("age"));
								studentDetails.setHeight(rs.getDouble("height"));
								studentDetails.setHeightUnit(rs.getString("height_unit"));
								studentDetails.setWeight(rs.getDouble("weight"));
								studentDetails.setWeightUnit(rs.getString("weight_unit"));
								studentDetails.setBmi(rs.getDouble("bmi"));
								studentDetails.setBmiPercentile(rs.getDouble("bmi_percentile"));
								studentDetails.setIbw(rs.getString("ibw"));
								studentDetails.setDateOfMeasurement(rs.getDate("date_of_measurement"));
								return studentDetails;
							}

							return null;
						}
					}));
		} catch (EmptyResultDataAccessException erdae) {
			erdae.printStackTrace();
			return null;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public List<StudentDetails> getReportForHealthyStudents(String startDate, String endDate, String schoolId,
			int branchId) {
		try {
			List<StudentDetails> studentDetails = this.jdbcTemplate.query(
					"select sh.date_of_measurement, count(*) as total from student s inner join student_health sh on s.student_roll_id = sh.student_roll_id where sh.date_of_measurement between ? and ? and sh.bmi_percentile between 5.00 and 85.00 and s.school_id = ? and s.branch_id = ? GROUP BY YEAR(sh.date_of_measurement), MONTH(sh.date_of_measurement) ",
					new Object[] { startDate, endDate, schoolId, Integer.valueOf(branchId) }, new RowMapper<StudentDetails>() {
						public StudentDetails mapRow(ResultSet rs, int rowNum) throws SQLException {
							StudentDetails studentDetails = new StudentDetails();
							studentDetails.setCategoryTotal(rs.getInt("total"));
							studentDetails.setDateOfMeasurement(rs.getDate("date_of_measurement"));
							return studentDetails;
						}
					});
			return studentDetails;
		} catch (EmptyResultDataAccessException erdae) {
			erdae.printStackTrace();
			return new ArrayList<StudentDetails>();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new ArrayList<StudentDetails>();
	}

	public List<StudentDetails> getReportForUnderWeightStudents(String startDate, String endDate, String schoolId,
			int branchId) {
		try {
			List<StudentDetails> studentDetails = this.jdbcTemplate.query(
					"select sh.date_of_measurement, count(*) as total from student s inner join student_health sh on s.student_roll_id = sh.student_roll_id where sh.date_of_measurement between ? and ? and sh.bmi_percentile < 5.00 and s.school_id = ? and s.branch_id = ? GROUP BY YEAR(sh.date_of_measurement), MONTH(sh.date_of_measurement) ",
					new Object[] { startDate, endDate, schoolId, Integer.valueOf(branchId) }, new RowMapper<StudentDetails>() {
						public StudentDetails mapRow(ResultSet rs, int rowNum) throws SQLException {
							StudentDetails studentDetails = new StudentDetails();
							studentDetails.setCategoryTotal(rs.getInt("total"));
							studentDetails.setDateOfMeasurement(rs.getDate("date_of_measurement"));
							return studentDetails;
						}
					});
			return studentDetails;
		} catch (EmptyResultDataAccessException erdae) {
			erdae.printStackTrace();
			return new ArrayList<StudentDetails>();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new ArrayList<StudentDetails>();
	}

	public List<StudentDetails> getReportForOverWeightStudents(String startDate, String endDate, String schoolId,
			int branchId) {
		try {
			List<StudentDetails> studentDetails = this.jdbcTemplate.query(
					"select sh.date_of_measurement, count(*) as total from student s inner join student_health sh on s.student_roll_id = sh.student_roll_id where sh.date_of_measurement between ? and ? and sh.bmi_percentile > 85.00 and s.school_id = ? and s.branch_id = ?  GROUP BY YEAR(sh.date_of_measurement), MONTH(sh.date_of_measurement) ",
					new Object[] { startDate, endDate, schoolId, Integer.valueOf(branchId) }, new RowMapper<StudentDetails>() {
						public StudentDetails mapRow(ResultSet rs, int rowNum) throws SQLException {
							StudentDetails studentDetails = new StudentDetails();
							studentDetails.setCategoryTotal(rs.getInt("total"));
							studentDetails.setDateOfMeasurement(rs.getDate("date_of_measurement"));
							return studentDetails;
						}
					});
			return studentDetails;
		} catch (EmptyResultDataAccessException erdae) {
			erdae.printStackTrace();
			return new ArrayList<StudentDetails>();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new ArrayList<StudentDetails>();
	}

	public List<StudentDetails> getReportTotalCountForBelowReferenceRanage(String startDate, String endDate,
			String schoolId, int branchId, String gender, String className) {
		try {
			List<StudentDetails> studentDetails = this.jdbcTemplate.query(
					"select sh.category, count(*) as total, sh.date_of_measurement from student s inner join student_health sh on s.student_roll_id = sh.student_roll_id where sh.date_of_measurement between ? and ? and s.gender like ':gender' and s.class_name like ':className' and s.school_id = ? and s.branch_id = ? and sh.category != 'No data available' and sh.bmi_percentile < 5.00 GROUP BY YEAR(sh.date_of_measurement), MONTH(sh.date_of_measurement)"
							.replace(":gender", gender).replace(":className", className),
					new Object[] { startDate, endDate, schoolId, Integer.valueOf(branchId) }, new RowMapper<StudentDetails>() {
						public StudentDetails mapRow(ResultSet rs, int rowNum) throws SQLException {
							StudentDetails studentDetails = new StudentDetails();
							studentDetails.setCategoryTotal(rs.getInt("total"));
							studentDetails.setDateOfMeasurement(rs.getDate("date_of_measurement"));
							return studentDetails;
						}
					});
			return studentDetails;
		} catch (EmptyResultDataAccessException erdae) {
			erdae.printStackTrace();
			return new ArrayList<StudentDetails>();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new ArrayList<StudentDetails>();
	}

	public List<StudentDetails> getReportTotalCountForAboveReferenceRanage(String startDate, String endDate,
			String schoolId, int branchId, String gender, String className) {
		try {
			List<StudentDetails> studentDetails = this.jdbcTemplate.query(
					"select sh.category, count(*) as total, sh.date_of_measurement from student s inner join student_health sh on s.student_roll_id = sh.student_roll_id where sh.date_of_measurement between ? and ? and sh.category != 'No data available' and s.gender like ':gender' and s.class_name like ':className' and sh.bmi_percentile > 85.00 and s.school_id = ? and s.branch_id = ? GROUP BY YEAR(sh.date_of_measurement), MONTH(sh.date_of_measurement)"
							.replace(":gender", gender).replace(":className", className),
					new Object[] { startDate, endDate, schoolId, Integer.valueOf(branchId) }, new RowMapper<StudentDetails>() {
						public StudentDetails mapRow(ResultSet rs, int rowNum) throws SQLException {
							StudentDetails studentDetails = new StudentDetails();
							studentDetails.setCategoryTotal(rs.getInt("total"));
							studentDetails.setDateOfMeasurement(rs.getDate("date_of_measurement"));
							return studentDetails;
						}
					});
			return studentDetails;
		} catch (EmptyResultDataAccessException erdae) {
			erdae.printStackTrace();
			return new ArrayList<StudentDetails>();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new ArrayList<StudentDetails>();
	}

	public List<StudentDetails> getReportTotalCountForhealthy(String startDate, String endDate, String schoolId,
			int branchId, String gender, String className) {
		try {
			List<StudentDetails> studentDetails = this.jdbcTemplate.query(
					"select sh.date_of_measurement, sh.category, count(*) as total, sh.date_of_measurement from student s inner join student_health sh on s.student_roll_id = sh.student_roll_id where sh.date_of_measurement between ? and ? and sh.bmi_percentile between 15.00 and 85.00 and s.school_id = ? and s.branch_id = ? and sh.category != 'No data available' and s.gender like ':gender' and s.class_name like ':className' GROUP BY YEAR(sh.date_of_measurement), MONTH(sh.date_of_measurement)"
							.replace(":gender", gender).replace(":className", className),
					new Object[] { startDate, endDate, schoolId, Integer.valueOf(branchId) }, new RowMapper<StudentDetails>() {
						public StudentDetails mapRow(ResultSet rs, int rowNum) throws SQLException {
							StudentDetails studentDetails = new StudentDetails();
							studentDetails.setCategoryTotal(rs.getInt("total"));
							studentDetails.setDateOfMeasurement(rs.getDate("date_of_measurement"));
							return studentDetails;
						}
					});
			return studentDetails;
		} catch (EmptyResultDataAccessException erdae) {
			erdae.printStackTrace();
			return new ArrayList<StudentDetails>();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new ArrayList<StudentDetails>();
	}

	public String getProfileImageForStudent(String schoolId, int branchId, String studentRollId) {
		System.out.println("==== " + schoolId + "=== " + branchId + "==== " + studentRollId);
		String profileImage = null;
		try {
			profileImage = (String) this.jdbcTemplate.queryForObject(
					"SELECT profile_image FROM student WHERE school_id = ? AND branch_id = ? AND student_roll_id = ?",
					new Object[] { schoolId, Integer.valueOf(branchId), studentRollId }, String.class);
		} catch (EmptyResultDataAccessException erde) {
			erde.printStackTrace();
		}

		return ((profileImage == null) ? "/resources/images/unisex.png"
				: CommonUtil.appendBase64StringInImageUrl(profileImage));
	}

/*	public void createTemporaryTableOfDates(List<String> dates, int userId) {
		try {
			this.jdbcTemplate.execute("DROP TABLE IF EXISTS months_" + userId);

			this.jdbcTemplate.execute("CREATE TABLE months_" + userId
					+ " ( yearmonth int DEFAULT NULL ) ENGINE=InnoDB DEFAULT CHARSET=utf8");

			String inserQuery = "INSERT INTO months_" + userId + " (yearmonth) VALUES (?)";
			this.jdbcTemplate.batchUpdate(inserQuery, new BatchPreparedStatementSetter(dates) {
				public void setValues(PreparedStatement ps, int i) throws SQLException {
					String date = (String) this.val$dates.get(i);
					ps.setInt(1, Integer.parseInt(date));
				}

				public int getBatchSize() {
					return this.val$dates.size();
				}
			});
		} catch (DataAccessException dae) {
			dae.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}*/

	public void addImportedStudent(Row row, boolean addHealthHistory)
			throws DuplicateKeyException, CustomGenericException {
		StudentDetails studentDetails = new StudentDetails();
		try {
			studentDetails.setStudentRollId(row.getCell(0).getStringCellValue());
			studentDetails.setFirstName(row.getCell(1).getStringCellValue());
			studentDetails.setLastName(row.getCell(2, Row.CREATE_NULL_AS_BLANK).getStringCellValue());

			if (row.getCell(3).getStringCellValue().length() > 1) {
				studentDetails.setGender(row.getCell(3).getStringCellValue());
			} else if (row.getCell(3).getStringCellValue().equalsIgnoreCase("F"))
				studentDetails.setGender("Female");
			else {
				studentDetails.setGender("Male");
			}

			studentDetails.setSchoolId(String.valueOf(Double.valueOf(row.getCell(4).getNumericCellValue()).intValue()));
			studentDetails.setBranchId(Double.valueOf(row.getCell(5).getNumericCellValue()).intValue());
			studentDetails.setClassName(row.getCell(6).getStringCellValue());
			studentDetails.setSectionName(row.getCell(7, Row.CREATE_NULL_AS_BLANK).getStringCellValue());
			studentDetails.setDOB(row.getCell(8).getDateCellValue());

			studentDetails.setHeight(
					Double.valueOf(row.getCell(9, Row.CREATE_NULL_AS_BLANK).getNumericCellValue()).intValue());
			studentDetails.setHeightUnit(row.getCell(10, Row.CREATE_NULL_AS_BLANK).getStringCellValue());
			studentDetails.setWeight(
					Double.valueOf(row.getCell(11, Row.CREATE_NULL_AS_BLANK).getNumericCellValue()).intValue());
			studentDetails.setWeightUnit(row.getCell(12, Row.CREATE_NULL_AS_BLANK).getStringCellValue());

			studentDetails.setStudentActive(true);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Problem in fetching the values from xls/xlsx file: <br/>"
					+ studentDetails.toString() + "======>" + e.getMessage());
		}

		addStudentDetailsFromImportFile(studentDetails, addHealthHistory);
	}

	@Transactional
	private void addStudentDetailsFromImportFile(StudentDetails studentDetails, boolean addHealthHistory)
			throws DuplicateKeyException, CustomGenericException {
		String sqlForStudent = null;
		try {
			if (isStudentExists(studentDetails.getSchoolId(), studentDetails.getBranchId(),
					studentDetails.getClassName(), studentDetails.getStudentRollId())) {
				sqlForStudent = "UPDATE  student SET first_name=?,last_name=?, class_name=? , section_name=?, gender=?, date_of_birth=?, updated_dt=?, isActive=? where student_roll_id = ? and school_id=?";

				this.jdbcTemplate.update(sqlForStudent,
						new Object[] { studentDetails.getFirstName(), studentDetails.getLastName(),
								studentDetails.getClassName(), studentDetails.getSectionName(),
								studentDetails.getGender(), studentDetails.getDOB(), new Date(),
								Boolean.valueOf(studentDetails.isStudentActive()), studentDetails.getStudentRollId(),
								studentDetails.getSchoolId() });
			} else {
				sqlForStudent = "INSERT INTO student (student_roll_id, first_name, last_name, school_id, branch_id, class_name, section_name, gender, date_of_birth,inserted_dt,updated_dt,isActive) VALUES(?,?,?,?,?,?,?,?,?,?,?,?)";

				this.jdbcTemplate.update(sqlForStudent,
						new Object[] { studentDetails.getStudentRollId(), studentDetails.getFirstName(),
								studentDetails.getLastName(), studentDetails.getSchoolId(),
								Integer.valueOf(studentDetails.getBranchId()), studentDetails.getClassName(),
								studentDetails.getSectionName(), studentDetails.getGender(), studentDetails.getDOB(),
								new Date(), new Date(), Boolean.valueOf(studentDetails.isStudentActive()) });
			}

		} catch (DuplicateKeyException dke) {
			throw new DuplicateKeyException("Duplicate key <br/>" + studentDetails.toString());
		} catch (DataIntegrityViolationException dive) {
			dive.printStackTrace();
			throw new CustomGenericException(
					"Cannot add or update a child row: a foreign key constraint fails. <br/> Check whether SchoolId or branchId or class name is created, before adding student with that school and branch");
		}

		if (studentDetails.getDOB() == null)
			return;
		System.out.println(CommonUtil.validateDoubleValue(studentDetails.getHeight()) + " "
				+ CommonUtil.validateDoubleValue(studentDetails.getWeight()));
		if ((!(CommonUtil.validateDoubleValue(studentDetails.getHeight())))
				|| (!(CommonUtil.validateDoubleValue(studentDetails.getWeight())))) {
			return;
		}

		AgeBMICalculator bmiCalculatror = new AgeBMICalculator();

		double bmi = 0.0D;
		int age = 0;
		String ibw = "";
		try {
			Date dateOfBirth = studentDetails.getDOB();

			System.out.println("DOB from student details" + dateOfBirth);

			age = AgeBMICalculator.calculateAge(dateOfBirth);
			studentDetails.setAge(age);

			System.out.println("==============");
			System.out.println(studentDetails.getHeight() + " === " + studentDetails.getWeight());

			bmi = bmiCalculatror.getBMIKg(studentDetails.getHeight(), studentDetails.getWeight());

			System.out.println("bmi value from data assembler-------------->>>>>>>>>" + bmi);

			ibw = bmiCalculatror.getIBW(studentDetails.getGender(), dateOfBirth);

			System.out.println("ibw value from data assembler<<<<<<<<<<<<<<<<<<<<<<<" + ibw);

			double calculatedBMIval = bmiCalculatror.getBMIKg(studentDetails.getHeight(), studentDetails.getWeight());

			System.out.println("bmival" + calculatedBMIval);

			int ageInMonths = AgeBMICalculator.getAgeInMonths(dateOfBirth);

			double genderIndex = bmiCalculatror.getGenderIndex(studentDetails.getGender());

			double calculatepercentageval = bmiCalculatror.getPercentage(calculatedBMIval, ageInMonths, genderIndex,
					BMIForAge.DATA);

			System.out.println("bmipercentilevalue" + calculatepercentageval);

			studentDetails.setBmiPercentile(calculatepercentageval);

			studentDetails.setBmi(bmi);
			studentDetails.setIbw(ibw);
		} catch (ParseException e) {
			e.printStackTrace();

			throw new CustomGenericException("ParseException : <br/>[DOB =" + studentDetails.getDOB() + " " + ",Age ="
					+ studentDetails.getAge() + " ,height =" + studentDetails.getHeight() + " " + ",weight ="
					+ studentDetails.getWeight());
		}

		System.out.println("============================================");
		System.out.println(studentDetails.getAge());
		if (studentDetails.getBmiPercentile() >= 0.0D) {
			try {
				StudentDetails currentHealth = getStudentCurrentHealth(studentDetails.getSchoolId(),
						studentDetails.getStudentRollId());
				WellnessLookupAssociation wellness = this.wellnessLookupAssociationDAO.getWellnessReportCategory(
						studentDetails.getGender(), new Double(studentDetails.getAge()).intValue(),
						new Double(studentDetails.getBmiPercentile()).intValue());

				if (currentHealth != null) {
					sqlForStudent = "UPDATE student_health SET class_name=?, age=?, height=?, height_unit=?, weight=?, weight_unit=?,bmi=?, bmi_percentile=?, ibw=?, bmi_type=?,date_of_measurement=?, category=? WHERE student_roll_id=? AND school_id=?";

					this.jdbcTemplate.update(sqlForStudent,
							new Object[] { studentDetails.getClassName(), Double.valueOf(studentDetails.getAge()),
									Double.valueOf(studentDetails.getHeight()), studentDetails.getHeightUnit(),
									Double.valueOf(studentDetails.getWeight()), studentDetails.getWeightUnit(),
									Double.valueOf(studentDetails.getBmi()),
									Double.valueOf(studentDetails.getBmiPercentile()), studentDetails.getIbw(),
									studentDetails.getBmiType(), new Date(),
									(wellness.getCategory() == null) ? "" : wellness.getCategory(),
									studentDetails.getStudentRollId(), studentDetails.getSchoolId() });
				} else {
					sqlForStudent = "insert into student_health (student_roll_id, school_id, class_name, age, height, height_unit, weight, weight_unit,bmi, bmi_percentile, ibw, bmi_type,date_of_measurement, category) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

					this.jdbcTemplate.update(sqlForStudent,
							new Object[] { studentDetails.getStudentRollId(), studentDetails.getSchoolId(),
									studentDetails.getClassName(), Double.valueOf(studentDetails.getAge()),
									Double.valueOf(studentDetails.getHeight()), studentDetails.getHeightUnit(),
									Double.valueOf(studentDetails.getWeight()), studentDetails.getWeightUnit(),
									Double.valueOf(studentDetails.getBmi()),
									Double.valueOf(studentDetails.getBmiPercentile()), studentDetails.getIbw(),
									studentDetails.getBmiType(), new Date(),
									(wellness.getCategory() == null) ? "" : wellness.getCategory() });
				}

			} catch (NullPointerException npe) {
				throw new NullPointerException("Null pointer exception :" + studentDetails.toString());
			}

			if (!(addHealthHistory))
				return;
			sqlForStudent = "insert into student_health_history (student_roll_id, school_id, class_name, age, height, height_unit, weight, weight_unit,bmi, bmi_percentile, ibw, bmi_type, date_of_measurement) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?)";

			this.jdbcTemplate.update(sqlForStudent,
					new Object[] { studentDetails.getStudentRollId(), studentDetails.getSchoolId(),
							studentDetails.getClassName(), Double.valueOf(studentDetails.getAge()),
							Double.valueOf(studentDetails.getHeight()), studentDetails.getHeightUnit(),
							Double.valueOf(studentDetails.getWeight()), studentDetails.getWeightUnit(),
							Double.valueOf(studentDetails.getBmi()), Double.valueOf(studentDetails.getBmiPercentile()),
							studentDetails.getIbw(), studentDetails.getBmiType(), new Date() });
		} else {
			throw new CustomGenericException("Bmi percentile cann't be calculated. Check the DOB, height & weight <br/>"
					+ studentDetails.toString());
		}
	}

	private boolean isStudentExists(String schoolId, int branchId, String className, String rollNumber) {
		boolean flag = false;
		Integer cnt = (Integer) this.jdbcTemplate.queryForObject(
				"SELECT count(*) FROM student WHERE school_id=? AND branch_id=? AND class_name=? AND student_roll_id=?",
				new Object[] { schoolId, Integer.valueOf(branchId), className, rollNumber }, Integer.class);

		if ((cnt != null) && (cnt.intValue() > 0)) {
			flag = true;
		}
		return flag;
	}

	public List<StudentDetails> getStudentsIDBySchool_Branch_Class_Gender(String schoolId, int branchId, String clazz,
			String gender) {
		List<StudentDetails> studentDetails = this.jdbcTemplate.query(
				"select student_roll_id, first_name, last_name from student where school_id=? and branch_id=? and class_name like ':class' and gender like ':gender'"
						.replace(":class", clazz).replace(":gender", gender),
				new Object[] { schoolId, Integer.valueOf(branchId) }, new RowMapper<StudentDetails>() {
					public StudentDetails mapRow(ResultSet rs, int rowNum) throws SQLException {
						StudentDetails studentDetails = new StudentDetails();
						studentDetails.setStudentRollId(rs.getString("student_roll_id"));
						studentDetails.setFirstName(rs.getString("first_name"));
						studentDetails.setLastName(rs.getString("last_name"));
						return studentDetails;
					}
				});
		return studentDetails;
	}

	public List<String> getMonthWiseHealthCategoryForStudent(String schoolId, String studentRollId) {
		List<String> categories = this.jdbcTemplate.query(
				"select COALESCE(shh.category, '-') as category from months t left outer join (select shh.date_of_measurement, wla.category from student_health_history shh inner join wellness_lookup_association wla on ((shh.age between wla.age_lower_limit and wla.age_upper_limit) and (shh.bmi between wla.bmi_lower_limit and wla.bmi_upper_limit)) where (shh.school_id = ? and shh.student_roll_id = ?) group by shh.date_of_measurement) shh on DATE_FORMAT(shh.date_of_measurement, '%Y%m') = t.yearmonth",
				new Object[] { schoolId, studentRollId }, new RowMapper<String>() {
					public String mapRow(ResultSet rs, int rowNum) throws SQLException {
						return rs.getString("category");
					}
				});
		return categories;
	}
	
	/*public void deleteTemporaryTableOfDates(int userId) {
		this.jdbcTemplate.execute("DROP TABLE IF EXISTS months_" + userId);
	}*/
}
