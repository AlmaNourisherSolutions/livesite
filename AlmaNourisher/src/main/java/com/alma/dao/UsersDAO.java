package com.alma.dao;

import com.alma.model.ContactUs;
import com.alma.model.Users;
import java.util.List;

public abstract interface UsersDAO {
	public abstract int addUser(Users paramUsers);

	public abstract Users getUser(String paramString);

	public abstract void updateUser(Users paramUsers);

	public abstract void deleteUser(int paramInt);

	public abstract List<Users> getUsers();

	public abstract List<Users> getUsersBasedOnJoinOfRoleAssociation(String paramString1, String paramString2,
			int paramInt, boolean paramBoolean);

	public abstract Users getUserRoleByID(int paramInt);

	public abstract Users getIDByUserEmail(String paramString);

	public abstract List<Users> getUsersBasedOnSchoolAdmin(int paramInt);

	public abstract Users getEmail(String paramString);

	public abstract void updateToken(String paramString1, String paramString2);

	public abstract int getUserIdByToken(String paramString);

	public abstract int updatePasswordForUser(int paramInt, String paramString);

	public abstract Users getUserPasswdByUserid(int paramInt);

	public abstract Users getUserByUserid(int paramInt);

	public abstract int updateUserProfile(String paramString1, String paramString2, String paramString3,
			String paramString4, String paramString5, int paramInt);

	public abstract void delete(String paramString);

	public abstract List<Users> getUserByRole(String paramString);

	public abstract int validateParentSchoolAssociation(String schoolUrl, Users u);

	public abstract int validateAdminSchoolAssociation(String schoolUrl, Users u);

	public abstract String userAssociatedVerification(String role, String url, Users loginUser);

	public abstract int addContactUsRecord(ContactUs contactUs);

	public abstract void encryptPassword();
}
