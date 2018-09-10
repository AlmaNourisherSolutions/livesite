package com.alma.dao;

import com.alma.model.ManageSchool;
import com.alma.model.School;

import java.util.List;
import org.springframework.web.multipart.MultipartFile;

public interface ManageSchoolDAO {
	public List<ManageSchool> getSchoolDetails();

	public Number saveOrUpdate(ManageSchool paramManageSchool, MultipartFile paramMultipartFile);

	public ManageSchool getSchoolById(String paramString) throws NullPointerException;

	public List<ManageSchool> getSchoolBasedOnWildCardColumnSearch(String paramString);

	public void editSchool(ManageSchool paramManageSchool);

	public void editSchool(ManageSchool paramManageSchool, MultipartFile paramMultipartFile);

	public String getSchoolLogo(int paramInt);

	public int totalSchoolsCount();

	public String getSchoolName(String schoolName);
	
	public List<ManageSchool> getSchoolList();

	public ManageSchool getSchoolFromUrl(String schoolUrl);

	void deleteSchool(int schoolId);
}
