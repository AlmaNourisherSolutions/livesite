package com.alma.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Branch {
	private int branchId;
	private String branchName;
	private String branchContactPerson = "-";
	private String branchDescription;
	private int schoolId;
	private String schoolName;
	private String branchEmail;
	private String branchLandlineNumber;
	private String branchMobileNumber;
	private String branchAddress;
	private String branchCity;
	private String branchState;
	private String branchCountry;
	private String branchPostalCode;
	private Date insertedDate;
	private Date updatedDate;
	private boolean branchIsActive;
	private Users schoolAdmin;

	private List<String> classes = new ArrayList<String>();
	public Branch() {
	}

	public Branch(int branchId, String branchName, String branchContactPerson, String branchDescription, int schoolId,
			String branchEmail, String branchLandlineNumber, String branchMobileNumber, String branchAddress,
			String branchCity, String branchState, String branchCountry, String branchPostalCode, Date insertedDate,
			Date updatedDate, boolean branchIsActive, String schoolAdmin) {
		this.branchId = branchId;
		this.branchName = branchName;
		this.branchContactPerson = branchContactPerson;
		this.branchDescription = branchDescription;
		this.schoolId = schoolId;
		this.branchEmail = branchEmail;
		this.branchLandlineNumber = branchLandlineNumber;
		this.branchMobileNumber = branchMobileNumber;
		this.branchAddress = branchAddress;
		this.branchCity = branchCity;
		this.branchState = branchState;
		this.branchCountry = branchCountry;
		this.branchPostalCode = branchPostalCode;
		this.insertedDate = insertedDate;
		this.updatedDate = updatedDate;
		this.branchIsActive = branchIsActive;
	}

	public int getBranchId() {
		return this.branchId;
	}

	public void setBranchId(int branchId) {
		this.branchId = branchId;
	}

	public String getBranchName() {
		return this.branchName;
	}

	public void setBranchName(String branchName) {
		this.branchName = branchName;
	}

	public String getBranchDescription() {
		return this.branchDescription;
	}

	public void setBranchDescription(String branchDescription) {
		this.branchDescription = branchDescription;
	}

	public int getSchoolId() {
		return this.schoolId;
	}

	public void setSchoolId(int schoolId) {
		this.schoolId = schoolId;
	}

	public String getBranchEmail() {
		return this.branchEmail;
	}

	public void setBranchEmail(String branchEmail) {
		this.branchEmail = branchEmail;
	}

	public String getBranchLandlineNumber() {
		return this.branchLandlineNumber;
	}

	public void setBranchLandlineNumber(String branchLandlineNumber) {
		this.branchLandlineNumber = branchLandlineNumber;
	}

	public String getBranchMobileNumber() {
		return this.branchMobileNumber;
	}

	public void setBranchMobileNumber(String branchMobileNumber) {
		this.branchMobileNumber = branchMobileNumber;
	}

	public String getBranchAddress() {
		return this.branchAddress;
	}

	public void setBranchAddress(String branchAddress) {
		this.branchAddress = branchAddress;
	}

	public String getBranchCity() {
		return this.branchCity;
	}

	public void setBranchCity(String branchCity) {
		this.branchCity = branchCity;
	}

	public String getBranchState() {
		return this.branchState;
	}

	public void setBranchState(String branchState) {
		this.branchState = branchState;
	}

	public String getBranchCountry() {
		return this.branchCountry;
	}

	public void setBranchCountry(String branchCountry) {
		this.branchCountry = branchCountry;
	}

	public String getBranchPostalCode() {
		return this.branchPostalCode;
	}

	public void setBranchPostalCode(String branchPostalCode) {
		this.branchPostalCode = branchPostalCode;
	}

	public Date getInsertedDate() {
		return this.insertedDate;
	}

	public void setInsertedDate(Date insertedDate) {
		this.insertedDate = insertedDate;
	}

	public Date getUpdatedDate() {
		return this.updatedDate;
	}

	public void setUpdatedDate(Date updatedDate) {
		this.updatedDate = updatedDate;
	}

	public boolean isBranchIsActive() {
		return this.branchIsActive;
	}

	public void setBranchIsActive(boolean branchIsActive) {
		this.branchIsActive = branchIsActive;
	}

	public String getBranchContactPerson() {
		return this.branchContactPerson;
	}

	public void setBranchContactPerson(String branchContactPerson) {
		this.branchContactPerson = branchContactPerson;
	}

	public Users getSchoolAdmin() {
		return this.schoolAdmin;
	}

	public void setSchoolAdmin(Users schoolAdmin) {
		this.schoolAdmin = schoolAdmin;
	}

	public String getSchoolName() {
		return this.schoolName;
	}

	public void setSchoolName(String schoolName) {
		this.schoolName = schoolName;
	}

	public List<String> getClasses() {
		return classes;
	}

	public void setClasses(List<String> classes) {
		this.classes = classes;
	}
}
