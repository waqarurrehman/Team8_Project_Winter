package com.team8.webdataintegration.winter.datafusion.fusers;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.team8.webdataintegration.winter.datafusion.fusers.conflictResolution.ReleaseDateConflictResolution;
import com.team8.webdataintegration.winter.model.Book;

import de.uni_mannheim.informatik.dws.winter.datafusion.AttributeValueFuser;
import de.uni_mannheim.informatik.dws.winter.datafusion.conflictresolution.ConflictResolutionFunction;
import de.uni_mannheim.informatik.dws.winter.datafusion.conflictresolution.Voting;
import de.uni_mannheim.informatik.dws.winter.datafusion.conflictresolution.meta.MostRecent;
import de.uni_mannheim.informatik.dws.winter.model.Correspondence;
import de.uni_mannheim.informatik.dws.winter.model.FusedValue;
import de.uni_mannheim.informatik.dws.winter.model.Matchable;
import de.uni_mannheim.informatik.dws.winter.model.RecordGroup;
import de.uni_mannheim.informatik.dws.winter.model.defaultmodel.Attribute;
import de.uni_mannheim.informatik.dws.winter.processing.Processable;



public class BookReleaseDateFuserMostRecent  extends AttributeValueFuser<LocalDateTime, Book, Attribute>  {

	
	public BookReleaseDateFuserMostRecent() {
		super(new ReleaseDateConflictResolution<Book, Attribute>());
	}

	
	@Override
	public LocalDateTime getValue(Book record, Correspondence<Attribute, Matchable> correspondence) {
		// TODO Auto-generated method stub
		return record.getRelease_date();
	}

	@Override
	public void fuse(RecordGroup<Book, Attribute> group, Book fusedRecord,
			Processable<Correspondence<Attribute, Matchable>> schemaCorrespondences, Attribute schemaElement) {
		// TODO Auto-generated method stub
		FusedValue<LocalDateTime, Book, Attribute> fused = getFusedValue(group, schemaCorrespondences, schemaElement);
		if(fusedRecord != null) {
			fusedRecord.setRelease_date(fused.getValue());
			fusedRecord.setAttributeProvenance(Book.RELEASE_DATE, fused.getOriginalIds());
		}
	}

	@Override
	public boolean hasValue(Book record, Correspondence<Attribute, Matchable> correspondence) {
		// TODO Auto-generated method stub
		return record.hasValue(Book.RELEASE_DATE);
	}

	

}
