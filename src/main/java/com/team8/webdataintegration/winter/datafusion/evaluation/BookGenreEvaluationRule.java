package com.team8.webdataintegration.winter.datafusion.evaluation;

import com.team8.webdataintegration.winter.model.Book;

import de.uni_mannheim.informatik.dws.winter.datafusion.EvaluationRule;
import de.uni_mannheim.informatik.dws.winter.model.Correspondence;
import de.uni_mannheim.informatik.dws.winter.model.Matchable;
import de.uni_mannheim.informatik.dws.winter.model.defaultmodel.Attribute;

public class BookGenreEvaluationRule extends EvaluationRule<Book, Attribute> {

	@Override
	public boolean isEqual(Book record1, Book record2, Attribute schemaElement) {
		return false;
	}

	@Override
	public boolean isEqual(Book record1, Book record2, Correspondence<Attribute, Matchable> schemaCorrespondence) {
		return isEqual(record1, record2, (Attribute)null);
	}


}
