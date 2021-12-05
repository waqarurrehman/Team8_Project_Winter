package com.team8.webdataintegration.winter.datafusion.evaluation;

import com.team8.webdataintegration.winter.identityResolution.BookDateComparator2Years;
import com.team8.webdataintegration.winter.model.Book;

import de.uni_mannheim.informatik.dws.winter.datafusion.EvaluationRule;
import de.uni_mannheim.informatik.dws.winter.model.Correspondence;
import de.uni_mannheim.informatik.dws.winter.model.Matchable;
import de.uni_mannheim.informatik.dws.winter.model.defaultmodel.Attribute;
import de.uni_mannheim.informatik.dws.winter.similarity.date.YearSimilarity;


public class BookReleaseDateEvaluationRule extends EvaluationRule<Book, Attribute>  {

	private YearSimilarity sim = new YearSimilarity(2);

	@Override
	public boolean isEqual(Book record1, Book record2, Attribute schemaElement) {
		if(record1.getRelease_date()==null && record2.getRelease_date()==null)
			return true;
		else if(record1.getRelease_date()==null ^ record2.getRelease_date()==null)
			return false;
		else
			return sim.calculate(record1.getRelease_date(), record2.getRelease_date()) > 0.5 ? true : false;
	}

	@Override
	public boolean isEqual(Book record1, Book record2, Correspondence<Attribute, Matchable> schemaCorrespondence) {
		return isEqual(record1, record2, (Attribute)null);
	}

}
