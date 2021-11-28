package com.team8.webdataintegration.winter.datafusion.evaluation;

import com.team8.webdataintegration.winter.model.Book;

import de.uni_mannheim.informatik.dws.winter.datafusion.EvaluationRule;
import de.uni_mannheim.informatik.dws.winter.model.Correspondence;
import de.uni_mannheim.informatik.dws.winter.model.Matchable;
import de.uni_mannheim.informatik.dws.winter.model.defaultmodel.Attribute;

public class BookRatingEvaluationRule extends EvaluationRule<Book, Attribute> {

	@Override
	public boolean isEqual(Book record1, Book record2, Attribute schemaElement) {
		// TODO Auto-generated method stub
		if(record1.getRating()== null && record2.getRating()==null)
			return true;
		else if(record1.getRating()== null ^ record2.getRating()==null)
			return false;
		else 
			return record1.getRating().equals(record2.getRating());
	}

	@Override
	public boolean isEqual(Book record1, Book record2, Correspondence<Attribute, Matchable> schemaCorrespondence) {
		// TODO Auto-generated method stub
		return isEqual(record1, record2, (Attribute)null);
	}

}
