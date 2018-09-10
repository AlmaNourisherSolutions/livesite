package com.alma.model;

import java.util.Date;

public class StudentHealthHistory {
	private int studentHealthHistoryId;
	private String studentRollId;
	private int schollId;
	private String className;
	private Double age;
	private int ageyears;
	private int agemonths;
	private Double height;
	private String heightUnit;
	private Double weight;
	private String weightUnit;
	private Double bmi;
	private Double bmiPercentile;
	private String ibw;
	private String bmiType;
	private Date dateOfMeasurement;
	private String healthStatus;
	private String $$hashKey;

	public StudentHealthHistory() {
	}

	public StudentHealthHistory(int studentHealthHistoryId, String studentRollId, int schollId, String className,
			Double age, Double height, String heightUnit, Double weight, String weightUnit, Double bmi,
			Double bmiPercentile, String ibw, String bmiType, Date dateOfMeasurement) {
		this.studentHealthHistoryId = studentHealthHistoryId;
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
		this.bmiType = bmiType;
		this.dateOfMeasurement = dateOfMeasurement;
	}

	public int getStudentHealthHistoryId() {
		return this.studentHealthHistoryId;
	}

	public void setStudentHealthHistoryId(int studentHealthHistoryId) {
		this.studentHealthHistoryId = studentHealthHistoryId;
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

	public Double getAge() {
		return this.age;
	}

	public void setAge(Double age) {
		this.age = age;
	}

	public Double getHeight() {
		return this.height;
	}

	public void setHeight(Double height) {
		this.height = height;
	}

	public String getHeightUnit() {
		return this.heightUnit;
	}

	public void setHeightUnit(String heightUnit) {
		this.heightUnit = heightUnit;
	}

	public Double getWeight() {
		return this.weight;
	}

	public void setWeight(Double weight) {
		this.weight = weight;
	}

	public String getWeightUnit() {
		return this.weightUnit;
	}

	public void setWeightUnit(String weightUnit) {
		this.weightUnit = weightUnit;
	}

	public Double getBmi() {
		return this.bmi;
	}

	public void setBmi(Double bmi) {
		this.bmi = bmi;
	}

	public Double getBmiPercentile() {
		return this.bmiPercentile;
	}

	public void setBmiPercentile(Double bmiPercentile) {
		this.bmiPercentile = bmiPercentile;
	}

	public String getIbw() {
		return this.ibw;
	}

	public void setIbw(String ibw) {
		this.ibw = ibw;
	}

	public String getBmiType() {
		return this.bmiType;
	}

	public void setBmiType(String bmiType) {
		this.bmiType = bmiType;
	}

	public Date getDateOfMeasurement() {
		return this.dateOfMeasurement;
	}

	public void setDateOfMeasurement(Date dateOfMeasurement) {
		this.dateOfMeasurement = dateOfMeasurement;
	}

	public String getHealthStatus() {
		return healthStatus;
	}

	public void setHealthStatus(String healthStatus) {
		this.healthStatus = healthStatus;
	}

	public String get$$hashKey() {
		return $$hashKey;
	}

	public void set$$hashKey(String $$hashKey) {
		this.$$hashKey = $$hashKey;
	}

	public int getAgeyears() {
		return ageyears;
	}

	public void setAgeyears(int ageyears) {
		this.ageyears = ageyears;
	}

	public int getAgemonths() {
		return agemonths;
	}

	public void setAgemonths(int agemonths) {
		this.agemonths = agemonths;
	}
}