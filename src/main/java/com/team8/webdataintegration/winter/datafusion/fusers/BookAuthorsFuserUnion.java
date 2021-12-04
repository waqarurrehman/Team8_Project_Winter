package com.team8.webdataintegration.winter.datafusion.fusers;

import java.util.List;

import com.team8.webdataintegration.winter.datafusion.fusers.conflictresolution.AuthorConflictResolution;
import com.team8.webdataintegration.winter.model.Author;
import com.team8.webdataintegration.winter.model.Book;

import de.uni_mannheim.informatik.dws.winter.datafusion.AttributeValueFuser;
import de.uni_mannheim.informatik.dws.winter.datafusion.conflictresolution.list.Union;
import de.uni_mannheim.informatik.dws.winter.model.Correspondence;
import de.uni_mannheim.informatik.dws.winter.model.FusedValue;
import de.uni_mannheim.informatik.dws.winter.model.Matchable;
import de.uni_mannheim.informatik.dws.winter.model.RecordGroup;
import de.uni_mannheim.informatik.dws.winter.model.defaultmodel.Attribute;
import de.uni_mannheim.informatik.dws.winter.processing.Processable;


public class BookAuthorsFuserUnion extends AttributeValueFuser<List<Author>, Book, Attribute> {

	
	public BookAuthorsFuserUnion() {
		super(new AuthorConflictResolution<Author, Book, Attribute>());
	}

	@Override
	public List<Author> getValue(Book record, Correspondence<Attribute, Matchable> correspondence) {
		return record.getAuthors();
	}

	@Override
	public void fuse(RecordGroup<Book, Attribute> group, Book fusedRecord,
			Processable<Correspondence<Attribute, Matchable>> schemaCorrespondences, Attribute schemaElement) {
		// TODO Auto-generated method stub
		FusedValue<List<Author>, Book, Attribute> fused = getFusedValue(group, schemaCorrespondences, schemaElement);
		if(fusedRecord != null) {
			fusedRecord.setAuthors(fused.getValue());
			fusedRecord.setAttributeProvenance(Book.AUTHORS, fused.getOriginalIds());
		}
	}

	@Override
	public boolean hasValue(Book record, Correspondence<Attribute, Matchable> correspondence) {
		return record.hasValue(Book.AUTHORS);
	}

	

}
