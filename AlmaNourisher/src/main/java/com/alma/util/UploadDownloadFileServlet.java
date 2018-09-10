package com.alma.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;

import com.alma.dao.ReportDAO;
import com.alma.dao.StudentDAO;
import com.alma.model.Food;
import com.alma.model.QFoodGrouping;
import com.alma.model.Questionaire;
import com.alma.model.Student;
import com.csvreader.CsvReader;

@Path("/UploadDownloadFileService")
@CrossOrigin
public class UploadDownloadFileServlet {
	
	String filepath = "D:/rugma/Alma/almaangular/app/assets/images/";
	//String filepath = "/home/almablr/public_html/assets/images/";
	static Logger log = Logger.getLogger(UploadDownloadFileServlet.class);
	
	@Autowired
	private StudentDAO studentDAO;
	
	@Autowired
	private ReportDAO reportDAO;
	
    @SuppressWarnings("resource")
	@POST  
    @Path("/upload")  
    @Consumes(MediaType.MULTIPART_FORM_DATA)  
    public Response uploadFile(  
            @FormDataParam("file") InputStream uploadedInputStream,  
            @FormDataParam("file") FormDataContentDisposition fileDetail) {  
            String fileLocation = filepath+"school/" + fileDetail.getFileName();
                    //saving file  
            try {  
            	File file = new File(fileLocation); 
            	file.createNewFile();
            	file.setReadable(true, false);
            	file.setWritable(true, false);
            	file.setExecutable(true, true);
       	      
                FileOutputStream out = new FileOutputStream(file);  
                int read = 0;  
                byte[] bytes = new byte[1024];   
                while ((read = uploadedInputStream.read(bytes)) != -1) {  
                    out.write(bytes, 0, read);  
                }  
                out.flush();  
                out.close();  
            } catch (IOException e) {e.printStackTrace();}  
            String output = "File successfully uploaded to : " + fileLocation;  
            return Response.status(200).entity(output).build();  
        }  
    
    @SuppressWarnings("resource")
	@POST  
    @Path("/uploadProfilePic")  
    @Consumes(MediaType.MULTIPART_FORM_DATA)  
    public Response uploadProfilePic(  
            @FormDataParam("file") InputStream uploadedInputStream,  
            @FormDataParam("file") FormDataContentDisposition fileDetail) {  
            String fileLocation = filepath+"student/" + fileDetail.getFileName(); 
                    //saving file  
            try {  
            	File file = new File(fileLocation); 
            	file.createNewFile();
            	file.setReadable(true, false);
            	file.setWritable(true, false);
            	file.setExecutable(true, true);

                FileOutputStream out = new FileOutputStream(file);   
                int read = 0;  
                byte[] bytes = new byte[1024];  
                while ((read = uploadedInputStream.read(bytes)) != -1) {  
                    out.write(bytes, 0, read);  
                }  
                out.flush();  
                out.close();  
            } catch (IOException e) {e.printStackTrace();}  
            String output = "File successfully uploaded to : " + fileLocation;  
            return Response.status(200).entity(output).build();  
        }  
    
    @SuppressWarnings("resource")
	@POST  
    @Path("/uploadArticleIcon")  
    @Consumes(MediaType.MULTIPART_FORM_DATA)  
    public Response uploadArticleIcon(  
            @FormDataParam("file") InputStream uploadedInputStream,  
            @FormDataParam("file") FormDataContentDisposition fileDetail) {  
            String fileLocation = filepath+"document/" + fileDetail.getFileName(); 
                    //saving file  
            try {  
            	File file = new File(fileLocation); 
            	file.createNewFile();
            	file.setReadable(true, false);
            	file.setWritable(true, false);
            	file.setExecutable(true, true);

                FileOutputStream out = new FileOutputStream(file);  
                int read = 0;  
                byte[] bytes = new byte[1024];  
                while ((read = uploadedInputStream.read(bytes)) != -1) {  
                    out.write(bytes, 0, read);  
                }  
                out.flush();  
                out.close();  
            } catch (IOException e) {e.printStackTrace();}  
            String output = "File successfully uploaded to : " + fileLocation;  
            return Response.status(200).entity(output).build();  
        }  
    
    @SuppressWarnings("static-access")
	@POST  
    @Path("/uploadStudentData")  
    @Consumes(MediaType.MULTIPART_FORM_DATA)  
    public Response uploadStudentData(  
            @FormDataParam("file") InputStream uploadedInputStream,  
            @FormDataParam("file") FormDataContentDisposition fileDetail) {  
    	
    		int procRecCounter = 0;
    		List<String> lines = new ArrayList<String>();
            try {  
            	CsvReader reader = new CsvReader(new InputStreamReader(uploadedInputStream)); 

            	reader.readHeaders();
            	
    			boolean isAnyMore = reader.readRecord();
    			lines.add("New Studen Data Load begins: "+new Date().toString());
    			
    			while (isAnyMore) {
    				
    				try{
    				System.out.println(procRecCounter);
    				procRecCounter += 1;

    				lines.add("----------------------- Record: "+procRecCounter+" ----------------------------");
    				lines.add("Current record values ---> " + reader.getRawRecord());
    				lines.add("---------------------------------------------------");
    				
    				SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
    				
    				String[] values = reader.getValues();
    				Student studentDetails = new Student();
    				if(values[0].trim().equals("")){
    					lines.add("-- student roll id is mandatory --");
    					isAnyMore = reader.readRecord();
    					continue;
    				}
    				studentDetails.setStudentRollId(values[0]);
    				if(values[1].trim().equals("")){
    					lines.add("-- First Name is mandatory --");
    					isAnyMore = reader.readRecord();
    					continue;
    				}
    				studentDetails.setFirstName(values[1]);
    				studentDetails.setLastName(values[2]);
    				if(values[3].trim().equals("") && values[4].trim().equals("")){
    					lines.add("-- parent email id / contact number is mandatory --");
    					isAnyMore = reader.readRecord();
    					continue;
    				}
    				studentDetails.getParent().setEmail(values[3]);
    				studentDetails.getParent().setContact(values[4]);
    				studentDetails.getParent().setPassword("alma@123");
    				String gender = values[5];
    				if("M".equalsIgnoreCase(gender) || "Male".equalsIgnoreCase(gender)){
    					studentDetails.setGender("Male");
    					studentDetails.setProfileImage("defaultb.png");
    				}
    				else if("F".equalsIgnoreCase(gender) || "Female".equalsIgnoreCase(gender)){
    					studentDetails.setGender("Female");
    					studentDetails.setProfileImage("defaultg.png");
    				}
    				else{
    					lines.add("-- invalid gender --");
    					isAnyMore = reader.readRecord();
    					continue;
    				}
    				String schoolName = values[6];
    				String branchName = values[7];
    				
    				int[] ids = studentDAO.getSchoolBranchId(schoolName, branchName);
    				studentDetails.setSchoolName(schoolName);
    				if(ids[0]>0)
    					studentDetails.setSchoolId(ids[0]);
    				else{
    					lines.add("-- invalid school / branch --");
    					isAnyMore = reader.readRecord();
    					continue;
    				}
    				studentDetails.setBranchName(branchName);
    				if(ids[1]>0)
    					studentDetails.setBranchId(ids[1]);
    				else{
    					lines.add("-- invalid school / branch --");
    					isAnyMore = reader.readRecord();
    					continue;
    				}
    				studentDetails.setClassName(values[8]);
    				studentDetails.setSectionName(values[9]);
    				Date dob = formatter.parse(values[10]);
    				studentDetails.setDateOfBirth(dob);
    			    
    			    studentDetails.getStudentsHealth().setAge(AgeBMICalculator.calculateAgeInDecimal(dob));
    			    
    			    boolean heightSet = false;
    			    boolean weightSet = false;
    				if(!"".equals(values[11].trim())){
    					studentDetails.getStudentsHealth().setHeight(Double.parseDouble(values[11]));
    					studentDetails.getStudentsHealth().setHeightUnit(values[12]);
    					heightSet = true;
    				}
    				if(!"".equals(values[13].trim())){
    					studentDetails.getStudentsHealth().setWeight(Double.parseDouble(values[13]));
    					studentDetails.getStudentsHealth().setWeightUnit(values[14]);
    					weightSet = true;
    				}
    				if(weightSet && heightSet){
    					AgeBMICalculator bmiCalc = new AgeBMICalculator();
    					Double height = studentDetails.getStudentsHealth().getHeight();
    					String heightUnit = studentDetails.getStudentsHealth().getHeightUnit();
    					Double weight = studentDetails.getStudentsHealth().getWeight();
    					String weightUnit = studentDetails.getStudentsHealth().getWeightUnit();
    					if("Centimeter".equalsIgnoreCase(heightUnit) || "cm".equalsIgnoreCase(heightUnit)){
				        	height = height * 0.01;
				        }
				        else if("Inches".equalsIgnoreCase(heightUnit)){
				        	height = height * 0.0254;
				        }
				        else if("Feet".equalsIgnoreCase(heightUnit)){
				        	height = height * 0.3048;
				        }
				        else{
				        	lines.add("-- invalid height unit --");
	    					isAnyMore = reader.readRecord();
	    					continue;
				        }
				        if("LBS".equalsIgnoreCase(weightUnit)){
				        	weight = weight/ 2.2;
				        }
				        else if("Kilograms".equalsIgnoreCase(weightUnit)||"kgs".equalsIgnoreCase(weightUnit)){
				        	
				        }
				        else{
				        	lines.add("-- invalid weight unit --");
	    					isAnyMore = reader.readRecord();
	    					continue;
				        }
				        
				        Integer ageInMonths = AgeBMICalculator.getAgeInMonths(dob);
				        if(ageInMonths > 200){
				        	lines.add("-- invalid date of birth "+dob+"--");
	    					isAnyMore = reader.readRecord();
	    					continue;
				        }
				        
				        Double bmi = bmiCalc.getBMIKg(height, weight);
				        String ibw = bmiCalc.getIBW(studentDetails.getGender(), dob);
				        

						double bmiPercentile = bmiCalc.getBMIPercentile(ageInMonths.doubleValue(), bmi, studentDetails.getGender());
						
						studentDetails.getStudentsHealth().setBmi(bmi);
						studentDetails.getStudentsHealth().setIbw(ibw);
						studentDetails.getStudentsHealth().setBmiPercentile(bmiPercentile);
    				}
    				studentDetails.setActive(true);

    				int studentId = studentDAO.getStudentIdFromRollId(studentDetails);
    				if(studentId<=0){
    					lines.add("-------  Student Record not found. Creating new Student record--------");
        				studentDAO.createStudent(studentDetails);
        				lines.add("*** Student created successfully ***");
    				}
    				else{
    					lines.add("-------  Student Record exist. Updating height and weight --------");
    					studentDetails.setStudentId(studentId);
    					
    					if(weightSet || heightSet){
    						studentDAO.updateStudentHealthRecord(studentDetails);
    					}
    					lines.add("*** Student details updated successfully ***");
    				}
    				}
    				catch(ParseException e){
    	            	e.printStackTrace();
    	            	this.log.error(e.getMessage());
    	            	lines.add("Failed to create / update student");
    	            	lines.add(e.getMessage());
    	            }
    				catch(Exception e){
    	            	e.printStackTrace();
    	            	this.log.error(e.getMessage());
    	            	lines.add("Failed to create / update student");
    	            	lines.add(e.getMessage());
    	            }

    				isAnyMore = reader.readRecord();
    			}
            } catch (IOException e) {
            	e.printStackTrace();
            	this.log.error(e.getMessage());
            	lines.add("Failed to create / update student");
            	lines.add(e.getMessage());
            }
            catch(NumberFormatException e){
            	e.printStackTrace();
            	this.log.error(e.getMessage());
            	lines.add("Failed to create / update student");
            	lines.add(e.getMessage());
            }
            catch(Exception e){
            	e.printStackTrace();
            	this.log.error(e.getMessage());
            	lines.add("Failed to create / update student");
            	lines.add(e.getMessage());
            }
            java.nio.file.Path file = Paths.get(filepath+"uploadLogs/StudentDataUploadLog.txt");
            try {
				Files.write(file, lines, Charset.forName("UTF-8"));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				this.log.error(e.getMessage());
			}
            String output = "File successfully processed: ";  
            return Response.status(200).entity(output).build();  
        }  
    
    @SuppressWarnings("static-access")
    @POST  
    @Path("/uploadQuestionaireData")  
    @Consumes(MediaType.MULTIPART_FORM_DATA)  
    public Response uploadQuestionaireData(  
            @FormDataParam("file") InputStream uploadedInputStream,  
            @FormDataParam("file") FormDataContentDisposition fileDetail) {  
    	
    		int procRecCounter = 0;
    		List<String> lines = new ArrayList<String>();
            try {  
            	CsvReader reader = new CsvReader(new InputStreamReader(uploadedInputStream)); 

            	reader.readHeaders();
            	reader.readHeaders();
            	reader.readHeaders();
            	
    			boolean isAnyMore = reader.readRecord();
    			
				lines.add("New Studen Data Load begins: "+new Date().toString());
    			
    			while (isAnyMore) {
    				
    				System.out.println(procRecCounter);
    				procRecCounter += 1;

    				lines.add("----------------------- Record: "+procRecCounter+" ----------------------------");
    				lines.add("Current record values ---> " + reader.getRawRecord());
    				lines.add("---------------------------------------------------");
    				
    				String[] values = reader.getValues();
    				Student studentDetails = new Student();
    				studentDetails.setStudentRollId(values[0]);
    				studentDetails.setFirstName(values[1]);
    				String gender = values[2];
    				if("M".equalsIgnoreCase(gender)){
    					studentDetails.setGender("Male");
    					studentDetails.setProfileImage("defaultb.png");
    				}
    				else if("F".equalsIgnoreCase(gender)){
    					studentDetails.setGender("Female");
    					studentDetails.setProfileImage("defaultg.png");
    				}
    				studentDetails.getParent().setEmail(values[3]);
    				studentDetails.getParent().setContact(values[4]);
    				
    				int studentId = studentDAO.getStudentIdFromRollId(studentDetails);
    				
    				if(studentId<=0){
    					lines.add("-------  Student Record not found. Add Student --------");
    					isAnyMore = reader.readRecord();
    					continue;
    				}
    				else{
    					studentDetails.setStudentId(studentId);
    					studentDetails.setStudentsHealth(studentDAO.getStudentsHealthByStudentId(studentId));
    					if(studentDetails.getStudentsHealth().getCategory()== null || "".equals(studentDetails.getStudentsHealth().getCategory().trim())){
    						lines.add("-------  Student Category missing. Student Height - Weight needs to be set --------");
        					isAnyMore = reader.readRecord();
        					continue;
    					}
    				}
    				
    				String foodPreference = values[5];
    				
    				if(!("Non vegetarian".equalsIgnoreCase(foodPreference) || "Vegetarian".equalsIgnoreCase(foodPreference) || "Eggetarian".equalsIgnoreCase(foodPreference))){
    					lines.add("-------  Invalid Food Preference --------");
    					isAnyMore = reader.readRecord();
    					continue;
    				}
    				
    				List<Questionaire> questions = this.reportDAO.getQuestionaire();
    				
    				int i = 6;
    				int j = i;
    				
    				for(Questionaire q: questions){
    					
    					if("FOOD".equals(q.getQuestionType())){
	    					List<QFoodGrouping> foodGroup = q.getFoodgroups();
	    					
	    					j = i;
	    					for(QFoodGrouping fg: foodGroup){
	    						j += fg.getFoods().size();
	    					}
	    					for(QFoodGrouping fg: foodGroup){
	    						List<Food> food = fg.getFoods();
	    						List<String> servings = fg.getServings();

	    						
	    						for(Food f: food){
	    							String freqans = values[i++].trim();
	    							if("2-3 times a day".equalsIgnoreCase(freqans)){
	    								f.setFreqans("23_times_day");
	    							}
	    							else if("Once a day".equalsIgnoreCase(freqans)){
	    								f.setFreqans("once_day");
	    							}
	    							else if("2-3 times a week".equalsIgnoreCase(freqans)){
	    								f.setFreqans("23_times_week");
	    							}
	    							else if("Once a week".equalsIgnoreCase(freqans) || "Once in 15 days".equalsIgnoreCase(freqans)){
	    								f.setFreqans("once_week");
	    							}else if("Never".equalsIgnoreCase(freqans)){
	    								f.setFreqans("never");
	    							}
	    							else{
	    								lines.add("-------  Food Frequency - Invalid option: Defaulting to never --------");
	    								f.setFreqans("never");
	    							}
	    							
	    							String servans = values[j++].trim();
	    							
	    							if(servings.get(0).equalsIgnoreCase(servans)){
	    								f.setServans("1");
	    							}
	    							else if(servings.get(1).equalsIgnoreCase(servans)){
	    								f.setServans("2");
	    							}
	    							else if(servings.get(2).equalsIgnoreCase(servans)){
	    								f.setServans("3");
	    							}
	    							else if(servings.get(3).equalsIgnoreCase(servans)){
	    								f.setServans("4");
	    							}
	    							else{
	    								lines.add("-------  Food Portions - Invalid option: Defaulting to never --------");
	    								f.setServans("4");
	    							}
	    						}
	    					}
    						i = j;
    					}
    					else if("BOOLEAN".equals(q.getQuestionType())){
    						q.setBoolAns(values[i++].toUpperCase());
    					}
    					else if("PA".equals(q.getQuestionType())){
    						q.setMcqAns(values[i++]);
    					}
    					
    				}

    				this.reportDAO.submitQuestionaire(questions, studentDetails, foodPreference);
    				
    				isAnyMore = reader.readRecord();
    			}
            } catch (IOException e) {
            	e.printStackTrace();
            	this.log.error(e.getMessage());
            	lines.add("Error:"+e.getMessage());
            }
            catch(NumberFormatException e){
            	e.printStackTrace();
            	this.log.error(e.getMessage());
            	lines.add("Error:"+e.getMessage());
            }
            catch(Exception e){
            	e.printStackTrace();
            	this.log.error(e.getMessage());
            	lines.add("Error:"+e.getMessage());
            }
            lines.add("Questioniare upload completed");
            java.nio.file.Path file = Paths.get(filepath+"uploadLogs/QuestionaireUploadLog.txt");
            try {
				Files.write(file, lines, Charset.forName("UTF-8"));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				this.log.error(e.getMessage());
			}
            String output = "File successfully processed: ";  
            return Response.status(200).entity(output).build();  
        }  
 }  