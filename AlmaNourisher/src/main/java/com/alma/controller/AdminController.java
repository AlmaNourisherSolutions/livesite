package com.alma.controller;

import com.alma.dao.BranchDAO;
import com.alma.dao.DocumentsDAO;
import com.alma.dao.ManageSchoolDAO;
import com.alma.dao.ManageUserDAO;
import com.alma.dao.ReportDAO;
import com.alma.dao.StudentDAO;
import com.alma.dao.StudentDetailDAO;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySources;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.core.env.Environment;
import org.apache.log4j.Logger;

import com.alma.model.Article;
import com.alma.model.ArticleComment;
import com.alma.model.Branch;
import com.alma.model.FoodFreqReport;
import com.alma.model.ManageSchool;
import com.alma.model.QStatistics;
import com.alma.model.Recipe;
import com.alma.model.RecipeComment;
import com.alma.model.Student;
import com.alma.model.StudentHealth;
import com.alma.model.StudentHealthHistory;
import com.alma.model.UserCredentials;
import com.alma.model.Users;
import com.alma.model.WellnessLookupAssociation;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.html.HtmlTags;
import com.itextpdf.text.html.simpleparser.HTMLTagProcessor;
import com.itextpdf.text.html.simpleparser.HTMLTagProcessors;
import com.itextpdf.text.html.simpleparser.HTMLWorker;
import com.itextpdf.text.html.simpleparser.StyleSheet;
import com.itextpdf.text.pdf.CMYKColor;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfGState;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import com.itextpdf.text.pdf.PdfWriter;
import com.alma.dao.UsersDAO;
import com.alma.dao.WellnessLookupAssociationDAO;

@Path("/admin")
@PropertySources({ @org.springframework.context.annotation.PropertySource({ "classpath:dbconfig.properties" }) })
@CrossOrigin
@Controller
//@RequestMapping(value = { "/admin" })
public class AdminController {
	
	static Logger log = Logger.getLogger(AdminController.class.getName());
	
	String filepath = "D:/rugma/Alma/almaangular/app/assets/";
	//String filepath = "/home/almablr/public_html/assets/";

	@Context 
	private HttpServletRequest request;
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@Autowired
	private UsersDAO userDAO;
	
	@Autowired
	private ManageSchoolDAO manageSchoolDAO;
	
	@Autowired
	private BranchDAO branchDAO;
	
	@Autowired
	private StudentDAO studentDAO;

	@Autowired
	private WellnessLookupAssociationDAO wellnessLookupAssociationDAO;
	
	@Autowired
	private ReportDAO reportDAO;
	
	
	@Autowired
	private DocumentsDAO documentsDAO;
	
	@Autowired
	private Environment env;

/*	@ResponseBody
	@RequestMapping(value = { "/validateLogin" }, method = {
			org.springframework.web.bind.annotation.RequestMethod.POST })*/
	
	@PostConstruct
	public void init() {
		System.setProperty("db.connection.url", this.env.getProperty("db.connection.url"));

		System.setProperty("db.connection.username", this.env.getProperty("db.connection.username"));

		System.setProperty("db.connection.password", this.env.getProperty("db.connection.password"));
	}
	
	@POST
	@Path("/validateLogin")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Users validateUserLogin(UserCredentials userCredentials) {
		Users loginUser = null;
		String isValidUser = "true";
		HttpSession session = request.getSession();
		loginUser = this.userDAO.getUser(userCredentials.getUsername());

		if (loginUser != null) {
			if (!(this.passwordEncoder.matches(userCredentials.getPassword(), loginUser.getPassword())))
				isValidUser = "false";
		} else {
			isValidUser = "userNotExists";
		}

		if (isValidUser.equals("true")) {
			
			String association = this.userDAO.userAssociatedVerification(userCredentials.getRole(), userCredentials.getSchoolUrl(), loginUser);
			
			if(!"".equals(association)){
				Users u = this.userDAO.getUserRoleByID(loginUser.getUserId());
				loginUser.setRoleName(u.getRoleName());
			}
			if(("Admin".equals(userCredentials.getRole()) && "AlmaAdmin".equals(association)) || (("Parent".equals(userCredentials.getRole()) || "SchoolAdmin".equals(userCredentials.getRole())) && !"".equals(association))){
				session = request.getSession(false);
				if ((session != null) && (!(session.isNew()))) {
					session.invalidate();
				}
				session = request.getSession();
				session.setAttribute("userEmail", loginUser.getEmail());
				session.setAttribute("userId", Integer.valueOf(loginUser.getUserId()));
				session.setAttribute("userName", new StringBuilder().append(loginUser.getFirstName()).append(" ")
						.append(loginUser.getLastName()).toString());
				session.setAttribute("role", loginUser.getRoleName());
				session.setAttribute("schoolName", association);
				loginUser.setSchoolUrl(association);
			}
			else{
				loginUser = null;
			}
		}

		return loginUser;
	}
	
	@POST
	@Path("/invalidateSession/{userId}")
	public String invalidateSession(@PathParam("userId") String userId) {
		HttpSession session = request.getSession(false);
		if ((session != null) && (!(session.isNew()))) {
			session.invalidate();
		}
		return "success";
	}
	
	@GET
	@Path("/getUser")
	@Produces(MediaType.APPLICATION_JSON)
	public Users getUser(){
		return this.userDAO.getUser("alma@gmail.com");
	}
	
	/**
	 * 
	 * Manage School
	 * 
	 */
	
	@POST
	@Path("/addSchool")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Number addSchool(ManageSchool manageSchool) {
		Number schoolid = 0;
		try {
			schoolid = this.manageSchoolDAO.saveOrUpdate(manageSchool, null);

			System.out.println(new StringBuilder().append("SchoolId ((((((((( ").append(schoolid).toString());
		} catch (Exception e) {
			log.error(e);
			e.printStackTrace();
		}
		return schoolid;
	}
	
	@POST
	@Path("/editSchool")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Number editSchool(ManageSchool manageSchool) {
		Number schoolid = 0;
		try {
			schoolid = this.manageSchoolDAO.saveOrUpdate(manageSchool, null);

			System.out.println(new StringBuilder().append("SchoolId ((((((((( ").append(schoolid).toString());
		} catch (Exception e) {
			log.error(e);
			e.printStackTrace();
		}
		return schoolid;
	}
	
	@POST
	@Path("/deleteSchool/{schoolId}")
	@Consumes(MediaType.APPLICATION_JSON)
	public void deleteSchool(@PathParam("schoolId") int schoolId) {
		
		this.manageSchoolDAO.deleteSchool(schoolId);
	}
	
	@POST
	@Path("/addSchoolBranch")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Number addSchoolBranch(Branch branch) {
		Number branchid = 0;
		try {
			branchid = this.branchDAO.saveOrUpdate(branch);

			System.out.println(new StringBuilder().append("BranchId ((((((((( ").append(branchid).toString());
		} catch (Exception e) {
			log.error(e);
			e.printStackTrace();
		}
		return branch.getSchoolId();
	}
	
	@POST
	@Path("/editSchoolBranch")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Number editSchoolBranch(Branch branch) {
		Number branchid = 0;
		try {
			branchid = this.branchDAO.saveOrUpdate(branch);

			System.out.println(new StringBuilder().append("BranchId ((((((((( ").append(branchid).toString());
		} catch (Exception e) {
			log.error(e);
			e.printStackTrace();
		}
		return branch.getSchoolId();
	}
	
	@POST
	@Path("/deleteSchoolBranch/{branchId}")
	@Consumes(MediaType.APPLICATION_JSON)
	public void deleteSchoolBranch(@PathParam("branchId") int branchId) {
		
		this.branchDAO.deleteSchoolBranch(branchId);
	}
	
	@GET
	@Path("/getSchoolList")
	@Produces(MediaType.APPLICATION_JSON)
	public List<ManageSchool> getAllSchool() {
		List<ManageSchool> schools = this.manageSchoolDAO.getSchoolDetails();

		return schools;
	}
	
	@POST
	@Path("/getSchoolById")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public ManageSchool getSchoolById(ManageSchool manageSchool) {
		Number schoolid = manageSchool.getSchoolId();
		try {
			manageSchool = this.manageSchoolDAO.getSchoolById(String.valueOf(schoolid));

			System.out.println(new StringBuilder().append("SchoolId ((((((((( ").append(schoolid).toString());
		} catch (Exception e) {
			log.error(e);
			e.printStackTrace();
		}
		return manageSchool;
	}
	
	@POST
	@Path("/getSchoolName/{schoolName}")
	public String getSchoolName(@PathParam("schoolName") String schoolName) {
		return this.manageSchoolDAO.getSchoolName(schoolName);
	}
	
	@GET
	@Path("/getSchoolBranchList/{schoolId}")
	@Produces(MediaType.APPLICATION_JSON)
	public List<Branch> getAllSchool(@PathParam("schoolId") String schoolId) {
		List<Branch> branches = this.branchDAO.getBranchBasedOnSchoolId(schoolId);
		for(Branch b: branches){
			b.setClasses(this.branchDAO.getClassBasedOnBranchId(b.getBranchId()+""));
		}

		return branches;
	}
	
	@GET
	@Path("/getClassList/{branchId}")
	@Produces(MediaType.APPLICATION_JSON)
	public List<String> getClassList(@PathParam("branchId") String branchId) {
		List<String> classList = this.branchDAO.getClassBasedOnBranchId(branchId);

		return classList;
	}
	
	@GET
	@Path("/getClassLookup")
	@Produces(MediaType.APPLICATION_JSON)
	public List<String> getClassLookup() {
		List<String> classLookup = this.branchDAO.getClassLookup();

		return classLookup;
	}
	
	/*
	 * 
	 * Manage Student
	 * 
	 */
	
	@POST
	@Path("/addStudent")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Number addStudent(Student studentDetail) {
		BigInteger studentId = null;
		
		studentId = this.studentDAO.createStudent(studentDetail);
		
		return studentId;
	}
	
	@POST
	@Path("/editStudent")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Number editStudent(Student studentDetail) {
		int studentId;
		
		studentId = this.studentDAO.updateStudent(studentDetail);
		
		return studentId;
	}
	
	@POST
	@Path("/deleteStudent/{studentId}")
	@Consumes(MediaType.APPLICATION_JSON)
	public void deleteStudent(@PathParam("studentId") int studentId) {
		
		this.studentDAO.deleteStudent(studentId);
	}
	
	@GET
	@Path("/getStudentList")
	@Produces(MediaType.APPLICATION_JSON)
	public List<Student> getAllStudentList() {
		List<Student> schoolAdminStudents = null;
			try {
				
					schoolAdminStudents = this.studentDAO.getAllStudents("30");

					if (schoolAdminStudents != null) {

						for (Student schoolAdminStudent : schoolAdminStudents) {
							String age = schoolAdminStudent.getGender();
							StudentHealth studentHealth = this.studentDAO.getStudentsHealthByRollIdSchoolId(
									schoolAdminStudent.getStudentRollId(), schoolAdminStudent.getSchoolId());

							if (studentHealth != null) {
								schoolAdminStudent.setStudentsHealth(studentHealth);

								WellnessLookupAssociation wla = this.wellnessLookupAssociationDAO.getWellnessReportbyCategory(schoolAdminStudent.getGender(), studentHealth.getAge(), studentHealth.getCategory());
								if(wla.getCategory()!=null)
									wla = this.wellnessLookupAssociationDAO.getWellnessRecipe(wla);
								schoolAdminStudent.setStudentsWellness(wla);
							
							}

						}
					}

			} catch (Exception e) {
				log.error(e);
				e.printStackTrace();
			}
		return schoolAdminStudents;
	}
	
	@GET
	@Path("/createQuestionaireTemplate")
	@Produces(MediaType.APPLICATION_JSON)
	public String createQuestionaireTemplate() {
		reportDAO.createQuestionaireTemplate();
		return "Success";
	}
	
	@GET
	@Path("/questionaireStatistics")
	@Produces(MediaType.APPLICATION_JSON)
	public List<QStatistics> questionaireStatistics(){
		return reportDAO.questionaireStatistics();
	}
	
	@GET
	@Path("/questionaireStatisticsTotalCount")
	@Produces(MediaType.APPLICATION_JSON)
	public int questionaireStatisticsTotalCount(){
		List<QStatistics> statsList = reportDAO.questionaireStatistics();
		int count = 0;
		for(QStatistics q: statsList){
			count += q.getCount();
		}
		return count;
	}
	
	@GET
	@Path("/encryptPassword")
	public String encryptPassword(){
		userDAO.encryptPassword();
		return "done";
	}
	
	@POST
	@Path("/addArticle")
	@Consumes(MediaType.APPLICATION_JSON)
	public String addArticle(Article article) {
		BigInteger articleId = null;
		
		articleId = this.documentsDAO.createArticle(article);
		
		return article.getSeoheading();
	}
	
	@POST
	@Path("/addRecipe")
	@Consumes(MediaType.APPLICATION_JSON)
	public String addRecipe(Recipe recipe) {
		BigInteger recipeId = null;
		
		recipeId = this.documentsDAO.createRecipe(recipe);
		
		return recipe.getSeoheading();
	}
	
	@POST
	@Path("/getArticleByName/{heading}")
	@Produces(MediaType.APPLICATION_JSON)
	public Article getArticleByName(@PathParam("heading") String heading) {
		Article article = new Article();
		try {
			article = this.documentsDAO.getArticleByName(heading);

			System.out.println(new StringBuilder().append("Article ((((((((( ").append(heading));
		} catch (Exception e) {
			log.error(e);
			e.printStackTrace();
		}
		return article;
	}
	
	@POST
	@Path("/getRecipeByName/{heading}")
	@Produces(MediaType.APPLICATION_JSON)
	public Recipe getRecipeByName(@PathParam("heading") String heading) {
		Recipe recipe = new Recipe();
		try {
			recipe = this.documentsDAO.getRecipeByName(heading);

			System.out.println(new StringBuilder().append("Recipe ((((((((( ").append(heading));
		} catch (Exception e) {
			log.error(e);
			e.printStackTrace();
		}
		return recipe;
	}
	
	@GET
	@Path("/getArticleList/{tag}")
	@Produces(MediaType.APPLICATION_JSON)
	public Map<String,List<Article>> getArticleList(@PathParam("tag") String tag) {
		return this.documentsDAO.getArticleList("ADMIN", null, tag);
	}
	
	@GET
	@Path("/getRecipeList/{tag}")
	@Produces(MediaType.APPLICATION_JSON)
	public Map<String,List<Recipe>> getRecipeList(@PathParam("tag") String tag) {
		return this.documentsDAO.getRecipeList("ADMIN",null, tag);
	}
	
	@GET
	@Path("/getRecentArticleList")
	@Produces(MediaType.APPLICATION_JSON)
	public List<Article> getRecentArticleList() {
		return this.documentsDAO.getRecentArticles("ADMIN",null);
	}
	
	@GET
	@Path("/getRecentRecipeList")
	@Produces(MediaType.APPLICATION_JSON)
	public List<Recipe> getRecentRecipeList() {
		return this.documentsDAO.getRecentRecipes("ADMIN",null);
	}
	
	@GET
	@Path("/getCommonTags")
	@Produces(MediaType.APPLICATION_JSON)
	public List<String> getCommonTags() {
		return this.documentsDAO.getCommonTags();
	}
	
	@POST
	@Path("/addCommentToArticle")
	@Consumes(MediaType.APPLICATION_JSON)
	public void addCommentToArticle(ArticleComment acomment){
		this.documentsDAO.addCommenttoArticle(acomment);
	}
	
	@POST
	@Path("/addCommentToRecipe")
	public void addCommentToRecipe(RecipeComment rcomment){
		this.documentsDAO.addCommenttoRecipe(rcomment);
	}
	
	@GET
	@Path("/getArticleComments/{articleId}")
	@Produces(MediaType.APPLICATION_JSON)
	public List<ArticleComment> getArticleComments(@PathParam("articleId") int articleId){
		return this.documentsDAO.getArticleComment(articleId);
	}
	
	@GET
	@Path("/getRecipeComments/{recipeId}")
	@Produces(MediaType.APPLICATION_JSON)
	public List<RecipeComment> getRecipeComments(@PathParam("recipeId") int recipeId){
		return this.documentsDAO.getRecipeComment(recipeId);
	}
	
	@POST
	@Path("/publishArticle/{articleId}")
	public String publishArticle(@PathParam("articleId") int articleId) {
	     this.documentsDAO.publishArticle(articleId);

		return "success";
	}
	
	@POST
	@Path("/publishRecipe/{recipeId}")
	public String publishRecipe(@PathParam("recipeId") int recipeId) {
	     this.documentsDAO.publishRecipe(recipeId);

		return "success";
	}
	
	@GET
	@Path("/getSchoolsCount")
	public int getSchoolsCount(){
		return this.manageSchoolDAO.totalSchoolsCount();
	}
	
	@GET
	@Path("/getStudentsCount")
	public int getStudentsCount(){
		return this.studentDAO.totalStudentsCount();
	}
	
	@GET
	@Path("/getArticlesCount")
	public int getArticlesCount(){
		return this.documentsDAO.totalArticlesCount();
	}
	
	@GET
	@Path("/getRecipesCount")
	public int getRecipesCount(){
		return this.documentsDAO.totalRecipesCount();
	}
	
	@GET
	@Path("/bulkNutritionReport/{branchId}")
	public String bulkNutritionReport(@PathParam("branchId") int branchId){
		List<Student> students = this.studentDAO.getStudentsByBranchId(branchId);
		if (students != null) {
			String filename = filepath+"reports/BulkNutritionReport_"+branchId+".pdf";
			Document document = new Document(PageSize.A4, 50, 50, 50, 50);
			PdfWriter writer = null;
			try {
				File file = new File(filename); 
				file.createNewFile();
            	file.setReadable(true, false);
            	file.setWritable(true, false);
            	file.setExecutable(true, true);
				FileOutputStream fileOutputStream= new FileOutputStream(file);
				writer = PdfWriter.getInstance(document, fileOutputStream);
			
				document.open();
				
			    Font f = FontFactory.getFont(FontFactory.HELVETICA, 12, Font.BOLD, new CMYKColor(255,255,255,255));
			    Font f1 = FontFactory.getFont(FontFactory.HELVETICA, 10, Font.NORMAL,new CMYKColor(255,255,255,255));
			    Font f2 = FontFactory.getFont(FontFactory.HELVETICA, 10, Font.NORMAL, new CMYKColor(0,0,0,209));
			    Font f3 = FontFactory.getFont(FontFactory.HELVETICA, 12, Font.BOLD, new CMYKColor(255,255,255,255));
			
				for (Student student : students) {
					StudentHealth studentHealth = this.studentDAO.getStudentsHealthByRollIdSchoolId(student.getStudentRollId(), student.getSchoolId());
	
					if (studentHealth != null) {
						double age = studentHealth.getAge();
						 String[] arr=String.valueOf(age).split("\\.");
						 studentHealth.setAgeYears(Integer.parseInt(arr[0]));
						 studentHealth.setAgeMonths(Integer.parseInt(arr[1])); // 9
						 student.setStudentsHealth(studentHealth);
	
						WellnessLookupAssociation wla = this.reportDAO.getNutritionReport(student.getStudentId());
						student.setStudentsWellness(wla);
						
					}
					
					Paragraph paragraph1 = new Paragraph();
					Image image = Image.getInstance(filepath+"images/school/"+student.getSchoolLogo());
				    image.scaleToFit(75, 75);
				    document.add( Chunk.NEWLINE );
				    paragraph1.add(new Chunk(image, 0, 0));
				    Paragraph c = new Paragraph("Nutrition Report", FontFactory.getFont(FontFactory.HELVETICA, 22, Font.BOLD, new CMYKColor(171, 79, 0,53)));		    
				    c.setAlignment(Element.ALIGN_CENTER);
				    paragraph1.add(c);
				    paragraph1.setSpacingAfter(25);
				    paragraph1.setSpacingBefore(10);
				    document.add(paragraph1);
				    
					PdfPTable t = new PdfPTable(3);t.setSpacingAfter(30);t.setWidthPercentage(100);
				    PdfPCell c1 = new PdfPCell(new Phrase("Personal Details: ",f));c1.setPadding(10);c1.setBorderColor(new CMYKColor(0,0,0,50));
				    t.addCell(c1);
				    PdfPCell c2 = new PdfPCell(new Phrase("School Details: ",f));c2.setPadding(10);c2.setBorderColor(new CMYKColor(0,0,0,50));
				    t.addCell(c2);
				    PdfPCell c3 = new PdfPCell();c3.setPaddingTop(5);c3.setPaddingBottom(7);c3.setPaddingLeft(10);c3.setPaddingRight(10);c3.setBorderColor(new CMYKColor(0,0,0,50));
				    Phrase phrase = new Phrase("Health Status: ",f);
				    
				    Font f5 = FontFactory.getFont(FontFactory.HELVETICA, 12, Font.BOLD, new CMYKColor(255,255,255,255));
				    if("Healthy".equalsIgnoreCase(student.getStudentsHealth().getCategory())){
				    	f5= FontFactory.getFont(FontFactory.HELVETICA, 12, Font.BOLD,new CMYKColor(61, 0, 194,115));
				    }
				    else if("Above Ideal Ref. Range".equalsIgnoreCase(student.getStudentsHealth().getCategory())){
				    	f5= FontFactory.getFont(FontFactory.HELVETICA, 12, Font.BOLD,new CMYKColor(0, 89, 255,0));
				    }
				    else if("Below Ideal Ref. Range".equalsIgnoreCase(student.getStudentsHealth().getCategory())){
				    	f5= FontFactory.getFont(FontFactory.HELVETICA, 12, Font.BOLD,new CMYKColor(0, 181, 224,43));
				    }
				    phrase.add(new Chunk(student.getStudentsHealth().getCategory(),f5));
				    c3.addElement(phrase);
				    t.addCell(c3);

				    SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy");
				    
				    PdfPCell cell = new PdfPCell();cell.setPadding(10);cell.setBorderColor(new CMYKColor(0,0,0,50));
				    Phrase p = new Phrase("Name: ",f2);p.add(new Chunk(student.getFirstName()+" "+student.getLastName(),f1));
				    cell.addElement(p);
				    p = new Phrase("Date of Birth: ",f2);p.add(new Chunk(sdf.format(student.getDOB()),f1));
				    cell.addElement(p);
				    p = new Phrase("Age: ",f2);p.add(new Chunk(student.getStudentsHealth().getAgeYears()+" years "+student.getStudentsHealth().getAgeMonths()+" months",f1));
				    cell.addElement(p);
				    p = new Phrase("Gender: ",f2); p.add(new Chunk(student.getGender(),f1));
				    cell.addElement(p);
				    t.addCell(cell);
				    
				    cell = new PdfPCell();cell.setPadding(10);cell.setBorderColor(new CMYKColor(0,0,0,50));
				    p = new Phrase("School Name: ",f2);p.add(new Chunk(student.getSchoolName(),f1));
				    cell.addElement(p);
				    p = new Phrase("Branch Name: ",f2);p.add(new Chunk(student.getBranchName(),f1));
				    cell.addElement(p);
				    p = new Phrase("Class: ",f2);p.add(new Chunk(student.getClassName(),f1));
				    cell.addElement(p);
				    p = new Phrase("Section: ",f2);p.add(new Chunk(student.getSectionName(),f1));
				    cell.addElement(p);
				    p = new Phrase("Student Id: ",f2);p.add(new Chunk(student.getStudentRollId(),f1));
				    cell.addElement(p);
				    t.addCell(cell);
				    
				    cell = new PdfPCell();cell.setPadding(10);cell.setBorderColor(new CMYKColor(0,0,0,50));
				    p = new Phrase("Height: ",f2);p.add(new Chunk(student.getStudentsHealth().getHeight()+" "+student.getStudentsHealth().getHeightUnit(),f1));
				    cell.addElement(p);
				    p = new Phrase("Weight: ",f2);p.add(new Chunk(student.getStudentsHealth().getWeight()+" "+student.getStudentsHealth().getWeightUnit(),f1));
				    cell.addElement(p);
				    p = new Phrase("Body Mass Index (BMI): ",f2);p.add(new Chunk(""+student.getStudentsHealth().getBmi(),f1));
				    cell.addElement(p);
				    p = new Phrase("BMI Percentile: ",f2);p.add(new Chunk(""+student.getStudentsHealth().getBmiPercentile(),f1));
				    cell.addElement(p);
				    p = new Phrase("Ideal Body Weight (IBW): ",f2);p.add(new Chunk(student.getStudentsHealth().getIbw(),f1));
				    cell.addElement(p);
				    t.addCell(cell);
				    
				    document.add(t);
				    
				    StyleSheet css = new StyleSheet();css.loadTagStyle(HtmlTags.DIV, "font-size", "10px");
				    
				    Map<String,HTMLTagProcessor> tag = new HashMap<String,HTMLTagProcessor>();
				    tag.put(HtmlTags.DIV, HTMLTagProcessors.DIV);
				    
				    Paragraph parent = new Paragraph("",f1);parent.setIndentationLeft(7);parent.setIndentationRight(7);
				    if(student.getStudentsWellness().getGuidelines()!=null && !"".equals(student.getStudentsWellness().getGuidelines().trim())){
				    	t = new PdfPTable(1);t.setSpacingAfter(10);t.setWidthPercentage(100);
				    	cell = new PdfPCell();cell.setPaddingTop(0);cell.setPaddingBottom(7);cell.setPaddingLeft(10);cell.setPaddingRight(10);cell.setBorderColor(new CMYKColor(0,0,0,50));
					    cell.setBackgroundColor(new CMYKColor(0,0,0,20));
					    cell.addElement(new Paragraph("GUIDELINES:", f3));
					    t.addCell(cell);
					    document.add(t);
					    
						List<Element> parsedHtmlElements = HTMLWorker.parseToList(new StringReader(student.getStudentsWellness().getGuidelines()), css);
					    for(Element e: parsedHtmlElements){
					    	if(e instanceof com.itextpdf.text.List){
					    		com.itextpdf.text.List l = (com.itextpdf.text.List) e;
					    		l.setPreSymbol("=>");l.setNumbered(com.itextpdf.text.List.ORDERED);
					    		parent.add(l);
					    	}
					    	else
					    		parent.add(e);
					    }
					    document.add(parent);
					    document.add( Chunk.NEWLINE );
				    }
				    parent = new Paragraph("",f1);
				    parent.setIndentationLeft(10);
				    if(student.getStudentsWellness().getPntsToRem()!=null && !"".equals(student.getStudentsWellness().getPntsToRem().trim())){
				    	t = new PdfPTable(1);
				    	t.setSpacingAfter(10);
				    	t.setWidthPercentage(100);
				    	cell = new PdfPCell();
					    cell.setPaddingTop(0);
					    cell.setPaddingBottom(7);
					    cell.setPaddingLeft(10);
					    cell.setPaddingRight(10);
					    cell.setBorderColor(new CMYKColor(0,0,0,50));
					    cell.setBackgroundColor(new CMYKColor(0,0,0,20));
					    cell.addElement(new Paragraph("POINTS TO REMEMBER:", f3));
					    t.addCell(cell);
					    document.add(t);
					    List<Element> parsedHtmlElements = HTMLWorker.parseToList(new StringReader(student.getStudentsWellness().getPntsToRem()), css);
					    for(Element e: parsedHtmlElements){
					    	if(e instanceof com.itextpdf.text.List){
					    		com.itextpdf.text.List l = (com.itextpdf.text.List) e;
					    		l.setPreSymbol("=>");
					    		l.setNumbered(com.itextpdf.text.List.ORDERED);
					    		parent.add(l);
					    	}
					    	else
					    		parent.add(e);
					    }
					    document.add(parent);
					    document.add( Chunk.NEWLINE );
				    }
				    parent = new Paragraph("",f1);
				    parent.setIndentationLeft(10);
				    if(student.getStudentsWellness().getRecipeName()!=null && !"".equals(student.getStudentsWellness().getRecipeName().trim())){
				    	t = new PdfPTable(1);
				    	t.setSpacingAfter(10);
				    	t.setWidthPercentage(100);
				    	cell = new PdfPCell();
					    cell.setPaddingTop(0);
					    cell.setPaddingBottom(7);
					    cell.setPaddingLeft(10);
					    cell.setPaddingRight(10);
					    cell.setBorderColor(new CMYKColor(0,0,0,50));
					    cell.setBackgroundColor(new CMYKColor(0,0,0,20));
					    cell.addElement(new Paragraph("A RECIPE JUST FOR YOU!", f3));
					    t.addCell(cell);
					    document.add(t);
					    List<Element> parsedHtmlElements = HTMLWorker.parseToList(new StringReader(student.getStudentsWellness().getRecipeName()), css);
					    for(Element e: parsedHtmlElements)
					    	parent.add(e);
					    parsedHtmlElements = HTMLWorker.parseToList(new StringReader(student.getStudentsWellness().getRecipeContent()), css);
					    for(Element e: parsedHtmlElements){
					    	if(e instanceof com.itextpdf.text.List){
					    		com.itextpdf.text.List l = (com.itextpdf.text.List) e;
					    		l.setPreSymbol("=>");
					    		l.setNumbered(com.itextpdf.text.List.ORDERED);
					    		parent.add(l);
					    	}
					    	else
					    		parent.add(e);
					    }
					    document.add(parent);
					    document.add( Chunk.NEWLINE );
				    }
				    
				    Paragraph p1 = null;
				    p1 = new Paragraph("For any further assistance you may contact us at info@almanourisher.com / 8147359500.", f1);
				    document.add(p1);
				    p1 = new Paragraph("Disclaimer: This report is based on your child's anthropometrics and not to be substituted as a total nutrition advice until we assess your child's diet history and lifestyle pattern.", f1);
				    document.add(p1);
				    p1 = new Paragraph("This is system generated report.", f1);
				    document.add(p1);
				    document.newPage();
				}
				document.close();
				manipulatePdf(filename, filepath+"reports/Water_BulkNutritionReport_"+branchId+".pdf");
			} catch (DocumentException | IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return "BulkNutritionReport_"+branchId;
	}
	
	@GET
	@Path("/bulkFoodFreqReport/{branchId}")
	public String bulkFoodFreqReport(@PathParam("branchId") int branchId){
		List<Student> students = this.studentDAO.getStudentsByBranchId(branchId);
		if (students != null) {
			String filename = filepath+"reports/BulkFoodFreqReport_"+branchId+".pdf";
			Document document = new Document(PageSize.A4, 50, 50, 50, 50);
			PdfWriter writer = null;
			try {
				File file = new File(filename); 
				file.createNewFile();
            	file.setReadable(true, false);
            	file.setWritable(true, false);
            	file.setExecutable(true, true);
				writer = PdfWriter.getInstance(document, new FileOutputStream(file));
			
				document.open();
				
			    Font f = FontFactory.getFont(FontFactory.HELVETICA, 12, Font.BOLD, new CMYKColor(255,255,255,255));
			    Font f1 = FontFactory.getFont(FontFactory.HELVETICA, 10, Font.NORMAL,new CMYKColor(255,255,255,255));
			    Font f2 = FontFactory.getFont(FontFactory.HELVETICA, 10, Font.NORMAL, new CMYKColor(0,0,0,209));
			    Font f3 = FontFactory.getFont(FontFactory.HELVETICA, 12, Font.BOLD, new CMYKColor(255,255,255,255));
			
				for (Student student : students) {
					StudentHealth studentHealth = this.studentDAO.getStudentsHealthByRollIdSchoolId(student.getStudentRollId(), student.getSchoolId());
	
					if (studentHealth != null) {
						double age = studentHealth.getAge();
						 String[] arr=String.valueOf(age).split("\\.");
						 studentHealth.setAgeYears(Integer.parseInt(arr[0]));
						 studentHealth.setAgeMonths(Integer.parseInt(arr[1])); // 9
						 student.setStudentsHealth(studentHealth);
	
						List<FoodFreqReport> ffrs = this.reportDAO.getFoodFreqReport(student.getStudentId());
						student.setFoodFreqReport(ffrs);
						
					}
					
					if(student.getFoodFreqReport().isEmpty()){
						continue;
					}
					
					Paragraph paragraph1 = new Paragraph();
					Image image = Image.getInstance(filepath+"images/school/"+student.getSchoolLogo());
				    image.scaleToFit(75, 75);
				    document.add( Chunk.NEWLINE );
				    paragraph1.add(new Chunk(image, 0, 0));
				    Paragraph c = new Paragraph("Food Frequency Report", FontFactory.getFont(FontFactory.HELVETICA, 22, Font.BOLD, new CMYKColor(171, 79, 0,53)));		    
				    c.setAlignment(Element.ALIGN_CENTER);
				    paragraph1.add(c);
				    paragraph1.setSpacingAfter(25);
				    document.add(paragraph1);
				    
				  //  document.add( Chunk.NEWLINE );
				    
					PdfPTable t = new PdfPTable(3);
					t.setSpacingAfter(30);
					t.setWidthPercentage(100);
				    PdfPCell c1 = new PdfPCell(new Phrase("Personal Details: ",f));
				    c1.setPadding(10);
				    c1.setBorderColor(new CMYKColor(0,0,0,50));
				    t.addCell(c1);
				    PdfPCell c2 = new PdfPCell(new Phrase("School Details: ",f));
				    c2.setPadding(10);
				    c2.setBorderColor(new CMYKColor(0,0,0,50));
				    t.addCell(c2);
				    PdfPCell c3 = new PdfPCell();
				    c3.setPaddingTop(5);
				    c3.setPaddingBottom(7);
				    c3.setPaddingLeft(10);
				    c3.setPaddingRight(10);
				    c3.setBorderColor(new CMYKColor(0,0,0,50));
				    Phrase phrase = new Phrase("Health Status: ",f);
				    
				    Font f5 = FontFactory.getFont(FontFactory.HELVETICA, 12, Font.BOLD, new CMYKColor(255,255,255,255));
				    if("Healthy".equalsIgnoreCase(student.getStudentsHealth().getCategory())){
				    	f5= FontFactory.getFont(FontFactory.HELVETICA, 12, Font.BOLD,new CMYKColor(61, 0, 194,115));
				    }
				    else if("Above Ideal Ref. Range".equalsIgnoreCase(student.getStudentsHealth().getCategory())){
				    	f5= FontFactory.getFont(FontFactory.HELVETICA, 12, Font.BOLD,new CMYKColor(0, 89, 255,0));
				    }
				    else if("Below Ideal Ref. Range".equalsIgnoreCase(student.getStudentsHealth().getCategory())){
				    	f5= FontFactory.getFont(FontFactory.HELVETICA, 12, Font.BOLD,new CMYKColor(0, 181, 224,43));
				    }
				    phrase.add(new Chunk(student.getStudentsHealth().getCategory(),f5));
				    c3.addElement(phrase);
				    t.addCell(c3);

				    SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy");
				    
				    PdfPCell cell = new PdfPCell();
				    cell.setPadding(10);
				    cell.setBorderColor(new CMYKColor(0,0,0,50));
				    Phrase p = new Phrase("Name: ",f2);
				    p.add(new Chunk(student.getFirstName()+" "+student.getLastName(),f1));
				    cell.addElement(p);
				    p = new Phrase("Date of Birth: ",f2);
				    p.add(new Chunk(sdf.format(student.getDOB()),f1));
				    cell.addElement(p);
				    p = new Phrase("Age: ",f2);
				    p.add(new Chunk(student.getStudentsHealth().getAgeYears()+" years "+student.getStudentsHealth().getAgeMonths()+" months",f1));
				    cell.addElement(p);
				    p = new Phrase("Gender: ",f2);
				    p.add(new Chunk(student.getGender(),f1));
				    cell.addElement(p);
				    p = new Phrase("Food Preference: ",f2);
				    p.add(new Chunk(student.getFoodFreqReport().get(0).getFoodPreference(),f1));
				    cell.addElement(p);
				    t.addCell(cell);
				    
				    cell = new PdfPCell();
				    cell.setPadding(10);
				    cell.setBorderColor(new CMYKColor(0,0,0,50));
				    p = new Phrase("School Name: ",f2);
				    p.add(new Chunk(student.getSchoolName(),f1));
				    cell.addElement(p);
				    p = new Phrase("Branch Name: ",f2);
				    p.add(new Chunk(student.getBranchName(),f1));
				    cell.addElement(p);
				    p = new Phrase("Class: ",f2);
				    p.add(new Chunk(student.getClassName(),f1));
				    cell.addElement(p);
				    p = new Phrase("Section: ",f2);
				    p.add(new Chunk(student.getSectionName(),f1));
				    cell.addElement(p);
				    p = new Phrase("Student Id: ",f2);
				    p.add(new Chunk(student.getStudentRollId(),f1));
				    cell.addElement(p);
				    t.addCell(cell);
				    
				    cell = new PdfPCell();
				    cell.setPadding(10);
				    cell.setBorderColor(new CMYKColor(0,0,0,50));
				    p = new Phrase("Height: ",f2);
				    p.add(new Chunk(student.getStudentsHealth().getHeight()+" "+student.getStudentsHealth().getHeightUnit(),f1));
				    cell.addElement(p);
				    p = new Phrase("Weight: ",f2);
				    p.add(new Chunk(student.getStudentsHealth().getWeight()+" "+student.getStudentsHealth().getWeightUnit(),f1));
				    cell.addElement(p);
				    p = new Phrase("Body Mass Index (BMI): ",f2);
				    p.add(new Chunk(""+student.getStudentsHealth().getBmi(),f1));
				    cell.addElement(p);
				    p = new Phrase("BMI Percentile: ",f2);
				    p.add(new Chunk(""+student.getStudentsHealth().getBmiPercentile(),f1));
				    cell.addElement(p);
				    p = new Phrase("Ideal Body Weight (IBW): ",f2);
				    p.add(new Chunk(student.getStudentsHealth().getIbw(),f1));
				    cell.addElement(p);
				    t.addCell(cell);
				    
				    document.add(t);
				    
				 //   document.add( Chunk.NEWLINE );
				    
				    StyleSheet css = new StyleSheet();
				    css.loadTagStyle(HtmlTags.DIV, "font-size", "10px");
				    
				    Map<String,HTMLTagProcessor> tag = new HashMap<String,HTMLTagProcessor>();
				    tag.put(HtmlTags.DIV, HTMLTagProcessors.DIV);
				   // htmlWorker.setStyleSheet(css);
				    
				    Image greenstar = Image.getInstance(filepath+"images/greenstar.png");
			    	greenstar.scaleToFit(15, 15);
			    	Image redstar = Image.getInstance(filepath+"images/redstar.png");
			    	redstar.scaleToFit(15, 15);
			    	Image orangestar = Image.getInstance(filepath+"images/orangestar.png");
			    	orangestar.scaleToFit(15, 15);
			    	
				    for(FoodFreqReport ffr: student.getFoodFreqReport()){
				    
					    Paragraph parent = new Paragraph("",f1);
					    parent.setIndentationLeft(7);
					    parent.setIndentationRight(7);
					    if(ffr.getContent()!=null && !"".equals(ffr.getContent().trim())){
					    	t = new PdfPTable(1);
					    	t.setSpacingAfter(10);
					    	t.setWidthPercentage(100);
					    	cell = new PdfPCell();
						    cell.setPaddingTop(7);
						    cell.setPaddingBottom(7);
						    cell.setPaddingLeft(10);
						    cell.setPaddingRight(10);
						    cell.setBorderColor(new CMYKColor(0,0,0,50));
						    cell.setBackgroundColor(new CMYKColor(0,0,0,20));
						    Paragraph question = new Paragraph();
						    if("C".equalsIgnoreCase(ffr.getGrade())){
							    question.add(new Chunk(redstar, 0, 0));
						    }
						    else if("B".equalsIgnoreCase(ffr.getGrade())){
						    	question.add(new Chunk(orangestar, 0, 0));
						    	question.add(new Chunk(orangestar, 0, 0));
						    }
						    else if("A".equalsIgnoreCase(ffr.getGrade())){
						    	question.add(new Chunk(greenstar, 0, 0));
							    question.add(new Chunk(greenstar, 0, 0));
							    question.add(new Chunk(greenstar, 0, 0));
						    }
						    question.add(new Chunk("     "+ffr.getQuestion(), f3));
						    cell.addElement(question);
						    t.addCell(cell);
						    document.add(t);
						    
						    
							List<Element> parsedHtmlElements = HTMLWorker.parseToList(new StringReader(ffr.getContent()), css);
						    for(Element e: parsedHtmlElements){
						    	if(e instanceof com.itextpdf.text.List){
						    		com.itextpdf.text.List l = (com.itextpdf.text.List) e;
						    		l.setPreSymbol("=>");
						    		l.setNumbered(com.itextpdf.text.List.ORDERED);
						    		parent.add(l);
						    	}
						    	else
						    		parent.add(e);
						    }
						    //htmlWorker.parse(new StringReader(student.getStudentsWellness().getGuidelines()));
						    document.add(parent);
						    document.add( Chunk.NEWLINE );
					    }
				    }
				    
				    Paragraph p1 = null;
				    p1 = new Paragraph("For any further assistance you may contact us at info@almanourisher.com / 8147359500.", f1);
				    document.add(p1);
				    p1 = new Paragraph("Disclaimer: This report is based on your child's anthropometrics and not to be substituted as a total nutrition advice until we assess your child's diet history and lifestyle pattern.", f1);
				    document.add(p1);
				    p1 = new Paragraph("This is system generated report.", f1);
				    document.add(p1);
				    p1 = new Paragraph("Log on to www.almanourisher.com with your registered email id or contact number [Password: alma@123].", f1);
				    document.add(p1);
				    document.newPage();
				}
				document.close();
				manipulatePdf(filename, filepath+"reports/Water_BulkFoodFreqReport_"+branchId+".pdf");
			} catch (DocumentException | IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return "BulkFoodFreqReport_"+branchId;
	}
	
	private void manipulatePdf(String src, String dest) throws IOException, DocumentException {
        PdfReader reader = new PdfReader(src);
        int n = reader.getNumberOfPages();
        File file = new File(dest); 
		file.createNewFile();
    	file.setReadable(true, false);
    	file.setWritable(true, false);
    	file.setExecutable(true, true);
        PdfStamper stamper = new PdfStamper(reader, new FileOutputStream(file));
        stamper.setRotateContents(false);
        
        // image watermark
        Image img = Image.getInstance(filepath+"images/alma-nourisher-logo.png");
        float w = img.getScaledWidth();
        float h = img.getScaledHeight();
        // transparency
        PdfGState gs1 = new PdfGState();
        gs1.setFillOpacity(0.1f);
        // properties
        PdfContentByte over;
        Rectangle pagesize;
        float x, y;
        // loop over every page
        for (int i = 1; i <= n; i++) {
            pagesize = reader.getPageSize(i);
            x = (pagesize.getLeft() + pagesize.getRight()) / 2;
            y = (pagesize.getTop() + pagesize.getBottom()) / 2;
            over = stamper.getOverContent(i);
            over.saveState();
            over.setGState(gs1);
            over.addImage(img, w, 0, 0, h, x - (w / 2), y - (h / 2));
            over.restoreState();
        }
        stamper.close();
        reader.close();
    }
}
