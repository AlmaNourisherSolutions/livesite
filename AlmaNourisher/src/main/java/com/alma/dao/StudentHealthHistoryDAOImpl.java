package com.alma.dao;

import com.alma.model.StudentHealthHistory;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import javax.sql.DataSource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

public class StudentHealthHistoryDAOImpl implements StudentHealthHistoryDAO {
	private JdbcTemplate jdbcTemplate;

	public StudentHealthHistoryDAOImpl(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

	public List<StudentHealthHistory> getStudentsHealthHistoryByRollIdSchoolId(String rollId, int schoolId) {
		List<StudentHealthHistory> studentsHealthHistory = this.jdbcTemplate.query(
				"SELECT * FROM student_health_history WHERE student_roll_id=':studentRollId' AND school_id=:schoolId order by date_of_measurement desc"
						.replace(":studentRollId", rollId).replace(":schoolId", String.valueOf(schoolId)),
				new RowMapper<StudentHealthHistory>() {
					public StudentHealthHistory mapRow(ResultSet rs, int rowNum) throws SQLException {
						StudentHealthHistory studentHealthHistory = new StudentHealthHistory();

						Double age = rs.getDouble("age");
						String[] arr=String.valueOf(age).split("\\.");
						
						studentHealthHistory.setStudentHealthHistoryId(rs.getInt("student_health_history_id"));
						studentHealthHistory.setStudentRollId(rs.getString("student_roll_id"));
						studentHealthHistory.setSchollId(rs.getInt("school_id"));
						studentHealthHistory.setClassName(rs.getString("class_name"));
						studentHealthHistory.setAge(age);
						studentHealthHistory.setHeight(rs.getDouble("height"));
						studentHealthHistory.setHeightUnit(rs.getString("height_unit"));
						studentHealthHistory.setWeight(rs.getDouble("weight"));
						studentHealthHistory.setBmi(rs.getDouble("bmi"));
						studentHealthHistory.setBmiPercentile(rs.getDouble("bmi_percentile"));
						studentHealthHistory.setIbw(rs.getString("ibw"));
						studentHealthHistory.setBmiType(rs.getString("bmi_type"));
						studentHealthHistory.setWeightUnit(rs.getString("weight_unit"));
						studentHealthHistory.setDateOfMeasurement(rs.getDate("date_of_measurement"));
						studentHealthHistory.setHealthStatus(rs.getString("category"));
						
						studentHealthHistory.setAgeyears(Integer.parseInt(arr[0]));
						studentHealthHistory.setAgemonths(Integer.parseInt(arr[1])); 

						return studentHealthHistory;
					}
				});
		return studentsHealthHistory;
	}
}
