package com.team8.webdataintegration.winter.datafusion.evaluation;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import com.team8.webdataintegration.winter.identityResolution.BookCustomAuthorComparator;
import com.team8.webdataintegration.winter.model.Author;
import com.team8.webdataintegration.winter.model.Book;

import de.uni_mannheim.informatik.dws.winter.datafusion.EvaluationRule;
import de.uni_mannheim.informatik.dws.winter.model.Correspondence;
import de.uni_mannheim.informatik.dws.winter.model.Matchable;
import de.uni_mannheim.informatik.dws.winter.model.defaultmodel.Attribute;


public class BookAuthorsEvaluationRule extends EvaluationRule<Book, Attribute> {

	@Override
	public boolean isEqual(Book record1, Book record2, Attribute schemaElement) {
		BookCustomAuthorComparator comp = new BookCustomAuthorComparator();
		ArrayList<String> author1 = new ArrayList<>();

		for (Author a : record1.getAuthors()) {
			author1.add(a.getAuthor_name());
		}

		ArrayList<String> author2 = new ArrayList<>();
		for (Author a : record2.getAuthors()) {
			author2.add(a.getAuthor_name());
		}

		double sim = comp.compare(author1, author2, null);

		if(sim > 0.8) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean isEqual(Book record1, Book record2, Correspondence<Attribute, Matchable> schemaCorrespondence) {
		// TODO Auto-generated method stub
		return isEqual(record1, record2, (Attribute)null);
	}

}
