package com.alma.dao;

import com.alma.model.Branch;

import java.util.List;

public interface BranchDAO {
	public List<Branch> getBranchBasedOnSchoolId(String paramString);

	public Branch getSchoolAndBranchBasedOnSchoolAdmin(int paramInt);

	public Branch getSchoolAndBranchBasedOnIds(String paramString1, String paramString2);
	
	public Number saveOrUpdate(Branch branch);

	public List<String> getClassBasedOnBranchId(String branchId);

	public List<String> getClassLookup();

	public void deleteSchoolBranch(int branchId);
}