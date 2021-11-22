package com.team8.webdataintegration.winter.identityResolution;

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
		String author1 = record1.getAuthors().get(0).getAuthor_name().replaceAll("\\.|,", " ").toLowerCase().replaceAll(" +", " ");
		String author2 = record2.getAuthors().get(0).getAuthor_name().replaceAll("\\.|,", " ").toLowerCase().replaceAll(" +", " ");
		return lev.calculate(author1, author2);
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
