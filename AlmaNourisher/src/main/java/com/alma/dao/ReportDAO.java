package com.alma.dao;

import java.util.List;

import com.alma.model.FoodFreqReport;
import com.alma.model.QStatistics;
import com.alma.model.Questionaire;
import com.alma.model.Student;
import com.alma.model.WellnessLookupAssociation;

public interface ReportDAO {

	List<Questionaire> getQuestionaire();

	String submitQuestionaire(List<Questionaire> questionaire, Student student,String foodPerference);

	WellnessLookupAssociation getNutritionReport(int studentId);

	List<FoodFreqReport> getFoodFreqReport(int studentId);
	
	void createQuestionaireTemplate();
	
	List<QStatistics> questionaireStatistics();

}
