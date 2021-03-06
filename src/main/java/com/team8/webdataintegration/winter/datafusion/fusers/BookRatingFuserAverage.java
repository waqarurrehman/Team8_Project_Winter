package com.team8.webdataintegration.winter.datafusion.fusers;

import com.team8.webdataintegration.winter.datafusion.fusers.conflictResolution.RatingConflictResolution;
import com.team8.webdataintegration.winter.model.Book;

import de.uni_mannheim.informatik.dws.winter.datafusion.AttributeValueFuser;
import de.uni_mannheim.informatik.dws.winter.datafusion.conflictresolution.numeric.Average;
import de.uni_mannheim.informatik.dws.winter.datafusion.conflictresolution.string.LongestString;
import de.uni_mannheim.informatik.dws.winter.model.Correspondence;
import de.uni_mannheim.informatik.dws.winter.model.FusedValue;
import de.uni_mannheim.informatik.dws.winter.model.Matchable;
import de.uni_mannheim.informatik.dws.winter.model.RecordGroup;
import de.uni_mannheim.informatik.dws.winter.model.defaultmodel.Attribute;
import de.uni_mannheim.informatik.dws.winter.processing.Processable;

public class BookRatingFuserAverage extends AttributeValueFuser<Double, Book, Attribute>  {

	public BookRatingFuserAverage() {
		super(new RatingConflictResolution<Book, Attribute>());
	}

	@Override
	public Double getValue(Book record, Correspondence<Attribute, Matchable> correspondence) {
		// TODO Auto-generated method stub
		return  record.getRating();
	}

	@Override
	public void fuse(RecordGroup<Book, Attribute> group, Book fusedRecord,
			Processable<Correspondence<Attribute, Matchable>> schemaCorrespondences, Attribute schemaElement) {
		// TODO Auto-generated method stub
		
		FusedValue<Double, Book, Attribute> fused = getFusedValue(group, schemaCorrespondences, schemaElement);
		if(fusedRecord != null) {
			fusedRecord.setRating(fused.getValue());
			fusedRecord.setAttributeProvenance(Book.RATING,
					fused.getOriginalIds());
		}
		
	}

	@Override
	public boolean hasValue(Book record, Correspondence<Attribute, Matchable> correspondence) {
		// TODO Auto-generated method stub
		return record.hasValue(Book.RATING);
	}

}
