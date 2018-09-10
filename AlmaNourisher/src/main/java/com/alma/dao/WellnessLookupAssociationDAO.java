package com.alma.dao;

import com.alma.model.WellnessLookupAssociation;

public interface WellnessLookupAssociationDAO {
	public WellnessLookupAssociation getWellnessReportCategory(String paramString, int paramInt1,
			int paramInt2);

	public WellnessLookupAssociation getWellnessReportbyCategory(String gender, double age, String category);

	public WellnessLookupAssociation getWellnessRecipe(WellnessLookupAssociation wla);
}