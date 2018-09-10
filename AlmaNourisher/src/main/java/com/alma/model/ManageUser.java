package com.alma.model;

import java.util.Date;

public class ManageUser {

	private int schoolId;
	private int branchId;
	private int userid;
	private String email;
	private String password;
	private String firstName;
	private String lastName;
	private Date updatedDate;
	private Date insertedDate;
	private boolean userisActive;
	private String contact;
	private String role_name;
	private String operationType;

	public ManageUser() {
	}

	public String toString() {
		return "[email=" + this.email + ", firstName=" + this.firstName + ", lastName=" + this.lastName
				+ ", userisActive=" + this.userisActive + ", contact=" + this.contact + "]";
	}

	public ManageUser(String email, String password, String firstName, String lastName, Date updatedDate,
			Date insertedDate, boolean userisActive, String contact, String role_name) {
		this.email = email;
		this.password = password;
		this.firstName = firstName;
		this.lastName = lastName;
		this.updatedDate = updatedDate;
		setUserisActive(userisActive);
		this.insertedDate = insertedDate;
		this.contact = contact;
		this.role_name = role_name;
	}

	public String getEmail() {
		return this.email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return this.password;
	}

	public void setPassword(String password) {
		this.password = password;
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

	public Date getUpdatedDate() {
		return this.updatedDate;
	}

	public void setUpdatedDate(Date updatedDate) {
		this.updatedDate = updatedDate;
	}

	public String getContact() {
		return this.contact;
	}

	public void setContact(String contact) {
		this.contact = contact;
	}

	public Date getInsertedDate() {
		return this.insertedDate;
	}

	public void setInsertedSate(Date insertedDate) {
		this.insertedDate = insertedDate;
	}

	public String getRole_name() {
		return this.role_name;
	}

	public void setRole_name(String role_name) {
		this.role_name = role_name;
	}

	public boolean isUserisActive() {
		return this.userisActive;
	}

	public void setUserisActive(boolean userisActive) {
		this.userisActive = userisActive;
	}

	public String getOperationType() {
		return this.operationType;
	}

	public void setOperationType(String operationType) {
		this.operationType = operationType;
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

	public int getUserid() {
		return this.userid;
	}

	public void setUserid(int userid) {
		this.userid = userid;
	}
}
