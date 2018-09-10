package com.alma.dao;

import com.alma.exception.CustomGenericException;
import com.alma.model.ManageUser;
import com.alma.model.Users;

import org.apache.poi.ss.usermodel.Row;
import org.springframework.dao.DuplicateKeyException;

public interface ManageUserDAO {

	public abstract void CreateUser(ManageUser paramManageUser);

	public abstract void editUser(ManageUser paramManageUser);

	public abstract ManageUser getUserDetailsByID(int paramInt) throws NullPointerException;

	public abstract int encryptUserPassword() throws Exception;

	public abstract void addImportedParent(Row paramRow) throws DuplicateKeyException, CustomGenericException;

	public abstract void changePassword(Users u);

}
