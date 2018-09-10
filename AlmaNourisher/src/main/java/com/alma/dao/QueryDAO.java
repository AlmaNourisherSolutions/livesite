package com.alma.dao;

import com.alma.model.Query;

public interface QueryDAO {
	public abstract String postQuery(Query query);
	public abstract Query getQuery(int userId, String role);
	public abstract String postQueryAnswer(Query query);
}
