package com.team8.webdataintegration.winter.identityResolution;

import com.team8.webdataintegration.winter.model.Author;
import com.team8.webdataintegration.winter.model.Book;
import de.uni_mannheim.informatik.dws.winter.matching.rules.comparators.Comparator;
import de.uni_mannheim.informatik.dws.winter.matching.rules.comparators.ComparatorLogger;
import de.uni_mannheim.informatik.dws.winter.model.Correspondence;
import de.uni_mannheim.informatik.dws.winter.model.Matchable;
import de.uni_mannheim.informatik.dws.winter.model.defaultmodel.Attribute;
import de.uni_mannheim.informatik.dws.winter.similarity.EqualsSimilarity;
import de.uni_mannheim.informatik.dws.winter.similarity.string.LevenshteinSimilarity;

public class BookCustomAuthorComparator implements Comparator<Book, Attribute> {

	private static final long serialVersionUID = 1L;
	private EqualsSimilarity<String> sim = new EqualsSimilarity<String>();
	private ComparatorLogger comparisonLog;
	private LevenshteinSimilarity lev = new LevenshteinSimilarity();

	@Override
	public double compare(Book record1, Book record2, Correspondence<Attribute, Matchable> schemaCorrespondence) {
		double sim;
		double max = 0;
		String max1 = normalizeAuthorName(record1.getAuthors().get(0).getAuthor_name());
		String max2 = normalizeAuthorName(record2.getAuthors().get(0).getAuthor_name());
		for(Author author1 : record1.getAuthors()) {
			for(Author author2 : record2.getAuthors()){
				sim = lev.calculate(normalizeAuthorName(author1.getAuthor_name()), normalizeAuthorName(author2.getAuthor_name()));
				if(sim > max) {
					max = sim;
					max1 = normalizeAuthorName(author1.getAuthor_name());
					max2 = normalizeAuthorName(author2.getAuthor_name());
				}
			}
		}

		if(comparisonLog != null) {
			comparisonLog.setComparatorName(getClass().getName());
			comparisonLog.setRecord1Value(record1.getAuthors().get(0).getAuthor_name());
			comparisonLog.setRecord2Value(record2.getAuthors().get(0).getAuthor_name());
			comparisonLog.setRecord1PreprocessedValue(max1);
			comparisonLog.setRecord2PreprocessedValue(max2);
			comparisonLog.setSimilarity(Double.toString(max));
		}

		return max;
	}

	public static String normalizeAuthorName(String name) {
		return name.replaceAll("\\.|,", " ").toLowerCase().replaceAll(" +", " ");
	}
	
	@Override
	public ComparatorLogger getComparisonLog() {
		return this.comparisonLog;
	}

	@Override
	public void setComparisonLog(ComparatorLogger comparatorLog) {
		this.comparisonLog = comparatorLog;
	}

}
