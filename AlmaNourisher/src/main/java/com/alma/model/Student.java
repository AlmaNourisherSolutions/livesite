package com.alma.model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Student {
	private int studentId;
	private String studentRollId;
	private String firstName;
	private String lastName;
	private int schoolId;
	private String schoolLogo;
	private int count;
	private int branchId;
	private String className;
	private String sectionName;
	private String profileImage;
	private String gender;
	private Date DOB;
	private Date updatedDate;
	private Date createdDate;
	private boolean isActive;
	private String $$hashKey;
	
	private Users parent = new Users();
	
	private String schoolName;
	private String branchName;
	
	private WellnessLookupAssociation studentsWellness = new WellnessLookupAssociation();
	
	private List<FoodFreqReport> foodFreqReport = new ArrayList<FoodFreqReport>();
	
	private StudentHealth studentsHealth = new StudentHealth();
	
	private List<StudentHealthHistory> studentHealthHistory = new ArrayList<StudentHealthHistory>();

	public Student() {
	}

	public Student(String studentRollId, String firstName, String lastName, int schoolId, int branchId,
			String className, String sectionName, String profileImage, String gender, Date dOB, Date updatedDate,
			Date createdDate, boolean isActive) {
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
		this.isActive = isActive;
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

	public int getSchoolId() {
		return this.schoolId;
	}

	public void setSchoolId(int schoolId) {
		this.schoolId = schoolId;
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

	public void setDateOfBirth(Date dOB) {
		this.DOB = dOB;
	}
	
	public void setDOB(String doB){
		SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
		try {
			this.DOB = sdf.parse(doB);
		} catch (ParseException e) {
			try {
			sdf = new SimpleDateFormat("yyyy-MM-dd");
			this.DOB = sdf.parse(doB);
			} catch (ParseException e1) {
				e1.printStackTrace();
			}
		}
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

	public boolean isActive() {
		return this.isActive;
	}

	public void setActive(boolean isActive) {
		this.isActive = isActive;
	}

	public String getSchoolLogo() {
		return this.schoolLogo;
	}

	public void setSchoolLogo(String schoolLogo) {
		this.schoolLogo = schoolLogo;
	}

	public int getCount() {
		return this.count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public WellnessLookupAssociation getStudentsWellness() {
		return studentsWellness;
	}

	public void setStudentsWellness(WellnessLookupAssociation studentsWellness) {
		this.studentsWellness = studentsWellness;
	}

	public StudentHealth getStudentsHealth() {
		return studentsHealth;
	}

	public void setStudentsHealth(StudentHealth studentsHealth) {
		this.studentsHealth = studentsHealth;
	}

	public Users getParent() {
		return parent;
	}

	public void setParent(Users parent) {
		this.parent = parent;
	}

	public String getBranchName() {
		return branchName;
	}

	public void setBranchName(String branchName) {
		this.branchName = branchName;
	}

	public String getSchoolName() {
		return schoolName;
	}

	public void setSchoolName(String schoolName) {
		this.schoolName = schoolName;
	}

	public List<StudentHealthHistory> getStudentHealthHistory() {
		return studentHealthHistory;
	}

	public void setStudentHealthHistory(List<StudentHealthHistory> studentHealthHistory) {
		this.studentHealthHistory = studentHealthHistory;
	}

	public int getStudentId() {
		return studentId;
	}

	public void setStudentId(int studentId) {
		this.studentId = studentId;
	}

	public List<FoodFreqReport> getFoodFreqReport() {
		return foodFreqReport;
	}

	public void setFoodFreqReport(List<FoodFreqReport> foodFreqReport) {
		this.foodFreqReport = foodFreqReport;
	}

	public String get$$hashKey() {
		return $$hashKey;
	}

	public void set$$hashKey(String $$hashKey) {
		this.$$hashKey = $$hashKey;
	}
}