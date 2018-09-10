package com.alma.dao;

import com.alma.model.Student;
import com.alma.model.StudentHealth;

import java.math.BigInteger;
import java.util.List;

public interface StudentDAO {
	public Student getStudentsByRollIdSchoolId(String paramString, int paramInt);

	public List<Student> getRecentAddedStudent(int paramInt1, int paramInt2);

	public List<Student> getStudentsBasedOnSchoolAdmin(int paramInt, String paramString);

	public List<Student> getStudentsBasedOnWildCardSearch(String paramString);

	public int totalStudentsCount(int paramInt);

	public int totalStudentsCount();

	public List<Student> getAllStudents(String paramString);

	public List<Student> getGenderCount(int paramInt1, int paramInt2);
	
	public StudentHealth getStudentsHealthByRollIdSchoolId(String paramString, int paramInt);
	
	public BigInteger createStudent(Student studentDetail);

	public List<Student> getChildBasedOnParentId(String parentId);

	public int[] getSchoolBranchId(String schoolName, String branchName);

	public int getStudentIdFromRollId(Student studentDetails);

	public void updateStudentHealthRecord(Student studentDetails);

	public StudentHealth getStudentsHealthByStudentId(int studentId);

	public List<Student> getStudentsByBranchId(int branchId);

	public int updateStudent(Student studentDetails);

	public void deleteStudent(int studentId);
}
