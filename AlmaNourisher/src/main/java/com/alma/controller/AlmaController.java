package com.alma.controller;

import java.util.List;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySources;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;

import com.alma.dao.DocumentsDAO;
import com.alma.dao.ManageSchoolDAO;
import com.alma.dao.UsersDAO;
import com.alma.model.Article;
import com.alma.model.ArticleComment;
import com.alma.model.ContactUs;
import com.alma.model.ManageSchool;
import com.alma.model.Recipe;
import com.alma.model.RecipeComment;

@Path("/alma")
@PropertySources({ @org.springframework.context.annotation.PropertySource({ "classpath:dbconfig.properties" }) })
@CrossOrigin
@Controller
public class AlmaController {
	
	@Autowired
	private ManageSchoolDAO manageSchoolDAO;
	
	@Autowired
	private UsersDAO usersDAO;
	
	@Autowired
	private DocumentsDAO documentsDAO;
	
	@GET
	@Path("/getSchoolList")
	@Produces(MediaType.APPLICATION_JSON)
	public List<ManageSchool> getAllSchool() {
		List<ManageSchool> schools = this.manageSchoolDAO.getSchoolList();

		return schools;
	}
	
	@GET
	@Path("/getSchoolFromUrl/{schoolUrl}")
	public ManageSchool getSchoolFromUrl(@PathParam("schoolUrl") String schoolUrl) {
		return this.manageSchoolDAO.getSchoolFromUrl(schoolUrl);
	}

	@POST
	@Path("/addContactUsRecord")
	@Consumes(MediaType.APPLICATION_JSON)
	public String  addContactUsRecord(ContactUs contactUs){
		usersDAO.addContactUsRecord(contactUs);
		return "success";
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
			e.printStackTrace();
		}
		return recipe;
	}
	
	@GET
	@Path("/getArticleList/{tag}")
	@Produces(MediaType.APPLICATION_JSON)
	public Map<String,List<Article>> getArticleList(@PathParam("tag") String tag) {
		return this.documentsDAO.getArticleList("GUEST", null, tag);
	}
	
	@GET
	@Path("/getRecipeList/{tag}")
	@Produces(MediaType.APPLICATION_JSON)
	public Map<String,List<Recipe>> getRecipeList(@PathParam("tag") String tag) {
		return this.documentsDAO.getRecipeList("GUEST",null, tag);
	}
	
	@GET
	@Path("/getRecentArticleList")
	@Produces(MediaType.APPLICATION_JSON)
	public List<Article> getRecentArticleList() {
		return this.documentsDAO.getRecentArticles("GUEST",null);
	}
	
	@GET
	@Path("/getRecentRecipeList")
	@Produces(MediaType.APPLICATION_JSON)
	public List<Recipe> getRecentRecipeList() {
		return this.documentsDAO.getRecentRecipes("GUEST",null);
	}
	
	@GET
	@Path("/getCommonTags")
	@Produces(MediaType.APPLICATION_JSON)
	public List<String> getCommonTags() {
		return this.documentsDAO.getCommonTags();
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
}
