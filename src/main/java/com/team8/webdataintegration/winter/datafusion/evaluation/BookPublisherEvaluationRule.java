package com.team8.webdataintegration.winter.datafusion.evaluation;

import com.team8.webdataintegration.winter.model.Book;

import de.uni_mannheim.informatik.dws.winter.datafusion.EvaluationRule;
import de.uni_mannheim.informatik.dws.winter.model.Correspondence;
import de.uni_mannheim.informatik.dws.winter.model.Matchable;
import de.uni_mannheim.informatik.dws.winter.model.defaultmodel.Attribute;
import de.uni_mannheim.informatik.dws.winter.similarity.string.LevenshteinSimilarity;
import de.uni_mannheim.informatik.dws.winter.similarity.string.TokenizingJaccardSimilarity;

public class BookPublisherEvaluationRule extends EvaluationRule<Book, Attribute> {

	private static String[] randomHouseSubsidiaries = {"Vintage", "Delacorte Press"};

	private static LevenshteinSimilarity sim = new LevenshteinSimilarity();

	@Override
	public boolean isEqual(Book record1, Book record2, Attribute schemaElement) {
		if(record1.getPublisher()== null && record2.getPublisher()==null)
			return true;
		else if(record1.getPublisher()== null ^ record2.getPublisher()==null)
			return false;
		else
			if(record1.getPublisher().equals("Random House")) {
				for(String s : randomHouseSubsidiaries) {
					if(record2.getPublisher().equals(s)) {
						return true;
					}
				}
			}
			if(record2.getPublisher().equals("Random House")) {
				for(String s : randomHouseSubsidiaries) {
					if(record1.getPublisher().equals(s)) {
						return true;
					}
				}
			}
			double d = sim.calculate(record1.getPublisher(), record2.getPublisher());
			return d > 0.5 ? true : false;
	}

	@Override
	public boolean isEqual(Book record1, Book record2, Correspondence<Attribute, Matchable> schemaCorrespondence) {
		return isEqual(record1, record2, (Attribute)null);
	}

}
