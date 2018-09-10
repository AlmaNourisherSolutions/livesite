package com.alma.dao;

import com.alma.exception.CustomGenericException;
import com.alma.model.StudentDetails;
import com.alma.model.StudentFileDetails;
import java.util.List;
import org.apache.poi.ss.usermodel.Row;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.web.multipart.MultipartFile;

public interface StudentDetailDAO {
	public int saveStudent(StudentDetails paramStudentDetails);

	public void saveStudent(StudentDetails paramStudentDetails, MultipartFile paramMultipartFile);

	public void updateStudent(StudentDetails paramStudentDetails);

	public void updateStudent(StudentDetails paramStudentDetails, MultipartFile paramMultipartFile);

	public void createStudentFromImportFile(List<StudentFileDetails> paramList);

	public void addImage(MultipartFile paramMultipartFile, String paramString1, String paramString2);

	public String getProfileImageForStudent(String paramString1, int paramInt, String paramString2);

	public StudentDetails getStudentCurrentHealth(String paramString1, String paramString2);

	public List<StudentDetails> getReportForHealthyStudents(String paramString1, String paramString2,
			String paramString3, int paramInt);

	public List<StudentDetails> getReportForUnderWeightStudents(String paramString1, String paramString2,
			String paramString3, int paramInt);

	public List<StudentDetails> getReportForOverWeightStudents(String paramString1, String paramString2,
			String paramString3, int paramInt);

	public List<StudentDetails> getReportTotalCountForBelowReferenceRanage(String paramString1,
			String paramString2, String paramString3, int paramInt, String paramString4, String paramString5);

	public List<StudentDetails> getReportTotalCountForAboveReferenceRanage(String paramString1,
			String paramString2, String paramString3, int paramInt, String paramString4, String paramString5);

	public List<StudentDetails> getReportTotalCountForhealthy(String paramString1, String paramString2,
			String paramString3, int paramInt, String paramString4, String paramString5);

//	public void createTemporaryTableOfDates(List<String> paramList, int paramInt);

//	public void deleteTemporaryTableOfDates(int paramInt);

	public void addImportedStudent(Row paramRow, boolean paramBoolean)
			throws DuplicateKeyException, CustomGenericException;

	public List<StudentDetails> getStudentsIDBySchool_Branch_Class_Gender(String paramString1, int paramInt,
			String paramString2, String paramString3);

	public List<String> getMonthWiseHealthCategoryForStudent(String paramString1, String paramString2);
}
