package com.team8.webdataintegration.winter.datafusion.fusers;

import com.team8.webdataintegration.winter.datafusion.fusers.conflictResolution.UnionComaSepratedString;
import com.team8.webdataintegration.winter.model.Book;

import de.uni_mannheim.informatik.dws.winter.datafusion.AttributeValueFuser;
import de.uni_mannheim.informatik.dws.winter.model.Correspondence;
import de.uni_mannheim.informatik.dws.winter.model.FusedValue;
import de.uni_mannheim.informatik.dws.winter.model.Matchable;
import de.uni_mannheim.informatik.dws.winter.model.RecordGroup;
import de.uni_mannheim.informatik.dws.winter.model.defaultmodel.Attribute;
import de.uni_mannheim.informatik.dws.winter.processing.Processable;

public class BookGenreFuserUnionComaSeparated extends AttributeValueFuser<String, Book, Attribute>  {

	public BookGenreFuserUnionComaSeparated() {
		//super(new LongestString<Book, Attribute>());
		super(new UnionComaSepratedString<Book, Attribute>());
	}

	@Override
	public String getValue(Book record, Correspondence<Attribute, Matchable> correspondence) {
		// TODO Auto-generated method stub
		return  record.getGenre();
	}

	@Override
	public void fuse(RecordGroup<Book, Attribute> group, Book fusedRecord,
			Processable<Correspondence<Attribute, Matchable>> schemaCorrespondences, Attribute schemaElement) {
		// TODO Auto-generated method stub
		
		FusedValue<String, Book, Attribute> fused = getFusedValue(group, schemaCorrespondences, schemaElement);
		if (fusedRecord != null) {
			fusedRecord.setGenre(fused.getValue());
			fusedRecord.setAttributeProvenance(Book.GENRE, fused.getOriginalIds());
		}
		
	}

	@Override
	public boolean hasValue(Book record, Correspondence<Attribute, Matchable> correspondence) {
		// TODO Auto-generated method stub
		return record.hasValue(Book.GENRE);
	}

}
