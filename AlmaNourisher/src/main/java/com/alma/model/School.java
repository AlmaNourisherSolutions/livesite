package com.alma.model;

import java.util.Date;

public class School {
	private int schoolId;
	private String schoolName;
	private String schoolLogo;
	private String landlineNumber;
	private String mobileNumber;
	private String schoolAddress;
	private String schoolEmail;
	private String schoolContactPerson;
	private String schoolCity;
	private String schoolState;
	private String schoolCountry;
	private String schoolPostalCode;
	private Date insertedDate;
	private Date updatedDate;
	private boolean schoolIsActive;
	
	private Users schoolAdmin;
	public School() {
	}

	public School(int schoolId, String schoolName, String schoolLogo, String landlineNumber, String mobileNumber,
			String schoolAddress, String schoolEmail, String schoolContactPerson, String schoolCity, String schoolState,
			String schoolCountry, String schoolPostalCode, Date insertedDate, Date updatedDate,
			boolean schoolIsActive) {
		this.schoolId = schoolId;
		this.schoolName = schoolName;
		this.schoolLogo = schoolLogo;
		this.landlineNumber = landlineNumber;
		this.mobileNumber = mobileNumber;
		this.schoolAddress = schoolAddress;
		this.schoolEmail = schoolEmail;
		this.schoolContactPerson = schoolContactPerson;
		this.schoolCity = schoolCity;
		this.schoolState = schoolState;
		this.schoolCountry = schoolCountry;
		this.schoolPostalCode = schoolPostalCode;
		this.insertedDate = insertedDate;
		this.updatedDate = updatedDate;
		this.schoolIsActive = schoolIsActive;
	}

	public int getSchoolId() {
		return this.schoolId;
	}

	public void setSchoolId(int schoolId) {
		this.schoolId = schoolId;
	}

	public String getSchoolName() {
		return this.schoolName;
	}

	public void setSchoolName(String schoolName) {
		this.schoolName = schoolName;
	}

	public String getSchoolLogo() {
		return this.schoolLogo;
	}

	public void setSchoolLogo(String schoolLogo) {
		this.schoolLogo = schoolLogo;
	}

	public String getLandlineNumber() {
		return this.landlineNumber;
	}

	public void setLandlineNumber(String landlineNumber) {
		this.landlineNumber = landlineNumber;
	}

	public String getMobileNumber() {
		return this.mobileNumber;
	}

	public void setMobileNumber(String mobileNumber) {
		this.mobileNumber = mobileNumber;
	}

	public String getSchoolAddress() {
		return this.schoolAddress;
	}

	public void setSchoolAddress(String schoolAddress) {
		this.schoolAddress = schoolAddress;
	}

	public String getSchoolEmail() {
		return this.schoolEmail;
	}

	public void setSchoolEmail(String schoolEmail) {
		this.schoolEmail = schoolEmail;
	}

	public String getSchoolContactPerson() {
		return this.schoolContactPerson;
	}

	public void setSchoolContactPerson(String schoolContactPerson) {
		this.schoolContactPerson = schoolContactPerson;
	}

	public String getSchoolCity() {
		return this.schoolCity;
	}

	public void setSchoolCity(String schoolCity) {
		this.schoolCity = schoolCity;
	}

	public String getSchoolState() {
		return this.schoolState;
	}

	public void setSchoolState(String schoolState) {
		this.schoolState = schoolState;
	}

	public String getSchoolCountry() {
		return this.schoolCountry;
	}

	public void setSchoolCountry(String schoolCountry) {
		this.schoolCountry = schoolCountry;
	}

	public String getSchoolPostalCode() {
		return this.schoolPostalCode;
	}

	public void setSchoolPostalCode(String schoolPostalCode) {
		this.schoolPostalCode = schoolPostalCode;
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

	public boolean isSchoolIsActive() {
		return this.schoolIsActive;
	}

	public void setSchoolIsActive(boolean schoolIsActive) {
		this.schoolIsActive = schoolIsActive;
	}

	public Users getSchoolAdmin() {
		return schoolAdmin;
	}

	public void setSchoolAdmin(Users schoolAdmin) {
		this.schoolAdmin = schoolAdmin;
	}
}
