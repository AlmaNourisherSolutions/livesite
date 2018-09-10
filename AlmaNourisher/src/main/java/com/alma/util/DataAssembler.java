package com.alma.util;

import com.alma.model.Student;
import com.alma.model.StudentDetails;
import com.alma.model.StudentFileDetails;
import com.alma.model.StudentHealth;
import java.text.ParseException;
import java.util.Date;
import java.util.Map;

public class DataAssembler {
	public static StudentDetails getStudentDetails(Student student, StudentHealth studentHealth) {
		StudentDetails studentDetails = new StudentDetails();

		studentDetails.setStudentRollId(student.getStudentRollId());
		studentDetails.setFirstName(student.getFirstName());
		studentDetails.setLastName(student.getLastName());
		studentDetails.setSchoolId(student.getSchoolId() + "");
		studentDetails.setBranchId(student.getBranchId());
		studentDetails.setClassName(student.getClassName());
		studentDetails.setSectionName(student.getSectionName());
		studentDetails.setProfileImage(student.getProfileImage());
		studentDetails.setGender(student.getGender());
		studentDetails.setDOB(student.getDOB());
		studentDetails.setAge(studentHealth.getAge());
		studentDetails.setHeight(studentHealth.getHeight());
		studentDetails.setHeightUnit(studentHealth.getHeightUnit());
		studentDetails.setWeight(studentHealth.getWeight());
		studentDetails.setWeightUnit(studentHealth.getWeightUnit());
		studentDetails.setBmi(studentHealth.getBmi());
		studentDetails.setBmiPercentile(studentHealth.getBmiPercentile());
		studentDetails.setIbw(studentHealth.getIbw());
		studentDetails.setDateOfMeasurement(studentHealth.getDateOfMeasurement());
		studentDetails.setStudentActive(student.isActive());

		return studentDetails;
	}

	public static StudentDetails getFileImportStudentDetails(Map<String, String> mapSchoolBranch,
			StudentFileDetails studentFileDetails) {
		System.out.println("dob value from studentfile details" + studentFileDetails.getDOB());

		StudentDetails studentDetails = new StudentDetails();

		studentDetails.setStudentRollId(studentFileDetails.getStudentRollId());
		studentDetails.setFirstName(studentFileDetails.getFirstName());
		studentDetails.setLastName(studentFileDetails.getLastName());
		studentDetails.setParentEmail(studentFileDetails.getParentEmail());
		studentDetails.setGender(studentFileDetails.getGender());

		String key = studentFileDetails.getSchoolName() + "#" + studentFileDetails.getBranchName();

		String value = (String) mapSchoolBranch.get(key);

		if (value != null) {
			studentDetails.setSchoolId(value.split("#")[0]);
			studentDetails.setBranchId(Integer.parseInt(value.split("#")[1]));
		}

		studentDetails.setClassName(studentFileDetails.getClassName());
		studentDetails.setSectionName(studentFileDetails.getSectionName());
		studentDetails.setDOB(studentFileDetails.getDOB());

		double height = 0.0D;
		double weight = 0.0D;

		if ((studentFileDetails.getHeight() != "") && (studentFileDetails.getWeight() != "")) {
			height = Double.parseDouble(studentFileDetails.getHeight());
			weight = Double.parseDouble(studentFileDetails.getWeight());
			studentDetails.setHeight(height);
			studentDetails.setWeight(weight);
		}

		studentDetails.setHeightUnit(studentFileDetails.getHeightUnit());
		studentDetails.setWeightUnit(studentFileDetails.getWeightUnit());

		AgeBMICalculator bmiCalculatror = new AgeBMICalculator();

		double bmi = 0.0D;
		int age = 0;
		String ibw = "";
		try {
			Date dateOfBirth = studentDetails.getDOB();

			System.out.println("DOB from student details" + dateOfBirth);

			age = AgeBMICalculator.calculateAge(dateOfBirth);

			studentDetails.setAge(age);

			if ((studentFileDetails.getHeight() != "") && (studentFileDetails.getWeight() != "")) {
				bmi = bmiCalculatror.getBMIKg(height, weight);

				System.out.println("bmi value from data assembler-------------->>>>>>>>>" + bmi);

				ibw = bmiCalculatror.getIBW(studentDetails.getGender(), dateOfBirth);

				System.out.println("ibw value from data assembler<<<<<<<<<<<<<<<<<<<<<<<" + ibw);

				double calculatedBMIval = bmiCalculatror.getBMIKg(height, weight);

				System.out.println("bmival" + calculatedBMIval);

				int ageInMonths = AgeBMICalculator.getAgeInMonths(dateOfBirth);

				double genderIndex = bmiCalculatror.getGenderIndex(studentDetails.getGender());

				double calculatepercentageval = bmiCalculatror.getPercentage(calculatedBMIval, ageInMonths, genderIndex,
						BMIForAge.DATA);

				System.out.println("bmipercentilevalue" + calculatepercentageval);

				studentDetails.setBmiPercentile(calculatepercentageval);

				studentDetails.setBmi(bmi);
				studentDetails.setIbw(ibw);
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}

		studentDetails.setProfileImage("resources/images/unisex.png");

		return studentDetails;
	}
}