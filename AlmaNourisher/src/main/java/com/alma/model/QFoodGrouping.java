package com.alma.model;

import java.util.ArrayList;
import java.util.List;

public class QFoodGrouping {
	private int serveGroupId;
	private String serveGroupName;
	private List<Food> foods = new ArrayList<Food>();
	
	public int getServeGroupId() {
		return serveGroupId;
	}
	public void setServeGroupId(int serveGroupId) {
		this.serveGroupId = serveGroupId;
	}
	public String getServeGroupName() {
		return serveGroupName;
	}
	public void setServeGroupName(String serveGroupName) {
		this.serveGroupName = serveGroupName;
	}
	public List<Food> getFoods() {
		return foods;
	}
	public void setFoods(List<Food> foods) {
		this.foods = foods;
	}
	public List<String> getServings() {
		return servings;
	}
	public void setServings(List<String> servings) {
		this.servings = servings;
	}
	private List<String> servings = new ArrayList<String>();
}
