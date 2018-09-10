package com.alma.dao;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;

import com.alma.model.Article;
import com.alma.model.ArticleComment;
import com.alma.model.Recipe;
import com.alma.model.RecipeComment;

public interface DocumentsDAO {
	public BigInteger createArticle(Article arcticle);
	public BigInteger createRecipe(Recipe recipe);
	public Article getArticleByName(String heading);
	public Recipe getRecipeByName(String heading);
	public Map<String,List<Article>> getArticleList(String role, String schoolUrl, String tag);
	public Map<String,List<Recipe>> getRecipeList(String role, String schoolUrl, String tag);
	public void publishArticle(int articleId);
	public void publishRecipe(int recipeId);
	public Article getArticleByName(String schoolUrl, String heading);
	public Recipe getRecipeByName(String schoolUrl, String heading);
	
	public List<Article> getRecentArticles(String role, String schoolUrl);
	public List<Recipe> getRecentRecipes(String role,String schoolUrl);
	public List<String> getCommonTags();
	
	public int totalArticlesCount();
	public int totalRecipesCount();
	
	public void addLikestoArticle(int id, int userId);
	public void addLikestoRecipe(int id, int userId);
	
	public void addCommenttoArticle(ArticleComment acomment);
	public void addCommenttoRecipe(RecipeComment rcomment);
	public List<ArticleComment> getArticleComment(int articleId);
	public List<RecipeComment> getRecipeComment(int recipeId);
	
	public boolean isArticleLiked(Integer articleId, Integer userId);
	public boolean isRecipeLiked(Integer recipeId, Integer userId);
}
