package com.alma.model;

import java.util.HashMap;
import java.util.Map;

public class Food {
	private String foodName;
	private Map<String, String> freqScoreCard = new HashMap<String, String>();
	private String freqans;
	private Map<String, String> servScoreCard = new HashMap<String, String>();
	private String servans;
	private int foodId;
	
	public String getFoodName() {
		return foodName;
	}
	public void setFoodName(String foodName) {
		this.foodName = foodName;
	}
	public String getFreqans() {
		return freqans;
	}
	public void setFreqans(String freqans) {
		this.freqans = freqans;
	}
	public String getServans() {
		return servans;
	}
	public void setServans(String servans) {
		this.servans = servans;
	}
	public int getFoodId() {
		return foodId;
	}
	public void setFoodId(int foodId) {
		this.foodId = foodId;
	}
	public Map<String, String> getFreqScoreCard() {
		return freqScoreCard;
	}
	public void setFreqScoreCard(Map<String, String> freqScoreCard) {
		this.freqScoreCard = freqScoreCard;
	}
	public Map<String, String> getServScoreCard() {
		return servScoreCard;
	}
	public void setServScoreCard(Map<String, String> servScoreCard) {
		this.servScoreCard = servScoreCard;
	}
}
