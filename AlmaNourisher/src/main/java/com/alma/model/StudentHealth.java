package com.alma.model;

import java.util.Date;

public class StudentHealth {
	private String studentRollId;
	private int schollId;
	private String className;
	private double age;
	private double height;
	private String heightUnit;
	private double weight;
	private String weightUnit;
	private double bmi;
	private double bmiPercentile;
	private String ibw;
	private Date dateOfMeasurement;
	private String category;
	private String bmiType;
	
	private int ageYears;
	private int ageMonths;
	
	public StudentHealth() {
	}

	public StudentHealth(String studentRollId, int schollId, String className, double age, double height,
			String heightUnit, double weight, String weightUnit, double bmi, double bmiPercentile, String ibw,
			String bmiType, Date dateOfMeasurement, String category) {
		this.studentRollId = studentRollId;
		this.schollId = schollId;
		this.className = className;
		this.age = age;
		this.height = height;
		this.heightUnit = heightUnit;
		this.weight = weight;
		this.weightUnit = weightUnit;
		this.bmi = bmi;
		this.bmiPercentile = bmiPercentile;
		this.ibw = ibw;
		this.dateOfMeasurement = dateOfMeasurement;
		this.category = category;
	}

	public String getStudentRollId() {
		return this.studentRollId;
	}

	public void setStudentRollId(String studentRollId) {
		this.studentRollId = studentRollId;
	}

	public int getSchollId() {
		return this.schollId;
	}

	public void setSchollId(int schollId) {
		this.schollId = schollId;
	}

	public String getClassName() {
		return this.className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public double getAge() {
		return this.age;
	}

	public void setAge(double age) {
		this.age = age;
	}

	public double getHeight() {
		return this.height;
	}

	public void setHeight(double height) {
		this.height = height;
	}

	public String getHeightUnit() {
		return this.heightUnit;
	}

	public void setHeightUnit(String heightUnit) {
		this.heightUnit = heightUnit;
	}

	public double getWeight() {
		return this.weight;
	}

	public void setWeight(double weight) {
		this.weight = weight;
	}

	public String getWeightUnit() {
		return this.weightUnit;
	}

	public void setWeightUnit(String weightUnit) {
		this.weightUnit = weightUnit;
	}

	public double getBmi() {
		return this.bmi;
	}

	public void setBmi(double bmi) {
		this.bmi = bmi;
	}

	public double getBmiPercentile() {
		return this.bmiPercentile;
	}

	public void setBmiPercentile(double bmiPercentile) {
		this.bmiPercentile = bmiPercentile;
	}

	public String getIbw() {
		return this.ibw;
	}

	public void setIbw(String ibw) {
		this.ibw = ibw;
	}

	public Date getDateOfMeasurement() {
		return this.dateOfMeasurement;
	}

	public void setDateOfMeasurement(Date dateOfMeasurement) {
		this.dateOfMeasurement = dateOfMeasurement;
	}

	public String getCategory() {
		return this.category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getBmiType() {
		return bmiType;
	}

	public void setBmiType(String bmiType) {
		this.bmiType = bmiType;
	}

	public int getAgeYears() {
		return ageYears;
	}

	public void setAgeYears(int ageYears) {
		this.ageYears = ageYears;
	}

	public int getAgeMonths() {
		return ageMonths;
	}

	public void setAgeMonths(int ageMonths) {
		this.ageMonths = ageMonths;
	}
}
