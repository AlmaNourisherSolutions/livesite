package com.alma.model;

import java.util.ArrayList;
import java.util.List;

public class Questionaire {
	private int questionId;
	private String question;
	private String questionType;
	private String[] options;
	private List<QFoodGrouping> foodgroups = new ArrayList<QFoodGrouping>();
	private String mcqAns;
	private String boolAns;
	
	private int studentId;
	private int schoolId;
	
	private int qorder;
	
	public int getQuestionId() {
		return questionId;
	}
	public void setQuestionId(int questionId) {
		this.questionId = questionId;
	}
	public String getQuestion() {
		return question;
	}
	public void setQuestion(String question) {
		this.question = question;
	}
	public String getQuestionType() {
		return questionType;
	}
	public void setQuestionType(String questionType) {
		this.questionType = questionType;
	}
	public String[] getOptions() {
		return options;
	}
	public void setOptions(String[] options) {
		this.options = options;
	}
	public int getQorder() {
		return qorder;
	}
	public void setQorder(int qorder) {
		this.qorder = qorder;
	}
	public List<QFoodGrouping> getFoodgroups() {
		return foodgroups;
	}
	public void setFoodgroups(List<QFoodGrouping> foodgroups) {
		this.foodgroups = foodgroups;
	}
	public String getMcqAns() {
		return mcqAns;
	}
	public void setMcqAns(String mcqAns) {
		this.mcqAns = mcqAns;
	}
	public String getBoolAns() {
		return boolAns;
	}
	public void setBoolAns(String boolAns) {
		this.boolAns = boolAns;
	}
	public int getSchoolId() {
		return schoolId;
	}
	public void setSchoolId(int schoolId) {
		this.schoolId = schoolId;
	}
	public int getStudentId() {
		return studentId;
	}
	public void setStudentId(int studentId) {
		this.studentId = studentId;
	}
}
