package com.alma.model;

import java.io.Serializable;

public class UserRoleAssociation implements Serializable {
	private static final long serialVersionUID = 1L;
	private int userId;
	private String roleName;

	public UserRoleAssociation() {
	}

	public UserRoleAssociation(int userId, String roleName) {
		setUserId(userId);
		this.roleName = roleName;
	}

	public String getRoleName() {
		return this.roleName;
	}

	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}

	public int getUserId() {
		return this.userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}
}
