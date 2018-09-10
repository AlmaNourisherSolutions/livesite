package com.alma.dao;

import com.alma.model.WellnessLookupAssociation;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.sql.DataSource;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;

public class WellnessLookupAssociationImpl implements WellnessLookupAssociationDAO {
	private JdbcTemplate jdbcTemplate;

	public WellnessLookupAssociationImpl(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

	public WellnessLookupAssociation getWellnessReportCategory(String gender, int age, int bmi_percentile) {
		return ((WellnessLookupAssociation) this.jdbcTemplate
				.query("select * from wellness_lookup_association where gender = ':gender' and :age between age_lower_limit and age_upper_limit and :bmi_percentile between bmi_lower_limit and bmi_upper_limit"
						.replace(":gender", gender).replace(":age", String.valueOf(age))
						.replace(":bmi_percentile", String.valueOf(bmi_percentile)), new ResultSetExtractor<WellnessLookupAssociation>() {
							public WellnessLookupAssociation extractData(ResultSet rs)
									throws SQLException, DataAccessException {
								if (rs.next()) {
									WellnessLookupAssociation wellnessReport = new WellnessLookupAssociation();
									wellnessReport.setAgeLowerLimit(rs.getInt("age_lower_limit"));
									wellnessReport.setAgeUpperLimit(rs.getInt("age_upper_limit"));
									wellnessReport.setBmiLowerLimit(rs.getInt("bmi_lower_limit"));
									wellnessReport.setBmiUpperLimit(rs.getInt("bmi_upper_limit"));
									wellnessReport.setCategory(rs.getString("category"));
									wellnessReport.setGender(rs.getString("gender"));
									return wellnessReport;
								}
								return new WellnessLookupAssociation();
							}
						}));
	}

	@Override
	public WellnessLookupAssociation getWellnessReportbyCategory(String gender, double age, String category) {
		return ((WellnessLookupAssociation) this.jdbcTemplate
				.query("select wla.*, guide.guidelines, guide.pnts_to_remember from wellness_lookup_association wla, rep1_guidelines guide "
						+ "where wla.category = guide.category and wla.gender = ':gender' and :age between wla.age_lower_limit and wla.age_upper_limit and wla.category = ':category'"
						.replace(":gender", gender).replace(":age", String.valueOf(age))
								.replace(":category", String.valueOf(category)), new ResultSetExtractor<WellnessLookupAssociation>() {
							public WellnessLookupAssociation extractData(ResultSet rs)
									throws SQLException, DataAccessException {
								if (rs.next()) {
									WellnessLookupAssociation wellnessReport = new WellnessLookupAssociation();
									wellnessReport.setAgeLowerLimit(rs.getInt("age_lower_limit"));
									wellnessReport.setAgeUpperLimit(rs.getInt("age_upper_limit"));
									wellnessReport.setBmiLowerLimit(rs.getInt("bmi_lower_limit"));
									wellnessReport.setBmiUpperLimit(rs.getInt("bmi_upper_limit"));
									wellnessReport.setCategory(rs.getString("category"));
									wellnessReport.setGender(rs.getString("gender"));
									wellnessReport.setGuidelines(rs.getString("guidelines"));
									wellnessReport.setPntsToRem(rs.getString("pnts_to_remember"));
									return wellnessReport;
								}
								return new WellnessLookupAssociation();
							}
						}));
	}

	@Override
	public WellnessLookupAssociation getWellnessRecipe(WellnessLookupAssociation wla) {
		return ((WellnessLookupAssociation) this.jdbcTemplate
				.query("select * from rep1_recipe rec where rec.category = ':category'  order by rand() limit 1"
						.replace(":category", wla.getCategory()), new ResultSetExtractor<WellnessLookupAssociation>() {
							public WellnessLookupAssociation extractData(ResultSet rs)
									throws SQLException, DataAccessException {
								if (rs.next()) {
									wla.setRecipeName(rs.getString("name"));
									wla.setRecipeContent(rs.getString("content"));
									return wla;
								}
								return wla;
							}
						}));
	}
}
