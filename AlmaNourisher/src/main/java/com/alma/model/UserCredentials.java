package com.alma.model;

import org.codehaus.jackson.annotate.JsonProperty;

public class UserCredentials {

    @JsonProperty
    private String username;

    @JsonProperty
    private String password;
    
    @JsonProperty
    private String role;
    
    @JsonProperty
    private String schoolUrl;


    public String getSchoolUrl() {
		return schoolUrl;
	}

	public void setSchoolUrl(String schoolUrl) {
		this.schoolUrl = schoolUrl;
	}
	
    public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}
