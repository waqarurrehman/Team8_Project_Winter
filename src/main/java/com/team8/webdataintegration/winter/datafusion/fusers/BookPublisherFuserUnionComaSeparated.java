package com.team8.webdataintegration.winter.datafusion.fusers;

import com.team8.webdataintegration.winter.datafusion.fusers.conflictResolution.PublisherConflictResolution;
import com.team8.webdataintegration.winter.datafusion.fusers.conflictResolution.UnionComaSepratedString;
import com.team8.webdataintegration.winter.model.Book;

import de.uni_mannheim.informatik.dws.winter.datafusion.AttributeValueFuser;
import de.uni_mannheim.informatik.dws.winter.datafusion.conflictresolution.string.LongestString;
import de.uni_mannheim.informatik.dws.winter.model.Correspondence;
import de.uni_mannheim.informatik.dws.winter.model.FusedValue;
import de.uni_mannheim.informatik.dws.winter.model.Matchable;
import de.uni_mannheim.informatik.dws.winter.model.RecordGroup;
import de.uni_mannheim.informatik.dws.winter.model.defaultmodel.Attribute;
import de.uni_mannheim.informatik.dws.winter.processing.Processable;



public class BookPublisherFuserUnionComaSeparated extends AttributeValueFuser<String, Book, Attribute>  {

	public BookPublisherFuserUnionComaSeparated() {
		super(new PublisherConflictResolution<Book, Attribute>());
	}

	@Override
	public String getValue(Book record, Correspondence<Attribute, Matchable> correspondence) {
		return  record.getPublisher();
	}

	@Override
	public void fuse(RecordGroup<Book, Attribute> group, Book fusedRecord,
			Processable<Correspondence<Attribute, Matchable>> schemaCorrespondences, Attribute schemaElement) {
		
		FusedValue<String, Book, Attribute> fused = getFusedValue(group, schemaCorrespondences, schemaElement);
		if (fusedRecord != null) {
			fusedRecord.setPublisher(fused.getValue());
			fusedRecord.setAttributeProvenance(Book.PUBLISHER,
					fused.getOriginalIds());
		}
		
	}

	@Override
	public boolean hasValue(Book record, Correspondence<Attribute, Matchable> correspondence) {
		return record.hasValue(Book.PUBLISHER);
	}

}
