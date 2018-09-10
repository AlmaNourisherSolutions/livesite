package com.alma.model;

import java.util.Date;
import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;

public class StudentDetails {
	private String studentRollId;
	private String firstName;
	private String lastName;
	private String schoolId;
	private int branchId;
	private String className;
	private String sectionName;
	private String profileImage;
	private String gender;
	private Date DOB;
	private Date updatedDate;
	private Date createdDate;
	private boolean studentActive;
	private double age;
	private double height;
	private String heightUnit;
	private double weight;
	private String weightUnit;

	@DecimalMax(value = "100.00", message = "Please check the accuracy of the information you entered.")
	@DecimalMin(value = "0.0", message = "Please check the accuracy of the information you entered.")
	private double bmi;
	private double bmiPercentile;
	private String ibw;
	private String bmiType;
	private Date dateOfMeasurement;
	private String parentEmail;
	private String operationType;
	private int categoryTotal;
	private String healthCategory;
	private String yearMonth;
	private String dateOfBirth;

	public StudentDetails() {
	}

	public String toString() {
		return "StudentDetails [studentRollId=" + this.studentRollId + ", firstName=" + this.firstName + ", lastName="
				+ this.lastName + ", schoolId=" + this.schoolId + ", branchId=" + this.branchId + ", className="
				+ this.className + ", sectionName=" + this.sectionName + ", gender=" + this.gender + ", DOB=" + this.DOB
				+ ", age=" + this.age + ", height=" + this.height + ", weight=" + this.weight + ", bmi=" + this.bmi
				+ ", bmiPercentile=" + this.bmiPercentile + ", ibw=" + this.ibw + ", parentEmail=" + this.parentEmail
				+ ", operationType=" + this.operationType + "]";
	}

	public StudentDetails(String studentRollId, String firstName, String lastName, String schoolId, int branchId,
			String className, String sectionName, String profileImage, String gender, Date dOB, Date updatedDate,
			Date createdDate, int schollId, double age, double height, String heightUnit, double weight,
			String weightUnit, double bmi, double bmiPercentile, String ibw, String bmiType, Date dateOfMeasurement) {
		this.studentRollId = studentRollId;
		this.firstName = firstName;
		this.lastName = lastName;
		this.schoolId = schoolId;
		this.branchId = branchId;
		this.className = className;
		this.sectionName = sectionName;
		this.profileImage = profileImage;
		this.gender = gender;
		this.DOB = dOB;
		this.updatedDate = updatedDate;
		this.createdDate = createdDate;
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

	public String getStudentRollId() {
		return this.studentRollId;
	}

	public void setStudentRollId(String studentRollId) {
		this.studentRollId = studentRollId;
	}

	public String getFirstName() {
		return this.firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return this.lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public int getBranchId() {
		return this.branchId;
	}

	public void setBranchId(int branchId) {
		this.branchId = branchId;
	}

	public String getClassName() {
		return this.className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public String getSectionName() {
		return this.sectionName;
	}

	public void setSectionName(String sectionName) {
		this.sectionName = sectionName;
	}

	public String getProfileImage() {
		return this.profileImage;
	}

	public void setProfileImage(String profileImage) {
		this.profileImage = profileImage;
	}

	public String getGender() {
		return this.gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public Date getDOB() {
		return this.DOB;
	}

	public void setDOB(Date dOB) {
		this.DOB = dOB;
	}

	public Date getUpdatedDate() {
		return this.updatedDate;
	}

	public void setUpdatedDate(Date updatedDate) {
		this.updatedDate = updatedDate;
	}

	public Date getCreatedDate() {
		return this.createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
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

	public String getOperationType() {
		return this.operationType;
	}

	public void setOperationType(String operationType) {
		this.operationType = operationType;
	}

	public String getParentEmail() {
		return this.parentEmail;
	}

	public void setParentEmail(String parentEmail) {
		this.parentEmail = parentEmail;
	}

	public String getSchoolId() {
		return this.schoolId;
	}

	public void setSchoolId(String schoolId) {
		this.schoolId = schoolId;
	}

	public String getDateOfBirth() {
		return this.dateOfBirth;
	}

	public void setDateOfBirth(String dateOfBirth) {
		this.dateOfBirth = dateOfBirth;
	}

	public boolean isStudentActive() {
		return this.studentActive;
	}

	public void setStudentActive(boolean studentActive) {
		this.studentActive = studentActive;
	}

	public int getCategoryTotal() {
		return this.categoryTotal;
	}

	public void setCategoryTotal(int categoryTotal) {
		this.categoryTotal = categoryTotal;
	}

	public String getHealthCategory() {
		return this.healthCategory;
	}

	public void setHealthCategory(String healthCategory) {
		this.healthCategory = healthCategory;
	}

	public String getYearMonth() {
		return this.yearMonth;
	}

	public void setYearMonth(String yearMonth) {
		this.yearMonth = yearMonth;
	}
}