package com.alma.dao;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import com.alma.model.ArticleComment;
import com.alma.model.Food;
import com.alma.model.FoodFreqReport;
import com.alma.model.QFoodGrouping;
import com.alma.model.QStatistics;
import com.alma.model.Questionaire;
import com.alma.model.Student;
import com.alma.model.WellnessLookupAssociation;
import com.csvreader.CsvWriter;

public class ReportDAOImpl implements ReportDAO {
	
	private JdbcTemplate jdbcTemplate;
	private Logger log = Logger.getLogger(ReportDAOImpl.class);

	public ReportDAOImpl(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

	@Override
	public List<Questionaire> getQuestionaire() {
		List<Questionaire> questions = this.jdbcTemplate.query(
				"SELECT q.qorder,bank.* FROM rep2_questionaire q, rep2_question_bank bank where q.ques_ids = bank.QId and q.active_flag = 1 order by q.qorder",
				new Object[] {}, new RowMapper<Questionaire>() {
					public Questionaire mapRow(ResultSet rs, int rowNum) throws SQLException {
						Questionaire questionaire = new Questionaire();
						
						questionaire.setQuestionId(rs.getInt("QId"));
						questionaire.setQuestion(rs.getString("question"));
						questionaire.setQorder(rs.getInt("qorder"));
						String questionType = rs.getString("type");
						questionaire.setQuestionType(questionType);
						
						if("PA".equals(questionType)){
							String[] options = {rs.getString("option1"),rs.getString("option2"),rs.getString("option3"),rs.getString("option4")};
							questionaire.setOptions(options);
						}
						
					/*	if("".equals(questionType)){
							questionaire.setFoods();
							questionaire.setServeGroupId();
							questionaire.setServeGroupName();
							questionaire.setServings();
						}*/
						
						return questionaire;
					}
				});
		for(Questionaire question: questions){
			if("FOOD".equals(question.getQuestionType())){
				// get servings
				List<QFoodGrouping> foodGroups = this.jdbcTemplate.query(
						"SELECT grp.group_id, grp.group_name, (select l1.value from rep2_servings_lookup l1 where grp.serve1 = l1.id) as sval1, "
						+ "(select l2.value from rep2_servings_lookup l2 where grp.serve2 = l2.id) as sval2, "
						+ "(select l3.value from rep2_servings_lookup l3 where grp.serve3 = l3.id) as sval3, "
						+ "(select l4.value from rep2_servings_lookup l4 where grp.serve4 = l4.id) as sval4 "
						+ "FROM rep2_servings_group grp where grp.group_id in (select food.group_id from rep2_qfood_options food where food.ques_id = ? group by food.group_id)",
						new Object[] {Integer.valueOf(question.getQuestionId())}, new RowMapper<QFoodGrouping>() {
							public QFoodGrouping mapRow(ResultSet rs, int rowNum) throws SQLException {
								QFoodGrouping qf = new QFoodGrouping();
								qf.setServeGroupId(rs.getInt("group_id"));
								qf.setServeGroupName(rs.getString("group_name"));
								List<String> s = new ArrayList<String>(Arrays.asList(rs.getString("sval1"),rs.getString("sval2"),rs.getString("sval3"),rs.getString("sval4")));
								qf.setServings(s);
								return qf;
							}
						});
				// get food list
				for(QFoodGrouping group: foodGroups){
					List<Food> foods = this.jdbcTemplate.query(
							"select food.food_id, food.name, fscore.*, pscore.* from rep2_qfood_options food, rep2_ques_freq_score fscore, rep2_ques_portion_score pscore where fscore.food_id = food.food_id and pscore.food_id = food.food_id and food.ques_id = ? and food.group_id = ?",
									new Object[] {Integer.valueOf(question.getQuestionId()),Integer.valueOf(group.getServeGroupId())}, new RowMapper<Food>() {
										public Food mapRow(ResultSet rs, int rowNum) throws SQLException {
											Food f = new Food();
											f.setFoodName(rs.getString("name"));
											f.setFoodId(rs.getInt("food_id"));
											 
											Map<String, String> freqScoreCard = new HashMap<String, String>();
											freqScoreCard.put("23_times_day", rs.getString("23_times_day"));
											freqScoreCard.put("once_day", rs.getString("once_day"));
											freqScoreCard.put("23_times_week", rs.getString("23_times_week"));
											freqScoreCard.put("once_week", rs.getString("once_week"));
											freqScoreCard.put("never", rs.getString("never"));
											f.setFreqScoreCard(freqScoreCard);
											
											Map<String, String> servScoreCard = new HashMap<String, String>();
											servScoreCard.put("serve1", rs.getString("serve1"));
											servScoreCard.put("serve2", rs.getString("serve2"));
											servScoreCard.put("serve3", rs.getString("serve3"));
											servScoreCard.put("serve4", rs.getString("serve4"));
											f.setServScoreCard(servScoreCard);
											
											return f;
										}
									});
					group.setFoods(foods);
				}
				question.setFoodgroups(foodGroups);
			}
		}
		return questions;
	}
	
	public String submitQuestionaire(List<Questionaire> questionaire, Student student, String foodPerference){
		
		List<FoodFreqReport> ffReport = new ArrayList<FoodFreqReport>();
		for(Questionaire question: questionaire){
			if("FOOD".equals(question.getQuestionType())){
				this.log.debug("Populating rep2_ques_answers table for food: question: "+question.getQuestion());
				String sql = "INSERT INTO rep2_ques_answers( student_id, ques_id, type, create_date, school_id) VALUES (?,?,?,?,?)";
				KeyHolder keyHolder = new GeneratedKeyHolder();
				this.jdbcTemplate.update(new PreparedStatementCreator() {
				        public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
				            PreparedStatement ps =
				                connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
				            ps.setInt(1, student.getStudentId());
				            ps.setInt(2, question.getQuestionId());
				            ps.setString(3, question.getQuestionType());
				            Date utilDate = new Date();
				            ps.setDate(4,  new java.sql.Date(utilDate.getTime()));
				            ps.setInt(5, student.getSchoolId());
				            return ps;
				        }
				    },
				    keyHolder);
				    
				FoodFreqReport ffr = new FoodFreqReport();
				ffr.setStudentId(student.getStudentId());
				ffr.setQuesId(question.getQuestionId());
				ffr.setQuesType(question.getQuestionType());
				ffr.setCreateDate(new Date());
				
				int score = 0;
				 for(QFoodGrouping foodGroup: question.getFoodgroups()){
					 for(Food food: foodGroup.getFoods()){
						 this.log.debug("Populating rep2_ques_food_answer table for food: "+food.getFoodName());
							String sqlFreq = "INSERT INTO rep2_ques_food_answer(qna_id, student_id, ques_id, food_id, freqans,servans) VALUES (?,?,?,?,?,?)";

							 this.jdbcTemplate.update(sqlFreq,
									new Object[] { keyHolder.getKey(), student.getStudentId(),question.getQuestionId(),food.getFoodId(),food.getFreqans(),food.getServans() });
					 
						 Map<String, String> fscore = food.getFreqScoreCard();
						 if(fscore.get(food.getFreqans())!=null)
							 score += Integer.parseInt(fscore.get(food.getFreqans()));
						 
						 Map<String, String> sscore = food.getServScoreCard();
						 if(sscore.get("serve"+food.getServans())!=null)
							 score += Integer.parseInt(sscore.get("serve"+food.getServans()));
						 
					 }
				 }
				 
				 ffr.setScore(score);
				 this.log.debug("Score: "+score);
				 String ansKeySql = "SELECT star, color_code, grade, content FROM rep2_food_ans_key WHERE ques_id = ? and ? BETWEEN age_group_min and age_group_max and (category = ? or category='All') and ? BETWEEN score_band_min and score_band_max and (food_preference = ? or food_preference = 'Any')";
				 this.jdbcTemplate.query(ansKeySql,
									new Object[] {Integer.valueOf(question.getQuestionId()),student.getStudentsHealth().getAge(),student.getStudentsHealth().getCategory(),
											score, foodPerference}, new RowMapper<String>() {
										public String mapRow(ResultSet rs, int rowNum) throws SQLException {
											 ffr.setGrade(rs.getString("grade"));
											 ffr.setContent(rs.getString("content"));
											 ffr.setColorCode(rs.getString("color_code"));
											return "done";
										}
									});
				 
				 ffReport.add(ffr);
			}
			else if("BOOLEAN".equals(question.getQuestionType())){
				this.log.debug("Populating rep2_ques_answers table for boolean: question: "+question.getQuestion());
				String sql = "INSERT INTO rep2_ques_answers( student_id, ques_id, type, bool_ans, create_date, school_id) VALUES (?,?,?,?,?,?)";

				this.jdbcTemplate.update(sql,
						new Object[] { student.getStudentId(), question.getQuestionId(),question.getQuestionType(), question.getBoolAns(),new Date(), student.getSchoolId() });
			
				/*FoodFreqReport ffr = new FoodFreqReport();
				ffr.setStudentId(student.getStudentId());
				ffr.setQuesId(question.getQuestionId());
				ffr.setQuesType(question.getQuestionType());
				ffr.setCreateDate(new Date());
				
				if("YES".equals(question.getBoolAns())){
					ffr.setScore(1);
				}
				else{
					ffr.setScore(0);
				}
				
				ffReport.add(ffr);*/
			}
			else if("PA".equals(question.getQuestionType())){
				this.log.debug("Populating rep2_ques_answers table for PA: question: "+question.getQuestion());
				String sql = "INSERT INTO rep2_ques_answers( student_id, ques_id, type, opt_ans, create_date, school_id) VALUES (?,?,?,?,?,?)";

				 this.jdbcTemplate.update(sql,
						new Object[] { student.getStudentId(), question.getQuestionId(),question.getQuestionType(), question.getMcqAns(),new Date(), student.getSchoolId() });
			
				 FoodFreqReport ffr = new FoodFreqReport();
					ffr.setStudentId(student.getStudentId());
					ffr.setQuesId(question.getQuestionId());
					ffr.setQuesType(question.getQuestionType());
					ffr.setCreateDate(new Date());
					
					 String ansKeySql = "SELECT opt_no, color_code, content FROM rep2_physical_activity_ans_key WHERE ques_id = ? and ? BETWEEN age_min and age_max and upper(optionVal) = upper(?)";
					 this.jdbcTemplate.query(ansKeySql,
										new Object[] {Integer.valueOf(question.getQuestionId()),student.getStudentsHealth().getAge(),question.getMcqAns()}, new RowMapper<String>() {
											public String mapRow(ResultSet rs, int rowNum) throws SQLException {
												 ffr.setGrade(rs.getString("opt_no"));
												 ffr.setContent(rs.getString("content"));
												 ffr.setColorCode(rs.getString("color_code"));
												return "done";
											}
										});
					
					ffReport.add(ffr);
			}
		}
		
		String sqlHistory = "INSERT INTO rep2_report_history( student_id, ques_id, ques_type, food_pref, score, grade, content, color_code, create_date,mvd_to_hist_date)"
				+ " SELECT student_id, ques_id, ques_type, food_pref, score, grade, content, color_code, create_date,? from rep2_report where student_id = ?";
		
		this.jdbcTemplate.update(sqlHistory,
				new Object[] { new Date(), student.getStudentId()});
		
		String sqlDelete = "DELETE FROM rep2_report where student_id = ?";
	
		this.jdbcTemplate.update(sqlDelete, new Object[] { student.getStudentId()});
		
		this.log.debug("Populating rep2_report for Student Id: "+student.getStudentId());
		//Insert report 2 content
		 String sql = "INSERT INTO rep2_report( student_id, ques_id, ques_type, score, grade, content, color_code, create_date, food_pref) VALUES (?,?,?,?,?,?,?,?,?)";

		 this.jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {

					@Override
					public void setValues(PreparedStatement ps, int i) throws SQLException {
						FoodFreqReport report = ffReport.get(i);
						ps.setInt(1, report.getStudentId());
						ps.setInt(2, report.getQuesId());
						ps.setString(3,report.getQuesType());
						ps.setInt(4, report.getScore());
						ps.setString(5, report.getGrade());
						ps.setString(6,report.getContent());
						ps.setString(7, report.getColorCode());
						ps.setDate(8, new java.sql.Date(report.getCreateDate().getTime()));
						ps.setString(9, foodPerference);
					}

					@Override
					public int getBatchSize() {
						return ffReport.size();
					}
				  });
		
		return "success";
	}

	@Override
	public WellnessLookupAssociation getNutritionReport(int studentId) {
		return ((WellnessLookupAssociation) this.jdbcTemplate
				.query("select rep.* from rep1_report rep where rep.student_id = ':studentId'"
						.replace(":studentId", String.valueOf(studentId)), new ResultSetExtractor<WellnessLookupAssociation>() {
							public WellnessLookupAssociation extractData(ResultSet rs)
									throws SQLException, DataAccessException {
								if (rs.next()) {
									WellnessLookupAssociation wellnessReport = new WellnessLookupAssociation();
									wellnessReport.setGuidelines(rs.getString("guidelines"));
									wellnessReport.setPntsToRem(rs.getString("point_to_rem"));
									wellnessReport.setRecipeName(rs.getString("recipeName"));
									wellnessReport.setRecipeContent(rs.getString("recipeContent"));
									return wellnessReport;
								}
								return new WellnessLookupAssociation();
							}
						}));
	}
	
	@Override
	public List<FoodFreqReport> getFoodFreqReport(int studentId){
		List<FoodFreqReport> ffrs = this.jdbcTemplate.query(
				"select bank.question, rep.* from rep2_report rep, rep2_question_bank bank where bank.QId = rep.ques_id and rep.student_id = ?",
				new Object[] {Integer.valueOf(studentId)}, new RowMapper<FoodFreqReport>() {
					public FoodFreqReport mapRow(ResultSet rs, int rowNum) throws SQLException {
									FoodFreqReport ffr = new FoodFreqReport();
									ffr.setQuestion(rs.getString("question"));
									ffr.setReportId(rs.getInt("report_id"));
									ffr.setStudentId(rs.getInt("student_id"));
									ffr.setQuesId(rs.getInt("ques_id"));
									ffr.setQuesType(rs.getString("ques_type"));
									ffr.setFoodPreference(rs.getString("food_pref")!=null?rs.getString("food_pref"):"");
									ffr.setScore(rs.getInt("score"));
									ffr.setGrade(rs.getString("grade"));
									ffr.setContent(rs.getString("content"));
									ffr.setColorCode(rs.getString("color_code"));
									ffr.setCreateDate(rs.getDate("create_date"));
									return ffr;
							}
						});
		return ffrs;
		
	}
	
	public void createQuestionaireTemplate(){
		List<Questionaire> questions = getQuestionaire();

		
		String outputFile = "questionaireUpload.csv";
		
		// before we open the file check to see if it already exists
		//boolean alreadyExists = new File(outputFile).exists();
			
		try {
			// use FileWriter constructor that specifies open for appending
			CsvWriter csvOutput = new CsvWriter(new FileWriter(outputFile, false), ',');
			
			
				csvOutput.write("Student Roll No");
				csvOutput.write("Student Name");
				csvOutput.write("Gender");
				csvOutput.write("Parent Email Id");
				csvOutput.write("Parent Contact No");
				csvOutput.write("Food Preference");
				
				for(Questionaire q: questions){
					if("FOOD".equals(q.getQuestionType())){
						List<QFoodGrouping> foodgroups = q.getFoodgroups();
						for(QFoodGrouping fg: foodgroups){
							List<Food> foods = fg.getFoods();
							for(Food f: foods){
								csvOutput.write(q.getQuestion()+" [Food Frequency]");
							}
						}
						for(QFoodGrouping fg: foodgroups){
							List<Food> foods = fg.getFoods();
							for(Food f: foods){
								csvOutput.write(q.getQuestion()+" [Portion sizes per serving]");
							}
						}
					}
					else{
						csvOutput.write(q.getQuestion());
					}
				}
				csvOutput.endRecord();
				
				csvOutput.write("-");
				csvOutput.write("-");
				csvOutput.write("-");
				csvOutput.write("-");
				csvOutput.write("-");
				csvOutput.write("-");
				for(Questionaire q: questions){
					if("FOOD".equals(q.getQuestionType())){
						List<QFoodGrouping> foodgroups = q.getFoodgroups();
						for(QFoodGrouping fg: foodgroups){
							List<Food> foods = fg.getFoods();
							for(Food f: foods){
								csvOutput.write(f.getFoodName());
							}
						}
						for(QFoodGrouping fg: foodgroups){
							List<Food> foods = fg.getFoods();
							for(Food f: foods){
								csvOutput.write(f.getFoodName());
							}
						}
					}
					else{
						csvOutput.write("-");
					}
				}
				csvOutput.endRecord();
				
				csvOutput.write("-");
				csvOutput.write("-");
				csvOutput.write("[M/F]");
				csvOutput.write("-");
				csvOutput.write("-");
				csvOutput.write("[Non vegetarian/Vegetarian/Eggetarian]");
				for(Questionaire q: questions){
					if("FOOD".equals(q.getQuestionType())){
						List<QFoodGrouping> foodgroups = q.getFoodgroups();
						for(QFoodGrouping fg: foodgroups){
							List<Food> foods = fg.getFoods();
							for(Food f: foods){
								csvOutput.write("[2-3 times a day / Once a day / 2-3 times a week / Once a week / Never]");
							}
						}
						for(QFoodGrouping fg: foodgroups){
							List<Food> foods = fg.getFoods();
							List<String> servings = fg.getServings();
							for(Food f: foods){
								String val = "[";
								for(String s: servings){
									val += s+" / ";
								}
								val = val.substring(0, val.length()-2);
								val += "]";
								csvOutput.write(val);
							}
						}
					}
					else if("BOOLEAN".equals(q.getQuestionType())){
						csvOutput.write("[Yes/No]");
					}
					else if("PA".equals(q.getQuestionType())){
						String[] options = q.getOptions();
						String val = "[";
						for(String s : options){
							 val += s+" / ";
						}
						val = val.substring(0, val.length()-2);
						val += "]";
						csvOutput.write(val);
					}
				}
				csvOutput.endRecord();
				
			csvOutput.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public List<QStatistics> questionaireStatistics(){
		String sql ="select res.school_name,res.branch_name, count(*) as count from ( "
				+ "select s.student_roll_id, u.email, u.contact, sch.school_name, b.branch_name from student s, student_parent sp, user_master u, school sch, branch b "
				+ "where s.student_roll_id = sp.student_roll_id and u.user_id = sp.user_id and s.school_id = sch.school_id and b.branch_id = s.branch_id and s.studentId in (select DISTINCT(r.student_id) from rep2_report r)) as res "
				+ "group by res.school_name,res.branch_name";
		List<QStatistics> statsList = this.jdbcTemplate.query(
				sql,
				new RowMapper<QStatistics>() {
					public QStatistics mapRow(ResultSet rs, int rowNum) throws SQLException {
						QStatistics stats = new QStatistics();
						stats.setSchoolName(rs.getString("school_name"));
						stats.setBranchName(rs.getString("branch_name"));
						stats.setCount(rs.getInt("count"));
						
						return stats;
					}
				});
		return statsList;
	}
}
