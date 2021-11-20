package com.team8.webdataintegration.winter.datafusion.fusers;

import com.team8.webdataintegration.winter.model.Book;

import de.uni_mannheim.informatik.dws.winter.datafusion.AttributeValueFuser;
import de.uni_mannheim.informatik.dws.winter.datafusion.conflictresolution.string.ShortestString;
import de.uni_mannheim.informatik.dws.winter.model.Correspondence;
import de.uni_mannheim.informatik.dws.winter.model.FusedValue;
import de.uni_mannheim.informatik.dws.winter.model.Matchable;
import de.uni_mannheim.informatik.dws.winter.model.RecordGroup;
import de.uni_mannheim.informatik.dws.winter.model.defaultmodel.Attribute;
import de.uni_mannheim.informatik.dws.winter.processing.Processable;




public class BookTileFuserShortestString extends AttributeValueFuser<String, Book, Attribute> {

	public BookTileFuserShortestString() {
		super(new ShortestString<Book, Attribute>());
	}
	

	@Override
	public String getValue(Book record, Correspondence<Attribute, Matchable> correspondence) {
		// TODO Auto-generated method stub
		return	record.getTitle();
	}

	@Override
	public void fuse(RecordGroup<Book, Attribute> group, Book fusedRecord,
			Processable<Correspondence<Attribute, Matchable>> schemaCorrespondences, Attribute schemaElement) {
		// TODO Auto-generated method stub
		// get the fused value
				FusedValue<String, Book, Attribute> fused = getFusedValue(group, schemaCorrespondences, schemaElement);

				// set the value for the fused record
				fusedRecord.setTitle(fused.getValue());

				// add provenance info
				fusedRecord.setAttributeProvenance(Book.TITLE, fused.getOriginalIds());
		
	}

	@Override
	public boolean hasValue(Book record, Correspondence<Attribute, Matchable> correspondence) {
		// TODO Auto-generated method stub
		return record.hasValue(Book.TITLE);
	}

}
