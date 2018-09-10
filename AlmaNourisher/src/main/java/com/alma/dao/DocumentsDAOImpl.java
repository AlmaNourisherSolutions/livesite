package com.alma.dao;

import java.math.BigInteger;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import com.alma.model.Article;
import com.alma.model.ArticleComment;
import com.alma.model.ManageSchool;
import com.alma.model.Recipe;
import com.alma.model.RecipeComment;

public class DocumentsDAOImpl implements DocumentsDAO {
	private JdbcTemplate jdbcTemplate;
	private Logger log = Logger.getLogger(DocumentsDAOImpl.class);
	
	private String[] monthNames = {"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};
	public DocumentsDAOImpl(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}
	
	@Override
	public BigInteger createArticle(Article article) {
		String seoUrl = article.getHeading().trim().replaceAll("[^a-zA-Z0-9\\s]", "");
		article.setSeoheading(seoUrl.replaceAll(" +", "-"));
		this.log.debug("Populating articles table .....");
		String sql = "INSERT INTO articles(heading, seo_url,icon_image, description, content, inserted_date, updated_date, publish_date) VALUES (?,?,?,?,?,?,?,?)";

		KeyHolder keyHolder = new GeneratedKeyHolder();
		this.jdbcTemplate.update(new PreparedStatementCreator() {
		        public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
		            PreparedStatement ps =
		                connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
		            ps.setString(1, article.getHeading());
		            ps.setString(2, article.getSeoheading());
		            ps.setString(3, article.getIcon());
		            ps.setString(4, article.getDescription());
		            ps.setString(5,article.getContent());
		            Date utilDate = new Date();
		            ps.setDate(6,  new java.sql.Date(utilDate.getTime()));
		            ps.setDate(7, new java.sql.Date(utilDate.getTime()));
		            ps.setDate(8, new java.sql.Date(article.getPublishDate().getTime()));
		            return ps;
		        }
		    },
		    keyHolder);
		BigInteger articleId =  (BigInteger) keyHolder.getKey();
		
		for(String t: article.getTags()){
			this.jdbcTemplate.update("INSERT INTO article_tags(article_id, tag) VALUES (?,?)",
					new Object[] { articleId.intValue(), t});
		}
		
		for(String s: article.getSchools()){
			this.jdbcTemplate.update("INSERT INTO school_article_map( school_id, article_id) VALUES (?,?)",
					new Object[] { s, articleId.intValue()});
		}
		return articleId;
	}

	@Override
	public BigInteger createRecipe(Recipe recipe) {
		String seoUrl = recipe.getHeading().trim().replaceAll("[^a-zA-Z0-9\\s]", "");
		recipe.setSeoheading(seoUrl.replaceAll(" +", "-"));
		
		this.log.debug("Populating recipes table .....");
		String sql = "INSERT INTO recipes(heading, seo_url, icon_image, description, content, inserted_date, updated_date,publish_date) VALUES (?,?,?,?,?,?,?,?)";
		
		KeyHolder keyHolder = new GeneratedKeyHolder();
		this.jdbcTemplate.update(new PreparedStatementCreator() {
		        public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
		            PreparedStatement ps =
		                connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
		            ps.setString(1, recipe.getHeading());
		            ps.setString(2, recipe.getSeoheading());
		            ps.setString(3, recipe.getIcon());
		            ps.setString(4, recipe.getDescription());
		            ps.setString(5,recipe.getContent());
		            Date utilDate = new Date();
		            ps.setDate(6,  new java.sql.Date(utilDate.getTime()));
		            ps.setDate(7, new java.sql.Date(utilDate.getTime()));
		            ps.setDate(8, new java.sql.Date(recipe.getPublishDate().getTime()));
		            return ps;
		        }
		    },
		    keyHolder);
		BigInteger recipeId = (BigInteger) keyHolder.getKey();
		
		for(String t: recipe.getTags()){
			this.jdbcTemplate.update("INSERT INTO recipe_tags(recipe_id, tag) VALUES (?,?)",
					new Object[] { recipeId.intValue(), t});
		}
		
		for(String s: recipe.getSchools()){
			this.jdbcTemplate.update("INSERT INTO school_recipe_map( school_id, recipe_id) VALUES (?,?)",
					new Object[] { s, recipeId.intValue()});
		}
		
		return recipeId;
	}

	@Override
	public Article getArticleByName(String heading) {
		// update views
		this.jdbcTemplate.update("UPDATE articles SET views=views+1 WHERE upper(seo_url) = upper(?)", new Object[] { heading });
		
		// get article
		Article article = (Article) this.jdbcTemplate.query(
				"SELECT * FROM articles where upper(seo_url) = upper(':seoUrl') order by inserted_date desc LIMIT 1".replace(":seoUrl", heading),
				new ResultSetExtractor<Article>() {
					public Article extractData(ResultSet rs) throws SQLException, DataAccessException {
						if (rs.next()) {
							Article article = new Article();
							article.setId(rs.getInt("id"));
							article.setHeading(rs.getString("heading"));
							article.setSeoheading(rs.getString("seo_url"));
							article.setIcon(rs.getString("icon_image"));
							article.setDescription(rs.getString("description"));
							article.setContent(rs.getString("content"));
							article.setViews(rs.getInt("views"));
							article.setLikes(rs.getInt("likes"));
							article.setPublished(rs.getBoolean("published"));
							article.setPublishDateValue(rs.getDate("publish_date"));

							return article;
						}

						return new Article();
					}
				});

		if(article.getId() >0){
			// get tags
			List<String> tags = this.jdbcTemplate.query(
					"SELECT * FROM article_tags where article_id = :article_id".replace(":article_id", String.valueOf(article.getId())),
					new RowMapper<String>() {
						public String mapRow(ResultSet rs, int rowNum) throws SQLException {
							return rs.getString("tag");
						}
					});
			article.setTags(tags);
		}
		return article;
	}

	@Override
	public Recipe getRecipeByName(String heading) {
		// update views
		this.jdbcTemplate.update("UPDATE recipes SET views=views+1 WHERE upper(seo_url) = upper(?)", new Object[] { heading });
		
		// get article
		Recipe recipe = (Recipe) this.jdbcTemplate.query(
				"SELECT * FROM recipes where upper(seo_url) = upper(':heading') order by inserted_date desc LIMIT 1".replace(":heading", heading),
				new ResultSetExtractor<Recipe>() {
					public Recipe extractData(ResultSet rs) throws SQLException, DataAccessException {
						if (rs.next()) {
							Recipe recipe = new Recipe();
							recipe.setId(rs.getInt("id"));
							recipe.setHeading(rs.getString("heading"));
							recipe.setSeoheading(rs.getString("seo_url"));
							recipe.setIcon(rs.getString("icon_image"));
							recipe.setDescription(rs.getString("description"));
							recipe.setContent(rs.getString("content"));
							recipe.setViews(rs.getInt("views"));
							recipe.setLikes(rs.getInt("likes"));
							recipe.setPublished(rs.getBoolean("published"));
							recipe.setPublishDateValue(rs.getDate("publish_date"));

							return recipe;
						}

						return new Recipe();
					}
				});
		if(recipe.getId() >0){
			// get tags
			List<String> tags = this.jdbcTemplate.query(
					"SELECT * FROM recipe_tags where recipe_id = :recipe_id".replace(":recipe_id", String.valueOf(recipe.getId())),
					new RowMapper<String>() {
						public String mapRow(ResultSet rs, int rowNum) throws SQLException {
							return rs.getString("tag");
						}
					});
			recipe.setTags(tags);
		}

		return recipe;
	}

	@Override
	public Map<String,List<Article>> getArticleList(String role, String schoolUrl, String tag) {
		String sql = "";
		if(tag.equalsIgnoreCase("noFilter")){
			if("ADMIN".equalsIgnoreCase(role)){
				sql = "SELECT * FROM articles order by inserted_date desc";
			}
			else if("PARENT".equalsIgnoreCase(role)){
				sql = "SELECT DISTINCT a.* FROM articles a, school_article_map map, school s where a.id = map.article_id and ((map.school_id = s.school_id and s.school_url = '"+schoolUrl+"') or (map.school_id='ALL')) and a.published = 1 order by a.inserted_date desc";
			}
			else {
				sql = "SELECT DISTINCT a.* FROM articles a, school_article_map map, school s where ((a.id = map.article_id and map.school_id='PUBLIC') or (a.publish_date + INTERVAL 30 DAY >=  NOW())) and a.published = 1 order by a.publish_Date desc";
			}
		}
		else{
			if("ADMIN".equalsIgnoreCase(role)){
				sql = "SELECT a.* FROM articles a, article_tags t where a.id = t.article_id and upper(t.tag) = upper('"+tag+"') order by inserted_date desc";
			}
			else if("PARENT".equalsIgnoreCase(role)){
				sql = "SELECT DISTINCT a.* FROM articles a, school_article_map map, school s, article_tags t where a.id = map.article_id and ((map.school_id = s.school_id and s.school_url = '"+schoolUrl+"') or (map.school_id='ALL')) and a.published = 1 and a.id = t.article_id and upper(t.tag) = upper('"+tag+"') order by a.inserted_date desc";
			}
			else {
				sql = "SELECT DISTINCT a.* FROM articles a, school_article_map map, school s, article_tags t where ((a.id = map.article_id and map.school_id='PUBLIC') or (a.publish_date + INTERVAL 30 DAY >=  NOW())) and a.published = 1 and a.id = t.article_id and upper(t.tag) = upper('"+tag+"') order by a.publish_Date desc";
			}
		}
		List<Article> articleList = this.jdbcTemplate.query(
				sql,
				new RowMapper<Article>() {
					public Article mapRow(ResultSet rs, int rowNum) throws SQLException {
						Article article = new Article();
						article.setId(rs.getInt("id"));
						article.setHeading(rs.getString("heading"));
						article.setSeoheading(rs.getString("seo_url"));
						article.setIcon(rs.getString("icon_image"));
						article.setDescription(rs.getString("description"));
						article.setViews(rs.getInt("views"));
						article.setLikes(rs.getInt("likes"));
						article.setPublished(rs.getBoolean("published"));
						article.setPublishDateValue(rs.getDate("publish_date"));
						article.setInsertedDate(rs.getDate("inserted_date"));
						article.setUpdatedDate(rs.getDate("updated_date"));
						return article;
					}
				});
		
		Map<String,List<Article>> map = new HashMap<String,List<Article>>();
		
		 for(Article a : articleList){
			 Date d = a.getUpdatedDate();
			 Calendar c = Calendar.getInstance();
			 c.setTime(d);
			 int m = c.get(Calendar.MONTH);
			 String month = monthNames[m];
			 if(!map.containsKey(month)){
				 List<Article> o = new ArrayList<Article>();
				 o.add(a);
				 map.put(month, o);
			 }
			 else{
				 List<Article> o = map.get(month);
				 o.add(a);
				 map.put(month, o);
			 }
		 }
		 
		 return map;
	}

	@Override
	public Map<String,List<Recipe>> getRecipeList(String role, String schoolUrl, String tag) {
		String sql = "";
		if(tag.equalsIgnoreCase("noFilter")){
			if("ADMIN".equalsIgnoreCase(role)){
				sql = "SELECT * FROM recipes order by inserted_date desc";
			}
			else if("PARENT".equalsIgnoreCase(role)){
				sql = "SELECT DISTINCT r.* FROM recipes r, school_recipe_map map, school s where r.id = map.recipe_id and ((map.school_id = s.school_id and s.school_url = '"+schoolUrl+"') or (map.school_id='ALL')) and r.published = 1 order by r.inserted_date desc";
			}
			else {
				sql = "SELECT DISTINCT r.* FROM recipes r, school_recipe_map map, school s where ((r.id = map.recipe_id and map.school_id='PUBLIC') or (r.publish_date + INTERVAL 30 DAY >=  NOW())) and r.published = 1 order by r.publish_Date desc";
			}
		}
		else{
			if("ADMIN".equalsIgnoreCase(role)){
				sql = "SELECT r.* FROM recipes r, recipe_tags t where r.id = t.recipe_id and upper(t.tag) = upper('"+tag+"') order by inserted_date desc";
			}
			else if("PARENT".equalsIgnoreCase(role)){
				sql = "SELECT DISTINCT r.* FROM recipes r, school_recipe_map map, school s, recipe_tags t where r.id = map.recipe_id and ((map.school_id = s.school_id and s.school_url = '"+schoolUrl+"') or (map.school_id='ALL')) and r.published = 1 and r.id = t.recipe_id and upper(t.tag) = upper('"+tag+"') order by r.inserted_date desc";
			}
			else {
				sql = "SELECT DISTINCT r.* FROM recipes r, school_recipe_map map, school s, recipe_tags t where ((r.id = map.recipe_id and map.school_id='PUBLIC') or (r.publish_date + INTERVAL 30 DAY >=  NOW())) and r.published = 1 and r.id = t.recipe_id and upper(t.tag) = upper('"+tag+"') order by r.publish_Date desc";
			}
		}
		List<Recipe> recipeList = this.jdbcTemplate.query(
				sql,
				new RowMapper<Recipe>() {
					public Recipe mapRow(ResultSet rs, int rowNum) throws SQLException {
						Recipe recipe = new Recipe();
						recipe.setId(rs.getInt("id"));
						recipe.setHeading(rs.getString("heading"));
						recipe.setSeoheading(rs.getString("seo_url"));
						recipe.setIcon(rs.getString("icon_image"));
						recipe.setDescription(rs.getString("description"));
						recipe.setViews(rs.getInt("views"));
						recipe.setLikes(rs.getInt("likes"));
						recipe.setPublished(rs.getBoolean("published"));
						recipe.setPublishDateValue(rs.getDate("publish_date"));
						recipe.setInsertedDate(rs.getDate("inserted_date"));
						recipe.setUpdatedDate(rs.getDate("updated_date"));
						return recipe;
					}
				});
		Map<String,List<Recipe>> map = new HashMap<String,List<Recipe>>();
		
		 for(Recipe a : recipeList){
			 Date d = a.getUpdatedDate();
			 Calendar c = Calendar.getInstance();
			 c.setTime(d);
			 int m = c.get(Calendar.MONTH);
			 String month = monthNames[m];
			 if(!map.containsKey(month)){
				 List<Recipe> o = new ArrayList<Recipe>();
				 o.add(a);
				 map.put(month, o);
			 }
			 else{
				 List<Recipe> o = map.get(month);
				 o.add(a);
				 map.put(month, o);
			 }
		 }
		 
		 return map;
	}

	@Override
	public void publishArticle(int articleId) {
		// publish
				this.jdbcTemplate.update("UPDATE articles SET published=1, publish_date = now() WHERE id = ?", new Object[] { articleId });
	}

	@Override
	public void publishRecipe(int recipeId) {
		// publish
				this.jdbcTemplate.update("UPDATE recipes SET published=1, publish_date = now() WHERE id = ?", new Object[] { recipeId });
		
	}

	@Override
	public Article getArticleByName(String schoolUrl, String heading) {
		// update views
				this.jdbcTemplate.update("UPDATE articles SET views=views+1 WHERE upper(seo_url) = upper(?)", new Object[] { heading });
				
				// get article
				Article article = (Article) this.jdbcTemplate.query(
						"SELECT a.* FROM articles a,school_article_map map, school s where a.id = map.article_id and ((map.school_id = s.school_id and s.school_url = ':schoolUrl') or (map.school_id='ALL')) and upper(a.seo_url) = upper(':heading') order by a.inserted_date desc LIMIT 1".replace(":schoolUrl", schoolUrl).replace(":heading", heading),
						new ResultSetExtractor<Article>() {
							public Article extractData(ResultSet rs) throws SQLException, DataAccessException {
								if (rs.next()) {
									Article article = new Article();
									article.setId(rs.getInt("id"));
									article.setHeading(rs.getString("heading"));
									article.setIcon(rs.getString("icon_image"));
									article.setDescription(rs.getString("description"));
									article.setContent(rs.getString("content"));
									article.setViews(rs.getInt("views"));
									article.setLikes(rs.getInt("likes"));
									article.setPublished(rs.getBoolean("published"));
									article.setPublishDateValue(rs.getDate("publish_date"));

									return article;
								}

								return new Article();
							}
						});

				if(article.getId() >0){
					// get tags
					List<String> tags = this.jdbcTemplate.query(
							"SELECT * FROM article_tags where article_id = :article_id".replace(":article_id", String.valueOf(article.getId())),
							new RowMapper<String>() {
								public String mapRow(ResultSet rs, int rowNum) throws SQLException {
									return rs.getString("tag");
								}
							});
					article.setTags(tags);
				}
				return article;
	}

	@Override
	public Recipe getRecipeByName(String schoolUrl, String heading) {
		// update views
				this.jdbcTemplate.update("UPDATE recipes SET views=views+1 WHERE upper(seo_url) = upper(?)", new Object[] { heading });
				
				// get article
				Recipe recipe = (Recipe) this.jdbcTemplate.query(
						"SELECT r.* FROM recipes r,school_recipe_map map, school s where r.id = map.recipe_id and ((map.school_id = s.school_id and s.school_url = ':schoolUrl') or (map.school_id='ALL')) and upper(r.seo_url) = upper(':heading') order by r.inserted_date desc LIMIT 1".replace(":schoolUrl", schoolUrl).replace(":heading", heading),
						new ResultSetExtractor<Recipe>() {
							public Recipe extractData(ResultSet rs) throws SQLException, DataAccessException {
								if (rs.next()) {
									Recipe recipe = new Recipe();
									recipe.setId(rs.getInt("id"));
									recipe.setHeading(rs.getString("heading"));
									recipe.setIcon(rs.getString("icon_image"));
									recipe.setDescription(rs.getString("description"));
									recipe.setContent(rs.getString("content"));
									recipe.setViews(rs.getInt("views"));
									recipe.setLikes(rs.getInt("likes"));
									recipe.setPublished(rs.getBoolean("published"));
									recipe.setPublishDateValue(rs.getDate("publish_date"));

									return recipe;
								}

								return new Recipe();
							}
						});
				if(recipe.getId() >0){
					// get tags
					List<String> tags = this.jdbcTemplate.query(
							"SELECT * FROM recipe_tags where recipe_id = :recipe_id".replace(":recipe_id", String.valueOf(recipe.getId())),
							new RowMapper<String>() {
								public String mapRow(ResultSet rs, int rowNum) throws SQLException {
									return rs.getString("tag");
								}
							});
					recipe.setTags(tags);
				}

				return recipe;
	}

	@Override
	public List<Article> getRecentArticles(String role, String schoolUrl) {
		String sql = "";
		if("ADMIN".equalsIgnoreCase(role)){
			sql = "SELECT * FROM articles order by inserted_date desc LIMIT 5";
		}
		else if("PARENT".equalsIgnoreCase(role)){
			sql = "SELECT DISTINCT a.* FROM articles a, school_article_map map, school s where a.id = map.article_id and ((map.school_id = s.school_id and s.school_url = '"+schoolUrl+"') or (map.school_id='ALL')) and a.published = 1 order by a.publish_Date desc LIMIT 6";	
		}
		else {
			sql = "SELECT DISTINCT a.* FROM articles a, school_article_map map, school s where ((a.id = map.article_id and map.school_id='PUBLIC') or (a.publish_date + INTERVAL 30 DAY >=  NOW())) and a.published = 1 order by a.publish_Date desc LIMIT 6";
		}
		List<Article> articleList = this.jdbcTemplate.query(
				sql,
				new RowMapper<Article>() {
					public Article mapRow(ResultSet rs, int rowNum) throws SQLException {
						Article article = new Article();
						article.setId(rs.getInt("id"));
						article.setHeading(rs.getString("heading"));
						article.setSeoheading(rs.getString("seo_url"));
						article.setIcon(rs.getString("icon_image"));
						article.setDescription(rs.getString("description"));
						article.setViews(rs.getInt("views"));
						article.setLikes(rs.getInt("likes"));
						article.setPublished(rs.getBoolean("published"));
						article.setPublishDateValue(rs.getDate("publish_date"));
						article.setInsertedDate(rs.getDate("inserted_date"));
						article.setUpdatedDate(rs.getDate("updated_date"));
						return article;
					}
				});
		
		return articleList;
	}

	@Override
	public List<Recipe> getRecentRecipes(String role, String schoolUrl) {
		String sql = "";
		if("ADMIN".equalsIgnoreCase(role)){
			sql = "SELECT * FROM recipes order by inserted_date desc LIMIT 6";
		}
		else if("PARENT".equalsIgnoreCase(role)){
			sql = "SELECT DISTINCT r.* FROM recipes r, school_recipe_map map, school s where r.id = map.recipe_id and ((map.school_id = s.school_id and s.school_url = '"+schoolUrl+"') or (map.school_id='ALL')) and r.published = 1 order by r.publish_Date desc LIMIT 6";
		}
		else {
			sql = "SELECT DISTINCT r.* FROM recipes r, school_recipe_map map, school s where ((r.id = map.recipe_id and map.school_id='PUBLIC') or (r.publish_date + INTERVAL 30 DAY >=  NOW())) and r.published = 1 order by r.publish_Date desc LIMIT 6";
		}
		List<Recipe> recipeList = this.jdbcTemplate.query(
				sql,
				new RowMapper<Recipe>() {
					public Recipe mapRow(ResultSet rs, int rowNum) throws SQLException {
						Recipe recipe = new Recipe();
						recipe.setId(rs.getInt("id"));
						recipe.setHeading(rs.getString("heading"));
						recipe.setSeoheading(rs.getString("seo_url"));
						recipe.setIcon(rs.getString("icon_image"));
						recipe.setDescription(rs.getString("description"));
						recipe.setViews(rs.getInt("views"));
						recipe.setLikes(rs.getInt("likes"));
						recipe.setPublished(rs.getBoolean("published"));
						recipe.setPublishDateValue(rs.getDate("publish_date"));
						recipe.setInsertedDate(rs.getDate("inserted_date"));
						recipe.setUpdatedDate(rs.getDate("updated_date"));
						return recipe;
					}
				});
		return recipeList;
	}

	@Override
	public List<String> getCommonTags() {
		List<String> tags = this.jdbcTemplate.query(
				"SELECT tag, count(*) FROM article_tags group by tag order by count(*) desc LIMIT 10",
				new RowMapper<String>() {
					public String mapRow(ResultSet rs, int rowNum) throws SQLException {
						return rs.getString("tag");
					}
				});
		
		return tags;
	}
	
	@Override
	public int totalArticlesCount() {
		String sql = "select count(*) from articles";
		return this.jdbcTemplate.queryForObject(sql,Integer.class);
	}

	@Override
	public int totalRecipesCount() {
		String sql = "select count(*) from recipes";
		return this.jdbcTemplate.queryForObject(sql,Integer.class);
	}

	@Override
	public void addLikestoArticle(int id, int userId){
		// update likes
		this.jdbcTemplate.update("UPDATE articles SET likes=likes+1 WHERE id = ?", new Object[] { id });
		
		this.jdbcTemplate.update("INSERT INTO article_likes(article_id, user_id) VALUES (?,?)",
				new Object[] { id, userId});
	}
	
	@Override
	public void addLikestoRecipe(int id, int userId){
		// update likes
				this.jdbcTemplate.update("UPDATE recipes SET likes=likes+1 WHERE id = ?", new Object[] { id });
				
				this.jdbcTemplate.update("INSERT INTO recipe_likes(recipe_id, user_id) VALUES (?,?)",
						new Object[] { id, userId});
	}
	
	@Override
	public void addCommenttoArticle(ArticleComment acomment){
		// update likes
		this.jdbcTemplate.update("INSERT INTO article_comments(article_id, user_id, comment, level, inserted_date) VALUES (?,?,?,?, NOW())",
				new Object[] {acomment.getArticleId(), acomment.getUserId(), acomment.getComment(), acomment.getLevel()});
	}
	
	@Override
	public void addCommenttoRecipe(RecipeComment rcomment){
		// update likes
		this.jdbcTemplate.update("INSERT INTO recipe_comments(recipe_id, user_id, comment, level, inserted_date) VALUES (?,?,?,?, NOW())",
						new Object[] { rcomment.getRecipeId(), rcomment.getUserId(), rcomment.getComment(), rcomment.getLevel()});
	}
	
	@Override
	public List<ArticleComment> getArticleComment(int articleId){
		String sql ="SELECT a.*, IFNULL(u.email, u.contact) as name FROM article_comments a, user_master u where a.user_id = u.user_id and a.article_id = "+articleId+" order by inserted_date desc";
		List<ArticleComment> commentList = this.jdbcTemplate.query(
				sql,
				new RowMapper<ArticleComment>() {
					public ArticleComment mapRow(ResultSet rs, int rowNum) throws SQLException {
						ArticleComment comment = new ArticleComment();
						comment.setArticleId(rs.getInt("article_id"));
						comment.setComment(rs.getString("comment"));
						comment.setUserId(rs.getInt("user_id"));
						comment.setLevel(rs.getInt("level"));
						comment.setParentLevel(rs.getInt("parent_level"));
						comment.setUserName(rs.getString("name"));
						comment.setInsertedDate(rs.getDate("inserted_date"));
						
						return comment;
					}
				});
		return commentList;
	}
	
	@Override
	public List<RecipeComment> getRecipeComment(int recipeId){
		String sql ="SELECT a.*, IFNULL(u.email, u.contact) as name FROM recipe_comments a, user_master u where a.user_id = u.user_id and a.recipe_id = "+recipeId+" order by inserted_date desc";
		List<RecipeComment> commentList = this.jdbcTemplate.query(
				sql,
				new RowMapper<RecipeComment>() {
					public RecipeComment mapRow(ResultSet rs, int rowNum) throws SQLException {
						RecipeComment comment = new RecipeComment();
						comment.setRecipeId(rs.getInt("recipe_id"));
						comment.setComment(rs.getString("comment"));
						comment.setUserId(rs.getInt("user_id"));
						comment.setLevel(rs.getInt("level"));
						comment.setParentLevel(rs.getInt("parent_level"));
						comment.setUserName(rs.getString("name"));
						comment.setInsertedDate(rs.getDate("inserted_date"));
						
						return comment;
					}
				});
		return commentList;
	}
	
	@Override
	public boolean isArticleLiked(Integer articleId, Integer userId){
		
		return this.jdbcTemplate.query(
				"SELECT * from article_likes where article_id = :articleId and user_id = :userId".replace(":articleId", articleId.toString()).replace(":userId", userId.toString()),
				new ResultSetExtractor<Boolean>() {
					public Boolean extractData(ResultSet rs) throws SQLException, DataAccessException {
						if (rs.next()) {
							return true;
						}
						else{
							return false;
						}
					}
				});
	}
	
	@Override
	public boolean isRecipeLiked(Integer recipeId, Integer userId){
		
		return this.jdbcTemplate.query(
				"SELECT * from recipe_likes where recipe_id = :recipeId and user_id = :userId".replace(":recipeId", recipeId.toString()).replace(":userId", userId.toString()),
				new ResultSetExtractor<Boolean>() {
					public Boolean extractData(ResultSet rs) throws SQLException, DataAccessException {
						if (rs.next()) {
							return true;
						}
						else{
							return false;
						}
					}
				});
	}
}
