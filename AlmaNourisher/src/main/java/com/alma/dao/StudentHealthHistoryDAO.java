package com.alma.dao;

import com.alma.model.StudentHealthHistory;
import java.util.List;

public interface StudentHealthHistoryDAO {
	public List<StudentHealthHistory> getStudentsHealthHistoryByRollIdSchoolId(String paramString,
			int paramInt);
}