package com.alma.model;

import java.io.Serializable;

public class ScreenMaster implements Serializable {
	private static final long serialVersionUID = 1L;
	private String screenName;
	private String targetPage;
	private String roleName;

	public ScreenMaster() {
	}

	public ScreenMaster(String screenName, String targetPage, String roleName) {
		this.screenName = screenName;
		this.targetPage = targetPage;
		this.roleName = roleName;
	}

	public String getScreenName() {
		return this.screenName;
	}

	public void setScreenName(String screenName) {
		this.screenName = screenName;
	}

	public String getTargetPage() {
		return this.targetPage;
	}

	public void setTargetPage(String targetPage) {
		this.targetPage = targetPage;
	}

	public String getRoleName() {
		return this.roleName;
	}

	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}
}
