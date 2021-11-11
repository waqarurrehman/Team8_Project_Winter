package com.team8.webdataintegration.winter.identityResolution;

import com.team8.webdataintegration.winter.model.Book;

import java.util.ArrayList;
import java.util.List;
import java.util.Collections;

import com.team8.webdataintegration.winter.model.Author;

import de.uni_mannheim.informatik.dws.winter.matching.rules.comparators.Comparator;
import de.uni_mannheim.informatik.dws.winter.matching.rules.comparators.ComparatorLogger;
import de.uni_mannheim.informatik.dws.winter.model.Correspondence;
import de.uni_mannheim.informatik.dws.winter.model.Matchable;
import de.uni_mannheim.informatik.dws.winter.model.defaultmodel.Attribute;
import de.uni_mannheim.informatik.dws.winter.similarity.EqualsSimilarity;

public class BookAuthorComparatorEqual implements Comparator<Book, Attribute> {

	private static final long serialVersionUID = 1L;
	private EqualsSimilarity<String> sim = new EqualsSimilarity<String>();
	
	private ComparatorLogger comparisonLog;

	@Override
	public double compare(Book record1, Book record2, Correspondence<Attribute, Matchable> schemaCorrespondence) {
		
		
		List<Author> listauth1 = record1.getAuthors();
		List<Author> listauth2 = record2.getAuthors();
		List<Double> authorsimilarity = new ArrayList<Double>();
		String book1Title = record1.getTitle();
		String book2Title = record2.getTitle();
		//run the comparison only when both both book have authors 
		if(listauth1 != null && !listauth1.isEmpty() 
				&& 
			listauth2 != null && !listauth2.isEmpty()) {
    	    
			for(Author a :listauth1) {
				for(Author b: listauth2) {
					//run the similarity when both authors have name
					if(a.getAuthor_name() != null && !a.getAuthor_name().isEmpty() 
						&& 
						b.getAuthor_name() != null && !b.getAuthor_name().isEmpty()) {
						double similarity = sim.calculate(a.getAuthor_name(), b.getAuthor_name());
						authorsimilarity.add(similarity);
						if(this.comparisonLog != null){
							this.comparisonLog.setComparatorName(getClass().getName());
							this.comparisonLog.setRecord1Value(book1Title+"_"+a.getAuthor_name());
							this.comparisonLog.setRecord2Value(book2Title+"_"+b.getAuthor_name());
							this.comparisonLog.setSimilarity(Double.toString(similarity));
						}
					}else {
						authorsimilarity.add(0d);
					}
				}
			}
			// return max similarity that was found
			return Collections.max(authorsimilarity);
		}else {
		  return 0;	
		}
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
