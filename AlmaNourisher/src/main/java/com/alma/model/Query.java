package com.alma.model;

import java.util.Date;

public class Query {

	public int queryId;
	public String query;
	public int queryUserId;
	public Date queryDate;
	
	public int answerId;
	public String answer;
	public int answerUserId;
	public String role;
	public Date ansDate;
	public int getQueryId() {
		return queryId;
	}
	public void setQueryId(int queryId) {
		this.queryId = queryId;
	}
	public String getQuery() {
		return query;
	}
	public void setQuery(String query) {
		this.query = query;
	}
	public int getQueryUserId() {
		return queryUserId;
	}
	public void setQueryUserId(int queryUserId) {
		this.queryUserId = queryUserId;
	}
	public Date getQueryDate() {
		return queryDate;
	}
	public void setQueryDate(Date queryDate) {
		this.queryDate = queryDate;
	}
	public int getAnswerId() {
		return answerId;
	}
	public void setAnswerId(int answerId) {
		this.answerId = answerId;
	}
	public String getAnswer() {
		return answer;
	}
	public void setAnswer(String answer) {
		this.answer = answer;
	}
	public int getAnswerUserId() {
		return answerUserId;
	}
	public void setAnswerUserId(int answerUserId) {
		this.answerUserId = answerUserId;
	}
	public String getRole() {
		return role;
	}
	public void setRole(String role) {
		this.role = role;
	}
	public Date getAnsDate() {
		return ansDate;
	}
	public void setAnsDate(Date ansDate) {
		this.ansDate = ansDate;
	}
	
}
