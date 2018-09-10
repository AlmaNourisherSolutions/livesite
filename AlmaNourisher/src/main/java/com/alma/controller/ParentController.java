package com.alma.controller;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringReader;
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
import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySources;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;

import com.alma.dao.DocumentsDAO;
import com.alma.dao.ManageUserDAO;
import com.alma.dao.ReportDAO;
import com.alma.dao.StudentDAO;
import com.alma.dao.StudentHealthHistoryDAO;
import com.alma.dao.UsersDAO;
import com.alma.dao.WellnessLookupAssociationDAO;
import com.alma.model.Article;
import com.alma.model.ArticleComment;
import com.alma.model.FoodFreqReport;
import com.alma.model.Query;
import com.alma.model.Questionaire;
import com.alma.model.QuestionaireAns;
import com.alma.model.Recipe;
import com.alma.model.RecipeComment;
import com.alma.model.Student;
import com.alma.model.StudentHealth;
import com.alma.model.StudentHealthHistory;
import com.alma.model.UserCredentials;
import com.alma.model.Users;
import com.alma.model.WellnessLookupAssociation;
import com.itextpdf.text.Anchor;
import com.itextpdf.text.Chapter;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Font.FontFamily;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Image;
import com.itextpdf.text.ListItem;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.Section;
import com.itextpdf.text.html.HtmlTags;
import com.itextpdf.text.html.simpleparser.HTMLTagProcessor;
import com.itextpdf.text.html.simpleparser.HTMLTagProcessors;
import com.itextpdf.text.html.simpleparser.HTMLWorker;
import com.itextpdf.text.html.simpleparser.StyleSheet;
import com.itextpdf.text.pdf.CMYKColor;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfGState;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import com.itextpdf.text.pdf.PdfWriter;

@Path("/parent")
@PropertySources({ @org.springframework.context.annotation.PropertySource({ "classpath:dbconfig.properties" }) })
@CrossOrigin
@Controller
public class ParentController {
	
	static Logger log = Logger.getLogger(ParentController.class.getName());
	
	//String filepath = "D:/work/Alma/almaAngular/app/assets/";
	String filepath = "/home/almablr/public_html/assets/";
	
	@Autowired
	private StudentDAO studentDAO;

	@Autowired
	private WellnessLookupAssociationDAO wellnessLookupAssociationDAO;
	
	@Autowired
	private StudentHealthHistoryDAO studentHealthHistoryDAO;
	
	@Autowired
	private ReportDAO reportDAO;
	
	@Autowired
	private DocumentsDAO documentsDAO;
	
	@Autowired
	private ManageUserDAO manageUserDAO;
	
	@Autowired
	private UsersDAO usersDAO;
	
	@GET
	@Path("/getChildDetails/{parentId}")
	public List<Student> getChildDetails(@PathParam("parentId") String parentId) {
		List<Student> children = this.studentDAO.getChildBasedOnParentId(parentId);
		
		if (children != null) {

			for (Student child : children) {
				StudentHealth studentHealth = this.studentDAO.getStudentsHealthByRollIdSchoolId(
						child.getStudentRollId(), child.getSchoolId());

				if (studentHealth != null) {
					double age = studentHealth.getAge();
					 String[] arr=String.valueOf(age).split("\\.");
					 studentHealth.setAgeYears(Integer.parseInt(arr[0]));
					 studentHealth.setAgeMonths(Integer.parseInt(arr[1])); // 9
					child.setStudentsHealth(studentHealth);

					WellnessLookupAssociation wla = this.reportDAO.getNutritionReport(child.getStudentId());
					child.setStudentsWellness(wla);
					
					List<FoodFreqReport> ffrs = this.reportDAO.getFoodFreqReport(child.getStudentId());
					child.setFoodFreqReport(ffrs);
				}
				
				List<StudentHealthHistory> studentHealthHistory = this.studentHealthHistoryDAO.getStudentsHealthHistoryByRollIdSchoolId(child.getStudentRollId(), child.getSchoolId());
				if(!studentHealthHistory.isEmpty())
					child.setStudentHealthHistory(studentHealthHistory);
			}
		}
		
		return children;
	}

	@GET
	@Path("/getQuestionaire")
	public List<Questionaire> getQuestionaire() {
		List<Questionaire> questionaire= this.reportDAO.getQuestionaire();
		
		return questionaire;
	}
	
	@POST
	@Path("/submitQuestionaire")
	@Consumes(MediaType.APPLICATION_JSON)
	public String submitQuestionaire(QuestionaireAns qna) {
		return this.reportDAO.submitQuestionaire(qna.getQuestionaire(), qna.getChild(), qna.getFoodPerference());
	}
	
	@GET
	@Path("/getArticleList/{schoolUrl}/{tag}")
	@Produces(MediaType.APPLICATION_JSON)
	public Map<String,List<Article>> getArticleList(@PathParam("schoolUrl") String schoolUrl, @PathParam("tag") String tag) {
		return this.documentsDAO.getArticleList("PARENT",schoolUrl, tag);
	}
	
	@GET
	@Path("/getRecipeList/{schoolUrl}/{tag}")
	@Produces(MediaType.APPLICATION_JSON)
	public Map<String,List<Recipe>> getRecipeList(@PathParam("schoolUrl") String schoolUrl, @PathParam("tag") String tag) {
		return this.documentsDAO.getRecipeList("PARENT",schoolUrl, tag);
	}
	
	@GET
	@Path("/getRecentArticleList/{schoolUrl}")
	@Produces(MediaType.APPLICATION_JSON)
	public List<Article> getRecentArticleList(@PathParam("schoolUrl") String schoolUrl) {
		return this.documentsDAO.getRecentArticles("PARENT",schoolUrl);
	}
	
	@GET
	@Path("/getRecentRecipeList/{schoolUrl}")
	@Produces(MediaType.APPLICATION_JSON)
	public List<Recipe> getRecentRecipeList(@PathParam("schoolUrl") String schoolUrl) {
		return this.documentsDAO.getRecentRecipes("PARENT",schoolUrl);
	}
	
	@GET
	@Path("/getCommonTags")
	@Produces(MediaType.APPLICATION_JSON)
	public List<String> getCommonTags() {
		return this.documentsDAO.getCommonTags();
	}
	
	@POST
	@Path("/getArticleByName/{schoolUrl}/{heading}")
	@Produces(MediaType.APPLICATION_JSON)
	public Article getArticleByName(@PathParam("schoolUrl") String schoolUrl, @PathParam("heading") String heading) {
		Article article = new Article();
		try {
			article = this.documentsDAO.getArticleByName(schoolUrl, heading);

			System.out.println(new StringBuilder().append("Article ((((((((( ").append(heading));
		} catch (Exception e) {
			log.error(e);
			e.printStackTrace();
		}
		return article;
	}
	
	@POST
	@Path("/getRecipeByName/{schoolUrl}/{heading}")
	@Produces(MediaType.APPLICATION_JSON)
	public Recipe getRecipeByName(@PathParam("schoolUrl") String schoolUrl, @PathParam("heading") String heading) {
		Recipe recipe = new Recipe();
		try {
			recipe = this.documentsDAO.getRecipeByName(schoolUrl, heading);

			System.out.println(new StringBuilder().append("Recipe ((((((((( ").append(heading));
		} catch (Exception e) {
			log.error(e);
			e.printStackTrace();
		}
		return recipe;
	}
	
	@POST
	@Path("/performLikeonArticle/{articleId}/{userId}")
	public void performLikeonArticle( @PathParam("articleId") int articleId,  @PathParam("userId") int userId){
		this.documentsDAO.addLikestoArticle(articleId, userId);
	}
	
	@POST
	@Path("/performLikeonRecipe/{recipeId}/{userId}")
	public void performLikeonRecipe( @PathParam("recipeId") int recipeId,  @PathParam("userId") int userId){
		this.documentsDAO.addLikestoRecipe(recipeId, userId);
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
	
	@GET
	@Path("/isArticleLiked/{articleId}/{userId}")
	@Produces(MediaType.APPLICATION_JSON)
	public boolean isArticleLiked(@PathParam("articleId") int articleId, @PathParam("userId") int userId){
		return this.documentsDAO.isArticleLiked(articleId, userId);
	}
	
	@GET
	@Path("/isRecipeLiked/{recipeId}/{userId}")
	@Produces(MediaType.APPLICATION_JSON)
	public boolean isRecipeLiked(@PathParam("recipeId") int recipeId, @PathParam("userId") int userId){
		return this.documentsDAO.isRecipeLiked(recipeId, userId);
	}
	
	@POST
	@Path("/changePassword")
	@Consumes(MediaType.APPLICATION_JSON)
	public String changePassword(Users u){
		this.manageUserDAO.changePassword(u);
		return "success";
	}
	
	@SuppressWarnings("deprecation")
	@POST
	@Path("/downloadNutritionReport")
	@Consumes(MediaType.APPLICATION_JSON)
	public String downloadNutritionReport(Student student){
		
		String filename = filepath+"reports/NutritionReport_"+student.getFirstName()+".pdf";
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
			
		    Font f = FontFactory.getFont(FontFactory.HELVETICA, 12, Font.BOLD);
		    Font f1 = FontFactory.getFont(FontFactory.HELVETICA, 10, Font.NORMAL,new CMYKColor(255,255,255,255));
		    Font f2 = FontFactory.getFont(FontFactory.HELVETICA, 10, Font.NORMAL, new CMYKColor(0,0,0,209));
		    Font f3 = FontFactory.getFont(FontFactory.HELVETICA, 12, Font.BOLD);
			
		    Paragraph paragraph1 = new Paragraph();
			Image image = Image.getInstance(filepath+"images/school/"+student.getSchoolLogo());
		    image.scaleToFit(75, 75);
		    Paragraph imgp = new Paragraph();
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
		    
		    if("Healthy".equalsIgnoreCase(student.getStudentsHealth().getCategory())){
		    	f= FontFactory.getFont(FontFactory.HELVETICA, 12, Font.BOLD,new CMYKColor(61, 0, 194,115));
		    }
		    else if("Above Ideal Ref. Range".equalsIgnoreCase(student.getStudentsHealth().getCategory())){
		    	f= FontFactory.getFont(FontFactory.HELVETICA, 12, Font.BOLD,new CMYKColor(0, 89, 255,0));
		    }
		    else if("Below Ideal Ref. Range".equalsIgnoreCase(student.getStudentsHealth().getCategory())){
		    	f= FontFactory.getFont(FontFactory.HELVETICA, 12, Font.BOLD,new CMYKColor(0, 181, 224,43));
		    }
		    phrase.add(new Chunk(student.getStudentsHealth().getCategory(),f));
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
		
		    
		    document.close();
		    
		    manipulatePdf(filename, filepath+"reports/Water_NutritionReport_"+student.getStudentId()+"_"+student.getFirstName()+".pdf");
		    
		    
		} catch (DocumentException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
		return filepath+"reports/Water_NutritionReport_"+student.getStudentId()+"_"+student.getFirstName()+".pdf";
	}	
	
	@SuppressWarnings("deprecation")
	@POST
	@Path("/downloadFoodFreqReport")
	@Consumes(MediaType.APPLICATION_JSON)
	public String downloadFoodFreqReport(Student student){
		
		String filename = filepath+"reports/FoodFreqReport_"+student.getFirstName()+".pdf";
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
			
		    Font f = FontFactory.getFont(FontFactory.HELVETICA, 12, Font.BOLD);
		    Font f1 = FontFactory.getFont(FontFactory.HELVETICA, 10, Font.NORMAL,new CMYKColor(255,255,255,255));
		    Font f2 = FontFactory.getFont(FontFactory.HELVETICA, 10, Font.NORMAL, new CMYKColor(0,0,0,209));
		    Font f3 = FontFactory.getFont(FontFactory.HELVETICA, 12, Font.BOLD);
			
		    Paragraph paragraph1 = new Paragraph();
			Image image = Image.getInstance(filepath+"images/school/"+student.getSchoolLogo());
		    image.scaleToFit(75, 75);
		    Paragraph imgp = new Paragraph();
		    paragraph1.add(new Chunk(image, 0, 0));
		    Paragraph c = new Paragraph("Food Frequency Report", FontFactory.getFont(FontFactory.HELVETICA, 22, Font.BOLD, new CMYKColor(171, 79, 0,53)));		    
		    c.setAlignment(Element.ALIGN_CENTER);
		    paragraph1.add(c);
		    paragraph1.setSpacingBefore(10);
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
		    
		    if("Healthy".equalsIgnoreCase(student.getStudentsHealth().getCategory())){
		    	f= FontFactory.getFont(FontFactory.HELVETICA, 12, Font.BOLD,new CMYKColor(61, 0, 194,115));
		    }
		    else if("Above Ideal Ref. Range".equalsIgnoreCase(student.getStudentsHealth().getCategory())){
		    	f= FontFactory.getFont(FontFactory.HELVETICA, 12, Font.BOLD,new CMYKColor(0, 89, 255,0));
		    }
		    else if("Below Ideal Ref. Range".equalsIgnoreCase(student.getStudentsHealth().getCategory())){
		    	f= FontFactory.getFont(FontFactory.HELVETICA, 12, Font.BOLD,new CMYKColor(0, 181, 224,43));
		    }
		    phrase.add(new Chunk(student.getStudentsHealth().getCategory(),f));
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
		    t.addCell(cell);
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
		    
		    for(FoodFreqReport ffr: student.getFoodFreqReport()){
		    
		    	Image greenstar = Image.getInstance(filepath+"images/greenstar.png");
		    	greenstar.scaleToFit(15, 15);
		    	Image redstar = Image.getInstance(filepath+"images/redstar.png");
		    	redstar.scaleToFit(15, 15);
		    	Image orangestar = Image.getInstance(filepath+"images/orangestar.png");
		    	orangestar.scaleToFit(15, 15);
			    
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
		/*    Image image2 = Image.getInstance(filepath+"images/alma-nourisher-logo.png");
		    image2.scaleAbsolute(120f, 120f);
		    document.add(image2);*/
		    
		    document.close();
		    
		    manipulatePdf(filename, filepath+"reports/Water_FoodFreqReport_"+student.getStudentId()+"_"+student.getFirstName()+".pdf");
		    
		    
		} catch (DocumentException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
		return filepath+"reports/Water_FoodFreqReport_"+student.getStudentId()+"_"+student.getFirstName()+".pdf";
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
	
	@POST
	@Path("/postQuery")
	@Consumes(MediaType.APPLICATION_JSON)
	public String postQuery(Query q){
		
		return "success";
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
	@Path("/addHeightnWeight")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Number addHeightnWeight(Student studentDetail) {
		
		this.studentDAO.updateStudentHealthRecord(studentDetail);
		
		return studentDetail.getStudentId();
	}
	
	@POST
	@Path("/editParentDetails")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Number editParentDetails(Users user) {
		
		this.usersDAO.updateUser(user);
		
		return user.getUserId();
	}
	
	@POST
	@Path("/updateHeightandWeight")
	@Consumes(MediaType.APPLICATION_JSON)
	public void updateHeightandWeight(Student studentDetails){
		this.studentDAO.updateStudentHealthRecord(studentDetails);
	}
	
}
