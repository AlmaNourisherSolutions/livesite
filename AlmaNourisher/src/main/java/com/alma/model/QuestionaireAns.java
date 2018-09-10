package com.alma.model;

import java.util.List;

public class QuestionaireAns {
	private Student child;
	private List<Questionaire> questionaire;
	private String foodPerference;
	
	public List<Questionaire> getQuestionaire() {
		return questionaire;
	}
	public void setQuestionaire(List<Questionaire> questionaire) {
		this.questionaire = questionaire;
	}
	public Student getChild() {
		return child;
	}
	public void setChild(Student child) {
		this.child = child;
	}
	public String getFoodPerference() {
		return foodPerference;
	}
	public void setFoodPerference(String foodPerference) {
		this.foodPerference = foodPerference;
	}
}
