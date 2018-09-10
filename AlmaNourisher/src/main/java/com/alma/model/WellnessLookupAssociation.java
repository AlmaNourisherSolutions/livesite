package com.alma.model;

public class WellnessLookupAssociation {
	private int ageLowerLimit;
	private int ageUpperLimit;
	private int bmiLowerLimit;
	private int bmiUpperLimit;
	private String category;
	private String guidelines;
	private String recipeContent;
	private String recipeName;
	private String gender;
	
	public String getRecipeContent() {
		return recipeContent;
	}

	public void setRecipeContent(String recipeContent) {
		this.recipeContent = recipeContent;
	}

	public String getRecipeName() {
		return recipeName;
	}

	public void setRecipeName(String recipeName) {
		this.recipeName = recipeName;
	}

	private String pntsToRem;

	public String getPntsToRem() {
		return pntsToRem;
	}

	public void setPntsToRem(String pntsToRem) {
		this.pntsToRem = pntsToRem;
	}

	public WellnessLookupAssociation() {
	}

	public WellnessLookupAssociation(int ageLowerLimit, int ageUpperLimit, int bmiLowerLimit, int bmiUpperLimit,
			String category, String reportName, String gender) {
		this.ageLowerLimit = ageLowerLimit;
		this.ageUpperLimit = ageUpperLimit;
		this.bmiLowerLimit = bmiLowerLimit;
		this.bmiUpperLimit = bmiUpperLimit;
		this.category = category;
	//	this.reportName = reportName;
		this.gender = gender;
	}

	public int getAgeLowerLimit() {
		return this.ageLowerLimit;
	}

	public void setAgeLowerLimit(int ageLowerLimit) {
		this.ageLowerLimit = ageLowerLimit;
	}

	public int getAgeUpperLimit() {
		return this.ageUpperLimit;
	}

	public void setAgeUpperLimit(int ageUpperLimit) {
		this.ageUpperLimit = ageUpperLimit;
	}

	public int getBmiLowerLimit() {
		return this.bmiLowerLimit;
	}

	public void setBmiLowerLimit(int bmiLowerLimit) {
		this.bmiLowerLimit = bmiLowerLimit;
	}

	public int getBmiUpperLimit() {
		return this.bmiUpperLimit;
	}

	public void setBmiUpperLimit(int bmiUpperLimit) {
		this.bmiUpperLimit = bmiUpperLimit;
	}

	public String getCategory() {
		return this.category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getGender() {
		return this.gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getGuidelines() {
		return guidelines;
	}

	public void setGuidelines(String guidelines) {
		this.guidelines = guidelines;
	}
}
