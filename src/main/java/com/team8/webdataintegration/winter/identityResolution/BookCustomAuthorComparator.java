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

import java.util.ArrayList;
import java.util.List;

public class BookCustomAuthorComparator implements Comparator<Book, Attribute> {

	private ComparatorLogger comparisonLog;
	private LevenshteinSimilarity lev = new LevenshteinSimilarity();

	@Override
	public double compare(Book record1, Book record2, Correspondence<Attribute, Matchable> schemaCorrespondence) {
		List<String> authors1 = new ArrayList<>();
		List<String> authors2 = new ArrayList<>();
		for(Author a : record1.getAuthors()) {
			authors1.add(a.getAuthor_name());
		}
		for(Author a : record2.getAuthors()) {
			authors2.add(a.getAuthor_name());
		}
		return compare(authors1, authors2, schemaCorrespondence);
	}

	public double compare(List<String> record1, List<String> record2, Correspondence<Attribute, Matchable> schemaCorrespondence) {
		double sim;
		double max = 0;
		String max1 = normalizeAuthorName(record1.get(0));
		String max2 = normalizeAuthorName(record2.get(0));
		for(String author1 : record1) {
			for(String author2 : record2){
				String name1 = normalizeAuthorName(author1);
				String name2 = normalizeAuthorName(author2);
				String[] tokens1 = name1.split(" ");
				String[] tokens2 = name2.split(" ");
				String initials1 = getInitials(tokens1);
				String initials2 = getInitials(tokens2);
				if(initials1.equals(initials2) && initials1.length() > 2) {
					name1 = tokens1[0] + " " + tokens1[tokens1.length - 1];
					name2 = tokens2[0] + " " + tokens2[tokens2.length - 1];
				}
				sim = lev.calculate(name1, name2);
				if(sim > max) {
					max = sim;
					max1 = name1;
					max2 = name2;
				}
			}
		}

		if(comparisonLog != null) {
			comparisonLog.setComparatorName(getClass().getName());
			comparisonLog.setRecord1Value(record1.get(0));
			comparisonLog.setRecord2Value(record2.get(0));
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

	public String getInitials(String[] nameTokenized) {
		String res = "";
		for(String s : nameTokenized) {
			res += s.charAt(0);
		}
		return res;
	}

}
