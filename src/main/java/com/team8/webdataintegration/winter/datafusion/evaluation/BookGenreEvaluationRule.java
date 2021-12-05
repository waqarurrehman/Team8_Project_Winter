package com.team8.webdataintegration.winter.datafusion.evaluation;

import com.team8.webdataintegration.winter.model.Book;

import de.uni_mannheim.informatik.dws.winter.datafusion.EvaluationRule;
import de.uni_mannheim.informatik.dws.winter.model.Correspondence;
import de.uni_mannheim.informatik.dws.winter.model.Matchable;
import de.uni_mannheim.informatik.dws.winter.model.defaultmodel.Attribute;
import de.uni_mannheim.informatik.dws.winter.similarity.string.LevenshteinSimilarity;

public class BookGenreEvaluationRule extends EvaluationRule<Book, Attribute> {

	private static LevenshteinSimilarity sim = new LevenshteinSimilarity();

	@Override
	public boolean isEqual(Book record1, Book record2, Attribute schemaElement) {
		String genreA = record1.getGenre();
		String genreB = record2.getGenre();
		genreA = genreA.replaceAll("[^a-zA-Z ]", "");
		genreB = genreB.replaceAll("[^a-zA-Z ]", "");
		double res = 0;
		if(genreA.length() < genreB.length()) {
			res = mongeElkan(genreA, genreB);
		} else {
			res = mongeElkan(genreB, genreA);
		}
		return res < 0.7 ? false : true;
	}

	@Override
	public boolean isEqual(Book record1, Book record2, Correspondence<Attribute, Matchable> schemaCorrespondence) {
		return isEqual(record1, record2, (Attribute)null);
	}

	public double mongeElkan(String a, String b) {
		double sum = 0;

		for (String s : a.split(" ")) {
			double max = 0;
			for (String q : b.split(" ")) {
				max = Math.max(max, sim.calculate(s, q));
			}
			sum += max;
		}
		return sum / a.split(" ").length;
	}

}
