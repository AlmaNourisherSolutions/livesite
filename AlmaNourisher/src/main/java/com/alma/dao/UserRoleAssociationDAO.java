package com.alma.dao;

import com.alma.model.UserRoleAssociation;
import java.util.List;

public interface UserRoleAssociationDAO {
	public List<UserRoleAssociation> getRolesByUserID(int paramInt);

	public boolean isSchoolAdmin(List<UserRoleAssociation> paramList);

	public boolean isAlmaAdmin(List<UserRoleAssociation> paramList);

	public boolean isParent(List<UserRoleAssociation> paramList);
}
